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
package org.aoju.bus.health.unix.freebsd.software;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.ByRef;
import org.aoju.bus.health.builtin.software.AbstractOSProcess;
import org.aoju.bus.health.builtin.software.OSProcess;
import org.aoju.bus.health.builtin.software.OSThread;
import org.aoju.bus.health.unix.FreeBsdLibc;
import org.aoju.bus.health.unix.freebsd.BsdSysctlKit;
import org.aoju.bus.health.unix.freebsd.ProcstatKit;
import org.aoju.bus.logger.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * OSProcess implementation
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@ThreadSafe
public class FreeBsdOSProcess extends AbstractOSProcess {

    static final String PS_THREAD_COLUMNS = Arrays.stream(PsThreadColumns.values()).map(Enum::name)
            .map(String::toLowerCase).collect(Collectors.joining(","));
    private static final int ARGMAX = BsdSysctlKit.sysctl("kern.argmax", 0);
    private final Supplier<Integer> bitness = Memoize.memoize(this::queryBitness);
    private final Supplier<List<String>> arguments = Memoize.memoize(this::queryArguments);
    private final Supplier<Map<String, String>> environmentVariables = Memoize.memoize(this::queryEnvironmentVariables);
    private String name;
    private String path = Normal.EMPTY;
    private String user;
    private String userID;
    private String group;
    private String groupID;
    private OSProcess.State state = OSProcess.State.INVALID;
    private int parentProcessID;
    private int threadCount;
    private int priority;
    private long virtualSize;
    private long residentSetSize;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private long bytesRead;
    private long bytesWritten;
    private long minorFaults;
    private long majorFaults;
    private long contextSwitches;
    private String commandLineBackup;
    private final Supplier<String> commandLine = Memoize.memoize(this::queryCommandLine);

    public FreeBsdOSProcess(int pid, Map<FreeBsdOperatingSystem.PsKeywords, String> psMap) {
        super(pid);
        updateAttributes(psMap);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getCommandLine() {
        return this.commandLine.get();
    }

    private String queryCommandLine() {
        String cl = String.join(" ", getArguments());
        return cl.isEmpty() ? this.commandLineBackup : cl;
    }

    @Override
    public List<String> getArguments() {
        return arguments.get();
    }

    private List<String> queryArguments() {
        if (ARGMAX > 0) {
            // Get arguments via sysctl(3)
            int[] mib = new int[4];
            mib[0] = 1; // CTL_KERN
            mib[1] = 14; // KERN_PROC
            mib[2] = 7; // KERN_PROC_ARGS
            mib[3] = getProcessID();
            // Allocate memory for arguments
            try (Memory m = new Memory(ARGMAX);
                 ByRef.CloseableSizeTByReference size = new ByRef.CloseableSizeTByReference(ARGMAX)) {
                // Fetch arguments
                if (FreeBsdLibc.INSTANCE.sysctl(mib, mib.length, m, size, null, size_t.ZERO) == 0) {
                    return Collections.unmodifiableList(
                            Builder.parseByteArrayToStrings(m.getByteArray(0, size.getValue().intValue())));
                } else {
                    Logger.warn(
                            "Failed sysctl call for process arguments (kern.proc.args), process {} may not exist. Error code: {}",
                            getProcessID(), Native.getLastError());
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables.get();
    }

    private Map<String, String> queryEnvironmentVariables() {
        if (ARGMAX > 0) {
            // Get environment variables via sysctl(3)
            int[] mib = new int[4];
            mib[0] = 1; // CTL_KERN
            mib[1] = 14; // KERN_PROC
            mib[2] = 35; // KERN_PROC_ENV
            mib[3] = getProcessID();
            // Allocate memory for environment variables
            try (Memory m = new Memory(ARGMAX)) {
                size_t.ByReference size = new size_t.ByReference(new size_t(ARGMAX));
                // Fetch environment variables
                if (FreeBsdLibc.INSTANCE.sysctl(mib, mib.length, m, size, null, size_t.ZERO) == 0) {
                    return Collections.unmodifiableMap(
                            Builder.parseByteArrayToStringMap(m.getByteArray(0, size.getValue().intValue())));
                } else {
                    Logger.warn(
                            "Failed sysctl call for process environment variables (kern.proc.env), process {} may not exist. Error code: {}",
                            getProcessID(), Native.getLastError());
                }
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public String getCurrentWorkingDirectory() {
        return ProcstatKit.getCwd(getProcessID());
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getUserID() {
        return this.userID;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public String getGroupID() {
        return this.groupID;
    }

    @Override
    public OSProcess.State getState() {
        return this.state;
    }

    @Override
    public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override
    public long getResidentSetSize() {
        return this.residentSetSize;
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
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }

    @Override
    public long getOpenFiles() {
        return ProcstatKit.getOpenFiles(getProcessID());
    }

    @Override
    public int getBitness() {
        return this.bitness.get();
    }

    @Override
    public long getAffinityMask() {
        long bitMask = 0L;
        // Would prefer to use native cpuset_getaffinity call but variable sizing is
        // kernel-dependent and requires C macros, so we use commandline instead.
        String cpuset = Executor.getFirstAnswer("cpuset -gp " + getProcessID());
        // Sample output:
        // pid 8 mask: 0, 1
        // cpuset: getaffinity: No such process
        String[] split = cpuset.split(":");
        if (split.length > 1) {
            String[] bits = split[1].split(",");
            for (String bit : bits) {
                int bitToSet = Builder.parseIntOrDefault(bit.trim(), -1);
                if (bitToSet >= 0) {
                    bitMask |= 1L << bitToSet;
                }
            }
        }
        return bitMask;
    }

    private int queryBitness() {
        // Get process abi vector
        int[] mib = new int[4];
        mib[0] = 1; // CTL_KERN
        mib[1] = 14; // KERN_PROC
        mib[2] = 9; // KERN_PROC_SV_NAME
        mib[3] = getProcessID();
        // Allocate memory for arguments
        try (Memory abi = new Memory(32); ByRef.CloseableSizeTByReference size = new ByRef.CloseableSizeTByReference(32)) {
            // Fetch abi vector
            if (0 == FreeBsdLibc.INSTANCE.sysctl(mib, mib.length, abi, size, null, size_t.ZERO)) {
                String elf = abi.getString(0);
                if (elf.contains("ELF32")) {
                    return 32;
                } else if (elf.contains("ELF64")) {
                    return 64;
                }
            }
        }
        return 0;
    }

    @Override
    public List<OSThread> getThreadDetails() {
        List<OSThread> threads = new ArrayList<>();
        String psCommand = "ps -awwxo " + PS_THREAD_COLUMNS + " -H";
        if (getProcessID() >= 0) {
            psCommand += " -p " + getProcessID();
        }
        List<String> threadList = Executor.runNative(psCommand);
        if (threadList.size() > 1) {
            // remove header row
            threadList.remove(0);
            // Fill list
            for (String thread : threadList) {
                Map<PsThreadColumns, String> threadMap = Builder.stringToEnumMap(PsThreadColumns.class, thread.trim(),
                        ' ');
                if (threadMap.containsKey(PsThreadColumns.PRI)) {
                    threads.add(new FreeBsdOSThread(getProcessID(), threadMap));
                }
            }
        }
        return threads;
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
    public long getContextSwitches() {
        return this.contextSwitches;
    }

    @Override
    public boolean updateAttributes() {
        String psCommand = "ps -awwxo " + FreeBsdOperatingSystem.PS_COMMAND_ARGS + " -p " + getProcessID();
        List<String> procList = Executor.runNative(psCommand);
        if (procList.size() > 1) {
            // skip header row
            Map<FreeBsdOperatingSystem.PsKeywords, String> psMap = Builder.stringToEnumMap(FreeBsdOperatingSystem.PsKeywords.class, procList.get(1).trim(), ' ');
            // Check if last (thus all) value populated
            if (psMap.containsKey(FreeBsdOperatingSystem.PsKeywords.ARGS)) {
                return updateAttributes(psMap);
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<FreeBsdOperatingSystem.PsKeywords, String> psMap) {
        long now = System.currentTimeMillis();
        switch (psMap.get(FreeBsdOperatingSystem.PsKeywords.STATE).charAt(0)) {
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
        this.parentProcessID = Builder.parseIntOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.PPID), 0);
        this.user = psMap.get(FreeBsdOperatingSystem.PsKeywords.USER);
        this.userID = psMap.get(FreeBsdOperatingSystem.PsKeywords.UID);
        this.group = psMap.get(FreeBsdOperatingSystem.PsKeywords.GROUP);
        this.groupID = psMap.get(FreeBsdOperatingSystem.PsKeywords.GID);
        this.threadCount = Builder.parseIntOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.NLWP), 0);
        this.priority = Builder.parseIntOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.PRI), 0);
        // These are in KB, multiply
        this.virtualSize = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.VSZ), 0) * 1024;
        this.residentSetSize = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.RSS), 0) * 1024;
        // Avoid divide by zero for processes up less than a second
        long elapsedTime = Builder.parseDHMSOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.ETIMES), 0L);
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        this.startTime = now - this.upTime;
        this.kernelTime = Builder.parseDHMSOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.SYSTIME), 0L);
        this.userTime = Builder.parseDHMSOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.TIME), 0L) - this.kernelTime;
        this.path = psMap.get(FreeBsdOperatingSystem.PsKeywords.COMM);
        this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
        this.minorFaults = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.MAJFLT), 0L);
        this.majorFaults = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.MINFLT), 0L);
        long nonVoluntaryContextSwitches = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.NVCSW), 0L);
        long voluntaryContextSwitches = Builder.parseLongOrDefault(psMap.get(FreeBsdOperatingSystem.PsKeywords.NIVCSW), 0L);
        this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
        this.commandLineBackup = psMap.get(FreeBsdOperatingSystem.PsKeywords.ARGS);
        return true;
    }

    enum PsThreadColumns {
        TDNAME, LWP, STATE, ETIMES, SYSTIME, TIME, TDADDR, NIVCSW, NVCSW, MAJFLT, MINFLT, PRI
    }

}
