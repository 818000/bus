/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2024 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.office.word;

import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.miaixz.bus.core.io.file.FileName;
import org.miaixz.bus.core.lang.Assert;
import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.core.xyz.FileKit;
import org.miaixz.bus.core.xyz.IoKit;

import java.awt.*;
import java.io.*;

/**
 * Word docx生成器
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Word07Writer implements Closeable {

    private final XWPFDocument doc;
    /**
     * 目标文件
     */
    protected File destFile;
    /**
     * 是否被关闭
     */
    protected boolean isClosed;

    /**
     * 构造
     */
    public Word07Writer() {
        this(new XWPFDocument());
    }

    /**
     * 构造
     *
     * @param destFile 写出的文件
     */
    public Word07Writer(final File destFile) {
        this(DocxKit.create(destFile), destFile);
    }

    /**
     * 构造
     *
     * @param doc {@link XWPFDocument}
     */
    public Word07Writer(final XWPFDocument doc) {
        this(doc, null);
    }

    /**
     * 构造
     *
     * @param doc      {@link XWPFDocument}
     * @param destFile 写出的文件
     */
    public Word07Writer(final XWPFDocument doc, final File destFile) {
        this.doc = doc;
        this.destFile = destFile;
    }

    /**
     * 获取图片类型枚举
     *
     * @param fileName 文件名称
     * @return 图片类型枚举
     */
    public static PictureType getType(final String fileName) {
        String extName = FileName.extName(fileName).toUpperCase();
        if ("JPG".equals(extName)) {
            extName = "JPEG";
        }

        PictureType picType;
        try {
            picType = PictureType.valueOf(extName);
        } catch (final IllegalArgumentException e) {
            // 默认值
            picType = PictureType.JPEG;
        }
        return picType;
    }

    /**
     * 获取{@link XWPFDocument}
     *
     * @return {@link XWPFDocument}
     */
    public XWPFDocument getDoc() {
        return this.doc;
    }

    /**
     * 设置写出的目标文件
     *
     * @param destFile 目标文件
     * @return this
     */
    public Word07Writer setDestFile(final File destFile) {
        this.destFile = destFile;
        return this;
    }

    /**
     * 增加一个段落
     *
     * @param font  字体信息{@link Font}
     * @param texts 段落中的文本，支持多个文本作为一个段落
     * @return this
     */
    public Word07Writer addText(final Font font, final String... texts) {
        return addText(null, font, texts);
    }

    /**
     * 增加一个段落
     *
     * @param align 段落对齐方式{@link ParagraphAlignment}
     * @param font  字体信息{@link Font}
     * @param texts 段落中的文本，支持多个文本作为一个段落
     * @return this
     */
    public Word07Writer addText(final ParagraphAlignment align, final Font font, final String... texts) {
        final XWPFParagraph p = this.doc.createParagraph();
        if (null != align) {
            p.setAlignment(align);
        }
        if (ArrayKit.isNotEmpty(texts)) {
            XWPFRun run;
            for (final String text : texts) {
                run = p.createRun();
                run.setText(text);
                if (null != font) {
                    run.setFontFamily(font.getFamily());
                    run.setFontSize(font.getSize());
                    run.setBold(font.isBold());
                    run.setItalic(font.isItalic());
                }
            }
        }
        return this;
    }

    /**
     * 增加表格数据
     *
     * @param data 表格数据，多行数据。元素表示一行数据，当为集合或者数组时，为一行；当为Map或者Bean时key表示标题，values为数据
     * @return this
     * @see DocxTable#createTable(XWPFDocument, Iterable)
     */
    public Word07Writer addTable(final Iterable<?> data) {
        DocxTable.createTable(this.doc, data);
        return this;
    }

    /**
     * 增加图片，单独成段落
     *
     * @param picFile 图片文件
     * @param width   宽度
     * @param height  高度
     * @return this
     */
    public Word07Writer addPicture(final File picFile, final int width, final int height) {
        final String fileName = picFile.getName();
        return addPicture(FileKit.getInputStream(picFile), getType(fileName), fileName, width, height);
    }

    /**
     * 增加图片，单独成段落，增加后图片流关闭，默认居中对齐
     *
     * @param in       图片流
     * @param picType  图片类型，见Document.PICTURE_TYPE_XXX
     * @param fileName 文件名
     * @param width    宽度
     * @param height   高度
     * @return this
     */
    public Word07Writer addPicture(final InputStream in, final PictureType picType, final String fileName,
            final int width, final int height) {
        return addPicture(in, picType, fileName, width, height, ParagraphAlignment.CENTER);
    }

    /**
     * 增加图片，单独成段落，增加后图片流关闭
     *
     * @param in       图片流
     * @param picType  图片类型，见Document.PICTURE_TYPE_XXX
     * @param fileName 文件名
     * @param width    宽度
     * @param height   高度
     * @param align    图片的对齐方式
     * @return this
     */
    public Word07Writer addPicture(final InputStream in, final PictureType picType, final String fileName,
            final int width, final int height, final ParagraphAlignment align) {
        final XWPFParagraph paragraph = doc.createParagraph();
        paragraph.setAlignment(align);
        final XWPFRun run = paragraph.createRun();
        try {
            run.addPicture(in, picType, fileName, Units.toEMU(width), Units.toEMU(height));
        } catch (final InvalidFormatException e) {
            throw new InternalException(e);
        } catch (final IOException e) {
            throw new InternalException(e);
        } finally {
            IoKit.closeQuietly(in);
        }

        return this;
    }

    /**
     * 增加多张图片，单独成段落，增加后图片流关闭
     *
     * @param width    图片统一宽度
     * @param height   图片统一高度
     * @param picFiles 图片列表
     * @return this
     */
    public Word07Writer addPictures(final int width, final int height, final File... picFiles) {
        final XWPFParagraph paragraph = doc.createParagraph();
        XWPFRun run;
        try {
            for (final File picFile : picFiles) {
                run = paragraph.createRun();
                final String name = picFile.getName();
                try (final BufferedInputStream in = FileKit.getInputStream(picFile)) {
                    run.addPicture(in, getType(name), name, Units.toEMU(width), Units.toEMU(height));
                }
            }
        } catch (final InvalidFormatException e) {
            throw new InternalException(e);
        } catch (final IOException e) {
            throw new InternalException(e);
        }
        return this;
    }

    /**
     * 将Excel Workbook刷出到预定义的文件 如果用户未自定义输出的文件，将抛出{@link NullPointerException} 预定义文件可以通过{@link #setDestFile(File)}
     * 方法预定义，或者通过构造定义
     *
     * @return this
     * @throws InternalException IO异常
     */
    public Word07Writer flush() throws InternalException {
        return flush(this.destFile);
    }

    /**
     * 将Excel Workbook刷出到文件 如果用户未自定义输出的文件，将抛出{@link NullPointerException}
     *
     * @param destFile 写出到的文件
     * @return this
     * @throws InternalException IO异常
     */
    public Word07Writer flush(final File destFile) throws InternalException {
        Assert.notNull(destFile,
                "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).");
        return flush(FileKit.getOutputStream(destFile), true);
    }

    /**
     * 将Word Workbook刷出到输出流
     *
     * @param out 输出流
     * @return this
     * @throws InternalException IO异常
     */
    public Word07Writer flush(final OutputStream out) throws InternalException {
        return flush(out, false);
    }

    /**
     * 将Word Document刷出到输出流
     *
     * @param out        输出流
     * @param isCloseOut 是否关闭输出流
     * @return this
     * @throws InternalException IO异常
     */
    public Word07Writer flush(final OutputStream out, final boolean isCloseOut) throws InternalException {
        Assert.isFalse(this.isClosed, "WordWriter has been closed!");
        try {
            this.doc.write(out);
            out.flush();
        } catch (final IOException e) {
            throw new InternalException(e);
        } finally {
            if (isCloseOut) {
                IoKit.closeQuietly(out);
            }
        }
        return this;
    }

    /**
     * 关闭Word文档 如果用户设定了目标文件，先写出目标文件后给关闭工作簿
     */
    @Override
    public void close() {
        if (null != this.destFile) {
            flush();
        }
        closeWithoutFlush();
    }

    /**
     * 关闭Word文档但是不写出
     */
    protected void closeWithoutFlush() {
        IoKit.closeQuietly(this.doc);
        this.isClosed = true;
    }

}
