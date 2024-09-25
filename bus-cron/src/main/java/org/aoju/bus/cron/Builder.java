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
package org.aoju.bus.cron;

import org.aoju.bus.core.exception.CrontabException;
import org.aoju.bus.core.exception.InternalException;
import org.aoju.bus.core.lang.Charset;
import org.aoju.bus.cron.factory.Task;
import org.aoju.bus.cron.pattern.CronPattern;
import org.aoju.bus.setting.magic.PopSetting;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 定时任务工具类 此工具持有一个全局{@link Scheduler},所有定时任务在同一个调度器中执行 {@link #setMatchSecond(boolean)}
 * 方法用于定义是否使用秒匹配模式,如果为true,则定时任务表达式中的第一位为秒,否则为分,默认是分
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Builder {

    /**
     * Crontab配置文件
     */
    public static final String CRONTAB_CONFIG_PATH = "config/cron.setting";
    public static final String CRONTAB_CONFIG_PATH2 = "cron.setting";

    private static final Lock lock = new ReentrantLock();
    private static final Scheduler scheduler = new Scheduler();
    private static PopSetting crontabSetting;

    /**
     * 自定义定时任务配置文件
     *
     * @param cronSetting 定时任务配置文件
     */
    public static void setCronSetting(PopSetting cronSetting) {
        crontabSetting = cronSetting;
    }

    /**
     * 自定义定时任务配置文件路径
     *
     * @param cronSettingPath 定时任务配置文件路径（相对绝对都可）
     */
    public static void setCronSetting(String cronSettingPath) {
        try {
            crontabSetting = new PopSetting(cronSettingPath, Charset.UTF_8, false);
        } catch (InternalException e) {
            // ignore setting file parse error and no config error
        }
    }

    /**
     * 设置是否支持秒匹配 此方法用于定义是否使用秒匹配模式,如果为true,则定时任务表达式中的第一位为秒,否则为分,默认是分
     *
     * @param isMatchSecond <code>true</code>支持,<code>false</code>不支持
     */
    public static void setMatchSecond(boolean isMatchSecond) {
        scheduler.setMatchSecond(isMatchSecond);
    }

    /**
     * 加入定时任务
     *
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task              任务
     * @return 定时任务ID
     */
    public static String schedule(String schedulingPattern, Task task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    /**
     * 加入定时任务
     *
     * @param id                定时任务ID
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task              任务
     * @return 定时任务ID
     */
    public static String schedule(String id, String schedulingPattern, Task task) {
        scheduler.schedule(id, schedulingPattern, task);
        return id;
    }

    /**
     * 加入定时任务
     *
     * @param schedulingPattern 定时任务执行时间的crontab表达式
     * @param task              任务
     * @return 定时任务ID
     */
    public static String schedule(String schedulingPattern, Runnable task) {
        return scheduler.schedule(schedulingPattern, task);
    }

    /**
     * 批量加入配置文件中的定时任务
     *
     * @param cronSetting 定时任务设置文件
     */
    public static void schedule(PopSetting cronSetting) {
        scheduler.schedule(cronSetting);
    }

    /**
     * 移除任务
     *
     * @param schedulerId 任务ID
     * @return 是否移除成功，{@code false}表示未找到对应ID的任务
     */
    public static boolean remove(String schedulerId) {
        return scheduler.descheduleWithStatus(schedulerId);
    }

    /**
     * 更新Task的执行时间规则
     *
     * @param id      Task的ID
     * @param pattern {@link CronPattern}
     */
    public static void updatePattern(String id, CronPattern pattern) {
        scheduler.updatePattern(id, pattern);
    }

    /**
     * @return 获得Scheduler对象
     */
    public static Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * 开始,非守护线程模式
     *
     * @see #start(boolean)
     */
    public static void start() {
        start(false);
    }

    /**
     * 开始
     *
     * @param isDaemon 是否以守护线程方式启动,如果为true,则在调用{@link #stop()}方法后执行的定时任务立即结束,否则等待执行完毕才结束
     */
    synchronized public static void start(boolean isDaemon) {
        if (scheduler.isStarted()) {
            throw new CrontabException("Scheduler has been started, please stop it first!");
        }

        lock.lock();
        try {
            if (null == crontabSetting) {
                // 尝试查找config/cron.setting
                setCronSetting(CRONTAB_CONFIG_PATH);
            }
            // 尝试查找cron.setting
            if (null == crontabSetting) {
                setCronSetting(CRONTAB_CONFIG_PATH2);
            }
        } finally {
            lock.unlock();
        }

        schedule(crontabSetting);
        scheduler.start(isDaemon);
    }

    /**
     * 重新启动定时任务 此方法会清除动态加载的任务,重新启动后,守护线程与否与之前保持一致
     */
    public static void restart() {
        lock.lock();
        try {
            if (null != crontabSetting) {
                // 重新读取配置文件
                crontabSetting.load();
            }
            if (scheduler.isStarted()) {
                // 关闭并清除已有任务
                stop();
            }
        } finally {
            lock.unlock();
        }

        // 重新加载任务
        schedule(crontabSetting);
        // 重新启动
        scheduler.start();
    }

    /**
     * 停止
     */
    public static void stop() {
        scheduler.stop(true);
    }

}
