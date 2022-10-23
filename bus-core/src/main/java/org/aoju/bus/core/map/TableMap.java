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
package org.aoju.bus.core.map;

import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.MapKit;
import org.aoju.bus.core.toolkit.ObjectKit;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * 无重复键的Map
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Kimi Liu
 * @since Java 17+
 */
public class TableMap<K, V> implements Map<K, V>, Iterable<Map.Entry<K, V>>, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<K> keys;
    private final List<V> values;

    /**
     * 构造
     */
    public TableMap() {
        this(10);
    }

    /**
     * 构造
     *
     * @param size 初始容量
     */
    public TableMap(final int size) {
        this.keys = new ArrayList<>(size);
        this.values = new ArrayList<>(size);
    }

    /**
     * 构造
     *
     * @param keys   键列表
     * @param values 值列表
     */
    public TableMap(final K[] keys, final V[] values) {
        this.keys = CollKit.toList(keys);
        this.values = CollKit.toList(values);
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return CollKit.isEmpty(keys);
    }

    @Override
    public boolean containsKey(final Object key) {
        return keys.contains(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return values.contains(value);
    }

    @Override
    public V get(final Object key) {
        final int index = keys.indexOf(key);
        if (index > -1) {
            return values.get(index);
        }
        return null;
    }

    /**
     * 根据value获得对应的key，只返回找到的第一个value对应的key值
     *
     * @param value 值
     * @return 键
     */
    public K getKey(final V value) {
        final int index = values.indexOf(value);
        if (index > -1) {
            return keys.get(index);
        }
        return null;
    }

    /**
     * 获取指定key对应的所有值
     *
     * @param key 键
     * @return 值列表
     */
    public List<V> getValues(final K key) {
        return CollKit.getAny(
                this.values,
                CollKit.indexOfAll(this.keys, (ele) -> ObjectKit.equals(ele, key))
        );
    }

    /**
     * 获取指定value对应的所有key
     *
     * @param value 值
     * @return 值列表
     */
    public List<K> getKeys(final V value) {
        return CollKit.getAny(
                this.keys,
                CollKit.indexOfAll(this.values, (ele) -> ObjectKit.equals(ele, value))
        );
    }

    @Override
    public V put(final K key, final V value) {
        keys.add(key);
        values.add(value);
        return null;
    }

    /**
     * 移除指定的所有键和对应的所有值
     *
     * @param key 键
     * @return 最后一个移除的值
     */
    @Override
    public V remove(final Object key) {
        V lastValue = null;
        int index;
        while ((index = keys.indexOf(key)) > -1) {
            lastValue = removeByIndex(index);
        }
        return lastValue;
    }

    /**
     * 移除指定位置的键值对
     *
     * @param index 位置，不能越界
     * @return 移除的值
     */
    public V removeByIndex(final int index) {
        keys.remove(index);
        return values.remove(index);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        keys.clear();
        values.clear();
    }

    @Override
    public Set<K> keySet() {
        return new HashSet<>(this.keys);
    }

    /**
     * 获取所有键，可重复，不可修改
     *
     * @return 键列表
     */
    public List<K> keys() {
        return Collections.unmodifiableList(this.keys);
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final Set<Map.Entry<K, V>> hashSet = new LinkedHashSet<>();
        for (int i = 0; i < size(); i++) {
            hashSet.add(MapKit.entry(keys.get(i), values.get(i)));
        }
        return hashSet;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<>() {
            private final Iterator<K> keysIter = keys.iterator();
            private final Iterator<V> valuesIter = values.iterator();

            @Override
            public boolean hasNext() {
                return keysIter.hasNext() && valuesIter.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                return MapKit.entry(keysIter.next(), valuesIter.next());
            }

            @Override
            public void remove() {
                keysIter.remove();
                valuesIter.remove();
            }
        };
    }

    @Override
    public String toString() {
        return "TableMap{" +
                "keys=" + keys +
                ", values=" + values +
                '}';
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        for (int i = 0; i < size(); i++) {
            action.accept(keys.get(i), values.get(i));
        }
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        boolean removed = false;
        for (int i = 0; i < size(); i++) {
            if (ObjectKit.equals(key, keys.get(i)) && ObjectKit.equals(value, values.get(i))) {
                removeByIndex(i);
                removed = true;
                // 移除当前元素，下个元素前移
                i--;
            }
        }
        return removed;
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        for (int i = 0; i < size(); i++) {
            final V newValue = function.apply(keys.get(i), values.get(i));
            values.set(i, newValue);
        }
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        for (int i = 0; i < size(); i++) {
            if (ObjectKit.equals(key, keys.get(i)) && ObjectKit.equals(oldValue, values.get(i))) {
                values.set(i, newValue);
                return true;
            }
        }
        return false;
    }

    /**
     * 替换指定key的所有值为指定值
     *
     * @param key   指定的key
     * @param value 替换的值
     * @return 最后替换的值
     */
    @Override
    public V replace(final K key, final V value) {
        V lastValue = null;
        for (int i = 0; i < size(); i++) {
            if (ObjectKit.equals(key, keys.get(i))) {
                lastValue = values.set(i, value);
            }
        }
        return lastValue;
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if (null == remappingFunction) {
            return null;
        }

        V lastValue = null;
        for (int i = 0; i < size(); i++) {
            if (ObjectKit.equals(key, keys.get(i))) {
                final V newValue = remappingFunction.apply(key, values.get(i));
                if (null != newValue) {
                    lastValue = values.set(i, newValue);
                } else {
                    removeByIndex(i);
                    // 移除当前元素，下个元素前移
                    i--;
                }
            }
        }
        return lastValue;
    }

}
