/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.toolkit;

import org.aoju.bus.core.text.csv.CsvReadConfig;
import org.aoju.bus.core.text.csv.CsvReader;
import org.aoju.bus.core.text.csv.CsvWriteConfig;
import org.aoju.bus.core.text.csv.CsvWriter;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * CSV工具
 *
 * @author Kimi Liu
 * @version 6.2.9
 * @since JDK 1.8+
 */
public class CsvKit {

    /**
     * 获取CSV读取器
     *
     * @return {@link CsvReader}
     */
    public static CsvReader getReader() {
        return new CsvReader();
    }

    /**
     * 获取CSV读取器
     *
     * @param config 配置
     * @return {@link CsvReader}
     */
    public static CsvReader getReader(CsvReadConfig config) {
        return new CsvReader(config);
    }

    /**
     * 获取CSV读取器
     *
     * @param reader {@link Reader}
     * @param config 配置, {@code null}表示默认配置
     * @return {@link CsvReader}
     */
    public static CsvReader getReader(Reader reader, CsvReadConfig config) {
        return new CsvReader(reader, config);
    }

    /**
     * 获取CSV生成器(写出器),使用默认配置,覆盖已有文件(如果存在)
     *
     * @param filePath File CSV文件路径
     * @param charset  编码
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(String filePath, Charset charset) {
        return new CsvWriter(filePath, charset);
    }

    /**
     * 获取CSV生成器(写出器),使用默认配置,覆盖已有文件(如果存在)
     *
     * @param file    File CSV文件
     * @param charset 编码
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(File file, Charset charset) {
        return new CsvWriter(file, charset);
    }

    /**
     * 获取CSV生成器(写出器),使用默认配置
     *
     * @param filePath File CSV文件路径
     * @param charset  编码
     * @param isAppend 是否追加
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(String filePath, Charset charset, boolean isAppend) {
        return new CsvWriter(filePath, charset, isAppend);
    }

    /**
     * 获取CSV生成器(写出器),使用默认配置
     *
     * @param file     File CSV文件
     * @param charset  编码
     * @param isAppend 是否追加
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(File file, Charset charset, boolean isAppend) {
        return new CsvWriter(file, charset, isAppend);
    }

    /**
     * 获取CSV生成器(写出器)
     *
     * @param file     File CSV文件
     * @param charset  编码
     * @param isAppend 是否追加
     * @param config   写出配置,null则使用默认配置
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(File file, Charset charset, boolean isAppend, CsvWriteConfig config) {
        return new CsvWriter(file, charset, isAppend, config);
    }

    /**
     * 获取CSV生成器(写出器)
     *
     * @param writer Writer
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(Writer writer) {
        return new CsvWriter(writer);
    }

    /**
     * 获取CSV生成器(写出器)
     *
     * @param writer Writer
     * @param config 写出配置,null则使用默认配置
     * @return {@link CsvWriter}
     */
    public static CsvWriter getWriter(Writer writer, CsvWriteConfig config) {
        return new CsvWriter(writer, config);
    }

}
