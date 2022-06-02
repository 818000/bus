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
package org.aoju.bus.core.text.replacer;

import org.aoju.bus.core.lang.Chain;
import org.aoju.bus.core.text.TextBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 字符串替换链，用于组合多个字符串替换逻辑
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class ReplacerChain extends TextReplacer implements Chain<TextReplacer, ReplacerChain> {

    private static final long serialVersionUID = 1L;

    private final List<TextReplacer> replacers = new LinkedList<>();

    /**
     * 构造
     *
     * @param replacers 字符串替换器
     */
    public ReplacerChain(TextReplacer... replacers) {
        for (TextReplacer replacer : replacers) {
            addChain(replacer);
        }
    }

    @Override
    public Iterator<TextReplacer> iterator() {
        return replacers.iterator();
    }

    @Override
    public ReplacerChain addChain(TextReplacer element) {
        replacers.add(element);
        return this;
    }

    @Override
    protected int replace(CharSequence text, int indexes, TextBuilder builder) {
        int consumed = 0;
        for (TextReplacer replacer : replacers) {
            consumed = replacer.replace(text, indexes, builder);
            if (0 != consumed) {
                return consumed;
            }
        }
        return consumed;
    }

}
