/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.lang.function;

import org.aoju.bus.core.exception.InternalException;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 表示对单个操作数的操作，该操作产生与其操作数相同类型的结果
 * 对于操作数和结果类型相同的情况，这是 Function 的一种特殊化
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@FunctionalInterface
public interface XUnaryOperator<T> extends UnaryOperator<T>, Serializable {

    /**
     * 返回始终返回其输入参数的一元运算符
     *
     * @param <T> 输入输出类型
     * @return 始终返回其输入参数的一元运算符
     */
    static <T> XUnaryOperator<T> identity() {
        return t -> t;
    }

    /**
     * 执行函数操作
     *
     * @param function 源函数
     * @param <T>      参数类型
     * @param <R>      结果类型
     * @param <F>      函数类型
     * @return 函数结果
     */
    static <T, R, F extends Function<T, R>> XUnaryOperator<T> casting(final F function) {
        return t -> (T) function.apply(t);
    }

    /**
     * 将此函数应用于给定的参数
     *
     * @param t 函数参数
     * @return 函数结果
     * @throws Exception 包装的检查异常
     */
    T applying(T t) throws Exception;

    /**
     * 将此函数应用于给定的参数
     *
     * @param t 函数参数
     * @return 函数结果
     */
    @Override
    default T apply(final T t) {
        try {
            return applying(t);
        } catch (final Exception e) {
            throw new InternalException(e);
        }
    }

}
