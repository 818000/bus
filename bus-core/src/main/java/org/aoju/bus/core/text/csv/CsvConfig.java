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
package org.aoju.bus.core.text.csv;

import org.aoju.bus.core.lang.Symbol;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CSV基础配置项
 *
 * @author Kimi Liu
 * @version 6.2.9
 * @since JDK 1.8+
 */
public class CsvConfig<T extends CsvConfig<?>> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段分隔符,默认逗号
     */
    protected char fieldSeparator = Symbol.C_COMMA;
    /**
     * 文本分隔符,文本包装符,默认双引号
     */
    protected char textDelimiter = Symbol.C_DOUBLE_QUOTES;
    /**
     * 注释符号，用于区分注释行，默认'#'
     */
    protected char commentCharacter = Symbol.C_SHAPE;
    /**
     * 标题别名
     */
    protected Map<String, String> headerAlias = new LinkedHashMap<>();

    /**
     * 设置字段分隔符,默认逗号
     *
     * @param fieldSeparator 字段分隔符,默认逗号
     * @return this
     */
    public T setFieldSeparator(final char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
        return (T) this;
    }

    /**
     * 设置 文本分隔符,文本包装符,默认双引号
     *
     * @param textDelimiter 文本分隔符,文本包装符,默认双引号
     * @return this
     */
    public T setTextDelimiter(char textDelimiter) {
        this.textDelimiter = textDelimiter;
        return (T) this;
    }

    /**
     * 设置 注释符号，用于区分注释行
     *
     * @param commentCharacter 注释符号，用于区分注释行
     * @return this
     */
    public T setCommentCharacter(char commentCharacter) {
        this.commentCharacter = commentCharacter;
        return (T) this;
    }

    /**
     * 设置标题行的别名Map
     *
     * @param headerAlias 别名Map
     * @return this
     */
    public T setHeaderAlias(Map<String, String> headerAlias) {
        this.headerAlias = headerAlias;
        return (T) this;
    }

    /**
     * 增加标题别名
     *
     * @param header 标题
     * @param alias  别名
     * @return this
     */
    public T addHeaderAlias(String header, String alias) {
        this.headerAlias.put(header, alias);
        return (T) this;
    }

    /**
     * 去除标题别名
     *
     * @param header 标题
     * @return this
     */
    public T removeHeaderAlias(String header) {
        this.headerAlias.remove(header);
        return (T) this;
    }

}
