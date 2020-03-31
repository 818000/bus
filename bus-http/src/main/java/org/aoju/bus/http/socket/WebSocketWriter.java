/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.http.socket;

import org.aoju.bus.core.io.*;

import java.io.IOException;
import java.util.Random;

/**
 * RFC 6455兼容的WebSocket帧写入器.
 * 这个类不是线程安全的
 *
 * @author Kimi Liu
 * @version 5.8.2
 * @since JDK 1.8+
 */
final class WebSocketWriter {

    final boolean isClient;
    final Random random;

    final BufferSink sink;

    final Buffer sinkBuffer;
    final Buffer buffer = new Buffer();
    final FrameSink frameSink = new FrameSink();
    private final byte[] maskKey;
    private final Buffer.UnsafeCursor maskCursor;
    boolean writerClosed;
    boolean activeWriter;

    WebSocketWriter(boolean isClient, BufferSink sink, Random random) {
        if (sink == null) throw new NullPointerException("sink == null");
        if (random == null) throw new NullPointerException("random == null");
        this.isClient = isClient;
        this.sink = sink;
        this.sinkBuffer = sink.buffer();
        this.random = random;

        maskKey = isClient ? new byte[4] : null;
        maskCursor = isClient ? new Buffer.UnsafeCursor() : null;
    }

    void writePing(ByteString payload) throws IOException {
        writeControlFrame(WebSocketProtocol.OPCODE_CONTROL_PING, payload);
    }

    void writePong(ByteString payload) throws IOException {
        writeControlFrame(WebSocketProtocol.OPCODE_CONTROL_PONG, payload);
    }

    /**
     * 发送带有可选代码和原因的关闭帧.
     *
     * @param code   RFC 6455或{@code 0}第7.4节定义的状态码。
     * @param reason 关闭或{@code null}的原因.
     * @throws IOException 异常
     */
    void writeClose(int code, ByteString reason) throws IOException {
        ByteString payload = ByteString.EMPTY;
        if (code != 0 || reason != null) {
            if (code != 0) {
                WebSocketProtocol.validateCloseCode(code);
            }
            Buffer buffer = new Buffer();
            buffer.writeShort(code);
            if (reason != null) {
                buffer.write(reason);
            }
            payload = buffer.readByteString();
        }

        try {
            writeControlFrame(WebSocketProtocol.OPCODE_CONTROL_CLOSE, payload);
        } finally {
            writerClosed = true;
        }
    }

    private void writeControlFrame(int opcode, ByteString payload) throws IOException {
        if (writerClosed) throw new IOException("closed");

        int length = payload.size();
        if (length > WebSocketProtocol.PAYLOAD_BYTE_MAX) {
            throw new IllegalArgumentException(
                    "Payload size must be less than or equal to " + WebSocketProtocol.PAYLOAD_BYTE_MAX);
        }

        int b0 = WebSocketProtocol.B0_FLAG_FIN | opcode;
        sinkBuffer.writeByte(b0);

        int b1 = length;
        if (isClient) {
            b1 |= WebSocketProtocol.B1_FLAG_MASK;
            sinkBuffer.writeByte(b1);

            random.nextBytes(maskKey);
            sinkBuffer.write(maskKey);

            if (length > 0) {
                long payloadStart = sinkBuffer.size();
                sinkBuffer.write(payload);

                sinkBuffer.readAndWriteUnsafe(maskCursor);
                maskCursor.seek(payloadStart);
                WebSocketProtocol.toggleMask(maskCursor, maskKey);
                maskCursor.close();
            }
        } else {
            sinkBuffer.writeByte(b1);
            sinkBuffer.write(payload);
        }

        sink.flush();
    }

    Sink newMessageSink(int formatOpcode, long contentLength) {
        if (activeWriter) {
            throw new IllegalStateException("Another message writer is active. Did you call close()?");
        }
        activeWriter = true;

        frameSink.formatOpcode = formatOpcode;
        frameSink.contentLength = contentLength;
        frameSink.isFirstFrame = true;
        frameSink.closed = false;

        return frameSink;
    }

    void writeMessageFrame(int formatOpcode, long byteCount, boolean isFirstFrame,
                           boolean isFinal) throws IOException {
        if (writerClosed) throw new IOException("closed");

        int b0 = isFirstFrame ? formatOpcode : WebSocketProtocol.OPCODE_CONTINUATION;
        if (isFinal) {
            b0 |= WebSocketProtocol.B0_FLAG_FIN;
        }
        sinkBuffer.writeByte(b0);

        int b1 = 0;
        if (isClient) {
            b1 |= WebSocketProtocol.B1_FLAG_MASK;
        }
        if (byteCount <= WebSocketProtocol.PAYLOAD_BYTE_MAX) {
            b1 |= (int) byteCount;
            sinkBuffer.writeByte(b1);
        } else if (byteCount <= WebSocketProtocol.PAYLOAD_SHORT_MAX) {
            b1 |= WebSocketProtocol.PAYLOAD_SHORT;
            sinkBuffer.writeByte(b1);
            sinkBuffer.writeShort((int) byteCount);
        } else {
            b1 |= WebSocketProtocol.PAYLOAD_LONG;
            sinkBuffer.writeByte(b1);
            sinkBuffer.writeLong(byteCount);
        }

        if (isClient) {
            random.nextBytes(maskKey);
            sinkBuffer.write(maskKey);

            if (byteCount > 0) {
                long bufferStart = sinkBuffer.size();
                sinkBuffer.write(buffer, byteCount);

                sinkBuffer.readAndWriteUnsafe(maskCursor);
                maskCursor.seek(bufferStart);
                WebSocketProtocol.toggleMask(maskCursor, maskKey);
                maskCursor.close();
            }
        } else {
            sinkBuffer.write(buffer, byteCount);
        }

        sink.emit();
    }

    final class FrameSink implements Sink {
        int formatOpcode;
        long contentLength;
        boolean isFirstFrame;
        boolean closed;

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            if (closed) throw new IOException("closed");

            buffer.write(source, byteCount);

            boolean deferWrite = isFirstFrame
                    && contentLength != -1
                    && buffer.size() > contentLength - 8192;

            long emitCount = buffer.completeSegmentByteCount();
            if (emitCount > 0 && !deferWrite) {
                writeMessageFrame(formatOpcode, emitCount, isFirstFrame, false);
                isFirstFrame = false;
            }
        }

        @Override
        public void flush() throws IOException {
            if (closed) throw new IOException("closed");

            writeMessageFrame(formatOpcode, buffer.size(), isFirstFrame, false);
            isFirstFrame = false;
        }

        @Override
        public Timeout timeout() {
            return sink.timeout();
        }

        @Override
        public void close() throws IOException {
            if (closed) throw new IOException("closed");

            writeMessageFrame(formatOpcode, buffer.size(), isFirstFrame, true);
            closed = true;
            activeWriter = false;
        }
    }

}
