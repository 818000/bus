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
package org.aoju.bus.socket.origin.plugins;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 指定结束标识的解码器
 *
 * @author Kimi Liu
 * @version 5.8.5
 * @since JDK 1.8+
 */
public class DelimiterFrame implements SmartDecoder {
    /**
     * 消息结束标志
     */
    private byte[] endFLag;
    /**
     * 期望本次校验的结束标索引位置
     */
    private int exceptIndex;
    /**
     * 存储已解析的数据
     */
    private List<ByteBuffer> bufferList;
    /**
     * 是否解析完成
     */
    private boolean finishRead;

    private int position;

    public DelimiterFrame(byte[] endFLag, int unitBufferSize) {
        this.endFLag = endFLag;
        bufferList = new ArrayList<>();
        bufferList.add(ByteBuffer.allocate(unitBufferSize));
    }

    public boolean decode(ByteBuffer byteBuffer) {
        if (finishRead) {
            throw new RuntimeException("delimiter has finish read");
        }
        ByteBuffer preBuffer = bufferList.get(position);

        while (byteBuffer.hasRemaining()) {
            if (!preBuffer.hasRemaining()) {
                preBuffer.flip();
                position++;
                if (position < bufferList.size()) {
                    preBuffer = bufferList.get(position);
                    preBuffer.clear();
                } else {
                    preBuffer = ByteBuffer.allocate(preBuffer.capacity());
                    bufferList.add(preBuffer);
                }
            }
            byte data = byteBuffer.get();
            preBuffer.put(data);
            if (data != endFLag[exceptIndex]) {
                exceptIndex = 0;
            } else if (++exceptIndex == endFLag.length) {
                preBuffer.flip();
                finishRead = true;
                break;
            }
        }

        return finishRead;
    }

    @Override
    public ByteBuffer getBuffer() {
        if (position == 0) {
            return bufferList.get(position);
        }
        byte[] data = new byte[(position) * bufferList.get(0).capacity() + bufferList.get(position).limit()];
        int index = 0;
        for (int i = 0; i < position; i++) {
            ByteBuffer b = bufferList.get(i);
            System.arraycopy(b.array(), b.position(), data, index, b.remaining());
            index += b.remaining();
        }
        ByteBuffer lastBuffer = bufferList.get(position);
        System.arraycopy(lastBuffer.array(), lastBuffer.position(), data, index, lastBuffer.remaining());
        return ByteBuffer.wrap(data);
    }

    /**
     * 重置解码器
     */
    public void reset() {
        reset(null);
    }

    /**
     * 重置解码器
     *
     * @param endFLag 更新结束标志
     */
    public void reset(byte[] endFLag) {
        if (endFLag != null) {
            this.endFLag = endFLag;
        }
        finishRead = false;
        exceptIndex = 0;
        position = 0;
        bufferList.get(position).clear();
    }
}
