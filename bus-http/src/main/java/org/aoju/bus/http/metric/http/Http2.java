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
package org.aoju.bus.http.metric.http;

import org.aoju.bus.core.io.ByteString;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.StringKit;

import java.io.IOException;

/**
 * @author Kimi Liu
 * @version 6.0.8
 * @since JDK 1.8+
 */
public final class Http2 {

    static final ByteString CONNECTION_PREFACE = ByteString.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");

    /**
     * 初始最大帧大小，应用于独立写入，或从同行读取.
     */
    static final int INITIAL_MAX_FRAME_SIZE = 0x4000; // 16384

    static final byte TYPE_DATA = 0x0;
    static final byte TYPE_HEADERS = 0x1;
    static final byte TYPE_PRIORITY = 0x2;
    static final byte TYPE_RST_STREAM = 0x3;
    static final byte TYPE_SETTINGS = 0x4;
    static final byte TYPE_PUSH_PROMISE = 0x5;
    static final byte TYPE_PING = 0x6;
    static final byte TYPE_GOAWAY = 0x7;
    static final byte TYPE_WINDOW_UPDATE = 0x8;
    static final byte TYPE_CONTINUATION = 0x9;

    static final byte FLAG_NONE = 0x0;
    static final byte FLAG_ACK = 0x1; // Used for settings and ping.
    static final byte FLAG_END_STREAM = 0x1; // Used for headers and data.
    static final byte FLAG_END_HEADERS = 0x4; // Used for headers and continuation.
    static final byte FLAG_END_PUSH_PROMISE = 0x4;
    static final byte FLAG_PADDED = 0x8; // Used for headers and data.
    static final byte FLAG_PRIORITY = 0x20; // Used for headers.
    static final byte FLAG_COMPRESSED = 0x20; // Used for data.
    /**
     * 查找表的有效标志的数据，报头，延续。无效的组合用二进制表示.
     */
    static final String[] FLAGS = new String[0x40];
    static final String[] BINARY = new String[256];
    /**
     * 查找有效框架类型的表
     */
    private static final String[] FRAME_NAMES = new String[]{
            "DATA",
            "HEADERS",
            "PRIORITY",
            "RST_STREAM",
            "SETTINGS",
            "PUSH_PROMISE",
            "PING",
            "GOAWAY",
            "WINDOW_UPDATE",
            "CONTINUATION"
    };

    static {
        for (int i = 0; i < BINARY.length; i++) {
            BINARY[i] = StringKit.format("%8s", Integer.toBinaryString(i)).replace(Symbol.C_SPACE, Symbol.C_ZERO);
        }

        FLAGS[FLAG_NONE] = Normal.EMPTY;
        FLAGS[FLAG_END_STREAM] = "END_STREAM";

        int[] prefixFlags = new int[]{FLAG_END_STREAM};

        FLAGS[FLAG_PADDED] = "PADDED";
        for (int prefixFlag : prefixFlags) {
            FLAGS[prefixFlag | FLAG_PADDED] = FLAGS[prefixFlag] + "|PADDED";
        }

        FLAGS[FLAG_END_HEADERS] = "END_HEADERS"; // Same as END_PUSH_PROMISE.
        FLAGS[FLAG_PRIORITY] = "PRIORITY"; // Same as FLAG_COMPRESSED.
        FLAGS[FLAG_END_HEADERS | FLAG_PRIORITY] = "END_HEADERS|PRIORITY"; // Only valid on HEADERS.
        int[] frameFlags = new int[]{
                FLAG_END_HEADERS, FLAG_PRIORITY, FLAG_END_HEADERS | FLAG_PRIORITY
        };

        for (int frameFlag : frameFlags) {
            for (int prefixFlag : prefixFlags) {
                FLAGS[prefixFlag | frameFlag] = FLAGS[prefixFlag] + Symbol.C_OR + FLAGS[frameFlag];
                FLAGS[prefixFlag | frameFlag | FLAG_PADDED]
                        = FLAGS[prefixFlag] + Symbol.C_OR + FLAGS[frameFlag] + "|PADDED";
            }
        }

        for (int i = 0; i < FLAGS.length; i++) {
            if (FLAGS[i] == null) FLAGS[i] = BINARY[i];
        }
    }

    private Http2() {
    }

    static IllegalArgumentException illegalArgument(String message, Object... args) {
        throw new IllegalArgumentException(StringKit.format(message, args));
    }

    static IOException ioException(String message, Object... args) throws IOException {
        throw new IOException(StringKit.format(message, args));
    }

    static String frameLog(boolean inbound, int streamId, int length, byte type, byte flags) {
        String formattedType = type < FRAME_NAMES.length ? FRAME_NAMES[type] : StringKit.format("0x%02x", type);
        String formattedFlags = formatFlags(type, flags);
        return StringKit.format("%s 0x%08x %5d %-13s %s", inbound ? "<<" : ">>", streamId, length,
                formattedType, formattedFlags);
    }

    static String formatFlags(byte type, byte flags) {
        if (flags == 0) {
            return Normal.EMPTY;
        }
        switch (type) {
            case TYPE_SETTINGS:
            case TYPE_PING:
                return flags == FLAG_ACK ? "ACK" : BINARY[flags];
            case TYPE_PRIORITY:
            case TYPE_RST_STREAM:
            case TYPE_GOAWAY:
            case TYPE_WINDOW_UPDATE:
                return BINARY[flags];
        }
        String result = flags < FLAGS.length ? FLAGS[flags] : BINARY[flags];
        if (type == TYPE_PUSH_PROMISE && (flags & FLAG_END_PUSH_PROMISE) != 0) {
            return result.replace("HEADERS", "PUSH_PROMISE");
        } else if (type == TYPE_DATA && (flags & FLAG_COMPRESSED) != 0) {
            return result.replace("PRIORITY", "COMPRESSED");
        }
        return result;
    }

}
