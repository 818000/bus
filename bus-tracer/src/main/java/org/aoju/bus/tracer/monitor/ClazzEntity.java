/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.tracer.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kimi Liu
 * @version 6.2.8
 * @since JDK 1.8+
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClazzEntity {

    /**
     * 当前应用进程ID
     */
    private Integer id;
    /**
     * 当前应用名称
     */
    private String name;
    /**
     * 时间 格式 MM/dd HH:mm"
     */
    private String date;
    /**
     * 表示载入了类的数量
     */
    private String loaded;
    /**
     * 表示载入了类的合计
     */
    private String bytes1;
    /**
     * 表示卸载类合计大小
     */
    private String bytes2;
    /**
     * 表示卸载类的数量
     */
    private String unloaded;
    /**
     * 表示加载和卸载类总共的耗时
     */
    private String time1;
    /**
     * 表示编译任务执行的次数
     */
    private String compiled;
    /**
     * 表示编译失败的次数
     */
    private String failed;
    /**
     * 表示编译不可用的次数
     */
    private String invalid;
    /**
     * 表示编译的总耗时
     */
    private String time2;

}
