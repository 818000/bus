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
package org.aoju.bus.office.support.excel;

import org.aoju.bus.core.date.DateTime;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.DateKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.office.Builder;
import org.aoju.bus.office.support.excel.sax.*;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Sax方式读取Excel相关工具类
 *
 * @author Kimi Liu
 * @version 6.2.6
 * @since JDK 1.8+
 */
public class ExcelSaxKit {

    // 填充字符串
    public static final char CELL_FILL_CHAR = Symbol.C_AT;
    // 列的最大位数
    public static final int MAX_CELL_BIT = 3;

    /**
     * 创建 {@link ExcelSaxReader}
     *
     * @param isXlsx     是否为xlsx格式（07格式）
     * @param rowHandler 行处理器
     * @return {@link ExcelSaxReader}
     */
    public static ExcelSaxReader<?> createSaxReader(boolean isXlsx, RowHandler rowHandler) {
        return isXlsx
                ? new Excel07SaxReader(rowHandler)
                : new Excel03SaxReader(rowHandler);
    }

    /**
     * 根据数据类型获取数据
     *
     * @param cellDataType       数据类型枚举
     * @param value              数据值
     * @param sharedStringsTable {@link SharedStringsTable}
     * @param numFmtString       数字格式名
     * @return 数据值
     */
    public static Object getDataValue(CellDataType cellDataType, String value, SharedStringsTable sharedStringsTable, String numFmtString) {
        if (null == value) {
            return null;
        }

        if (null == cellDataType) {
            cellDataType = CellDataType.NULL;
        }

        Object result;
        switch (cellDataType) {
            case BOOL:
                result = (value.charAt(0) != Symbol.C_ZERO);
                break;
            case ERROR:
                result = StringKit.format("\\\"ERROR: {} ", value);
                break;
            case FORMULA:
                result = StringKit.format("\"{}\"", value);
                break;
            case INLINESTR:
                result = new XSSFRichTextString(value).toString();
                break;
            case SSTINDEX:
                try {
                    final int index = Integer.parseInt(value);
                    result = sharedStringsTable.getItemAt(index).getString();
                } catch (NumberFormatException e) {
                    result = value;
                }
                break;
            case NUMBER:
                try {
                    result = getNumberValue(value, numFmtString);
                } catch (NumberFormatException e) {
                    result = value;
                }
                break;
            case DATE:
                try {
                    result = getDateValue(value);
                } catch (Exception e) {
                    result = value;
                }
                break;
            default:
                result = value;
                break;
        }
        return result;
    }

    /**
     * 格式化数字或日期值
     *
     * @param value        值
     * @param numFmtIndex  数字格式索引
     * @param numFmtString 数字格式名
     * @return 格式化后的值
     */
    public static String formatCellContent(String value, int numFmtIndex, String numFmtString) {
        if (null != numFmtString) {
            try {
                value = new DataFormatter().formatRawCellContents(Double.parseDouble(value), numFmtIndex, numFmtString);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return value;
    }

    /**
     * 计算两个单元格之间的单元格数目(同一行)
     *
     * @param preRef 前一个单元格位置,例如A1
     * @param ref    当前单元格位置,例如A8
     * @return 同一行中两个单元格之间的空单元格数
     */
    public static int countNullCell(String preRef, String ref) {
        // excel2007最大行数是1048576,最大列数是16384,最后一列列名是XFD
        // 数字代表列,去掉列信息
        String preXfd = StringKit.nullToDefault(preRef, Symbol.AT).replaceAll("\\d+", Normal.EMPTY);
        String xfd = StringKit.nullToDefault(ref, Symbol.AT).replaceAll("\\d+", Normal.EMPTY);

        // A表示65,@表示64,如果A算作1,那@代表0
        // 填充最大位数3
        preXfd = StringKit.fillBefore(preXfd, CELL_FILL_CHAR, MAX_CELL_BIT);
        xfd = StringKit.fillBefore(xfd, CELL_FILL_CHAR, MAX_CELL_BIT);

        char[] preLetter = preXfd.toCharArray();
        char[] letter = xfd.toCharArray();
        // 用字母表示则最多三位,每26个字母进一位
        int res = (letter[0] - preLetter[0]) * 26 * 26 + (letter[1] - preLetter[1]) * 26 + (letter[2] - preLetter[2]);
        return res - 1;
    }

    /**
     * 从Excel的XML文档中读取内容，并使用{@link ContentHandler}处理
     *
     * @param xmlDocStream Excel的XML文档流
     * @param handler      文档内容处理接口，实现此接口用于回调处理数据
     * @throws InstrumentException POI异常，包装了SAXException
     */
    public static void readFrom(InputStream xmlDocStream, ContentHandler handler) throws InstrumentException {
        XMLReader xmlReader;
        try {
            xmlReader = XMLHelper.newXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(xmlDocStream));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 获取日期
     *
     * @param value 单元格值
     * @return 日期
     */
    public static DateTime getDateValue(String value) {
        return getDateValue(Double.parseDouble(value));
    }

    /**
     * 获取日期
     *
     * @param value 单元格值
     * @return 日期
     */
    public static DateTime getDateValue(double value) {
        return DateKit.date(org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value, false));
    }

    /**
     * 在Excel03 sax读取中获取日期或数字类型的结果值
     *
     * @param cell           记录单元格
     * @param value          值
     * @param formatListener {@link FormatTrackingHSSFListener}
     * @return 值，可能为Date或Double或Long
     */
    public static Object getNumberOrDateValue(CellValueRecordInterface cell, double value, FormatTrackingHSSFListener formatListener) {
        if (Builder.isDateFormat(formatListener.getFormatIndex(cell), formatListener.getFormatString(cell))) {
            // 可能为日期格式
            return getDateValue(value);
        }
        return getNumberValue(value, formatListener.getFormatString(cell));
    }

    /**
     * 获取数字类型值
     *
     * @param value        值
     * @param numFmtString 格式
     * @return 数字，可以是Double、Long
     */
    private static Number getNumberValue(String value, String numFmtString) {
        if (StringKit.isBlank(value)) {
            return null;
        }
        return getNumberValue(Double.parseDouble(value), numFmtString);
    }

    /**
     * 获取数字类型值，除非格式中明确数字保留小数，否则无小数情况下按照long返回
     *
     * @param numValue     值
     * @param numFmtString 格式
     * @return 数字，可以是Double、Long
     */
    private static Number getNumberValue(double numValue, String numFmtString) {
        // 普通数字
        if (null != numFmtString && false == StringKit.contains(numFmtString, Symbol.DOT)) {
            return (long) numValue;
        }
        return numValue;
    }

}
