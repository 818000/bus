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
package org.aoju.bus.cron.factory;

import org.aoju.bus.cron.pattern.CronPattern;

/**
 * 定时作业，除了定义了作业，也定义了作业的执行周期以及ID
 *
 * @author Kimi Liu
 * @version 6.3.5
 * @since Java 17+
 */
public class CronTask implements Task {

    private final String id;
    private final Task task;
    private CronPattern pattern;

    /**
     * 构造
     *
     * @param id      ID
     * @param pattern 表达式
     * @param task    作业
     */
    public CronTask(String id, CronPattern pattern, Task task) {
        this.id = id;
        this.pattern = pattern;
        this.task = task;
    }

    @Override
    public void execute() {
        task.execute();
    }

    /**
     * 获取作业ID
     *
     * @return 作业ID
     */
    public String getId() {
        return id;
    }

    /**
     * 获取表达式
     *
     * @return 表达式
     */
    public CronPattern getPattern() {
        return pattern;
    }

    /**
     * 设置新的定时表达式
     *
     * @param pattern 表达式
     * @return this
     */
    public CronTask setPattern(CronPattern pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * 获取原始作业
     *
     * @return 作业
     */
    public Task getRaw() {
        return this.task;
    }

}
