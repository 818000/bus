/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2024 miaixz.org and other contributors.                    *
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
package org.miaixz.bus.core.lang.reflect.method;

import org.miaixz.bus.core.center.map.reference.WeakConcurrentMap;
import org.miaixz.bus.core.lang.mutable.Mutable;
import org.miaixz.bus.core.lang.mutable.MutableObject;
import org.miaixz.bus.core.toolkit.ArrayKit;
import org.miaixz.bus.core.toolkit.ClassKit;
import org.miaixz.bus.core.toolkit.MapKit;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 方法查找工具类，用于从指定的类或类层级结构中，根据特定规则搜索方法。
 *
 * <p><strong>查找方式与查找范围</strong>
 * <p>支持四种语义的查找：
 * <ul>
 *     <li><i>get</i>: 获取首个符合条件的方法；</li>
 *     <li><i>getWithMetadata</i>: 获取首个带有指定元数据的方法与该元数据；</li>
 *     <li><i>find</i>: 获得所有符合条件的方法；</li>
 *     <li><i>findWithMetadata</i>: 获取所有带有指定元数据的方法与这些元数据；</li>
 * </ul>
 * 基于上述四种语义的查找，提供四种查找范围：
 * <ul>
 *     <li><i>xxxFromSpecificMethods</i>：在用户给定的方法列表中查找匹配的方法；</li>
 *     <li><i>xxxFromMethods</i>：在{@link Class#getMethods}的范围中查找匹配的方法；</li>
 *     <li><i>xxxFromDeclaredMethods</i>：在{@link Class#getDeclaredMethods}的范围中查找匹配的方法；</li>
 *     <li><i>xxxFromAllMethods</i>：在类的层级结构中的所有类的所有方法的范围中查找匹配的方法；</li>
 * </ul>
 * 比如，我们希望获取{@link Class#getMethods()}中所有匹配条件的方法，则应当调用{@link #findFromMethods}，
 * 若我们希望获得类所有方法范围中首个匹配的方法，则应当调用{@link #getFromAllMethods}。
 *
 * <p><strong>匹配规则</strong>
 * <p>方法查找的规则由{@link Function}实现。
 * 规定，当{@link Function#apply(Object)}方法返回元数据不为{@code null}时，则认为方法与其匹配，返回结果时将同时返回匹配的方法与元数据。
 * 因此，我们可以通过实现{@link Function}接口来同时实现方法的查找与元数据的获取：
 * 比如，我们希望查找所有方法上带有{@code Annotation}注解的方法，则可以实现如下：
 * <pre>{@code
 * 		Map<Method, Annotation> methods = MethodScanner.findFromAllMethods(Foo.class, method -> method.getAnnotation(Annotation.class));
 * }</pre>
 * 此外，对于一些无需获取元数据的查找，我们可以使用{@link MethodMatcher}提供的一些内置实现：
 * <pre>{@code
 * 		// 查找所有静态公开方法
 * 		Set<Method> methods = MethodScanner.findFromAllMethods(Foo.class, MethodMatcher.isPublicStatic());
 * 		// 按照方法名与参数类型查找方法
 * 		Method method = MethodScanner.getFromAllMethods(Foo.class, MethodMatcher.forNameAndParameterTypes("foo", String.class));
 * }</pre>
 *
 * <p><strong>缓存</strong>
 * <p>对于{@link #getDeclaredMethods}与{@link #getMethods}方法与基于这两个方法实现的，
 * 所有{@code xxxFromMethods}与{@code xxxFromDeclaredMethods}方法，
 * 都提供了缓存基于{@link WeakConcurrentMap}的缓存支持。
 * {@link #getAllMethods}与所有{@code xxxFromAllMethods}方法都基于{@link #getDeclaredMethods}实现，
 * 但是每次全量查找，都需要重新遍历类层级结构，因此会带来一定的额外的性能损耗。
 * 缓存在GC时会被回收，但是也可以通过{@link #clearCaches}手动清除缓存。
 *
 * @author Kimi Liu
 * @see MethodMatcher
 * @since Java 17+
 */
public class MethodScanner {

    /**
     * 空方法列表
     */
    private static final Method[] EMPTY_METHODS = new Method[0];

    /**
     * 方法缓存
     */
    private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE = new WeakConcurrentMap<>();

    /**
     * 直接声明的方法缓存
     */
    private static final WeakConcurrentMap<Class<?>, Method[]> DECLARED_METHODS_CACHE = new WeakConcurrentMap<>();

    /**
     * 获取当前类及父类的所有公共方法，等同于{@link Class#getMethods()}
     *
     * @param type 类
     * @return 当前类及父类的所有公共方法
     */
    public static Method[] getMethods(final Class<?> type) {
        if (Objects.isNull(type)) {
            return EMPTY_METHODS;
        }
        return METHODS_CACHE.computeIfAbsent(type, Class::getMethods);
    }

    /**
     * 获取当前类直接声明的所有方法，等同于{@link Class#getDeclaredMethods()}
     *
     * @param type 类
     * @return 当前类及父类的所有公共方法
     */
    public static Method[] getDeclaredMethods(final Class<?> type) {
        if (Objects.isNull(type)) {
            return EMPTY_METHODS;
        }
        return DECLARED_METHODS_CACHE.computeIfAbsent(type, Class::getDeclaredMethods);
    }

    /**
     * 获取当前类层级结构中的所有方法。
     * 等同于按广度优先遍历类及其所有父类与接口，并依次调用{@link Class#getDeclaredMethods()}。
     * 返回的方法排序规则如下：
     * <ul>
     *     <li>离{@code type}距离越近，则顺序越靠前；</li>
     *     <li>与{@code type}距离相同，则父类优先于接口；</li>
     *     <li>与{@code type}距离相同的接口，则顺序遵循接口在{@link Class#getInterfaces()}的顺序；</li>
     * </ul>
     *
     * @param type 类
     * @return 当前类及父类的所有公共方法
     * @see ClassKit#traverseTypeHierarchyWhile(Class, Predicate)
     */
    public static Method[] getAllMethods(final Class<?> type) {
        if (Objects.isNull(type)) {
            return EMPTY_METHODS;
        }
        final List<Method> methods = new ArrayList<>();
        ClassKit.traverseTypeHierarchyWhile(type, t -> {
            methods.addAll(Arrays.asList(getDeclaredMethods(t)));
            return true;
        });
        return methods.isEmpty() ? EMPTY_METHODS : methods.toArray(new Method[0]);
    }

    /**
     * 清空缓存
     */
    public static void clearCaches() {
        METHODS_CACHE.clear();
        DECLARED_METHODS_CACHE.clear();
    }

    /**
     * 从指定方法列表中筛选所有方法上带有指定元数据方法的方法与对应元数据
     *
     * @param methods 方法列表
     * @param lookup  查找器
     * @param <T>     结果类型
     * @return 方法与对应的元数据集合
     */
    public static <T> Map<Method, T> findWithMetadataFromSpecificMethods(final Method[] methods, final Function<Method, T> lookup) {
        if (ArrayKit.isEmpty(methods)) {
            return Collections.emptyMap();
        }
        final Map<Method, T> results = new LinkedHashMap<>();
        for (final Method method : methods) {
            final T result = lookup.apply(method);
            if (Objects.nonNull(result)) {
                results.put(method, result);
            }
        }
        return results;
    }

    /**
     * 从指定方法列表中筛选所有方法上带有指定元数据方法的方法
     *
     * @param methods 方法列表
     * @param lookup  查找器
     * @return 方法集合
     */
    public static Set<Method> findFromSpecificMethods(final Method[] methods, final Function<Method, ?> lookup) {
        return findWithMetadataFromSpecificMethods(methods, lookup).keySet();
    }

    /**
     * 从指定方法列表中筛选所有方法上带有指定元数据方法的方法与对应元数据
     *
     * @param methods 方法列表
     * @param lookup  查找器
     * @param <T>     值类型
     * @return 方法与对应的元数据
     */
    public static <T> Map.Entry<Method, T> getWithMetadataFromSpecificMethods(final Method[] methods, final Function<Method, T> lookup) {
        for (final Method method : methods) {
            final T result = lookup.apply(method);
            if (Objects.nonNull(result)) {
                return MapKit.entry(method, result);
            }
        }
        return null;
    }

    /**
     * 从指定方法列表中筛选所有方法上带有指定元数据方法的方法
     *
     * @param methods 方法列表
     * @param lookup  查找器
     * @return 方法
     */
    public static Method getFromSpecificMethods(final Method[] methods, final Function<Method, ?> lookup) {
        final Map.Entry<Method, ?> result = getWithMetadataFromSpecificMethods(methods, lookup);
        return Objects.isNull(result) ? null : result.getKey();
    }

    /**
     * 获取方法上带有指定元数据的方法与对应元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法与对应的元数据集合
     */
    public static <T> Map<Method, T> findWithMetadataFromMethods(final Class<?> type, final Function<Method, T> lookup) {
        return findWithMetadataFromSpecificMethods(getMethods(type), lookup);
    }

    /**
     * 获取方法上带有指定元数据的方法
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法集合
     */
    public static Set<Method> findFromMethods(final Class<?> type, final Function<Method, ?> lookup) {
        return findFromSpecificMethods(getMethods(type), lookup);
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法及元数据，若无任何匹配的结果则返回{@code null}
     */
    public static <T> Map.Entry<Method, T> getWithMetadataFromMethods(final Class<?> type, final Function<Method, T> lookup) {
        return getWithMetadataFromSpecificMethods(getMethods(type), lookup);
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法，若无任何匹配的结果则返回{@code null}
     */
    public static Method getFromMethods(final Class<?> type, final Function<Method, ?> lookup) {
        return getFromSpecificMethods(getMethods(type), lookup);
    }

    /**
     * 获取方法上带有指定元数据的方法与对应元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法与对应的元数据集合
     */
    public static <T> Map<Method, T> findWithMetadataFromDeclaredMethods(final Class<?> type, final Function<Method, T> lookup) {
        return findWithMetadataFromSpecificMethods(getDeclaredMethods(type), lookup);
    }

    /**
     * 获取方法上带有指定元数据的方法
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法集合
     */
    public static Set<Method> findFromDeclaredMethods(final Class<?> type, final Function<Method, ?> lookup) {
        return findFromSpecificMethods(getDeclaredMethods(type), lookup);
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法及元数据，若无任何匹配的结果则返回{@code null}
     */
    public static <T> Map.Entry<Method, T> getWithMetadataFromDeclaredMethods(final Class<?> type, final Function<Method, T> lookup) {
        return getWithMetadataFromSpecificMethods(getDeclaredMethods(type), lookup);
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法，若无任何匹配的结果则返回{@code null}
     */
    public static Method getFromDeclaredMethods(final Class<?> type, final Function<Method, ?> lookup) {
        return getFromSpecificMethods(getDeclaredMethods(type), lookup);
    }

    /**
     * 获取方法上带有指定元数据的方法与对应元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法与对应的元数据集合
     */
    public static <T> Map<Method, T> findWithMetadataFromAllMethods(final Class<?> type, final Function<Method, T> lookup) {
        return findWithMetadataFromSpecificMethods(getAllMethods(type), lookup);
    }

    /**
     * 获取方法上带有指定元数据的方法
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法集合
     */
    public static Set<Method> findFromAllMethods(final Class<?> type, final Function<Method, ?> lookup) {
        return findFromSpecificMethods(getAllMethods(type), lookup);
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @param <T>    值类型
     * @return 方法及元数据，若无任何匹配的结果则返回{@code null}
     */
    public static <T> Map.Entry<Method, T> getWithMetadataFromAllMethods(final Class<?> type, final Function<Method, T> lookup) {
        if (Objects.isNull(type)) {
            return null;
        }
        final Mutable<Map.Entry<Method, T>> result = new MutableObject<>();
        ClassKit.traverseTypeHierarchyWhile(type, t -> {
            final Map.Entry<Method, T> target = getWithMetadataFromDeclaredMethods(t, lookup);
            if (Objects.nonNull(target)) {
                result.set(target);
                return false;
            }
            return true;
        });
        return result.get();
    }

    /**
     * 获取首个方法上带有指定元数据的方法及元数据
     *
     * @param type   类型
     * @param lookup 查找器
     * @return 方法，若无任何匹配的结果则返回{@code null}
     */
    public static Method getFromAllMethods(final Class<?> type, final Function<Method, ?> lookup) {
        final Map.Entry<Method, ?> target = getWithMetadataFromAllMethods(type, lookup);
        return Objects.isNull(target) ? null : target.getKey();
    }

}
