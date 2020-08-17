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
package org.aoju.bus.health.unix.aix.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.builtin.hardware.AbstractVirtualMemory;
import org.aoju.bus.health.unix.aix.Perfstat;

import java.util.function.Supplier;

/**
 * Memory obtained by perfstat_memory_total_t
 *
 * @author Kimi Liu
 * @version 6.0.6
 * @since JDK 1.8+
 */
@ThreadSafe
final class AixVirtualMemory extends AbstractVirtualMemory {

    // AIX has multiple page size units, but for purposes of "pages" in perfstat,
    // the docs specify 4KB pages so we hardcode this
    private static final long PAGESIZE = 4096L;
    // Memoized perfstat from GlobalMemory
    private final Supplier<Perfstat.perfstat_memory_total_t> perfstatMem;

    /**
     * Constructor for SolarisVirtualMemory.
     *
     * @param perfstatMem The memoized perfstat data from the global memory class
     */
    AixVirtualMemory(Supplier<Perfstat.perfstat_memory_total_t> perfstatMem) {
        this.perfstatMem = perfstatMem;
    }

    @Override
    public long getSwapUsed() {
        Perfstat.perfstat_memory_total_t perfstat = perfstatMem.get();
        return (perfstat.pgsp_total - perfstat.pgsp_free) * PAGESIZE;
    }

    @Override
    public long getSwapTotal() {
        return perfstatMem.get().pgsp_total * PAGESIZE;
    }

    @Override
    public long getVirtualMax() {
        return perfstatMem.get().virt_total * PAGESIZE;
    }

    @Override
    public long getVirtualInUse() {
        return perfstatMem.get().virt_active * PAGESIZE;
    }

    @Override
    public long getSwapPagesIn() {
        return perfstatMem.get().pgspins;
    }

    @Override
    public long getSwapPagesOut() {
        return perfstatMem.get().pgspouts;
    }

}
