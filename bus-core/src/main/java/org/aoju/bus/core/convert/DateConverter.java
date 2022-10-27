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
package org.aoju.bus.core.convert;

import org.aoju.bus.core.date.DateTime;
import org.aoju.bus.core.exception.ConvertException;
import org.aoju.bus.core.toolkit.DateKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;

/**
 * 日期转换器
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class DateConverter extends AbstractConverter {

    public static final DateConverter INSTANCE = new DateConverter();
    private static final long serialVersionUID = 1L;
    /**
     * 日期格式化
     */
    private String format;

    /**
     * 构造
     */
    public DateConverter() {
        this(null);
    }

    /**
     * 构造
     *
     * @param format 日期格式
     */
    public DateConverter(final String format) {
        this.format = format;
    }

    /**
     * 获取日期格式
     *
     * @return 设置日期格式
     */
    public String getFormat() {
        return format;
    }

    /**
     * 设置日期格式
     *
     * @param format 日期格式
     */
    public void setFormat(final String format) {
        this.format = format;
    }

    @Override
    protected java.util.Date convertInternal(final Class<?> targetClass, final Object value) {
        if (value == null || (value instanceof CharSequence && StringKit.isBlank(value.toString()))) {
            return null;
        }
        if (value instanceof TemporalAccessor) {
            return wrap(targetClass, DateKit.date((TemporalAccessor) value));
        } else if (value instanceof Calendar) {
            return wrap(targetClass, DateKit.date((Calendar) value));
        } else if (null == this.format && value instanceof Number) {
            return wrap(targetClass, ((Number) value).longValue());
        } else {
            // 统一按照字符串处理
            final String valueStr = convertToString(value);
            final DateTime dateTime = StringKit.isBlank(this.format)
                    ? DateKit.parse(valueStr) //
                    : DateKit.parse(valueStr, this.format);
            if (null != dateTime) {
                return wrap(targetClass, dateTime);
            }
        }

        throw new ConvertException("Can not convert {}:[{}] to {}", value.getClass().getName(), value, targetClass.getName());
    }

    /**
     * java.util.Date转为子类型
     *
     * @param date Date
     * @return 目标类型对象
     */
    private java.util.Date wrap(final Class<?> targetClass, final DateTime date) {
        // 返回指定类型
        if (java.util.Date.class == targetClass) {
            return date.toJdkDate();
        }
        if (DateTime.class == targetClass) {
            return date;
        }
        if (java.sql.Date.class == targetClass) {
            return date.toSqlDate();
        }
        if (java.sql.Time.class == targetClass) {
            return new java.sql.Time(date.getTime());
        }
        if (java.sql.Timestamp.class == targetClass) {
            return date.toTimestamp();
        }

        throw new UnsupportedOperationException(StringKit.format("Unsupported target Date type: {}", targetClass.getName()));
    }

    /**
     * java.util.Date转为子类型
     *
     * @param mills Date
     * @return 目标类型对象
     */
    private java.util.Date wrap(final Class<?> targetClass, final long mills) {
        // 返回指定类型
        if (java.util.Date.class == targetClass) {
            return new java.util.Date(mills);
        }
        if (DateTime.class == targetClass) {
            return DateKit.date(mills);
        }
        if (java.sql.Date.class == targetClass) {
            return new java.sql.Date(mills);
        }
        if (java.sql.Time.class == targetClass) {
            return new java.sql.Time(mills);
        }
        if (java.sql.Timestamp.class == targetClass) {
            return new java.sql.Timestamp(mills);
        }

        throw new UnsupportedOperationException(StringKit.format("Unsupported target Date type: {}", targetClass.getName()));
    }

}
