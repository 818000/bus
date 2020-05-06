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
package org.aoju.bus.core.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * @author Kimi Liu
 * @version 5.8.9
 * @since JDK 1.8+
 */
public class ByteUtils {


    public static int bytesToVR(byte[] bytes, int off) {
        return bytesToUShortBE(bytes, off);
    }

    public static int bytesToUShort(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToUShortBE(bytes, off)
                : bytesToUShortLE(bytes, off);
    }

    public static int bytesToUShortBE(byte[] bytes, int off) {
        return ((bytes[off] & 255) << 8) + (bytes[off + 1] & 255);
    }

    public static int bytesToUShortLE(byte[] bytes, int off) {
        return ((bytes[off + 1] & 255) << 8) + (bytes[off] & 255);
    }

    public static int bytesToShort(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToShortBE(bytes, off)
                : bytesToShortLE(bytes, off);
    }

    public static int bytesToShortBE(byte[] bytes, int off) {
        return (bytes[off] << 8) + (bytes[off + 1] & 255);
    }

    public static int bytesToShortLE(byte[] bytes, int off) {
        return (bytes[off + 1] << 8) + (bytes[off] & 255);
    }

    public static void bytesToShorts(byte[] b, short[] s, int off, int len, boolean bigEndian) {
        if (bigEndian)
            bytesToShortsBE(b, s, off, len);
        else
            bytesToShortsLE(b, s, off, len);
    }

    public static void bytesToShortsLE(byte[] b, short[] s, int off, int len) {
        int boff = 0;
        for (int j = 0; j < len; j++) {
            int b0 = b[boff + 1];
            int b1 = b[boff] & 0xff;
            s[off + j] = (short) ((b0 << 8) | b1);
            boff += 2;
        }
    }

    public static void bytesToShortsBE(byte[] b, short[] s, int off, int len) {
        int boff = 0;
        for (int j = 0; j < len; j++) {
            int b0 = b[boff];
            int b1 = b[boff + 1] & 0xff;
            s[off + j] = (short) ((b0 << 8) | b1);
            boff += 2;
        }
    }

    public static int bytesToInt(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToIntBE(bytes, off) : bytesToIntLE(bytes, off);
    }

    public static int bytesToIntBE(byte[] bytes, int off) {
        return (bytes[off] << 24) + ((bytes[off + 1] & 255) << 16)
                + ((bytes[off + 2] & 255) << 8) + (bytes[off + 3] & 255);
    }

    public static int bytesToIntLE(byte[] bytes, int off) {
        return (bytes[off + 3] << 24) + ((bytes[off + 2] & 255) << 16)
                + ((bytes[off + 1] & 255) << 8) + (bytes[off] & 255);
    }

    public static int bytesToTag(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToTagBE(bytes, off) : bytesToTagLE(bytes, off);
    }

    public static int bytesToTagBE(byte[] bytes, int off) {
        return bytesToIntBE(bytes, off);
    }

    public static int bytesToTagLE(byte[] bytes, int off) {
        return (bytes[off + 1] << 24) + ((bytes[off] & 255) << 16)
                + ((bytes[off + 3] & 255) << 8) + (bytes[off + 2] & 255);
    }

    public static float bytesToFloat(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToFloatBE(bytes, off)
                : bytesToFloatLE(bytes, off);
    }

    public static float bytesToFloatBE(byte[] bytes, int off) {
        return Float.intBitsToFloat(bytesToIntBE(bytes, off));
    }

    public static float bytesToFloatLE(byte[] bytes, int off) {
        return Float.intBitsToFloat(bytesToIntLE(bytes, off));
    }

    public static long bytesToLong(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToLongBE(bytes, off)
                : bytesToLongLE(bytes, off);
    }

    public static long bytesToLongBE(byte[] bytes, int off) {
        return ((long) bytes[off] << 56)
                + ((long) (bytes[off + 1] & 255) << 48)
                + ((long) (bytes[off + 2] & 255) << 40)
                + ((long) (bytes[off + 3] & 255) << 32)
                + ((long) (bytes[off + 4] & 255) << 24)
                + ((bytes[off + 5] & 255) << 16)
                + ((bytes[off + 6] & 255) << 8)
                + (bytes[off + 7] & 255);
    }

    public static long bytesToLongLE(byte[] bytes, int off) {
        return ((long) bytes[off + 7] << 56)
                + ((long) (bytes[off + 6] & 255) << 48)
                + ((long) (bytes[off + 5] & 255) << 40)
                + ((long) (bytes[off + 4] & 255) << 32)
                + ((long) (bytes[off + 3] & 255) << 24)
                + ((bytes[off + 2] & 255) << 16)
                + ((bytes[off + 1] & 255) << 8)
                + (bytes[off] & 255);
    }

    public static double bytesToDouble(byte[] bytes, int off, boolean bigEndian) {
        return bigEndian ? bytesToDoubleBE(bytes, off)
                : bytesToDoubleLE(bytes, off);
    }

    public static double bytesToDoubleBE(byte[] bytes, int off) {
        return Double.longBitsToDouble(bytesToLongBE(bytes, off));
    }

    public static double bytesToDoubleLE(byte[] bytes, int off) {
        return Double.longBitsToDouble(bytesToLongLE(bytes, off));
    }

    public static byte[] shortToBytes(int i, byte[] bytes, int off,
                                      boolean bigEndian) {
        return bigEndian ? shortToBytesBE(i, bytes, off)
                : shortToBytesLE(i, bytes, off);
    }

    public static byte[] shortToBytesBE(int i, byte[] bytes, int off) {
        bytes[off] = (byte) (i >> 8);
        bytes[off + 1] = (byte) i;
        return bytes;
    }

    public static byte[] shortToBytesLE(int i, byte[] bytes, int off) {
        bytes[off + 1] = (byte) (i >> 8);
        bytes[off] = (byte) i;
        return bytes;
    }

    public static byte[] intToBytes(int i, byte[] bytes, int off,
                                    boolean bigEndian) {
        return bigEndian ? intToBytesBE(i, bytes, off)
                : intToBytesLE(i, bytes, off);
    }

    public static byte[] intToBytesBE(int i, byte[] bytes, int off) {
        bytes[off] = (byte) (i >> 24);
        bytes[off + 1] = (byte) (i >> 16);
        bytes[off + 2] = (byte) (i >> 8);
        bytes[off + 3] = (byte) i;
        return bytes;
    }

    public static byte[] intToBytesLE(int i, byte[] bytes, int off) {
        bytes[off + 3] = (byte) (i >> 24);
        bytes[off + 2] = (byte) (i >> 16);
        bytes[off + 1] = (byte) (i >> 8);
        bytes[off] = (byte) i;
        return bytes;
    }

    public static byte[] tagToBytes(int i, byte[] bytes, int off,
                                    boolean bigEndian) {
        return bigEndian ? tagToBytesBE(i, bytes, off)
                : tagToBytesLE(i, bytes, off);
    }

    public static byte[] tagToBytesBE(int i, byte[] bytes, int off) {
        return intToBytesBE(i, bytes, off);
    }

    public static byte[] tagToBytesLE(int i, byte[] bytes, int off) {
        bytes[off + 1] = (byte) (i >> 24);
        bytes[off] = (byte) (i >> 16);
        bytes[off + 3] = (byte) (i >> 8);
        bytes[off + 2] = (byte) i;
        return bytes;
    }

    public static byte[] floatToBytes(float f, byte[] bytes, int off,
                                      boolean bigEndian) {
        return bigEndian ? floatToBytesBE(f, bytes, off)
                : floatToBytesLE(f, bytes, off);
    }

    public static byte[] floatToBytesBE(float f, byte[] bytes, int off) {
        return intToBytesBE(Float.floatToIntBits(f), bytes, off);
    }

    public static byte[] floatToBytesLE(float f, byte[] bytes, int off) {
        return intToBytesLE(Float.floatToIntBits(f), bytes, off);
    }

    public static byte[] doubleToBytes(double d, byte[] bytes, int off,
                                       boolean bigEndian) {
        return bigEndian ? doubleToBytesBE(d, bytes, off)
                : doubleToBytesLE(d, bytes, off);
    }

    public static byte[] doubleToBytesBE(double d, byte[] bytes, int off) {
        return longToBytesBE(Double.doubleToLongBits(d), bytes, off);
    }

    public static byte[] doubleToBytesLE(double d, byte[] bytes, int off) {
        return longToBytesLE(Double.doubleToLongBits(d), bytes, off);
    }

    public static byte[] longToBytes(long l, byte[] bytes, int off,
                                     boolean bigEndian) {
        return bigEndian ? longToBytesBE(l, bytes, off)
                : longToBytesLE(l, bytes, off);
    }

    public static byte[] longToBytesBE(long l, byte[] bytes, int off) {
        bytes[off] = (byte) (l >> 56);
        bytes[off + 1] = (byte) (l >> 48);
        bytes[off + 2] = (byte) (l >> 40);
        bytes[off + 3] = (byte) (l >> 32);
        bytes[off + 4] = (byte) (l >> 24);
        bytes[off + 5] = (byte) (l >> 16);
        bytes[off + 6] = (byte) (l >> 8);
        bytes[off + 7] = (byte) l;
        return bytes;
    }

    public static byte[] longToBytesLE(long l, byte[] bytes, int off) {
        bytes[off + 7] = (byte) (l >> 56);
        bytes[off + 6] = (byte) (l >> 48);
        bytes[off + 5] = (byte) (l >> 40);
        bytes[off + 4] = (byte) (l >> 32);
        bytes[off + 3] = (byte) (l >> 24);
        bytes[off + 2] = (byte) (l >> 16);
        bytes[off + 1] = (byte) (l >> 8);
        bytes[off] = (byte) l;
        return bytes;
    }

    public static byte[][] swapShorts(byte[][] bs) {
        int carry = 0;
        for (int i = 0; i < bs.length; i++) {
            byte[] b = bs[i];
            if (carry != 0)
                swapLastFirst(bs[i - 1], b);
            int len = b.length - carry;
            swapShorts(b, carry, len & ~1);
            carry = len & 1;
        }
        return bs;
    }

    public static byte[] swapShorts(byte[] b, int off, int len) {
        checkLength(len, 2);
        for (int i = off, n = off + len; i < n; i += 2)
            swap(b, i, i + 1);
        return b;
    }

    public static byte[] swapInts(byte[] b, int off, int len) {
        checkLength(len, 4);
        for (int i = off, n = off + len; i < n; i += 4) {
            swap(b, i, i + 3);
            swap(b, i + 1, i + 2);
        }
        return b;
    }

    public static byte[] swapLongs(byte[] b, int off, int len) {
        checkLength(len, 8);
        for (int i = off, n = off + len; i < n; i += 8) {
            swap(b, i, i + 7);
            swap(b, i + 1, i + 6);
            swap(b, i + 2, i + 5);
            swap(b, i + 3, i + 4);
        }
        return b;
    }

    private static void checkLength(int len, int numBytes) {
        if (len < 0 || (len % numBytes) != 0)
            throw new IllegalArgumentException("length: " + len);
    }

    private static void swap(byte[] bytes, int a, int b) {
        byte t = bytes[a];
        bytes[a] = bytes[b];
        bytes[b] = t;
    }

    private static void swapLastFirst(byte[] b1, byte[] b2) {
        int last = b1.length - 1;
        byte t = b2[0];
        b2[0] = b1[last];
        b1[last] = t;
    }

    public static byte[] intsToBytesLE(int... values) {
        byte[] ret = new byte[4 * values.length];
        for (int i = 0; i < values.length; i++) {
            intToBytesLE(values[i], ret, 4 * i);
        }
        return ret;
    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = org.aoju.bus.core.lang.Charset.UTF_8;
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }

    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * 拆分byte数组为几个等份（最后一份可能小于len）
     *
     * @param array 数组
     * @param len   每个小节的长度
     * @return 拆分后的数组
     */
    public static byte[][] split(byte[] array, int len) {
        int x = array.length / len;
        int y = array.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(array, i * len, arr, 0, y);
            } else {
                System.arraycopy(array, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

}
