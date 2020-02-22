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
package org.aoju.bus.office.support.excel;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.utils.DateUtils;
import org.aoju.bus.office.support.excel.cell.CellEditor;
import org.aoju.bus.office.support.excel.cell.FormulaCellValue;
import org.aoju.bus.office.support.excel.editors.TrimEditor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

/**
 * Excel表格中单元格工具类
 *
 * @author Kimi Liu
 * @version 5.6.3
 * @since JDK 1.8+
 */
public class CellUtils {

    /**
     * 获取单元格值
     *
     * @param cell            {@link Cell}单元格
     * @param isTrimCellValue 如果单元格类型为字符串,是否去掉两边空白符
     * @return 值, 类型可能为：Date、Double、Boolean、String
     */
    public static Object getCellValue(Cell cell, boolean isTrimCellValue) {
        if (null == cell) {
            return null;
        }
        return getCellValue(cell, cell.getCellType(), isTrimCellValue);
    }

    /**
     * 获取单元格值
     *
     * @param cell       {@link Cell}单元格
     * @param cellEditor 单元格值编辑器 可以通过此编辑器对单元格值做自定义操作
     * @return 值, 类型可能为：Date、Double、Boolean、String
     */
    public static Object getCellValue(Cell cell, CellEditor cellEditor) {
        if (null == cell) {
            return null;
        }
        return getCellValue(cell, cell.getCellType(), cellEditor);
    }

    /**
     * 获取单元格值
     *
     * @param cell            {@link Cell}单元格
     * @param cellType        单元格值类型{@link CellType}枚举
     * @param isTrimCellValue 如果单元格类型为字符串,是否去掉两边空白符
     * @return 值, 类型可能为：Date、Double、Boolean、String
     */
    public static Object getCellValue(Cell cell, CellType cellType, final boolean isTrimCellValue) {
        return getCellValue(cell, cellType, isTrimCellValue ? new TrimEditor() : null);
    }

    /**
     * 获取单元格值
     * 如果单元格值为数字格式,则判断其格式中是否有小数部分,无则返回Long类型,否则返回Double类型
     *
     * @param cell       {@link Cell}单元格
     * @param cellType   单元格值类型{@link CellType}枚举,如果为{@code null}默认使用cell的类型
     * @param cellEditor 单元格值编辑器 可以通过此编辑器对单元格值做自定义操作
     * @return 值, 类型可能为：Date、Double、Boolean、String
     */
    public static Object getCellValue(Cell cell, CellType cellType, CellEditor cellEditor) {
        if (null == cell) {
            return null;
        }
        if (null == cellType) {
            cellType = cell.getCellType();
        }

        Object value;
        switch (cellType) {
            case NUMERIC:
                value = getNumericValue(cell);
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                // 遇到公式时查找公式结果类型
                value = getCellValue(cell, cell.getCachedFormulaResultType(), cellEditor);
                break;
            case BLANK:
                value = Normal.EMPTY;
                break;
            case ERROR:
                final FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
                value = (null == error) ? Normal.EMPTY : error.getString();
                break;
            default:
                value = cell.getStringCellValue();
        }
        return null == cellEditor ? value : cellEditor.edit(cell, value);
    }

    /**
     * 设置单元格值
     * 根据传入的styleSet自动匹配样式
     * 当为头部样式时默认赋值头部样式,但是头部中如果有数字、日期等类型,将按照数字、日期样式设置
     *
     * @param cell     单元格
     * @param value    值
     * @param styleSet 单元格样式集,包括日期等样式
     * @param isHeader 是否为标题单元格
     */
    public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
        if (null == cell) {
            return;
        }

        if (null != styleSet) {
            final CellStyle headCellStyle = styleSet.getHeadCellStyle();
            final CellStyle cellStyle = styleSet.getCellStyle();
            if (isHeader && null != headCellStyle) {
                cell.setCellStyle(headCellStyle);
            } else if (null != cellStyle) {
                cell.setCellStyle(cellStyle);
            }
        }

        if (value instanceof Date) {
            if (null != styleSet && null != styleSet.getCellStyleForDate()) {
                cell.setCellStyle(styleSet.getCellStyleForDate());
            }
        } else if (value instanceof TemporalAccessor) {
            if (null != styleSet && null != styleSet.getCellStyleForDate()) {
                cell.setCellStyle(styleSet.getCellStyleForDate());
            }
        } else if (value instanceof Calendar) {
            if (null != styleSet && null != styleSet.getCellStyleForDate()) {
                cell.setCellStyle(styleSet.getCellStyleForDate());
            }
        } else if (value instanceof Number) {
            if ((value instanceof Double || value instanceof Float || value instanceof BigDecimal) && null != styleSet && null != styleSet.getCellStyleForNumber()) {
                cell.setCellStyle(styleSet.getCellStyleForNumber());
            }
        }
        setCellValue(cell, value, null);
    }

    /**
     * 设置单元格值
     * 根据传入的styleSet自动匹配样式
     * 当为头部样式时默认赋值头部样式，但是头部中如果有数字、日期等类型，将按照数字、日期样式设置
     *
     * @param cell  单元格
     * @param value 值
     * @param style 自定义样式，null表示无样式
     */
    public static void setCellValue(Cell cell, Object value, CellStyle style) {
        if (null == cell) {
            return;
        }

        if (null != style) {
            cell.setCellStyle(style);
        }

        if (null == value) {
            cell.setCellValue(Normal.EMPTY);
        } else if (value instanceof FormulaCellValue) {
            // 公式
            cell.setCellFormula(((FormulaCellValue) value).getValue());
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof TemporalAccessor) {
            if (value instanceof Instant) {
                cell.setCellValue(Date.from((Instant) value));
            } else if (value instanceof LocalDateTime) {
                cell.setCellValue((LocalDateTime) value);
            } else if (value instanceof LocalDate) {
                cell.setCellValue((LocalDate) value);
            }
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取已有行或创建新行
     *
     * @param row       Excel表的行
     * @param cellIndex 列号
     * @return {@link Row}
     */
    public static Cell getOrCreateCell(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (null == cell) {
            cell = row.createCell(cellIndex);
        }
        return cell;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet  {@link Sheet}
     * @param row    行号
     * @param column 列号
     * @return 是否是合并单元格
     */
    public static boolean isMergedRegion(Sheet sheet, int row, int column) {
        final int sheetMergeCount = sheet.getNumMergedRegions();
        CellRangeAddress ca;
        for (int i = 0; i < sheetMergeCount; i++) {
            ca = sheet.getMergedRegion(i);
            if (row >= ca.getFirstRow() && row <= ca.getLastRow() && column >= ca.getFirstColumn() && column <= ca.getLastColumn()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 合并单元格,可以根据设置的值来合并行和列
     *
     * @param sheet       表对象
     * @param firstRow    起始行,0开始
     * @param lastRow     结束行,0开始
     * @param firstColumn 起始列,0开始
     * @param lastColumn  结束列,0开始
     * @param cellStyle   单元格样式,只提取边框样式
     * @return 合并后的单元格号
     */
    public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
        final CellRangeAddress cellRangeAddress = new CellRangeAddress(//
                firstRow, // first row (0-based)
                lastRow, // last row (0-based)
                firstColumn, // first column (0-based)
                lastColumn // last column (0-based)
        );

        if (null != cellStyle) {
            RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
            RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
            RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
            RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
        }
        return sheet.addMergedRegion(cellRangeAddress);
    }

    /**
     * 获取数字类型的单元格值
     *
     * @param cell 单元格
     * @return 单元格值, 可能为Long、Double、Date
     */
    private static Object getNumericValue(Cell cell) {
        final double value = cell.getNumericCellValue();

        final CellStyle style = cell.getCellStyle();
        if (null == style) {
            return value;
        }

        final short formatIndex = style.getDataFormat();
        // 判断是否为日期
        if (isDateType(cell, formatIndex)) {
            return DateUtils.date(cell.getDateCellValue());
        }

        final String format = style.getDataFormatString();
        // 普通数字
        if (null != format && format.indexOf(Symbol.C_DOT) < 0) {
            final long longPart = (long) value;
            if (longPart == value) {
                // 对于无小数部分的数字类型,转为Long
                return longPart;
            }
        }
        return value;
    }

    /**
     * 是否为日期格式
     * 判断方式：
     *
     * <pre>
     * 1、指定序号
     * 2、org.apache.poi.ss.usermodel.DateUtil.isADateFormat方法判定
     * </pre>
     *
     * @param cell        单元格
     * @param formatIndex 格式序号
     * @return 是否为日期格式
     */
    private static boolean isDateType(Cell cell, int formatIndex) {
        if (formatIndex == 14 || formatIndex == 31 || formatIndex == 57 || formatIndex == 58 || formatIndex == 20 || formatIndex == 32) {
            return true;
        }

        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            return true;
        }

        return false;
    }

}
