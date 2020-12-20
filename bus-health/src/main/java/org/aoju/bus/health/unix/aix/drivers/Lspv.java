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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.health.unix.aix.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.builtin.hardware.HWPartition;

import java.util.*;
import java.util.Map.Entry;

/**
 * Utility to query lspv
 *
 * @author Kimi Liu
 * @version 6.1.6
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Lspv {

    private Lspv() {
    }

    /**
     * Query {@code lspv} to get partition info
     *
     * @param device    The disk to get the volumes from
     * @param majMinMap A map of device name to a pair with major and minor numbers.
     * @return A pair containing the model and serial number for the device, or null
     * if not found
     */
    public static List<HWPartition> queryLogicalVolumes(String device, Map<String, Pair<Integer, Integer>> majMinMap) {
        /*-
         $ lspv -L hdisk0
        PHYSICAL VOLUME:    hdisk0                   VOLUME GROUP:     rootvg
        PV IDENTIFIER:      000acfde95524f85 VG IDENTIFIER     000acfde00004c000000000395525276
        PV STATE:           active
        STALE PARTITIONS:   0                        ALLOCATABLE:      yes
        PP SIZE:            128 megabyte(s)          LOGICAL VOLUMES:  12
        TOTAL PPs:          271 (34688 megabytes)    VG DESCRIPTORS:   2
        FREE PPs:           227 (29056 megabytes)    HOT SPARE:        no
        USED PPs:           44 (5632 megabytes)      MAX REQUEST:      256 kilobytes
        FREE DISTRIBUTION:  54..46..19..54..54
        USED DISTRIBUTION:  01..08..35..00..00
         */
        String stateMarker = "PV STATE:";
        String sizeMarker = "PP SIZE:";
        long ppSize = 0L; // All physical partitions are the same size
        for (String s : Executor.runNative("lspv -L " + device)) {
            if (s.startsWith(stateMarker)) {
                if (!s.contains("active")) {
                    return Collections.emptyList();
                }
            } else if (s.contains(sizeMarker)) {
                ppSize = Builder.getFirstIntValue(s);
            }
        }
        if (ppSize == 0L) {
            return Collections.emptyList();
        }
        // Convert to megabytes
        ppSize <<= 20;
        /*-
         $ lspv -p hdisk0
        hdisk0:
        PP RANGE  STATE   REGION        LV NAME             TYPE       MOUNT POINT
        1-1     used    outer edge    hd5                 boot       N/A
        2-55    free    outer edge
        56-59    used    outer middle  hd6                 paging     N/A
        60-61    used    outer middle  livedump            jfs2       /var/adm/ras/livedump
        62-62    used    outer middle  loglv01             jfslog     N/A
        63-63    used    outer middle  lv01                jfs        N/A
        64-109   free    outer middle
        110-110   used    center        hd8                 jfs2log    N/A
        111-112   used    center        hd4                 jfs2       /
        113-128   used    center        hd2                 jfs2       /usr
        129-131   used    center        hd9var              jfs2       /var
        132-132   used    center        hd3                 jfs2       /tmp
        133-133   used    center        hd9var              jfs2       /var
        134-136   used    center        hd10opt             jfs2       /opt
        137-137   used    center        hd11admin           jfs2       /admin
        138-140   used    center        hd2                 jfs2       /usr
        141-141   used    center        hd3                 jfs2       /tmp
        142-142   used    center        hd4                 jfs2       /
        143-143   used    center        hd9var              jfs2       /var
        144-144   used    center        hd2                 jfs2       /usr
        145-163   free    center
        164-217   free    inner middle
        218-271   free    inner edge
         */
        Map<String, String> mountMap = new HashMap<>();
        Map<String, String> typeMap = new HashMap<>();
        Map<String, Integer> ppMap = new HashMap<>();
        for (String s : Executor.runNative("lspv -p " + device)) {
            String[] split = RegEx.SPACES.split(s.trim());
            if (split.length >= 6 && "used".equals(split[1])) {
                // Region may have two words, so count from end
                String name = split[split.length - 3];
                mountMap.put(name, split[split.length - 1]);
                typeMap.put(name, split[split.length - 2]);
                int ppCount = 1 + Builder.getNthIntValue(split[0], 2) - Builder.getNthIntValue(split[0], 1);
                ppMap.put(name, ppCount + ppMap.getOrDefault(name, 0));
            }
        }
        List<HWPartition> partitions = new ArrayList<>();
        for (Entry<String, String> entry : mountMap.entrySet()) {
            String mount = "N/A".equals(entry.getValue()) ? "" : entry.getValue();
            // All maps should have same keys
            String name = entry.getKey();
            String type = typeMap.get(name);
            long size = ppSize * ppMap.get(name);
            Pair<Integer, Integer> majMin = majMinMap.get(name);
            int major = majMin == null ? Builder.getFirstIntValue(name) : majMin.getLeft();
            int minor = majMin == null ? Builder.getFirstIntValue(name) : majMin.getRight();
            partitions.add(new HWPartition(name, name, type, "", size, major, minor, mount));
        }
        return partitions;
    }

}
