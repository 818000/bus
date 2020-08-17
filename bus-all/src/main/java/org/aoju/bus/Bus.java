/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
package org.aoju.bus;

import org.aoju.bus.core.Version;

/**
 * <p>
 * Bus (应用/服务总线) 是一个微服务套件、基础框架，它基于Java8编写，参考、借鉴了大量已有
 * 框架、组件的设计，可以作为后端服务的开发基础中间件。代码简洁，架构清晰，非常适合学习使用
 * </p>
 *
 * <p>
 * 目标期望能努力打造一套从 基础框架 - 分布式微服务架构 - 持续集成 - 自动化部署 -系统监测
 * 等，快速实现业务需求的全栈式技术解决方案
 * </p>
 *
 * <p>
 * 欢迎各种形式的贡献，包括但不限于优化，添加功能，文档 代码的改进，问题和 bugs 的报告
 * </p>
 *
 * @author Kimi Liu
 * @version 6.0.6
 * @since JDK 1.8+
 */
public class Bus {

    /**
     * 版本号信息
     *
     * @return 版本号
     */
    public static String get() {
        return Version.get();
    }

    /**
     * 主要版本号
     *
     * @return 版本号
     */
    public static String major() {
        return Version.major();
    }

    /**
     * 次要版本号
     *
     * @return 次要号
     */
    public static String minor() {
        return Version.minor();
    }

    /**
     * 阶段版本号
     *
     * @return 阶段号
     */
    public static String stage() {
        return Version.stage();
    }

    /**
     * 版本质量
     *
     * @return 质量
     */
    public static String level() {
        return Version.level();
    }

    /**
     * 完整版本号
     *
     * @return the agent
     */
    public static String all() {
        return Version.all();
    }

}
