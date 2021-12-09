/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.image.galaxy;

import org.aoju.bus.core.lang.Symbol;

import java.time.Period;
import java.util.Calendar;

/**
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public class RetentionPeriod implements Comparable<RetentionPeriod> {

    private final String value;
    private final Period period;
    private final ScheduleExpression schedule;

    public RetentionPeriod(String value, Period period, ScheduleExpression schedule) {
        this.value = value;
        this.period = period;
        this.schedule = schedule;
    }

    public static RetentionPeriod valueOf(String s) {
        String[] split1 = Property.split(s, Symbol.C_BRACKET_RIGHT);
        switch (split1.length) {
            case 1:
                return new RetentionPeriod(s, Period.parse(s), null);
            case 2:
                String[] split2 = Property.split(split1[0], Symbol.C_BRACKET_LEFT);
                if (split2.length == 2)
                    return new RetentionPeriod(s,
                            Period.parse(split1[split1.length - 1]),
                            ScheduleExpression.valueOf(split2[1]));
        }
        throw new IllegalArgumentException(s);
    }

    @Override
    public String toString() {
        return value;
    }

    public Period getPeriod() {
        return period;
    }

    public String getPrefix() {
        return value.substring(0, value.indexOf(Symbol.C_BRACKET_RIGHT) + 1);
    }

    public boolean match(Calendar cal) {
        return null == schedule || schedule.contains(cal);
    }

    @Override
    public int compareTo(RetentionPeriod o) {
        return null != schedule ? null != o.schedule ? value.compareTo(o.value) : -1 : null != o.schedule ? 1 : 0;
    }

    public enum DeleteStudies {
        OlderThan, ReceivedBefore, NotUsedSince
    }

}
