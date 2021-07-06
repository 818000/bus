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
package org.aoju.bus.health.linux.hardware;

import com.sun.jna.platform.linux.Udev;
import com.sun.jna.platform.linux.Udev.UdevContext;
import com.sun.jna.platform.linux.Udev.UdevDevice;
import com.sun.jna.platform.linux.Udev.UdevEnumerate;
import com.sun.jna.platform.linux.Udev.UdevListEntry;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.FileKit;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.builtin.hardware.AbstractHWDiskStore;
import org.aoju.bus.health.builtin.hardware.HWDiskStore;
import org.aoju.bus.health.builtin.hardware.HWPartition;
import org.aoju.bus.health.linux.ProcPath;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Linux hard disk implementation.
 *
 * @author Kimi Liu
 * @version 6.2.5
 * @since JDK 1.8+
 */
@ThreadSafe
public final class LinuxHWDiskStore extends AbstractHWDiskStore {

    private static final String BLOCK = "block";
    private static final String DISK = "disk";
    private static final String PARTITION = "partition";

    private static final String STAT = "stat";
    private static final String SIZE = "size";
    private static final String MINOR = "MINOR";
    private static final String MAJOR = "MAJOR";

    private static final String ID_FS_TYPE = "ID_FS_TYPE";
    private static final String ID_FS_UUID = "ID_FS_UUID";
    private static final String ID_MODEL = "ID_MODEL";
    private static final String ID_SERIAL_SHORT = "ID_SERIAL_SHORT";

    private static final String DM_UUID = "DM_UUID";
    private static final String DM_VG_NAME = "DM_VG_NAME";
    private static final String DM_LV_NAME = "DM_LV_NAME";
    private static final String LOGICAL_VOLUME_GROUP = "Logical Volume Group";
    private static final String DEV_LOCATION = "/dev/";
    private static final String DEV_MAPPER = DEV_LOCATION + "mapper/";

    private static final int SECTORSIZE = 512;

    // Get a list of orders to pass to Builder
    private static final int[] UDEV_STAT_ORDERS = new int[UdevStat.values().length];
    // There are at least 11 elements in udev stat output or sometimes 15. We want
    // the rightmost 11 or 15 if there is leading text.
    private static final int UDEV_STAT_LENGTH;

    static {
        for (UdevStat stat : UdevStat.values()) {
            UDEV_STAT_ORDERS[stat.ordinal()] = stat.getOrder();
        }
    }

    static {
        String stat = Builder.getStringFromFile(ProcPath.DISKSTATS);
        int statLength = 11;
        if (!stat.isEmpty()) {
            statLength = Builder.countStringToLongArray(stat, Symbol.C_SPACE);
        }
        UDEV_STAT_LENGTH = statLength;
    }

    private long reads = 0L;
    private long readBytes = 0L;
    private long writes = 0L;
    private long writeBytes = 0L;
    private long currentQueueLength = 0L;
    private long transferTime = 0L;
    private long timeStamp = 0L;
    private List<HWPartition> partitionList = new ArrayList<>();

    private LinuxHWDiskStore(String name, String model, String serial, long size) {
        super(name, model, serial, size);
    }

    /**
     * Gets the disks on this machine
     *
     * @return an {@code UnmodifiableList} of {@link HWDiskStore} objects
     * representing the disks
     */
    public static List<HWDiskStore> getDisks() {
        return Collections.unmodifiableList(getDisks(null));
    }

    private static List<HWDiskStore> getDisks(LinuxHWDiskStore storeToUpdate) {
        LinuxHWDiskStore store = null;
        List<HWDiskStore> result = new ArrayList<>();

        Map<String, String> mountsMap = readMountsMap();

        UdevContext udev = Udev.INSTANCE.udev_new();
        try {
            UdevEnumerate enumerate = udev.enumerateNew();
            try {
                enumerate.addMatchSubsystem(BLOCK);
                enumerate.scanDevices();
                for (UdevListEntry entry = enumerate.getListEntry(); null != entry; entry = entry.getNext()) {
                    String syspath = entry.getName();
                    UdevDevice device = udev.deviceNewFromSyspath(syspath);
                    if (null != device) {
                        try {
                            // devnode is what we use as name, like /dev/sda
                            String devnode = device.getDevnode();
                            // Ignore loopback and ram disks; do nothing
                            if (null != devnode && !devnode.startsWith("/dev/loop")
                                    && !devnode.startsWith("/dev/ram")) {
                                if (DISK.equals(device.getDevtype())) {
                                    // Null model and serial in virtual environments
                                    String devModel = device.getPropertyValue(ID_MODEL);
                                    String devSerial = device.getPropertyValue(ID_SERIAL_SHORT);
                                    long devSize = Builder.parseLongOrDefault(device.getSysattrValue(SIZE), 0L)
                                            * SECTORSIZE;
                                    if (devnode.startsWith("/dev/dm")) {
                                        devModel = LOGICAL_VOLUME_GROUP;
                                        devSerial = device.getPropertyValue(DM_UUID);
                                        store = new LinuxHWDiskStore(devnode, devModel,
                                                devSerial == null ? Normal.UNKNOWN : devSerial, devSize);
                                        String vgName = device.getPropertyValue(DM_VG_NAME);
                                        String lvName = device.getPropertyValue(DM_LV_NAME);
                                        store.partitionList.add(new HWPartition(
                                                getPartitionNameForDmDevice(vgName, lvName), device.getSysname(),
                                                device.getPropertyValue(ID_FS_TYPE) == null ? PARTITION
                                                        : device.getPropertyValue(ID_FS_TYPE),
                                                device.getPropertyValue(ID_FS_UUID) == null ? ""
                                                        : device.getPropertyValue(ID_FS_UUID),
                                                Builder.parseLongOrDefault(device.getSysattrValue(SIZE), 0L)
                                                        * SECTORSIZE,
                                                Builder.parseIntOrDefault(device.getPropertyValue(MAJOR), 0),
                                                Builder.parseIntOrDefault(device.getPropertyValue(MINOR), 0),
                                                getMountPointOfDmDevice(vgName, lvName)));
                                    } else {
                                        store = new LinuxHWDiskStore(devnode,
                                                devModel == null ? Normal.UNKNOWN : devModel,
                                                devSerial == null ? Normal.UNKNOWN : devSerial, devSize);
                                    }
                                    if (null == storeToUpdate) {
                                        // If getting all stores, add to the list with stats
                                        computeDiskStats(store, device.getSysattrValue(STAT));
                                        result.add(store);
                                    } else if (store.getName().equals(storeToUpdate.getName())
                                            && store.getModel().equals(storeToUpdate.getModel())
                                            && store.getSerial().equals(storeToUpdate.getSerial())
                                            && store.getSize() == storeToUpdate.getSize()) {
                                        // If we are only updating a single disk, the name, model, serial, and size are
                                        // sufficient to test if this is a match. Add the (old) object, release handle
                                        // and return.
                                        computeDiskStats(storeToUpdate, device.getSysattrValue(STAT));
                                        result.add(storeToUpdate);
                                        break;
                                    }
                                } else if (null == storeToUpdate && null != store // only add if getting new list
                                        && PARTITION.equals(device.getDevtype())) {
                                    // udev_device_get_parent_*() does not take a reference on the returned device,
                                    // it is automatically unref'd with the parent
                                    UdevDevice parent = device.getParentWithSubsystemDevtype(BLOCK, DISK);
                                    if (null != parent && store.getName().equals(parent.getDevnode())) {
                                        // `store` should still point to the parent HWDiskStore this partition is
                                        // attached to. If not, it's an error, so skip.
                                        String name = device.getDevnode();
                                        store.partitionList.add(new HWPartition(name, device.getSysname(),
                                                null == device.getPropertyValue(ID_FS_TYPE) ? PARTITION
                                                        : device.getPropertyValue(ID_FS_TYPE),
                                                null == device.getPropertyValue(ID_FS_UUID) ? Normal.EMPTY
                                                        : device.getPropertyValue(ID_FS_UUID),
                                                Builder.parseLongOrDefault(device.getSysattrValue(SIZE), 0L)
                                                        * SECTORSIZE,
                                                Builder.parseIntOrDefault(device.getPropertyValue(MAJOR), 0),
                                                Builder.parseIntOrDefault(device.getPropertyValue(MINOR), 0),
                                                mountsMap.getOrDefault(name,
                                                        getDependentNamesFromHoldersDirectory(device.getSysname()))));
                                    }
                                }
                            }
                        } finally {
                            device.unref();
                        }
                    }
                }
            } finally {
                enumerate.unref();
            }
        } finally {
            udev.unref();
        }
        // Iterate the list and make the partitions unmodifiable
        for (HWDiskStore hwds : result) {
            ((LinuxHWDiskStore) hwds).partitionList = Collections.unmodifiableList(hwds.getPartitions().stream()
                    .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
        }
        return result;
    }

    private static Map<String, String> readMountsMap() {
        Map<String, String> mountsMap = new HashMap<>();
        List<String> mounts = FileKit.readLines(ProcPath.MOUNTS);
        for (String mount : mounts) {
            String[] split = RegEx.SPACES.split(mount);
            if (split.length < 2 || !split[0].startsWith(DEV_LOCATION)) {
                continue;
            }
            mountsMap.put(split[0], split[1]);
        }
        return mountsMap;
    }

    private static void computeDiskStats(LinuxHWDiskStore store, String devstat) {
        long[] devstatArray = Builder.parseStringToLongArray(devstat, UDEV_STAT_ORDERS, UDEV_STAT_LENGTH, Symbol.C_SPACE);
        store.timeStamp = System.currentTimeMillis();

        // Reads and writes are converted in bytes
        store.reads = devstatArray[UdevStat.READS.ordinal()];
        store.readBytes = devstatArray[UdevStat.READ_BYTES.ordinal()] * SECTORSIZE;
        store.writes = devstatArray[UdevStat.WRITES.ordinal()];
        store.writeBytes = devstatArray[UdevStat.WRITE_BYTES.ordinal()] * SECTORSIZE;
        store.currentQueueLength = devstatArray[UdevStat.QUEUE_LENGTH.ordinal()];
        store.transferTime = devstatArray[UdevStat.ACTIVE_MS.ordinal()];
    }

    private static String getPartitionNameForDmDevice(String vgName, String lvName) {
        return new StringBuilder().append(DEV_LOCATION).append(vgName).append('/').append(lvName).toString();
    }

    private static String getMountPointOfDmDevice(String vgName, String lvName) {
        return new StringBuilder().append(DEV_MAPPER).append(vgName).append('-').append(lvName).toString();
    }

    private static String getDependentNamesFromHoldersDirectory(String sysPath) {
        File holdersDir = new File(sysPath + "/holders");
        File[] holders = holdersDir.listFiles();
        if (holders != null) {
            return Arrays.stream(holders).map(File::getName).collect(Collectors.joining(" "));
        }
        return Normal.EMPTY;
    }

    @Override
    public long getReads() {
        return reads;
    }

    @Override
    public long getReadBytes() {
        return readBytes;
    }

    @Override
    public long getWrites() {
        return writes;
    }

    @Override
    public long getWriteBytes() {
        return writeBytes;
    }

    @Override
    public long getCurrentQueueLength() {
        return currentQueueLength;
    }

    @Override
    public long getTransferTime() {
        return transferTime;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public List<HWPartition> getPartitions() {
        return this.partitionList;
    }

    @Override
    public boolean updateAttributes() {
        // If this returns non-empty (the same store, but updated) then we were
        // successful in the update
        return !getDisks(this).isEmpty();
    }

    // Order the field is in udev stats
    enum UdevStat {
        // The parsing implementation in Builder requires these to be declared
        // in increasing order. Use 0-ordered index here
        READS(0), READ_BYTES(2), WRITES(4), WRITE_BYTES(6), QUEUE_LENGTH(8), ACTIVE_MS(9);

        private int order;

        UdevStat(int order) {
            this.order = order;
        }

        public int getOrder() {
            return this.order;
        }
    }

}
