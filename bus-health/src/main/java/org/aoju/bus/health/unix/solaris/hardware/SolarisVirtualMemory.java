/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.solaris.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.builtin.hardware.AbstractVirtualMemory;
import org.aoju.bus.health.unix.solaris.drivers.SystemPages;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.aoju.bus.health.Memoize.defaultExpiration;
import static org.aoju.bus.health.Memoize.memoize;

/**
 * Memory obtained by kstat and swap
 *
 * @author Kimi Liu
 * @version 5.9.3
 * @since JDK 1.8+
 */
@ThreadSafe
final class SolarisVirtualMemory extends AbstractVirtualMemory {

    private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");

    private final SolarisGlobalMemory global;

    // Physical
    private final Supplier<Pair<Long, Long>> availTotal = memoize(SystemPages::queryAvailableTotal,
            defaultExpiration());

    // Swap
    private final Supplier<Pair<Long, Long>> usedTotal = memoize(SolarisVirtualMemory::querySwapInfo,
            defaultExpiration());

    private final Supplier<Long> pagesIn = memoize(SolarisVirtualMemory::queryPagesIn, defaultExpiration());

    private final Supplier<Long> pagesOut = memoize(SolarisVirtualMemory::queryPagesOut, defaultExpiration());

    /**
     * Constructor for SolarisVirtualMemory.
     *
     * @param solarisGlobalMemory The parent global memory class instantiating this
     */
    SolarisVirtualMemory(SolarisGlobalMemory solarisGlobalMemory) {
        this.global = solarisGlobalMemory;
    }

    private static long queryPagesIn() {
        long swapPagesIn = 0L;
        for (String s : Executor.runNative("kstat -p cpu_stat:::pgpgin")) {
            swapPagesIn += Builder.parseLastLong(s, 0L);
        }
        return swapPagesIn;
    }

    private static long queryPagesOut() {
        long swapPagesOut = 0L;
        for (String s : Executor.runNative("kstat -p cpu_stat:::pgpgout")) {
            swapPagesOut += Builder.parseLastLong(s, 0L);
        }
        return swapPagesOut;
    }

    private static Pair<Long, Long> querySwapInfo() {
        long swapTotal = 0L;
        long swapUsed = 0L;
        String swap = Executor.getAnswerAt("swap -lk", 1);
        Matcher m = SWAP_INFO.matcher(swap);
        if (m.matches()) {
            swapTotal = Builder.parseLongOrDefault(m.group(1), 0L) << 10;
            swapUsed = swapTotal - (Builder.parseLongOrDefault(m.group(2), 0L) << 10);
        }
        return Pair.of(swapUsed, swapTotal);
    }

    @Override
    public long getSwapUsed() {
        return usedTotal.get().getLeft();
    }

    @Override
    public long getSwapTotal() {
        return usedTotal.get().getRight();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getPageSize() * availTotal.get().getRight() + getSwapTotal();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getPageSize() * (availTotal.get().getRight() - availTotal.get().getLeft()) + getSwapUsed();
    }

    @Override
    public long getSwapPagesIn() {
        return pagesIn.get();
    }

    @Override
    public long getSwapPagesOut() {
        return pagesOut.get();
    }

}
