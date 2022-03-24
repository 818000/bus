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
package org.aoju.bus.core.text.finder;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.toolkit.MathKit;

/**
 * 字符查找器
 * 查找指定字符在字符串中的位置信息
 *
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
public class CharFinder extends TextFinder {

    private static final long serialVersionUID = 1L;

    private final char word;
    private final boolean caseInsensitive;

    /**
     * 构造，不忽略字符大小写
     *
     * @param word 被查找的字符
     */
    public CharFinder(char word) {
        this(word, false);
    }

    /**
     * 构造
     *
     * @param word            被查找的字符
     * @param caseInsensitive 是否忽略大小写
     */
    public CharFinder(char word, boolean caseInsensitive) {
        this.word = word;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!");
        final int limit = getValidEndIndex();
        if (negative) {
            for (int i = from; i > limit; i--) {
                if (MathKit.equals(this.word, text.charAt(i), caseInsensitive)) {
                    return i;
                }
            }
        } else {
            for (int i = from; i < limit; i++) {
                if (MathKit.equals(this.word, text.charAt(i), caseInsensitive)) {
                    return i;
                }
            }
        }
        return Normal.__1;
    }

    @Override
    public int end(int start) {
        if (start < 0) {
            return -1;
        }
        return start + 1;
    }

}
