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
package org.aoju.bus.mapper.criteria;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.mapper.annotation.LogicDelete;
import org.aoju.bus.mapper.builder.EntityBuilder;
import org.aoju.bus.mapper.builder.SqlBuilder;
import org.aoju.bus.mapper.entity.Condition;
import org.aoju.bus.mapper.entity.DynamicTableName;
import org.aoju.bus.mapper.entity.EntityColumn;

import java.lang.reflect.Method;
import java.util.*;

/**
 * OGNL静态方法
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public abstract class OGNL {

    public static final String SAFE_DELETE_ERROR = "通用 Mapper 安全检查: 对查询条件参数进行检查时出错!";
    public static final String SAFE_DELETE_EXCEPTION = "通用 Mapper 安全检查: 当前操作的方法没有指定查询条件，不允许执行该操作!";

    /**
     * 校验通用 Condition 的 entityClass 和当前方法是否匹配
     *
     * @param parameter      参数
     * @param entityFullName 对象全称
     * @return true支持，false不支持
     */
    public static boolean checkConditionEntityClass(Object parameter, String entityFullName) {
        if (parameter != null && parameter instanceof Condition && StringKit.isNotEmpty(entityFullName)) {
            Condition condition = (Condition) parameter;
            Class<?> entityClass = condition.getEntityClass();
            if (!entityClass.getCanonicalName().equals(entityFullName)) {
                throw new InstrumentException("当前 Condition 方法对应实体为:" + entityFullName
                        + ", 但是参数 Condition 中的 entityClass 为:" + entityClass.getCanonicalName());
            }
        }
        return true;
    }

    /**
     * 检查 paremeter 对象中指定的 fields 是否全是 null，如果是则抛出异常
     *
     * @param parameter 参数
     * @param fields    字段信息
     * @return true支持，false不支持
     */
    public static boolean notAllNullParameterCheck(Object parameter, String fields) {
        if (parameter != null) {
            try {
                Set<EntityColumn> columns = EntityBuilder.getColumns(parameter.getClass());
                Set<String> fieldSet = new HashSet<>(Arrays.asList(fields.split(Symbol.COMMA)));
                for (EntityColumn column : columns) {
                    if (fieldSet.contains(column.getProperty())) {
                        Object value = column.getEntityField().getValue(parameter);
                        if (value != null) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                throw new InstrumentException(SAFE_DELETE_ERROR, e);
            }
        }
        throw new InstrumentException(SAFE_DELETE_EXCEPTION);
    }

    /**
     * 校验集合类型参数不能为空
     *
     * @param parameter 参数
     * @param error     错误
     * @return true支持，false不支持
     */
    public static boolean notEmptyCollectionCheck(Object parameter, String error) {
        if (parameter == null || (parameter instanceof Collection && ((Collection) parameter).size() == 0)) {
            throw new IllegalArgumentException(error);
        }
        return true;
    }

    /**
     * 检查 paremeter 对象中指定的 fields 是否全是 null，如果是则抛出异常
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean conditionHasAtLeastOneCriteriaCheck(Object parameter) {
        if (parameter != null) {
            try {
                if (parameter instanceof Condition) {
                    List<Condition.Criteria> criteriaList = ((Condition) parameter).getOredCriteria();
                    if (criteriaList != null && criteriaList.size() > 0) {
                        return true;
                    }
                } else {
                    Method getter = parameter.getClass().getDeclaredMethod("getOredCriteria");
                    Object list = getter.invoke(parameter);
                    if (list != null && list instanceof List && ((List) list).size() > 0) {
                        return true;
                    }
                }
            } catch (Exception e) {
                throw new InstrumentException(SAFE_DELETE_ERROR, e);
            }
        }
        throw new InstrumentException(SAFE_DELETE_EXCEPTION);
    }

    /**
     * 是否包含自定义查询列
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean hasSelectColumns(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            if (condition.getSelectColumns() != null && condition.getSelectColumns().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含自定义 Count 列
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean hasCountColumn(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return StringKit.isNotEmpty(condition.getCountColumn());
        }
        return false;
    }

    /**
     * 是否包含 forUpdate
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean hasForUpdate(Object parameter) {
        if (parameter != null && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return condition.isForUpdate();
        }
        return false;
    }

    /**
     * 不包含自定义查询列
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean hasNoSelectColumns(Object parameter) {
        return !hasSelectColumns(parameter);
    }

    /**
     * 判断参数是否支持动态表名
     *
     * @param parameter 参数
     * @return true支持，false不支持
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (parameter != null && parameter instanceof DynamicTableName) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否b支持动态表名
     *
     * @param parameter 参数
     * @return true不支持，false支持
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

    /**
     * 判断条件是 and 还是 or
     *
     * @param parameter 参数
     * @return the string
     */
    public static String andOr(Object parameter) {
        if (parameter instanceof Condition.Criteria) {
            return ((Condition.Criteria) parameter).getAndOr();
        } else if (parameter instanceof Condition.Criterion) {
            return ((Condition.Criterion) parameter).getAndOr();
        } else if (parameter.getClass().getCanonicalName().endsWith("Criteria")) {
            return "or";
        } else {
            return "and";
        }
    }

    /**
     * 拼接逻辑删除字段的未删除查询条件
     *
     * @param parameter 参数
     * @return the string
     */
    public static String andNotLogicDelete(Object parameter) {
        String result = "";
        if (parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            Map<String, EntityColumn> propertyMap = condition.getPropertyMap();

            for (Map.Entry<String, EntityColumn> entry : propertyMap.entrySet()) {
                EntityColumn column = entry.getValue();
                if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                    // 未逻辑删除的条件
                    result = "and " + column.getColumn() + " = " + SqlBuilder.getLogicDeletedValue(column, false);
                }
            }
        }
        return result;
    }

}
