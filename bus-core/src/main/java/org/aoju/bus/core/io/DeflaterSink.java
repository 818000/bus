/*
 * The MIT License
 *
 * Copyright (c) 2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.core.io;


import org.aoju.bus.core.utils.IoUtils;

import java.io.IOException;
import java.util.zip.Deflater;

/**
 * 这种流体的强冲刷可能导致压缩降低 每一个
 * 调用{@link #flush}立即压缩所有当前缓存的数据;
 * 这种早期压缩可能不如执行的压缩有效
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public final class DeflaterSink implements Sink {

    private final BufferSink sink;
    private final Deflater deflater;
    private boolean closed;

    public DeflaterSink(Sink sink, Deflater deflater) {
        this(IoUtils.buffer(sink), deflater);
    }

    DeflaterSink(BufferSink sink, Deflater deflater) {
        if (sink == null) throw new IllegalArgumentException("source == null");
        if (deflater == null) throw new IllegalArgumentException("inflater == null");
        this.sink = sink;
        this.deflater = deflater;
    }

    @Override
    public void write(Buffer source, long byteCount) throws IOException {
        IoUtils.checkOffsetAndCount(source.size, 0, byteCount);
        while (byteCount > 0) {
            Segment head = source.head;
            int toDeflate = (int) Math.min(byteCount, head.limit - head.pos);
            deflater.setInput(head.data, head.pos, toDeflate);

            deflate(false);

            source.size -= toDeflate;
            head.pos += toDeflate;
            if (head.pos == head.limit) {
                source.head = head.pop();
                LifeCycle.recycle(head);
            }

            byteCount -= toDeflate;
        }
    }

    private void deflate(boolean syncFlush) throws IOException {
        Buffer buffer = sink.buffer();
        while (true) {
            Segment s = buffer.writableSegment(1);

            int deflated = syncFlush
                    ? deflater.deflate(s.data, s.limit, Segment.SIZE - s.limit, Deflater.SYNC_FLUSH)
                    : deflater.deflate(s.data, s.limit, Segment.SIZE - s.limit);

            if (deflated > 0) {
                s.limit += deflated;
                buffer.size += deflated;
                sink.emitCompleteSegments();
            } else if (deflater.needsInput()) {
                if (s.pos == s.limit) {
                    buffer.head = s.pop();
                    LifeCycle.recycle(s);
                }
                return;
            }
        }
    }

    @Override
    public void flush() throws IOException {
        deflate(true);
        sink.flush();
    }

    void finishDeflate() throws IOException {
        deflater.finish();
        deflate(false);
    }

    @Override
    public void close() {
        if (closed) return;

        Throwable thrown = null;
        try {
            finishDeflate();
        } catch (Throwable e) {
            thrown = e;
        }

        try {
            deflater.end();
        } catch (Throwable e) {
            if (thrown == null) thrown = e;
        }

        try {
            sink.close();
        } catch (Throwable e) {
            if (thrown == null) thrown = e;
        }
        closed = true;

        if (thrown != null) IoUtils.sneakyRethrow(thrown);
    }

    @Override
    public Timeout timeout() {
        return sink.timeout();
    }

    @Override
    public String toString() {
        return "DeflaterSink(" + sink + ")";
    }

}
