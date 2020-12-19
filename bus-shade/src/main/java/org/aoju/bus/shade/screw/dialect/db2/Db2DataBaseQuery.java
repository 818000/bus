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
package org.aoju.bus.shade.screw.dialect.db2;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.shade.screw.dialect.AbstractDatabaseQuery;
import org.aoju.bus.shade.screw.metadata.Column;
import org.aoju.bus.shade.screw.metadata.Database;
import org.aoju.bus.shade.screw.metadata.PrimaryKey;
import org.aoju.bus.shade.screw.metadata.Table;

import javax.sql.DataSource;
import java.util.List;

import static org.aoju.bus.shade.Builder.NOT_SUPPORTED;

/**
 * db2 数据库查询
 *
 * @author Kimi Liu
 * @version 6.1.6
 * @since JDK 1.8+
 */
public class Db2DataBaseQuery extends AbstractDatabaseQuery {
    /**
     * 构造函数
     *
     * @param dataSource {@link DataSource}
     */
    public Db2DataBaseQuery(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 获取数据库
     *
     * @return {@link Database} 数据库信息
     */
    @Override
    public Database getDataBase() throws InstrumentException {
        throw new InstrumentException(NOT_SUPPORTED);
    }

    /**
     * 获取表信息
     *
     * @return {@link List} 所有表信息
     */
    @Override
    public List<Table> getTables() {
        throw new InstrumentException(NOT_SUPPORTED);
    }

    /**
     * 获取列信息
     *
     * @param table {@link String} 表名
     * @return {@link List} 表字段信息
     * @throws InstrumentException 异常
     */
    @Override
    public List<Column> getTableColumns(String table) throws InstrumentException {
        Assert.notEmpty(table, "Table name can not be empty!");
        throw new InstrumentException(NOT_SUPPORTED);
    }

    /**
     * 获取所有列信息
     *
     * @return {@link List} 表字段信息
     * @throws InstrumentException 异常
     */
    @Override
    public List<? extends Column> getTableColumns() throws InstrumentException {
        throw new InstrumentException(NOT_SUPPORTED);
    }

    /**
     * 根据表名获取主键
     *
     * @param table {@link String}
     * @return {@link List}
     * @throws InstrumentException 异常
     */
    @Override
    public List<? extends PrimaryKey> getPrimaryKeys(String table) throws InstrumentException {
        throw new InstrumentException(NOT_SUPPORTED);
    }

}
