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
package org.aoju.bus.health.mac.hardware;

import com.sun.jna.platform.mac.IOKit.IOConnect;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.health.builtin.hardware.AbstractSensors;
import org.aoju.bus.health.mac.SmcKit;

/**
 * Sensors from SMC
 *
 * @author Kimi Liu
 * @version 6.1.9
 * @since JDK 1.8+
 */
@ThreadSafe
final class MacSensors extends AbstractSensors {

    // This shouldn't change once determined
    private int numFans = 0;

    @Override
    public double queryCpuTemperature() {
        IOConnect conn = SmcKit.smcOpen();
        double temp = SmcKit.smcGetFloat(conn, SmcKit.SMC_KEY_CPU_TEMP);
        SmcKit.smcClose(conn);
        if (temp > 0d) {
            return temp;
        }
        return 0d;
    }

    @Override
    public int[] queryFanSpeeds() {
        // If we don't have fan # try to get it
        IOConnect conn = SmcKit.smcOpen();
        if (this.numFans == 0) {
            this.numFans = (int) SmcKit.smcGetLong(conn, SmcKit.SMC_KEY_FAN_NUM);
        }
        int[] fanSpeeds = new int[this.numFans];
        for (int i = 0; i < this.numFans; i++) {
            fanSpeeds[i] = (int) SmcKit.smcGetFloat(conn, String.format(SmcKit.SMC_KEY_FAN_SPEED, i));
        }
        SmcKit.smcClose(conn);
        return fanSpeeds;
    }

    @Override
    public double queryCpuVoltage() {
        IOConnect conn = SmcKit.smcOpen();
        double volts = SmcKit.smcGetFloat(conn, SmcKit.SMC_KEY_CPU_VOLTAGE) / 1000d;
        SmcKit.smcClose(conn);
        return volts;
    }

}
