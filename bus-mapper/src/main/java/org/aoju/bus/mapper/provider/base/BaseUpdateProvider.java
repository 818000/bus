/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org mybatis.io and other contributors.           *
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
package org.aoju.bus.mapper.provider.base;

import org.aoju.bus.mapper.builder.MapperBuilder;
import org.aoju.bus.mapper.builder.MapperTemplate;
import org.aoju.bus.mapper.builder.SqlBuilder;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseUpdateProvider实现类，基础方法实现类
 *
 * @author Kimi Liu
 * @version 6.3.5
 * @since JDK 1.8+
 */
public class BaseUpdateProvider extends MapperTemplate {

    public BaseUpdateProvider(Class<?> mapperClass, MapperBuilder mapperBuilder) {
        super(mapperClass, mapperBuilder);
    }

    /**
     * 通过主键更新全部字段
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String updateByPrimaryKey(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlBuilder.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlBuilder.updateSetColumns(entityClass, null, false, false));
        sql.append(SqlBuilder.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String updateByPrimaryKeySelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlBuilder.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlBuilder.updateSetColumns(entityClass, null, true, isNotEmpty()));
        sql.append(SqlBuilder.wherePKColumns(entityClass, true));
        return sql.toString();
    }

}
