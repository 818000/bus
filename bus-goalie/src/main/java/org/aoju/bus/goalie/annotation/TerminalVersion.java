/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
package org.aoju.bus.goalie.annotation;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8++
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TerminalVersion {

    int[] terminals() default {};

    Version op() default Version.NIL;

    String version() default Normal.EMPTY;

    /**
     * 版本信息
     */
    enum Version {

        NIL(Normal.EMPTY),
        LT(Symbol.LT),
        GT(Symbol.GT),
        GTE(Symbol.GT + Symbol.EQUAL),
        LE(Symbol.LE),
        LTE(Symbol.LE + Symbol.EQUAL),
        GE(Symbol.GE),
        NE(Symbol.NOT + Symbol.EQUAL),
        EQ(Symbol.EQUAL + Symbol.EQUAL);


        private final String code;

        Version(String code) {
            this.code = code;
        }

        public static Version parse(String code) {
            for (Version operator : Version.values()) {
                if (operator.getCode().equalsIgnoreCase(code)) {
                    return operator;
                }
            }
            return null;
        }

        public String getCode() {
            return code;
        }

    }

}
