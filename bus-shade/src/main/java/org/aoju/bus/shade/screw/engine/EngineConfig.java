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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.shade.screw.engine;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 文件生成配置
 *
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
@Data
@Builder
public class EngineConfig implements Serializable {
    /**
     * 是否打开输出目录
     */
    private boolean openOutputDir;
    /**
     * 文件产生位置
     */
    private String fileOutputDir;
    /**
     * 生成文件类型
     */
    private EngineFileType fileType;
    /**
     * 生成实现
     */
    private TemplateType produceType;
    /**
     * 自定义模板，模板需要和文件类型和使用模板的语法进行编写和处理，否则将会生成错误
     */
    private String customTemplate;
    /**
     * 文件名称
     */
    private String fileName;
}
