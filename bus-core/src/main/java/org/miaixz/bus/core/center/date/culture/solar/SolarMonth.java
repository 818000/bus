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
package org.miaixz.bus.core.center.date.culture.solar;

import java.util.ArrayList;
import java.util.List;

import org.miaixz.bus.core.center.date.culture.parts.MonthPart;

/**
 * Represents a month in the Gregorian calendar.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class SolarMonth extends MonthPart {

    /**
     * Names of solar months.
     */
    public static final String[] NAMES = { "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月" };

    /**
     * Number of days in each month (for non-leap years).
     */
    public static final int[] DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    /**
     * Constructs a {@code SolarMonth} with the given year and month.
     *
     * @param year  The year.
     * @param month The month.
     * @throws IllegalArgumentException if the month is out of valid range (1-12).
     */
    public SolarMonth(int year, int month) {
        validate(year, month);
        this.year = year;
        this.month = month;
    }

    /**
     * Creates a {@code SolarMonth} instance from the given year and month.
     *
     * @param year  The year.
     * @param month The month.
     * @return A new {@link SolarMonth} instance.
     */
    public static SolarMonth fromYm(int year, int month) {
        return new SolarMonth(year, month);
    }

    /**
     * Validates the given year and month.
     *
     * @param year  The year to validate.
     * @param month The month to validate, must be between 1 and 12.
     * @throws IllegalArgumentException if the month is out of valid range or the year is invalid.
     */
    public static void validate(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(String.format("illegal solar month: %d", month));
        }
        SolarYear.validate(year);
    }

    /**
     * Gets the solar year this month belongs to.
     *
     * @return The {@link SolarYear}.
     */
    public SolarYear getSolarYear() {
        return SolarYear.fromYear(year);
    }

    /**
     * Gets the number of days in this month (October 1582 has only 21 days).
     *
     * @return The number of days.
     */
    public int getDayCount() {
        if (1582 == year && 10 == month) {
            return 21;
        }
        int d = DAYS[getIndexInYear()];
        // Solar leap years have one extra day in February
        if (2 == month && getSolarYear().isLeap()) {
            d++;
        }
        return d;
    }

    /**
     * Gets the index of this month within the year (0-11).
     *
     * @return The index.
     */
    public int getIndexInYear() {
        return month - 1;
    }

    /**
     * Gets the solar quarter this month belongs to.
     *
     * @return The {@link SolarQuarter}.
     */
    public SolarQuarter getQuarter() {
        return SolarQuarter.fromIndex(year, getIndexInYear() / 3);
    }

    /**
     * Gets the number of weeks in this month.
     *
     * @param start The starting day of the week, 1-7 (1 for Monday, 7 for Sunday).
     * @return The number of weeks.
     */
    public int getWeekCount(int start) {
        return (int) Math
                .ceil((indexOf(SolarDay.fromYmd(year, month, 1).getWeek().getIndex() - start, 7) + getDayCount()) / 7D);
    }

    /**
     * Gets the name of this solar month.
     *
     * @return The name of this solar month.
     */
    public String getName() {
        return NAMES[getIndexInYear()];
    }

    @Override
    public String toString() {
        return getSolarYear() + getName();
    }

    /**
     * Gets the solar month after a specified number of months.
     *
     * @param n The number of months to add.
     * @return The {@link SolarMonth} after {@code n} months.
     */
    public SolarMonth next(int n) {
        int i = month - 1 + n;
        return fromYm((year * 12 + i) / 12, indexOf(i, 12) + 1);
    }

    /**
     * Gets a list of all solar weeks in this month.
     *
     * @param start The starting day of the week, 1-7 (1 for Monday, 7 for Sunday).
     * @return A list of {@link SolarWeek} objects for this month.
     */
    public List<SolarWeek> getWeeks(int start) {
        int size = getWeekCount(start);
        List<SolarWeek> l = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            l.add(SolarWeek.fromYm(year, month, i, start));
        }
        return l;
    }

    /**
     * Gets a list of all solar days in this month.
     *
     * @return A list of {@link SolarDay} objects for this month.
     */
    public List<SolarDay> getDays() {
        int size = getDayCount();
        List<SolarDay> l = new ArrayList<>(size);
        for (int i = 1; i <= size; i++) {
            l.add(SolarDay.fromYmd(year, month, i));
        }
        return l;
    }

    /**
     * Gets the first day of this month.
     *
     * @return The {@link SolarDay} representing the first day of this month.
     */
    public SolarDay getFirstDay() {
        return SolarDay.fromYmd(year, month, 1);
    }

}
