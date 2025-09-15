/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.logger.metric.tinylog;

import java.io.Serial;

import org.miaixz.bus.core.lang.Normal;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.core.xyz.StringKit;
import org.miaixz.bus.logger.magic.AbstractProvider;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

/**
 * tinylog
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class TinyLoggingProvider extends AbstractProvider {

    @Serial
    private static final long serialVersionUID = 2852287656777L;

    /**
     * 堆栈增加层数，因为封装因此多了两层，此值用于正确获取当前类名
     */
    private static final int DEPTH = 5;
    private static final LoggingProvider provider = ProviderRegistry.getLoggingProvider();
    /**
     * 文本消息格式化程序
     */
    private static final MessageFormatter formatter = new AdvancedMessageFormatter(Configuration.getLocale(),
            Configuration.isEscapingEnabled());
    /**
     * 日志级别
     */
    private final int level;

    /**
     * 构造
     *
     * @param clazz 日志实现类
     */
    public TinyLoggingProvider(final Class<?> clazz) {
        this(null == clazz ? Normal.NULL : clazz.getName());
    }

    /**
     * 构造
     *
     * @param name 名称
     */
    public TinyLoggingProvider(final String name) {
        this.name = name;
        this.level = provider.getMinimumLevel().ordinal();
    }

    /**
     * 如果最后一个参数为异常参数，则获取之，否则返回null
     *
     * @param args 参数
     * @return 最后一个异常参数
     */
    private static Throwable getLastArgumentIfThrowable(final Object... args) {
        if (ArrayKit.isNotEmpty(args) && args[args.length - 1] instanceof Throwable) {
            return (Throwable) args[args.length - 1];
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTraceEnabled() {
        return this.level <= Level.TRACE.ordinal();
    }

    @Override
    public void trace(final String fqcn, final Throwable t, final String format, final Object... args) {
        logIfEnabled(fqcn, Level.TRACE, t, format, args);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.level <= Level.DEBUG.ordinal();
    }

    @Override
    public void debug(final String fqcn, final Throwable t, final String format, final Object... args) {
        logIfEnabled(fqcn, Level.DEBUG, t, format, args);
    }

    @Override
    public boolean isInfoEnabled() {
        return this.level <= Level.INFO.ordinal();
    }

    @Override
    public void info(final String fqcn, final Throwable t, final String format, final Object... args) {
        logIfEnabled(fqcn, Level.INFO, t, format, args);
    }

    @Override
    public boolean isWarnEnabled() {
        return this.level <= Level.WARN.ordinal();
    }

    @Override
    public void warn(final String fqcn, final Throwable t, final String format, final Object... args) {
        logIfEnabled(fqcn, Level.WARN, t, format, args);
    }

    @Override
    public boolean isErrorEnabled() {
        return this.level <= Level.ERROR.ordinal();
    }

    @Override
    public void error(final String fqcn, final Throwable t, final String format, final Object... args) {
        logIfEnabled(fqcn, Level.ERROR, t, format, args);
    }

    @Override
    public void log(final String fqcn, final org.miaixz.bus.logger.Level level, final Throwable t, final String format,
            final Object... args) {
        logIfEnabled(fqcn, toTinyLevel(level), t, format, args);
    }

    @Override
    public boolean isEnabled(final org.miaixz.bus.logger.Level level) {
        return this.level <= toTinyLevel(level).ordinal();
    }

    /**
     * 在对应日志级别打开情况下打印日志
     *
     * @param fqcn   完全限定类名(Fully Qualified Class Name)，用于定位日志位置
     * @param level  日志级别
     * @param t      异常，null则检查最后一个参数是否为Throwable类型，是则取之，否则不打印堆栈
     * @param format 日志消息模板
     * @param args   日志消息参数
     */
    private void logIfEnabled(final String fqcn, final Level level, Throwable t, final String format,
            final Object... args) {
        if (null == t) {
            t = getLastArgumentIfThrowable(args);
        }
        provider.log(DEPTH, null, level, t, formatter, StringKit.toString(format), args);
    }

    /**
     * 将Level等级转换为Tinylog的Level等级
     *
     * @param level Level等级
     * @return the level
     */
    private Level toTinyLevel(final org.miaixz.bus.logger.Level level) {
        final Level tinyLevel;
        switch (level) {
        case TRACE:
            tinyLevel = Level.TRACE;
            break;
        case DEBUG:
            tinyLevel = Level.DEBUG;
            break;
        case INFO:
            tinyLevel = Level.INFO;
            break;
        case WARN:
            tinyLevel = Level.WARN;
            break;
        case ERROR:
            tinyLevel = Level.ERROR;
            break;
        case OFF:
            tinyLevel = Level.OFF;
            break;
        default:
            throw new Error(StringKit.format("Can not identify level: {}", level));
        }
        return tinyLevel;
    }

    @Override
    public org.miaixz.bus.logger.Level getLevel() {
        Level tinyLevel = provider.getMinimumLevel();
        if (tinyLevel == null) {
            return org.miaixz.bus.logger.Level.OFF;
        }
        return switch (tinyLevel) {
        case TRACE -> org.miaixz.bus.logger.Level.TRACE;
        case DEBUG -> org.miaixz.bus.logger.Level.DEBUG;
        case INFO -> org.miaixz.bus.logger.Level.INFO;
        case WARN -> org.miaixz.bus.logger.Level.WARN;
        case ERROR -> org.miaixz.bus.logger.Level.ERROR;
        case OFF -> org.miaixz.bus.logger.Level.OFF;
        };
    }

}