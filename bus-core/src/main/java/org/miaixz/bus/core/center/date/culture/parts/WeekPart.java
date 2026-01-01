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

import org.miaixz.bus.core.center.date.culture.Week;

/**
 * Abstract base class representing week-level date-time components.
 * <p>
 * Extends month-level precision with week-based indexing, providing support for weekly time intervals within a month.
 * Each month can contain up to 6 weeks (index 0-5).
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public abstract class WeekPart extends MonthPart {

    /**
     * The week index within the month (0-5)
     */
    protected int index;

    /**
     * The start day of the week.
     * <p>
     * Values 1-6 represent Monday through Saturday, 0 represents Sunday.
     * </p>
     */
    protected int start;

    /**
     * Gets the week index.
     *
     * @return the week index within the month (0-5)
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the start day of the week.
     *
     * @return the WeekPart enum representing the start day
     */
    public Week getStart() {
        return Week.fromIndex(start);
    }

    /**
     * Validates week index and start day values.
     *
     * @param index the week index to validate (0-5)
     * @param start the start day to validate (0-6, where 0=Sunday, 1-6=Monday-Saturday)
     * @throws IllegalArgumentException if index is not in range 0-5 or start is not in range 0-6
     */
    public static void validate(int index, int start) {
        if (index < 0 || index > 5) {
            throw new IllegalArgumentException(String.format("illegal week index: %d", index));
        }
        if (start < 0 || start > 6) {
            throw new IllegalArgumentException(String.format("illegal week start: %d", start));
        }
    }

}
