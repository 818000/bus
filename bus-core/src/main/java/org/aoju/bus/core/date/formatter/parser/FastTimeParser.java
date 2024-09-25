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

import org.aoju.bus.core.date.DateTime;
import org.aoju.bus.core.date.Formatter;
import org.aoju.bus.core.date.formatter.NormalMotd;
import org.aoju.bus.core.lang.Fields;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.StringKit;

/**
 * 时间日期字符串，日期默认为当天，支持格式类似于；
 * 
 * <pre>
 *   HH:mm:ss
 *   HH:mm
 * </pre>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class FastTimeParser extends NormalMotd implements DateParser {

    public static FastTimeParser INSTANCE = new FastTimeParser();

    @Override
    public DateTime parse(String source) {
        source = StringKit.format("{} {}", Formatter.format(new DateTime()), source);
        if (1 == StringKit.count(source, Symbol.C_COLON)) {
            // 时间格式为 HH:mm
            return new DateTime(source, Fields.NORM_DATETIME_MINUTE_PATTERN);
        } else {
            // 时间格式为 HH:mm:ss
            return new DateTime(source, Fields.NORM_DATETIME_FORMAT);
        }
    }

}
