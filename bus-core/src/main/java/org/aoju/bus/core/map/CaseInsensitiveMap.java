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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 忽略大小写的Map
 * 对KEY忽略大小写,get("Value")和get("value")获得的值相同,put进入的值也会被覆盖
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Kimi Liu
 * @since Java 17+
 */
public class CaseInsensitiveMap<K, V> extends FuncKeyMap<K, V> {

    /**
     * 构造
     */
    public CaseInsensitiveMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     */
    public CaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 构造
     * 注意此构造将传入的Map作为被包装的Map，针对任何修改，传入的Map都会被同样修改
     *
     * @param map 被包装的自定义Map创建器
     */
    public CaseInsensitiveMap(Map<? extends K, ? extends V> map) {
        this(DEFAULT_LOAD_FACTOR, map);
    }

    /**
     * 构造
     *
     * @param loadFactor 加载因子
     * @param map        Map
     */
    public CaseInsensitiveMap(float loadFactor, Map<? extends K, ? extends V> map) {
        this(map.size(), loadFactor);
        this.putAll(map);
    }

    /**
     * 构造
     *
     * @param initialCapacity 初始大小
     * @param loadFactor      加载因子
     */
    public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
        this(MapBuilder.create(new HashMap<>(initialCapacity, loadFactor)));
    }

    /**
     * 构造
     * 注意此构造将传入的Map作为被包装的Map，针对任何修改，传入的Map都会被同样修改
     *
     * @param emptyMapBuilder 被包装的自定义Map创建器
     */
    CaseInsensitiveMap(MapBuilder<K, V> emptyMapBuilder) {
        super(emptyMapBuilder.build(), (Function<Object, K> & Serializable) (key) -> {
            if (key instanceof CharSequence) {
                key = key.toString().toLowerCase();
            }
            return (K) key;
        });
    }

}
