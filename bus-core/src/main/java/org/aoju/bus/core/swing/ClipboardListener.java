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
package org.aoju.bus.core.swing;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

/**
 * 剪贴板监听事件处理接口
 * 用户通过实现此接口,实现监听剪贴板内容变化
 *
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
public interface ClipboardListener {

    /**
     * 剪贴板变动触发的事件方法
     * 在此事件中对剪贴板设置值无效,如若修改,需返回修改内容
     *
     * @param clipboard 剪贴板对象
     * @param contents  内容
     * @return 如果对剪贴板内容做修改, 则返回修改的内容,{@code null}表示保留原内容
     */
    Transferable onChange(Clipboard clipboard, Transferable contents);

}
