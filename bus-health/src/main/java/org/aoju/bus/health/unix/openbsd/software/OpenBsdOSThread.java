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
package org.aoju.bus.health.unix.openbsd.software;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.builtin.software.AbstractOSThread;
import org.aoju.bus.health.builtin.software.OSProcess;

import java.util.List;
import java.util.Map;

/**
 * OSThread implementation
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public class OpenBsdOSThread extends AbstractOSThread {

    private int threadId;
    private String name = "";
    private OSProcess.State state = OSProcess.State.INVALID;
    private long minorFaults;
    private long majorFaults;
    private long startMemoryAddress;
    private long contextSwitches;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private int priority;

    public OpenBsdOSThread(int processId, Map<OpenBsdOSProcess.PsThreadColumns, String> threadMap) {
        super(processId);
        updateAttributes(threadMap);
    }

    @Override
    public int getThreadId() {
        return this.threadId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public OSProcess.State getState() {
        return this.state;
    }

    @Override
    public long getStartMemoryAddress() {
        return this.startMemoryAddress;
    }

    @Override
    public long getContextSwitches() {
        return this.contextSwitches;
    }

    @Override
    public long getMinorFaults() {
        return this.minorFaults;
    }

    @Override
    public long getMajorFaults() {
        return this.majorFaults;
    }

    @Override
    public long getKernelTime() {
        return this.kernelTime;
    }

    @Override
    public long getUserTime() {
        return this.userTime;
    }

    @Override
    public long getUpTime() {
        return this.upTime;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public boolean updateAttributes() {
        String psCommand = "ps -aHwwxo " + OpenBsdOSProcess.PS_THREAD_COLUMNS + " -p " + getOwningProcessId();
        // there is no switch for thread in ps command, hence filtering.
        List<String> threadList = Executor.runNative(psCommand);
        String tidStr = Integer.toString(this.threadId);
        for (String psOutput : threadList) {
            Map<OpenBsdOSProcess.PsThreadColumns, String> threadMap = Builder.stringToEnumMap(OpenBsdOSProcess.PsThreadColumns.class, psOutput.trim(),
                    Symbol.C_SPACE);
            if (threadMap.containsKey(OpenBsdOSProcess.PsThreadColumns.ARGS) && tidStr.equals(threadMap.get(OpenBsdOSProcess.PsThreadColumns.TID))) {
                return updateAttributes(threadMap);
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<OpenBsdOSProcess.PsThreadColumns, String> threadMap) {
        this.threadId = Builder.parseIntOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.TID), 0);
        switch (threadMap.get(OpenBsdOSProcess.PsThreadColumns.STATE).charAt(0)) {
            case 'R':
                this.state = OSProcess.State.RUNNING;
                break;
            case 'I':
            case 'S':
                this.state = OSProcess.State.SLEEPING;
                break;
            case 'D':
            case 'L':
            case 'U':
                this.state = OSProcess.State.WAITING;
                break;
            case 'Z':
                this.state = OSProcess.State.ZOMBIE;
                break;
            case 'T':
                this.state = OSProcess.State.STOPPED;
                break;
            default:
                this.state = OSProcess.State.OTHER;
                break;
        }
        // Avoid divide by zero for processes up less than a second
        long elapsedTime = Builder.parseDHMSOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.ETIME), 0L);
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        long now = System.currentTimeMillis();
        this.startTime = now - this.upTime;
        // ps does not provide kerneltime on OpenBSD
        this.kernelTime = 0L;
        this.userTime = Builder.parseDHMSOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.CPUTIME), 0L);
        this.startMemoryAddress = 0L;
        long nonVoluntaryContextSwitches = Builder.parseLongOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.NIVCSW), 0L);
        long voluntaryContextSwitches = Builder.parseLongOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.NVCSW), 0L);
        this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
        this.majorFaults = Builder.parseLongOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.MAJFLT), 0L);
        this.minorFaults = Builder.parseLongOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.MINFLT), 0L);
        this.priority = Builder.parseIntOrDefault(threadMap.get(OpenBsdOSProcess.PsThreadColumns.PRI), 0);
        this.name = threadMap.get(OpenBsdOSProcess.PsThreadColumns.ARGS);
        return true;
    }

}
