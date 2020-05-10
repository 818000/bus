/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2021 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.logger.Logger;

/**
 * 提供对FreeBSD上的sysctl调用的访问
 *
 * @author Kimi Liu
 * @version 5.9.0
 * @since JDK 1.8+
 */
@ThreadSafe
public final class BsdSysctl {

    private static final String SYSCTL_FAIL = "Failed syctl call: {}, Error code: {}";

    private BsdSysctl() {
    }

    /**
     * 执行带有int结果的sysctl调用
     *
     * @param name 系统的名称
     * @param def  默认int值
     * @return 如果调用成功，则返回调用的int结果;否则默认
     */
    public static int sysctl(String name, int def) {
        IntByReference size = new IntByReference(FreeBsdLibc.INT_SIZE);
        Pointer p = new Memory(size.getValue());
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, size, null, 0)) {
            Logger.error("Failed sysctl call: {}, Error code: {}", name, Native.getLastError());
            return def;
        }
        return p.getInt(0);
    }

    /**
     * 执行带有长结果的sysctl调用
     *
     * @param name 系统的名称
     * @param def  默认长整型值
     * @return 如果调用成功，则返回调用的长整型结果;否则默认
     */
    public static long sysctl(String name, long def) {
        IntByReference size = new IntByReference(FreeBsdLibc.UINT64_SIZE);
        Pointer p = new Memory(size.getValue());
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, size, null, 0)) {
            Logger.warn(SYSCTL_FAIL, name, Native.getLastError());
            return def;
        }
        return p.getLong(0);
    }

    /**
     * 执行带有字符串结果的sysctl调用
     *
     * @param name 系统的名称
     * @param def  默认字符串值
     * @return 如果调用成功，则返回调用的字符串结果;否则默认
     */
    public static String sysctl(String name, String def) {
        // Call first time with null pointer to get value of size
        IntByReference size = new IntByReference();
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, null, size, null, 0)) {
            Logger.warn(SYSCTL_FAIL, name, Native.getLastError());
            return def;
        }
        // Add 1 to size for null terminated string
        Pointer p = new Memory(size.getValue() + 1L);
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, p, size, null, 0)) {
            Logger.warn(SYSCTL_FAIL, name, Native.getLastError());
            return def;
        }
        return p.getString(0);
    }

    /**
     * 执行带有结构结果的sysctl调用
     *
     * @param name   系统的名称
     * @param struct 构造结果
     * @return 如果结构成功填充为真，则为假
     */
    public static boolean sysctl(String name, Structure struct) {
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, struct.getPointer(), new IntByReference(struct.size()), null,
                0)) {
            Logger.error(SYSCTL_FAIL, name, Native.getLastError());
            return false;
        }
        struct.read();
        return true;
    }

}
