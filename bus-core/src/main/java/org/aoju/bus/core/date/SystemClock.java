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
package org.aoju.bus.core.date;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统时钟
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多(具体耗时高出多少我还没测试过,有人说是100倍左右)
 * System.currentTimeMillis()之所以慢是因为去跟系统打了一次交道
 * 后台定时更新时钟,JVM退出时,线程自动回收
 *
 * @author Kimi Liu
 * @version 6.0.9
 * @since JDK 1.8+
 */
public class SystemClock {

    /**
     * 时钟更新间隔,单位毫秒
     */
    private final long period;
    /**
     * 现在时刻的毫秒数
     */
    private volatile long now;

    private SystemClock(long period) {
        this.period = period;
        this.now = System.currentTimeMillis();
        scheduleClockUpdating();
    }

    /**
     * 单例实例
     *
     * @return 单例实例
     */
    private static SystemClock instance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * @return 当前时间
     */
    public static long now() {
        return instance().currentTimeMillis();
    }

    /**
     * @return 当前时间字符串表现形式
     */
    public static String nowDate() {
        return new Timestamp(instance().currentTimeMillis()).toString();
    }

    /**
     * 开启计时器线程
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "System Clock");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> now = System.currentTimeMillis(), period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @return 当前时间毫秒数
     */
    private long currentTimeMillis() {
        return now;
    }

    /**
     * 单例
     */
    private static class InstanceHolder {
        public static final SystemClock INSTANCE = new SystemClock(1);
    }

}
