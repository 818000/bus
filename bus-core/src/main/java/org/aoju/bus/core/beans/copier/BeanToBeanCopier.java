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
package org.aoju.bus.core.beans.copier;

import org.aoju.bus.core.beans.PropertyDesc;
import org.aoju.bus.core.convert.Convert;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.toolkit.BeanKit;
import org.aoju.bus.core.toolkit.TypeKit;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Bean属性拷贝到Bean中的拷贝器
 *
 * @param <S> 源Bean类型
 * @param <T> 目标Bean类型
 * @author Kimi Liu
 * @version 6.3.5
 * @since Java 17+
 */
public class BeanToBeanCopier<S, T> extends AbstractCopier<S, T> {

    /**
     * 目标的类型（用于泛型类注入）
     */
    private final Type targetType;

    /**
     * 构造
     *
     * @param source      来源Map
     * @param target      目标Bean对象
     * @param targetType  目标泛型类型
     * @param copyOptions 拷贝选项
     */
    public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
        super(source, target, copyOptions);
        this.targetType = targetType;
    }

    @Override
    public T copy() {
        Class<?> actualEditable = target.getClass();
        if (null != copyOptions.editable) {
            // 检查限制类是否为target的父类或接口
            Assert.isTrue(copyOptions.editable.isInstance(target),
                    "Target class [{}] not assignable to Editable class [{}]", actualEditable.getName(), copyOptions.editable.getName());
            actualEditable = copyOptions.editable;
        }
        final Map<String, PropertyDesc> targetPropertyDescMap = BeanKit.getBeanDesc(actualEditable).getPropMap(copyOptions.ignoreCase);

        final Map<String, PropertyDesc> sourcePropertyDescMap = BeanKit.getBeanDesc(source.getClass()).getPropMap(copyOptions.ignoreCase);
        sourcePropertyDescMap.forEach((sFieldName, sDesc) -> {
            if (null == sFieldName || false == sDesc.isReadable(copyOptions.transientSupport)) {
                // 字段空或不可读，跳过
                return;
            }

            sFieldName = copyOptions.editFieldName(sFieldName);
            // 对key做转换，转换后为null的跳过
            if (null == sFieldName) {
                return;
            }

            // 检查目标字段可写性
            final PropertyDesc tDesc = targetPropertyDescMap.get(sFieldName);
            if (null == tDesc || false == tDesc.isWritable(this.copyOptions.transientSupport)) {
                // 字段不可写，跳过之
                return;
            }

            // 检查源对象属性是否过滤属性
            Object sValue = sDesc.getValue(this.source);
            if (false == copyOptions.testPropertyFilter(sDesc.getField(), sValue)) {
                return;
            }

            // 获取目标字段真实类型并转换源值
            final Type fieldType = TypeKit.getActualType(this.targetType, tDesc.getFieldType());
            sValue = Convert.convertWithCheck(fieldType, sValue, null, this.copyOptions.ignoreError);
            sValue = copyOptions.editFieldValue(sFieldName, sValue);

            // 目标赋值
            tDesc.setValue(this.target, sValue, copyOptions.ignoreNullValue, copyOptions.ignoreError, copyOptions.override);
        });
        return this.target;
    }

}
