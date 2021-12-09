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
package org.aoju.bus.mapper.additional.delete;

import org.aoju.bus.core.lang.function.Fn;
import org.aoju.bus.mapper.annotation.RegisterMapper;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @param <T> 不能为空
 * @author Kimi Liu
 * @version 6.3.2
 * @since JDK 1.8+
 */
@RegisterMapper
public interface DeleteByPropertyMapper<T> {

    /**
     * 根据实体中的属性删除，条件使用等号
     *
     * @param fn    属性
     * @param value 属性值
     * @return the int
     */
    @DeleteProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteByProperty(@Param("fn") Fn<T, ?> fn, @Param("value") Object value);

    /**
     * 根据实体中的属性删除，条件使用 in
     *
     * @param fn    属性
     * @param value 属性值
     * @return the int
     */
    @DeleteProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteInByProperty(@Param("fn") Fn<T, ?> fn, @Param("values") Object value);

    /**
     * 根据属性及对应值进行删除，删除条件使用 between
     *
     * @param fn    属性
     * @param begin 开始值
     * @param end   开始值
     * @return the int
     */
    @SelectProvider(type = DeletePropertyProvider.class, method = "dynamicSQL")
    int deleteBetweenByProperty(@Param("fn") Fn<T, ?> fn, @Param("begin") Object begin, @Param("end") Object end);

}
