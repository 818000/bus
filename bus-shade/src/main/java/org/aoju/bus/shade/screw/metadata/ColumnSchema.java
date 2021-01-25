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
package org.aoju.bus.shade.screw.metadata;

import lombok.Data;

/**
 * 表列领域对象
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
@Data
public class ColumnSchema {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5417752216907437665L;
    /**
     * 表中的列的索引（从 1 开始）
     */
    private String ordinalPosition;
    /**
     * 名称
     */
    private String columnName;
    /**
     * SQL 数据类型带长度
     */
    private String columnType;
    /**
     * SQL 数据类型 名称
     */
    private String typeName;
    /**
     * 列长度
     */
    private String columnLength;
    /**
     * 列大小
     */
    private String columnSize;
    /**
     * 小数位
     */
    private String decimalDigits;
    /**
     * 可为空
     */
    private String nullable;
    /**
     * 是否主键
     */
    private String primaryKey;
    /**
     * 默认值
     */
    private String columnDef;
    /**
     * 说明
     */
    private String remarks;
}
