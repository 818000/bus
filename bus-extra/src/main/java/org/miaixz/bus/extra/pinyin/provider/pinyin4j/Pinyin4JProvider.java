/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.extra.pinyin.provider.pinyin4j;

import org.apache.logging.log4j.util.InternalException;
import org.miaixz.bus.core.lang.Assert;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.extra.pinyin.PinyinProvider;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * 封装了Pinyin4j的引擎。
 *
 * <p>
 * pinyin4j(<a href="http://sourceforge.net/projects/pinyin4j">http://sourceforge.net/projects/pinyin4j</a>)封装。
 * </p>
 *
 * <p>
 * 引入：
 * 
 * <pre>
 * &lt;dependency&gt;
 *     &lt;groupId&gt;com.belerweb&lt;/groupId&gt;
 *     &lt;artifactId&gt;pinyin4j&lt;/artifactId&gt;
 *     &lt;version&gt;2.5.1&lt;/version&gt;
 * &lt;/dependency&gt;
 * </pre>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Pinyin4JProvider implements PinyinProvider {

    private static final HanyuPinyinOutputFormat WITH_TONE_MARK;
    private static final HanyuPinyinOutputFormat WITHOUT_TONE;
    static {
        WITH_TONE_MARK = new HanyuPinyinOutputFormat();
        // 小写
        WITH_TONE_MARK.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 'ü' 使用 "v" 代替
        WITH_TONE_MARK.setVCharType(HanyuPinyinVCharType.WITH_V);
        WITH_TONE_MARK.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);

        WITHOUT_TONE = new HanyuPinyinOutputFormat();
        // 小写
        WITHOUT_TONE.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        // 'ü' 使用 "v" 代替
        WITHOUT_TONE.setVCharType(HanyuPinyinVCharType.WITH_V);
        WITHOUT_TONE.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    /**
     * 构造
     */
    public Pinyin4JProvider() {
        // SPI方式加载时检查库是否引入
        Assert.notNull(PinyinHelper.class);
    }

    @Override
    public String getPinyin(final char c, final boolean tone) {
        String result;
        try {
            final String[] results = PinyinHelper.toHanyuPinyinStringArray(c, tone ? WITH_TONE_MARK : WITHOUT_TONE);
            result = ArrayKit.isEmpty(results) ? String.valueOf(c) : results[0];
        } catch (final BadHanyuPinyinOutputFormatCombination e) {
            result = String.valueOf(c);
        }
        return result;
    }

    @Override
    public String getPinyin(final String str, final String separator, final boolean tone) {
        final StringBuilder result = new StringBuilder();
        boolean isFirst = true;
        final int strLen = str.length();
        try {
            for (int i = 0; i < strLen; i++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    result.append(separator);
                }
                final String[] pinyinStringArray = PinyinHelper
                        .toHanyuPinyinStringArray(str.charAt(i), tone ? WITH_TONE_MARK : WITHOUT_TONE);
                if (ArrayKit.isEmpty(pinyinStringArray)) {
                    result.append(str.charAt(i));
                } else {
                    result.append(pinyinStringArray[0]);
                }
            }
        } catch (final BadHanyuPinyinOutputFormatCombination e) {
            throw new InternalException(e);
        }
        return result.toString();
    }

}
