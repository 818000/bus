/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.aix.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility to query up time.
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Uptime {

    private static final long MINUTE_MS = 60L * 1000L;
    private static final long HOUR_MS = 60L * MINUTE_MS;
    private static final long DAY_MS = 24L * HOUR_MS;

    // sample format:
    // 18:36pm up 10 days 8:11, 2 users, load average: 3.14, 2.74, 2.41
    private static final Pattern UPTIME_FORMAT_AIX = Pattern
            .compile(".*\\sup\\s+((\\d+)\\s+days?,?\\s+)?\\b((\\d+):)?(\\d+)(\\s+min(utes?)?)?,\\s+\\d+\\s+user.+");

    private Uptime() {
    }

    /**
     * Query {@code uptime} to get up time
     *
     * @return Up time in milliseconds
     */
    public static long queryUpTime() {
        long uptime = 0L;
        String s = Executor.getFirstAnswer("/usr/bin/uptime");
        Matcher m = UPTIME_FORMAT_AIX.matcher(s);
        if (m.matches()) {
            if (m.group(2) != null) {
                uptime += Builder.parseLongOrDefault(m.group(2), 0L) * DAY_MS;
            }
            if (m.group(4) != null) {
                uptime += Builder.parseLongOrDefault(m.group(4), 0L) * HOUR_MS;
            }
            uptime += Builder.parseLongOrDefault(m.group(5), 0L) * MINUTE_MS;
        }
        return uptime;
    }

}
