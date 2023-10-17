/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.aix.software;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_partition_config_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_process_t;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.software.*;
import org.aoju.bus.health.unix.AixLibc;
import org.aoju.bus.health.unix.aix.drivers.Uptime;
import org.aoju.bus.health.unix.aix.drivers.Who;
import org.aoju.bus.health.unix.aix.drivers.perfstat.PerfstatConfig;
import org.aoju.bus.health.unix.aix.drivers.perfstat.PerfstatProcess;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * AIX (Advanced Interactive eXecutive) is a series of proprietary Unix
 * operating systems developed and sold by IBM for several of its computer
 * platforms.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@ThreadSafe
public class AixOperatingSystem extends AbstractOperatingSystem {

    private static final long BOOTTIME = querySystemBootTimeMillis() / 1000L;
    private final Supplier<perfstat_partition_config_t> config = Memoize.memoize(PerfstatConfig::queryConfig);
    private final Supplier<perfstat_process_t[]> procCpu = Memoize.memoize(PerfstatProcess::queryProcesses,
            Memoize.defaultExpiration());

    private static long querySystemBootTimeMillis() {
        long bootTime = Who.queryBootTime();
        if (bootTime >= 1000L) {
            return bootTime;
        }
        return System.currentTimeMillis() - Uptime.queryUpTime();
    }

    @Override
    public String queryManufacturer() {
        return "IBM";
    }

    @Override
    public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
        perfstat_partition_config_t cfg = config.get();

        String systemName = System.getProperty("os.name");
        String archName = System.getProperty("os.arch");
        String versionNumber = System.getProperty("os.version");
        if (StringKit.isBlank(versionNumber)) {
            versionNumber = Executor.getFirstAnswer("oslevel");
        }
        String releaseNumber = Native.toString(cfg.OSBuild);
        if (StringKit.isBlank(releaseNumber)) {
            releaseNumber = Executor.getFirstAnswer("oslevel -s");
        } else {
            // strip leading date
            int idx = releaseNumber.lastIndexOf(' ');
            if (idx > 0 && idx < releaseNumber.length()) {
                releaseNumber = releaseNumber.substring(idx + 1);
            }
        }
        return Pair.of(systemName, new OperatingSystem.OSVersionInfo(versionNumber, archName, releaseNumber));
    }

    @Override
    protected int queryBitness(int jvmBitness) {
        if (jvmBitness == 64) {
            return 64;
        }
        // 9th bit of conf is 64-bit kernel
        return (config.get().conf & 0x0080_0000) > 0 ? 64 : 32;
    }

    @Override
    public FileSystem getFileSystem() {
        return new AixFileSystem();
    }

    @Override
    public InternetProtocolStats getInternetProtocolStats() {
        return new AixInternetProtocolStats();
    }

    @Override
    public List<OSProcess> queryAllProcesses() {
        return getProcessListFromProcfs(-1);
    }

    @Override
    public List<OSProcess> queryChildProcesses(int parentPid) {
        List<OSProcess> allProcs = queryAllProcesses();
        Set<Integer> descendantPids = getChildrenOrDescendants(allProcs, parentPid, false);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public List<OSProcess> queryDescendantProcesses(int parentPid) {
        List<OSProcess> allProcs = queryAllProcesses();
        Set<Integer> descendantPids = getChildrenOrDescendants(allProcs, parentPid, true);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public OSProcess getProcess(int pid) {
        List<OSProcess> procs = getProcessListFromProcfs(pid);
        if (procs.isEmpty()) {
            return null;
        }
        return procs.get(0);
    }

    private List<OSProcess> getProcessListFromProcfs(int pid) {
        List<OSProcess> procs = new ArrayList<>();
        // Fetch user/system times from perfstat
        perfstat_process_t[] perfstat = procCpu.get();
        Map<Integer, Pair<Long, Long>> cpuMap = new HashMap<>();
        for (perfstat_process_t stat : perfstat) {
            int statpid = (int) stat.pid;
            if (pid < 0 || statpid == pid) {
                cpuMap.put(statpid, Pair.of((long) stat.ucpu_time, (long) stat.scpu_time));
            }
        }

        // Keys of this map are pids
        for (Entry<Integer, Pair<Long, Long>> entry : cpuMap.entrySet()) {
            OSProcess proc = new AixOSProcess(entry.getKey(), entry.getValue(), procCpu, this);
            if (proc.getState() != OSProcess.State.INVALID) {
                procs.add(proc);
            }
        }
        return procs;
    }

    @Override
    public int getProcessId() {
        return AixLibc.INSTANCE.getpid();
    }

    @Override
    public int getProcessCount() {
        return procCpu.get().length;
    }

    @Override
    public int getThreadId() {
        return AixLibc.INSTANCE.thread_self();
    }

    @Override
    public OSThread getCurrentThread() {
        OSProcess proc = getCurrentProcess();
        final int tid = getThreadId();
        return proc.getThreadDetails().stream().filter(t -> t.getThreadId() == tid).findFirst()
                .orElse(new AixOSThread(proc.getProcessID(), tid));
    }


    @Override
    public int getThreadCount() {
        long tc = 0L;
        for (perfstat_process_t proc : procCpu.get()) {
            tc += proc.num_threads;
        }
        return (int) tc;
    }

    @Override
    public long getSystemUptime() {
        return System.currentTimeMillis() / 1000L - BOOTTIME;
    }

    @Override
    public long getSystemBootTime() {
        return BOOTTIME;
    }

    @Override
    public NetworkParams getNetworkParams() {
        return new AixNetworkParams();
    }

    @Override
    public List<OSService> getServices() {
        List<OSService> services = new ArrayList<>();
        // Get system services from lssrc command
        /*-
         Output:
         Subsystem         Group            PID          Status
            platform_agent                    2949214      active
            cimsys                            2490590      active
            snmpd            tcpip            2883698      active
            syslogd          ras              2359466      active
            sendmail         mail             3145828      active
            portmap          portmap          2818188      active
            inetd            tcpip            2752656      active
            lpd              spooler                       inoperative
                        ...
         */
        List<String> systemServicesInfoList = Executor.runNative("lssrc -a");
        if (systemServicesInfoList.size() > 1) {
            systemServicesInfoList.remove(0); // remove header
            for (String systemService : systemServicesInfoList) {
                String[] serviceSplit = RegEx.SPACES.split(systemService.trim());
                if (systemService.contains("active")) {
                    if (serviceSplit.length == 4) {
                        services.add(new OSService(serviceSplit[0], Builder.parseIntOrDefault(serviceSplit[2], 0),
                                OSService.State.RUNNING));
                    } else if (serviceSplit.length == 3) {
                        services.add(new OSService(serviceSplit[0], Builder.parseIntOrDefault(serviceSplit[1], 0),
                                OSService.State.RUNNING));
                    }
                } else if (systemService.contains("inoperative")) {
                    services.add(new OSService(serviceSplit[0], 0, OSService.State.STOPPED));
                }
            }
        }
        // Get installed services from /etc/rc.d/init.d
        File dir = new File("/etc/rc.d/init.d");
        File[] listFiles;
        if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
            for (File file : listFiles) {
                String installedService = Executor.getFirstAnswer(file.getAbsolutePath() + " status");
                // Apache httpd daemon is running with PID 3997858.
                if (installedService.contains("running")) {
                    services.add(new OSService(file.getName(), Builder.parseLastInt(installedService, 0), OSService.State.RUNNING));
                } else {
                    services.add(new OSService(file.getName(), 0, OSService.State.STOPPED));
                }
            }
        }
        return services;
    }

}
