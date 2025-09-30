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
package org.miaixz.bus.core.center.date.format.parser;

import java.util.regex.Pattern;

import org.miaixz.bus.core.center.date.DateTime;

/**
 * 全局正则日期解析器，通过预定义或自定义的正则规则解析日期字符串。
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class NormalDateParser implements PredicateDateParser {

    /**
     * 默认单例实例
     */
    public static NormalDateParser INSTANCE = new NormalDateParser();

    /**
     * 正则日期解析器
     */
    private final RegexDateParser parser;

    /**
     * 构造，初始化默认的解析规则。
     */
    public NormalDateParser() {
        parser = createDefault();
    }

    /**
     * 测试是否适用此解析器。
     *
     * @param charSequence 日期字符串
     * @return 始终返回true，作为兜底解析器
     */
    @Override
    public boolean test(final CharSequence charSequence) {
        return true;
    }

    /**
     * 解析日期字符串，线程安全。
     *
     * @param source 日期字符串
     * @return 解析后的日期对象
     */
    @Override
    public DateTime parse(final CharSequence source) {
        return (DateTime) parser.parse(source);
    }

    /**
     * 创建默认的正则日期解析器。
     *
     * @return 正则日期解析器
     */
    private RegexDateParser createDefault() {
        final String yearRegex = "(?<year>\\d{2,4})";
        // 月的正则，匹配：Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec，
        // 或 January, February, March, April, May, June, July, August, September, October, November, December
        final String monthRegex = "(?<month>[jfmaasond][aepucoe][nbrylgptvc]\\w{0,6}|[一二三四五六七八九十]{1,2}月)";
        final String dayRegex = "(?<day>\\d{1,2})(?:th)?";
        // 周的正则，匹配：Mon, Tue, Wed, Thu, Fri, Sat, Sun，
        // 或 Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
        // 周一般出现在日期头部，可选
        final String weekRegexWithSuff = "((?<week>[mwfts][oeruha][ndieut](\\w{3,6})?|(星期|周)[一二三四五六日])\\W+)?";
        // hh:mm:ss.SSSSZ hh:mm:ss.SSSS hh:mm:ss hh:mm
        final String timeRegexWithPre = "(" + "(\\W+|T)(at\\s)?(?<hour>\\d{1,2})" + "\\W(?<minute>\\d{1,2})"
                + "(\\W(?<second>\\d{1,2}))?秒?" + "(?:[.,](?<nanosecond>\\d{1,9}))?(?<zero>z)?" + "(\\s?(?<m>[ap]m))?"
                + ")?";
        // 月开头，类似：May 8
        final String dateRegexMonthFirst = monthRegex + "\\W+" + dayRegex;
        // 日开头，类似：02-Jan
        final String dateRegexDayFirst = dayRegex + "\\W+" + monthRegex;
        // 时区拼接，类似：
        // GMT+0100 (GMT Daylight Time)
        // +0200 (CEST)
        // GMT+0100
        // MST
        final String zoneRegex = "\\s?(?<zone>"
                // 匹配：GMT MST等
                + "[a-z ]*"
                // 匹配：+08:00 +0800 +08
                + "(\\s?[-+]\\d{1,2}:?(?:\\d{2})?)*"
                // 匹配：(GMT Daylight Time)等
                + "(\\s?[(]?[a-z ]+[)]?)?" + ")";
        final String maskRegex = "(\\smsk m=[+-]\\d[.]\\d+)?";

        return RegexDateParser.of(
                // 【年月日时】类似：2009-Feb-08，时间部分可选，类似：5:57:50，05:57:50 +08:00
                yearRegex + "\\W" + dateRegexMonthFirst + timeRegexWithPre + zoneRegex + maskRegex,
                // 【年月日时】类似：2020-02-08或2020年02月08日，时间部分可选，类似：5:57:50，05:57:50 +08:00
                yearRegex + "\\W(?<month>\\d{1,2})(\\W(?<day>\\d{1,2}))?日?" + timeRegexWithPre + zoneRegex + maskRegex,

                // 【周月日年时】类似：May 8, 2009，时间部分可选，类似：5:57:50，05:57:50 +08:00
                weekRegexWithSuff + dateRegexMonthFirst + "\\W+" + yearRegex + timeRegexWithPre + zoneRegex + maskRegex,
                // 【周月日时年】类似：Mon Jan 2 15:05:05 MST 2020
                weekRegexWithSuff + dateRegexMonthFirst + timeRegexWithPre + zoneRegex + "\\W+" + yearRegex + maskRegex,
                // 【周日月年时】类似：Monday, 02-Jan-06 15:05:05 MST
                weekRegexWithSuff + dateRegexDayFirst + "\\W+" + yearRegex + timeRegexWithPre + zoneRegex + maskRegex,
                // 【日月年时】MM/dd/yyyy, dd/MM/yyyy, 类似：5/12/2020 03:00:50，为避免歧义，只支持4位年
                "(?<dayOrMonth>\\d{1,2}\\W\\d{1,2})\\W(?<year>\\d{4})" + timeRegexWithPre + zoneRegex + maskRegex,

                // 纯数字日期时间
                // yyyy
                // yyyyMM
                // yyyyMMdd
                // yyyyMMddhhmmss
                // unixtime(10)
                // millisecond(13)
                // microsecond(16)
                // nanosecond(19)
                "^(?<number>\\d{4,19})$");
    }

    /**
     * 设置月份优先顺序，当无法区分月和日时，决定使用mm/dd还是dd/mm。
     *
     * @param preferMonthFirst true为mm/dd，false为dd/mm
     */
    synchronized public void setPreferMonthFirst(final boolean preferMonthFirst) {
        parser.setPreferMonthFirst(preferMonthFirst);
    }

    /**
     * 注册自定义日期正则规则。
     *
     * @param regex 日期正则表达式
     */
    synchronized public void registerRegex(final String regex) {
        parser.addRegex(regex);
    }

    /**
     * 注册自定义日期正则模式。
     *
     * @param pattern 日期正则模式
     */
    synchronized public void registerPattern(final Pattern pattern) {
        parser.addPattern(pattern);
    }

}
