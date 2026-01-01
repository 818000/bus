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
/**
 * Provides classes for the Chinese Lunar calendar system.
 * <p>
 * The Lunar calendar (also known as the traditional Chinese calendar) is a lunisolar calendar that incorporates
 * elements of both lunar and solar calendars. It has been used for centuries in China and other East Asian countries to
 * determine festival dates, auspicious days, and agricultural timing.
 * </p>
 * <p>
 * This package contains concrete implementations of date-time components for the Lunar calendar system, including:
 * </p>
 * <ul>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarYear} - Lunar year representation with Sixty Cycle
 * (GanZhi), Nine Star, and Jupiter Direction</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarMonth} - Lunar month representation with leap month
 * support</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarDay} - Lunar day representation with comprehensive
 * cultural information</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarWeek} - Lunar week representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarHour} - Two-hour time blocks (Shichen) with Eight
 * Characters (Bazi)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarFestival} - Traditional Lunar festivals</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.lunar.LunarSeason} - Lunar seasons</li>
 * </ul>
 * <ul>
 * <li>Sixty Cycle (GanZhi) - Heavenly Stems and Earthly Branches combinations</li>
 * <li>Nine Star (Jiu Xing) - Nine stars associated with time periods</li>
 * <li>Jupiter Direction (Tai Sui) - The position of Jupiter in Chinese astrology</li>
 * <li>Solar Terms (Jie Qi) - 24 seasonal periods</li>
 * <li>Traditional Festivals - Spring Festival, Mid-Autumn Festival, etc.</li>
 * <li>Auspicious Days - Days suitable for specific activities</li>
 * <li>Eight Characters (Ba Zi) - Four pillars of destiny for birth charts</li>
 * </ul>
 * <p>
 * The Lunar calendar implementation follows the national standard "Compilation and Promulgation of the Lunar Calendar"
 * (GB/T 33661-2017).
 * </p>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
package org.miaixz.bus.core.center.date.culture.lunar;
