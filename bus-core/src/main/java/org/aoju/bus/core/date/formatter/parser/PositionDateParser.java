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

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

/**
 * 带有{@link ParsePosition}的日期解析接口，用于解析日期字符串为 {@link Date} 对象
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public interface PositionDateParser extends DateParser {

    /**
     * 将日期字符串解析并转换为 {@link Date} 对象 等价于 {@link java.text.DateFormat#parse(String, ParsePosition)}
     *
     * @param source 日期字符串
     * @param pos    {@link ParsePosition}
     * @return {@link Date}
     */
    Date parse(String source, ParsePosition pos);

    /**
     * 根据给定格式更新{@link Calendar}
     *
     * @param source   被转换的日期字符串
     * @param pos      定义开始转换的位置，转换结束后更新转换到的位置
     * @param calendar 要将解析字段设置到其中的日历
     * @return true
     */
    boolean parse(String source, ParsePosition pos, Calendar calendar);

    /**
     * 根据 {@link ParsePosition} 给定将日期字符串解析并转换为 {@link Date} 对象
     *
     * @param source 应该解析其开头的{@code String}
     * @param pos    解析的位置
     * @return {@code java.util.date}对象
     */
    default Object parseObject(final String source, final ParsePosition pos) {
        return parse(source, pos);
    }

}
