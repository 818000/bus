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

import java.io.Serializable;

/**
 * 函数对象
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象
 * 在JDK8之前，Java的函数并不能作为参数传递，也不能作为返回值存在
 * 此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @author Kimi Liu
 * @version 6.3.5
 * @since Java 17+
 */
@FunctionalInterface
public interface VoidFunc0 extends Serializable {

    /**
     * 执行函数
     *
     * @throws Exception 自定义异常
     */
    void call() throws Exception;

    /**
     * 执行函数，异常包装为RuntimeException
     */
    default void callWithRuntimeException() {
        try {
            call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
