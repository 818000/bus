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
package org.aoju.bus.mapper.additional.aggregation;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.mapper.builder.EntityBuilder;
import org.aoju.bus.mapper.builder.MapperBuilder;
import org.aoju.bus.mapper.builder.MapperTemplate;
import org.aoju.bus.mapper.builder.SqlBuilder;
import org.aoju.bus.mapper.criteria.Assert;
import org.aoju.bus.mapper.criteria.Words;
import org.aoju.bus.mapper.entity.EntityColumn;
import org.aoju.bus.mapper.entity.EntityTable;
import org.apache.ibatis.mapping.MappedStatement;

import java.text.MessageFormat;
import java.util.Map;

/**
 * 聚合实现类
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public class AggregationProvider extends MapperTemplate {

    public AggregationProvider(Class<?> mapperClass, MapperBuilder mapperBuilder) {
        super(mapperClass, mapperBuilder);
    }

    public static String aggregationSelectClause(Class<?> entityClass, String wrapKeyword, AggregateCondition condition) {
        Assert.notEmpty(condition.getAggregateProperty(), "aggregateProperty must have length; it must not be null or empty");
        Assert.notNull(condition.getAggregateType(), "aggregateType is required; it must not be null");
        EntityTable entityTable = EntityBuilder.getEntityTable(entityClass);
        Map<String, EntityColumn> propertyMap = entityTable.getPropertyMap();
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(condition.getAggregateType().name());
        String columnName = propertyMap.get(condition.getAggregateProperty()).getColumn();
        selectBuilder.append(Symbol.PARENTHESE_LEFT).append(columnName).append(Symbol.PARENTHESE_RIGHT);
        selectBuilder.append(" AS ");
        if (StringKit.isNotEmpty(condition.getAggregateAliasName())) {
            selectBuilder.append(condition.getAggregateAliasName());
        } else {
            selectBuilder.append(wrapKeyword(wrapKeyword, columnName));
        }
        if (condition.getGroupByProperties() != null && condition.getGroupByProperties().size() > 0) {
            for (String property : condition.getGroupByProperties()) {
                selectBuilder.append(", ");
                columnName = propertyMap.get(property).getColumn();
                selectBuilder.append(columnName).append(" AS ").append(wrapKeyword(wrapKeyword, columnName));
            }
        }
        return selectBuilder.toString();
    }

    private static String wrapKeyword(String wrapKeyword, String columnName) {
        if (StringKit.isNotEmpty(wrapKeyword) && Words.containsWord(columnName)) {
            return MessageFormat.format(wrapKeyword, columnName);
        }
        return columnName;
    }

    public static String aggregationGroupBy(Class<?> entityClass, String wrapKeyword, AggregateCondition condition) {
        if (condition.getGroupByProperties() != null && condition.getGroupByProperties().size() > 0) {
            EntityTable entityTable = EntityBuilder.getEntityTable(entityClass);
            Map<String, EntityColumn> propertyMap = entityTable.getPropertyMap();
            StringBuilder groupByBuilder = new StringBuilder();
            for (String property : condition.getGroupByProperties()) {
                if (groupByBuilder.length() == 0) {
                    groupByBuilder.append(" GROUP BY ");
                } else {
                    groupByBuilder.append(", ");
                }
                groupByBuilder.append(propertyMap.get(property).getColumn());
            }
            return groupByBuilder.toString();
        }
        return "";
    }

    /**
     * 根据Condition查询总数
     *
     * @param ms MappedStatement
     * @return the string
     */
    public String selectAggregationByCondition(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if (isCheckConditionEntityClass()) {
            sql.append(SqlBuilder.conditionCheck(entityClass));
        }
        sql.append("SELECT ${@org.aoju.bus.mapper.additional.aggregation.AggregationProvider@aggregationSelectClause(");
        sql.append("@").append(entityClass.getCanonicalName()).append("@class");
        sql.append(", '").append(getConfig().getWrapKeyword()).append("'");
        sql.append(", aggregateCondition");
        sql.append(")} ");
        sql.append(SqlBuilder.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlBuilder.updateByConditionWhereClause());
        sql.append(" ${@org.aoju.bus.mapper.additional.aggregation.AggregationProvider@aggregationGroupBy(");
        sql.append("@").append(entityClass.getCanonicalName()).append("@class");
        sql.append(", '").append(getConfig().getWrapKeyword()).append("'");
        sql.append(", aggregateCondition");
        sql.append(")} ");
        sql.append(SqlBuilder.conditionOrderBy("condition", entityClass));
        sql.append(SqlBuilder.conditionForUpdate());
        return sql.toString();
    }

}
