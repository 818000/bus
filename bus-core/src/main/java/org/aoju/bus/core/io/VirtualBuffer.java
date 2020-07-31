/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
 ********************************************************************************/
package org.aoju.bus.core.io;

import java.nio.ByteBuffer;

/**
 * 虚拟ByteBuffer缓冲区
 *
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
public final class VirtualBuffer {

    /**
     * 当前虚拟buffer的归属内存页
     */
    private final PageBuffer pageBuffer;
    /**
     * 通过ByteBuffer.slice()隐射出来的虚拟ByteBuffer
     *
     * @see ByteBuffer#slice()
     */
    private ByteBuffer buffer;
    private boolean clean = false;
    /**
     * 当前虚拟buffer映射的实际buffer.position
     */
    private int parentPosition;

    /**
     * 当前虚拟buffer映射的实际buffer.limit
     */
    private int parentLimit;

    VirtualBuffer(PageBuffer pageBuffer, ByteBuffer buffer, int parentPosition, int parentLimit) {
        this.pageBuffer = pageBuffer;
        this.buffer = buffer;
        this.parentPosition = parentPosition;
        this.parentLimit = parentLimit;
    }

    int getParentPosition() {
        return parentPosition;
    }

    void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
    }

    int getParentLimit() {
        return parentLimit;
    }

    void setParentLimit(int parentLimit) {
        this.parentLimit = parentLimit;
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    void buffer(ByteBuffer buffer) {
        this.buffer = buffer;
        clean = false;
    }

    public void clean() {
        if (clean) {
            System.err.println("buffer has cleaned");
            throw new RuntimeException();
        }
        clean = true;
        if (pageBuffer != null) {
            pageBuffer.clean(this);
        }
    }

}
