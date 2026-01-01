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

import java.util.ArrayList;
import java.util.List;

import org.miaixz.bus.core.center.date.culture.*;
import org.miaixz.bus.core.center.date.culture.fetus.FetusDay;
import org.miaixz.bus.core.center.date.culture.parts.DayPart;
import org.miaixz.bus.core.center.date.culture.ren.MinorRen;
import org.miaixz.bus.core.center.date.culture.sixty.*;
import org.miaixz.bus.core.center.date.culture.star.nine.NineStar;
import org.miaixz.bus.core.center.date.culture.star.six.SixStar;
import org.miaixz.bus.core.center.date.culture.star.twelve.TwelveStar;
import org.miaixz.bus.core.center.date.culture.star.twentyeight.TwentyEightStar;
import org.miaixz.bus.core.center.date.culture.solar.SolarDay;
import org.miaixz.bus.core.center.date.culture.solar.SolarTerms;

/**
 * Represents a day in the Lunar calendar.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class LunarDay extends DayPart {

    /**
     * Names of the days in a lunar month.
     */
    public static final String[] NAMES = { "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十", "十一", "十二", "十三",
            "十四", "十五", "十六", "十七", "十八", "十九", "二十", "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十" };

    /**
     * Constructs a {@code LunarDay} from a year, month, and day.
     *
     * @param year  The lunar year.
     * @param month The lunar month (a negative value indicates a leap month).
     * @param day   The lunar day.
     */
    public LunarDay(int year, int month, int day) {
        validate(year, month, day);
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Creates a {@code LunarDay} from a year, month, and day.
     *
     * @param year  The lunar year.
     * @param month The lunar month (a negative value indicates a leap month).
     * @param day   The lunar day.
     * @return a new {@code LunarDay} instance.
     */
    public static LunarDay fromYmd(int year, int month, int day) {
        return new LunarDay(year, month, day);
    }

    /**
     * Validates lunar year, month, and day values.
     *
     * @param year  The lunar year.
     * @param month The lunar month (a negative value indicates a leap month).
     * @param day   The lunar day.
     * @throws IllegalArgumentException if the day is invalid or exceeds the number of days in the month.
     */
    public static void validate(int year, int month, int day) {
        if (day < 1) {
            throw new IllegalArgumentException(String.format("illegal lunar day %d", day));
        }
        LunarMonth m = LunarMonth.fromYm(year, month);
        if (day > m.getDayCount()) {
            throw new IllegalArgumentException(String.format("illegal day %d in %s", day, m));
        }
    }

    /**
     * Gets the lunar month object.
     *
     * @return The {@link LunarMonth}.
     */
    public LunarMonth getLunarMonth() {
        return LunarMonth.fromYm(year, month);
    }

    /**
     * Gets the name of the day (e.g., "初一").
     *
     * @return The name of the day.
     */
    public String getName() {
        return NAMES[day - 1];
    }

    @Override
    public String toString() {
        return getLunarMonth() + getName();
    }

    @Override
    public LunarDay next(int n) {
        return getSolarDay().next(n).getLunarDay();
    }

    /**
     * Checks if this lunar day is before another.
     *
     * @param target The other lunar day.
     * @return {@code true} if this day is before the target day.
     */
    public boolean isBefore(LunarDay target) {
        if (year != target.getYear()) {
            return year < target.getYear();
        }
        if (month != target.getMonth()) {
            return Math.abs(month) < Math.abs(target.getMonth());
        }
        return day < target.getDay();
    }

    /**
     * Checks if this lunar day is after another.
     *
     * @param target The other lunar day.
     * @return {@code true} if this day is after the target day.
     */
    public boolean isAfter(LunarDay target) {
        if (year != target.getYear()) {
            return year > target.getYear();
        }
        if (month != target.getMonth()) {
            return Math.abs(month) >= Math.abs(target.getMonth());
        }
        return day > target.getDay();
    }

    /**
     * Gets the day of the week.
     *
     * @return The {@link Week}.
     */
    public Week getWeek() {
        return getSolarDay().getWeek();
    }

    /**
     * Gets the Sixty Cycle (Ganzhi) of this day.
     *
     * @return The {@link SixtyCycle}.
     */
    public SixtyCycle getSixtyCycle() {
        int offset = (int) getLunarMonth().getFirstJulianDay().next(day - 12).getDay();
        return SixtyCycle.fromName(HeavenStem.fromIndex(offset).getName() + EarthBranch.fromIndex(offset).getName());
    }

    /**
     * Gets the Duty (Jian Chu) of this day.
     *
     * @return The {@link Duty}.
     */
    public Duty getDuty() {
        return getSixtyCycleDay().getDuty();
    }

    /**
     * Gets the Twelve Star (Huang Dao Hei Dao) of this day.
     *
     * @return The {@link TwelveStar}.
     */
    public TwelveStar getTwelveStar() {
        return getSixtyCycleDay().getTwelveStar();
    }

    /**
     * Gets the Nine Star (Jiu Xing) of this day.
     *
     * @return The {@link NineStar}.
     */
    public NineStar getNineStar() {
        SolarDay d = getSolarDay();
        SolarTerms dongZhi = SolarTerms.fromIndex(d.getYear(), 0);
        SolarDay dongZhiSolar = dongZhi.getSolarDay();
        SolarDay xiaZhiSolar = dongZhi.next(12).getSolarDay();
        SolarDay dongZhiSolar2 = dongZhi.next(24).getSolarDay();
        int dongZhiIndex = dongZhiSolar.getLunarDay().getSixtyCycle().getIndex();
        int xiaZhiIndex = xiaZhiSolar.getLunarDay().getSixtyCycle().getIndex();
        int dongZhiIndex2 = dongZhiSolar2.getLunarDay().getSixtyCycle().getIndex();
        SolarDay solarShunBai = dongZhiSolar.next(dongZhiIndex > 29 ? 60 - dongZhiIndex : -dongZhiIndex);
        SolarDay solarShunBai2 = dongZhiSolar2.next(dongZhiIndex2 > 29 ? 60 - dongZhiIndex2 : -dongZhiIndex2);
        SolarDay solarNiZi = xiaZhiSolar.next(xiaZhiIndex > 29 ? 60 - xiaZhiIndex : -xiaZhiIndex);
        int offset = 0;
        if (!d.isBefore(solarShunBai) && d.isBefore(solarNiZi)) {
            offset = d.subtract(solarShunBai);
        } else if (!d.isBefore(solarNiZi) && d.isBefore(solarShunBai2)) {
            offset = 8 - d.subtract(solarNiZi);
        } else if (!d.isBefore(solarShunBai2)) {
            offset = d.subtract(solarShunBai2);
        } else if (d.isBefore(solarShunBai)) {
            offset = 8 + solarShunBai.subtract(d);
        }
        return NineStar.fromIndex(offset);
    }

    /**
     * Gets the direction of Jupiter (Tai Sui) for this day.
     *
     * @return The {@link Direction}.
     */
    public Direction getJupiterDirection() {
        int index = getSixtyCycle().getIndex();
        return index % 12 < 6 ? Element.fromIndex(index / 12).getDirection()
                : LunarYear.fromYear(year).getJupiterDirection();
    }

    /**
     * Gets the Daily Fetus Spirit (Tai Shen).
     *
     * @return The {@link FetusDay}.
     */
    public FetusDay getFetusDay() {
        return FetusDay.fromLunarDay(this);
    }

    /**
     * Gets the day index within the lunar phase.
     *
     * @return The day index within the lunar phase.
     */
    public Phase.PhaseDay getPhaseDay() {
        SolarDay today = getSolarDay();
        LunarMonth m = getLunarMonth().next(1);
        Phase p = Phase.fromIndex(m.getYear(), m.getMonthWithLeap(), 0);
        SolarDay d = p.getSolarDay();
        while (d.isAfter(today)) {
            p = p.next(-1);
            d = p.getSolarDay();
        }
        return new Phase.PhaseDay(p, today.subtract(d));
    }

    /**
     * Gets the lunar phase (Moon Phase).
     *
     * @return The {@link Phase}.
     */
    public Phase getPhase() {
        return getPhaseDay().getPhase();
    }

    /**
     * Gets the Six Star (Liu Yao).
     *
     * @return The {@link SixStar}.
     */
    public SixStar getSixStar() {
        return SixStar.fromIndex((Math.abs(month) + day - 2) % 6);
    }

    /**
     * Gets the corresponding Gregorian (Solar) day.
     *
     * @return The {@link SolarDay}.
     */
    public SolarDay getSolarDay() {
        return getLunarMonth().getFirstJulianDay().next(day - 1).getSolarDay();
    }

    /**
     * Gets the corresponding Sixty Cycle (Ganzhi) day object.
     *
     * @return The {@link SixtyCycleDay}.
     */
    public SixtyCycleDay getSixtyCycleDay() {
        return getSolarDay().getSixtyCycleDay();
    }

    /**
     * Gets the Twenty-Eight Mansions (Xiu) for this day.
     *
     * @return The {@link TwentyEightStar}.
     */
    public TwentyEightStar getTwentyEightStar() {
        return TwentyEightStar.fromIndex(new int[] { 10, 18, 26, 6, 14, 22, 2 }[getSolarDay().getWeek().getIndex()])
                .next(-7 * getSixtyCycle().getEarthBranch().getIndex());
    }

    /**
     * Gets the traditional lunar festival for this day, if any.
     *
     * @return The {@link LunarFestival}, or null if this day is not a festival.
     */
    public LunarFestival getFestival() {
        return LunarFestival.fromYmd(year, month, day);
    }

    /**
     * Gets the list of {@code LunarHour} objects for this day.
     *
     * @return A list of {@link LunarHour} objects.
     */
    public List<LunarHour> getHours() {
        List<LunarHour> l = new ArrayList<>();
        l.add(LunarHour.fromYmdHms(year, month, day, 0, 0, 0));
        for (int i = 0; i < 24; i += 2) {
            l.add(LunarHour.fromYmdHms(year, month, day, i + 1, 0, 0));
        }
        return l;
    }

    /**
     * Gets the list of Gods (Shen Sha) for this day.
     *
     * @return A list of {@link God}s.
     */
    public List<God> getGods() {
        return getSixtyCycleDay().getGods();
    }

    /**
     * Gets the list of recommended activities for this day.
     *
     * @return A list of recommended {@link Taboo}s.
     */
    public List<Taboo> getRecommends() {
        return getSixtyCycleDay().getRecommends();
    }

    /**
     * Gets the list of avoided activities for this day.
     *
     * @return A list of avoided {@link Taboo}s.
     */
    public List<Taboo> getAvoids() {
        return getSixtyCycleDay().getAvoids();
    }

    /**
     * Gets the Minor Liu Ren for this day.
     *
     * @return The {@link MinorRen}.
     */
    public MinorRen getMinorRen() {
        return getLunarMonth().getMinorRen().next(day - 1);
    }

    /**
     * Gets the Three Pillars (Year, Month, Day).
     *
     * @return The {@link ThreePillars}.
     */
    public ThreePillars getThreePillars() {
        return getSixtyCycleDay().getThreePillars();
    }

}
