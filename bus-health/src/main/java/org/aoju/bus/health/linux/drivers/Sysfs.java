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
package org.aoju.bus.health.linux.drivers;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.health.Builder;

/**
 * Utility to read info from {@code sysfs}
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Sysfs {

    private Sysfs() {
    }

    /**
     * Query the vendor from sysfs
     *
     * @return The vendor if available, null otherwise
     */
    public static String querySystemVendor() {
        final String sysVendor = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "sys_vendor").trim();
        if (!sysVendor.isEmpty()) {
            return sysVendor;
        }
        return null;
    }

    /**
     * Query the model from sysfs
     *
     * @return The model if available, null otherwise
     */
    public static String queryProductModel() {
        final String productName = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "product_name").trim();
        final String productVersion = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "product_version")
                .trim();
        if (productName.isEmpty()) {
            if (!productVersion.isEmpty()) {
                return productVersion;
            }
        } else {
            if (!productVersion.isEmpty() && !"None".equals(productVersion)) {
                return productName + " (version: " + productVersion + ")";
            } else {
                return productName;
            }
        }
        return null;
    }

    /**
     * Query the product serial number from sysfs
     *
     * @return The serial number if available, null otherwise
     */
    public static String queryProductSerial() {
        String serial = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "product_serial");
        if (!serial.isEmpty() && !"None".equals(serial)) {
            return serial;
        }
        return queryBoardSerial();
    }

    /**
     * Query the UUID from sysfs
     *
     * @return The UUID if available, null otherwise
     */
    public static String queryUUID() {
        // These sysfs files accessible by root, or can be chmod'd at boot time
        // to enable access without root
        String uuid = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "product_uuid");
        if (!uuid.isEmpty() && !"None".equals(uuid)) {
            return uuid;
        }
        return null;
    }

    /**
     * Query the board vendor from sysfs
     *
     * @return The board vendor if available, null otherwise
     */
    public static String queryBoardVendor() {
        final String boardVendor = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "board_vendor").trim();
        if (!boardVendor.isEmpty()) {
            return boardVendor;
        }
        return null;
    }

    /**
     * Query the board model from sysfs
     *
     * @return The board model if available, null otherwise
     */
    public static String queryBoardModel() {
        final String boardName = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "board_name").trim();
        if (!boardName.isEmpty()) {
            return boardName;
        }
        return null;
    }

    /**
     * Query the board version from sysfs
     *
     * @return The board version if available, null otherwise
     */
    public static String queryBoardVersion() {
        final String boardVersion = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "board_version").trim();
        if (!boardVersion.isEmpty()) {
            return boardVersion;
        }
        return null;
    }

    /**
     * Query the board serial number from sysfs
     *
     * @return The board serial number if available, null otherwise
     */
    public static String queryBoardSerial() {
        final String boardSerial = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "board_serial").trim();
        if (!boardSerial.isEmpty()) {
            return boardSerial;
        }
        return null;
    }

    /**
     * Query the bios vendor from sysfs
     *
     * @return The bios vendor if available, null otherwise
     */
    public static String queryBiosVendor() {
        final String biosVendor = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "bios_vendor").trim();
        if (biosVendor.isEmpty()) {
            return biosVendor;
        }
        return null;
    }

    /**
     * Query the bios description from sysfs
     *
     * @return The bios description if available, null otherwise
     */
    public static String queryBiosDescription() {
        final String modalias = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "modalias").trim();
        if (!modalias.isEmpty()) {
            return modalias;
        }
        return null;
    }

    /**
     * Query the bios version from sysfs
     *
     * @param biosRevision A revision string to append
     * @return The bios version if available, null otherwise
     */
    public static String queryBiosVersion(String biosRevision) {
        final String biosVersion = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "bios_version").trim();
        if (!biosVersion.isEmpty()) {
            return biosVersion + (StringKit.isBlank(biosRevision) ? Normal.EMPTY : " (revision " + biosRevision + ")");
        }
        return null;
    }

    /**
     * Query the bios release date from sysfs
     *
     * @return The bios release date if available, null otherwise
     */
    public static String queryBiosReleaseDate() {
        final String biosDate = Builder.getStringFromFile(Builder.SYSFS_SERIAL_PATH + "bios_date").trim();
        if (!biosDate.isEmpty()) {
            return Builder.parseMmDdYyyyToYyyyMmDD(biosDate);
        }
        return null;
    }

}
