/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
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
package org.aoju.bus.tracer.binding.apache.cxf.interceptor;

import org.aoju.bus.tracer.Backend;
import org.aoju.bus.tracer.Builder;
import org.aoju.bus.tracer.config.TraceFilterConfig;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.Phase;

/**
 * @author Kimi Liu
 * @version 6.0.2
 * @since JDK 1.8+
 */
public class TraceRequestInInterceptor extends AbstractTraceInInterceptor {

    public TraceRequestInInterceptor(Backend backend) {
        this(backend, Builder.DEFAULT);
    }

    public TraceRequestInInterceptor(Backend backend, String profile) {
        super(Phase.PRE_INVOKE, TraceFilterConfig.Channel.IncomingRequest, backend, profile);
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        if (shouldHandleMessage(message)) {
            org.aoju.bus.tracer.Builder.generateInvocationIdIfNecessary(backend);
        }
    }

    @Override
    protected boolean shouldHandleMessage(Message message) {
        return !MessageUtils.isRequestor(message);
    }

}
