/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.http.cache;

import org.aoju.bus.core.io.FileSystem;
import org.aoju.bus.core.io.*;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.utils.IoUtils;
import org.aoju.bus.http.Builder;
import org.aoju.bus.logger.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 使用文件系统上有限空间的缓存。每个缓存条目都有一个字符串键和固定数量的值
 * 每个键必须匹配regex [a-z0-9_-]{1,64}。值是字节序列，可以作为流或文件访问
 * 每个值必须在{@code 0}和{@code Integer之间。MAX_VALUE}字节的长度
 *
 * @author Kimi Liu
 * @version 5.8.9
 * @since JDK 1.8+
 */
public final class DiskLruCache implements Closeable, Flushable {

    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";
    static final String MAGIC = "libcore.io.DiskLruCache";
    static final String VERSION_1 = Symbol.ONE;
    static final long ANY_SEQUENCE_NUMBER = -1;
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,120}");
    private static final String CLEAN = "CLEAN";
    private static final String DIRTY = "DIRTY";
    private static final String REMOVE = "REMOVE";
    private static final String READ = "READ";

    final FileSystem fileSystem;
    /**
     * 缓存存储其数据的目录
     */
    final File directory;
    final int valueCount;
    final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<>(0, 0.75f, true);
    private final File journalFile;
    private final File journalFileTmp;
    private final File journalFileBackup;
    private final int appVersion;
    private final Executor executor;
    BufferSink journalWriter;
    int redundantOpCount;
    boolean hasJournalErrors;
    boolean initialized;
    /**
     * 如果缓存已关闭，则为true
     */
    boolean closed;
    boolean mostRecentTrimFailed;
    boolean mostRecentRebuildFailed;
    /**
     * 存用于存储其数据的最大字节数
     */
    private long maxSize;
    /**
     * 当前用于在此缓存中存储值的字节数
     */
    private long size = 0;
    private final Runnable cleanupRunnable = new Runnable() {
        public void run() {
            synchronized (DiskLruCache.this) {
                if (!initialized | closed) {
                    return;
                }

                try {
                    trimToSize();
                } catch (IOException ignored) {
                    mostRecentTrimFailed = true;
                }

                try {
                    if (journalRebuildRequired()) {
                        rebuildJournal();
                        redundantOpCount = 0;
                    }
                } catch (IOException e) {
                    mostRecentRebuildFailed = true;
                    journalWriter = IoUtils.buffer(IoUtils.blackhole());
                }
            }
        }
    };
    /**
     * 为了区分旧快照和当前快照，每次提交编辑时都会给每个条目一个序列号。
     * 如果快照的序列号不等于其条目的序列号，则该快照将失效
     */
    private long nextSequenceNumber = 0;

    DiskLruCache(FileSystem fileSystem, File directory, int appVersion, int valueCount, long maxSize,
                 Executor executor) {
        this.fileSystem = fileSystem;
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
        this.executor = executor;
    }

    /**
     * 创建一个驻留在{@code directory}中的缓存。此缓存在第一次访问时惰性初始化，如果它不存在，将创建它.
     *
     * @param fileSystem 读写文件
     * @param directory  一个可写目录
     * @param appVersion 版本信息
     * @param valueCount 每个缓存条目的值数目.
     * @param maxSize    此缓存应用于存储的最大字节数
     * @return the disk cache
     */
    public static DiskLruCache create(FileSystem fileSystem, File directory, int appVersion,
                                      int valueCount, long maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        if (valueCount <= 0) {
            throw new IllegalArgumentException("valueCount <= 0");
        }

        Executor executor = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(), Builder.threadFactory("Httpd DiskLruCache", true));

        return new DiskLruCache(fileSystem, directory, appVersion, valueCount, maxSize, executor);
    }

    public synchronized void initialize() throws IOException {
        assert Thread.holdsLock(this);

        if (initialized) {
            return;
        }

        // 如果存在bkp文件，就使用它
        if (fileSystem.exists(journalFileBackup)) {
            // 如果日志文件也存在，删除备份文件
            if (fileSystem.exists(journalFile)) {
                fileSystem.delete(journalFileBackup);
            } else {
                fileSystem.rename(journalFileBackup, journalFile);
            }
        }

        if (fileSystem.exists(journalFile)) {
            try {
                readJournal();
                processJournal();
                initialized = true;
                return;
            } catch (IOException journalIsCorrupt) {
                Logger.warn("DiskLruCache " + directory + " is corrupt: "
                        + journalIsCorrupt.getMessage() + ", removing", journalIsCorrupt);
            }

            // 缓存已损坏，请尝试删除目录中的内容
            try {
                delete();
            } finally {
                closed = false;
            }
        }

        rebuildJournal();

        initialized = true;
    }

    private void readJournal() throws IOException {
        BufferSource source = IoUtils.buffer(fileSystem.source(journalFile));
        try {
            String magic = source.readUtf8LineStrict();
            String version = source.readUtf8LineStrict();
            String appVersionString = source.readUtf8LineStrict();
            String valueCountString = source.readUtf8LineStrict();
            String blank = source.readUtf8LineStrict();
            if (!MAGIC.equals(magic)
                    || !VERSION_1.equals(version)
                    || !Integer.toString(appVersion).equals(appVersionString)
                    || !Integer.toString(valueCount).equals(valueCountString)
                    || !"".equals(blank)) {
                throw new IOException("unexpected journal header: [" + magic + ", " + version + ", "
                        + valueCountString + ", " + blank + "]");
            }

            int lineCount = 0;
            while (true) {
                try {
                    readJournalLine(source.readUtf8LineStrict());
                    lineCount++;
                } catch (EOFException endOfJournal) {
                    break;
                }
            }
            redundantOpCount = lineCount - lruEntries.size();

            // 如果我们以截断的行结束，则在添加日志之前重新生成它
            if (!source.exhausted()) {
                rebuildJournal();
            } else {
                journalWriter = newJournalWriter();
            }
        } finally {
            IoUtils.close(source);
        }
    }

    private BufferSink newJournalWriter() throws FileNotFoundException {
        Sink fileSink = fileSystem.appendingSink(journalFile);
        Sink faultHidingSink = new FaultHideSink(fileSink) {
            @Override
            protected void onException(IOException e) {
                assert (Thread.holdsLock(DiskLruCache.this));
                hasJournalErrors = true;
            }
        };
        return IoUtils.buffer(faultHidingSink);
    }

    private void readJournalLine(String line) throws IOException {
        int firstSpace = line.indexOf(Symbol.C_SPACE);
        if (firstSpace == -1) {
            throw new IOException("unexpected journal line: " + line);
        }

        int keyBegin = firstSpace + 1;
        int secondSpace = line.indexOf(Symbol.C_SPACE, keyBegin);
        final String key;
        if (secondSpace == -1) {
            key = line.substring(keyBegin);
            if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
                lruEntries.remove(key);
                return;
            }
        } else {
            key = line.substring(keyBegin, secondSpace);
        }

        Entry entry = lruEntries.get(key);
        if (entry == null) {
            entry = new Entry(key);
            lruEntries.put(key, entry);
        }

        if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
            String[] parts = line.substring(secondSpace + 1).split(Symbol.SPACE);
            entry.readable = true;
            entry.currentEditor = null;
            entry.setLengths(parts);
        } else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
            entry.currentEditor = new Editor(entry);
        } else if (secondSpace == -1 && firstSpace == READ.length() && line.startsWith(READ)) {
            // This work was already done by calling lruEntries.get().
        } else {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    /**
     * 计算初始大小并收集垃圾作为打开缓存的一部分。脏条目被认为是不一致的，将被删除
     *
     * @throws IOException 异常
     */
    private void processJournal() throws IOException {
        fileSystem.delete(journalFileTmp);
        for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext(); ) {
            Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < valueCount; t++) {
                    size += entry.lengths[t];
                }
            } else {
                entry.currentEditor = null;
                for (int t = 0; t < valueCount; t++) {
                    fileSystem.delete(entry.cleanFiles[t]);
                    fileSystem.delete(entry.dirtyFiles[t]);
                }
                i.remove();
            }
        }
    }

    /**
     * 创建一个删除冗余信息的新日志。如果存在当前日志，它将替换它
     *
     * @throws IOException 异常
     */
    synchronized void rebuildJournal() throws IOException {
        if (journalWriter != null) {
            journalWriter.close();
        }

        BufferSink writer = IoUtils.buffer(fileSystem.sink(journalFileTmp));
        try {
            writer.writeUtf8(MAGIC).writeByte(Symbol.C_LF);
            writer.writeUtf8(VERSION_1).writeByte(Symbol.C_LF);
            writer.writeDecimalLong(appVersion).writeByte(Symbol.C_LF);
            writer.writeDecimalLong(valueCount).writeByte(Symbol.C_LF);
            writer.writeByte(Symbol.C_LF);

            for (Entry entry : lruEntries.values()) {
                if (entry.currentEditor != null) {
                    writer.writeUtf8(DIRTY).writeByte(Symbol.C_SPACE);
                    writer.writeUtf8(entry.key);
                    writer.writeByte(Symbol.C_LF);
                } else {
                    writer.writeUtf8(CLEAN).writeByte(Symbol.C_SPACE);
                    writer.writeUtf8(entry.key);
                    entry.writeLengths(writer);
                    writer.writeByte(Symbol.C_LF);
                }
            }
        } finally {
            writer.close();
        }

        if (fileSystem.exists(journalFile)) {
            fileSystem.rename(journalFile, journalFileBackup);
        }
        fileSystem.rename(journalFileTmp, journalFile);
        fileSystem.delete(journalFileBackup);

        journalWriter = newJournalWriter();
        hasJournalErrors = false;
        mostRecentRebuildFailed = false;
    }

    /**
     * 返回名为{@code key}的条目的快照，如果条目不存在，则返回null，
     * 否则当前无法读取。如果返回一个值，它将被移动到LRU队列的头部
     *
     * @param key 缓存key
     * @return the 快照信息
     * @throws IOException 异常
     */
    public synchronized Snapshot get(String key) throws IOException {
        initialize();

        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (entry == null || !entry.readable) return null;

        Snapshot snapshot = entry.snapshot();
        if (snapshot == null) return null;

        redundantOpCount++;
        journalWriter.writeUtf8(READ).writeByte(Symbol.C_SPACE).writeUtf8(key).writeByte(Symbol.C_LF);
        if (journalRebuildRequired()) {
            executor.execute(cleanupRunnable);
        }

        return snapshot;
    }

    /**
     * 返回名为{@code key}的条目的编辑器，如果另一个编辑正在进行，则返回null
     *
     * @param key 文件key
     * @return 编辑器
     * @throws IOException 异常
     */
    public Editor edit(String key) throws IOException {
        return edit(key, ANY_SEQUENCE_NUMBER);
    }

    synchronized Editor edit(String key, long expectedSequenceNumber) throws IOException {
        initialize();

        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER && (entry == null
                || entry.sequenceNumber != expectedSequenceNumber)) {
            return null;
        }
        if (entry != null && entry.currentEditor != null) {
            return null;
        }
        if (mostRecentTrimFailed || mostRecentRebuildFailed) {
            executor.execute(cleanupRunnable);
            return null;
        }

        // 在创建文件之前刷新日志，以防止文件泄漏
        journalWriter.writeUtf8(DIRTY).writeByte(Symbol.C_SPACE).writeUtf8(key).writeByte(Symbol.C_LF);
        journalWriter.flush();

        if (hasJournalErrors) {
            return null;
        }

        if (entry == null) {
            entry = new Entry(key);
            lruEntries.put(key, entry);
        }
        Editor editor = new Editor(entry);
        entry.currentEditor = editor;
        return editor;
    }

    public File getDirectory() {
        return directory;
    }

    public synchronized long getMaxSize() {
        return maxSize;
    }

    /**
     * 更改缓存可以存储的最大字节数，并在必要时对作业进行排队，以修剪现有存储
     *
     * @param maxSize 最大值
     */
    public synchronized void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        if (initialized) {
            executor.execute(cleanupRunnable);
        }
    }

    public synchronized long size() throws IOException {
        initialize();
        return size;
    }

    synchronized void completeEdit(Editor editor, boolean success) throws IOException {
        Entry entry = editor.entry;
        if (entry.currentEditor != editor) {
            throw new IllegalStateException();
        }

        // 如果这个编辑是第一次创建条目，那么每个索引必须有一个值
        if (success && !entry.readable) {
            for (int i = 0; i < valueCount; i++) {
                if (!editor.written[i]) {
                    editor.abort();
                    throw new IllegalStateException("Newly created entry didn't create value for index " + i);
                }
                if (!fileSystem.exists(entry.dirtyFiles[i])) {
                    editor.abort();
                    return;
                }
            }
        }

        for (int i = 0; i < valueCount; i++) {
            File dirty = entry.dirtyFiles[i];
            if (success) {
                if (fileSystem.exists(dirty)) {
                    File clean = entry.cleanFiles[i];
                    fileSystem.rename(dirty, clean);
                    long oldLength = entry.lengths[i];
                    long newLength = fileSystem.size(clean);
                    entry.lengths[i] = newLength;
                    size = size - oldLength + newLength;
                }
            } else {
                fileSystem.delete(dirty);
            }
        }

        redundantOpCount++;
        entry.currentEditor = null;
        if (entry.readable | success) {
            entry.readable = true;
            journalWriter.writeUtf8(CLEAN).writeByte(Symbol.C_SPACE);
            journalWriter.writeUtf8(entry.key);
            entry.writeLengths(journalWriter);
            journalWriter.writeByte(Symbol.C_LF);
            if (success) {
                entry.sequenceNumber = nextSequenceNumber++;
            }
        } else {
            lruEntries.remove(entry.key);
            journalWriter.writeUtf8(REMOVE).writeByte(Symbol.C_SPACE);
            journalWriter.writeUtf8(entry.key);
            journalWriter.writeByte(Symbol.C_LF);
        }
        journalWriter.flush();

        if (size > maxSize || journalRebuildRequired()) {
            executor.execute(cleanupRunnable);
        }
    }

    /**
     * 只有当日志的大小减半并至少减少2000个ops时，我们才会重建日志
     *
     * @return the true/false
     */
    boolean journalRebuildRequired() {
        final int redundantOpCompactThreshold = 2000;
        return redundantOpCount >= redundantOpCompactThreshold
                && redundantOpCount >= lruEntries.size();
    }

    /**
     * 如果{@code key}存在并且可以删除，则删除它。如果当前正在编辑
     * {@code key}的条目，那么编辑将正常完成，但是它的值不会被存储
     *
     * @param key 缓存key
     * @return 如果一个条目被删除，则为真
     * @throws IOException 异常
     */
    public synchronized boolean remove(String key) throws IOException {
        initialize();

        checkNotClosed();
        validateKey(key);
        Entry entry = lruEntries.get(key);
        if (entry == null) return false;
        boolean removed = removeEntry(entry);
        if (removed && size <= maxSize) mostRecentTrimFailed = false;
        return removed;
    }

    boolean removeEntry(Entry entry) throws IOException {
        if (entry.currentEditor != null) {
            entry.currentEditor.detach();
        }

        for (int i = 0; i < valueCount; i++) {
            fileSystem.delete(entry.cleanFiles[i]);
            size -= entry.lengths[i];
            entry.lengths[i] = 0;
        }

        redundantOpCount++;
        journalWriter.writeUtf8(REMOVE).writeByte(Symbol.C_SPACE).writeUtf8(entry.key).writeByte(Symbol.C_LF);
        lruEntries.remove(entry.key);

        if (journalRebuildRequired()) {
            executor.execute(cleanupRunnable);
        }

        return true;
    }

    public synchronized boolean isClosed() {
        return closed;
    }

    private synchronized void checkNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("cache is closed");
        }
    }

    @Override
    public synchronized void flush() throws IOException {
        if (!initialized) return;

        checkNotClosed();
        trimToSize();
        journalWriter.flush();
    }

    @Override
    public synchronized void close() throws IOException {
        if (!initialized || closed) {
            closed = true;
            return;
        }
        for (Entry entry : lruEntries.values().toArray(new Entry[lruEntries.size()])) {
            if (entry.currentEditor != null) {
                entry.currentEditor.abort();
            }
        }
        trimToSize();
        journalWriter.close();
        journalWriter = null;
        closed = true;
    }

    void trimToSize() throws IOException {
        while (size > maxSize) {
            Entry toEvict = lruEntries.values().iterator().next();
            removeEntry(toEvict);
        }
        mostRecentTrimFailed = false;
    }

    /**
     * 关闭缓存并删除其所有存储值。这将删除缓存目录中的所有文件，包括没有由缓存创建的文件
     *
     * @throws IOException 异常
     */
    public void delete() throws IOException {
        close();
        fileSystem.deleteContents(directory);
    }

    /**
     * 从缓存中删除所有存储值,飞行中的编辑将正常完成，但不会存储它们的值
     *
     * @throws IOException 异常
     */
    public synchronized void evictAll() throws IOException {
        initialize();
        // 为了安全迭代而复制
        for (Entry entry : lruEntries.values().toArray(new Entry[lruEntries.size()])) {
            removeEntry(entry);
        }
        mostRecentTrimFailed = false;
    }

    private void validateKey(String key) {
        Matcher matcher = LEGAL_KEY_PATTERN.matcher(key);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "keys must match regex [a-z0-9_-]{1,120}: \"" + key + Symbol.DOUBLE_QUOTES);
        }
    }

    /**
     * 返回缓存当前项的迭代器。这个迭代器不会抛出{@code ConcurrentModificationException}，
     * 调用者必须{@link Snapshot#close}每个由{@link Iterator#next}返回的快照
     * 如果做不到这一点，就会泄漏打开的文件,返回的迭代器支持 {@link Iterator#remove}.
     *
     * @return 返回迭代器
     * @throws IOException 异常
     */
    public synchronized Iterator<Snapshot> snapshots() throws IOException {
        initialize();
        return new Iterator<Snapshot>() {
            /**
             * 迭代条目的副本以防止并发修改错误
             */
            final Iterator<Entry> delegate = new ArrayList<>(lruEntries.values()).iterator();

            /**
             * 要从{@link #next}返回的快照。如果还没有计算出来，就是Null
             */
            Snapshot nextSnapshot;

            /**
             * 要使用{@link #remove}删除的快照。如果删除是非法的，则为Null
             */
            Snapshot removeSnapshot;

            @Override
            public boolean hasNext() {
                if (nextSnapshot != null) return true;

                synchronized (DiskLruCache.this) {
                    // 如果缓存关闭，则截断迭代器。
                    if (closed) return false;

                    while (delegate.hasNext()) {
                        Entry entry = delegate.next();
                        if (!entry.readable) continue;
                        Snapshot snapshot = entry.snapshot();
                        if (snapshot == null) continue;
                        nextSnapshot = snapshot;
                        return true;
                    }
                }

                return false;
            }

            @Override
            public Snapshot next() {
                if (!hasNext()) throw new NoSuchElementException();
                removeSnapshot = nextSnapshot;
                nextSnapshot = null;
                return removeSnapshot;
            }

            @Override
            public void remove() {
                if (removeSnapshot == null) throw new IllegalStateException("remove() before next()");
                try {
                    DiskLruCache.this.remove(removeSnapshot.key);
                } catch (IOException ignored) {
                    // 这里没什么用。未能从缓存中删除。这很可能是因为无法更新日志，但是缓存的条目仍然没有了
                    Logger.error(ignored);
                } finally {
                    removeSnapshot = null;
                }
            }
        };
    }

    /**
     * 快照信息
     */
    public final class Snapshot implements Closeable {
        private final String key;
        private final long sequenceNumber;
        private final Source[] sources;
        private final long[] lengths;

        Snapshot(String key, long sequenceNumber, Source[] sources, long[] lengths) {
            this.key = key;
            this.sequenceNumber = sequenceNumber;
            this.sources = sources;
            this.lengths = lengths;
        }

        public String key() {
            return key;
        }

        public Editor edit() throws IOException {
            return DiskLruCache.this.edit(key, sequenceNumber);
        }

        public Source getSource(int index) {
            return sources[index];
        }

        public long getLength(int index) {
            return lengths[index];
        }

        public void close() {
            for (Source in : sources) {
                IoUtils.close(in);
            }
        }
    }

    public final class Editor {
        final Entry entry;
        final boolean[] written;
        private boolean done;

        Editor(Entry entry) {
            this.entry = entry;
            this.written = (entry.readable) ? null : new boolean[valueCount];
        }

        void detach() {
            if (entry.currentEditor == this) {
                for (int i = 0; i < valueCount; i++) {
                    try {
                        fileSystem.delete(entry.dirtyFiles[i]);
                    } catch (IOException e) {

                    }
                }
                entry.currentEditor = null;
            }
        }

        public Source newSource(int index) {
            synchronized (DiskLruCache.this) {
                if (done) {
                    throw new IllegalStateException();
                }
                if (!entry.readable || entry.currentEditor != this) {
                    return null;
                }
                try {
                    return fileSystem.source(entry.cleanFiles[index]);
                } catch (FileNotFoundException e) {
                    return null;
                }
            }
        }

        public Sink newSink(int index) {
            synchronized (DiskLruCache.this) {
                if (done) {
                    throw new IllegalStateException();
                }
                if (entry.currentEditor != this) {
                    return IoUtils.blackhole();
                }
                if (!entry.readable) {
                    written[index] = true;
                }
                File dirtyFile = entry.dirtyFiles[index];
                Sink sink;
                try {
                    sink = fileSystem.sink(dirtyFile);
                } catch (FileNotFoundException e) {
                    return IoUtils.blackhole();
                }
                return new FaultHideSink(sink) {
                    @Override
                    protected void onException(IOException e) {
                        synchronized (DiskLruCache.this) {
                            detach();
                        }
                    }
                };
            }
        }

        public void commit() throws IOException {
            synchronized (DiskLruCache.this) {
                if (done) {
                    throw new IllegalStateException();
                }
                if (entry.currentEditor == this) {
                    completeEdit(this, true);
                }
                done = true;
            }
        }

        /**
         * 中止这个编辑。这释放了编辑锁，因此可以在同一个键上启动另一个编辑
         *
         * @throws IOException 异常
         */
        public void abort() throws IOException {
            synchronized (DiskLruCache.this) {
                if (done) {
                    throw new IllegalStateException();
                }
                if (entry.currentEditor == this) {
                    completeEdit(this, false);
                }
                done = true;
            }
        }

        public void abortUnlessCommitted() {
            synchronized (DiskLruCache.this) {
                if (!done && entry.currentEditor == this) {
                    try {
                        completeEdit(this, false);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    private final class Entry {
        final String key;

        /**
         * Lengths of this entry's files.
         */
        final long[] lengths;
        final File[] cleanFiles;
        final File[] dirtyFiles;

        boolean readable;

        Editor currentEditor;

        long sequenceNumber;

        Entry(String key) {
            this.key = key;

            lengths = new long[valueCount];
            cleanFiles = new File[valueCount];
            dirtyFiles = new File[valueCount];

            StringBuilder fileBuilder = new StringBuilder(key).append(Symbol.C_DOT);
            int truncateTo = fileBuilder.length();
            for (int i = 0; i < valueCount; i++) {
                fileBuilder.append(i);
                cleanFiles[i] = new File(directory, fileBuilder.toString());
                fileBuilder.append(".tmp");
                dirtyFiles[i] = new File(directory, fileBuilder.toString());
                fileBuilder.setLength(truncateTo);
            }
        }

        void setLengths(String[] strings) throws IOException {
            if (strings.length != valueCount) {
                throw invalidLengths(strings);
            }

            try {
                for (int i = 0; i < strings.length; i++) {
                    lengths[i] = Long.parseLong(strings[i]);
                }
            } catch (NumberFormatException e) {
                throw invalidLengths(strings);
            }
        }

        void writeLengths(BufferSink writer) throws IOException {
            for (long length : lengths) {
                writer.writeByte(Symbol.C_SPACE).writeDecimalLong(length);
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + Arrays.toString(strings));
        }

        Snapshot snapshot() {
            if (!Thread.holdsLock(DiskLruCache.this)) throw new AssertionError();

            Source[] sources = new Source[valueCount];
            long[] lengths = this.lengths.clone();
            try {
                for (int i = 0; i < valueCount; i++) {
                    sources[i] = fileSystem.source(cleanFiles[i]);
                }
                return new Snapshot(key, sequenceNumber, sources, lengths);
            } catch (FileNotFoundException e) {
                for (int i = 0; i < valueCount; i++) {
                    if (sources[i] != null) {
                        IoUtils.close(sources[i]);
                    } else {
                        break;
                    }
                }
                try {
                    removeEntry(this);
                } catch (IOException ignored) {
                }
                return null;
            }
        }
    }

}
