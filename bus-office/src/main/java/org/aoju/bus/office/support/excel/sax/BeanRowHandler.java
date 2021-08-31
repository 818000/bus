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
package org.aoju.bus.office.support.excel.sax;

import org.aoju.bus.core.convert.Convert;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.toolkit.BeanKit;
import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.IterKit;

import java.util.List;

/**
 * Bean形式的行处理器
 * 将一行数据转换为Map，key为指定行，value为当前行对应位置的值
 *
 * @author Kimi Liu
 * @version 6.2.8
 * @since JDK 1.8+
 */
public abstract class BeanRowHandler<T> extends AbstractRowHandler<T> {

    /**
     * 标题所在行（从0开始计数）
     */
    private final int headerRowIndex;
    /**
     * 标题行
     */
    List<String> headerList;

    /**
     * 构造
     *
     * @param headerRowIndex 标题所在行（从0开始计数）
     * @param startRowIndex  读取起始行（包含，从0开始计数）
     * @param endRowIndex    读取结束行（包含，从0开始计数）
     * @param clazz          Bean类型
     */
    public BeanRowHandler(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> clazz) {
        super(startRowIndex, endRowIndex);
        Assert.isTrue(headerRowIndex <= startRowIndex, "Header row must before the start row!");
        this.headerRowIndex = headerRowIndex;
        this.convertFunc = (rowList) -> BeanKit.toBean(IterKit.toMap(headerList, rowList), clazz);
    }

    @Override
    public void handle(int sheetIndex, long rowIndex, List<Object> rowList) {
        if (rowIndex == this.headerRowIndex) {
            this.headerList = CollKit.unmodifiable(Convert.toList(String.class, rowList));
            return;
        }
        super.handle(sheetIndex, rowIndex, rowList);
    }

}
