/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2024 miaixz.org and other contributors.                    *
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
package org.miaixz.bus.core.lang;

import java.time.format.DateTimeFormatter;

/**
 * 日期场景属性
 * 工具类，提供格式化字符串很多，但是对于具体什么含义，不够清晰，这里进行说明：
 * 常见日期格式模式字符串：
 * <ul>
 *    <li>yyyy-MM-dd                   示例：2022-08-05</li>
 *    <li>yyyy年MM月dd日                示例：2022年08月05日</li>
 *    <li>yyyy-MM-dd HH:mm:ss          示例：2022-08-05 12:59:59</li>
 *    <li>yyyy-MM-dd HH:mm:ss.SSS      示例：2022-08-05 12:59:59.559</li>
 *    <li>yyyy-MM-dd HH:mm:ss.SSSZ     示例：2022-08-05 12:59:59.559+0800【东八区中国时区】、2022-08-05 04:59:59.559+0000【冰岛0时区】, 年月日 时分秒 毫秒 时区</li>
 *    <li>yyyy-MM-dd HH:mm:ss.SSSz     示例：2022-08-05 12:59:59.559UTC【世界标准时间=0时区】、2022-08-05T12:59:59.599GMT【冰岛0时区】、2022-08-05T12:59:59.599CST【东八区中国时区】、2022-08-23T03:45:00.599EDT【美国东北纽约时间，-0400】 ,年月日 时分秒 毫秒 时区</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z' 示例：2022-08-05T12:59:59.559Z, 其中：''单引号表示转义字符，T:分隔符，Z:一般指UTC,0时区的时间含义</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ   示例：2022-08-05T11:59:59.559+0800, 其中：Z,表示时区</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss.SSSX   示例：2022-08-05T12:59:59.559+08, 其中：X:两位时区，+08表示：东8区，中国时区</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss.SSSXX  示例：2022-08-05T12:59:59.559+0800, 其中：XX:四位时区</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss.SSSXXX 示例：2022-08-05T12:59:59.559+08:00, 其中：XX:五位时区</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss        示例：2022-08-05T12:59:59+08</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ssXXX     示例：2022-08-05T12:59:59+08:00</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ssZ       示例：2022-08-05T12:59:59+0800</li>
 *    <li>yyyy-MM-dd'T'HH:mm:ss'Z'     示例：2022-08-05T12:59:59Z</li>
 *    <li>EEE MMM dd HH:mm:ss z yyyy   示例：周五 8月 05 12:59:00 UTC+08:00 2022</li>
 *    <li>EEE MMM dd HH:mm:ss zzz yyyy 示例：周五 8月 05 12:59:00 UTC+08:00 2022,其中z表示UTC时区，但：1~3个z没有任何区别</li>
 *    <li>EEE, dd MMM yyyy HH:mm:ss z  示例：周五, 05 8月 2022 12:59:59 UTC+08:00</li>
 * </ul>
 * <p>
 * 系统提供的，请查看，有大量定义好的格式化对象，可以直接使用，如：
 * {@link DateTimeFormatter#ISO_DATE}
 * {@link DateTimeFormatter#ISO_DATE_TIME}
 * 查看更多，请参阅上述官方文档
 * </p>
 *
 * <p>
 * 特殊说明：UTC时间，世界标准时间，0时区的时间，伦敦时间，可以直接加Z表示不加空格，
 * 如：“09:30 UTC”表示为“09:30Z”或“T0930Z”，其中：Z 是 +00:00 的缩写，意思是 UTC(零时分秒的偏移量).
 * </p>
 * <ul>
 *     <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
 *     <li>2022-08-23T15:20:46UTC</li>
 *     <li>2022-08-23T15:20:46 UTC</li>
 *     <li>2022-08-23T15:20:46+0000</li>
 *     <li>2022-08-23T15:20:46 +0000</li>
 *     <li>2022-08-23T15:20:46Z</li>
 * </ul>
 * <p>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Fields {

    /**
     * 年格式：yyyy
     */
    public static final String NORM_YEAR = "yyyy";
    /**
     * 年月格式：yyyy-MM
     */
    public static final String NORM_MONTH = "yyyy-MM";

    /**
     * 简单年月格式：yyyyMM
     */
    public static final String SIMPLE_MONTH = "yyyyMM";

    /**
     * 标准日期格式：yyyy-MM-dd
     */
    public static final String NORM_DATE = "yyyy-MM-dd";

    /**
     * 格式化通配符: HH:mm
     */
    public static final String NORM_HOUR_MINUTE = "HH:mm";

    /**
     * 标准时间格式：HH:mm:ss
     */
    public static final String NORM_TIME = "HH:mm:ss";

    /**
     * 标准日期时间格式，精确到分：yyyy-MM-dd HH:mm
     */
    public static final String NORM_DATETIME_MINUTE = "yyyy-MM-dd HH:mm";

    /**
     * 标准日期时间格式，精确到秒：yyyy-MM-dd HH:mm:ss
     */
    public static final String NORM_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * 标准日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String NORM_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * ISO8601日期时间格式，精确到毫秒：yyyy-MM-dd HH:mm:ss,SSS
     */
    public static final String NORM_DATETIME_COMMA_MS = "yyyy-MM-dd HH:mm:ss,SSS";

    /**
     * 中文日期格式: M月d日
     */
    public static final String CN_MONTH = "M月d日";

    /**
     * 标准日期格式：yyyy年MM月dd日
     */
    public static final String CN_DATE = "yyyy年MM月dd日";

    /**
     * 标准日期格式：yyyy年MM月dd日HH时mm分ss秒
     */
    public static final String CN_DATE_TIME = "yyyy年MM月dd日HH时mm分ss秒";

    /**
     * 标准日期格式：yyyyMMdd
     */
    public static final String PURE_DATE = "yyyyMMdd";

    /**
     * 标准日期格式: HHmm
     */
    public static final String PURE_HOUR_MINUTE = "HHmm";
    /**
     * 标准日期格式：HHmmss
     */
    public static final String PURE_TIME = "HHmmss";

    /**
     * 标准日期格式：yyyyMMddHHmmss
     */
    public static final String PURE_DATETIME = "yyyyMMddHHmmss";

    /**
     * 标准日期格式：yyyyMMddHHmmssSSS
     */
    public static final String PURE_DATETIME_MS = "yyyyMMddHHmmssSSS";

    /**
     * 格式化通配符: yyyyMMddHHmmss.SSS
     */
    public static final String PURE_DATETIME_TIP_PATTERN = "yyyyMMddHHmmss.SSS";

    /**
     * HTTP头中日期时间格式：EEE, dd MMM yyyy HH:mm:ss z
     */
    public static final String HTTP_DATETIME = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * JDK中日期时间格式：EEE MMM dd HH:mm:ss zzz yyyy
     */
    public static final String JDK_DATETIME = "EEE MMM dd HH:mm:ss zzz yyyy";

    /**
     * ISO8601日期时间：yyyy-MM-dd'T'HH:mm:ss
     * 按照ISO8601规范，默认使用T分隔日期和时间，末尾不加Z表示当地时区
     */
    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss.SSS
     */
    public static final String ISO8601_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * UTC时间：yyyy-MM-dd'T'HH:mm:ss'Z'
     * 按照ISO8601规范，后缀加Z表示UTC时间
     */
    public static final String UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * ISO8601时间：yyyy-MM-dd'T'HH:mm:ssZ，Z表示一个时间偏移，如+0800
     */
    public static final String ISO8601_WITH_ZONE_OFFSET = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * ISO8601时间：yyyy-MM-dd'T'HH:mm:ssXXX
     */
    public static final String ISO8601_WITH_XXX_OFFSET = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /**
     * ISO8601时间：yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static final String UTC_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * ISO8601时间：yyyy-MM-dd'T'HH:mm:ss.SSSZ
     */
    public static final String ISO8601_MS_WITH_ZONE_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * ISO8601时间：yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     */
    public static final String ISO8601_MS_WITH_XXX_OFFSET = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

}
