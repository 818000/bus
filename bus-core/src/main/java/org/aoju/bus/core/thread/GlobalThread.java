/*
 * The MIT License
 *
 * Copyright (c) 2017 aoju.org All rights reserved.
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
package org.aoju.bus.core.thread;

import org.aoju.bus.core.lang.exception.InstrumentException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 全局公共线程池
 *
 * @author Kimi Liu
 * @version 5.5.0
 * @since JDK 1.8+
 */
public class GlobalThread {

    private static ExecutorService executor;

    static {
        init();
    }

    private GlobalThread() {
    }

    /**
     * 初始化全局线程池
     */
    synchronized public static void init() {
        if (null != executor) {
            executor.shutdownNow();
        }
        executor = ExecutorBuilder.create().useSynchronousQueue().build();
    }

    /**
     * 关闭公共线程池
     *
     * @param isNow 是否立即关闭而不等待正在执行的线程
     */
    synchronized public static void shutdown(boolean isNow) {
        if (null != executor) {
            if (isNow) {
                executor.shutdownNow();
            } else {
                executor.shutdown();
            }
        }
    }

    /**
     * 获得 {@link ExecutorService}
     *
     * @return {@link ExecutorService}
     */
    public static ExecutorService getExecutor() {
        return executor;
    }

    /**
     * 直接在公共线程池中执行线程
     *
     * @param runnable 可运行对象
     */
    public static void execute(Runnable runnable) {
        try {
            executor.execute(runnable);
        } catch (Exception e) {
            throw new InstrumentException("Exception when running task!");
        }
    }

    /**
     * 执行有返回值的异步方法
     * Future代表一个异步执行的操作,通过get()方法可以获得操作的结果,如果异步操作还没有完成,则,get()会使当前线程阻塞
     *
     * @param <T>  执行的Task
     * @param task {@link Callable}
     * @return Future
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    /**
     * 执行有返回值的异步方法
     * Future代表一个异步执行的操作,通过get()方法可以获得操作的结果,如果异步操作还没有完成,则,get()会使当前线程阻塞
     *
     * @param runnable 可运行对象
     * @return {@link Future}
     * @since 3.1.9
     */
    public static Future<?> submit(Runnable runnable) {
        return executor.submit(runnable);
    }

}
