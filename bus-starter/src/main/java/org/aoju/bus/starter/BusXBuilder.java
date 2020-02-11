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
package org.aoju.bus.starter;

/**
 * 全局常量配置
 *
 * @author Kimi Liu
 * @version 5.5.8
 * @since JDK 1.8+
 */
public class BusXBuilder {

    /***
     * 应用图标
     */
    public static final String[] BUS_BANNER = {
            "",
            " $$$$$$\\   $$$$$$\\  $$\\ $$\\   $$\\     $$$$$$\\   $$$$$$\\   $$$$$$\\",
            " \\____$$\\ $$  __$$\\ \\__|$$ |  $$ |   $$  __$$\\ $$  __$$\\ $$  __$$\\",
            " $$$$$$$ |$$ /  $$ |$$\\ $$ |  $$ |   $$ /  $$ |$$ |  \\__|$$ /  $$ |",
            "$$  __$$ |$$ |  $$ |$$ |$$ |  $$ |   $$ |  $$ |$$ |      $$ |  $$ |",
            "\\$$$$$$$ |\\$$$$$$  |$$ |\\$$$$$$  |$$\\\\$$$$$$  |$$ |      \\$$$$$$$ |",
            " \\_______| \\______/ $$ | \\______/ \\__|\\______/ \\__|       \\____$$ |",
            "              $$\\   $$ |                                 $$\\   $$ |",
            "              \\$$$$$$  |                                 \\$$$$$$  |",
            "               \\______/                                   \\______/"
    };
    /***
     * 应用名称
     */
    public static final String BUS_NAME = "spring.application.name";
    /***
     * 应用版本
     */
    public static final String BUS_VERSION = "version";
    /***
     * BOOT
     */
    public static final String BUS_BOOT = "::Bus Boot::";
    /***
     * BOOT 版本
     */
    public static final String BUS_BOOT_VERSION = "bus-boot.version";
    /***
     * BOOT 版本信息
     */
    public static final String BUS_BOOT_FORMATTED_VERSION = "bus-boot.formatted-version";
    /***
     * BOOT 环境属性
     */
    public static final String BUS_BOOT_PROPERTIES = "GenieBuilder";

    public static final String BUS_HIGH_PRIORITY_CONFIG = "BusHighPriorityConfig";

    public static final String BUS_BOOTSTRAP = "BusBootstrap";


}
