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
package org.aoju.bus.health.unix.openbsd.hardware;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractComputerSystem;
import org.aoju.bus.health.builtin.hardware.Baseboard;
import org.aoju.bus.health.builtin.hardware.Firmware;
import org.aoju.bus.health.unix.UnixBaseboard;
import org.aoju.bus.health.unix.openbsd.OpenBsdSysctlKit;

import java.util.function.Supplier;

/**
 * OpenBSD ComputerSystem implementation
 *
 * @author Kimi Liu
 * @version 6.2.2
 * @since JDK 1.8+
 */
public class OpenBsdComputerSystem extends AbstractComputerSystem {

    private final Supplier<String> manufacturer = Memoize.memoize(OpenBsdComputerSystem::queryManufacturer);

    private final Supplier<String> model = Memoize.memoize(OpenBsdComputerSystem::queryModel);

    private final Supplier<String> serialNumber = Memoize.memoize(OpenBsdComputerSystem::querySerialNumber);

    private final Supplier<String> uuid = Memoize.memoize(OpenBsdComputerSystem::queryUUID);

    private static String queryManufacturer() {
        return OpenBsdSysctlKit.sysctl("hw.vendor", Normal.UNKNOWN);
    }

    private static String queryModel() {
        return OpenBsdSysctlKit.sysctl("hw.version", Normal.UNKNOWN);
    }

    private static String querySerialNumber() {
        return OpenBsdSysctlKit.sysctl("hw.serialno", Normal.UNKNOWN);
    }

    private static String queryUUID() {
        return OpenBsdSysctlKit.sysctl("hw.uuid", Normal.UNKNOWN);
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
    public String getHardwareUUID() {
        return uuid.get();
    }

    @Override
    protected Firmware createFirmware() {
        return new OpenBsdFirmware();
    }

    @Override
    protected Baseboard createBaseboard() {
        return new UnixBaseboard(manufacturer.get(), model.get(), serialNumber.get(),
                OpenBsdSysctlKit.sysctl("hw.product", Normal.UNKNOWN));
    }

}
