/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.solaris.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to query lshal
 *
 * @author Kimi Liu
 * @version 6.2.6
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Lshal {

    private static final String LSHAL_CMD = "lshal";

    private Lshal() {
    }

    /**
     * Query lshal to get device major
     *
     * @return A map with disk names as the key and block device major as the value
     * if lshal is installed; empty map otherwise
     */
    public static Map<String, Integer> queryDiskToMajorMap() {
        Map<String, Integer> majorMap = new HashMap<>();
        List<String> lshal = Executor.runNative(LSHAL_CMD);
        String diskName = null;
        for (String line : lshal) {
            if (line.startsWith("udi ")) {
                String udi = Builder.getSingleQuoteStringValue(line);
                diskName = udi.substring(udi.lastIndexOf(Symbol.C_SLASH) + 1);
            } else {
                line = line.trim();
                if (line.startsWith("block.major") && null != diskName) {
                    majorMap.put(diskName, Builder.getFirstIntValue(line));
                }
            }
        }
        return majorMap;
    }

}
