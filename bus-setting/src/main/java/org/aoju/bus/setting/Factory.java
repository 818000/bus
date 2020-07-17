/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
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
package org.aoju.bus.setting;

import org.aoju.bus.setting.format.ElementFormatter;
import org.aoju.bus.setting.magic.IniComment;
import org.aoju.bus.setting.magic.IniProperty;
import org.aoju.bus.setting.magic.IniSection;

/**
 * iniFormatter的函数接口
 * 通常，格式化程序需要三种格式，例如
 * {@link IniComment},
 * {@link IniSection},
 * {@link IniProperty}
 *
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
public interface Factory {

    /**
     * 通过三个字符获取一个iniFormatter
     *
     * @param commentElementFormatter  a formatter for comment
     * @param sectionElementFormatter  a formatter for section
     * @param propertyElementFormatter a formatter for property
     * @return an {@link Format}
     */
    Format apply(ElementFormatter<IniComment> commentElementFormatter,
                 ElementFormatter<IniSection> sectionElementFormatter,
                 ElementFormatter<IniProperty> propertyElementFormatter);

}
