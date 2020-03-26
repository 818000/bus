/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.logger.dialect.console;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.core.lang.Console;
import org.aoju.bus.core.lang.Dict;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.utils.DateUtils;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.logger.AbstractAware;
import org.aoju.bus.logger.level.Level;

/**
 * 利用System.out.println()打印日志
 *
 * @author Kimi Liu
 * @version 5.8.1
 * @since JDK 1.8+
 */
public class ConsoleLog extends AbstractAware {

    private static String logFormat = "[{date}] [{level}] {name}: {msg}";
    private static Level currentLevel = Level.DEBUG;
    private String name;

    /**
     * 构造
     *
     * @param clazz 类
     */
    public ConsoleLog(Class<?> clazz) {
        this.name = (null == clazz) ? Normal.NULL : clazz.getName();
    }

    /**
     * 构造
     *
     * @param name 类名
     */
    public ConsoleLog(String name) {
        this.name = name;
    }

    /**
     * 设置自定义的日志显示级别
     *
     * @param customLevel 自定义级别
     */
    public static void setLevel(Level customLevel) {
        Assert.notNull(customLevel);
        currentLevel = customLevel;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTrace() {
        return isEnabled(Level.TRACE);
    }

    @Override
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.TRACE, t, format, arguments);
    }

    @Override
    public boolean isDebug() {
        return isEnabled(Level.DEBUG);
    }

    @Override
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.DEBUG, t, format, arguments);
    }

    @Override
    public boolean isInfo() {
        return isEnabled(Level.INFO);
    }

    @Override
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.INFO, t, format, arguments);
    }

    @Override
    public boolean isWarn() {
        return isEnabled(Level.WARN);
    }

    @Override
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.WARN, t, format, arguments);
    }

    @Override
    public boolean isError() {
        return isEnabled(Level.ERROR);
    }

    @Override
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        log(fqcn, Level.ERROR, t, format, arguments);
    }

    @Override
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        if (false == isEnabled(level)) {
            return;
        }

        final Dict dict = Dict.create()
                .set("date", DateUtils.now())
                .set("level", level.toString())
                .set("name", this.name)
                .set("msg", StringUtils.format(format, arguments));

        final String logMsg = StringUtils.format(logFormat, dict);

        if (level.ordinal() >= Level.WARN.ordinal()) {
            Console.error(t, logMsg);
        } else {
            Console.log(t, logMsg);
        }
    }

    @Override
    public boolean isEnabled(Level level) {
        return currentLevel.compareTo(level) <= 0;
    }

}
