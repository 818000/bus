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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.cron;

import org.aoju.bus.core.key.UUID;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.thread.ExecutorBuilder;
import org.aoju.bus.core.thread.ThreadBuilder;
import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.cron.factory.InvokeTask;
import org.aoju.bus.cron.factory.RunnableTask;
import org.aoju.bus.cron.factory.Task;
import org.aoju.bus.cron.listener.TaskListener;
import org.aoju.bus.cron.listener.TaskListenerManager;
import org.aoju.bus.cron.pattern.CronPattern;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.setting.magic.PopSetting;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 任务调度器
 * <p>
 * 调度器启动流程：
 *
 * <pre>
 * 启动Timer -  启动Launcher -  启动Executor
 * </pre>
 * <p>
 * 调度器关闭流程:
 *
 * <pre>
 * 关闭Timer -  关闭所有运行中的Launcher -  关闭所有运行中的Executor
 * </pre>
 * <p>
 * 其中：
 *
 * <pre>
 * <strong>Launcher</strong>：定时器每分钟调用一次(如果{@link Scheduler#isMatchSecond()}为<code>true</code>每秒调用一次),
 * 负责检查<strong>Repertoire</strong>是否有匹配到此时间运行的Task
 * </pre>
 *
 * <pre>
 * <strong>Executor</strong>：Launcher匹配成功后,触发Executor执行具体的作业,执行完毕销毁
 * </pre>
 *
 * @author Kimi Liu
 * @version 6.1.5
 * @since JDK 1.8+
 */
public class Scheduler {

    /**
     * 同步锁
     */
    private final Lock lock = new ReentrantLock();
    /**
     * 是否为守护线程
     */
    protected boolean daemon;
    /**
     * 启动管理器
     */
    protected Supervisor supervisor;
    /**
     * 执行管理器
     */
    protected Manager manager;
    /**
     * 线程池
     */
    protected ExecutorService threadExecutor;
    /**
     * 定时任务配置
     */
    protected Configure config = new Configure();
    /**
     * 定时任务表
     */
    protected Repertoire repertoire = new Repertoire();
    /**
     * 监听管理器列表
     */
    protected TaskListenerManager listenerManager = new TaskListenerManager();
    /**
     * 是否已经启动
     */
    private boolean started = false;
    /**
     * 定时器
     */
    private CronTimer timer;

    /**
     * 设置时区
     *
     * @param timeZone 时区
     * @return this
     */
    public Scheduler setTimeZone(TimeZone timeZone) {
        this.config.setTimeZone(timeZone);
        return this;
    }

    /**
     * 是否为守护线程
     *
     * @return 是否为守护线程
     */
    public boolean isDaemon() {
        return this.daemon;
    }

    /**
     * 设置是否为守护线程
     * 如果为true,则在调用{@link #stop()}方法后执行的定时任务立即结束,否则等待执行完毕才结束 默认非守护线程
     *
     * @param on <code>true</code>为守护线程,否则非守护线程
     * @return this
     * @throws InstrumentException 定时任务已经启动抛出此异常
     */
    public Scheduler setDaemon(boolean on) throws InstrumentException {
        lock.lock();
        try {
            if (this.started) {
                throw new InstrumentException("Scheduler already started!");
            }
            this.daemon = on;
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 是否支持秒匹配
     *
     * @return <code>true</code>使用,<code>false</code>不使用
     */
    public boolean isMatchSecond() {
        return this.config.isMatchSecond();
    }

    /**
     * 设置是否支持秒匹配,默认不使用
     *
     * @param isMatchSecond <code>true</code>支持,<code>false</code>不支持
     * @return this
     */
    public Scheduler setMatchSecond(boolean isMatchSecond) {
        this.config.setMatchSecond(isMatchSecond);
        return this;
    }

    /**
     * 增加监听器
     *
     * @param listener {@link TaskListener}
     * @return this
     */
    public Scheduler addListener(TaskListener listener) {
        this.listenerManager.addListener(listener);
        return this;
    }

    /**
     * 移除监听器
     *
     * @param listener {@link TaskListener}
     * @return this
     */
    public Scheduler removeListener(TaskListener listener) {
        this.listenerManager.removeListener(listener);
        return this;
    }

    /**
     * 批量加入配置文件中的定时任务
     * 配置文件格式为： xxx.xxx.xxx.Class.method = * * * * *
     *
     * @param cronSetting 定时任务设置文件
     * @return this
     */
    public Scheduler schedule(PopSetting cronSetting) {
        if (CollKit.isNotEmpty(cronSetting)) {
            String group;
            for (Entry<String, LinkedHashMap<String, String>> groupedEntry : cronSetting.getGroupMap().entrySet()) {
                group = groupedEntry.getKey();
                for (Entry<String, String> entry : groupedEntry.getValue().entrySet()) {
                    String jobClass = entry.getKey();
                    if (StringKit.isNotBlank(group)) {
                        jobClass = group + Symbol.DOT + jobClass;
                    }
                    final String pattern = entry.getValue();
                    Logger.debug("Load job: {} {}", pattern, jobClass);
                    try {
                        schedule(pattern, new InvokeTask(jobClass));
                    } catch (Exception e) {
                        throw new InstrumentException("Schedule [{}] [{}] error!", pattern, jobClass);
                    }
                }
            }
        }
        return this;
    }

    /**
     * 新增Task,使用随机UUID
     *
     * @param pattern {@link CronPattern}对应的String表达式
     * @param task    {@link Runnable}
     * @return ID
     */
    public String schedule(String pattern, Runnable task) {
        return schedule(pattern, new RunnableTask(task));
    }

    /**
     * 新增Task,使用随机UUID
     *
     * @param pattern {@link CronPattern}对应的String表达式
     * @param task    {@link Task}
     * @return ID
     */
    public String schedule(String pattern, Task task) {
        String id = UUID.randomUUID().toString();
        schedule(id, pattern, task);
        return id;
    }

    /**
     * 新增Task
     *
     * @param id      ID,为每一个Task定义一个ID
     * @param pattern {@link CronPattern}对应的String表达式
     * @param task    {@link Runnable}
     * @return this
     */
    public Scheduler schedule(String id, String pattern, Runnable task) {
        return schedule(id, new CronPattern(pattern), new RunnableTask(task));
    }

    /**
     * 新增Task
     *
     * @param id      ID,为每一个Task定义一个ID
     * @param pattern {@link CronPattern}对应的String表达式
     * @param task    {@link Task}
     * @return this
     */
    public Scheduler schedule(String id, String pattern, Task task) {
        return schedule(id, new CronPattern(pattern), task);
    }

    /**
     * 新增Task
     *
     * @param id      ID,为每一个Task定义一个ID
     * @param pattern {@link CronPattern}
     * @param task    {@link Task}
     * @return this
     */
    public Scheduler schedule(String id, CronPattern pattern, Task task) {
        repertoire.add(id, pattern, task);
        return this;
    }

    /**
     * 移除Task
     *
     * @param id Task的ID
     * @return this
     */
    public Scheduler deschedule(String id) {
        this.repertoire.remove(id);
        return this;
    }

    /**
     * 更新Task执行的时间规则
     *
     * @param id      Task的ID
     * @param pattern {@link CronPattern}
     * @return this
     */
    public Scheduler updatePattern(String id, CronPattern pattern) {
        this.repertoire.updatePattern(id, pattern);
        return this;
    }

    /**
     * 获取定时任务表，注意此方法返回非复制对象，对返回对象的修改将影响已有定时任务
     *
     * @return 定时任务表 {@link Repertoire}
     */
    public Repertoire getTaskTable() {
        return this.repertoire;
    }

    /**
     * 获得指定id的{@link CronPattern}
     *
     * @param id ID
     * @return {@link CronPattern}
     */
    public CronPattern getPattern(String id) {
        return this.repertoire.getPattern(id);
    }

    /**
     * 获得指定id的{@link Task}
     *
     * @param id ID
     * @return {@link Task}
     */
    public Task getTask(String id) {
        return this.repertoire.getTask(id);
    }

    /**
     * 是否无任务
     *
     * @return true表示无任务
     */
    public boolean isEmpty() {
        return this.repertoire.isEmpty();
    }

    /**
     * 当前任务数
     *
     * @return 当前任务数
     */
    public int size() {
        return this.repertoire.size();
    }

    /**
     * 清空任务表
     *
     * @return this
     */
    public Scheduler clear() {
        this.repertoire = new Repertoire();
        return this;
    }

    /**
     * @return 是否已经启动
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * 启动
     *
     * @param isDeamon 是否以守护线程方式启动,如果为true,则在调用{@link #stop()}方法后执行的定时任务立即结束,否则等待执行完毕才结束
     * @return this
     */
    public Scheduler start(boolean isDeamon) {
        this.daemon = isDeamon;
        return start();
    }

    /**
     * 启动
     *
     * @return this
     */
    public Scheduler start() {
        lock.lock();
        try {
            if (this.started) {
                throw new InstrumentException("Schedule is started!");
            }

            this.threadExecutor = ExecutorBuilder.create().useSynchronousQueue().setThreadFactory(//
                    ThreadBuilder.create().setNamePrefix("exec-cron-").setDaemon(this.daemon).build()//
            ).build();
            this.supervisor = new Supervisor(this);
            this.manager = new Manager(this);

            // Start CronTimer
            timer = new CronTimer(this);
            timer.setDaemon(this.daemon);
            timer.start();
            this.started = true;
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 停止定时任务
     * 此方法调用后会将定时器进程立即结束,如果为守护线程模式,则正在执行的作业也会自动结束,否则作业线程将在执行完成后结束
     * 此方法并不会清除任务表中的任务,请调用{@link #clear()} 方法清空任务或者使用{@link #stop(boolean)}方法可选是否清空
     *
     * @return this
     */
    public Scheduler stop() {
        return stop(false);
    }

    /**
     * 停止定时任务
     * 此方法调用后会将定时器进程立即结束,如果为守护线程模式,则正在执行的作业也会自动结束,否则作业线程将在执行完成后结束
     *
     * @param clearTasks 标记
     * @return this
     */
    public Scheduler stop(boolean clearTasks) {
        lock.lock();
        try {
            if (false == started) {
                throw new IllegalStateException("Scheduler not started !");
            }

            // 停止CronTimer
            this.timer.stopTimer();
            this.timer = null;

            //停止线程池
            this.threadExecutor.shutdown();
            this.threadExecutor = null;

            //可选是否清空任务表
            if (clearTasks) {
                clear();
            }

            // 修改标志
            started = false;
        } finally {
            lock.unlock();
        }
        return this;
    }

}
