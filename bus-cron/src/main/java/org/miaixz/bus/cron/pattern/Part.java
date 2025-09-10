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
package org.miaixz.bus.cron.pattern;

import java.util.Calendar;

import org.miaixz.bus.core.center.date.culture.en.Month;
import org.miaixz.bus.core.center.date.culture.en.Week;
import org.miaixz.bus.core.lang.Assert;
import org.miaixz.bus.core.lang.exception.CrontabException;

/**
 * 表达式各个部分的枚举，用于限定在表达式中的位置和规则（如最小值和最大值） {@link #ordinal()}表示此部分在表达式中的位置，如0表示秒 表达式各个部分的枚举位置为：
 * 
 * <pre>
 *         0       1    2        3         4       5         6
 *     [SECOND] MINUTE HOUR DAY_OF_MONTH MONTH DAY_OF_WEEK [YEAR]
 * </pre>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public enum Part {

    /**
     * 秒[0-59]
     */
    SECOND(Calendar.SECOND, 0, 59),
    /**
     * 分[0-59]
     */
    MINUTE(Calendar.MINUTE, 0, 59),
    /**
     * 时[0-23]
     */
    HOUR(Calendar.HOUR_OF_DAY, 0, 23),
    /**
     * 日[1-31]，32表示最后一天
     */
    DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 32),
    /**
     * 月[1-12]
     */
    MONTH(Calendar.MONTH, Month.JANUARY.getIsoValue(), Month.DECEMBER.getIsoValue()),
    /**
     * 周中的天，如周一、周日等，[0-6]即[SUNDAY-SATURDAY]
     */
    DAY_OF_WEEK(Calendar.DAY_OF_WEEK, Week.SUNDAY.ordinal(), Week.SATURDAY.ordinal()),
    /**
     * 年[1970-2099]
     */
    YEAR(Calendar.YEAR, 1970, 2099);

    private static final Part[] ENUMS = Part.values();

    private final int calendarField;
    private final int min;
    private final int max;

    /**
     * 构造
     *
     * @param calendarField Calendar中对应字段项
     * @param min           限定最小值（包含）
     * @param max           限定最大值（包含）
     */
    Part(final int calendarField, final int min, final int max) {
        this.calendarField = calendarField;
        if (min > max) {
            this.min = max;
            this.max = min;
        } else {
            this.min = min;
            this.max = max;
        }
    }

    /**
     * 根据位置获取Part
     *
     * @param i 位置，从0开始
     * @return Part
     */
    public static Part of(final int i) {
        return ENUMS[i];
    }

    /**
     * 获取Calendar中对应字段项
     *
     * @return Calendar中对应字段项
     */
    public int getCalendarField() {
        return this.calendarField;
    }

    /**
     * 获取最小值
     *
     * @return 最小值
     */
    public int getMin() {
        return this.min;
    }

    /**
     * 获取最大值
     *
     * @return 最大值
     */
    public int getMax() {
        return this.max;
    }

    /**
     * 检查单个值是否有效
     *
     * @param value 值
     * @return 检查后的值
     * @throws CrontabException 检查无效抛出此异常
     */
    public int checkValue(final int value) throws CrontabException {
        Assert.checkBetween(value, min, max,
                () -> new CrontabException("{} value {} out of range: [{} , {}]", this.name(), value, min, max));
        return value;
    }

}
