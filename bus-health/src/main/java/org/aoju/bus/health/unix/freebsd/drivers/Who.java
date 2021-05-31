/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.unix.freebsd.drivers;

import com.sun.jna.Native;
import org.aoju.bus.core.annotation.ThreadSafe;
import org.aoju.bus.core.lang.Charset;
import org.aoju.bus.health.builtin.software.OSSession;
import org.aoju.bus.health.unix.CLibrary;
import org.aoju.bus.health.unix.freebsd.FreeBsdLibc;
import org.aoju.bus.health.unix.freebsd.FreeBsdLibc.FreeBsdUtmpx;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility to query logged in users.
 *
 * @author Kimi Liu
 * @version 6.2.3
 * @since JDK 1.8+
 */
@ThreadSafe
public final class Who {

    private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;

    private Who() {
    }

    /**
     * Query {@code getutxent} to get logged in users.
     *
     * @return A list of logged in user sessions
     */
    public static synchronized List<OSSession> queryUtxent() {
        List<OSSession> whoList = new ArrayList<>();
        FreeBsdUtmpx ut;
        // Rewind
        LIBC.setutxent();
        try {
            // Iterate
            while (null != (ut = LIBC.getutxent())) {
                if (ut.ut_type == CLibrary.USER_PROCESS || ut.ut_type == CLibrary.LOGIN_PROCESS) {
                    String user = Native.toString(ut.ut_user, Charset.US_ASCII);
                    String device = Native.toString(ut.ut_line, Charset.US_ASCII);
                    String host = Native.toString(ut.ut_host, Charset.US_ASCII);
                    long loginTime = ut.ut_tv.tv_sec * 1000L + ut.ut_tv.tv_usec / 1000L;
                    // Sanity check. If errors, default to who command line
                    if (user.isEmpty() || device.isEmpty() || loginTime < 0 || loginTime > System.currentTimeMillis()) {
                        return org.aoju.bus.health.unix.Who.queryWho();
                    }
                    whoList.add(new OSSession(user, device, loginTime, host));
                }
            }
        } finally {
            // Close
            LIBC.endutxent();
        }
        return whoList;
    }

}
