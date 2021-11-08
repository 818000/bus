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
package org.aoju.bus.office.support.excel.cell.setters;

import org.aoju.bus.core.toolkit.PatternKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.util.regex.Pattern;

/**
 * 字符串转义Cell值设置器
 * 使用 _x005F前缀转义_xXXXX_，避免被decode的问题
 * 如用户传入'_x5116_'会导致乱码，使用此设置器转义为'_x005F_x5116_'
 *
 * @author Kimi Liu
 * @version 6.3.1
 * @since JDK 1.8+
 */
public class EscapeStringCellSetter extends CharSequenceCellSetter {

    private static final Pattern utfPtrn = Pattern.compile("_x[0-9A-Fa-f]{4}_");

    /**
     * 构造
     *
     * @param value 值
     */
    public EscapeStringCellSetter(CharSequence value) {
        super(escape(StringKit.toString(value)));
    }

    /**
     * 使用 _x005F前缀转义_xXXXX_，避免被decode的问题
     *
     * @param value 被转义的字符串
     * @return 转义后的字符串
     */
    private static String escape(String value) {
        if (value == null || false == value.contains("_x")) {
            return value;
        }

        // 使用 _x005F前缀转义_xXXXX_，避免被decode的问题
        return PatternKit.replaceAll(value, utfPtrn, "_x005F$0");
    }
}
