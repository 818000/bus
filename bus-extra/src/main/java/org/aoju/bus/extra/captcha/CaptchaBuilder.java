/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
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
 ********************************************************************************/
package org.aoju.bus.extra.captcha;

import org.aoju.bus.extra.captcha.provider.CircleProvider;
import org.aoju.bus.extra.captcha.provider.LineProvider;
import org.aoju.bus.extra.captcha.provider.ShearProvider;

/**
 * 图形验证码工具
 *
 * @author Kimi Liu
 * @version 6.0.0
 * @since JDK 1.8+
 */
public class CaptchaBuilder {

    /**
     * 创建线干扰的验证码，默认5位验证码，150条干扰线
     *
     * @param width  图片宽
     * @param height 图片高
     * @return {@link  LineProvider}
     */
    public static LineProvider createLineCaptcha(int width, int height) {
        return new LineProvider(width, height);
    }

    /**
     * 创建线干扰的验证码
     *
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param lineCount 干扰线条数
     * @return {@link LineProvider}
     */
    public static LineProvider createLineCaptcha(int width, int height, int codeCount, int lineCount) {
        return new LineProvider(width, height, codeCount, lineCount);
    }

    /**
     * 创建圆圈干扰的验证码，默认5位验证码，15个干扰圈
     *
     * @param width  图片宽
     * @param height 图片高
     * @return {@link CircleProvider}
     */
    public static CircleProvider createCircleCaptcha(int width, int height) {
        return new CircleProvider(width, height);
    }

    /**
     * 创建圆圈干扰的验证码
     *
     * @param width       图片宽
     * @param height      图片高
     * @param codeCount   字符个数
     * @param circleCount 干扰圆圈条数
     * @return {@link CircleProvider}
     */
    public static CircleProvider createCircleCaptcha(int width, int height, int codeCount, int circleCount) {
        return new CircleProvider(width, height, codeCount, circleCount);
    }

    /**
     * 创建扭曲干扰的验证码，默认5位验证码
     *
     * @param width  图片宽
     * @param height 图片高
     * @return {@link ShearProvider}
     */
    public static ShearProvider createShearCaptcha(int width, int height) {
        return new ShearProvider(width, height);
    }

    /**
     * 创建扭曲干扰的验证码，默认5位验证码
     *
     * @param width     图片宽
     * @param height    图片高
     * @param codeCount 字符个数
     * @param thickness 干扰线宽度
     * @return {@link ShearProvider}
     */
    public static ShearProvider createShearCaptcha(int width, int height, int codeCount, int thickness) {
        return new ShearProvider(width, height, codeCount, thickness);
    }

}
