/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.date.formatter.parser;

import org.aoju.bus.core.date.formatter.DateMotd;

import java.text.ParseException;
import java.util.Date;

/**
 * 日期解析接口,用于解析日期字符串为 {@link Date} 对象
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public interface DateParser extends DateMotd {

    /**
     * 将日期字符串解析并转换为  {@link Date} 对象
     * 等价于 {@link java.text.DateFormat#parse(String)}
     *
     * @param source 被解析的日期字符串
     * @return {@link Date}对象
     * @throws ParseException 转换异常，被转换的字符串格式错误。
     */
    Date parse(String source) throws ParseException;

    /**
     * 将日期字符串解析并转换为  {@link Date} 对象
     *
     * @param source 被解析的日期字符串
     * @return {@link Date}对象
     * @throws ParseException if the beginning of the specified string cannot be parsed.
     * @see java.text.DateFormat#parseObject(String)
     */
    default Object parseObject(final String source) throws ParseException {
        return parse(source);
    }

}
