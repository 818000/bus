/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org sandao and other contributors.               *
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
package org.aoju.bus.socket.plugins;

import org.aoju.bus.socket.AioSession;
import org.aoju.bus.socket.QuickAioClient;
import org.aoju.bus.socket.SocketStatus;

import java.nio.channels.AsynchronousChannelGroup;

/**
 * 断链重连插件
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
public class ReconnectPlugin<T> extends AbstractPlugin<T> {

    private final AsynchronousChannelGroup asynchronousChannelGroup;
    private final QuickAioClient<T> client;
    private boolean shutdown = false;

    public ReconnectPlugin(QuickAioClient<T> client) {
        this(client, null);
    }

    public ReconnectPlugin(QuickAioClient<T> client, AsynchronousChannelGroup asynchronousChannelGroup) {
        this.client = client;
        this.asynchronousChannelGroup = asynchronousChannelGroup;
    }

    @Override
    public void stateEvent(SocketStatus socketStatus, AioSession session, Throwable throwable) {
        if (socketStatus != SocketStatus.SESSION_CLOSED || shutdown) {
            return;
        }
        try {
            if (asynchronousChannelGroup == null) {
                client.start();
            } else {
                client.start(asynchronousChannelGroup);
            }
        } catch (Exception e) {
            shutdown = true;
            e.printStackTrace();
        }

    }

    public void shutdown() {
        shutdown = true;
    }

}
