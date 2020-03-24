/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
 ********************************************************************************/
package org.aoju.bus.validate.strategy;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.utils.ObjectUtils;
import org.aoju.bus.core.utils.ReflectUtils;
import org.aoju.bus.validate.Context;
import org.aoju.bus.validate.annotation.InEnum;
import org.aoju.bus.validate.validators.Matcher;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * int enum 校验
 *
 * @author Kimi Liu
 * @version 5.8.0
 * @since JDK 1.8+
 */
public class InEnumStrategy implements Matcher<Object, InEnum> {

    @Override
    public boolean on(Object object, InEnum annotation, Context context) {
        if (ObjectUtils.isEmpty(object)) {
            return false;
        }
        Class<? extends Enum> enumClass = annotation.enumClass();
        try {
            Method method = enumClass.getMethod(annotation.method());
            Enum[] enums = enumClass.getEnumConstants();
            for (Enum e : enums) {
                Object value = ReflectUtils.invokeMethod(method, e);
                if (Objects.equals(value, object)) {
                    return true;
                }
            }
            return false;
        } catch (NoSuchMethodException e) {
            throw new InstrumentException(e.getMessage());
        }
    }

}
