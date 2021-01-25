/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.office.process;

/**
 * 用于FreeBSD的{@link ProcessManager}实现
 *
 * @author Kimi Liu
 * @version 6.1.9
 * @since JDK 1.8+
 */
public class FreeBSDProcessManager extends UnixProcessManager {

    /**
     * 获取{@code FreeBSDProcessManager}的默认实例.
     *
     * @return 默认的{@code FreeBSDProcessManager}实例.
     */
    public static FreeBSDProcessManager getDefault() {
        return DefaultHolder.INSTANCE;
    }

    @Override
    protected String[] getRunningProcessesCommand(final String process) {
        return new String[]{
                "/bin/sh",
                "-c",
                "/bin/ps -e -o pid,args | /usr/bin/grep " + process + " | /usr/bin/grep -v grep"
        };
    }

    private static class DefaultHolder {
        static final FreeBSDProcessManager INSTANCE = new FreeBSDProcessManager();
    }

}
