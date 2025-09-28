/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org OSHI and other contributors.               ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.health.windows.driver.registry;

import java.util.*;

import org.miaixz.bus.health.Parsing;
import org.miaixz.bus.health.builtin.software.ApplicationInfo;
import org.miaixz.bus.logger.Logger;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public final class InstalledAppsData {

    private static final Map<WinReg.HKEY, List<String>> REGISTRY_PATHS = new HashMap<>();
    private static final int[] ACCESS_FLAGS = { WinNT.KEY_WOW64_64KEY, WinNT.KEY_WOW64_32KEY };

    static {
        REGISTRY_PATHS.put(
                WinReg.HKEY_LOCAL_MACHINE,
                Arrays.asList(
                        "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall",
                        "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall"));

        REGISTRY_PATHS.put(
                WinReg.HKEY_CURRENT_USER,
                Arrays.asList("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall"));
    }

    public static List<ApplicationInfo> queryInstalledApps() {
        Set<ApplicationInfo> appInfoSet = new LinkedHashSet<>();

        // Iterate through both HKLM and HKCU paths
        for (Map.Entry<WinReg.HKEY, List<String>> entry : REGISTRY_PATHS.entrySet()) {
            WinReg.HKEY rootKey = entry.getKey();
            List<String> uninstallPaths = entry.getValue();

            for (String registryPath : uninstallPaths) {
                for (int accessFlag : ACCESS_FLAGS) {
                    String[] keys = Advapi32Util.registryGetKeys(rootKey, registryPath, accessFlag);
                    for (String key : keys) {
                        String fullPath = registryPath + "\\" + key;
                        try {
                            String name = getRegistryValueOrUnknown(rootKey, fullPath, "DisplayName", accessFlag);
                            if (name == null) {
                                continue;
                            }
                            String version = getRegistryValueOrUnknown(rootKey, fullPath, "DisplayVersion", accessFlag);
                            String publisher = getRegistryValueOrUnknown(rootKey, fullPath, "Publisher", accessFlag);
                            String installDate = getRegistryValueOrUnknown(
                                    rootKey,
                                    fullPath,
                                    "InstallDate",
                                    accessFlag);
                            String installLocation = getRegistryValueOrUnknown(
                                    rootKey,
                                    fullPath,
                                    "InstallLocation",
                                    accessFlag);
                            String installSource = getRegistryValueOrUnknown(
                                    rootKey,
                                    fullPath,
                                    "InstallSource",
                                    accessFlag);

                            long installDateEpoch = Parsing.parseDateToEpoch(installDate, "yyyyMMdd");

                            Map<String, String> additionalInfo = new LinkedHashMap<>();
                            additionalInfo.put("installLocation", installLocation);
                            additionalInfo.put("installSource", installSource);

                            ApplicationInfo app = new ApplicationInfo(name, version, publisher, installDateEpoch,
                                    additionalInfo);
                            appInfoSet.add(app);
                        } catch (Win32Exception e) {
                            // Skip keys that are inaccessible or have missing values
                        }
                    }
                }
            }
        }

        return new ArrayList<>(appInfoSet);
    }

    private static String getRegistryValueOrUnknown(WinReg.HKEY rootKey, String path, String key, int accessFlag) {
        try {
            String value = Advapi32Util.registryGetStringValue(rootKey, path, key, accessFlag);
            if (value != null && !value.trim().isEmpty()) {
                return value;
            }
        } catch (Win32Exception e) {
            Logger.trace("Unable to access " + path + " with flag " + accessFlag + ": " + e.getMessage());
        }
        return null;
    }

}
