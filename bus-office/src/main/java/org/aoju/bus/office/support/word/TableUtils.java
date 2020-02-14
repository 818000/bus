/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.office.support.word;

import org.aoju.bus.core.convert.Convert;
import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.utils.BeanUtils;
import org.aoju.bus.core.utils.CollUtils;
import org.aoju.bus.core.utils.IterUtils;
import org.aoju.bus.core.utils.MapUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Word中表格相关工具
 *
 * @author Kimi Liu
 * @version 5.6.0
 * @since JDK 1.8+
 */
public class TableUtils {

    /**
     * 创建空表,只有一行
     *
     * @param doc {@link XWPFDocument}
     * @return {@link XWPFTable}
     */
    public static XWPFTable createTable(XWPFDocument doc) {
        return createTable(doc, null);
    }

    /**
     * 创建表格并填充数据
     *
     * @param doc  {@link XWPFDocument}
     * @param data 数据
     * @return {@link XWPFTable}
     */
    public static XWPFTable createTable(XWPFDocument doc, Iterable<?> data) {
        Assert.notNull(doc, "XWPFDocument must be not null !");
        XWPFTable table = doc.createTable();

        if (IterUtils.isEmpty(data)) {
            // 数据为空,返回空表
            return table;
        }

        int index = 0;
        for (Object rowData : data) {
            writeRow(getOrCreateRow(table, index), rowData, true);
            index++;
        }

        return table;
    }

    /**
     * 写一行数据
     *
     * @param row              行
     * @param rowBean          行数据
     * @param isWriteKeyAsHead 如果为Map或者Bean,是否写标题
     */
    public static void writeRow(XWPFTableRow row, Object rowBean, boolean isWriteKeyAsHead) {
        if (rowBean instanceof Iterable) {
            writeRow(row, (Iterable<?>) rowBean);
        }

        Map rowMap = null;
        if (rowBean instanceof Map) {
            rowMap = (Map) rowBean;
        } else if (BeanUtils.isBean(rowBean.getClass())) {
            rowMap = BeanUtils.beanToMap(rowBean, new LinkedHashMap<>(), false, false);
        } else {
            // 其它转为字符串默认输出
            writeRow(row, CollUtils.newArrayList(rowBean), isWriteKeyAsHead);
        }

        writeRow(row, rowMap, isWriteKeyAsHead);
    }

    /**
     * 写行数据
     *
     * @param row              行
     * @param rowMap           行数据
     * @param isWriteKeyAsHead 是否写标题
     */
    public static void writeRow(XWPFTableRow row, Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
        if (MapUtils.isEmpty(rowMap)) {
            return;
        }

        if (isWriteKeyAsHead) {
            writeRow(row, rowMap.keySet());
        }
        writeRow(row, rowMap.values());
    }

    /**
     * 写行数据
     *
     * @param row     行
     * @param rowData 行数据
     */
    public static void writeRow(XWPFTableRow row, Iterable<?> rowData) {
        XWPFTableCell cell;
        int index = 0;
        for (Object cellData : rowData) {
            cell = getOrCreateCell(row, index);
            cell.setText(Convert.toString(cellData));
            index++;
        }
    }

    /**
     * 获取或创建新行
     * 存在则直接返回,不存在创建新的行
     *
     * @param table {@link XWPFTable}
     * @param index 索引（行号）,从0开始
     * @return {@link XWPFTableRow}
     */
    public static XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
        XWPFTableRow row = table.getRow(index);
        if (null == row) {
            row = table.createRow();
        }

        return row;
    }

    /**
     * 获取或创建新单元格
     * 存在则直接返回,不存在创建新的单元格
     *
     * @param row   {@link XWPFTableRow} 行
     * @param index index 索引（列号）,从0开始
     * @return {@link XWPFTableCell}
     */
    public static XWPFTableCell getOrCreateCell(XWPFTableRow row, int index) {
        XWPFTableCell cell = row.getCell(index);
        if (null == cell) {
            cell = row.createCell();
        }
        return cell;
    }

}
