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
package org.aoju.bus.validate.strategy;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.lang.exception.NoSuchException;
import org.aoju.bus.core.utils.ClassUtils;
import org.aoju.bus.core.utils.ObjectUtils;
import org.aoju.bus.core.utils.ReflectUtils;
import org.aoju.bus.validate.Context;
import org.aoju.bus.validate.Registry;
import org.aoju.bus.validate.annotation.Reflect;
import org.aoju.bus.validate.validators.Matcher;

import java.lang.reflect.Method;

/**
 * 反射信息校验
 *
 * @author Kimi Liu
 * @version 5.6.2
 * @since JDK 1.8+
 */
public class ReflectStrategy implements Matcher<Object, Reflect> {

    @Override
    public boolean on(Object object, Reflect annotation, Context context) {
        if (ObjectUtils.isEmpty(object)) {
            return false;
        }
        Class<?> clazz = annotation.target();
        String methodName = annotation.method();
        Object result;
        try {
            Method method = clazz.getDeclaredMethod(methodName, object.getClass());
            Object bean = ClassUtils.getClass(clazz);
            result = ReflectUtils.invokeMethod(method, bean);
        } catch (NoSuchMethodException e) {
            throw new InstrumentException(e.getMessage(), e);
        }

        for (String name : annotation.validator()) {
            if (!Registry.getInstance().contains(name)) {
                throw new NoSuchException("尝试使用一个不存在的校验器：" + name);
            }
            Matcher matcher = (Matcher) Registry.getInstance().require(name);
            if (!matcher.on(result, null, context)) {
                return false;
            }
        }
        return true;
    }

}
