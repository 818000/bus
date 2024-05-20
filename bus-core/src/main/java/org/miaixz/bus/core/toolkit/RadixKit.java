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
package org.miaixz.bus.core.toolkit;

/**
 * 进制转换工具类，可以转换为任意进制
 * 把一个十进制整数根据自己定义的进制规则进行转换
 * 主要应用一下情况：
 * <ul>
 *     <li>根据ID生成邀请码,并且尽可能的缩短。并且不希望直接猜测出和ID的关联</li>
 *     <li>短连接的生成，根据ID转成短连接，同样不希望被猜测到</li>
 *     <li>数字加密，通过两次不同进制的转换，让有规律的数字看起来没有任何规律</li>
 *     <li>....</li>
 * </ul>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class RadixKit {

    /**
     * 34进制字符串，不包含 IO 字符
     * 对于需要补齐的，自己可以随机填充IO字符
     * 26个字母：abcdefghijklmnopqrstuvwxyz
     */
    public static final String RADIXS_34 = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    /**
     * 打乱后的34进制
     */
    public static final String RADIXS_SHUFFLE_34 = "H3UM16TDFPSBZJ90CW28QYRE45AXKNGV7L";

    /**
     * 59进制字符串,不包含 IOl 字符
     */
    public static final String RADIXS_59 = "0123456789abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ";
    /**
     * 打乱后的59进制
     */
    public static final String RADIXS_SHUFFLE_59 = "vh9wGkfK8YmqbsoENP3764SeCX0dVzrgy1HRtpnTaLjJW2xQiZAcBMUFDu5";

    /**
     * 把一个整型数值转换成自己定义的进制
     * 长度即进制
     * <ul>
     *   <li>encode("AB",10)  51转换成2进制，A=0;B=1 。 二进制1010，结果 BABA</li>
     *   <li>encode("VIP",21)  21转换成3进制，V=0;I=1;P=2 ，三进制210 ,得到结果PIV </li>
     * </ul>
     *
     * @param radixs 自定进制,不要重复，否则转不回来的。
     * @param num    要转换的数值
     * @return 自定义进制字符串
     */
    public static String encode(final String radixs, final int num) {
        //考虑到负数问题
        final long tmpNum = (num >= 0 ? num : (0x100000000L - (~num + 1)));
        return encode(radixs, tmpNum, 32);
    }

    /**
     * 把一个长整型数值转换成自己定义的进制
     *
     * @param radixs 自定进制,不要重复，否则转不回来的。
     * @param num    要转换的数值
     * @return 自定义进制字符串
     */
    public static String encode(final String radixs, final long num) {
        if (num < 0) {
            throw new RuntimeException("暂不支持负数！");
        }

        return encode(radixs, num, 64);
    }

    /**
     * 把转换后的进制字符还原成int 值
     *
     * @param radixs 自定进制,需要和encode的保持一致
     * @param encode 需要转换成十进制的字符串
     * @return int
     */
    public static int decodeToInt(final String radixs, final String encode) {
        // 还原负数
        return (int) decode(radixs, encode);
    }

    /**
     * 把转换后进制的字符还原成long 值
     *
     * @param radixs 自定进制,需要和encode的保持一致
     * @param encode 需要转换成十进制的字符串
     * @return long
     */
    public static long decode(final String radixs, final String encode) {
        // 目标是多少进制
        final int rl = radixs.length();
        long res = 0L;

        for (final char c : encode.toCharArray()) {
            res = res * rl + radixs.indexOf(c);
        }
        return res;
    }

    private static String encode(final String radixs, final long num, final int maxLength) {
        if (radixs.length() < 2) {
            throw new RuntimeException("自定义进制最少两个字符哦！");
        }
        // 目标是多少进制
        final int rl = radixs.length();
        // 考虑到负数问题
        long tmpNum = num;
        // 进制的结果，二进制最小进制转换结果是32个字符
        // StringBuilder 比较耗时
        final char[] aa = new char[maxLength];
        // 因为反需字符串比较耗时
        int i = aa.length;
        do {
            aa[--i] = radixs.charAt((int) (tmpNum % rl));
            tmpNum /= rl;
        } while (tmpNum > 0);
        // 去掉前面的字符串，trim比较耗时
        return new String(aa, i, aa.length - i);
    }

}
