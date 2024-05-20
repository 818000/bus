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
package org.miaixz.bus.core.annotation.resolve.elements;

import org.miaixz.bus.core.annotation.resolve.AnnotatedElements;
import org.miaixz.bus.core.annotation.resolve.AnnotationMapping;
import org.miaixz.bus.core.annotation.resolve.ResolvedAnnotationMapping;
import org.miaixz.bus.core.center.stream.EasyStream;
import org.miaixz.bus.core.text.CharsBacker;
import org.miaixz.bus.core.xyz.AnnoKit;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.core.xyz.ObjectKit;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.BiFunction;

/**
 * <p>注解元素映射，用于包装一个{@link AnnotatedElement}，然后将被包装的元素上，
 * 直接声明的注解以及这些注解的元组全部解析为{@link ResolvedAnnotationMapping}。
 * 从而用于支持对元注解的访问操作。
 *
 * <p>默认情况下，总是不扫描{@link java.lang}包下的注解，
 * 并且在当前实例中，{@link Inherited}注解将不生效，
 * 即通过<em>directly</em>方法将无法获得父类上带有{@link Inherited}的注解。
 *
 * <p>在一个{@link MetaAnnotatedElement}中，
 * {@link AnnotatedElement}上同类型的注解或元注解只会被保留一个，
 * 即当出现两个根注解都具有相同元注解时，仅有第一个根注解上的元注解会被保留，
 * 因此当通过{@link #getAnnotationsByType(Class)}
 * 或{@link #getDeclaredAnnotationsByType(Class)}方法用于只能获得一个注解对象。
 *
 * @param <T> AnnotationMapping类型
 * @author Kimi Liu
 * @see ResolvedAnnotationMapping
 * @since Java 17+
 */
public class MetaAnnotatedElement<T extends AnnotationMapping<Annotation>> implements AnnotatedElement, Iterable<T> {

    /**
     * 注解对象
     */
    private final AnnotatedElement element;

    /**
     * 创建{@link AnnotationMapping}的工厂方法，返回值为{@code null}时将忽略该注解
     */
    private final BiFunction<T, Annotation, T> mappingFactory;

    /**
     * 注解映射，此处为懒加载，默认为{@code null}，获取该属性必须通过{@link #getAnnotationMappings()}触发初始化
     */
    private volatile Map<Class<? extends Annotation>, T> annotationMappings;

    /**
     * 解析注解属性
     *
     * @param element        被注解元素
     * @param mappingFactory 创建{@link AnnotationMapping}的工厂方法，返回值为{@code null}时将忽略该注解
     */
    public MetaAnnotatedElement(final AnnotatedElement element, final BiFunction<T, Annotation, T> mappingFactory) {
        this.element = Objects.requireNonNull(element);
        this.mappingFactory = Objects.requireNonNull(mappingFactory);
        // 等待懒加载
        this.annotationMappings = null;
    }

    /**
     * 获取{@link AnnotatedElement}上的注解结构，该方法会针对相同的{@link AnnotatedElement}缓存映射对象
     *
     * @param element        被注解元素
     * @param mappingFactory 创建{@link AnnotationMapping}的工厂方法，返回值为{@code null}时将忽略该注解
     * @param <A>            {@link AnnotationMapping}类型
     * @return {@link AnnotatedElement}上的注解结构
     */
    public static <A extends AnnotationMapping<Annotation>> MetaAnnotatedElement<A> create(
            final AnnotatedElement element, final BiFunction<A, Annotation, A> mappingFactory) {
        return new MetaAnnotatedElement<>(element, mappingFactory);
    }

    /**
     * 从{@link AnnotatedElement}直接声明的注解的层级结构中获得注解映射对象
     *
     * @param annotationType 注解类型
     * @return 注解映射对象
     */
    public Optional<T> getMapping(final Class<? extends Annotation> annotationType) {
        return Optional.ofNullable(annotationType)
                .map(getAnnotationMappings()::get);
    }

    /**
     * 获取被包装的{@link AnnotatedElement}
     *
     * @return 被包装的 {@link AnnotatedElements}
     */
    public AnnotatedElement getElement() {
        return element;
    }

    /**
     * 从{@link AnnotatedElement}直接声明的注解中获得注解映射对象
     *
     * @param annotationType 注解类型
     * @return 注解映射对象
     */
    public Optional<T> getDeclaredMapping(final Class<? extends Annotation> annotationType) {
        return EasyStream.of(getAnnotationMappings().values())
                .filter(T::isRoot)
                .findFirst(mapping -> ObjectKit.equals(annotationType, mapping.annotationType()));
    }

    /**
     * 注解是否是{@link AnnotatedElement}直接声明的注解，或者在这些注解的层级结构中存在
     *
     * @param annotationType 注解元素
     * @return 是否
     */
    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return getMapping(annotationType)
                .isPresent();
    }

    /**
     * 从{@link AnnotatedElement}直接声明的注解的层级结构中获得注解对象
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解对象
     */
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return getMapping(annotationType)
                .map(T::getResolvedAnnotation)
                .map(annotationType::cast)
                .orElse(null);
    }

    /**
     * 从{@link AnnotatedElement}直接声明的注解中获得注解对象
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return 注解对象
     */
    @Override
    public <A extends Annotation> A getDeclaredAnnotation(final Class<A> annotationType) {
        return getDeclaredMapping(annotationType)
                .map(T::getResolvedAnnotation)
                .map(annotationType::cast)
                .orElse(null);
    }

    /**
     * 获取{@link AnnotatedElement}直接的指定类型注解
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return {@link AnnotatedElement}直接声明的指定类型注解
     */
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        final A result = getAnnotation(annotationType);
        if (Objects.nonNull(result)) {
            return (A[]) new Annotation[]{result};
        }
        return ArrayKit.newArray(annotationType, 0);
    }

    /**
     * 获取{@link AnnotatedElement}直接声明的指定类型注解
     *
     * @param annotationType 注解类型
     * @param <A>            注解类型
     * @return {@link AnnotatedElement}直接声明的指定类型注解
     */
    @Override
    public <A extends Annotation> A[] getDeclaredAnnotationsByType(final Class<A> annotationType) {
        final A result = getDeclaredAnnotation(annotationType);
        if (Objects.nonNull(result)) {
            return (A[]) new Annotation[]{result};
        }
        return ArrayKit.newArray(annotationType, 0);
    }

    /**
     * 获取{@link AnnotatedElement}直接声明的注解的映射对象
     *
     * @return {@link AnnotatedElement}直接声明的注解的映射对象
     */
    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getAnnotationMappings().values().stream()
                .filter(T::isRoot)
                .map(T::getResolvedAnnotation)
                .toArray(Annotation[]::new);
    }

    /**
     * 获取所有注解
     *
     * @return 所有注解
     */
    @Override
    public Annotation[] getAnnotations() {
        return getAnnotationMappings().values().stream()
                .map(T::getResolvedAnnotation)
                .toArray(Annotation[]::new);
    }

    /**
     * 获取注解映射对象集合的迭代器
     *
     * @return 迭代器
     */
    @Override
    public Iterator<T> iterator() {
        return getAnnotationMappings().values().iterator();
    }

    /**
     * 比较两个实例是否相等
     *
     * @param o 对象
     * @return 是否
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MetaAnnotatedElement<?> that = (MetaAnnotatedElement<?>) o;
        return element.equals(that.element) && mappingFactory.equals(that.mappingFactory);
    }

    /**
     * 获取实例的哈希值
     *
     * @return 哈希值
     */
    @Override
    public int hashCode() {
        return Objects.hash(element, mappingFactory);
    }

    /**
     * 获取注解映射，若当前实例未完成初始化则先进行初始化
     *
     * @return 不可变的注解映射集合
     */
    protected final Map<Class<? extends Annotation>, T> getAnnotationMappings() {
        initAnnotationMappingsIfNecessary();
        return annotationMappings;
    }

    /**
     * 该注解是否需要映射
     * 默认情况下，已经处理过、或在{@link java.lang}包下的注解不会被处理
     *
     * @param mappings   当前已处理的注解
     * @param annotation 注解对象
     * @return 是否
     */
    protected boolean isNeedMapping(final Map<Class<? extends Annotation>, T> mappings, final Annotation annotation) {
        return !CharsBacker.startWith(annotation.annotationType().getName(), "java.lang.")
                && !mappings.containsKey(annotation.annotationType());
    }

    /**
     * 创建注解映射
     */
    private T createMapping(final T source, final Annotation annotation) {
        return mappingFactory.apply(source, annotation);
    }

    /**
     * 扫描{@link AnnotatedElement}上直接声明的注解，然后按广度优先扫描这些注解的元注解，
     * 直到将所有类型的注解对象皆加入{@link #annotationMappings}为止
     */
    private void initAnnotationMappingsIfNecessary() {
        // 双重检查保证初始化过程线程安全
        if (Objects.isNull(annotationMappings)) {
            synchronized (this) {
                if (Objects.isNull(annotationMappings)) {
                    final Map<Class<? extends Annotation>, T> mappings = new LinkedHashMap<>(8);
                    initAnnotationMappings(mappings);
                    this.annotationMappings = Collections.unmodifiableMap(mappings);
                }
            }
        }
    }

    /**
     * 初始化
     */
    private void initAnnotationMappings(final Map<Class<? extends Annotation>, T> mappings) {
        final Deque<T> deque = new LinkedList<>();
        Arrays.stream(AnnoKit.getDeclaredAnnotations(element))
                .filter(m -> isNeedMapping(mappings, m))
                .map(annotation -> createMapping(null, annotation))
                .filter(Objects::nonNull)
                .forEach(deque::addLast);
        while (!deque.isEmpty()) {
            // 若已有该类型的注解，则不再进行扫描
            final T mapping = deque.removeFirst();
            if (!isNeedMapping(mappings, mapping)) {
                continue;
            }
            // 保存该注解，并将其需要处理的元注解也加入队列
            mappings.put(mapping.annotationType(), mapping);
            for (final Annotation annotation : AnnoKit.getDeclaredAnnotations(mapping.annotationType())) {
                if (mappings.containsKey(annotation.annotationType())) {
                    continue;
                }
                final T m = createMapping(mapping, annotation);
                if (Objects.nonNull(m) && isNeedMapping(mappings, m)) {
                    deque.addLast(m);
                }
            }
        }
    }

}
