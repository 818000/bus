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
package org.aoju.bus.core.toolkit;

import org.aoju.bus.core.lang.Assert;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 枚举工具类
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
public class EnumKit {

    /**
     * 指定类是否为Enum类
     *
     * @param clazz 类
     * @return 是否为Enum类
     */
    public static boolean isEnum(Class<?> clazz) {
        Assert.notNull(clazz);
        return clazz.isEnum();
    }

    /**
     * 指定类是否为Enum类
     *
     * @param obj 类
     * @return 是否为Enum类
     */
    public static boolean isEnum(Object obj) {
        Assert.notNull(obj);
        return obj.getClass().isEnum();
    }

    /**
     * Enum对象转String，调用{@link Enum#name()} 方法
     *
     * @param e Enum
     * @return name值
     */
    public static String toString(Enum<?> e) {
        return null != e ? e.name() : null;
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @return 枚举值
     */
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
        return Enum.valueOf(enumClass, value);
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
     * 如果无枚举值，返回默认值
     *
     * @param <E>          枚举类型泛型
     * @param enumClass    枚举类
     * @param value        值
     * @param defaultValue 无对应枚举值返回的默认值
     * @return 枚举值
     */
    public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value, E defaultValue) {
        return ObjectKit.defaultIfNull(fromStringQuietly(enumClass, value), defaultValue);
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}，转换失败返回{@code null} 而非报错
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param value     值
     * @return 枚举值
     */
    public static <E extends Enum<E>> E fromStringQuietly(Class<E> enumClass, String value) {
        if (null == enumClass || StringKit.isBlank(value)) {
            return null;
        }

        try {
            return fromString(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 模糊匹配转换为枚举，给定一个值，匹配枚举中定义的所有字段名(包括name属性)，一旦匹配到返回这个枚举对象，否则返回null
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param value     值
     * @return 匹配到的枚举对象，未匹配到返回null
     */
    public static <E extends Enum<E>> E likeValueOf(Class<E> enumClass, Object value) {
        if (value instanceof CharSequence) {
            value = value.toString().trim();
        }

        final Field[] fields = ReflectKit.getFields(enumClass);
        final Enum<?>[] enums = enumClass.getEnumConstants();
        String fieldName;
        for (Field field : fields) {
            fieldName = field.getName();
            if (field.getType().isEnum() || "ENUM$VALUES".equals(fieldName) || "ordinal".equals(fieldName)) {
                // 跳过一些特殊字段
                continue;
            }
            for (Enum<?> enumObj : enums) {
                if (ObjectKit.equal(value, ReflectKit.getFieldValue(enumObj, field))) {
                    return (E) enumObj;
                }
            }
        }
        return null;
    }

    /**
     * 字符串转枚举，调用{@link Enum#valueOf(Class, String)}
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param index     枚举索引
     * @return 枚举值，null表示无此对应枚举
     */
    public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
        final E[] enumConstants = enumClass.getEnumConstants();
        return index >= 0 && index < enumConstants.length ? enumConstants[index] : null;
    }

    /**
     * 枚举类中所有枚举对象的name列表
     *
     * @param clazz 枚举类
     * @return name列表
     */
    public static List<String> getNames(Class<? extends Enum<?>> clazz) {
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final List<String> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(e.name());
        }
        return list;
    }

    /**
     * 获得枚举类中各枚举对象下指定字段的值
     *
     * @param clazz     枚举类
     * @param fieldName 字段名，最终调用getXXX方法
     * @return 字段值列表
     */
    public static List<Object> getFieldValues(Class<? extends Enum<?>> clazz, String fieldName) {
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final List<Object> list = new ArrayList<>(enums.length);
        for (Enum<?> e : enums) {
            list.add(ReflectKit.getFieldValue(e, fieldName));
        }
        return list;
    }

    /**
     * 获得枚举类中所有的字段名
     * 除用户自定义的字段名，也包括“name”字段，例如：
     *
     * <pre>
     *   EnumUtil.getFieldNames(Color.class) == ["name", "index"]
     * </pre>
     *
     * @param clazz 枚举类
     * @return 字段名列表
     */
    public static List<String> getFieldNames(Class<? extends Enum<?>> clazz) {
        final List<String> names = new ArrayList<>();
        final Field[] fields = ReflectKit.getFields(clazz);
        String name;
        for (Field field : fields) {
            name = field.getName();
            if (field.getType().isEnum() || name.contains("$VALUES") || "ordinal".equals(name)) {
                continue;
            }
            if (false == names.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    /**
     * 获得枚举名对应指定字段值的Map
     * 键为枚举名，值为字段值
     *
     * @param clazz     枚举类
     * @param fieldName 字段名，最终调用getXXX方法
     * @return 枚举名对应指定字段值的Map
     */
    public static Map<String, Object> getFieldNames(Class<? extends Enum<?>> clazz, String fieldName) {
        final Enum<?>[] enums = clazz.getEnumConstants();
        if (null == enums) {
            return null;
        }
        final Map<String, Object> map = MapKit.newHashMap(enums.length);
        for (Enum<?> e : enums) {
            map.put(e.name(), ReflectKit.getFieldValue(e, fieldName));
        }
        return map;
    }

    /**
     * 获取枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序
     * 结果中键为枚举名，值为枚举对象
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @return 枚举字符串值和枚举对象的Map对应，使用LinkedHashMap保证有序
     */
    public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(final Class<E> enumClass) {
        final LinkedHashMap<String, E> map = new LinkedHashMap<>();
        for (final E e : enumClass.getEnumConstants()) {
            map.put(e.name(), e);
        }
        return map;
    }

    /**
     * 判断某个值是存在枚举中
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param val       需要查找的值
     * @return 是否存在
     */
    public static <E extends Enum<E>> boolean contains(final Class<E> enumClass, String val) {
        return getEnumMap(enumClass).containsKey(val);
    }

    /**
     * 判断某个值是不存在枚举中
     *
     * @param <E>       枚举类型
     * @param enumClass 枚举类
     * @param val       需要查找的值
     * @return 是否不存在
     */
    public static <E extends Enum<E>> boolean notContains(final Class<E> enumClass, String val) {
        return false == contains(enumClass, val);
    }

    /**
     * 忽略大小检查某个枚举值是否匹配指定值
     *
     * @param e   枚举值
     * @param val 需要判断的值
     * @return 是非匹配
     */
    public static boolean equalsIgnoreCase(final Enum<?> e, String val) {
        return StringKit.equalsIgnoreCase(toString(e), val);
    }

    /**
     * 检查某个枚举值是否匹配指定值
     *
     * @param e   枚举值
     * @param val 需要判断的值
     * @return 是非匹配
     */
    public static boolean equals(final Enum<?> e, String val) {
        return StringKit.equals(toString(e), val);
    }

    /**
     * 获取枚举的{@code List}
     *
     * @param <E>       枚举的类型
     * @param enumClass 要查询的枚举的类
     * @return 枚举的可修改列表，从不为空
     */
    public static <E extends Enum<E>> List<E> getEnumList(final Class<E> enumClass) {
        return new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
    }

    /**
     * 检查指定的名称是否是类的有效枚举
     *
     * @param <E>       枚举的类型
     * @param enumClass 要查询的枚举的类
     * @param enumName  枚举名null返回false
     * @return 如果枚举名有效，则为真，否则为假
     */
    public static <E extends Enum<E>> boolean isValidEnum(final Class<E> enumClass, final String enumName) {
        return getEnum(enumClass, enumName) != null;
    }

    /**
     * 检查指定的名称是否是类的有效枚举
     *
     * @param <E>       枚举的类型
     * @param enumClass 要查询的枚举的类
     * @param enumName  枚举名null返回false
     * @return 如果枚举名有效，则为真，否则为假
     */
    public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(final Class<E> enumClass, final String enumName) {
        return getEnumIgnoreCase(enumClass, enumName) != null;
    }

    /**
     * 获取类的枚举，如果没有找到，则返回{@code null}
     *
     * @param <E>       枚举的类型
     * @param enumClass 要查询的枚举的类
     * @param enumName  枚举名
     * @return 枚举，如果没有找到则为空
     */
    public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String enumName) {
        if (enumName == null) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, enumName);
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * 获取类的枚举，如果没有找到，则返回{@code null}
     *
     * @param <E>       枚举的类型
     * @param enumClass 要查询的枚举的类
     * @param enumName  枚举名
     * @return 枚举，如果没有找到则为空
     */
    public static <E extends Enum<E>> E getEnumIgnoreCase(final Class<E> enumClass, final String enumName) {
        if (enumName == null || !enumClass.isEnum()) {
            return null;
        }
        for (final E each : enumClass.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(enumName)) {
                return each;
            }
        }
        return null;
    }

}
