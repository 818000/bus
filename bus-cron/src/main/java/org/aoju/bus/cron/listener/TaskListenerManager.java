/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.cron.listener;

import org.aoju.bus.cron.Executor;
import org.aoju.bus.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 监听调度器,统一管理监听
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class TaskListenerManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<TaskListener> listeners = new ArrayList<>();

    /**
     * 增加监听器
     *
     * @param listener {@link TaskListener}
     * @return this
     */
    public TaskListenerManager addListener(TaskListener listener) {
        synchronized (listeners) {
            this.listeners.add(listener);
        }
        return this;
    }

    /**
     * 移除监听器
     *
     * @param listener {@link TaskListener}
     * @return this
     */
    public TaskListenerManager removeListener(TaskListener listener) {
        synchronized (listeners) {
            this.listeners.remove(listener);
        }
        return this;
    }

    /**
     * 通知所有监听任务启动器启动
     *
     * @param executor {@link Executor}
     */
    public void notifyTaskStart(Executor executor) {
        synchronized (listeners) {
            TaskListener listener;
            for (TaskListener taskListener : listeners) {
                listener = taskListener;
                if (null != listener) {
                    listener.onStart(executor);
                }
            }
        }
    }

    /**
     * 通知所有监听任务启动器成功结束
     *
     * @param executor {@link Executor}
     */
    public void notifyTaskSucceeded(Executor executor) {
        synchronized (listeners) {
            for (TaskListener listener : listeners) {
                listener.onSucceeded(executor);
            }
        }
    }

    /**
     * 通知所有监听任务启动器结束并失败
     * 无监听将打印堆栈到命令行
     *
     * @param executor  {@link Executor}
     * @param exception 失败原因
     */
    public void notifyTaskFailed(Executor executor, Throwable exception) {
        synchronized (listeners) {
            int size = listeners.size();
            if (size > 0) {
                for (TaskListener listener : listeners) {
                    listener.onFailed(executor, exception);
                }
            } else {
                Logger.error(exception, exception.getMessage());
            }
        }
    }

}
