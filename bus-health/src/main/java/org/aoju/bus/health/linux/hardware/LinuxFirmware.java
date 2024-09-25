/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org OSHI and other contributors.                 *
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
import org.aoju.bus.core.lang.RegEx;
import org.aoju.bus.core.lang.tuple.Pair;
import org.aoju.bus.health.Executor;
import org.aoju.bus.health.Memoize;
import org.aoju.bus.health.builtin.hardware.AbstractFirmware;
import org.aoju.bus.health.linux.drivers.Dmidecode;
import org.aoju.bus.health.linux.drivers.Sysfs;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Firmware data obtained by sysfs.
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@Immutable
final class LinuxFirmware extends AbstractFirmware {

    private static final DateTimeFormatter VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss",
            Locale.ENGLISH);
    private final Supplier<VcGenCmdStrings> vcGenCmd = Memoize.memoize(LinuxFirmware::queryVcGenCmd);
    private final Supplier<String> manufacturer = Memoize.memoize(this::queryManufacturer);
    private final Supplier<String> description = Memoize.memoize(this::queryDescription);
    private final Supplier<String> releaseDate = Memoize.memoize(this::queryReleaseDate);
    private final Supplier<Pair<String, String>> biosNameRev = Memoize.memoize(Dmidecode::queryBiosNameRev);
    private final Supplier<String> version = Memoize.memoize(this::queryVersion);
    private final Supplier<String> name = Memoize.memoize(this::queryName);

    private static VcGenCmdStrings queryVcGenCmd() {
        String vcReleaseDate;
        String vcManufacturer;
        String vcVersion;

        List<String> vcgencmd = Executor.runNative("vcgencmd version");
        if (vcgencmd.size() >= 3) {
            // First line is date
            try {
                vcReleaseDate = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse(vcgencmd.get(0)));
            } catch (DateTimeParseException e) {
                vcReleaseDate = Normal.UNKNOWN;
            }
            // Second line is copyright
            String[] copyright = RegEx.SPACES.split(vcgencmd.get(1));
            vcManufacturer = copyright[copyright.length - 1];
            // Third line is version
            vcVersion = vcgencmd.get(2).replace("version ", "");
            return new VcGenCmdStrings(vcReleaseDate, vcManufacturer, vcVersion, "RPi", "Bootloader");
        }
        return new VcGenCmdStrings(null, null, null, null, null);
    }

    @Override
    public String getManufacturer() {
        return manufacturer.get();
    }

    @Override
    public String getDescription() {
        return description.get();
    }

    @Override
    public String getVersion() {
        return version.get();
    }

    @Override
    public String getReleaseDate() {
        return releaseDate.get();
    }

    @Override
    public String getName() {
        return name.get();
    }

    private String queryManufacturer() {
        String result;
        if ((result = Sysfs.queryBiosVendor()) == null && (result = vcGenCmd.get().manufacturer) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private String queryDescription() {
        String result;
        if ((result = Sysfs.queryBiosDescription()) == null && (result = vcGenCmd.get().description) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private String queryVersion() {
        String result;
        if ((result = Sysfs.queryBiosVersion(this.biosNameRev.get().getRight())) == null
                && (result = vcGenCmd.get().version) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private String queryReleaseDate() {
        String result;
        if ((result = Sysfs.queryBiosReleaseDate()) == null && (result = vcGenCmd.get().releaseDate) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private String queryName() {
        String result;
        if ((result = biosNameRev.get().getLeft()) == null && (result = vcGenCmd.get().name) == null) {
            return Normal.UNKNOWN;
        }
        return result;
    }

    private static final class VcGenCmdStrings {
        private final String releaseDate;
        private final String manufacturer;
        private final String version;
        private final String name;
        private final String description;

        private VcGenCmdStrings(String releaseDate, String manufacturer, String version, String name,
                String description) {
            this.releaseDate = releaseDate;
            this.manufacturer = manufacturer;
            this.version = version;
            this.name = name;
            this.description = description;
        }
    }

}
