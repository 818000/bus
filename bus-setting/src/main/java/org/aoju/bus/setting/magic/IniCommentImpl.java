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
package org.aoju.bus.setting.magic;

import org.aoju.bus.core.lang.Symbol;

/**
 * Ini file's comment.
 *
 * @author Kimi Liu
 * @version 6.0.6
 * @since JDK 1.8+
 */
public class IniCommentImpl extends AbstractElement implements IniComment {

    public IniCommentImpl(String value, String originalValue, int lineNumber) {
        super(value, originalValue, lineNumber);
    }

    public IniCommentImpl(String originalValue, int lineNumber) {
        super(originalValue.substring(1), originalValue, lineNumber);
    }

    /**
     * Get instance only based on value
     *
     * @param value      value
     * @param lineNumber line number
     * @return the object
     */
    public static IniCommentImpl byValue(String value, int lineNumber) {
        return new IniCommentImpl(value, Symbol.C_SHAPE + value, lineNumber);
    }

    /**
     * If the value changed, change the originalValue
     *
     * @param newValue when value changes,
     *                 like {@link #setValue(String)} or
     *                 {@link #setValue(java.util.function.Function)}
     * @return the object
     */
    @Override
    protected String valueChanged(String newValue) {
        return "# " + trim(newValue);
    }

}
