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
package org.aoju.bus.tracer.binding.apache.httpclient;

import org.aoju.bus.tracer.Backend;
import org.aoju.bus.tracer.Builder;
import org.aoju.bus.tracer.Tracer;
import org.aoju.bus.tracer.config.TraceFilterConfig;
import org.aoju.bus.tracer.transport.HttpHeaderTransport;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.util.Map;

/**
 * @author Kimi Liu
 * @version 6.3.3
 * @since JDK 1.8+
 */
public class TraceHttpRequestInterceptor implements HttpRequestInterceptor {

    private final Backend backend;
    private final HttpHeaderTransport transportSerialization;
    private final String profile;

    public TraceHttpRequestInterceptor() {
        this(Builder.DEFAULT);
    }

    public TraceHttpRequestInterceptor(String profile) {
        this(Tracer.getBackend(), profile);
    }

    TraceHttpRequestInterceptor(Backend backend, String profile) {
        this.backend = backend;
        this.transportSerialization = new HttpHeaderTransport();
        this.profile = profile;
    }

    @Override
    public final void process(final HttpRequest httpRequest, final HttpContext httpContext) {
        final TraceFilterConfig filterConfiguration = backend.getConfiguration(profile);
        if (!backend.isEmpty() && filterConfiguration.shouldProcessContext(TraceFilterConfig.Channel.OutgoingRequest)) {
            final Map<String, String> filteredParams = filterConfiguration.filterDeniedParams(backend.copyToMap(),
                    TraceFilterConfig.Channel.OutgoingRequest);
            httpRequest.setHeader(Builder.TPIC_HEADER, transportSerialization.render(filteredParams));
        }
    }

}
