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

import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.mapper.criteria.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聚合查询条件
 *
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
public class AggregateCondition implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 聚合属性
     */
    private String aggregateProperty;
    private String aggregateAliasName;
    /**
     * groupBy 查询列
     */
    private List<String> groupByProperties;
    /**
     * 聚合函数
     */
    private AggregateType aggregateType;

    public AggregateCondition() {
        this(null, AggregateType.COUNT, null);
    }

    /**
     * 默认查询count计数，不分组
     *
     * @param aggregateProperty 聚合查询属性，不能为空；为保证返回结果key与传入值相同 方法不会去除前后空格
     */
    public AggregateCondition(String aggregateProperty) {
        this(aggregateProperty, AggregateType.COUNT, null);
    }

    /**
     * 默认查询count计数
     *
     * @param aggregateProperty 聚合查询属性，不能为空；为保证返回结果key与传入值相同 方法不会去除前后空格
     * @param groupByProperties 为保证返回结果key与传入值相同 方法不会去除每一项前后空格
     */
    public AggregateCondition(String aggregateProperty, String[] groupByProperties) {
        this(aggregateProperty, AggregateType.COUNT, groupByProperties);
    }

    /**
     * 按指定聚合方法查询，不分组
     *
     * @param aggregateProperty 不能为空，为保证返回结果key与传入值相同 方法不会去除前后空格
     * @param aggregateType     聚合函数
     */
    public AggregateCondition(String aggregateProperty, AggregateType aggregateType) {
        this(aggregateProperty, aggregateType, null);
    }

    /**
     * @param aggregateProperty 不能为空，为保证返回结果key与传入值相同 方法不会去除前后空格
     * @param aggregateType     聚合函数
     * @param groupByProperties 为保证返回结果key与传入值相同 方法不会去除每一项前后空格
     */
    public AggregateCondition(String aggregateProperty, AggregateType aggregateType,
                              String[] groupByProperties) {
        this.groupByProperties = new ArrayList<>();
        // 需要放在propertyMap初始化完成后执行
        aggregateType(aggregateType);
        if (StringKit.isNotEmpty(aggregateProperty)) {
            aggregateBy(aggregateProperty);
        }
        groupBy(groupByProperties);
    }

    public static AggregateCondition builder() {
        return new AggregateCondition();
    }

    public AggregateCondition groupBy(String... groupByProperties) {
        if (groupByProperties != null && groupByProperties.length > 0) {
            this.groupByProperties.addAll(Arrays.asList(groupByProperties));
        }
        return this;
    }

    public AggregateCondition aggregateBy(String aggregateProperty) {
        Assert.notEmpty(aggregateProperty,
                "aggregateProperty must have length; it must not be null or empty");
        this.aggregateProperty = aggregateProperty;
        return this;
    }

    public AggregateCondition aliasName(String aggregateAliasName) {
        this.aggregateAliasName = aggregateAliasName;
        return this;
    }

    public AggregateCondition aggregateType(AggregateType aggregateType) {
        Assert.notNull(aggregateType,
                "aggregateType is required; it must not be null");
        this.aggregateType = aggregateType;
        return this;
    }

    public String getAggregateProperty() {
        return aggregateProperty;
    }

    public String getAggregateAliasName() {
        return aggregateAliasName;
    }

    public List<String> getGroupByProperties() {
        return groupByProperties;
    }

    public AggregateType getAggregateType() {
        return aggregateType;
    }

}
