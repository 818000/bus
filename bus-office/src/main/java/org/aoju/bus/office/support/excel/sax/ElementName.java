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

/**
 * 标签名枚举
 *
 * @author Kimi Liu
 * @version 6.3.0
 * @since JDK 1.8+
 */
public enum ElementName {

    /**
     * 行标签名，表示一行
     */
    row,
    /**
     * Cell单元格标签名，表示一个单元格
     */
    c,
    /**
     * Value单元格值的标签，表示单元格内的值
     */
    v,
    /**
     * Formula公式，表示一个存放公式的单元格
     */
    f;

    /**
     * 解析支持的节点名枚举
     *
     * @param elementName 节点名
     * @return 节点名枚举
     */
    public static ElementName of(String elementName) {
        try {
            return valueOf(elementName);
        } catch (Exception ignore) {
        }
        return null;
    }

    /**
     * 给定标签名是否匹配当前标签
     *
     * @param elementName 标签名
     * @return 是否匹配
     */
    public boolean match(String elementName) {
        return this.name().equals(elementName);
    }

}
