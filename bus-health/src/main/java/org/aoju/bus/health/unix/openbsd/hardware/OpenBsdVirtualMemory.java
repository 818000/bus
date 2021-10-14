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
package org.aoju.bus.health.unix.openbsd.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.tuple.Triple;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractVirtualMemory;

import java.util.function.Supplier;

/**
 * Memory info on OpenBSD
 *
 * @author Kimi Liu
 * @version 6.3.0
 * @since JDK 1.8+
 */
@ThreadSafe
final class OpenBsdVirtualMemory extends AbstractVirtualMemory {

    private final Supplier<Triple<Integer, Integer, Integer>> usedTotalPgin = Memoize.memoize(
            OpenBsdVirtualMemory::queryVmstat, Memoize.defaultExpiration());
    private final Supplier<Integer> pgout = Memoize.memoize(OpenBsdVirtualMemory::queryUvm, Memoize.defaultExpiration());
    OpenBsdGlobalMemory global;

    OpenBsdVirtualMemory(OpenBsdGlobalMemory freeBsdGlobalMemory) {
        this.global = freeBsdGlobalMemory;
    }

    private static Triple<Integer, Integer, Integer> queryVmstat() {
        int used = 0;
        int total = 0;
        int swapIn = 0;
        for (String line : Executor.runNative("vmstat -s")) {
            if (line.contains("swap pages in use")) {
                used = Builder.getFirstIntValue(line);
            } else if (line.contains("swap pages")) {
                total = Builder.getFirstIntValue(line);
            } else if (line.contains("pagein operations")) {
                swapIn = Builder.getFirstIntValue(line);
            }
        }
        return Triple.of(used, total, swapIn);
    }

    private static int queryUvm() {
        for (String line : Executor.runNative("systat -ab uvm")) {
            if (line.contains("pdpageouts")) {
                // First column is non-numeric "Constants" header
                return Builder.getFirstIntValue(line);
            }
        }
        return 0;
    }

    @Override
    public long getSwapUsed() {
        return usedTotalPgin.get().getLeft() * global.getPageSize();
    }

    @Override
    public long getSwapTotal() {
        return usedTotalPgin.get().getMiddle() * global.getPageSize();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getTotal() + getSwapTotal();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
    }

    @Override
    public long getSwapPagesIn() {
        return usedTotalPgin.get().getRight() * global.getPageSize();
    }

    @Override
    public long getSwapPagesOut() {
        return pgout.get() * global.getPageSize();
    }

}
