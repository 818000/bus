/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2024 miaixz.org and other contributors.                    ~
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
package org.miaixz.bus.core.center.date.builder;

import org.miaixz.bus.core.lang.exception.DateException;
import org.miaixz.bus.core.xyz.ZoneKit;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * DateBuilder类用于构建和操作日期
 * 该类提供了多个方法来设置年、月、日等日期字段，以及获取构建的日期对象
 * 它是不可变的，因此每个设置方法都会返回一个新的DateBuilder实例
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public final class DateBuilder {

    private static final ZoneOffset DEFAULT_OFFSET = OffsetDateTime.now().getOffset();

    /**
     * 年份
     */
    private int year;
    /**
     * 月份
     * 从1开始
     */
    private int month;
    /**
     * 周数
     * SO8601规范，1代表Monday，2代表Tuesday，以此类推。
     */
    private int week;
    /**
     * 日
     */
    private int day;
    /**
     * 小时
     */
    private int hour;
    /**
     * 分钟
     */
    private int minute;
    /**
     * 秒
     */
    private int second;
    /**
     * 毫秒
     */
    private long millisecond;
    /**
     * 纳秒
     */
    private int nanosecond;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 时区
     */
    private TimeZone zone;
    /**
     * 时区偏移量
     * 分钟
     */
    private int offset;
    /**
     * 时区偏移量是否已设置
     */
    private boolean flag;
    /**
     * 上午标志
     */
    private boolean am;
    /**
     * 下午标志
     */
    private boolean pm;

    /**
     * 创建并返回一个DateBuilder实例
     *
     * @return 一个新的DateBuilder实例
     */
    public static DateBuilder of() {
        return new DateBuilder();
    }

    /**
     * 获取年份
     *
     * @return 返回设置的年份
     */
    public int getYear() {
        return year;
    }

    /**
     * 设置年份。
     *
     * @param year 要设置的年份
     * @return 返回DateBuilder实例，支持链式调用。
     */
    public DateBuilder setYear(final int year) {
        this.year = year;
        return this;
    }

    /**
     * 获取月份，从1开始。
     *
     * @return 返回设置的月份，从1开始。
     */
    public int getMonth() {
        return month;
    }

    /**
     * 设置月份，从1开始。
     *
     * @param month 要设置的月份，从1开始。
     * @return this
     */
    public DateBuilder setMonth(final int month) {
        this.month = month;
        return this;
    }

    /**
     * 获取当前周数的方法
     *
     * @return int 返回当前的周数
     */
    public int getWeek() {
        return week;
    }

    /**
     * 设置日期构建器的周数，ISO8601规范，1代表Monday，2代表Tuesday，以此类推。
     *
     * @param week 指定的周数，通常用于构建具体的日期对象，ISO8601规范，1代表Monday，2代表Tuesday，以此类推。
     * @return this。
     */
    public DateBuilder setWeek(final int week) {
        this.week = week;
        return this;
    }

    /**
     * 获取当前日期对象中的日部分
     *
     * @return 返回一个整数，表示当前日期中的日。
     */
    public int getDay() {
        return day;
    }

    /**
     * 设置日期对象中的日部分
     *
     * @param day 指定要设置的日，必须为整数。
     * @return this
     */
    public DateBuilder setDay(final int day) {
        this.day = day;
        return this;
    }

    /**
     * 获取当前日期对象中的小时数
     *
     * @return 小时数，返回值类型为int。
     */
    public int getHour() {
        return hour;
    }

    /**
     * 设置日期对象中的小时数
     *
     * @param hour 要设置的小时数，必须为整数。
     * @return this
     */
    public DateBuilder setHour(final int hour) {
        this.hour = hour;
        return this;
    }

    /**
     * 获取当前日期构建器中的分钟数
     *
     * @return 返回设置的分钟数，类型为int。
     */
    public int getMinute() {
        return minute;
    }

    /**
     * 设置日期构建器中的分钟数
     *
     * @param minute 要设置的分钟数，必须为整数。
     * @return this。
     */
    public DateBuilder setMinute(final int minute) {
        this.minute = minute;
        return this;
    }

    /**
     * 获取当前日期时间对象中的秒数
     *
     * @return 返回当前日期时间对象中的秒数
     */
    public int getSecond() {
        return second;
    }

    /**
     * 设置日期时间对象中的秒数
     *
     * @param second 指定要设置的秒数
     * @return this
     */
    public DateBuilder setSecond(final int second) {
        this.second = second;
        return this;
    }

    /**
     * 获取纳秒数
     *
     * @return 当前对象的纳秒数
     */
    public int getNanosecond() {
        return nanosecond;
    }

    /**
     * 设置纳秒数
     *
     * @param nanosecond 要设置的纳秒数
     * @return this
     */
    public DateBuilder setNanosecond(final int nanosecond) {
        this.nanosecond = nanosecond;
        return this;
    }

    /**
     * 获取Unix时间戳（秒）
     *
     * @return 当前对象的Unix时间戳（以秒为单位）
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置Unix时间戳（秒）
     *
     * @param timestamp 要设置的Unix时间戳（以秒为单位）
     * @return this
     */
    public DateBuilder setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * 获取时间戳（毫秒）
     *
     * @return 当前对象的时间戳（以毫秒为单位）
     */
    public long getMillisecond() {
        return millisecond;
    }

    /**
     * 设置时间戳（毫秒）
     *
     * @param millisecond 要设置的时间戳（以毫秒为单位）
     * @return this
     */
    public DateBuilder setMillisecond(final long millisecond) {
        this.millisecond = millisecond;
        return this;
    }

    /**
     * 检查时区偏移量是否已设置
     *
     * @return 如果时区偏移量已设置则返回true，否则返回false。
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * 设置时区偏移量是否已设置的状态
     *
     * @param flag 指定时区偏移量是否已设置的状态
     * @return this
     */
    public DateBuilder setFlag(final boolean flag) {
        this.flag = flag;
        return this;
    }

    /**
     * 获取时区偏移量
     *
     * @return 返回设置的时区偏移量
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 设置时区偏移量
     *
     * @param offset 要设置的时区偏移量
     * @return this
     */
    public DateBuilder setOffset(final int offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 获取时区
     *
     * @return 返回设置的时区
     */
    public TimeZone getZone() {
        return zone;
    }

    /**
     * 设置时区
     *
     * @param zone 要设置的时区
     * @return this
     */
    public DateBuilder setZone(final TimeZone zone) {
        this.zone = zone;
        return this;
    }

    /**
     * 检查当前是否为上午
     *
     * @return 如果当前设置为上午则返回true，否则返回false。
     */
    public boolean isAm() {
        return am;
    }

    /**
     * 设置是否为上午的状态
     *
     * @param am 指定是否为上午的状态
     * @return this
     */
    public DateBuilder setAm(final boolean am) {
        this.am = am;
        return this;
    }

    /**
     * 检查当前是否为下午
     *
     * @return 如果当前设置为下午则返回true，否则返回false。
     */
    public boolean isPm() {
        return pm;
    }

    /**
     * 设置是否为下午的状态
     *
     * @param pm 指定是否为下午的状态
     * @return this
     */
    public DateBuilder setPm(final boolean pm) {
        this.pm = pm;
        return this;
    }

    /**
     * 重置所有值为默认值
     *
     * @return this
     */
    public DateBuilder reset() {
        this.week = 1;
        this.year = 0;
        this.month = 1;
        this.day = 1;
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        this.millisecond = 0;
        this.nanosecond = 0;
        this.timestamp = 0;
        this.am = false;
        this.pm = false;
        this.flag = false;
        this.offset = 0;
        this.zone = null;
        return this;
    }

    /**
     * 将当前时间对象转换为{@link Date}类型。此方法根据是否设置了时区偏移量使用不同的转换策略。
     * <ul>
     *     <li>如果时区偏移量未设置，则将时间转换为 Calendar 对象后获取其 Date 表现形式</li>
     *     <li>如果时区偏移量已设置，则直接转换为 OffsetDateTime 对象，进一步转换为 Instant 对象，最后转换为 Date 对象返回。</li>
     * </ul>
     *
     * @return the date 表示当前时间的 Date 对象
     */
    public Date toDate() {
        if (!flag) {
            // 时区偏移量未设置，使用 Calendar 进行转换
            return toCalendar().getTime();
        }
        // 时区偏移量已设置，直接转换为 Date 对象返回
        return Date.from(toOffsetDateTime().toInstant());
    }

    /**
     * 将当前对象的日期时间信息转换为{@link Calendar}实例。
     * 如果`timestamp`不为0，将根据时间戳（秒）和纳秒偏移量构造Calendar。
     * 否则，根据提供的时区信息`zone`或`offset`来设置Calendar的时区。
     * 最后，设置年、月、日、时、分、秒和毫秒信息。
     *
     * @return Calendar 根据当前日期时间信息构建的Calendar实例。
     * @throws DateTimeException 如果时区偏移量无法转换为有效的时区ID，则抛出异常。
     */
    public Calendar toCalendar() {
        this.prepare();
        // 获取一个Calendar实例
        final Calendar calendar = Calendar.getInstance();

        // 设置时区
        if (zone != null) {
            // 使用指定的时区
            calendar.setTimeZone(zone);
            // 如果设置了时区偏移量
        } else if (flag) {
            // 尝试根据偏移量获取时区ID
            final String[] ids = TimeZone.getAvailableIDs(offset * 60_000);
            // 如果没有找到有效的时区ID
            if (ids.length == 0) {
                throw new DateException("Can't build Calendar, " +
                        "because the zoneOffset[{}] can't be converted to an valid TimeZone.", this.offset);
            }
            // 设置第一个找到的时区
            calendar.setTimeZone(TimeZone.getTimeZone(ids[0]));
        }

        // 如果毫秒数不为0，则直接使用毫秒数设置时间
        if (millisecond != 0) {
            calendar.setTimeInMillis(millisecond);
            return calendar;
        }

        // 如果有unix时间戳，则据此设置时间
        if (timestamp != 0) {
            // 设置时间戳对应的毫秒数
            calendar.setTimeInMillis(timestamp * 1000 + nanosecond / 1_000_000);
            return calendar;
        }

        // 设置日期和时间字段
        calendar.set(Calendar.YEAR, year);
        // Calendar的月份从0开始
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        // 纳秒转换为毫秒
        calendar.set(Calendar.MILLISECOND, nanosecond / 1_000_000);
        return calendar;
    }

    /**
     * 将当前对象的日期时间信息转换为{@link LocalDateTime}。
     * 此方法根据对象中的时间信息（年、月、日、时、分、秒、纳秒）和时区信息（如果存在），
     * 创建并返回一个LocalDateTime实例。时区信息可以是Unix时间戳中的秒数（timestamp），
     * 也可以是显式设置的时区偏移量（flag），或者使用默认时区（zone != null）。
     *
     * @return LocalDateTime 表示当前对象日期时间的LocalDateTime实例。
     */
    public LocalDateTime toLocalDateTime() {
        this.prepare();

        if (millisecond > 0) {
            final Instant instant = Instant.ofEpochMilli(millisecond);
            return LocalDateTime.ofEpochSecond(instant.getEpochSecond(), instant.getNano(), DEFAULT_OFFSET);
        }

        // 如果timestamp大于0，使用unix时间戳创建LocalDateTime
        if (timestamp > 0) {
            return LocalDateTime.ofEpochSecond(timestamp, nanosecond, DEFAULT_OFFSET);
        }

        // 创建LocalDateTime实例，使用年月日时分秒纳秒信息
        final LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, nanosecond);

        int zoneSecond = 0; // 用于存储时区偏移秒数

        // 如果设置了时区，计算时区偏移量
        if (zone != null) {
            zoneSecond = (TimeZone.getDefault().getRawOffset() - zone.getRawOffset()) / 1000;
        }

        // 如果设置了显式的时区偏移量，计算时区偏移量
        if (flag) {
            zoneSecond = TimeZone.getDefault().getRawOffset() / 1000 - offset * 60;
        }

        // 如果存在时区偏移，对LocalDateTime进行调整后返回，否则直接返回原始的LocalDateTime实例
        return zoneSecond == 0 ? dateTime : dateTime.plusSeconds(zoneSecond);
    }

    /**
     * 将当前对象转换为 {@link OffsetDateTime}。
     * 此方法根据 timestamp、时区偏移量或时区来构建 OffsetDateTime。
     * 如果 timestamp 大于 0，将使用 timestamp 和纳秒来创建 UTC 时间。
     * 如果设置了时区偏移量，将使用该偏移量构造 {@link OffsetDateTime}。
     * 如果设置了时区，将使用该时区构造 {@link OffsetDateTime}。
     * 如果以上信息均未设置，则默认使用 UTC 时间戳 0 创建 OffsetDateTime。
     *
     * @return 表示当前时间的 OffsetDateTime 对象
     */
    public OffsetDateTime toOffsetDateTime() {
        // 准备工作，可能涉及一些初始化或数据处理
        this.prepare();

        if (millisecond > 0) {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(millisecond), ZoneKit.ZONE_ID_UTC);
        }

        if (timestamp > 0) {
            // 如果设置了 unix 时间戳，则使用它和纳秒创建 UTC 时间
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp, nanosecond), ZoneKit.ZONE_ID_UTC);
        }
        // 创建 LocalDateTime 对象
        final LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second, nanosecond);

        // 检查是否设置了时区偏移量
        if (flag) {
            // 根据偏移量创建 ZoneOffset
            final ZoneOffset offset = ZoneOffset.ofHoursMinutes(this.offset / 60, this.offset % 60);
            // 使用时区偏移量构造 OffsetDateTime
            return dateTime.atOffset(offset);
        }

        // 检查是否设置了时区
        if (zone != null) {
            // 使用时区构造 OffsetDateTime
            return dateTime.atZone(zone.toZoneId()).toOffsetDateTime();
        }

        // 默认情况下，使用 UTC 时间戳 0 创建 OffsetDateTime
        return dateTime.atZone(ZoneOffset.ofHoursMinutes(0, 0)).toOffsetDateTime();
    }

    /**
     * 根据上午（am）或下午（pm）的设置，转换12小时制为24小时制。
     */
    private void prepare() {
        // 如果设置为上午且小时为12，将小时调整为0
        if (am && hour == 12) {
            this.hour = 0;
        }
        // 如果设置为下午且小时不是12，将小时增加12
        if (pm && hour != 12) {
            this.hour += 12;
        }
    }

}
