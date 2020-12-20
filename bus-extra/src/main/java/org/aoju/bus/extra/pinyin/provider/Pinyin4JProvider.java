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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.extra.pinyin.provider;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.text.Builders;
import org.aoju.bus.core.toolkit.ArrayKit;
import org.aoju.bus.core.toolkit.StringKit;

/**
 * Pinyin4j引擎
 *
 * @author Kimi Liu
 * @version 6.1.6
 * @since JDK 1.8+
 */
public class Pinyin4JProvider extends AbstractPinyinProvider {

    /**
     * 设置汉子拼音输出的格式
     */
    HanyuPinyinOutputFormat format;

    /**
     * 构造
     */
    public Pinyin4JProvider() {
        this(null);
    }

    /**
     * 构造
     *
     * @param format 格式
     */
    public Pinyin4JProvider(HanyuPinyinOutputFormat format) {
        init(format);
    }

    /**
     * 初始化
     *
     * @param format 格式
     */
    public void init(HanyuPinyinOutputFormat format) {
        if (null == format) {
            format = new HanyuPinyinOutputFormat();
            // 小写
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            // 不加声调
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            // 'ü' 使用 "v" 代替
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
        }
        this.format = format;
    }

    @Override
    public String getPinyin(char c) {
        String result;
        try {
            String[] results = PinyinHelper.toHanyuPinyinStringArray(c, format);
            result = ArrayKit.isEmpty(results) ? String.valueOf(c) : results[0];
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            result = String.valueOf(c);
        }
        return result;
    }

    @Override
    public String getPinyin(String str, String separator) {
        final Builders result = StringKit.builders();
        boolean isFirst = true;
        final int strLen = str.length();
        try {
            for (int i = 0; i < strLen; i++) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    result.append(separator);
                }
                final String[] pinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i), format);
                if (ArrayKit.isEmpty(pinyinStringArray)) {
                    result.append(str.charAt(i));
                } else {
                    result.append(pinyinStringArray[0]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new InstrumentException(e);
        }
        return result.toString();
    }

}
