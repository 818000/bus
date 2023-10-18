/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2023 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 当任务队列过长时处于阻塞状态，直到添加到队列中，如果阻塞过程中被中断，就会抛出{@link InterruptedException}异常
 * 有时候在线程池内访问第三方接口，只希望固定并发数去访问，并且不希望丢弃任务时使用此策略，队列满的时候会处于阻塞状态(例如刷库的场景)
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class BlockPolicy implements RejectedExecutionHandler {

    /**
     * 线程池关闭时，为避免任务丢失，留下处理方法
     * 如果需要由调用方来运行，可以{@code new BlockPolicy(Runnable::run)}
     */
    private final Consumer<Runnable> handler;

    /**
     * 构造
     *
     * @param handler 线程池关闭后的执行策略
     */
    public BlockPolicy(final Consumer<Runnable> handler) {
        this.handler = handler;
    }

    /**
     * 构造
     */
    public BlockPolicy() {
        this(null);
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        // 线程池未关闭时，阻塞等待
        if (false == e.isShutdown()) {
            try {
                e.getQueue().put(r);
            } catch (InterruptedException ex) {
                throw new RejectedExecutionException("Task " + r + " rejected from " + e);
            }
        } else if (null != handler) {
            // 当设置了关闭时候的处理
            handler.accept(r);
        }
        // 线程池关闭后，丢弃任务
    }

}
