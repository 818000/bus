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
package org.aoju.bus.mapper.provider;

import org.aoju.bus.mapper.builder.MapperBuilder;
import org.aoju.bus.mapper.builder.MapperTemplate;
import org.aoju.bus.mapper.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * BaseSelectProvider实现类,基础方法实现类
 *
 * @author Kimi Liu
 * @version 5.5.3
 * @since JDK 1.8+
 */
public class BaseSelectProvider extends MapperTemplate {

    public BaseSelectProvider(Class<?> mapperClass, MapperBuilder mapperBuilder) {
        super(mapperClass, mapperBuilder);
    }

    /**
     * 查询
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectOne(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectAllColumns(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.whereAllIfColumns(entityClass, isNotEmpty()));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String select(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectAllColumns(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.whereAllIfColumns(entityClass, isNotEmpty()));
        sql.append(SqlSourceBuilder.orderByDefault(entityClass));
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectByRowBounds(MappedStatement ms) {
        return select(ms);
    }

    /**
     * 根据主键进行查询
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectByPrimaryKey(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //将返回值修改为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectAllColumns(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.wherePKColumns(entityClass));
        return sql.toString();
    }

    /**
     * 查询总数
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectCount(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectCount(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.whereAllIfColumns(entityClass, isNotEmpty()));
        return sql.toString();
    }

    /**
     * 根据主键查询总数
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String existsWithPrimaryKey(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectCountExists(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.wherePKColumns(entityClass));
        return sql.toString();
    }

    /**
     * 查询全部结果
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectAll(MappedStatement ms) {
        final Class<?> entityClass = getEntityClass(ms);
        //修改返回值类型为实体类型
        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlSourceBuilder.selectAllColumns(entityClass));
        sql.append(SqlSourceBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlSourceBuilder.orderByDefault(entityClass));
        return sql.toString();
    }

}
