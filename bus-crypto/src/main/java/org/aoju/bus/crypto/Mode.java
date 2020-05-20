/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.crypto;

/**
 * 模式
 * 加密算法模式,是用来描述加密算法(此处特指分组密码,不包括流密码,)
 * 在加密时对明文分组的模式,它代表了不同的分组方式
 *
 * @author Kimi Liu
 * @version 5.9.2
 * @since JDK 1.8+
 */
public enum Mode {
    /**
     * 无模式
     */
    NONE,
    /**
     * 密码分组连接模式(Cipher Block Chaining)
     */
    CBC,
    /**
     * 密文反馈模式(Cipher Feedback)
     */
    CFB,
    /**
     * 计数器模式(A simplification of OFB)
     */
    CTR,
    /**
     * Cipher Text Stealing
     */
    CTS,
    /**
     * 电子密码本模式(Electronic CodeBook)
     */
    ECB,
    /**
     * 输出反馈模式(Output Feedback)
     */
    OFB,
    /**
     * Propagating Cipher Block
     */
    PCBC
}
