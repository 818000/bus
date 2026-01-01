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
 * Provides classes for the Gregorian (Solar) calendar system used in China.
 * <p>
 * The Solar calendar is the internationally accepted civil calendar based on the Earth's revolution around the Sun.
 * This package provides implementations of Solar calendar components specifically designed for use in Chinese contexts,
 * including integration with traditional Lunar calendar concepts and solar terms.
 * </p>
 * <p>
 * This package contains concrete implementations of date-time components for the Solar calendar system, including:
 * </p>
 * <ul>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarYear} - Solar year representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarMonth} - Solar month representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarDay} - Solar day with cultural information</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarWeek} - Solar week representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarQuarter} - Fiscal quarter representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarHalfYear} - Half-year representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarTime} - Precise time representation</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarTerms} - The 24 solar terms (Jie Qi)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarTermDay} - Solar term day calculations</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.solar.SolarFestival} - Solar-based holidays and observances</li>
 * </ul>
 * <p>
 * Key features include:
 * </p>
 * <ul>
 * <li>Bidirectional conversion with Lunar calendar dates</li>
 * <li>Integration with traditional Chinese solar terms (24 seasonal divisions)</li>
 * <li>Cultural periods like Dog Days (San Fu), Nine Days (Jiu Jiu), and Plum Rain (Mei Yu)</li>
 * <li>Support for both traditional and modern holidays</li>
 * <li>Week and quarter calculations for business applications</li>
 * </ul>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
package org.miaixz.bus.core.center.date.culture.solar;
