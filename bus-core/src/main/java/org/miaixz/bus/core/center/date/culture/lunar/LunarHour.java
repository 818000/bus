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
package org.miaixz.bus.core.center.date.culture.lunar;

import java.util.List;

import org.miaixz.bus.core.center.date.culture.Taboo;
import org.miaixz.bus.core.center.date.culture.eightchar.EightChar;
import org.miaixz.bus.core.center.date.culture.eightchar.provider.EightCharProvider;
import org.miaixz.bus.core.center.date.culture.eightchar.provider.impl.DefaultEightCharProvider;
import org.miaixz.bus.core.center.date.culture.parts.SecondPart;
import org.miaixz.bus.core.center.date.culture.ren.MinorRen;
import org.miaixz.bus.core.center.date.culture.sixty.EarthBranch;
import org.miaixz.bus.core.center.date.culture.sixty.HeavenStem;
import org.miaixz.bus.core.center.date.culture.sixty.SixtyCycle;
import org.miaixz.bus.core.center.date.culture.sixty.SixtyCycleHour;
import org.miaixz.bus.core.center.date.culture.star.nine.NineStar;
import org.miaixz.bus.core.center.date.culture.star.twelve.TwelveStar;
import org.miaixz.bus.core.center.date.culture.solar.SolarDay;
import org.miaixz.bus.core.center.date.culture.solar.SolarTerms;
import org.miaixz.bus.core.center.date.culture.solar.SolarTime;

/**
 * Represents a specific two-hour block (shichen) in the Lunar calendar.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class LunarHour extends SecondPart {

    /**
     * The provider for Eight Characters (Bazi) calculations.
     */
    public static EightCharProvider provider = new DefaultEightCharProvider();

    /**
     * Constructs a {@code LunarHour} from its components.
     *
     * @param year   The lunar year.
     * @param month  The lunar month (a negative value indicates a leap month).
     * @param day    The lunar day.
     * @param hour   The hour (0-23).
     * @param minute The minute (0-59).
     * @param second The second (0-59).
     */
    public LunarHour(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    /**
     * Creates a {@code LunarHour} from its components.
     *
     * @param year   The lunar year.
     * @param month  The lunar month (a negative value indicates a leap month).
     * @param day    The lunar day.
     * @param hour   The hour (0-23).
     * @param minute The minute (0-59).
     * @param second The second (0-59).
     * @return a new {@code LunarHour} instance.
     */
    public static LunarHour fromYmdHms(int year, int month, int day, int hour, int minute, int second) {
        return new LunarHour(year, month, day, hour, minute, second);
    }

    /**
     * Validates lunar date and time values.
     *
     * @param year   The lunar year.
     * @param month  The lunar month (a negative value indicates a leap month).
     * @param day    The lunar day.
     * @param hour   The hour (0-23).
     * @param minute The minute (0-59).
     * @param second The second (0-59).
     * @throws IllegalArgumentException if any parameter is out of valid range.
     */
    public static void validate(int year, int month, int day, int hour, int minute, int second) {
        SecondPart.validate(hour, minute, second);
        LunarDay.validate(year, month, day);
    }

    /**
     * Gets the lunar day object.
     *
     * @return The {@link LunarDay}.
     */
    public LunarDay getLunarDay() {
        return LunarDay.fromYmd(year, month, day);
    }

    /**
     * Gets the name of the two-hour block (e.g., "子时").
     *
     * @return The name of the hour.
     */
    public String getName() {
        return EarthBranch.fromIndex(getIndexInDay()).getName() + "时";
    }

    @Override
    public String toString() {
        return getLunarDay() + getSixtyCycle().getName() + "时";
    }

    /**
     * Gets the index of this hour within the day (0-11 for the 12 two-hour blocks). For example, 23:00-00:59 is index 0
     * (子时).
     *
     * @return The index of the hour block.
     */
    public int getIndexInDay() {
        return (hour + 1) / 2;
    }

    @Override
    public LunarHour next(int n) {
        if (n == 0) {
            return fromYmdHms(year, month, day, hour, minute, second);
        }
        int h = hour + n * 2;
        int diff = h < 0 ? -1 : 1;
        int hour = Math.abs(h);
        int days = hour / 24 * diff;
        hour = (hour % 24) * diff;
        if (hour < 0) {
            hour += 24;
            days--;
        }
        LunarDay d = getLunarDay().next(days);
        return fromYmdHms(d.getYear(), d.getMonth(), d.getDay(), hour, minute, second);
    }

    /**
     * Checks if this lunar hour is before another.
     *
     * @param target The other lunar hour.
     * @return {@code true} if this hour is before the target.
     */
    public boolean isBefore(LunarHour target) {
        LunarDay d = getLunarDay();
        if (!d.equals(target.getLunarDay())) {
            return d.isBefore(target.getLunarDay());
        }
        if (hour != target.getHour()) {
            return hour < target.getHour();
        }
        return minute != target.getMinute() ? minute < target.getMinute() : second < target.getSecond();
    }

    /**
     * Checks if this lunar hour is after another.
     *
     * @param target The other lunar hour.
     * @return {@code true} if this hour is after the target.
     */
    public boolean isAfter(LunarHour target) {
        LunarDay d = getLunarDay();
        if (!d.equals(target.getLunarDay())) {
            return d.isAfter(target.getLunarDay());
        }
        if (hour != target.getHour()) {
            return hour > target.getHour();
        }
        return minute != target.getMinute() ? minute > target.getMinute() : second > target.getSecond();
    }

    /**
     * Gets the Sixty Cycle (Ganzhi) of this hour.
     *
     * @return The {@link SixtyCycle}.
     */
    public SixtyCycle getSixtyCycle() {
        int earthBranchIndex = getIndexInDay() % 12;
        SixtyCycle d = getLunarDay().getSixtyCycle();
        if (hour >= 23) {
            d = d.next(1);
        }
        return SixtyCycle.fromName(
                HeavenStem.fromIndex(d.getHeavenStem().getIndex() % 5 * 2 + earthBranchIndex).getName()
                        + EarthBranch.fromIndex(earthBranchIndex).getName());
    }

    /**
     * Gets the Twelve Star (Huang Dao Hei Dao) for this hour.
     *
     * @return The {@link TwelveStar}.
     */
    public TwelveStar getTwelveStar() {
        return TwelveStar.fromIndex(
                getSixtyCycle().getEarthBranch().getIndex()
                        + (8 - getSixtyCycleHour().getDay().getEarthBranch().getIndex() % 6) * 2);
    }

    /**
     * Gets the Nine Star (Jiu Xing) for this hour.
     *
     * @return The {@link NineStar}.
     */
    public NineStar getNineStar() {
        LunarDay d = getLunarDay();
        SolarDay solar = d.getSolarDay();
        SolarTerms dongZhi = SolarTerms.fromIndex(solar.getYear(), 0);
        int earthBranchIndex = getIndexInDay() % 12;
        int index = new int[] { 8, 5, 2 }[d.getSixtyCycle().getEarthBranch().getIndex() % 3];
        if (!solar.isBefore(dongZhi.getJulianDay().getSolarDay())
                && solar.isBefore(dongZhi.next(12).getJulianDay().getSolarDay())) {
            index = 8 + earthBranchIndex - index;
        } else {
            index -= earthBranchIndex;
        }
        return NineStar.fromIndex(index);
    }

    /**
     * Gets the corresponding Gregorian (Solar) time.
     *
     * @return The {@link SolarTime}.
     */
    public SolarTime getSolarTime() {
        SolarDay d = getLunarDay().getSolarDay();
        return SolarTime.fromYmdHms(d.getYear(), d.getMonth(), d.getDay(), hour, minute, second);
    }

    /**
     * Gets the Eight Characters (Bazi) for this specific time.
     *
     * @return The {@link EightChar}.
     */
    public EightChar getEightChar() {
        return provider.getEightChar(this);
    }

    /**
     * Gets the corresponding Sixty Cycle (Ganzhi) hour object.
     *
     * @return The {@link SixtyCycleHour}.
     */
    public SixtyCycleHour getSixtyCycleHour() {
        return getSolarTime().getSixtyCycleHour();
    }

    /**
     * Gets the list of recommended activities for this hour.
     *
     * @return A list of recommended {@link Taboo}s.
     */
    public List<Taboo> getRecommends() {
        return Taboo.getHourRecommends(getSixtyCycleHour().getDay(), getSixtyCycle());
    }

    /**
     * Gets the list of avoided activities for this hour.
     *
     * @return A list of avoided {@link Taboo}s.
     */
    public List<Taboo> getAvoids() {
        return Taboo.getHourAvoids(getSixtyCycleHour().getDay(), getSixtyCycle());
    }

    /**
     * Gets the Minor Liu Ren for this hour.
     *
     * @return The {@link MinorRen}.
     */
    public MinorRen getMinorRen() {
        return getLunarDay().getMinorRen().next(getIndexInDay());
    }

}
