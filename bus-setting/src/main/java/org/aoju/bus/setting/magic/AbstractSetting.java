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
package org.aoju.bus.setting.magic;

import org.aoju.bus.core.beans.copier.CopyOptions;
import org.aoju.bus.core.beans.copier.ValueProvider;
import org.aoju.bus.core.getter.GroupedGetter;
import org.aoju.bus.core.getter.TypeGetter;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.function.XFunction;
import org.aoju.bus.core.toolkit.*;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Setting抽象类
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public abstract class AbstractSetting implements TypeGetter<CharSequence>,
        GroupedGetter<CharSequence, CharSequence>, Serializable {

    @Override
    public Object getObject(final CharSequence key, final Object defaultValue) {
        return ObjectKit.defaultIfNull(getObjectByGroup(key, Normal.EMPTY), defaultValue);
    }

    /**
     * 根据lambda的方法引用，获取
     *
     * @param func 方法引用
     * @param <P>  参数类型
     * @param <T>  返回值类型
     * @return 获取表达式对应属性和返回的对象
     */
    public <P, T> T get(final XFunction<P, T> func) {
        final LambdaKit.Info lambdaInfo = LambdaKit.resolve(func);
        return get(lambdaInfo.getFieldName(), lambdaInfo.getReturnType());
    }

    /**
     * 获得字符串类型值，如果字符串为{@code null}或者""返回默认值
     *
     * @param key          KEY
     * @param group        分组
     * @param defaultValue 默认值
     * @return 值，如果字符串为{@code null}或者""返回默认值
     */
    public String getByGroupNotEmpty(final String key, final String group, final String defaultValue) {
        final String value = getStringByGroup(key, group);
        return ObjectKit.defaultIfEmpty(value, defaultValue);
    }

    /**
     * 获得数组型
     *
     * @param key 属性名
     * @return 属性值
     */
    public String[] getStrs(final String key) {
        return getStrs(key, null);
    }

    /**
     * 获得数组型
     *
     * @param key          属性名
     * @param defaultValue 默认的值
     * @return 属性值
     */
    public String[] getStrs(final CharSequence key, final String[] defaultValue) {
        String[] value = getStrsByGroup(key, null);
        if (null == value) {
            value = defaultValue;
        }

        return value;
    }

    /**
     * 获得数组型默认逗号分隔
     * 若配置文件中键值对类似于：
     * <pre>
     *     a = 1,2,3,4
     * </pre>
     * 则获取结果为：[1, 2, 3, 4]
     *
     * @param key   属性名
     * @param group 分组名
     * @return 属性值
     */
    public String[] getStrsByGroup(final CharSequence key, final CharSequence group) {
        return getStrsByGroup(key, group, Symbol.COMMA);
    }

    /**
     * 获得数组型，可自定义分隔符
     * 假定分隔符为逗号，若配置文件中键值对类似于：
     * <pre>
     *     a = 1,2,3,4
     * </pre>
     * 则获取结果为：[1, 2, 3, 4]
     *
     * @param key       属性名
     * @param group     分组名
     * @param delimiter 分隔符
     * @return 属性值
     */
    public String[] getStrsByGroup(final CharSequence key, final CharSequence group, final CharSequence delimiter) {
        final String value = getStringByGroup(key, group);
        if (StringKit.isBlank(value)) {
            return null;
        }
        return StringKit.splitToArray(value, delimiter);
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法
     * 只支持基本类型的转换
     *
     * @param <T>   Bean类型
     * @param group 分组
     * @param bean  Bean对象
     * @return Bean
     */
    public <T> T toBean(final CharSequence group, final T bean) {
        return BeanKit.fillBean(bean, new ValueProvider<>() {

            @Override
            public Object value(final String key, final Type valueType) {
                return getObjectByGroup(key, group);
            }

            @Override
            public boolean containsKey(final String key) {
                return null != getObjectByGroup(key, group);
            }
        }, CopyOptions.create());
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法
     * 只支持基本类型的转换
     *
     * @param <T>       Bean类型
     * @param group     分组
     * @param beanClass Bean类型
     * @return Bean
     */
    public <T> T toBean(final CharSequence group, final Class<T> beanClass) {
        return toBean(group, ReflectKit.newInstanceIfPossible(beanClass));
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法
     * 只支持基本类型的转换
     *
     * @param <T>  bean类型
     * @param bean Bean
     * @return Bean
     */
    public <T> T toBean(final T bean) {
        return toBean(null, bean);
    }

    /**
     * 将setting中的键值关系映射到对象中，原理是调用对象对应的set方法
     * 只支持基本类型的转换
     *
     * @param <T>       bean类型
     * @param beanClass Bean类型
     * @return Bean
     */
    public <T> T toBean(final Class<T> beanClass) {
        return toBean(null, beanClass);
    }

}
