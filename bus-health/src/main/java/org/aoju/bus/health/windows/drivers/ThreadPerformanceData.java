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
package org.aoju.bus.health.windows.drivers;

import com.sun.jna.platform.win32.WinBase;
import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.core.lang.tuple.Triple;
import org.aoju.bus.health.windows.ThreadInformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Utility to read thread data from HKEY_PERFORMANCE_DATA information with
 * backup from Performance Counters or WMI
 *
 * @author Kimi Liu
 * @version 6.0.2
 * @since JDK 1.8+
 */
@ThreadSafe
public final class ThreadPerformanceData {

    private static final String THREAD = "Thread";

    private ThreadPerformanceData() {
    }

    /**
     * Query the registry for thread performance counters
     *
     * @param pids An optional collection of thread IDs to filter the list to. May be
     *             null for no filtering.
     * @return A map with Thread ID as the key and a {@link PerfCounterBlock} object
     * populated with performance counter information if successful, or null
     * otherwise.
     */
    public static Map<Integer, PerfCounterBlock> buildThreadMapFromRegistry(Collection<Integer> pids) {
        // Grab the data from the registry.
        Triple<List<Map<ThreadInformation.ThreadPerformanceProperty, Object>>, Long, Long> threadData = HkeyPerformance
                .readPerfDataFromRegistry(THREAD, ThreadInformation.ThreadPerformanceProperty.class);
        if (threadData == null) {
            return null;
        }
        List<Map<ThreadInformation.ThreadPerformanceProperty, Object>> threadInstanceMaps = threadData.getLeft();
        long perfTime100nSec = threadData.getMiddle();
        long now = threadData.getRight();

        // Create a map and fill it
        Map<Integer, PerfCounterBlock> threadMap = new HashMap<>();
        // Iterate instances.
        for (Map<ThreadInformation.ThreadPerformanceProperty, Object> threadInstanceMap : threadInstanceMaps) {
            int pid = ((Integer) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS)).intValue();
            if ((pids == null || pids.contains(pid)) && pid > 0) {
                int tid = ((Integer) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD)).intValue();
                String name = (String) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.NAME);
                long upTime = (perfTime100nSec - (Long) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME))
                        / 10_000L;
                if (upTime < 1) {
                    upTime = 1;
                }
                long user = ((Long) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME)).longValue()
                        / 10_000L;
                long kernel = ((Long) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME))
                        .longValue() / 10_000L;
                int priority = ((Integer) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT)).intValue();
                int threadState = ((Integer) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE)).intValue();
                long startAddr = ((Long) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS)).longValue();
                int contextSwitches = ((Integer) threadInstanceMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC))
                        .intValue();
                threadMap.put(tid, new PerfCounterBlock(name, tid, pid, now - upTime, user, kernel, priority,
                        threadState, startAddr, contextSwitches));
            }
        }
        return threadMap;
    }

    /**
     * Query PerfMon for thread performance counters
     *
     * @param pids An optional collection of process IDs to filter the list to. May
     *             be null for no filtering.
     * @return A map with Thread ID as the key and a {@link PerfCounterBlock} object
     * populated with performance counter information.
     */
    public static Map<Integer, PerfCounterBlock> buildThreadMapFromPerfCounters(Collection<Integer> pids) {
        Map<Integer, PerfCounterBlock> threadMap = new HashMap<>();
        Pair<List<String>, Map<ThreadInformation.ThreadPerformanceProperty, List<Long>>> instanceValues = ThreadInformation
                .queryThreadCounters();
        long now = System.currentTimeMillis(); // 1970 epoch
        List<String> instances = instanceValues.getLeft();
        Map<ThreadInformation.ThreadPerformanceProperty, List<Long>> valueMap = instanceValues.getRight();
        List<Long> tidList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDTHREAD);
        List<Long> pidList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.IDPROCESS);
        List<Long> userList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTUSERTIME); // 100-nsec
        List<Long> kernelList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PERCENTPRIVILEGEDTIME); // 100-nsec
        List<Long> startTimeList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.ELAPSEDTIME); // filetime
        List<Long> priorityList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.PRIORITYCURRENT);
        List<Long> stateList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.THREADSTATE);
        List<Long> startAddrList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.STARTADDRESS);
        List<Long> contextSwitchesList = valueMap.get(ThreadInformation.ThreadPerformanceProperty.CONTEXTSWITCHESPERSEC);

        int nameIndex = 0;
        for (int inst = 0; inst < instances.size(); inst++) {
            int pid = pidList.get(inst).intValue();
            if (pids == null || pids.contains(pid)) {
                int tid = tidList.get(inst).intValue();
                String name = Integer.toString(nameIndex++);
                long startTime = startTimeList.get(inst);
                startTime = WinBase.FILETIME.filetimeToDate((int) (startTime >> 32), (int) (startTime & 0xffffffffL)).getTime();
                if (startTime > now) {
                    startTime = now - 1;
                }
                long user = userList.get(inst) / 10_000L;
                long kernel = kernelList.get(inst) / 10_000L;
                int priority = priorityList.get(inst).intValue();
                int threadState = stateList.get(inst).intValue();
                long startAddr = startAddrList.get(inst).longValue();
                int contextSwitches = contextSwitchesList.get(inst).intValue();

                // if creation time value is less than current millis, it's in 1970 epoch,
                // otherwise it's 1601 epoch and we must convert
                threadMap.put(tid, new PerfCounterBlock(name, tid, pid, startTime, user, kernel, priority, threadState,
                        startAddr, contextSwitches));
            }
        }
        return threadMap;
    }

    /**
     * Class to encapsulate data from the registry performance counter block
     */
    @Immutable
    public static class PerfCounterBlock {
        private final String name;
        private final int threadID;
        private final int owningProcessID;
        private final long startTime;
        private final long userTime;
        private final long kernelTime;
        private final int priority;
        private final int threadState;
        private final long startAddress;
        private final int contextSwitches;

        public PerfCounterBlock(String name, int threadID, int owningProcessID, long startTime, long userTime,
                                long kernelTime, int priority, int threadState, long startAddress, int contextSwitches) {
            this.name = name;
            this.threadID = threadID;
            this.owningProcessID = owningProcessID;
            this.startTime = startTime;
            this.userTime = userTime;
            this.kernelTime = kernelTime;
            this.priority = priority;
            this.threadState = threadState;
            this.startAddress = startAddress;
            this.contextSwitches = contextSwitches;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the threadID
         */
        public int getThreadID() {
            return threadID;
        }

        /**
         * @return the owningProcessID
         */
        public int getOwningProcessID() {
            return owningProcessID;
        }

        /**
         * @return the startTime
         */
        public long getStartTime() {
            return startTime;
        }

        /**
         * @return the userTime
         */
        public long getUserTime() {
            return userTime;
        }

        /**
         * @return the kernelTime
         */
        public long getKernelTime() {
            return kernelTime;
        }

        /**
         * @return the priority
         */
        public int getPriority() {
            return priority;
        }

        /**
         * @return the threadState
         */
        public int getThreadState() {
            return threadState;
        }

        /**
         * @return the startMemoryAddress
         */
        public long getStartAddress() {
            return startAddress;
        }

        /**
         * @return the contextSwitches
         */
        public int getContextSwitches() {
            return contextSwitches;
        }
    }

}
