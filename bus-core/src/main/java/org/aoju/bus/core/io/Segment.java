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
package org.aoju.bus.core.io;

import org.aoju.bus.core.lang.Normal;

/**
 * 缓冲区的一段
 * 缓冲区中的每个段都是一个循环链表节点,它引用以下内容和
 * 缓冲区中前面的段
 * 池中的每个段都是一个单链列表节点,引用池
 * 段的底层字节数组可以在缓冲区和字节字符串之间共享 当一个
 * 段不能回收,也不能改变它的字节数据
 * 唯一的例外是允许所有者段附加到段中,写入数据
 * {@code limit}及以上 每个字节数组都有一个单独的拥有段 的立场,
 * 限制、prev和next引用不共享
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public final class Segment {

    /**
     * 所有段的大小(以字节为单位)
     */
    public static final int SIZE = 8192;

    /**
     * 这样做避免了这么多字节的{@code arraycopy()}时，将被共享
     */
    public static final int SHARE_MINIMUM = Normal._1024;

    public final byte[] data;

    /**
     * 此段中要读取的应用程序数据字节的下一个字节.
     */
    public int pos;

    /**
     * 准备写入的可用数据的第一个字节.
     */
    public int limit;

    /**
     * 如果其他段或字节字符串使用相同的字节数组，则为真.
     */
    public boolean shared;

    /**
     * 如果这个段拥有字节数组并可以向其追加，则为True，扩展{@code limit}.
     */
    public boolean owner;

    /**
     * 链表或循环链表中的下一段.
     */
    public Segment next;

    /**
     * 循环链表中的前一段.
     */
    public Segment prev;

    Segment() {
        this.data = new byte[SIZE];
        this.owner = true;
        this.shared = false;
    }

    Segment(byte[] data, int pos, int limit, boolean shared, boolean owner) {
        this.data = data;
        this.pos = pos;
        this.limit = limit;
        this.shared = shared;
        this.owner = owner;
    }

    public final Segment sharedCopy() {
        shared = true;
        return new Segment(data, pos, limit, true, false);
    }

    public final Segment unsharedCopy() {
        return new Segment(data.clone(), pos, limit, false, true);
    }

    public final Segment pop() {
        Segment result = next != this ? next : null;
        prev.next = next;
        next.prev = prev;
        next = null;
        prev = null;
        return result;
    }

    public final Segment push(Segment segment) {
        segment.prev = this;
        segment.next = next;
        next.prev = segment;
        next = segment;
        return segment;
    }

    public final Segment split(int byteCount) {
        if (byteCount <= 0 || byteCount > limit - pos) throw new IllegalArgumentException();
        Segment prefix;

        if (byteCount >= SHARE_MINIMUM) {
            prefix = sharedCopy();
        } else {
            prefix = LifeCycle.take();
            System.arraycopy(data, pos, prefix.data, 0, byteCount);
        }

        prefix.limit = prefix.pos + byteCount;
        pos += byteCount;
        prev.push(prefix);
        return prefix;
    }

    public final void compact() {
        if (prev == this) {
            throw new IllegalStateException();
        }
        if (!prev.owner) {
            return;
        }
        int byteCount = limit - pos;
        int availableByteCount = SIZE - prev.limit + (prev.shared ? 0 : prev.pos);
        if (byteCount > availableByteCount) {
            return;
        }
        writeTo(prev, byteCount);
        pop();
        LifeCycle.recycle(this);
    }

    public final void writeTo(Segment sink, int byteCount) {
        if (!sink.owner) throw new IllegalArgumentException();
        if (sink.limit + byteCount > SIZE) {
            if (sink.shared) throw new IllegalArgumentException();
            if (sink.limit + byteCount - sink.pos > SIZE) throw new IllegalArgumentException();
            System.arraycopy(sink.data, sink.pos, sink.data, 0, sink.limit - sink.pos);
            sink.limit -= sink.pos;
            sink.pos = 0;
        }

        System.arraycopy(data, pos, sink.data, sink.limit, byteCount);
        sink.limit += byteCount;
        pos += byteCount;
    }

}
