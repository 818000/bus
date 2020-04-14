/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.core.date.format;

import org.aoju.bus.core.lang.Assert;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 日期格式化器缓存
 *
 * @author Kimi Liu
 * @version 5.8.5
 * @since JDK 1.8+
 */
public abstract class FormatCache<F extends Format> {


    private static final ConcurrentMap<MultipartKey, String> cDateTimeInstanceCache = new ConcurrentHashMap<>(7);
    private final ConcurrentMap<MultipartKey, F> cInstanceCache = new ConcurrentHashMap<>(7);

    /**
     * 获取指定样式和区域设置的日期/时间格式
     *
     * @param dateStyle 日期样式:FULL、LONG、MEDIUM或SHORT, null表示没有日期格式
     * @param timeStyle 时间样式:FULL、LONG、MEDIUM或SHORT, null表示格式中没有时间
     * @param locale    所需格式的非空语言环境
     * @return 本地化的标准日期/时间格式
     * @throws IllegalArgumentException 如果区域设置没有定义日期/时间模式
     */
    static String getPatternForStyle(final Integer dateStyle, final Integer timeStyle, final Locale locale) {
        final MultipartKey key = new MultipartKey(dateStyle, timeStyle, locale);

        String pattern = cDateTimeInstanceCache.get(key);
        if (pattern == null) {
            try {
                DateFormat formatter;
                if (dateStyle == null) {
                    formatter = DateFormat.getTimeInstance(timeStyle.intValue(), locale);
                } else if (timeStyle == null) {
                    formatter = DateFormat.getDateInstance(dateStyle.intValue(), locale);
                } else {
                    formatter = DateFormat.getDateTimeInstance(dateStyle.intValue(), timeStyle.intValue(), locale);
                }
                pattern = ((SimpleDateFormat) formatter).toPattern();
                final String previous = cDateTimeInstanceCache.putIfAbsent(key, pattern);
                if (previous != null) {
                    pattern = previous;
                }
            } catch (final ClassCastException ex) {
                throw new IllegalArgumentException("No date time pattern for locale: " + locale);
            }
        }
        return pattern;
    }

    /**
     * 使用默认的pattern、timezone和locale获得缓存中的实例
     *
     * @return 日期/时间格式器
     */
    public F getInstance() {
        return getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * 使用 pattern, time zone and locale 获得对应的 格式化器
     *
     * @param pattern  非空日期格式,使用与 {@link SimpleDateFormat}相同格式
     * @param timeZone 时区,默认当前时区
     * @param locale   地区,默认使用当前地区
     * @return 格式化器
     * @throws IllegalArgumentException pattern 无效或<code>null</code>
     */
    public F getInstance(final String pattern, TimeZone timeZone, Locale locale) {
        Assert.notBlank(pattern, "pattern must not be blank");
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final MultipartKey key = new MultipartKey(pattern, timeZone, locale);
        F format = cInstanceCache.get(key);
        if (format == null) {
            format = createInstance(pattern, timeZone, locale);
            final F previousValue = cInstanceCache.putIfAbsent(key, format);
            if (previousValue != null) {
                format = previousValue;
            }
        }
        return format;
    }

    /**
     * 创建格式化器
     *
     * @param pattern  非空日期格式,使用与 {@link SimpleDateFormat}相同格式
     * @param timeZone 时区,默认当前时区
     * @param locale   地区,默认使用当前地区
     * @return 格式化器
     * @throws IllegalArgumentException pattern 无效或<code>null</code>
     */
    abstract protected F createInstance(String pattern, TimeZone timeZone, Locale locale);

    /**
     * 获取使用指定样式、时区和区域设置的日期/时间格式化程序实例
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的日期
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的时间
     * @param timeZone  可选时区，覆盖格式化日期的时区，空表示使用默认地区
     * @param locale    可选区域设置，覆盖系统区域设置
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException 如果区域设置没有定义日期/时间模式
     */
    private F getDateTimeInstance(final Integer dateStyle, final Integer timeStyle, final TimeZone timeZone, Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        final String pattern = getPatternForStyle(dateStyle, timeStyle, locale);
        return getInstance(pattern, timeZone, locale);
    }

    /**
     * 获取使用指定样式、时区和区域设置的日期/时间格式化程序实例
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的日期
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的时间
     * @param timeZone  可选时区，覆盖格式化日期的时区，空表示使用默认地区
     * @param locale    可选区域设置，覆盖系统区域设置
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException 如果区域设置没有定义日期/时间模式
     */
    F getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * 获取使用指定样式、时区和区域设置的日期格式化程序实例
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的日期
     * @param timeZone  可选时区，覆盖格式化日期的时区，空表示使用默认地区
     * @param locale    可选区域设置，覆盖系统区域设置
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException 如果区域设置没有定义日期/时间模式
     */
    F getDateInstance(final int dateStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(Integer.valueOf(dateStyle), null, timeZone, locale);
    }

    /**
     * 获取使用指定样式、时区和区域设置的时间格式化程序实例
     *
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT, null表示没有格式的时间
     * @param timeZone  可选时区，覆盖格式化日期的时区，空表示使用默认地区
     * @param locale    可选区域设置，覆盖系统区域设置
     * @return 本地化的标准日期/时间格式化程序
     * @throws IllegalArgumentException 如果区域设置没有定义日期/时间模式
     */
    F getTimeInstance(final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return getDateTimeInstance(null, Integer.valueOf(timeStyle), timeZone, locale);
    }

    /**
     * 帮助类来保存多部分映射键
     */
    private static class MultipartKey {
        private final Object[] keys;
        private int hashCode;

        /**
         * 构造一个MultipartKey的实例来保存指定的对象
         *
         * @param keys 组成键的一组对象。每个键可以为空
         */
        public MultipartKey(final Object... keys) {
            this.keys = keys;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MultipartKey other = (MultipartKey) obj;
            return false != Arrays.equals(keys, other.keys);
        }

        @Override
        public int hashCode() {
            if (hashCode == 0) {
                int rc = 0;
                for (final Object key : keys) {
                    if (key != null) {
                        rc = rc * 7 + key.hashCode();
                    }
                }
                hashCode = rc;
            }
            return hashCode;
        }
    }

}
