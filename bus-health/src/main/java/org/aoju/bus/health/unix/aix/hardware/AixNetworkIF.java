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
package org.aoju.bus.health.unix.aix.hardware;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractNetworkIF;
import org.aoju.bus.health.builtin.hardware.NetworkIF;
import org.aoju.bus.health.unix.aix.drivers.perfstat.PerfstatNetInterface;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * AIXNetworks class.
 *
 * @author Kimi Liu
 * @version 6.1.9
 * @since JDK 1.8+
 */
@ThreadSafe
public final class AixNetworkIF extends AbstractNetworkIF {

    private long bytesRecv;
    private long bytesSent;
    private long packetsRecv;
    private long packetsSent;
    private long inErrors;
    private long outErrors;
    private long inDrops;
    private long collisions;
    private long speed;
    private long timeStamp;

    private Supplier<Perfstat.perfstat_netinterface_t[]> netstats;

    public AixNetworkIF(NetworkInterface netint, Supplier<Perfstat.perfstat_netinterface_t[]> netstats) {
        super(netint);
        this.netstats = netstats;
        updateAttributes();
    }

    /**
     * Gets all network interfaces on this machine
     *
     * @param includeLocalInterfaces include local interfaces in the result
     * @return A list of {@link NetworkIF} objects representing the interfaces
     */
    public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
        Supplier<Perfstat.perfstat_netinterface_t[]> netstats = Memoize.memoize(PerfstatNetInterface::queryNetInterfaces,
                Memoize.defaultExpiration());
        List<NetworkIF> ifList = new ArrayList<>();
        for (NetworkInterface ni : getNetworkInterfaces(includeLocalInterfaces)) {
            ifList.add(new AixNetworkIF(ni, netstats));
        }
        return ifList;
    }

    @Override
    public long getBytesRecv() {
        return this.bytesRecv;
    }

    @Override
    public long getBytesSent() {
        return this.bytesSent;
    }

    @Override
    public long getPacketsRecv() {
        return this.packetsRecv;
    }

    @Override
    public long getPacketsSent() {
        return this.packetsSent;
    }

    @Override
    public long getInErrors() {
        return this.inErrors;
    }

    @Override
    public long getOutErrors() {
        return this.outErrors;
    }

    @Override
    public long getInDrops() {
        return this.inDrops;
    }

    @Override
    public long getCollisions() {
        return this.collisions;
    }

    @Override
    public long getSpeed() {
        return this.speed;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public boolean updateAttributes() {
        Perfstat.perfstat_netinterface_t[] stats = netstats.get();
        long now = System.currentTimeMillis();
        for (Perfstat.perfstat_netinterface_t stat : stats) {
            String name = Native.toString(stat.name);
            if (name.equals(this.getName())) {
                this.bytesSent = stat.obytes;
                this.bytesRecv = stat.ibytes;
                this.packetsSent = stat.opackets;
                this.packetsRecv = stat.ipackets;
                this.outErrors = stat.oerrors;
                this.inErrors = stat.ierrors;
                this.collisions = stat.collisions;
                this.inDrops = stat.if_iqdrops;
                this.speed = stat.bitrate;
                this.timeStamp = now;
                return true;
            }
        }
        return false;
    }

}
