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
package org.aoju.bus.health.linux.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.toolkit.FileKit;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.builtin.hardware.CentralProcessor;
import org.aoju.bus.health.linux.ProcPath;

import java.util.List;

/**
 * Utility to read CPU statistics from {@code /proc/stat}
 *
 * @author Kimi Liu
 * @version 6.0.0
 * @since JDK 1.8+
 */
@ThreadSafe
public final class CpuStat {

    private CpuStat() {
    }

    /**
     * Gets the System CPU ticks array from {@code /proc/stat}
     *
     * @return Array of CPU ticks
     */
    public static long[] getSystemCpuLoadTicks() {
        long[] ticks = new long[CentralProcessor.TickType.values().length];
        // /proc/stat expected format
        // first line is overall user,nice,system,idle,iowait,irq, etc.
        // cpu 3357 0 4313 1362393 ...
        String tickStr;
        List<String> procStat = FileKit.readLines(ProcPath.STAT);
        if (!procStat.isEmpty()) {
            tickStr = procStat.get(0);
        } else {
            return ticks;
        }
        // Split the line. Note the first (0) element is "cpu" so remaining
        // elements are offset by 1 from the enum index
        String[] tickArr = RegEx.SPACES.split(tickStr);
        if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex()) {
            // If ticks don't at least go user/nice/system/idle, abort
            return ticks;
        }
        // Note tickArr is offset by 1 because first element is "cpu"
        for (int i = 0; i < CentralProcessor.TickType.values().length; i++) {
            ticks[i] = Builder.parseLongOrDefault(tickArr[i + 1], 0L);
        }
        // Ignore guest or guest_nice, they are included in user/nice
        return ticks;
    }

    /**
     * Gets an arrya of Processor CPU ticks array from /proc/stat
     *
     * @param logicalProcessorCount The number of logical processors, which corresponds to the number
     *                              of lines to read from the file.
     * @return Array of CPU ticks for each processor
     */
    public static long[][] getProcessorCpuLoadTicks(int logicalProcessorCount) {
        long[][] ticks = new long[logicalProcessorCount][CentralProcessor.TickType.values().length];
        // /proc/stat expected format
        // first line is overall user,nice,system,idle, etc.
        // cpu 3357 0 4313 1362393 ...
        // per-processor subsequent lines for cpu0, cpu1, etc.
        int cpu = 0;
        List<String> procStat = FileKit.readLines(ProcPath.STAT);
        for (String stat : procStat) {
            if (stat.startsWith("cpu") && !stat.startsWith("cpu ")) {
                // Split the line. Note the first (0) element is "cpu" so
                // remaining
                // elements are offset by 1 from the enum index
                String[] tickArr = RegEx.SPACES.split(stat);
                if (tickArr.length <= CentralProcessor.TickType.IDLE.getIndex()) {
                    // If ticks don't at least go user/nice/system/idle, abort
                    return ticks;
                }
                // Note tickArr is offset by 1
                for (int i = 0; i < CentralProcessor.TickType.values().length; i++) {
                    ticks[cpu][i] = Builder.parseLongOrDefault(tickArr[i + 1], 0L);
                }
                // Ignore guest or guest_nice, they are included in
                if (++cpu >= logicalProcessorCount) {
                    break;
                }
            }
        }
        return ticks;
    }

    /**
     * Gets the number of context switches from /proc/stat
     *
     * @return The number of context switches if available, -1 otherwise
     */
    public static long getContextSwitches() {
        List<String> procStat = FileKit.readLines(ProcPath.STAT);
        for (String stat : procStat) {
            if (stat.startsWith("ctxt ")) {
                String[] ctxtArr = RegEx.SPACES.split(stat);
                if (ctxtArr.length == 2) {
                    return Builder.parseLongOrDefault(ctxtArr[1], 0);
                }
            }
        }
        return -1;
    }

    /**
     * Gets the number of interrupts from /proc/stat
     *
     * @return The number of interrupts if available, -1 otherwise
     */
    public static long getInterrupts() {
        List<String> procStat = FileKit.readLines(ProcPath.STAT);
        for (String stat : procStat) {
            if (stat.startsWith("intr ")) {
                String[] intrArr = RegEx.SPACES.split(stat);
                if (intrArr.length > 2) {
                    return Builder.parseLongOrDefault(intrArr[1], 0);
                }
            }
        }
        return -1;
    }

    /**
     * Gets the boot time from /proc/stat
     *
     * @return The boot time if available, 0 otherwise
     */
    public static long getBootTime() {
        // Boot time given by btime variable in /proc/stat.
        List<String> procStat = FileKit.readLines(ProcPath.STAT);
        for (String stat : procStat) {
            if (stat.startsWith("btime")) {
                String[] bTime = RegEx.SPACES.split(stat);
                return Builder.parseLongOrDefault(bTime[1], 0L);
            }
        }
        return 0;
    }

}
