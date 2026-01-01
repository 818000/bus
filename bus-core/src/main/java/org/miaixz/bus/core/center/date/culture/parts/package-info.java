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
 * Provides abstract base classes for hierarchical date-time components in the Chinese calendar system.
 * <p>
 * This package contains a hierarchy of abstract classes that represent different granularities of date-time components,
 * from year down to second. Each level extends the previous one, creating an inheritance chain that allows for
 * progressive refinement of temporal precision in Chinese calendar implementations.
 * </p>
 * <p>
 * The component hierarchy is as follows:
 * </p>
 * <ul>
 * <li>{@link org.miaixz.bus.core.center.date.culture.parts.YearPart} - Year-level components (root of the
 * hierarchy)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.parts.MonthPart} - Month-level components (extends YearPart)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.parts.DayPart} - Day-level components (extends MonthPart)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.parts.WeekPart} - Week-level components (extends MonthPart)</li>
 * <li>{@link org.miaixz.bus.core.center.date.culture.parts.SecondPart} - Second-level components (extends DayPart)</li>
 * </ul>
 * <p>
 * These abstract base classes are extended by concrete implementations in both the
 * {@link org.miaixz.bus.core.center.date.culture.lunar} (Lunar calendar) and
 * {@link org.miaixz.bus.core.center.date.culture.solar} (Solar calendar) packages to provide specific calendar
 * functionality.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
package org.miaixz.bus.core.center.date.culture.parts;
