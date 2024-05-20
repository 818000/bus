/**
 * 定时任务模块，提供类Crontab表达式的定时任务，实现参考了Cron4j，同时可以支持秒级别的定时任务定义和年的定义（同时兼容Crontab、Cron4j、Quartz表达式）
 * 定时任务模块由三部分组成：
 * <ul>
 *     <li>{@link org.miaixz.bus.cron.Scheduler} 定时任务调度器，用于整体管理任务的增删、启停和触发运行。</li>
 *     <li>{@link org.miaixz.bus.cron.crontab.Crontab} 定时任务实现，用于定义具体的任务</li>
 *     <li>{@link org.miaixz.bus.cron.pattern.CronPattern} 定时任务表达式，用于定义任务触发时间</li>
 * </ul>
 * <p>
 * 同时，提供了{@link org.miaixz.bus.cron.Builder}工具类，维护一个全局的{@link org.miaixz.bus.cron.Scheduler}。
 *
 * @author Kimi Liu
 * @since Java 17+
 */
package org.miaixz.bus.cron;
