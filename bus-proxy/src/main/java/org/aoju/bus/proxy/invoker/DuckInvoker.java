/*
 * The MIT License
 *
 * Copyright (c) 2017 aoju.org All rights reserved.
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
package org.aoju.bus.proxy.invoker;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.proxy.Invoker;
import org.aoju.bus.proxy.Provider;

import java.lang.reflect.Method;

/**
 * 支持的调用程序，这意味着它在从目标提供程序返回的对象上找到匹配的方法并调用它。
 * 该类有助于将现有类调整为它没有实现的接口
 *
 * @author Kimi Liu
 * @version 5.5.0
 * @since JDK 1.8+
 */
public class DuckInvoker implements Invoker {

    private final Provider targetProvider;

    public DuckInvoker(final Provider targetProvider) {
        this.targetProvider = targetProvider;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) throws Throwable {
        final Object target = targetProvider.getObject();
        final Class targetClass = target.getClass();
        try {
            final Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            if (method.getReturnType().isAssignableFrom(targetMethod.getReturnType())) {
                return targetMethod.invoke(target, arguments);
            }
            throw new UnsupportedOperationException(
                    "Target type " + targetClass.getName() + " method has incompatible return type.");
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException(
                    "Target type " + targetClass.getName() + " does not have a method matching " + method + Symbol.DOT);
        }
    }

}
