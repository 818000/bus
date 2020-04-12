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
package org.aoju.bus.logger.dialect.commons;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.logger.AbstractAware;
import org.aoju.bus.logger.level.Level;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Apache Commons Logging
 *
 * @author Kimi Liu
 * @version 5.8.3
 * @since JDK 1.8+
 */
public class ApacheCommonsLog extends AbstractAware {

    private final transient Log logger;
    private final String name;

    public ApacheCommonsLog(Log logger, String name) {
        this.logger = logger;
        this.name = name;
    }

    public ApacheCommonsLog(Class<?> clazz) {
        this(LogFactory.getLog(clazz), null == clazz ? Normal.NULL : clazz.getName());
    }

    public ApacheCommonsLog(String name) {
        this(LogFactory.getLog(name), name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTrace() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(String fqcn, Throwable t, String format, Object... arguments) {
        if (isTrace()) {
            logger.trace(StringUtils.format(format, arguments), t);
        }
    }

    @Override
    public boolean isDebug() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(String fqcn, Throwable t, String format, Object... arguments) {
        if (isDebug()) {
            logger.debug(StringUtils.format(format, arguments), t);
        }
    }

    @Override
    public boolean isInfo() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(String fqcn, Throwable t, String format, Object... arguments) {
        if (isInfo()) {
            logger.info(StringUtils.format(format, arguments), t);
        }
    }

    @Override
    public boolean isWarn() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarn()) {
            logger.warn(StringUtils.format(format, arguments));
        }
    }

    @Override
    public void warn(Throwable t, String format, Object... arguments) {
    }

    @Override
    public void warn(String fqcn, Throwable t, String format, Object... arguments) {
        if (isWarn()) {
            logger.warn(StringUtils.format(format, arguments), t);
        }
    }

    @Override
    public boolean isError() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(String fqcn, Throwable t, String format, Object... arguments) {
        if (isError()) {
            logger.warn(StringUtils.format(format, arguments), t);
        }
    }

    @Override
    public void log(String fqcn, Level level, Throwable t, String format, Object... arguments) {
        switch (level) {
            case TRACE:
                trace(t, format, arguments);
                break;
            case DEBUG:
                debug(t, format, arguments);
                break;
            case INFO:
                info(t, format, arguments);
                break;
            case WARN:
                warn(t, format, arguments);
                break;
            case ERROR:
                error(t, format, arguments);
                break;
            default:
                throw new Error(StringUtils.format("Can not identify level: {}", level));
        }
    }

}
