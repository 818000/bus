/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org sandao and other contributors.               *
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
package org.aoju.bus.socket.handler;

import org.aoju.bus.socket.NetMonitor;
import org.aoju.bus.socket.SocketStatus;
import org.aoju.bus.socket.TcpAioSession;

import java.nio.channels.CompletionHandler;

/**
 * 读写事件回调处理类
 *
 * @author Kimi Liu
 * @version 6.2.3
 * @since JDK 1.8+
 */
public class CompletionWriteHandler<T> implements CompletionHandler<Integer, TcpAioSession<T>> {

    @Override
    public void completed(final Integer result, final TcpAioSession<T> aioSession) {
        try {
            NetMonitor monitor = aioSession.getServerConfig().getMonitor();
            if (null != monitor) {
                monitor.afterWrite(aioSession, result);
            }
            aioSession.writeCompleted();
        } catch (Exception e) {
            failed(e, aioSession);
        }
    }


    @Override
    public void failed(Throwable exc, TcpAioSession<T> aioSession) {
        try {
            aioSession.getServerConfig().getProcessor().stateEvent(aioSession, SocketStatus.OUTPUT_EXCEPTION, exc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            aioSession.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}