/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2024 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.extra.ssh.provider.jsch;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import org.miaixz.bus.core.lang.Symbol;
import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.xyz.CollKit;
import org.miaixz.bus.core.xyz.FileKit;
import org.miaixz.bus.core.xyz.ListKit;
import org.miaixz.bus.core.xyz.StringKit;
import org.miaixz.bus.extra.ftp.AbstractFtp;
import org.miaixz.bus.extra.ftp.FtpConfig;
import org.miaixz.bus.extra.ssh.Connector;
import org.miaixz.bus.extra.ssh.JschKit;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Predicate;

/**
 * SFTP是Secure File Transfer Protocol的缩写，安全文件传送协议。可以为传输文件提供一种安全的加密方法。 SFTP 为
 * SSH的一部份，是一种传输文件到服务器的安全方式。SFTP是使用加密传输认证信息和传输的数据，所以，使用SFTP是非常安全的。
 * 但是，由于这种传输方式使用了加密/解密技术，所以传输效率比普通的FTP要低得多，如果您对网络安全性要求更高时，可以使用SFTP代替FTP。
 *
 * <p>
 * 此类为基于jsch的SFTP实现 参考：<a href=
 * "https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html">https://www.cnblogs.com/longyg/archive/2012/06/25/2556576.html</a>
 * </p>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class JschSftp extends AbstractFtp {

    private Session session;
    private ChannelSftp channel;

    /**
     * 构造
     *
     * @param config FTP配置
     */
    public JschSftp(final FtpConfig config) {
        this(config, true);
    }

    /**
     * 构造
     *
     * @param config FTP配置
     * @param init   是否立即初始化
     */
    public JschSftp(final FtpConfig config, final boolean init) {
        super(config);
        if (init) {
            init();
        }
    }

    /**
     * 构造
     *
     * @param session {@link Session}
     * @param charset 编码
     * @param timeOut 超时时间，单位毫秒
     */
    public JschSftp(final Session session, final Charset charset, final long timeOut) {
        super(FtpConfig.of().setCharset(charset).setConnectionTimeout(timeOut));
        this.session = session;
        init();
    }

    /**
     * 构造
     *
     * @param channel {@link ChannelSftp}
     * @param charset 编码
     * @param timeOut 超时时间，单位毫秒
     */
    public JschSftp(final ChannelSftp channel, final Charset charset, final long timeOut) {
        super(FtpConfig.of().setCharset(charset).setConnectionTimeout(timeOut));
        this.channel = channel;
        init();
    }

    /**
     * 构造
     *
     * @param sshHost 远程主机
     * @param sshPort 远程主机端口
     * @param sshUser 远程主机用户名
     * @param sshPass 远程主机密码
     * @return JschSftp
     */
    public static JschSftp of(final String sshHost, final int sshPort, final String sshUser, final String sshPass) {
        return of(sshHost, sshPort, sshUser, sshPass, DEFAULT_CHARSET);
    }

    /**
     * 构造
     *
     * @param sshHost 远程主机
     * @param sshPort 远程主机端口
     * @param sshUser 远程主机用户名
     * @param sshPass 远程主机密码
     * @param charset 编码
     * @return JschSftp
     */
    public static JschSftp of(final String sshHost, final int sshPort, final String sshUser, final String sshPass,
            final Charset charset) {
        return new JschSftp(new FtpConfig(Connector.of(sshHost, sshPort, sshUser, sshPass), charset));
    }

    /**
     * 初始化
     */
    public void init() {
        if (null == this.channel) {
            if (null == this.session) {
                final FtpConfig config = this.ftpConfig;
                final Connector connector = config.getConnector();
                this.session = new JschSession(Connector.of(connector.getHost(), connector.getPort(),
                        connector.getUser(), connector.getPassword(), connector.getTimeout())).getRaw();
            }

            if (false == session.isConnected()) {
                // 首先Session需连接
                try {
                    session.connect((int) this.ftpConfig.getConnector().getTimeout());
                } catch (final JSchException e) {
                    throw new InternalException(e);
                }
            }

            // 创建Channel
            try {
                this.channel = (ChannelSftp) this.session.openChannel(ChannelType.SFTP.getValue());
            } catch (final JSchException e) {
                throw new InternalException(e);
            }
        }

        try {
            if (!channel.isConnected()) {
                channel.connect((int) Math.max(this.ftpConfig.getConnector().getTimeout(), 0));
            }
            channel.setFilenameEncoding(this.ftpConfig.getCharset().toString());
        } catch (final JSchException | SftpException e) {
            throw new InternalException(e);
        }
    }

    @Override
    public JschSftp reconnectIfTimeout() {
        if (StringKit.isBlank(this.ftpConfig.getConnector().getHost())) {
            throw new InternalException("Host is blank!");
        }
        try {
            this.cd(Symbol.SLASH);
        } catch (final InternalException e) {
            close();
            init();
        }
        return this;
    }

    /**
     * 获取SFTP通道客户端
     *
     * @return 通道客户端
     */
    public ChannelSftp getClient() {
        if (false == this.channel.isConnected()) {
            init();
        }
        return this.channel;
    }

    /**
     * 远程当前目录
     *
     * @return 远程当前目录
     */
    @Override
    public String pwd() {
        try {
            return getClient().pwd();
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    /**
     * 获取HOME路径
     *
     * @return HOME路径
     */
    public String home() {
        try {
            return getClient().getHome();
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    /**
     * 遍历某个目录下所有文件或目录，不会递归遍历
     *
     * @param path 遍历某个目录下所有文件或目录
     * @return 目录或文件名列表
     */
    @Override
    public List<String> ls(final String path) {
        return ls(path, null);
    }

    /**
     * 遍历某个目录下所有目录，不会递归遍历
     *
     * @param path 遍历某个目录下所有目录
     * @return 目录名列表
     */
    public List<String> lsDirs(final String path) {
        return ls(path, t -> t.getAttrs().isDir());
    }

    /**
     * 遍历某个目录下所有文件，不会递归遍历
     *
     * @param path 遍历某个目录下所有文件
     * @return 文件名列表
     */
    public List<String> lsFiles(final String path) {
        return ls(path, t -> !t.getAttrs().isDir());
    }

    /**
     * 遍历某个目录下所有文件或目录，不会递归遍历 此方法自动过滤"."和".."两种目录
     *
     * @param path      遍历某个目录下所有文件或目录
     * @param predicate 文件或目录过滤器，可以实现过滤器返回自己需要的文件或目录名列表，{@link Predicate#test(Object)}为{@code true}保留
     * @return 目录或文件名列表
     */
    public List<String> ls(final String path, final Predicate<LsEntry> predicate) {
        final List<LsEntry> entries = lsEntries(path, predicate);
        if (CollKit.isEmpty(entries)) {
            return ListKit.empty();
        }
        return CollKit.map(entries, LsEntry::getFilename);
    }

    /**
     * 遍历某个目录下所有文件或目录，生成LsEntry列表，不会递归遍历 此方法自动过滤"."和".."两种目录
     *
     * @param path 遍历某个目录下所有文件或目录
     * @return 目录或文件名列表
     */
    public List<LsEntry> lsEntries(final String path) {
        return lsEntries(path, null);
    }

    /**
     * 遍历某个目录下所有文件或目录，生成LsEntry列表，不会递归遍历 此方法自动过滤"."和".."两种目录
     *
     * @param path      遍历某个目录下所有文件或目录
     * @param predicate 文件或目录过滤器，可以实现过滤器返回自己需要的文件或目录名列表，{@link Predicate#test(Object)}为{@code true}保留
     * @return 目录或文件名列表
     */
    public List<LsEntry> lsEntries(final String path, final Predicate<LsEntry> predicate) {
        final List<LsEntry> entryList = new ArrayList<>();
        try {
            getClient().ls(path, entry -> {
                final String fileName = entry.getFilename();
                if (!StringKit.equals(".", fileName) && !StringKit.equals("..", fileName)) {
                    if (null == predicate || predicate.test(entry)) {
                        entryList.add(entry);
                    }
                }
                return LsEntrySelector.CONTINUE;
            });
        } catch (final SftpException e) {
            if (!StringKit.startWithIgnoreCase(e.getMessage(), "No such file")) {
                throw new InternalException(e);
            }
            // 文件不存在忽略
        }
        return entryList;
    }

    @Override
    public boolean mkdir(final String dir) {
        if (isDir(dir)) {
            // 目录已经存在，创建直接返回
            return true;
        }
        try {
            getClient().mkdir(dir);
            return true;
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    @Override
    public boolean isDir(final String dir) {
        final SftpATTRS sftpATTRS;
        try {
            sftpATTRS = getClient().stat(dir);
        } catch (final SftpException e) {
            final String msg = e.getMessage();
            if (StringKit.containsAnyIgnoreCase(msg, "No such file", "does not exist")) {
                // 文件不存在直接返回false
                return false;
            }
            throw new InternalException(e);
        }
        return sftpATTRS.isDir();
    }

    /**
     * 打开指定目录，如果指定路径非目录或不存在抛出异常
     *
     * @param directory directory
     * @return 是否打开目录
     * @throws InternalException 进入目录失败异常
     */
    @Override
    synchronized public boolean cd(final String directory) throws InternalException {
        if (StringKit.isBlank(directory)) {
            // 当前目录
            return true;
        }
        try {
            getClient().cd(directory.replace('\\', '/'));
            return true;
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 要删除的文件绝对路径
     */
    @Override
    public boolean delFile(final String filePath) {
        try {
            getClient().rm(filePath);
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
        return true;
    }

    /**
     * 删除文件夹及其文件夹下的所有文件
     *
     * @param dirPath 文件夹路径
     * @return boolean 是否删除成功
     */
    @Override
    public boolean delDir(final String dirPath) {
        if (!cd(dirPath)) {
            return false;
        }

        final ChannelSftp channel = getClient();

        final Vector<LsEntry> list;
        try {
            list = channel.ls(channel.pwd());
        } catch (final SftpException e) {
            throw new InternalException(e);
        }

        String fileName;
        for (final LsEntry entry : list) {
            fileName = entry.getFilename();
            if (!".".equals(fileName) && !"..".equals(fileName)) {
                if (entry.getAttrs().isDir()) {
                    delDir(fileName);
                } else {
                    delFile(fileName);
                }
            }
        }

        if (!cd("..")) {
            return false;
        }

        // 删除空目录
        try {
            channel.rmdir(dirPath);
            return true;
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    /**
     * 将本地文件或者文件夹同步（覆盖）上传到远程路径
     *
     * @param remotePath 远程路径
     * @param file       文件或者文件夹
     */
    public void upload(final String remotePath, final File file) {
        if (!FileKit.exists(file)) {
            return;
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (final File fileItem : files) {
                if (fileItem.isDirectory()) {
                    final String mkdir = FileKit.normalize(remotePath + "/" + fileItem.getName());
                    this.upload(mkdir, fileItem);
                } else {
                    this.uploadFile(remotePath, fileItem);
                }
            }
        } else {
            this.uploadFile(remotePath, file);
        }
    }

    @Override
    public boolean uploadFile(final String destPath, final File file) {
        if (!FileKit.isFile(file)) {
            throw new InternalException("[{}] is not a file!", file);
        }
        this.mkDirs(destPath);
        put(FileKit.getAbsolutePath(file), destPath);
        return true;
    }

    /**
     * 上传文件到指定目录，可选：
     *
     * <pre>
     * 1. path为null或""上传到当前路径
     * 2. path为相对路径则相对于当前路径的子路径
     * 3. path为绝对路径则上传到此路径
     * </pre>
     *
     * @param destPath   服务端路径，可以为{@code null} 或者相对路径或绝对路径
     * @param fileName   文件名
     * @param fileStream 文件流
     */
    public void uploadFile(String destPath, final String fileName, final InputStream fileStream) {
        destPath = StringKit.addSuffixIfNot(destPath, Symbol.SLASH) + StringKit.removePrefix(fileName, Symbol.SLASH);
        put(fileStream, destPath, null, Mode.OVERWRITE);
    }

    /**
     * 将本地文件上传到目标服务器，目标文件名为destPath，若destPath为目录，则目标文件名将与srcFilePath文件名相同。覆盖模式
     *
     * @param srcFilePath 本地文件路径
     * @param destPath    目标路径，
     * @return this
     */
    public JschSftp put(final String srcFilePath, final String destPath) {
        return put(srcFilePath, destPath, Mode.OVERWRITE);
    }

    /**
     * 将本地文件上传到目标服务器，目标文件名为destPath，若destPath为目录，则目标文件名将与srcFilePath文件名相同。
     *
     * @param srcFilePath 本地文件路径
     * @param destPath    目标路径，
     * @param mode        {@link Mode} 模式
     * @return this
     */
    public JschSftp put(final String srcFilePath, final String destPath, final Mode mode) {
        return put(srcFilePath, destPath, null, mode);
    }

    /**
     * 将本地文件上传到目标服务器，目标文件名为destPath，若destPath为目录，则目标文件名将与srcFilePath文件名相同。
     *
     * @param srcFilePath 本地文件路径
     * @param destPath    目标路径，
     * @param monitor     上传进度监控，通过实现此接口完成进度显示
     * @param mode        {@link Mode} 模式
     * @return this
     */
    public JschSftp put(final String srcFilePath, final String destPath, final SftpProgressMonitor monitor,
            final Mode mode) {
        try {
            getClient().put(srcFilePath, destPath, monitor, mode.ordinal());
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
        return this;
    }

    /**
     * 将本地数据流上传到目标服务器，目标文件名为destPath，目标必须为文件
     *
     * @param srcStream 本地的数据流
     * @param destPath  目标路径，
     * @param monitor   上传进度监控，通过实现此接口完成进度显示
     * @param mode      {@link Mode} 模式
     * @return this
     */
    public JschSftp put(final InputStream srcStream, final String destPath, final SftpProgressMonitor monitor,
            final Mode mode) {
        try {
            getClient().put(srcStream, destPath, monitor, mode.ordinal());
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
        return this;
    }

    @Override
    public void download(final String src, final File destFile) {
        get(src, FileKit.getAbsolutePath(destFile));
    }

    /**
     * 下载文件到{@link OutputStream}中
     *
     * @param src 源文件路径，包括文件名
     * @param out 目标流
     * @see #get(String, OutputStream)
     */
    public void download(final String src, final OutputStream out) {
        get(src, out);
    }

    /**
     * 递归下载FTP服务器上文件到本地(文件目录和服务器同步)
     *
     * @param sourcePath ftp服务器目录，必须为目录
     * @param destDir    本地目录
     */
    @Override
    public void recursiveDownloadFolder(final String sourcePath, final File destDir) throws InternalException {
        String fileName;
        String srcFile;
        File destFile;
        for (final LsEntry item : lsEntries(sourcePath)) {
            fileName = item.getFilename();
            srcFile = StringKit.format("{}/{}", sourcePath, fileName);
            destFile = FileKit.file(destDir, fileName);

            if (!item.getAttrs().isDir()) {
                // 本地不存在文件或者ftp上文件有修改则下载
                if (!FileKit.exists(destFile) || (item.getAttrs().getMTime() > (destFile.lastModified() / 1000))) {
                    download(srcFile, destFile);
                }
            } else {
                // 服务端依旧是目录，继续递归
                FileKit.mkdir(destFile);
                recursiveDownloadFolder(srcFile, destFile);
            }
        }

    }

    /**
     * 获取远程文件
     *
     * @param src  远程文件路径
     * @param dest 目标文件路径
     * @return this
     */
    public JschSftp get(final String src, final String dest) {
        try {
            getClient().get(src, dest);
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
        return this;
    }

    /**
     * 获取远程文件
     *
     * @param src 远程文件路径
     * @param out 目标流
     * @return this
     */
    public JschSftp get(final String src, final OutputStream out) {
        try {
            getClient().get(src, out);
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
        return this;
    }

    @Override
    public InputStream getFileStream(final String path) {
        try {
            return getClient().get(path);
        } catch (final SftpException e) {
            throw new InternalException(e);
        }
    }

    @Override
    public void close() {
        JschKit.close(this.channel);
        JschKit.close(this.session);
    }

    @Override
    public String toString() {
        final Connector connector = this.ftpConfig.getConnector();
        return "JschSftp{" + "host='" + connector.getHost() + '\'' + ", port=" + connector.getPort() + ", user='"
                + connector.getUser() + '\'' + '}';
    }

    /**
     * JSch支持的三种文件传输模式
     */
    public enum Mode {
        /**
         * 完全覆盖模式，这是JSch的默认文件传输模式，即如果目标文件已经存在，传输的文件将完全覆盖目标文件，产生新的文件。
         */
        OVERWRITE,
        /**
         * 恢复模式，如果文件已经传输一部分，这时由于网络或其他任何原因导致文件传输中断，如果下一次传输相同的文件，则会从上一次中断的地方续传。
         */
        RESUME,
        /**
         * 追加模式，如果目标文件已存在，传输的文件将在目标文件后追加。
         */
        APPEND
    }
}
