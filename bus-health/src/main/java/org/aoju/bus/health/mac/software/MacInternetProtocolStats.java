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
package org.aoju.bus.health.mac.software;

import com.sun.jna.Memory;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.software.AbstractInternetProtocolStats;
import org.aoju.bus.health.builtin.software.InternetProtocolStats;
import org.aoju.bus.health.mac.SysctlKit;
import org.aoju.bus.health.mac.SystemB;
import org.aoju.bus.health.unix.CLibrary;
import org.aoju.bus.health.unix.NetStat;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Internet Protocol Stats implementation
 *
 * @author Kimi Liu
 * @version 6.2.6
 * @since JDK 1.8+
 */
@ThreadSafe
public class MacInternetProtocolStats extends AbstractInternetProtocolStats {

    private final Supplier<CLibrary.BsdTcpstat> tcpstat = Memoize.memoize(MacInternetProtocolStats::queryTcpstat, Memoize.defaultExpiration());
    private final Supplier<CLibrary.BsdUdpstat> udpstat = Memoize.memoize(MacInternetProtocolStats::queryUdpstat, Memoize.defaultExpiration());
    // With elevated permissions use tcpstat only
    // Backup estimate get ipstat and subtract off udp
    private final Supplier<CLibrary.BsdIpstat> ipstat = Memoize.memoize(MacInternetProtocolStats::queryIpstat, Memoize.defaultExpiration());
    private final Supplier<CLibrary.BsdIp6stat> ip6stat = Memoize.memoize(MacInternetProtocolStats::queryIp6stat, Memoize.defaultExpiration());
    private final Supplier<Pair<Long, Long>> establishedv4v6 = Memoize.memoize(NetStat::queryTcpnetstat, Memoize.defaultExpiration());
    private boolean isElevated;


    public MacInternetProtocolStats(boolean elevated) {
        this.isElevated = elevated;
    }

    private static CLibrary.BsdTcpstat queryTcpstat() {
        CLibrary.BsdTcpstat mt = new CLibrary.BsdTcpstat();
        Memory m = SysctlKit.sysctl("net.inet.tcp.stats");
        if (null != m && m.size() >= 128) {
            mt.tcps_connattempt = m.getInt(0);
            mt.tcps_accepts = m.getInt(4);
            mt.tcps_drops = m.getInt(12);
            mt.tcps_conndrops = m.getInt(16);
            mt.tcps_sndpack = m.getInt(64);
            mt.tcps_sndrexmitpack = m.getInt(72);
            mt.tcps_rcvpack = m.getInt(104);
            mt.tcps_rcvbadsum = m.getInt(112);
            mt.tcps_rcvbadoff = m.getInt(116);
            mt.tcps_rcvmemdrop = m.getInt(120);
            mt.tcps_rcvshort = m.getInt(124);
        }
        return mt;
    }

    private static CLibrary.BsdIpstat queryIpstat() {
        CLibrary.BsdIpstat mi = new CLibrary.BsdIpstat();
        Memory m = SysctlKit.sysctl("net.inet.ip.stats");
        if (null != m && m.size() >= 60) {
            mi.ips_total = m.getInt(0);
            mi.ips_badsum = m.getInt(4);
            mi.ips_tooshort = m.getInt(8);
            mi.ips_toosmall = m.getInt(12);
            mi.ips_badhlen = m.getInt(16);
            mi.ips_badlen = m.getInt(20);
            mi.ips_delivered = m.getInt(56);
        }
        return mi;
    }

    private static CLibrary.BsdIp6stat queryIp6stat() {
        CLibrary.BsdIp6stat mi6 = new CLibrary.BsdIp6stat();
        Memory m = SysctlKit.sysctl("net.inet6.ip6.stats");
        if (null != m && m.size() >= 96) {
            mi6.ip6s_total = m.getLong(0);
            mi6.ip6s_localout = m.getLong(88);
        }
        return mi6;
    }

    public static CLibrary.BsdUdpstat queryUdpstat() {
        CLibrary.BsdUdpstat ut = new CLibrary.BsdUdpstat();
        Memory m = SysctlKit.sysctl("net.inet.udp.stats");
        if (null != m && m.size() >= 1644) {
            ut.udps_ipackets = m.getInt(0);
            ut.udps_hdrops = m.getInt(4);
            ut.udps_badsum = m.getInt(8);
            ut.udps_badlen = m.getInt(12);
            ut.udps_opackets = m.getInt(36);
            ut.udps_noportmcast = m.getInt(48);
            ut.udps_rcv6_swcsum = m.getInt(64);
            ut.udps_snd6_swcsum = m.getInt(80);
        }
        return ut;
    }

    /*
     * There are multiple versions of some tcp/udp/ip stats structures in macOS.
     * Since we only need a few of the hundreds of fields, we can improve
     * performance by selectively reading the ints from the appropriate offsets,
     * which are consistent across the structure.
     */

    private static List<Integer> queryFdList(int pid) {
        List<Integer> fdList = new ArrayList<>();
        int bufferSize = SystemB.INSTANCE.proc_pidinfo(pid, SystemB.PROC_PIDLISTFDS, 0, null, 0);
        if (bufferSize > 0) {
            SystemB.ProcFdInfo fdInfo = new SystemB.ProcFdInfo();
            int numStructs = bufferSize / fdInfo.size();
            SystemB.ProcFdInfo[] fdArray = (SystemB.ProcFdInfo[]) fdInfo.toArray(numStructs);
            bufferSize = SystemB.INSTANCE.proc_pidinfo(pid, SystemB.PROC_PIDLISTFDS, 0, fdArray[0], bufferSize);
            numStructs = bufferSize / fdInfo.size();
            for (int i = 0; i < numStructs; i++) {
                if (fdArray[i].proc_fdtype == SystemB.PROX_FDTYPE_SOCKET) {
                    fdList.add(fdArray[i].proc_fd);
                }
            }
        }
        return fdList;
    }

    private static IPConnection queryIPConnection(int pid, int fd) {
        SystemB.SocketFdInfo si = new SystemB.SocketFdInfo();
        int ret = SystemB.INSTANCE.proc_pidfdinfo(pid, fd, SystemB.PROC_PIDFDSOCKETINFO, si, si.size());
        if (si.size() == ret && si.psi.soi_family == SystemB.AF_INET || si.psi.soi_family == SystemB.AF_INET6) {
            SystemB.InSockInfo ini;
            String type;
            TcpState state;
            if (si.psi.soi_kind == SystemB.SOCKINFO_TCP) {
                si.psi.soi_proto.setType("pri_tcp");
                si.psi.soi_proto.read();
                ini = si.psi.soi_proto.pri_tcp.tcpsi_ini;
                state = stateLookup(si.psi.soi_proto.pri_tcp.tcpsi_state);
                type = "tcp";
            } else if (si.psi.soi_kind == SystemB.SOCKINFO_IN) {
                si.psi.soi_proto.setType("pri_in");
                si.psi.soi_proto.read();
                ini = si.psi.soi_proto.pri_in;
                state = InternetProtocolStats.TcpState.NONE;
                type = "udp";
            } else {
                return null;
            }

            byte[] laddr;
            byte[] faddr;
            if (ini.insi_vflag == 1) {
                laddr = Builder.parseIntToIP(ini.insi_laddr[3]);
                faddr = Builder.parseIntToIP(ini.insi_faddr[3]);
                type += "4";
            } else if (ini.insi_vflag == 2) {
                laddr = Builder.parseIntArrayToIP(ini.insi_laddr);
                faddr = Builder.parseIntArrayToIP(ini.insi_faddr);
                type += "6";
            } else {
                return null;
            }
            int lport = Builder.bigEndian16ToLittleEndian(ini.insi_lport);
            int fport = Builder.bigEndian16ToLittleEndian(ini.insi_fport);
            return new IPConnection(type, laddr, lport, faddr, fport, state, si.psi.soi_qlen, si.psi.soi_incqlen, pid);
        }
        return null;
    }

    private static TcpState stateLookup(int state) {
        switch (state) {
            case 0:
                return InternetProtocolStats.TcpState.CLOSED;
            case 1:
                return InternetProtocolStats.TcpState.LISTEN;
            case 2:
                return InternetProtocolStats.TcpState.SYN_SENT;
            case 3:
                return InternetProtocolStats.TcpState.SYN_RECV;
            case 4:
                return InternetProtocolStats.TcpState.ESTABLISHED;
            case 5:
                return InternetProtocolStats.TcpState.CLOSE_WAIT;
            case 6:
                return InternetProtocolStats.TcpState.FIN_WAIT_1;
            case 7:
                return InternetProtocolStats.TcpState.CLOSING;
            case 8:
                return InternetProtocolStats.TcpState.LAST_ACK;
            case 9:
                return InternetProtocolStats.TcpState.FIN_WAIT_2;
            case 10:
                return InternetProtocolStats.TcpState.TIME_WAIT;
            default:
                return InternetProtocolStats.TcpState.UNKNOWN;
        }
    }

    @Override
    public TcpStats getTCPv4Stats() {
        CLibrary.BsdTcpstat tcp = tcpstat.get();
        if (this.isElevated) {
            return new TcpStats(establishedv4v6.get().getLeft(), Builder.unsignedIntToLong(tcp.tcps_connattempt),
                    Builder.unsignedIntToLong(tcp.tcps_accepts), Builder.unsignedIntToLong(tcp.tcps_conndrops),
                    Builder.unsignedIntToLong(tcp.tcps_drops), Builder.unsignedIntToLong(tcp.tcps_sndpack),
                    Builder.unsignedIntToLong(tcp.tcps_rcvpack), Builder.unsignedIntToLong(tcp.tcps_sndrexmitpack),
                    Builder.unsignedIntToLong(
                            tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort),
                    0L);
        }
        CLibrary.BsdIpstat ip = ipstat.get();
        CLibrary.BsdUdpstat udp = udpstat.get();
        return new TcpStats(establishedv4v6.get().getLeft(), Builder.unsignedIntToLong(tcp.tcps_connattempt),
                Builder.unsignedIntToLong(tcp.tcps_accepts), Builder.unsignedIntToLong(tcp.tcps_conndrops),
                Builder.unsignedIntToLong(tcp.tcps_drops),
                Math.max(0L, Builder.unsignedIntToLong(ip.ips_delivered - udp.udps_opackets)),
                Math.max(0L, Builder.unsignedIntToLong(ip.ips_total - udp.udps_ipackets)),
                Builder.unsignedIntToLong(tcp.tcps_sndrexmitpack),
                Math.max(0L, Builder.unsignedIntToLong(ip.ips_badsum + ip.ips_tooshort + ip.ips_toosmall
                        + ip.ips_badhlen + ip.ips_badlen - udp.udps_hdrops + udp.udps_badsum + udp.udps_badlen)),
                0L);
    }

    @Override
    public TcpStats getTCPv6Stats() {
        CLibrary.BsdIp6stat ip6 = ip6stat.get();
        CLibrary.BsdUdpstat udp = udpstat.get();
        return new TcpStats(establishedv4v6.get().getRight(), 0L, 0L, 0L, 0L,
                ip6.ip6s_localout - Builder.unsignedIntToLong(udp.udps_snd6_swcsum),
                ip6.ip6s_total - Builder.unsignedIntToLong(udp.udps_rcv6_swcsum), 0L, 0L, 0L);
    }

    @Override
    public UdpStats getUDPv4Stats() {
        CLibrary.BsdUdpstat stat = udpstat.get();
        return new UdpStats(Builder.unsignedIntToLong(stat.udps_opackets),
                Builder.unsignedIntToLong(stat.udps_ipackets), Builder.unsignedIntToLong(stat.udps_noportmcast),
                Builder.unsignedIntToLong(stat.udps_hdrops + stat.udps_badsum + stat.udps_badlen));
    }

    @Override
    public UdpStats getUDPv6Stats() {
        CLibrary.BsdUdpstat stat = udpstat.get();
        return new UdpStats(Builder.unsignedIntToLong(stat.udps_snd6_swcsum),
                Builder.unsignedIntToLong(stat.udps_rcv6_swcsum), 0L, 0L);
    }

    @Override
    public List<IPConnection> getConnections() {
        List<IPConnection> conns = new ArrayList<>();
        int[] pids = new int[1024];
        int numberOfProcesses = SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, pids, pids.length * SystemB.INT_SIZE)
                / SystemB.INT_SIZE;
        for (int i = 0; i < numberOfProcesses; i++) {
            // Handle off-by-one bug in proc_listpids where the size returned
            // is: SystemB.INT_SIZE * (pids + 1)
            if (pids[i] > 0) {
                for (Integer fd : queryFdList(pids[i])) {
                    IPConnection ipc = queryIPConnection(pids[i], fd);
                    if (null != ipc) {
                        conns.add(ipc);
                    }
                }
            }
        }
        return conns;
    }

}
