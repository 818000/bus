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
package org.aoju.bus.mapper.builder;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.mapper.criteria.Assert;
import org.aoju.bus.mapper.criteria.Criteria;
import org.aoju.bus.mapper.criteria.Criterion;
import org.aoju.bus.mapper.entity.Condition;
import org.aoju.bus.mapper.entity.EntityTableName;

/**
 * OGNL静态方法
 *
 * @author Kimi Liu
 * @version 6.2.3
 * @since JDK 1.8+
 */
public abstract class OGNL {

    /**
     * 校验通用 Condition 的 entityClass 和当前方法是否匹配
     *
     * @param parameter      object
     * @param entityFullName String
     * @return the boolean
     */
    public static boolean checkEntityClass(Object parameter, String entityFullName) {
        if (null != parameter
                && parameter instanceof Condition && Assert.isNotEmpty(entityFullName)) {
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
     * 是否包含自定义查询列
     *
     * @param parameter Object
     * @return the boolean
     */
    public static boolean hasSelectColumns(Object parameter) {
        if (null != parameter && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            if (null != condition.getSelectColumns() && condition.getSelectColumns().size() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否包含自定义 Count 列
     *
     * @param parameter Object
     * @return the boolean
     */
    public static boolean hasCountColumn(Object parameter) {
        if (null != parameter && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return Assert.isNotEmpty(condition.getCountColumn());
        }
        return false;
    }

    /**
     * 是否包含 forUpdate
     *
     * @param parameter Object
     * @return the boolean
     */
    public static boolean hasForUpdate(Object parameter) {
        if (null != parameter && parameter instanceof Condition) {
            Condition condition = (Condition) parameter;
            return condition.isForUpdate();
        }
        return false;
    }

    /**
     * 不包含自定义查询列
     *
     * @param parameter Object
     * @return the boolean
     */
    public static boolean hasNoSelectColumns(Object parameter) {
        return !hasSelectColumns(parameter);
    }

    /**
     * 判断参数是否支持动态表名
     *
     * @param parameter Object
     * @return true支持, false不支持
     */
    public static boolean isDynamicParameter(Object parameter) {
        if (null != parameter && parameter instanceof EntityTableName) {
            return true;
        }
        return false;
    }

    /**
     * 判断参数是否b支持动态表名
     *
     * @param parameter Object
     * @return true不支持, false支持
     */
    public static boolean isNotDynamicParameter(Object parameter) {
        return !isDynamicParameter(parameter);
    }

    /**
     * 判断条件是 and 还是 or
     *
     * @param parameter Object
     * @return the boolean
     */
    public static String andOr(Object parameter) {
        if (parameter instanceof Criteria) {
            return ((Criteria) parameter).getAndOr();
        } else if (parameter instanceof Criterion) {
            return ((Criterion) parameter).getAndOr();
        } else if (parameter.getClass().getCanonicalName().endsWith("Criteria")) {
            return "or";
        } else {
            return "and";
        }
    }

}
