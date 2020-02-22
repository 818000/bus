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

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.utils.CollUtils;
import org.aoju.bus.core.utils.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel图片工具类
 *
 * @author Kimi Liu
 * @version 5.6.3
 * @since JDK 1.8+
 */
public class ExcelPicUtils {

    /**
     * 获取工作簿指定sheet中图片列表
     *
     * @param workbook   工作簿{@link Workbook}
     * @param sheetIndex sheet的索引
     * @return 图片映射, 键格式：行_列,值：{@link PictureData}
     */
    public static Map<String, PictureData> getPicMap(Workbook workbook, int sheetIndex) {
        Assert.notNull(workbook, "Workbook must be not null !");
        if (sheetIndex < 0) {
            sheetIndex = 0;
        }

        if (workbook instanceof HSSFWorkbook) {
            return getPicMapXls((HSSFWorkbook) workbook, sheetIndex);
        } else if (workbook instanceof XSSFWorkbook) {
            return getPicMapXlsx((XSSFWorkbook) workbook, sheetIndex);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Workbook type [{}] is not supported!", workbook.getClass()));
        }
    }

    /**
     * 获取XLS工作簿指定sheet中图片列表
     *
     * @param workbook   工作簿{@link Workbook}
     * @param sheetIndex sheet的索引
     * @return 图片映射, 键格式：行_列,值：{@link PictureData}
     */
    private static Map<String, PictureData> getPicMapXls(HSSFWorkbook workbook, int sheetIndex) {
        final Map<String, PictureData> picMap = new HashMap<>();
        final List<HSSFPictureData> pictures = workbook.getAllPictures();
        if (CollUtils.isNotEmpty(pictures)) {
            final HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
            HSSFClientAnchor anchor;
            int pictureIndex;
            for (HSSFShape shape : sheet.getDrawingPatriarch().getChildren()) {
                if (shape instanceof HSSFPicture) {
                    pictureIndex = ((HSSFPicture) shape).getPictureIndex() - 1;
                    anchor = (HSSFClientAnchor) shape.getAnchor();
                    picMap.put(StringUtils.format("{}_{}", anchor.getRow1(), anchor.getCol1()), pictures.get(pictureIndex));
                }
            }
        }
        return picMap;
    }

    /**
     * 获取XLSX工作簿指定sheet中图片列表
     *
     * @param workbook   工作簿{@link Workbook}
     * @param sheetIndex sheet的索引
     * @return 图片映射, 键格式：行_列,值：{@link PictureData}
     */
    private static Map<String, PictureData> getPicMapXlsx(XSSFWorkbook workbook, int sheetIndex) {
        final Map<String, PictureData> sheetIndexPicMap = new HashMap<>();
        final XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        XSSFDrawing drawing;
        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing) {
                drawing = (XSSFDrawing) dr;
                final List<XSSFShape> shapes = drawing.getShapes();
                XSSFPicture pic;
                CTMarker ctMarker;
                for (XSSFShape shape : shapes) {
                    pic = (XSSFPicture) shape;
                    ctMarker = pic.getPreferredSize().getFrom();
                    sheetIndexPicMap.put(StringUtils.format("{}_{}", ctMarker.getRow(), ctMarker.getCol()), pic.getPictureData());
                }
            }
        }
        return sheetIndexPicMap;
    }

}
