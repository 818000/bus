/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.office.csv;

import org.aoju.bus.core.collection.ComputeIterator;
import org.aoju.bus.core.exception.InternalException;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.text.TextBuilder;
import org.aoju.bus.core.toolkit.IoKit;
import org.aoju.bus.core.toolkit.MapKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.*;

/**
 * CSV行解析器,参考：FastCSV
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public final class CsvParser extends ComputeIterator<CsvRow> implements Closeable, Serializable {

    private static final int DEFAULT_ROW_CAPACITY = 10;

    private final Reader reader;
    private final CsvReadConfig config;

    private final Buffer buf = new Buffer(IoKit.DEFAULT_LARGE_BUFFER_SIZE);
    /**
     * 当前读取字段
     */
    private final TextBuilder currentField = new TextBuilder(Normal._512);
    /**
     * 前一个特殊分界字符
     */
    private int preChar = -1;
    /**
     * 是否在引号包装内
     */
    private boolean inQuotes;
    /**
     * 标题行
     */
    private CsvRow header;
    /**
     * 当前行号
     */
    private long lineNo = -1;
    /**
     * 引号内的行数
     */
    private long inQuotesLineCount;
    /**
     * 第一行字段数,用于检查每行字段数是否一致
     */
    private int firstLineFieldCount = -1;
    /**
     * 最大字段数量
     */
    private int maxFieldCount;
    /**
     * 是否读取结束
     */
    private boolean finished;

    /**
     * CSV解析器
     *
     * @param reader Reader
     * @param config 配置，null则为默认配置
     */
    public CsvParser(final Reader reader, CsvReadConfig config) {
        this.reader = Objects.requireNonNull(reader, "reader must not be null");
        this.config = ObjectKit.defaultIfNull(config, CsvReadConfig::defaultConfig);
    }

    @Override
    protected CsvRow computeNext() {
        return nextRow();
    }

    /**
     * 获取头部字段列表，如果headerLineNo &lt; 0,抛出异常
     *
     * @return 头部列表
     * @throws IllegalStateException 如果不解析头部或者没有调用nextRow()方法
     */
    public List<String> getHeader() {
        if (config.headerLineNo < 0) {
            throw new IllegalStateException("No header available - header parsing is disabled");
        }
        if (lineNo == 0) {
            throw new IllegalStateException("No header available - call nextRow() first");
        }
        return header.fields;
    }

    /**
     * 读取下一行数据
     *
     * @return CsvRow
     * @throws InternalException IO读取异常
     */
    public CsvRow nextRow() throws InternalException {
        long startingLineNo;
        List<String> currentFields;
        int fieldCount;
        while (false == finished) {
            startingLineNo = ++lineNo;
            currentFields = readLine();
            fieldCount = currentFields.size();
            if (fieldCount < 1) {
                break;
            }

            // 跳过空行
            if (config.skipEmptyRows && fieldCount == 1 && currentFields.get(0).isEmpty()) {
                continue;
            }

            // 检查每行的字段数是否一致
            if (config.errorOnDifferentFieldCount) {
                if (firstLineFieldCount == -1) {
                    firstLineFieldCount = fieldCount;
                } else if (fieldCount != firstLineFieldCount) {
                    throw new InternalException(String.format("Line %d has %d fields, but first line has %d fields", lineNo, fieldCount, firstLineFieldCount));
                }
            }

            // 记录最大字段数
            if (fieldCount > maxFieldCount) {
                maxFieldCount = fieldCount;
            }

            // 初始化标题
            if (lineNo == config.headerLineNo && null == header) {
                initHeader(currentFields);
                // 作为标题行后，此行跳过，下一行做为第一行
                continue;
            }

            return new CsvRow(startingLineNo, null == header ? null : header.headerMap, currentFields);
        }

        return null;
    }

    /**
     * 当前行做为标题行
     *
     * @param currentFields 当前行字段列表
     */
    private void initHeader(final List<String> currentFields) {
        final Map<String, Integer> localHeaderMap = new LinkedHashMap<>(currentFields.size());
        for (int i = 0; i < currentFields.size(); i++) {
            String field = currentFields.get(i);
            if (MapKit.isNotEmpty(this.config.headerAlias)) {
                // 自定义别名
                field = ObjectKit.defaultIfNull(this.config.headerAlias.get(field), field);
            }
            if (StringKit.isNotEmpty(field) && false == localHeaderMap.containsKey(field)) {
                localHeaderMap.put(field, i);
            }
        }

        header = new CsvRow(this.lineNo, Collections.unmodifiableMap(localHeaderMap), Collections.unmodifiableList(currentFields));
    }

    /**
     * 读取一行数据，如果读取结束，返回size为0的List
     * 空行是size为1的List，唯一元素是""
     * 行号要考虑注释行和引号包装的内容中的换行
     *
     * @return 一行数据
     * @throws InternalException IO异常
     */
    private List<String> readLine() throws InternalException {
        // 矫正行号
        // 当一行内容包含多行数据时，记录首行行号，但是读取下一行时，需要把多行内容的行数加上
        if (inQuotesLineCount > 0) {
            this.lineNo += this.inQuotesLineCount;
            this.inQuotesLineCount = 0;
        }

        final List<String> currentFields = new ArrayList<>(maxFieldCount > 0 ? maxFieldCount : DEFAULT_ROW_CAPACITY);

        final TextBuilder currentField = this.currentField;
        final Buffer buf = this.buf;
        int preChar = this.preChar;//前一个特殊分界字符
        int copyLen = 0; //拷贝长度
        boolean inComment = false;

        while (true) {
            if (false == buf.hasRemaining()) {
                // 此Buffer读取结束，开始读取下一段
                if (copyLen > 0) {
                    buf.appendTo(currentField, copyLen);
                    // 此处无需mark，read方法会重置mark
                }
                if (buf.read(this.reader) < 0) {
                    // CSV读取结束
                    finished = true;

                    if (!currentField.isEmpty() || preChar == config.fieldSeparator) {
                        //剩余部分作为一个字段
                        addField(currentFields, currentField.toString(true));
                    }
                    break;
                }

                //重置
                copyLen = 0;
            }

            final char c = buf.get();

            // 注释行标记
            if (preChar < 0 || preChar == Symbol.C_CR || preChar == Symbol.C_LF) {
                // 判断行首字符为指定注释字符的注释开始，直到遇到换行符
                // 行首分两种，1是preChar < 0表示文本开始，2是换行符后紧跟就是下一行的开始
                if (null != this.config.commentCharacter && c == this.config.commentCharacter) {
                    inComment = true;
                }
            }
            // 注释行处理
            if (inComment) {
                if (c == Symbol.C_CR || c == Symbol.C_LF) {
                    // 注释行以换行符为结尾
                    lineNo++;
                    inComment = false;
                }
                // 跳过注释行中的任何字符
                buf.mark();
                preChar = c;
                continue;
            }

            if (inQuotes) {
                //引号内，做为内容，直到引号结束
                if (c == config.textDelimiter) {
                    // End of quoted text
                    inQuotes = false;
                } else {
                    // 字段内容中新行
                    if (isLineEnd(c, preChar)) {
                        inQuotesLineCount++;
                    }
                }
                // 普通字段字符
                copyLen++;
            } else {
                // 非引号内
                if (c == config.fieldSeparator) {
                    //一个字段结束
                    if (copyLen > 0) {
                        buf.appendTo(currentField, copyLen);
                        copyLen = 0;
                    }
                    buf.mark();
                    addField(currentFields, currentField.toString(true));
                } else if (c == config.textDelimiter) {
                    // 引号开始
                    inQuotes = true;
                    copyLen++;
                } else if (c == Symbol.C_CR) {
                    // \r，直接结束
                    if (copyLen > 0) {
                        buf.appendTo(currentField, copyLen);
                    }
                    buf.mark();
                    addField(currentFields, currentField.toString(true));
                    preChar = c;
                    break;
                } else if (c == Symbol.C_LF) {
                    // \n
                    if (preChar != Symbol.C_CR) {
                        if (copyLen > 0) {
                            buf.appendTo(currentField, copyLen);
                        }
                        buf.mark();
                        addField(currentFields, currentField.toString(true));
                        preChar = c;
                        break;
                    }
                    // 前一个字符是\r，已经处理过这个字段了，此处直接跳过
                    buf.mark();
                } else {
                    // 普通字符
                    copyLen++;
                }
            }

            preChar = c;
        }
        this.preChar = preChar;
        lineNo++;

        return currentFields;
    }

    /**
     * 是否行结束符
     *
     * @param c       符号
     * @param preChar 前一个字符
     * @return 是否结束
     */
    private boolean isLineEnd(char c, int preChar) {
        return (c == Symbol.C_CR || c == Symbol.C_LF) && preChar != Symbol.C_CR;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * 将字段加入字段列表并自动去包装和去转义
     *
     * @param currentFields 当前的字段列表（即为行）
     * @param field         字段
     */
    private void addField(List<String> currentFields, String field) {
        final char textDelimiter = this.config.textDelimiter;

        // 忽略多余引号后的换行符
        field = StringKit.trim(field, 1, (c -> c == Symbol.C_LF || c == Symbol.C_CR));

        field = StringKit.unWrap(field, textDelimiter);
        field = StringKit.replace(field, "" + textDelimiter + textDelimiter, textDelimiter + "");
        if (this.config.trimField) {
            field = StringKit.trim(field);
        }
        currentFields.add(field);
    }

    /**
     * 内部Buffer
     */
    private static class Buffer {

        final char[] buf;
        /**
         * 标记位置，用于读数据
         */
        private int mark;
        /**
         * 当前位置
         */
        private int position;
        /**
         * 读取的数据长度，一般小于buf.length，-1表示无数据
         */
        private int limit;

        Buffer(int capacity) {
            buf = new char[capacity];
        }

        /**
         * 是否还有未读数据
         *
         * @return 是否还有未读数据
         */
        public final boolean hasRemaining() {
            return position < limit;
        }

        /**
         * 读取到缓存
         * 全量读取，会重置Buffer中所有数据
         *
         * @param reader {@link Reader}
         */
        int read(Reader reader) {
            int length;
            try {
                length = reader.read(this.buf);
            } catch (IOException e) {
                throw new InternalException(e);
            }
            this.mark = 0;
            this.position = 0;
            this.limit = length;
            return length;
        }

        /**
         * 先获取当前字符，再将当前位置后移一位
         * 此方法不检查是否到了数组末尾，请自行使用{@link #hasRemaining()}判断。
         *
         * @return 当前位置字符
         * @see #hasRemaining()
         */
        char get() {
            return this.buf[this.position++];
        }

        /**
         * 标记位置记为下次读取位置
         */
        void mark() {
            this.mark = this.position;
        }

        /**
         * 将数据追加到{@link TextBuilder}，追加结束后需手动调用{@link #mark()} 重置读取位置
         *
         * @param textBuilder {@link TextBuilder}
         * @param length      追加的长度
         * @see #mark()
         */
        void appendTo(TextBuilder textBuilder, int length) {
            textBuilder.append(this.buf, this.mark, length);
        }
    }

}
