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
package org.aoju.bus.health.linux.hardware;

import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractComputerSystem;
import org.aoju.bus.health.builtin.hardware.Baseboard;
import org.aoju.bus.health.builtin.hardware.Firmware;
import org.aoju.bus.health.linux.drivers.*;

import java.util.function.Supplier;

/**
 * Hardware data obtained from sysfs.
 *
 * @author Kimi Liu
 * @version 6.1.6
 * @since JDK 1.8+
 */
@Immutable
final class LinuxComputerSystem extends AbstractComputerSystem {

    private final Supplier<String> manufacturer = Memoize.memoize(LinuxComputerSystem::queryManufacturer);

    private final Supplier<String> model = Memoize.memoize(LinuxComputerSystem::queryModel);

    private final Supplier<String> serialNumber = Memoize.memoize(LinuxComputerSystem::querySerialNumber);

    private static String queryManufacturer() {
        String result;
        if ((result = Sysfs.querySystemVendor()) == null && (result = CpuInfo.queryCpuManufacturer()) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private static String queryModel() {
        String result;
        if ((result = Sysfs.queryProductModel()) == null && (result = Devicetree.queryModel()) == null
                && (result = Lshw.queryModel()) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private static String querySerialNumber() {
        String result;
        if ((result = Sysfs.queryProductSerial()) == null && (result = Dmidecode.querySerialNumber()) == null
                && (result = Lshal.querySerialNumber()) == null && (result = Lshw.querySerialNumber()) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    @Override
    public String getManufacturer() {
        return manufacturer.get();
    }

    @Override
    public String getModel() {
        return model.get();
    }

    @Override
    public String getSerialNumber() {
        return serialNumber.get();
    }

    @Override
    public Firmware createFirmware() {
        return new LinuxFirmware();
    }

    @Override
    public Baseboard createBaseboard() {
        return new LinuxBaseboard();
    }

}
