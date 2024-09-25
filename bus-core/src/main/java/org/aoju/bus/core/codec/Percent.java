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
package org.aoju.bus.core.codec;

import org.aoju.bus.core.codec.provider.Base16Provider;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.ArrayKit;
import org.aoju.bus.core.toolkit.HexKit;
import org.aoju.bus.core.toolkit.StringKit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * 百分号编码(Percent-encoding), 也称作URL编码(URL encoding) 百分号编码可用于URI的编码，也可以用于"application/x-www-form-urlencoded"的MIME准备数据
 * <ul>
 * <li>URI：遵循RFC 3986保留字规范</li>
 * <li>application/x-www-form-urlencoded，遵循W3C HTML Form content types规范，如空格须转+</li>
 * </ul>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Percent implements Encoder<byte[], byte[]>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存放安全编码
     */
    private final BitSet safeCharacters;
    /**
     * 是否编码空格为+ 如果为{@code true}，则将空格编码为"+"，此项只在"application/x-www-form-urlencoded"中使用
     * 如果为{@code false}，则空格编码为"%20",此项一般用于URL的Query部分（RFC3986规范）
     */
    private boolean encodeSpaceAsPlus = false;

    /**
     * 构造 [a-zA-Z0-9]默认不被编码
     */
    public Percent() {
        this(new BitSet(Normal._256));
    }

    /**
     * 构造
     *
     * @param safeCharacters 安全字符，安全字符不被编码
     */
    public Percent(final BitSet safeCharacters) {
        this.safeCharacters = safeCharacters;
    }

    /**
     * 检查给定字符是否为安全字符
     *
     * @param c 字符
     * @return {@code true}表示安全，否则非安全字符
     */
    public boolean isSafe(final char c) {
        return this.safeCharacters.get(c);
    }

    @Override
    public byte[] encode(final byte[] bytes) {
        // 初始容量计算，简单粗暴假设所有byte都需要转义，容量是三倍
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length * 3);
        for (int i = 0; i < bytes.length; i++) {
            encodeTo(buffer, bytes[i]);
        }

        return buffer.array();
    }

    /**
     * 将URL中的字符串编码为%形式
     *
     * @param path           需要编码的字符串
     * @param charset        编码, {@code null}返回原字符串，表示不编码
     * @param customSafeChar 自定义安全字符
     * @return 编码后的字符串
     */
    public String encode(final CharSequence path, final Charset charset, final char... customSafeChar) {
        if (null == charset || StringKit.isEmpty(path)) {
            return StringKit.toString(path);
        }

        final StringBuilder rewrittenPath = new StringBuilder(path.length() * 3);
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        final OutputStreamWriter writer = new OutputStreamWriter(buf, charset);

        char c;
        for (int i = 0; i < path.length(); i++) {
            c = path.charAt(i);
            if (safeCharacters.get(c) || ArrayKit.contains(customSafeChar, c)) {
                rewrittenPath.append(c);
            } else if (encodeSpaceAsPlus && c == Symbol.C_SPACE) {
                // 对于空格单独处理
                rewrittenPath.append(Symbol.C_PLUS);
            } else {
                // convert to external encoding before hex conversion
                try {
                    writer.write(c);
                    writer.flush();
                } catch (final IOException e) {
                    buf.reset();
                    continue;
                }

                // 兼容双字节的Unicode符处理（如部分emoji）
                final byte[] ba = buf.toByteArray();
                for (final byte toEncode : ba) {
                    // Converting each byte in the buffer
                    rewrittenPath.append(Symbol.C_PERCENT);
                    HexKit.appendHex(rewrittenPath, toEncode, false);
                }
                buf.reset();
            }
        }
        return rewrittenPath.toString();
    }

    /**
     * 将单一byte转义到{@link ByteBuffer}中
     *
     * @param buffer {@link ByteBuffer}
     * @param b      字符byte
     */
    private void encodeTo(final ByteBuffer buffer, final byte b) {
        if (safeCharacters.get(b)) {
            // 跳过安全字符
            buffer.put(b);
        } else if (encodeSpaceAsPlus && b == Symbol.C_SPACE) {
            // 对于空格单独处理
            buffer.put((byte) Symbol.C_PLUS);
        } else {
            buffer.put((byte) Symbol.C_PERCENT);
            buffer.put((byte) Base16Provider.CODEC_UPPER.hexDigit(b >> 4));
            buffer.put((byte) Base16Provider.CODEC_UPPER.hexDigit(b));
        }
    }

    /**
     * {@link Percent}构建器 由于{@link Percent}本身应该是只读对象，因此将此对象的构建放在Builder中
     */
    public static class Builder implements org.aoju.bus.core.builder.Builder<Percent> {

        private static final long serialVersionUID = 1L;
        private final Percent codec;

        private Builder(final Percent codec) {
            this.codec = codec;
        }

        /**
         * 从已知Percent创建Percent，会复制给定Percent的安全字符
         *
         * @param codec Percent
         * @return this
         */
        public static Builder of(final Percent codec) {
            return new Builder(new Percent((BitSet) codec.safeCharacters.clone()));
        }

        /**
         * 创建Percent，使用指定字符串中的字符作为安全字符
         *
         * @param chars 安全字符合集
         * @return this
         */
        public static Builder of(final CharSequence chars) {
            Assert.notNull(chars, "chars must not be null");
            final Builder builder = of(new Percent());
            final int length = chars.length();
            for (int i = 0; i < length; i++) {
                builder.addSafe(chars.charAt(i));
            }
            return builder;
        }

        /**
         * 增加安全字符 安全字符不被编码
         *
         * @param c 字符
         * @return this
         */
        public Builder addSafe(final char c) {
            codec.safeCharacters.set(c);
            return this;
        }

        /**
         * 增加安全字符 安全字符不被编码
         *
         * @param chars 安全字符
         * @return this
         */
        public Builder addSafes(final String chars) {
            final int length = chars.length();
            for (int i = 0; i < length; i++) {
                addSafe(chars.charAt(i));
            }
            return this;
        }

        /**
         * 移除安全字符 安全字符不被编码
         *
         * @param c 字符
         * @return this
         */
        public Builder removeSafe(final char c) {
            codec.safeCharacters.clear(c);
            return this;
        }

        /**
         * 增加安全字符到当前的Percent
         *
         * @param other {@link Percent}
         * @return this
         */
        public Builder or(final Percent other) {
            codec.safeCharacters.or(other.safeCharacters);
            return this;
        }

        /**
         * 是否将空格编码为+ 如果为{@code true}，则将空格编码为"+"，此项只在"application/x-www-form-urlencoded"中使用
         * 如果为{@code false}，则空格编码为"%20",此项一般用于URL的Query部分（RFC3986规范）
         *
         * @param encodeSpaceAsPlus 是否将空格编码为+
         * @return this
         */
        public Builder setEncodeSpaceAsPlus(final boolean encodeSpaceAsPlus) {
            codec.encodeSpaceAsPlus = encodeSpaceAsPlus;
            return this;
        }

        @Override
        public Percent build() {
            return codec;
        }
    }

}
