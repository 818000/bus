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
package org.aoju.bus.core.date.format;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * FastDateFormat 是一个线程安全的 {@link java.text.SimpleDateFormat}
 * 实现通过以下静态方法获得此对象:
 * {@link #getInstance(String, TimeZone, Locale)}
 * {@link #getDateInstance(int, TimeZone, Locale)}
 * {@link #getTimeInstance(int, TimeZone, Locale)}
 * {@link #getDateTimeInstance(int, int, TimeZone, Locale)}
 *
 * @author Kimi Liu
 * @version 5.9.2
 * @since JDK 1.8+
 */
public class FormatBuilder extends Format implements DateParser, DatePrinter {

    /**
     * 完全地区相关的日期或时间样式
     */
    public static final int FULL = DateFormat.FULL;
    /**
     * 长地区相关的日期或时间样式
     */
    public static final int LONG = DateFormat.LONG;
    /**
     * 中等地区相关的日期或时间样式
     */
    public static final int MEDIUM = DateFormat.MEDIUM;
    /**
     * 短地区相关的日期或时间样式
     */
    public static final int SHORT = DateFormat.SHORT;

    private static final FormatCache<FormatBuilder> cache = new FormatCache<FormatBuilder>() {
        @Override
        protected FormatBuilder createInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
            return new FormatBuilder(pattern, timeZone, locale);
        }
    };

    private final FastDatePrinter printer;
    private final FastDateParser parser;

    /**
     * 构造
     *
     * @param pattern  使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @param timeZone 非空时区 {@link TimeZone}
     * @param locale   {@link Locale} 日期地理位置
     * @throws NullPointerException 如果模式、时区或区域设置为空
     */
    protected FormatBuilder(final String pattern, final TimeZone timeZone, final Locale locale) {
        this(pattern, timeZone, locale, null);
    }

    /**
     * 构造
     *
     * @param pattern      使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @param timeZone     非空时区 {@link TimeZone}
     * @param locale       {@link Locale} 日期地理位置
     * @param centuryStart 使用100年周期的开始作为2位数年解析的“默认世纪”。如果centuryStart为空，则默认为- 80年
     * @throws NullPointerException 如果模式、时区或地区为空.
     */
    protected FormatBuilder(final String pattern, final TimeZone timeZone, final Locale locale, final Date centuryStart) {
        printer = new FastDatePrinter(pattern, timeZone, locale);
        parser = new FastDateParser(pattern, timeZone, locale, centuryStart);
    }

    /**
     * 获得 {@link FormatBuilder} 实例，使用默认格式和地区
     *
     * @return {@link FormatBuilder}
     */
    public static FormatBuilder getInstance() {
        return cache.getInstance();
    }

    /**
     * 获得 {@link FormatBuilder} 实例,使用默认地区
     * 支持缓存
     *
     * @param pattern 使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @return {@link FormatBuilder}
     * @throws IllegalArgumentException 日期格式问题
     */
    public static FormatBuilder getInstance(final String pattern) {
        return cache.getInstance(pattern, null, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param pattern  使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @param timeZone 时区{@link TimeZone}
     * @return {@link FormatBuilder}
     * @throws IllegalArgumentException 日期格式问题
     */
    public static FormatBuilder getInstance(final String pattern, final TimeZone timeZone) {
        return cache.getInstance(pattern, timeZone, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param pattern 使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @param locale  {@link Locale} 日期地理位置
     * @return {@link FormatBuilder}
     * @throws IllegalArgumentException 日期格式问题
     */
    public static FormatBuilder getInstance(final String pattern, final Locale locale) {
        return cache.getInstance(pattern, null, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param pattern  使用{@link java.text.SimpleDateFormat} 相同的日期格式
     * @param timeZone 时区{@link TimeZone}
     * @param locale   {@link Locale} 日期地理位置
     * @return {@link FormatBuilder}
     * @throws IllegalArgumentException 日期格式问题
     */
    public static FormatBuilder getInstance(final String pattern, final TimeZone timeZone, final Locale locale) {
        return cache.getInstance(pattern, timeZone, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style 日期格式: FULL, LONG, MEDIUM, or SHORT
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateInstance(final int style) {
        return cache.getDateInstance(style, null, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style  日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param locale {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateInstance(final int style, final Locale locale) {
        return cache.getDateInstance(style, null, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style    日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone 时区{@link TimeZone}
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateInstance(final int style, final TimeZone timeZone) {
        return cache.getDateInstance(style, timeZone, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style    日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone 时区{@link TimeZone}
     * @param locale   {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return cache.getDateInstance(style, timeZone, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style 时间格式: FULL, LONG, MEDIUM, or SHORT
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getTimeInstance(final int style) {
        return cache.getTimeInstance(style, null, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style  时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param locale {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getTimeInstance(final int style, final Locale locale) {
        return cache.getTimeInstance(style, null, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style    时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone 可选时区，覆盖格式化时间的时区
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getTimeInstance(final int style, final TimeZone timeZone) {
        return cache.getTimeInstance(style, timeZone, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param style    时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone 可选时区，覆盖格式化时间的时区
     * @param locale   {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getTimeInstance(final int style, final TimeZone timeZone, final Locale locale) {
        return cache.getTimeInstance(style, timeZone, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateTimeInstance(final int dateStyle, final int timeStyle) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, null, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param locale    {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateTimeInstance(final int dateStyle, final int timeStyle, final Locale locale) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, null, locale);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone  时区{@link TimeZone}
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone) {
        return getDateTimeInstance(dateStyle, timeStyle, timeZone, null);
    }

    /**
     * 获得 {@link FormatBuilder} 实例
     * 支持缓存
     *
     * @param dateStyle 日期格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeStyle 时间格式: FULL, LONG, MEDIUM, or SHORT
     * @param timeZone  时区{@link TimeZone}
     * @param locale    {@link Locale} 日期地理位置
     * @return 本地化 {@link FormatBuilder}
     */
    public static FormatBuilder getDateTimeInstance(final int dateStyle, final int timeStyle, final TimeZone timeZone, final Locale locale) {
        return cache.getDateTimeInstance(dateStyle, timeStyle, timeZone, locale);
    }

    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(printer.format(obj));
    }

    @Override
    public String format(final long millis) {
        return printer.format(millis);
    }

    @Override
    public String format(final Date date) {
        return printer.format(date);
    }

    @Override
    public String format(final Calendar calendar) {
        return printer.format(calendar);
    }

    @Override
    public <B extends Appendable> B format(final long millis, final B buf) {
        return printer.format(millis, buf);
    }

    @Override
    public <B extends Appendable> B format(final Date date, final B buf) {
        return printer.format(date, buf);
    }

    @Override
    public <B extends Appendable> B format(final Calendar calendar, final B buf) {
        return printer.format(calendar, buf);
    }

    @Override
    public Date parse(final String source) throws ParseException {
        return parser.parse(source);
    }

    @Override
    public Date parse(final String source, final ParsePosition pos) {
        return parser.parse(source, pos);
    }

    @Override
    public boolean parse(final String source, final ParsePosition pos, final Calendar calendar) {
        return parser.parse(source, pos, calendar);
    }

    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        return parser.parseObject(source, pos);
    }

    @Override
    public String getPattern() {
        return printer.getPattern();
    }

    @Override
    public TimeZone getTimeZone() {
        return printer.getTimeZone();
    }

    @Override
    public Locale getLocale() {
        return printer.getLocale();
    }

    /**
     * 估算生成的日期字符串长度
     * 实际生成的字符串长度小于或等于此值
     *
     * @return 日期字符串长度
     */
    public int getMaxLengthEstimate() {
        return printer.getMaxLengthEstimate();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FormatBuilder == false) {
            return false;
        }
        final FormatBuilder other = (FormatBuilder) obj;
        return printer.equals(other.printer);
    }

    @Override
    public int hashCode() {
        return printer.hashCode();
    }

    @Override
    public String toString() {
        return "FastDateFormat[" + printer.getPattern() + "," + printer.getLocale() + "," + printer.getTimeZone().getID() + "]";
    }

}
