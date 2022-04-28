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
package org.aoju.bus.crypto.digest.mac;

import org.aoju.bus.core.exception.CryptoException;
import org.aoju.bus.core.toolkit.IoKit;

import java.io.IOException;
import java.io.InputStream;

/**
 * MAC(Message Authentication Code)算法引擎
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public interface MacEngine {

    /**
     * 加入需要被摘要的内容
     *
     * @param in 内容
     */
    default void update(byte[] in) {
        update(in, 0, in.length);
    }

    /**
     * 加入需要被摘要的内容
     *
     * @param in    内容
     * @param inOff 内容起始位置
     * @param len   内容长度
     */
    void update(byte[] in, int inOff, int len);

    /**
     * 结束并生成摘要
     *
     * @return 摘要内容
     */
    byte[] doFinal();

    /**
     * 重置
     */
    void reset();

    /**
     * 生成摘要
     *
     * @param data         {@link InputStream} 数据流
     * @param bufferLength 缓存长度，不足1使用 {@link  IoKit#DEFAULT_BUFFER_SIZE} 做为默认值
     * @return 摘要bytes
     */
    default byte[] digest(InputStream data, int bufferLength) {
        if (bufferLength < 1) {
            bufferLength = IoKit.DEFAULT_BUFFER_SIZE;
        }

        final byte[] buffer = new byte[bufferLength];

        byte[] result;
        try {
            int read = data.read(buffer, 0, bufferLength);

            while (read > -1) {
                update(buffer, 0, read);
                read = data.read(buffer, 0, bufferLength);
            }
            result = doFinal();
        } catch (IOException e) {
            throw new CryptoException(e);
        } finally {
            reset();
        }
        return result;
    }

    /**
     * 获取MAC算法块大小
     *
     * @return MAC算法块大小
     */
    int getMacLength();

    /**
     * 获取当前算法
     *
     * @return 算法
     */
    String getAlgorithm();

}
