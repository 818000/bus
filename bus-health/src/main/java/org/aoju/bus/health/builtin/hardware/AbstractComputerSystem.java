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
package org.aoju.bus.health.builtin.hardware;

import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.health.Memoize;

import java.util.function.Supplier;

/**
 * 计算机系统数据
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
@Immutable
public abstract class AbstractComputerSystem implements ComputerSystem {

    private final Supplier<Firmware> firmware = Memoize.memoize(this::createFirmware);

    private final Supplier<Baseboard> baseboard = Memoize.memoize(this::createBaseboard);

    @Override
    public Firmware getFirmware() {
        return firmware.get();
    }

    /**
     * 实例化特定于平台的{@link Firmware}对象
     *
     * @return 特定于平台的{@link Firmware}对象
     */
    protected abstract Firmware createFirmware();

    @Override
    public Baseboard getBaseboard() {
        return baseboard.get();
    }

    /**
     * 实例化特定于平台的{@link Baseboard}对象
     *
     * @return 特定于平台的{@link Baseboard}对象
     */
    protected abstract Baseboard createBaseboard();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("manufacturer=").append(getManufacturer()).append(", ");
        sb.append("model=").append(getModel()).append(", ");
        sb.append("serial number=").append(getSerialNumber()).append(", ");
        sb.append("uuid=").append(getHardwareUUID());
        return sb.toString();
    }

}
