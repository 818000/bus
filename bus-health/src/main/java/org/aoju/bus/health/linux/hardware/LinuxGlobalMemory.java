/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.linux.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractGlobalMemory;
import org.aoju.bus.health.builtin.hardware.VirtualMemory;
import org.aoju.bus.health.linux.ProcPath;
import org.aoju.bus.health.linux.software.LinuxOperatingSystem;

import java.util.List;
import java.util.function.Supplier;

/**
 * Memory obtained by /proc/meminfo and sysinfo.totalram
 *
 * @author Kimi Liu
 * @version 6.5.0
 * @since Java 17+
 */
@ThreadSafe
public final class LinuxGlobalMemory extends AbstractGlobalMemory {

    private static final long PAGE_SIZE = LinuxOperatingSystem.getPageSize();

    private final Supplier<Pair<Long, Long>> availTotal = Memoize.memoize(LinuxGlobalMemory::readMemInfo, Memoize.defaultExpiration());

    private final Supplier<VirtualMemory> vm = Memoize.memoize(this::createVirtualMemory);

    /**
     * Updates instance variables from reading /proc/meminfo. While most of the
     * information is available in the sysinfo structure, the most accurate
     * calculation of MemAvailable is only available from reading this pseudo-file.
     * The maintainers of the Linux Kernel have indicated this location will be kept
     * up to date if the calculation changes: see
     * https://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/commit/?
     * id=34e431b0ae398fc54ea69ff85ec700722c9da773
     * <p>
     * Internally, reading /proc/meminfo is faster than sysinfo because it only
     * spends time populating the memory components of the sysinfo structure.
     *
     * @return A pair containing available and total memory in bytes
     */
    private static Pair<Long, Long> readMemInfo() {
        long memFree = 0L;
        long activeFile = 0L;
        long inactiveFile = 0L;
        long sReclaimable = 0L;

        long memTotal = 0L;
        long memAvailable;

        List<String> procMemInfo = Builder.readFile(ProcPath.MEMINFO);
        for (String checkLine : procMemInfo) {
            String[] memorySplit = RegEx.SPACES.split(checkLine, 2);
            if (memorySplit.length > 1) {
                switch (memorySplit[0]) {
                    case "MemTotal:":
                        memTotal = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        break;
                    case "MemAvailable:":
                        memAvailable = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        // We're done!
                        return Pair.of(memAvailable, memTotal);
                    case "MemFree:":
                        memFree = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        break;
                    case "Active(file):":
                        activeFile = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        break;
                    case "Inactive(file):":
                        inactiveFile = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        break;
                    case "SReclaimable:":
                        sReclaimable = Builder.parseDecimalMemorySizeToBinary(memorySplit[1]);
                        break;
                    default:
                        // do nothing with other lines
                        break;
                }
            }
        }
        // We didn't find MemAvailable so we estimate from other fields
        return Pair.of(memFree + activeFile + inactiveFile + sReclaimable, memTotal);
    }

    @Override
    public long getAvailable() {
        return availTotal.get().getLeft();
    }

    @Override
    public long getTotal() {
        return availTotal.get().getRight();
    }

    @Override
    public long getPageSize() {
        return PAGE_SIZE;
    }

    @Override
    public VirtualMemory getVirtualMemory() {
        return vm.get();
    }

    private VirtualMemory createVirtualMemory() {
        return new LinuxVirtualMemory(this);
    }

}
