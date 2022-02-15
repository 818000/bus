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
package org.aoju.bus.core.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 实现一个{@link java.util.Iterator} 任何数组的迭代器
 * 数组可以是对象数组,也可以是基元数组 如果你知道
 * class是更好的选择,因为它会表现得更好
 * 迭代器实现了一个{@link #reset}方法,允许重置
 * 如果需要,迭代器返回到开始
 *
 * @param <E> 元素类型
 * @author Kimi Liu
 * @version 6.3.5
 * @since JDK 1.8+
 */
public class ArrayIterator<E> implements IterableIterator<E>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数组
     */
    private final Object array;
    /**
     * 起始位置
     */
    private int startIndex;
    /**
     * 结束位置
     */
    private int endIndex;
    /**
     * 当前位置
     */
    private int index;

    /**
     * 构造
     *
     * @param array 数组
     * @throws IllegalArgumentException array对象不为数组抛出此异常
     * @throws NullPointerException     array对象为null
     */
    public ArrayIterator(E[] array) {
        this((Object) array);
    }

    /**
     * 构造
     *
     * @param array 数组
     * @throws IllegalArgumentException array对象不为数组抛出此异常
     * @throws NullPointerException     array对象为null
     */
    public ArrayIterator(Object array) {
        this(array, 0);
    }

    /**
     * 构造
     *
     * @param array      数组
     * @param startIndex 起始位置，当起始位置小于0或者大于结束位置，置为0。
     * @throws IllegalArgumentException array对象不为数组抛出此异常
     * @throws NullPointerException     array对象为null
     */
    public ArrayIterator(final Object array, final int startIndex) {
        this(array, startIndex, -1);
    }

    /**
     * 构造
     *
     * @param array      数组
     * @param startIndex 起始位置，当起始位置小于0或者大于结束位置，置为0。
     * @param endIndex   结束位置，当结束位置小于0或者大于数组长度，置为数组长度。
     * @throws IllegalArgumentException array对象不为数组抛出此异常
     * @throws NullPointerException     array对象为null
     */
    public ArrayIterator(final Object array, final int startIndex, final int endIndex) {
        this.endIndex = Array.getLength(array);
        if (endIndex > 0 && endIndex < this.endIndex) {
            this.endIndex = endIndex;
        }

        if (startIndex >= 0 && startIndex < this.endIndex) {
            this.startIndex = startIndex;
        }
        this.array = array;
        this.index = this.startIndex;
    }

    @Override
    public boolean hasNext() {
        return (index < endIndex);
    }

    @Override
    public E next() {
        if (hasNext() == false) {
            throw new NoSuchElementException();
        }
        return (E) Array.get(array, index++);
    }

    /**
     * 不允许操作数组元素
     *
     * @throws UnsupportedOperationException always
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() method is not supported");
    }

    /**
     * 获得原始数组对象
     *
     * @return 原始数组对象
     */
    public Object getArray() {
        return array;
    }

    /**
     * 重置数组位置
     */
    public void reset() {
        this.index = this.startIndex;
    }

    @Override
    public Iterator<E> iterator() {
        return this;
    }

}
