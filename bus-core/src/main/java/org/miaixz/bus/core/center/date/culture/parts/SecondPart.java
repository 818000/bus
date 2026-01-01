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
package org.miaixz.bus.core.center.date.culture.parts;

/**
 * Abstract base class representing second-level date-time components.
 * <p>
 * Extends day-level precision by adding hour, minute, and second fields, providing the finest level of temporal
 * precision in this hierarchy with second-level granularity.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public abstract class SecondPart extends DayPart {

    /**
     * The hour value (0-23)
     */
    protected int hour;

    /**
     * The minute value (0-59)
     */
    protected int minute;

    /**
     * The second value (0-59)
     */
    protected int second;

    /**
     * Validates time values for hour, minute, and second.
     *
     * @param hour   the hour value to validate (0-23)
     * @param minute the minute value to validate (0-59)
     * @param second the second value to validate (0-59)
     * @throws IllegalArgumentException if hour is not in range 0-23, minute is not in range 0-59, or second is not in
     *                                  range 0-59
     */
    public static void validate(int hour, int minute, int second) {
        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException(String.format("illegal hour: %d", hour));
        }
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException(String.format("illegal minute: %d", minute));
        }
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException(String.format("illegal second: %d", second));
        }
    }

    /**
     * Gets the hour value.
     *
     * @return the hour value (0-23)
     */
    public int getHour() {
        return hour;
    }

    /**
     * Gets the minute value.
     *
     * @return the minute value (0-59)
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Gets the second value.
     *
     * @return the second value (0-59)
     */
    public int getSecond() {
        return second;
    }

}
