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
package org.aoju.bus.core.image.painter;

import org.aoju.bus.core.image.Images;
import org.aoju.bus.core.image.element.AbstractElement;
import org.aoju.bus.core.image.element.ImageElement;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 图片绘制器
 *
 * @author Kimi Liu
 * @version 6.3.3
 * @since JDK 1.8+
 */
public class ImagePainter implements Painter {

    @Override
    public void draw(Graphics2D g, AbstractElement element, int canvasWidth) {
        // 强制转成子类
        ImageElement imageElement = (ImageElement) element;

        // 读取元素图
        BufferedImage image = imageElement.getImage();

        // 计算宽高
        int width = 0;
        int height = 0;

        switch (imageElement.getZoomMode()) {
            case ORIGIN:
                width = image.getWidth();
                height = image.getHeight();
                break;
            case WIDTH:
                width = imageElement.getWidth();
                height = image.getHeight() * width / image.getWidth();
                break;
            case HEIGHT:
                height = imageElement.getHeight();
                width = image.getWidth() * height / image.getHeight();
                break;
            case OPTIONAL:
                height = imageElement.getHeight();
                width = imageElement.getWidth();
                break;
        }

        // 设置圆角
        if (null != imageElement.getRoundCorner()) {
            image = Images.makeRoundCorner(image, width, height, imageElement.getRoundCorner());
        }

        //高斯模糊
        if (null != imageElement.getBlur()) {
            image = Images.makeBlur(image, imageElement.getBlur());
        }

        // 判断是否设置居中
        if (imageElement.isCenter()) {
            int centerX = (canvasWidth - width) / 2;
            imageElement.setX(centerX);
        }

        //旋转
        if (null != imageElement.getRotate()) {
            g.rotate(Math.toRadians(imageElement.getRotate()), imageElement.getX() + imageElement.getWidth() / 2, imageElement.getY() + imageElement.getHeight() / 2);
        }

        //设置透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, imageElement.getAlpha()));

        // 将元素图绘制到画布
        g.drawImage(image, imageElement.getX(), imageElement.getY(), width, height, null);

        //绘制完后反向旋转，以免影响后续元素
        if (null != imageElement.getRotate()) {
            g.rotate(-Math.toRadians(imageElement.getRotate()), imageElement.getX() + imageElement.getWidth() / 2, imageElement.getY() + imageElement.getHeight() / 2);
        }
    }

}

