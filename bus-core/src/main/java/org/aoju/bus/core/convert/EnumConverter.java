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
package org.aoju.bus.core.convert;

import org.aoju.bus.core.exception.ConvertException;
import org.aoju.bus.core.lang.Enums;
import org.aoju.bus.core.map.WeakMap;
import org.aoju.bus.core.toolkit.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 无泛型检查的枚举转换器
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class EnumConverter extends AbstractConverter {

    private static final long serialVersionUID = 1L;

    public static final EnumConverter INSTANCE = new EnumConverter();

    private static final WeakMap<Class<?>, Map<Class<?>, Method>> VALUE_OF_METHOD_CACHE = new WeakMap<>();

    /**
     * 尝试转换，转换规则为：
     * <ul>
     *     <li>如果实现{@link Enums}接口，则调用fromInt或fromStr转换</li>
     *     <li>找到类似转换的静态方法调用实现转换且优先使用</li>
     *     <li>约定枚举类应该提供 valueOf(String) 和 valueOf(Integer)用于转换</li>
     *     <li>oriInt /name 转换托底</li>
     * </ul>
     *
     * @param value     被转换的值
     * @param enumClass enum类
     * @return 对应的枚举值
     */
    protected static Enum tryConvertEnum(final Object value, final Class enumClass) {
        if (value == null) {
            return null;
        }

        // Enums实现转换
        if (Enums.class.isAssignableFrom(enumClass)) {
            final Enums first = (Enums) EnumKit.getEnumAt(enumClass, 0);
            if (null != first) {
                if (value instanceof Integer) {
                    return (Enum) first.from((Integer) value);
                } else if (value instanceof String) {
                    return (Enum) first.from(value.toString());
                }
            }
        }

        // 用户自定义方法
        // 查找枚举中所有返回值为目标枚举对象的方法，如果发现方法参数匹配，就执行之
        try {
            final Map<Class<?>, Method> methodMap = getMethodMap(enumClass);
            if (MapKit.isNotEmpty(methodMap)) {
                final Class<?> valueClass = value.getClass();
                for (final Map.Entry<Class<?>, Method> entry : methodMap.entrySet()) {
                    if (ClassKit.isAssignable(entry.getKey(), valueClass)) {
                        return ReflectKit.invokeStatic(entry.getValue(), value);
                    }
                }
            }
        } catch (final Exception ignore) {
            //ignore
        }

        //oriInt 应该滞后使用 以 GB/T 2261.1-2003 性别编码为例，对应整数并非连续数字会导致数字转枚举时失败
        //0 - 未知的性别
        //1 - 男性
        //2 - 女性
        //5 - 女性改(变)为男性
        //6 - 男性改(变)为女性
        //9 - 未说明的性别
        Enum enumResult = null;
        if (value instanceof Integer) {
            enumResult = EnumKit.getEnumAt(enumClass, (Integer) value);
        } else if (value instanceof String) {
            try {
                enumResult = Enum.valueOf(enumClass, (String) value);
            } catch (final IllegalArgumentException e) {
                //ignore
            }
        }

        return enumResult;
    }

    /**
     * 获取用于转换为enum的所有static方法
     *
     * @param enumClass 枚举类
     * @return 转换方法map，key为方法参数类型，value为方法
     */
    private static Map<Class<?>, Method> getMethodMap(final Class<?> enumClass) {
        return VALUE_OF_METHOD_CACHE.computeIfAbsent(enumClass, (key) -> Arrays.stream(enumClass.getMethods())
                .filter(BeanKit::isStatic)
                .filter(m -> m.getReturnType() == enumClass)
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> !"valueOf".equals(m.getName()))
                .collect(Collectors.toMap(m -> m.getParameterTypes()[0], m -> m, (k1, k2) -> k1)));
    }

    @Override
    protected Object convertInternal(final Class<?> targetClass, final Object value) {
        Enum enumValue = tryConvertEnum(value, targetClass);
        if (null == enumValue && false == value instanceof String) {
            // 最后尝试先将value转String，再valueOf转换
            enumValue = Enum.valueOf((Class) targetClass, convertToString(value));
        }

        if (null != enumValue) {
            return enumValue;
        }

        throw new ConvertException("Can not convert {} to {}", value, targetClass);
    }

}
