/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.codec.provider;

import org.aoju.bus.core.codec.Decoder;
import org.aoju.bus.core.codec.Encoder;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.toolkit.ArrayKit;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Base62编码解码实现，常用于短URL
 * From https://github.com/seruco/base62
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Base62Provider implements Encoder<byte[], byte[]>, Decoder<byte[], byte[]>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final int STANDARD_BASE = 256;
    private static final int TARGET_BASE = 62;

    /**
     * 实例
     */
    public static Base62Provider INSTANCE = new Base62Provider();

    /**
     * 按照字典转换bytes
     *
     * @param indices    内容
     * @param dictionary 字典
     * @return 转换值
     */
    private static byte[] translate(final byte[] indices, final byte[] dictionary) {
        final byte[] translation = new byte[indices.length];

        for (int i = 0; i < indices.length; i++) {
            translation[i] = dictionary[indices[i]];
        }

        return translation;
    }

    /**
     * 使用定义的字母表从源基准到目标基准
     *
     * @param message    消息bytes
     * @param sourceBase 源基准长度
     * @param targetBase 目标基准长度
     * @return 计算结果
     */
    private static byte[] convert(final byte[] message, final int sourceBase, final int targetBase) {
        // 计算结果长度，算法来自：http://codegolf.stackexchange.com/a/21672
        final int estimatedLength = estimateOutputLength(message.length, sourceBase, targetBase);

        final ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);

        byte[] source = message;

        while (source.length > 0) {
            final ByteArrayOutputStream quotient = new ByteArrayOutputStream(source.length);

            int remainder = 0;

            for (final byte b : source) {
                final int accumulator = (b & 0xFF) + remainder * sourceBase;
                final int digit = (accumulator - (accumulator % targetBase)) / targetBase;

                remainder = accumulator % targetBase;

                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit);
                }
            }

            out.write(remainder);

            source = quotient.toByteArray();
        }

        // pad output with zeroes corresponding to the number of leading zeroes in the message
        for (int i = 0; i < message.length - 1 && message[i] == 0; i++) {
            out.write(0);
        }

        return ArrayKit.reverse(out.toByteArray());
    }

    /**
     * 估算结果长度
     *
     * @param inputLength 输入长度
     * @param sourceBase  源基准长度
     * @param targetBase  目标基准长度
     * @return 估算长度
     */
    private static int estimateOutputLength(final int inputLength, final int sourceBase, final int targetBase) {
        return (int) Math.ceil((Math.log(sourceBase) / Math.log(targetBase)) * inputLength);
    }

    /**
     * 编码指定消息bytes为Base62格式的bytes
     *
     * @param data 被编码的消息
     * @return Base62内容
     */
    @Override
    public byte[] encode(final byte[] data) {
        return encode(data, false);
    }

    /**
     * 编码指定消息bytes为Base62格式的bytes
     *
     * @param data        被编码的消息
     * @param useInverted 是否使用反转风格，即将GMP风格中的大小写做转换
     * @return Base62内容
     */
    public byte[] encode(final byte[] data, final boolean useInverted) {
        final Base62Encoder encoder = useInverted ? Base62Encoder.INVERTED_ENCODER : Base62Encoder.GMP_ENCODER;
        return encoder.encode(data);
    }

    /**
     * 解码Base62消息
     *
     * @param encoded Base62内容
     * @return 消息
     */
    @Override
    public byte[] decode(final byte[] encoded) {
        return decode(encoded, false);
    }

    /**
     * 解码Base62消息
     *
     * @param encoded     Base62内容
     * @param useInverted 是否使用反转风格，即将GMP风格中的大小写做转换
     * @return 消息
     */
    public byte[] decode(final byte[] encoded, final boolean useInverted) {
        final Base62Decoder decoder = useInverted ? Base62Decoder.INVERTED_DECODER : Base62Decoder.GMP_DECODER;
        return decoder.decode(encoded);
    }

    /**
     * Base62编码器
     */
    public static class Base62Encoder implements Encoder<byte[], byte[]> {

        /**
         * GMP 编码器
         */
        public static Base62Encoder GMP_ENCODER = new Base62Encoder(Normal.UPPER_LOWER_NUMBER.getBytes());
        /**
         * INVERTED 编码器
         */
        public static Base62Encoder INVERTED_ENCODER = new Base62Encoder(Normal.LOWER_UPPER_NUMBER.getBytes());
        /**
         * 字符信息
         */
        private final byte[] alphabet;

        /**
         * 构造
         *
         * @param alphabet 字符表
         */
        public Base62Encoder(final byte[] alphabet) {
            this.alphabet = alphabet;
        }

        @Override
        public byte[] encode(final byte[] data) {
            final byte[] indices = convert(data, STANDARD_BASE, TARGET_BASE);
            return translate(indices, alphabet);
        }
    }

    /**
     * Base62解码器
     */
    public static class Base62Decoder implements Decoder<byte[], byte[]> {

        /**
         * GMP 解码器
         */
        public static Base62Decoder GMP_DECODER = new Base62Decoder(Normal.UPPER_LOWER_NUMBER.getBytes());
        /**
         * INVERTED 解码器
         */
        public static Base62Decoder INVERTED_DECODER = new Base62Decoder(Normal.LOWER_UPPER_NUMBER.getBytes());
        /**
         * 查找表
         */
        private final byte[] lookupTable;

        /**
         * 构造
         *
         * @param alphabet 字母表
         */
        public Base62Decoder(final byte[] alphabet) {
            lookupTable = new byte['z' + 1];
            for (int i = 0; i < alphabet.length; i++) {
                lookupTable[alphabet[i]] = (byte) i;
            }
        }


        @Override
        public byte[] decode(final byte[] encoded) {
            final byte[] prepared = translate(encoded, lookupTable);
            return convert(prepared, TARGET_BASE, STANDARD_BASE);
        }
    }

}
