/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org mybatis.io and other contributors.           *
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
package org.aoju.bus.mapper.builder;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.mapper.builder.resolve.DefaultEntityResolve;
import org.aoju.bus.mapper.builder.resolve.EntityResolve;
import org.aoju.bus.mapper.entity.Config;
import org.aoju.bus.mapper.entity.EntityColumn;
import org.aoju.bus.mapper.entity.EntityTable;
import org.aoju.bus.mapper.reflect.MetaObject;
import org.apache.ibatis.mapping.MappedStatement;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类
 *
 * @author Kimi Liu
 * @version 6.3.0
 * @since JDK 1.8+
 */
public class EntityBuilder {

    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, EntityTable> entityTableMap = new ConcurrentHashMap<>();

    private static final EntityResolve DEFAULT = new DefaultEntityResolve();

    /**
     * 实体类解析器
     */
    private static EntityResolve resolve = DEFAULT;

    /**
     * 获取表对象
     *
     * @param entityClass 实体Class对象
     * @return the object
     */
    public static EntityTable getEntityTable(Class<?> entityClass) {
        EntityTable entityTable = entityTableMap.get(entityClass);
        if (entityTable == null) {
            throw new InstrumentException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
        }
        return entityTable;
    }

    /**
     * 获取默认的orderby语句
     *
     * @param entityClass 实体Class对象
     * @return the string
     */
    public static String getOrderByClause(Class<?> entityClass) {
        EntityTable table = getEntityTable(entityClass);
        if (table.getOrderByClause() != null) {
            return table.getOrderByClause();
        }

        List<EntityColumn> orderEntityColumns = new ArrayList<>();
        for (EntityColumn column : table.getEntityClassColumns()) {
            if (column.getOrderBy() != null) {
                orderEntityColumns.add(column);
            }
        }

        Collections.sort(orderEntityColumns, Comparator.comparingInt(EntityColumn::getOrderPriority));

        StringBuilder orderBy = new StringBuilder();
        for (EntityColumn column : orderEntityColumns) {
            if (orderBy.length() != 0) {
                orderBy.append(Symbol.COMMA);
            }
            orderBy.append(column.getColumn()).append(" ").append(column.getOrderBy());
        }
        table.setOrderByClause(orderBy.toString());
        return table.getOrderByClause();
    }

    /**
     * 获取全部列
     *
     * @param entityClass 实体Class对象
     * @return the object
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassColumns();
    }

    /**
     * 获取主键信息
     *
     * @param entityClass 实体Class对象
     * @return the object
     */
    public static Set<EntityColumn> getPKColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassPKColumns();
    }

    /**
     * 获取查询的Select
     *
     * @param entityClass 实体Class对象
     * @return the string
     */
    public static String getSelectColumns(Class<?> entityClass) {
        EntityTable entityTable = getEntityTable(entityClass);
        if (entityTable.getBaseSelect() != null) {
            return entityTable.getBaseSelect();
        }
        Set<EntityColumn> columnList = getColumns(entityClass);
        StringBuilder selectBuilder = new StringBuilder();
        boolean skipAlias = Map.class.isAssignableFrom(entityClass);
        for (EntityColumn entityColumn : columnList) {
            selectBuilder.append(entityColumn.getColumn());
            if (!skipAlias && !entityColumn.getColumn().equalsIgnoreCase(entityColumn.getProperty())) {
                //不等的时候分几种情况，例如`DESC`
                if (entityColumn.getColumn().substring(1, entityColumn.getColumn().length() - 1).equalsIgnoreCase(entityColumn.getProperty())) {
                    selectBuilder.append(Symbol.COMMA);
                } else {
                    selectBuilder.append(" AS ").append(entityColumn.getProperty()).append(Symbol.COMMA);
                }
            } else {
                selectBuilder.append(Symbol.COMMA);
            }
        }
        entityTable.setBaseSelect(selectBuilder.substring(0, selectBuilder.length() - 1));
        return entityTable.getBaseSelect();
    }

    /**
     * 初始化实体属性
     *
     * @param entityClass 实体Class对象
     * @param config      配置
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass, Config config) {
        if (entityTableMap.get(entityClass) != null) {
            return;
        }
        // 创建并缓存EntityTable
        EntityTable entityTable = resolve.resolveEntity(entityClass, config);
        entityTableMap.put(entityClass, entityTable);
    }

    /**
     * 设置实体类解析器
     *
     * @param resolve 解析器
     */
    static void setResolve(EntityResolve resolve) {
        EntityBuilder.resolve = resolve;
    }

    /**
     * 通过反射设置MappedStatement的keyProperties字段值
     *
     * @param pkColumns 所有的主键字段
     * @param ms        MappedStatement
     */
    public static void setKeyProperties(Set<EntityColumn> pkColumns, MappedStatement ms) {
        if (pkColumns == null || pkColumns.isEmpty()) {
            return;
        }

        List<String> keyProperties = new ArrayList<String>(pkColumns.size());
        for (EntityColumn column : pkColumns) {
            keyProperties.add(column.getProperty());
        }

        MetaObject.forObject(ms).setValue("keyProperties", keyProperties.toArray(new String[]{}));
    }

}
