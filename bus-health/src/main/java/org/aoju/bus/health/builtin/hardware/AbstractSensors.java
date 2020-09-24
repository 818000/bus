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
 ********************************************************************************/
package org.aoju.bus.health.builtin.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.Memoize;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * 来自WMI或Open Hardware Monitor的传感器
 *
 * @author Kimi Liu
 * @version 6.1.0
 * @since JDK 1.8+
 */
@ThreadSafe
public abstract class AbstractSensors implements Sensors {

    private final Supplier<Double> cpuTemperature = Memoize.memoize(this::queryCpuTemperature, Memoize.defaultExpiration());

    private final Supplier<int[]> fanSpeeds = Memoize.memoize(this::queryFanSpeeds, Memoize.defaultExpiration());

    private final Supplier<Double> cpuVoltage = Memoize.memoize(this::queryCpuVoltage, Memoize.defaultExpiration());

    @Override
    public double getCpuTemperature() {
        return cpuTemperature.get();
    }

    protected abstract double queryCpuTemperature();

    @Override
    public int[] getFanSpeeds() {
        return fanSpeeds.get();
    }

    protected abstract int[] queryFanSpeeds();

    @Override
    public double getCpuVoltage() {
        return cpuVoltage.get();
    }

    protected abstract double queryCpuVoltage();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CPU Temperature=").append(getCpuTemperature()).append("°C, ");
        sb.append("Fan Speeds=").append(Arrays.toString(getFanSpeeds())).append(", ");
        sb.append("CPU Voltage=").append(getCpuVoltage());
        return sb.toString();
    }

}
