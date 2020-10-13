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
package org.aoju.bus.health.unix.solaris.hardware;

import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.builtin.hardware.AbstractSensors;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensors from prtpicl
 *
 * @author Kimi Liu
 * @version 6.1.1
 * @since JDK 1.8+
 */
@ThreadSafe
final class SolarisSensors extends AbstractSensors {

    @Override
    public double queryCpuTemperature() {
        double maxTemp = 0d;
        // Return max found temp
        for (String line : Executor.runNative("/usr/sbin/prtpicl -v -c temperature-sensor")) {
            if (line.trim().startsWith("Temperature:")) {
                int temp = Builder.parseLastInt(line, 0);
                if (temp > maxTemp) {
                    maxTemp = temp;
                }
            }
        }
        // If it's in millidegrees:
        if (maxTemp > 1000) {
            maxTemp /= 1000;
        }
        return maxTemp;
    }

    @Override
    public int[] queryFanSpeeds() {
        List<Integer> speedList = new ArrayList<>();
        // Return max found temp
        for (String line : Executor.runNative("/usr/sbin/prtpicl -v -c fan")) {
            if (line.trim().startsWith("Speed:")) {
                speedList.add(Builder.parseLastInt(line, 0));
            }
        }
        int[] fans = new int[speedList.size()];
        for (int i = 0; i < speedList.size(); i++) {
            fans[i] = speedList.get(i);
        }
        return fans;
    }

    @Override
    public double queryCpuVoltage() {
        double voltage = 0d;
        for (String line : Executor.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
            if (line.trim().startsWith("Voltage:")) {
                voltage = Builder.parseDoubleOrDefault(line.replace("Voltage:", Normal.EMPTY).trim(), 0d);
                break;
            }
        }
        return voltage;
    }

}
