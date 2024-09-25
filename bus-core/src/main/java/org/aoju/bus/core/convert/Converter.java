/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.convert;

import org.aoju.bus.core.exception.ConvertException;
import org.aoju.bus.core.toolkit.ObjectKit;

import java.lang.reflect.Type;

/**
 * 转换器接口,实现类型转换
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@FunctionalInterface
public interface Converter {

    /**
     * 转换为指定类型 如果类型无法确定，将读取默认值的类型做为目标类型
     *
     * @param targetType 目标Type，非泛型类使用
     * @param value      原始值
     * @return 转换后的值
     * @throws ConvertException 转换无法正常完成或转换异常时抛出此异常
     */
    Object convert(Type targetType, Object value) throws ConvertException;

    /**
     * 转换值为指定类型，可选是否不抛异常转换 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param targetType   目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     */
    default <T> T convert(final Type targetType, final Object value, final T defaultValue) {
        return (T) ObjectKit.defaultIfNull(convert(targetType, value), defaultValue);
    }

}
