/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.core.convert;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.utils.ObjectUtils;
import org.aoju.bus.core.utils.StringUtils;

/**
 * 将浮点数类型的number转换成英语的表达方式
 *
 * @author Kimi Liu
 * @version 5.6.0
 * @since JDK 1.8+
 */
public class NumberWord {

    /**
     * 将阿拉伯数字转为英文表达式
     *
     * @param x 阿拉伯数字,可以为{@link Number}对象,也可以是普通对象,最后会使用字符串方式处理
     * @return 英文表达式
     */
    public static String format(Object x) {
        if (x != null) {
            return format(x.toString());
        } else {
            return "";
        }
    }

    /**
     * 将阿拉伯数字转为英文表达式
     *
     * @param x 阿拉伯数字字符串
     * @return 英文表达式
     */
    private static String format(String x) {
        int z = x.indexOf(Symbol.DOT); // 取小数点位置
        String lstr, rstr = Normal.EMPTY;
        if (z > -1) { // 看是否有小数,如果有,则分别取左边和右边
            lstr = x.substring(0, z);
            rstr = x.substring(z + 1);
        } else {
            // 否则就是全部
            lstr = x;
        }

        String lstrrev = StringUtils.reverse(lstr); // 对左边的字串取反
        String[] a = new String[5]; // 定义5个字串变量来存放解析出来的叁位一组的字串

        switch (lstrrev.length() % 3) {
            case 1:
                lstrrev += Symbol.ZERO + Symbol.ZERO;
                break;
            case 2:
                lstrrev += Symbol.ZERO;
                break;
        }
        String lm = ""; // 用来存放转换後的整数部分
        for (int i = 0; i < lstrrev.length() / 3; i++) {
            a[i] = StringUtils.reverse(lstrrev.substring(3 * i, 3 * i + 3)); // 截取第一个叁位
            if (!ObjectUtils.equal(a[i], "000")) { // 用来避免这种情况：1000000 = first million
                // thousand only
                if (i != 0) {
                    lm = transThree(a[i]) + Symbol.SPACE + parseMore(String.valueOf(i)) + Symbol.SPACE + lm; // 加:
                    // thousand、million、billion
                } else {
                    lm = transThree(a[i]); // 防止i=0时, 在多加两个空格.
                }
            } else {
                lm += transThree(a[i]);
            }
        }

        String xs = ""; // 用来存放转换後小数部分
        if (z > -1) {
            xs = "AND CENTS " + transTwo(rstr) + Symbol.SPACE; // 小数部分存在时转换小数
        }

        return lm.trim() + Symbol.SPACE + xs + "ONLY";
    }

    private static String parseFirst(String s) {
        return Normal.EN_NUMBER[Integer.parseInt(s.substring(s.length() - 1))];
    }

    private static String parseTeen(String s) {
        return Normal.NUMBER_TEEN[Integer.parseInt(s) - 10];
    }

    private static String parseTen(String s) {
        return Normal.NUMBER_TEN[Integer.parseInt(s.substring(0, 1)) - 1];
    }

    private static String parseMore(String s) {
        return Normal.NUMBER_MORE[Integer.parseInt(s)];
    }

    // 两位
    private static String transTwo(String s) {
        String value;
        // 判断位数
        if (s.length() > 2) {
            s = s.substring(0, 2);
        } else if (s.length() < 2) {
            s = Symbol.ZERO + s;
        }

        if (s.startsWith(Symbol.ZERO)) {// 07 - seven 是否小於10
            value = parseFirst(s);
        } else if (s.startsWith(Symbol.ONE)) {// 17 seventeen 是否在10和20之间
            value = parseTeen(s);
        } else if (s.endsWith(Symbol.ZERO)) {// 是否在10与100之间的能被10整除的数
            value = parseTen(s);
        } else {
            value = parseTen(s) + Symbol.SPACE + parseFirst(s);
        }
        return value;
    }

    // 制作叁位的数
    // s.length = 3
    private static String transThree(String s) {
        String value;
        if (s.startsWith(Symbol.ZERO)) {// 是否小於100
            value = transTwo(s.substring(1));
        } else if (ObjectUtils.equal(s.substring(1), "00")) {// 是否被100整除
            value = parseFirst(s.substring(0, 1)) + " HUNDRED";
        } else {
            value = parseFirst(s.substring(0, 1)) + " HUNDRED AND " + transTwo(s.substring(1));
        }
        return value;
    }

}
