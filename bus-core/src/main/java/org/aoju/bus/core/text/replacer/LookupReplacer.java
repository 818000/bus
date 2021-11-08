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
package org.aoju.bus.core.text.replacer;

import org.aoju.bus.core.text.TextBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 查找替换器，通过查找指定关键字，替换对应的值
 *
 * @author Kimi Liu
 * @version 6.3.1
 * @since JDK 1.8+
 */
public class LookupReplacer extends TextReplacer {

    private static final long serialVersionUID = 1L;

    private final Map<String, String> lookupMap;
    private final Set<Character> prefixSet;
    private final int minLength;
    private final int maxLength;

    /**
     * 构造
     *
     * @param lookup 被查找的键值对
     */
    public LookupReplacer(String[]... lookup) {
        this.lookupMap = new HashMap<>();
        this.prefixSet = new HashSet<>();

        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;
        String key;
        int keySize;
        for (String[] pair : lookup) {
            key = pair[0];
            lookupMap.put(key, pair[1]);
            this.prefixSet.add(key.charAt(0));
            keySize = key.length();
            if (keySize > maxLength) {
                maxLength = keySize;
            }
            if (keySize < minLength) {
                minLength = keySize;
            }
        }
        this.maxLength = maxLength;
        this.minLength = minLength;
    }

    @Override
    protected int replace(CharSequence text, int indexes, TextBuilder builder) {
        if (prefixSet.contains(text.charAt(indexes))) {
            int max = this.maxLength;
            if (indexes + this.maxLength > text.length()) {
                max = text.length() - indexes;
            }
            CharSequence subSeq;
            String result;
            for (int i = max; i >= this.minLength; i--) {
                subSeq = text.subSequence(indexes, indexes + i);
                result = lookupMap.get(subSeq.toString());
                if (null != result) {
                    builder.append(result);
                    return i;
                }
            }
        }
        return 0;
    }

}
