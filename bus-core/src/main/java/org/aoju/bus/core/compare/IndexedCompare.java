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
package org.aoju.bus.core.compare;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.toolkit.ArrayKit;

import java.util.Comparator;

/**
 * 按照数组的顺序正序排列,数组的元素位置决定了对象的排序先后
 * 如果参与排序的元素并不在数组中,则排序在前
 *
 * @param <T> 被排序元素类型
 * @author Kimi Liu
 * @version 6.3.5
 * @since JDK 1.8+
 */
public class IndexedCompare<T> implements Comparator<T> {

    private final boolean atEndIfMiss;
    private final T[] array;

    /**
     * 构造
     *
     * @param obj 参与排序的数组，数组的元素位置决定了对象的排序先后
     */
    public IndexedCompare(T... obj) {
        this(false, obj);
    }

    /**
     * 构造
     *
     * @param atEndIfMiss 如果不在列表中是否排在后边
     * @param obj         参与排序的数组，数组的元素位置决定了对象的排序先后
     */
    public IndexedCompare(boolean atEndIfMiss, T... obj) {
        Assert.notNull(obj, "'objs' array must not be null");
        this.atEndIfMiss = atEndIfMiss;
        this.array = obj;
    }

    @Override
    public int compare(T o1, T o2) {
        final int index1 = getOrder(o1);
        final int index2 = getOrder(o2);

        return Integer.compare(index1, index2);
    }

    /**
     * 查找对象类型所在列表的位置
     *
     * @param object 对象
     * @return 位置，未找到位置根据{@link #atEndIfMiss}取不同值，false返回-1，否则返回列表长度
     */
    private int getOrder(T object) {
        int order = ArrayKit.indexOf(array, object);
        if (order < 0) {
            order = this.atEndIfMiss ? this.array.length : -1;
        }
        return order;
    }

}
