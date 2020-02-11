/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.health.hardware.unix.solaris;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.health.Builder;
import org.aoju.bus.health.Command;
import org.aoju.bus.health.hardware.AbstractUsbDevice;
import org.aoju.bus.health.hardware.UsbDevice;

import java.util.*;

/**
 * <p>
 * SolarisUsbDevice class.
 * </p>
 *
 * @author Kimi Liu
 * @version 5.5.8
 * @since JDK 1.8+
 */
public class SolarisUsbDevice extends AbstractUsbDevice {

    /**
     * <p>
     * Constructor for SolarisUsbDevice.
     * </p>
     *
     * @param name             a {@link java.lang.String} object.
     * @param vendor           a {@link java.lang.String} object.
     * @param vendorId         a {@link java.lang.String} object.
     * @param productId        a {@link java.lang.String} object.
     * @param serialNumber     a {@link java.lang.String} object.
     * @param uniqueDeviceId   a {@link java.lang.String} object.
     * @param connectedDevices an array of {@link UsbDevice} objects.
     */
    public SolarisUsbDevice(String name, String vendor, String vendorId, String productId, String serialNumber,
                            String uniqueDeviceId, UsbDevice[] connectedDevices) {
        super(name, vendor, vendorId, productId, serialNumber, uniqueDeviceId, connectedDevices);
    }

    /**
     * {@inheritDoc}
     *
     * @param tree a boolean.
     * @return an array of {@link UsbDevice} objects.
     */
    public static UsbDevice[] getUsbDevices(boolean tree) {
        UsbDevice[] devices = getUsbDevices();
        if (tree) {
            return devices;
        }
        List<UsbDevice> deviceList = new ArrayList<>();
        // Top level is controllers; they won't be added to the list, but all
        // their connected devices will be
        for (UsbDevice device : devices) {
            deviceList.add(new SolarisUsbDevice(device.getName(), device.getVendor(), device.getVendorId(),
                    device.getProductId(), device.getSerialNumber(), device.getUniqueDeviceId(),
                    new SolarisUsbDevice[0]));
            addDevicesToList(deviceList, device.getConnectedDevices());
        }
        return deviceList.toArray(new UsbDevice[0]);
    }

    private static UsbDevice[] getUsbDevices() {
        Map<String, String> nameMap = new HashMap<>();
        Map<String, String> vendorIdMap = new HashMap<>();
        Map<String, String> productIdMap = new HashMap<>();
        Map<String, List<String>> hubMap = new HashMap<>();
        Map<String, String> deviceTypeMap = new HashMap<>();

        // Enumerate all usb devices and build information maps
        List<String> devices = Command.runNative("prtconf -pv");
        if (devices.isEmpty()) {
            return new SolarisUsbDevice[0];
        }
        // For each item enumerated, store information in the maps
        Map<Integer, String> lastParent = new HashMap<>();
        String key = Normal.EMPTY;
        int indent = 0;
        List<String> usbControllers = new ArrayList<>();
        for (String line : devices) {
            // Node 0x... identifies start of a new tree
            if (line.contains("Node 0x")) {
                // Remove indent for key
                key = line.replaceFirst("^\\s*", Normal.EMPTY);
                // Calculate indent and store as last parent at this depth
                int depth = line.length() - key.length();
                // Store first indent for future use
                if (indent == 0) {
                    indent = depth;
                }
                // Store this Node ID as parent at this depth
                lastParent.put(depth, key);
                // Add as child to appropriate parent
                if (depth > indent) {
                    // Has a parent. Get parent and add this node to child list
                    hubMap.computeIfAbsent(lastParent.get(depth - indent), x -> new ArrayList<>()).add(key);
                } else {
                    // No parent, add to controllers list
                    usbControllers.add(key);
                }
            } else if (!key.isEmpty()) {
                // We are currently processing for node identified by key. Save
                // approrpriate variables to maps.
                line = line.trim();
                if (line.startsWith("model:")) {
                    nameMap.put(key, Builder.getSingleQuoteStringValue(line));
                } else if (line.startsWith("name:")) {
                    // Name is backup for model if model doesn't exist, so only
                    // put if key doesn't yet exist
                    nameMap.putIfAbsent(key, Builder.getSingleQuoteStringValue(line));
                } else if (line.startsWith("vendor-id:")) {
                    // Format: vendor-id: 00008086
                    if (line.length() > 4) {
                        vendorIdMap.put(key, line.substring(line.length() - 4));
                    }
                } else if (line.startsWith("device-id:")) {
                    // Format: device-id: 00002440
                    if (line.length() > 4) {
                        productIdMap.put(key, line.substring(line.length() - 4));
                    }
                } else if (line.startsWith("device_type:")) {
                    // Name is backup for model if model doesn't exist, so only
                    // put if key doesn't yet exist
                    deviceTypeMap.putIfAbsent(key, Builder.getSingleQuoteStringValue(line));
                }
            }
        }

        // Build tree and return
        List<UsbDevice> controllerDevices = new ArrayList<>();
        for (String controller : usbControllers) {
            // Only do controllers that are USB device type
            if ("usb".equals(deviceTypeMap.getOrDefault(controller, Normal.EMPTY))) {
                controllerDevices.add(
                        getDeviceAndChildren(controller, "0000", "0000", nameMap, vendorIdMap, productIdMap, hubMap));
            }
        }
        return controllerDevices.toArray(new UsbDevice[0]);
    }

    private static void addDevicesToList(List<UsbDevice> deviceList, UsbDevice[] connectedDevices) {
        for (UsbDevice device : connectedDevices) {
            deviceList.add(device);
            addDevicesToList(deviceList, device.getConnectedDevices());
        }
    }

    /**
     * 通过从映射获取信息来填充字段，递归地创建SolarisUsbDevices
     *
     * @param devPath      设备节点路径
     * @param vid          默认的(父)提供者ID
     * @param pid          默认的(父)产品ID
     * @param nameMap      名称信息
     * @param vendorIdMap  提供者信息
     * @param productIdMap 产品信息
     * @param hubMap       集线器
     * @return 与此装置相对应的一种solarisusb装置
     */
    private static SolarisUsbDevice getDeviceAndChildren(String devPath, String vid, String pid,
                                                         Map<String, String> nameMap, Map<String, String> vendorIdMap, Map<String, String> productIdMap,
                                                         Map<String, List<String>> hubMap) {
        String vendorId = vendorIdMap.getOrDefault(devPath, vid);
        String productId = productIdMap.getOrDefault(devPath, pid);
        List<String> childPaths = hubMap.getOrDefault(devPath, new ArrayList<>());
        List<SolarisUsbDevice> usbDevices = new ArrayList<>();
        for (String path : childPaths) {
            usbDevices.add(getDeviceAndChildren(path, vendorId, productId, nameMap, vendorIdMap, productIdMap, hubMap));
        }
        Collections.sort(usbDevices);
        return new SolarisUsbDevice(nameMap.getOrDefault(devPath, vendorId + Symbol.COLON + productId), Normal.EMPTY, vendorId, productId,
                Normal.EMPTY, devPath, usbDevices.toArray(new UsbDevice[0]));
    }
}
