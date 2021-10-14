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
package org.aoju.bus.health.unix.aix.hardware;

import org.aoju.bus.core.annotation.Immutable;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.builtin.hardware.AbstractGraphicsCard;
import org.aoju.bus.health.builtin.hardware.GraphicsCard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Graphics Card info obtained from lscfg
 *
 * @author Kimi Liu
 * @version 6.3.0
 * @since JDK 1.8+
 */
@Immutable
final class AixGraphicsCard extends AbstractGraphicsCard {

    /**
     * Constructor for AixGraphicsCard
     *
     * @param name        The name
     * @param deviceId    The device ID
     * @param vendor      The vendor
     * @param versionInfo The version info
     * @param vram        The VRAM
     */
    AixGraphicsCard(String name, String deviceId, String vendor, String versionInfo, long vram) {
        super(name, deviceId, vendor, versionInfo, vram);
    }

    /**
     * Gets graphics cards
     *
     * @param lscfg A memoized lscfg list
     * @return List of graphics cards
     */
    public static List<GraphicsCard> getGraphicsCards(Supplier<List<String>> lscfg) {
        List<GraphicsCard> cardList = new ArrayList<>();
        boolean display = false;
        String name = null;
        String vendor = null;
        List<String> versionInfo = new ArrayList<>();
        for (String line : lscfg.get()) {
            String s = line.trim();
            if (s.startsWith("Name:") && s.contains("display")) {
                display = true;
            } else if (display && s.toLowerCase().contains("graphics")) {
                name = s;
            } else if (display && null != name) {
                if (s.startsWith("Manufacture ID")) {
                    vendor = Builder.removeLeadingDots(s.substring(14));
                } else if (s.contains("Level")) {
                    versionInfo.add(s.replaceAll("\\.\\.+", Symbol.EQUAL));
                } else if (s.startsWith("Hardware Location Code")) {
                    cardList.add(new AixGraphicsCard(name, Normal.UNKNOWN,
                            StringKit.isBlank(vendor) ? Normal.UNKNOWN : vendor,
                            versionInfo.isEmpty() ? Normal.UNKNOWN : String.join(Symbol.COMMA, versionInfo), 0L));
                    display = false;
                }
            }
        }
        return cardList;
    }

}
