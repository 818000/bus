/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org sandao and other contributors.               *
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
package org.aoju.bus.socket.protocol;

import org.aoju.bus.socket.AioSession;
import org.aoju.bus.socket.Protocol;
import org.aoju.bus.socket.convert.FixedLengthFrameDecoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class StringProtocol implements Protocol<String> {

    private final Charset charset;

    private final HashMap<AioSession, FixedLengthFrameDecoder> decoderMap = new HashMap<>();
    private long lastClearTime = System.currentTimeMillis();

    public StringProtocol(Charset charset) {
        this.charset = charset;
    }

    public StringProtocol() {
        this(StandardCharsets.UTF_8);
    }

    @Override
    public String decode(ByteBuffer readBuffer, AioSession session) {
        if (System.currentTimeMillis() - lastClearTime > 5000) {
            lastClearTime = System.currentTimeMillis();
            decoderMap.keySet().stream().filter(AioSession::isInvalid).forEach(decoderMap::remove);
        }
        FixedLengthFrameDecoder decoder = decoderMap.get(session);
        //消息长度超过缓冲区容量
        if (decoder != null) {
            String content = bigContent(readBuffer, decoder);
            //解码成功,释放解码器
            if (content != null) {
                decoderMap.remove(session);
            }
            return content;
        }

        int remaining = readBuffer.remaining();
        if (remaining < Integer.BYTES) {
            return null;
        }
        readBuffer.mark();
        int length = readBuffer.getInt();
        //消息长度超过缓冲区容量引发的半包,启用定长消息解码器,本次解码失败
        if (length + Integer.BYTES > readBuffer.capacity()) {
            FixedLengthFrameDecoder fixedLengthFrameDecoder = new FixedLengthFrameDecoder(length);
            decoderMap.put(session, fixedLengthFrameDecoder);
            return null;
        }
        //半包，解码失败
        if (length > readBuffer.remaining()) {
            readBuffer.reset();
            return null;
        }
        return convert(readBuffer, length);
    }

    /**
     * 大消息体解码
     */
    private String bigContent(ByteBuffer readBuffer, FixedLengthFrameDecoder decoder) {
        if (!decoder.decode(readBuffer)) {
            return null;
        }
        ByteBuffer byteBuffer = decoder.getBuffer();
        return convert(byteBuffer, byteBuffer.capacity());
    }

    /**
     * 消息解码
     */
    private String convert(ByteBuffer byteBuffer, int length) {
        byte[] b = new byte[length];
        byteBuffer.get(b);
        return new String(b, charset);
    }

}
