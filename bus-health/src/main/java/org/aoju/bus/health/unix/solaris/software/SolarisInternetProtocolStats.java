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
package org.aoju.bus.health.unix.solaris.software;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.builtin.software.AbstractInternetProtocolStats;

import java.util.List;

/**
 * Internet Protocol Stats implementation
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
@ThreadSafe
public class SolarisInternetProtocolStats extends AbstractInternetProtocolStats {

    private static TcpStats getTcpStats() {
        long connectionsEstablished = 0;
        long connectionsActive = 0;
        long connectionsPassive = 0;
        long connectionFailures = 0;
        long connectionsReset = 0;
        long segmentsSent = 0;
        long segmentsReceived = 0;
        long segmentsRetransmitted = 0;
        long inErrors = 0;
        long outResets = 0;
        List<String> netstat = Executor.runNative("netstat -s -P tcp");
        // append IP
        netstat.addAll(Executor.runNative("netstat -s -P ip"));
        for (String s : netstat) {
            // Two stats per line. Split the strings by index of "tcp"
            String[] stats = splitOnPrefix(s, "tcp");
            // Now of form tcpXX = 123
            for (String stat : stats) {
                if (null != stat) {
                    String[] split = stat.split(Symbol.EQUAL);
                    if (split.length == 2) {
                        switch (split[0].trim()) {
                            case "tcpCurrEstab":
                                connectionsEstablished = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpActiveOpens":
                                connectionsActive = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpPassiveOpens":
                                connectionsPassive = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpAttemptFails":
                                connectionFailures = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpEstabResets":
                                connectionsReset = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpOutSegs":
                                segmentsSent = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpInSegs":
                                segmentsReceived = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpRetransSegs":
                                segmentsRetransmitted = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpInErr":
                                // doesn't have tcp in second column
                                inErrors = Builder.getFirstIntValue(split[1].trim());
                                break;
                            case "tcpOutRsts":
                                outResets = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return new TcpStats(connectionsEstablished, connectionsActive, connectionsPassive, connectionFailures,
                connectionsReset, segmentsSent, segmentsReceived, segmentsRetransmitted, inErrors, outResets);
    }

    private static UdpStats getUdpStats() {
        long datagramsSent = 0;
        long datagramsReceived = 0;
        long datagramsNoPort = 0;
        long datagramsReceivedErrors = 0;
        List<String> netstat = Executor.runNative("netstat -s -P udp");
        // append IP
        netstat.addAll(Executor.runNative("netstat -s -P ip"));
        for (String s : netstat) {
            // Two stats per line. Split the strings by index of "udp"
            String[] stats = splitOnPrefix(s, "udp");
            // Now of form udpXX = 123
            for (String stat : stats) {
                if (null != stat) {
                    String[] split = stat.split(Symbol.EQUAL);
                    if (split.length == 2) {
                        switch (split[0].trim()) {
                            case "udpOutDatagrams":
                                datagramsSent = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpInDatagrams":
                                datagramsReceived = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpNoPorts":
                                datagramsNoPort = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpInErrors":
                                datagramsReceivedErrors = Builder.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return new UdpStats(datagramsSent, datagramsReceived, datagramsNoPort, datagramsReceivedErrors);
    }

    private static String[] splitOnPrefix(String s, String prefix) {
        String[] stats = new String[2];
        int first = s.indexOf(prefix);
        if (first >= 0) {
            int second = s.indexOf(prefix, first + 1);
            if (second >= 0) {
                stats[0] = s.substring(first, second).trim();
                stats[1] = s.substring(second).trim();
            } else {
                stats[0] = s.substring(first).trim();
            }
        }
        return stats;
    }

    @Override
    public TcpStats getTCPv4Stats() {
        return getTcpStats();
    }

    @Override
    public UdpStats getUDPv4Stats() {
        return getUdpStats();
    }

}
