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
package org.aoju.bus.core.instance;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.function.XSupplier;
import org.aoju.bus.core.toolkit.ArrayKit;
import org.aoju.bus.core.toolkit.ClassKit;
import org.aoju.bus.core.toolkit.ReflectKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 实例化工具类
 * 对于 {@link InstanceFactory} 的便于使用
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@ThreadSafe
public final class Instances {

    /**
     * 如果对象池中存在此对象
     */

    private static final ConcurrentHashMap<String, Object> POOL = new ConcurrentHashMap<>();

    private Instances() {

    }

    /**
     * 静态方法单例
     *
     * @param clazz 类信息
     * @param <T>   泛型
     * @return 结果
     */
    public static <T> T singletion(Class<T> clazz) {
        return InstanceFactory.getInstance().singleton(clazz);
    }

    /**
     * 静态方法单例
     *
     * @param clazz     类信息
     * @param groupName 分组名称
     * @param <T>       泛型
     * @return 结果
     */
    public static <T> T singletion(Class<T> clazz, final String groupName) {
        return InstanceFactory.getInstance().singleton(clazz, groupName);
    }

    /**
     * threadLocal 同一个线程对应的实例一致
     *
     * @param clazz class
     * @param <T>   泛型
     * @return 结果
     */
    public static <T> T threadLocal(Class<T> clazz) {
        return InstanceFactory.getInstance().threadLocal(clazz);
    }

    /**
     * {@link ThreadSafe} 线程安全标示的使用单例,或者使用多例
     *
     * @param clazz class
     * @param <T>   泛型
     * @return 结果
     */
    public static <T> T threadSafe(Class<T> clazz) {
        return InstanceFactory.getInstance().threadSafe(clazz);
    }

    /**
     * 多例
     *
     * @param clazz class
     * @param <T>   泛型
     * @return 结果
     */
    public static <T> T multiple(Class<T> clazz) {
        return InstanceFactory.getInstance().multiple(clazz);
    }

    /**
     * 获得指定类的单例对象
     * 对象存在于池中返回，否则创建，每次调用此方法获得的对象为同一个对象
     * 注意：单例针对的是类和参数，也就是说只有类、参数一致才会返回同一个对象
     *
     * @param <T>    单例对象类型
     * @param clazz  类
     * @param params 构造方法参数
     * @return 单例对象
     */
    public static <T> T singletion(Class<T> clazz, Object... params) {
        Assert.notNull(clazz, "Class must be not null !");
        final String key = buildKey(clazz.getName(), params);
        return singletion(key, () -> ReflectKit.newInstance(clazz, params));
    }

    /**
     * 获得指定类的单例对象
     * 对象存在于池中返回，否则创建，每次调用此方法获得的对象为同一个对象
     * 注意：单例针对的是类和参数，也就是说只有类、参数一致才会返回同一个对象
     *
     * @param <T>      单例对象类型
     * @param key      自定义键
     * @param supplier 单例对象的创建函数
     * @return 单例对象
     */
    public static <T> T singletion(String key, XSupplier<T> supplier) {
        Object value = POOL.get(key);
        if (null == value) {
            POOL.putIfAbsent(key, supplier.get());
            value = POOL.get(key);
        }
        return (T) value;
    }

    /**
     * 获得指定类的单例对象
     * 对象存在于池中返回，否则创建，每次调用此方法获得的对象为同一个对象
     *
     * @param <T>       单例对象类型
     * @param className 类名
     * @param params    构造参数
     * @return 单例对象
     */
    public static <T> T singletion(String className, Object... params) {
        Assert.notBlank(className, "Class name must be not blank !");
        final Class<T> clazz = ClassKit.loadClass(className);
        return singletion(clazz, params);
    }

    /**
     * 将已有对象放入单例中，其Class做为键
     *
     * @param object 对象
     */
    public static void put(Object object) {
        Assert.notNull(object, "Bean object must be not null !");
        put(object.getClass().getName(), object);
    }

    /**
     * 将已有对象放入单例中，key做为键
     *
     * @param key    键
     * @param object 对象
     */
    public static void put(String key, Object object) {
        POOL.put(key, object);
    }

    /**
     * 判断某个类的对象是否存在
     *
     * @param clazz  类
     * @param params 构造参数
     * @return 是否存在
     */
    public static boolean exists(Class<?> clazz, Object... params) {
        if (null != clazz) {
            final String key = buildKey(clazz.getName(), params);
            return POOL.containsKey(key);
        }
        return false;
    }

    /**
     * 获取单例池中存在的所有类
     *
     * @return 非重复的类集合
     */
    public static Set<Class<?>> getExistClass() {
        return POOL.values().stream().map(Object::getClass).collect(Collectors.toSet());
    }

    /**
     * 移除指定Singleton对象
     *
     * @param clazz 类
     */
    public static void remove(Class<?> clazz) {
        if (null != clazz) {
            remove(clazz.getName());
        }
    }

    /**
     * 移除指定Singleton对象
     *
     * @param key 键
     */
    public static void remove(String key) {
        POOL.remove(key);
    }

    /**
     * 清除所有Singleton对象
     */
    public static void destroy() {
        POOL.clear();
    }

    /**
     * 构建key
     *
     * @param className 类名
     * @param params    参数列表
     * @return key
     */
    public static String buildKey(String className, Object... params) {
        if (ArrayKit.isEmpty(params)) {
            return className;
        }
        return StringKit.format("{}#{}", className, ArrayKit.join(params, Symbol.UNDERLINE));
    }

}
