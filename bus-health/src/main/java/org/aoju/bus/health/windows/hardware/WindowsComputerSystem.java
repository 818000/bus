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
package org.aoju.bus.health.windows.hardware;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractComputerSystem;
import org.aoju.bus.health.builtin.hardware.Baseboard;
import org.aoju.bus.health.builtin.hardware.Firmware;
import org.aoju.bus.health.windows.WmiKit;
import org.aoju.bus.health.windows.drivers.Win32Bios;
import org.aoju.bus.health.windows.drivers.Win32ComputerSystem;
import org.aoju.bus.health.windows.drivers.Win32ComputerSystemProduct;

import java.util.function.Supplier;

/**
 * Hardware data obtained from WMI.
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
@Immutable
final class WindowsComputerSystem extends AbstractComputerSystem {

    private final Supplier<Pair<String, String>> manufacturerModel = Memoize.memoize(
            WindowsComputerSystem::queryManufacturerModel);
    private final Supplier<Pair<String, String>> serialNumberUUID = Memoize.memoize(
            WindowsComputerSystem::querySystemSerialNumberUUID);

    private static Pair<String, String> queryManufacturerModel() {
        String manufacturer = null;
        String model = null;
        WmiResult<Win32ComputerSystem.ComputerSystemProperty> win32ComputerSystem = Win32ComputerSystem.queryComputerSystem();
        if (win32ComputerSystem.getResultCount() > 0) {
            manufacturer = WmiKit.getString(win32ComputerSystem, Win32ComputerSystem.ComputerSystemProperty.MANUFACTURER, 0);
            model = WmiKit.getString(win32ComputerSystem, Win32ComputerSystem.ComputerSystemProperty.MODEL, 0);
        }
        return Pair.of(StringKit.isBlank(manufacturer) ? Normal.UNKNOWN : manufacturer,
                StringKit.isBlank(model) ? Normal.UNKNOWN : model);
    }

    private static Pair<String, String> querySystemSerialNumberUUID() {
        String serialNumber = null;
        String uuid = null;
        WmiResult<Win32ComputerSystemProduct.ComputerSystemProductProperty> win32ComputerSystemProduct = Win32ComputerSystemProduct
                .queryIdentifyingNumberUUID();
        if (win32ComputerSystemProduct.getResultCount() > 0) {
            serialNumber = WmiKit.getString(win32ComputerSystemProduct,
                    Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0);
            uuid = WmiKit.getString(win32ComputerSystemProduct, Win32ComputerSystemProduct.ComputerSystemProductProperty.UUID, 0);
        }
        if (StringKit.isBlank(serialNumber)) {
            serialNumber = querySerialFromBios();
        }
        if (StringKit.isBlank(serialNumber)) {
            serialNumber = Normal.UNKNOWN;
        }
        if (StringKit.isBlank(uuid)) {
            uuid = Normal.UNKNOWN;
        }
        return Pair.of(serialNumber, uuid);
    }

    private static String querySerialFromBios() {
        WmiResult<Win32Bios.BiosSerialProperty> serialNum = Win32Bios.querySerialNumber();
        if (serialNum.getResultCount() > 0) {
            return WmiKit.getString(serialNum, Win32Bios.BiosSerialProperty.SERIALNUMBER, 0);
        }
        return null;
    }

    @Override
    public String getManufacturer() {
        return manufacturerModel.get().getLeft();
    }

    @Override
    public String getModel() {
        return manufacturerModel.get().getRight();
    }

    @Override
    public String getSerialNumber() {
        return serialNumberUUID.get().getLeft();
    }

    @Override
    public String getHardwareUUID() {
        return serialNumberUUID.get().getRight();
    }

    @Override
    public Firmware createFirmware() {
        return new WindowsFirmware();
    }

    @Override
    public Baseboard createBaseboard() {
        return new WindowsBaseboard();
    }

}
