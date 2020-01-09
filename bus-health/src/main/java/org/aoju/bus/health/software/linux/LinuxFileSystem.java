/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.health.software.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.software.FileSystem;
import org.aoju.bus.health.software.OSFileStore;
import org.aoju.bus.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The Linux File System contains {@link OSFileStore}s which
 * are a storage pool, device, partition, volume, concrete file system or other
 * implementation specific means of file storage. In Linux, these are found in
 * the /proc/mount filesystem, excluding temporary and kernel mounts.
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public class LinuxFileSystem implements FileSystem {

    // Linux defines a set of virtual file systems
    private final List<String> pseudofs = Arrays.asList(//
            "rootfs", // Minimal fs to support kernel boot
            "sysfs", // SysFS file system
            "proc", // Proc file system
            "devtmpfs", // Dev temporary file system
            "devpts", // Dev pseudo terminal devices file system
            "securityfs", // Kernel security file system
            "cgroup", // Cgroup file system
            "pstore", // Pstore file system
            "hugetlbfs", // Huge pages support file system
            "configfs", // Config file system
            "selinuxfs", // SELinux file system
            "systemd-1", // Systemd file system
            "binfmt_misc", // Binary format support file system
            "mqueue", // Message queue file system
            "debugfs", // Debug file system
            "nfsd", // NFS file system
            "sunrpc", // Sun RPC file system
            "rpc_pipefs", // Sun RPC file system
            "fusectl", // FUSE control file system
            // NOTE: FUSE's fuseblk is not evalued because used as file system
            // representation of a FUSE block storage
            // "fuseblk" // FUSE block file system
            // "tmpfs", // Temporary file system
            // NOTE: tmpfs is evaluated apart, because Linux uses it for
            // RAMdisks
            "overlay" // Overlay file system https://wiki.archlinux.org/index.php/Overlay_filesystem
    );

    // System path mounted as tmpfs
    private final List<String> tmpfsPaths = Arrays.asList("/run", "/sys", "/proc");

    /**
     * <p>
     * updateFileStoreStats.
     * </p>
     *
     * @param osFileStore a {@link OSFileStore} object.
     * @return a boolean.
     */
    public static boolean updateFileStoreStats(OSFileStore osFileStore) {
        for (OSFileStore fileStore : new LinuxFileSystem().getFileStoreMatching(osFileStore.getName(), null)) {
            if (osFileStore.getVolume().equals(fileStore.getVolume())
                    && osFileStore.getMount().equals(fileStore.getMount())) {
                osFileStore.setLogicalVolume(fileStore.getLogicalVolume());
                osFileStore.setDescription(fileStore.getDescription());
                osFileStore.setType(fileStore.getType());
                osFileStore.setFreeSpace(fileStore.getFreeSpace());
                osFileStore.setUsableSpace(fileStore.getUsableSpace());
                osFileStore.setTotalSpace(fileStore.getTotalSpace());
                osFileStore.setFreeInodes(fileStore.getFreeInodes());
                osFileStore.setTotalInodes(fileStore.getTotalInodes());
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if file path equals or starts with an element in the given list
     *
     * @param aList   A list of path prefixes
     * @param charSeq a path to check
     * @return true if the charSeq exactly equals, or starts with the directory in
     * aList
     */
    private boolean listElementStartsWith(List<String> aList, String charSeq) {
        for (String match : aList) {
            if (charSeq.equals(match) || charSeq.startsWith(match + Symbol.SLASH)) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets File System Information.
     */
    @Override
    public OSFileStore[] getFileStores() {
        // Map uuids with device path as key
        Map<String, String> uuidMap = new HashMap<>();
        File uuidDir = new File("/dev/disk/by-uuid");
        if (uuidDir.listFiles() != null) {
            for (File uuid : uuidDir.listFiles()) {
                try {
                    // Store UUID as value with path (e.g., /dev/sda1) as key
                    uuidMap.put(uuid.getCanonicalPath(), uuid.getName().toLowerCase());
                } catch (IOException e) {
                    Logger.error("Couldn't get canonical path for {}. {}", uuid.getName(), e);
                }
            }
        }

        // List file systems
        List<OSFileStore> fsList = getFileStoreMatching(null, uuidMap);

        return fsList.toArray(new OSFileStore[0]);
    }

    private List<OSFileStore> getFileStoreMatching(String nameToMatch, Map<String, String> uuidMap) {
        List<OSFileStore> fsList = new ArrayList<>();

        // Parse /proc/self/mounts to get fs types
        List<String> mounts = Builder.readFile("/proc/self/mounts");
        for (String mount : mounts) {
            String[] split = mount.split(Symbol.SPACE);
            // As reported in fstab(5) manpage, struct is:
            // 1st field is volume name
            // 2nd field is path with spaces escaped as \040
            // 3rd field is fs type
            // 4th field is mount options (ignored)
            // 5th field is used by dump(8) (ignored)
            // 6th field is fsck order (ignored)
            if (split.length < 6) {
                continue;
            }

            // Exclude pseudo file systems
            String path = split[1].replaceAll("\\\\040", Symbol.SPACE);
            String type = split[2];
            if (this.pseudofs.contains(type) // exclude non-fs types
                    || path.equals("/dev") // exclude plain dev directory
                    || listElementStartsWith(this.tmpfsPaths, path) // well known prefixes
                    || path.endsWith("/shm") // exclude shared memory
            ) {
                continue;
            }

            String name = split[0].replaceAll("\\\\040", Symbol.SPACE);
            if (path.equals(Symbol.SLASH)) {
                name = Symbol.SLASH;
            }

            // If only updating for one name, skip others
            if (nameToMatch != null && !nameToMatch.equals(name)) {
                continue;
            }

            String volume = split[0].replaceAll("\\\\040", Symbol.SPACE);
            String uuid = uuidMap != null ? uuidMap.getOrDefault(split[0], Normal.EMPTY) : Normal.EMPTY;

            String description;
            if (volume.startsWith("/dev")) {
                description = "Local Disk";
            } else if (volume.equals("tmpfs")) {
                description = "Ram Disk";
            } else if (type.startsWith("nfs") || type.equals("cifs")) {
                description = "Network Disk";
            } else {
                description = "Mount Point";
            }

            // Add in logical volume found at /dev/mapper, useful when linking
            // file system with drive.
            String logicalVolume = Normal.EMPTY;
            String volumeMapperDirectory = "/dev/mapper/";
            Path link = Paths.get(volume);
            if (link.toFile().exists() && Files.isSymbolicLink(link)) {
                try {
                    Path slink = Files.readSymbolicLink(link);
                    Path full = Paths.get(volumeMapperDirectory + slink.toString());
                    if (full.toFile().exists()) {
                        logicalVolume = full.normalize().toString();
                    }
                } catch (IOException e) {
                    Logger.warn("Couldn't access symbolic path  {}. {}", link, e);
                }
            }

            long totalInodes = 0L;
            long freeInodes = 0L;
            long totalSpace = 0L;
            long usableSpace = 0L;
            long freeSpace = 0L;

            try {
                LibC.Statvfs vfsStat = new LibC.Statvfs();
                if (0 == LibC.INSTANCE.statvfs(path, vfsStat)) {
                    totalInodes = vfsStat.f_files.longValue();
                    freeInodes = vfsStat.f_ffree.longValue();
                    totalSpace = vfsStat.f_blocks.longValue() * vfsStat.f_bsize.longValue();
                    usableSpace = vfsStat.f_bavail.longValue() * vfsStat.f_bsize.longValue();
                    freeSpace = vfsStat.f_bfree.longValue() * vfsStat.f_bsize.longValue();
                } else {
                    File tmpFile = new File(path);
                    totalSpace = tmpFile.getTotalSpace();
                    usableSpace = tmpFile.getUsableSpace();
                    freeSpace = tmpFile.getFreeSpace();
                    Logger.warn("Failed to get information to use statvfs. path: {}, Error code: {}", path,
                            Native.getLastError());
                }
            } catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
                Logger.error("Failed to get file counts from statvfs. {}", e);
            }

            OSFileStore osStore = new OSFileStore();
            osStore.setName(name);
            osStore.setVolume(volume);
            osStore.setMount(path);
            osStore.setDescription(description);
            osStore.setType(type);
            osStore.setUUID(uuid);
            osStore.setFreeSpace(freeSpace);
            osStore.setUsableSpace(usableSpace);
            osStore.setTotalSpace(totalSpace);
            osStore.setFreeInodes(freeInodes);
            osStore.setTotalInodes(totalInodes);
            osStore.setLogicalVolume(logicalVolume);

            fsList.add(osStore);
        }
        return fsList;
    }

    @Override
    public long getOpenFileDescriptors() {
        return getFileDescriptors(0);
    }

    @Override
    public long getMaxFileDescriptors() {
        return getFileDescriptors(2);
    }

    /**
     * Returns a value from the Linux system file /proc/sys/fs/file-nr.
     *
     * @param index The index of the value to retrieve. 0 returns the total allocated
     *              file descriptors. 1 returns the number of used file descriptors
     *              for kernel 2.4, or the number of unused file descriptors for
     *              kernel 2.6. 2 returns the maximum number of file descriptors that
     *              can be allocated.
     * @return Corresponding file descriptor value from the Linux system file.
     */
    private long getFileDescriptors(int index) {
        String filename = "/proc/sys/fs/file-nr";
        if (index < 0 || index > 2) {
            throw new IllegalArgumentException("Index must be between 0 and 2.");
        }
        List<String> osDescriptors = Builder.readFile(filename);
        if (!osDescriptors.isEmpty()) {
            String[] splittedLine = osDescriptors.get(0).split("\\D+");
            return Builder.parseLongOrDefault(splittedLine[index], 0L);
        }
        return 0L;
    }
}
