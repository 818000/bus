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
package org.aoju.bus.cron.listener;

import org.aoju.bus.cron.Executor;

/**
 * 定时任务监听接口
 * 通过实现此接口,实现对定时任务的各个环节做监听
 *
 * @author Kimi Liu
 * @version 6.1.0
 * @since JDK 1.8+
 */
public interface TaskListener {

    /**
     * 定时任务启动时触发
     *
     * @param executor {@link Executor}
     */
    void onStart(Executor executor);

    /**
     * 任务成功结束时触发
     *
     * @param executor {@link Executor}
     */
    void onSucceeded(Executor executor);

    /**
     * 任务启动失败时触发
     *
     * @param executor  {@link Executor}
     * @param exception 异常
     */
    void onFailed(Executor executor, Throwable exception);

}
