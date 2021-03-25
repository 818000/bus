/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 提供默认值的Map
 *
 * @author Kimi Liu
 * @version 6.2.2
 * @since JDK 1.8+
 */
public class TolerantMap<K, V> extends MapWrapper<K, V> {

    private static final long serialVersionUID = 1L;

    private final V defaultValue;

    /**
     * 构造
     *
     * @param defaultValue 默认值
     */
    public TolerantMap(V defaultValue) {
        this(new HashMap<>(), defaultValue);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始容量
     * @param loadFactor      增长因子
     * @param defaultValue    默认值
     */
    public TolerantMap(int initialCapacity, float loadFactor, V defaultValue) {
        this(new HashMap<>(initialCapacity, loadFactor), defaultValue);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始容量
     * @param defaultValue    默认值
     */
    public TolerantMap(int initialCapacity, V defaultValue) {
        this(new HashMap<>(initialCapacity), defaultValue);
    }

    /**
     * 构造
     *
     * @param map          Map实现
     * @param defaultValue 默认值
     */
    public TolerantMap(Map<K, V> map, V defaultValue) {
        super(map);
        this.defaultValue = defaultValue;
    }

    /**
     * 构建TolerantMap
     *
     * @param map          map实现
     * @param defaultValue 默认值
     * @param <K>          键类型
     * @param <V>          值类型
     * @return TolerantMap
     */
    public static <K, V> TolerantMap<K, V> of(Map<K, V> map, V defaultValue) {
        return new TolerantMap<>(map, defaultValue);
    }

    @Override
    public V get(Object key) {
        return getOrDefault(key, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }
        if (false == super.equals(o)) {
            return false;
        }
        final TolerantMap<?, ?> that = (TolerantMap<?, ?>) o;
        return getRaw().equals(that.getRaw()) && Objects.equals(defaultValue, that.defaultValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRaw(), defaultValue);
    }

    @Override
    public String toString() {
        return "TolerantMap{" + "map=" + getRaw() + ", defaultValue=" + defaultValue + '}';
    }

}
