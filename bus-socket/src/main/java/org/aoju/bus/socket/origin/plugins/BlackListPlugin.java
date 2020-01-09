/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.socket.origin.plugins;


import org.aoju.bus.logger.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 黑名单插件,会拒绝与黑名单中的IP建立连接
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public final class BlackListPlugin<T> extends AbstractPlugin<T> {

    private ConcurrentLinkedQueue<BlackListRule> ipBlackList = new ConcurrentLinkedQueue<>();

    @Override
    public boolean acceptMonitor(AsynchronousSocketChannel channel) {
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = (InetSocketAddress) channel.getRemoteAddress();
        } catch (IOException e) {
            Logger.error("get remote address error.", e);
        }
        if (inetSocketAddress == null) {
            return true;
        }
        for (BlackListRule rule : ipBlackList) {
            if (!rule.access(inetSocketAddress)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 添加黑名单失败规则
     *
     * @param rule 规则
     */
    public void addRule(BlackListRule rule) {
        ipBlackList.add(rule);
    }

    /**
     * 移除黑名单规则
     *
     * @param rule 规则
     */
    public void removeRule(BlackListRule rule) {
        ipBlackList.remove(rule);
    }

    /**
     * 黑名单规则定义
     */
    public interface BlackListRule {
        boolean access(InetSocketAddress address);
    }

}
