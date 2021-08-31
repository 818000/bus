/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org Greg Messner and other contributors.         *
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
package org.aoju.bus.gitlab;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class provides utility methods for parsing and formatting org.aoju.bus.gitlab.ISO8601 formatted dates.
 *
 * @author Kimi Liu
 * @version 6.2.8
 * @since JDK 1.8+
 */
public class ISO8601 {

    public static final String PATTERN = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String MSEC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String SPACEY_PATTERN = "yyyy-MM-dd HH:mm:ss Z";
    public static final String SPACEY_MSEC_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS Z";
    public static final String PATTERN_MSEC = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String OUTPUT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String OUTPUT_MSEC_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String UTC_PATTERN = "yyyy-MM-dd HH:mm:ss 'UTC'";
    public static final String DATE_ONLY_PATTERN = "yyyy-MM-dd";

    private static final DateTimeFormatter ODT_WITH_MSEC_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[['T'][ ]HH:mm:ss.SSS[ ][XXXXX][XXXX]]").toFormatter();
    private static final DateTimeFormatter ODT_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd[['T'][ ]HH:mm:ss[.SSS][ ][XXX][X]]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
            .toFormatter();

    /**
     * Get a org.aoju.bus.gitlab.ISO8601 formatted string for the current date and time.
     *
     * @return a org.aoju.bus.gitlab.ISO8601 formatted string for the current date and time
     */
    public static String getTimestamp() {
        return (SafeDateFormatter.getDateFormat(PATTERN).format(new Date()));
    }

    /**
     * Get a ISO8601formatted string for the current date and time.
     *
     * @param withMsec flag indicating whether to include milliseconds
     * @return a org.aoju.bus.gitlab.ISO8601 formatted string for the current date and time
     */
    public static String getTimestamp(boolean withMsec) {
        return (withMsec ? SafeDateFormatter.getDateFormat(PATTERN_MSEC).format(new Date()) :
                SafeDateFormatter.getDateFormat(PATTERN).format(new Date()));
    }

    /**
     * Get a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Calendar instance.
     *
     * @param cal the Calendar instance to get the org.aoju.bus.gitlab.ISO8601 formatted string for
     * @return a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Calendar instance, or null if call is null
     */
    public static String toString(Calendar cal) {

        if (cal == null) {
            return (null);
        }

        return (toString(cal.getTime()));
    }

    /**
     * Get a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Date instance.
     *
     * @param date     the Date instance to get the org.aoju.bus.gitlab.ISO8601 formatted string for
     * @param withMsec flag indicating whether to include milliseconds
     * @return a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Date instance, or null if date is null
     */
    public static String toString(Date date, boolean withMsec) {

        if (date == null) {
            return (null);
        }

        long time = date.getTime();
        return (withMsec && time % 1000 != 0 ?
                SafeDateFormatter.getDateFormat(OUTPUT_MSEC_PATTERN).format(date) :
                SafeDateFormatter.getDateFormat(OUTPUT_PATTERN).format(date));
    }

    /**
     * Get a string that includes the date only in yyyy-mm-ss format.
     *
     * @param date the Date instance to get the date only formatted string for
     * @return a string that includes the date only in yyyy-mm-ss format, or null if date is null
     */
    public static String dateOnly(Date date) {

        if (date == null) {
            return (null);
        }

        return SafeDateFormatter.getDateFormat(DATE_ONLY_PATTERN).format(date);
    }

    /**
     * Get a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Date instance.
     *
     * @param date the Date instance to get the org.aoju.bus.gitlab.ISO8601 formatted string for
     * @return a org.aoju.bus.gitlab.ISO8601 formatted string for the provided Date instance, or null if date is null
     */
    public static String toString(Date date) {
        return (toString(date, true));
    }

    /**
     * Parses an org.aoju.bus.gitlab.ISO8601 formatted string a returns an Instant instance.
     *
     * @param dateTimeString the org.aoju.bus.gitlab.ISO8601 formatted string
     * @return an Instant instance for the org.aoju.bus.gitlab.ISO8601 formatted string
     */
    public static Instant toInstant(String dateTimeString) {

        if (dateTimeString == null) {
            return (null);
        }

        dateTimeString = dateTimeString.trim();

        if (dateTimeString.endsWith("Z")) {
            return (Instant.parse(dateTimeString));
        } else {

            // Convert UTC zoned dates to 0 offset date
            if (dateTimeString.endsWith("UTC")) {
                dateTimeString = dateTimeString.replace("UTC", "+0000");
            }

            OffsetDateTime odt = (dateTimeString.length() > 25 ?
                    OffsetDateTime.parse(dateTimeString, ODT_WITH_MSEC_PARSER) :
                    OffsetDateTime.parse(dateTimeString, ODT_PARSER));

            return (odt.toInstant());
        }
    }

    /**
     * Parses an org.aoju.bus.gitlab.ISO8601 formatted string a returns a Date instance.
     *
     * @param dateTimeString the org.aoju.bus.gitlab.ISO8601 formatted string
     * @return a Date instance for the org.aoju.bus.gitlab.ISO8601 formatted string
     */
    public static Date toDate(String dateTimeString) {
        Instant instant = toInstant(dateTimeString);
        return (null != instant ? Date.from(instant) : null);
    }

    /**
     * Parses an org.aoju.bus.gitlab.ISO8601 formatted string a returns a Calendar instance.
     *
     * @param dateTimeString the org.aoju.bus.gitlab.ISO8601 formatted string
     * @return a Calendar instance for the org.aoju.bus.gitlab.ISO8601 formatted string
     */
    public static Calendar toCalendar(String dateTimeString) {

        Date date = toDate(dateTimeString);
        if (date == null) {
            return (null);
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return (cal);
    }

    // Set up ThreadLocal storage to save a thread local SimpleDateFormat keyed with the format string
    private static final class SafeDateFormatter {

        private static final ThreadLocal<Map<String, SimpleDateFormat>> safeFormats = ThreadLocal.withInitial(() -> (new ConcurrentHashMap<>()));

        private static SimpleDateFormat getDateFormat(String formatSpec) {

            Map<String, SimpleDateFormat> formatMap = safeFormats.get();
            SimpleDateFormat format = formatMap.get(formatSpec);
            if (format == null) {
                format = new SimpleDateFormat(formatSpec);
                format.setLenient(true);
                format.setTimeZone(TimeZone.getTimeZone("UTC"));
                formatMap.put(formatSpec, format);
            }

            return (format);
        }
    }

}
