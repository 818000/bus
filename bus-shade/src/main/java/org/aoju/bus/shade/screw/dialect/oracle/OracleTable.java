/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.shade.screw.dialect.oracle;

import lombok.Data;
import org.aoju.bus.shade.screw.mapping.MappingField;
import org.aoju.bus.shade.screw.metadata.Table;

/**
 * Oracle table
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@Data
public class OracleTable implements Table {
    /**
     * TABLE_CAT
     */
    @MappingField(value = "TABLE_CAT")
    private String tableCat;
    /**
     * 表名
     */
    @MappingField(value = "TABLE_NAME")
    private String tableName;
    /**
     * 表模式
     */
    @MappingField(value = "TABLE_SCHEM")
    private String tableSchem;
    /**
     * 表类型
     */
    @MappingField(value = "TABLE_TYPE")
    private String tableType;
    /**
     * 备注
     */
    @MappingField(value = "REMARKS")
    private String remarks;
}
