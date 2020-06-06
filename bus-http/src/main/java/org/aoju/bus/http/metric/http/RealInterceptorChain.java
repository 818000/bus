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
package org.aoju.bus.http.metric.http;

import org.aoju.bus.http.Builder;
import org.aoju.bus.http.NewCall;
import org.aoju.bus.http.Request;
import org.aoju.bus.http.Response;
import org.aoju.bus.http.accord.Connection;
import org.aoju.bus.http.accord.RealConnection;
import org.aoju.bus.http.accord.StreamAllocation;
import org.aoju.bus.http.metric.EventListener;
import org.aoju.bus.http.metric.Interceptor;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 承载整个拦截器链的具体拦截器链:
 * 所有应用程序拦截器、Httpd核心、所有网络拦截器，最后是网络调用者.
 *
 * @author Kimi Liu
 * @version 5.9.8
 * @since JDK 1.8+
 */
public final class RealInterceptorChain implements Interceptor.Chain {

    private final List<Interceptor> interceptors;
    private final StreamAllocation streamAllocation;
    private final HttpCodec httpCodec;
    private final RealConnection connection;
    private final int index;
    private final Request request;
    private final NewCall call;
    private final EventListener eventListener;
    private final int connectTimeout;
    private final int readTimeout;
    private final int writeTimeout;
    private int calls;

    public RealInterceptorChain(List<Interceptor> interceptors, StreamAllocation streamAllocation,
                                HttpCodec httpCodec, RealConnection connection, int index, Request request, NewCall call,
                                EventListener eventListener, int connectTimeout, int readTimeout, int writeTimeout) {
        this.interceptors = interceptors;
        this.connection = connection;
        this.streamAllocation = streamAllocation;
        this.httpCodec = httpCodec;
        this.index = index;
        this.request = request;
        this.call = call;
        this.eventListener = eventListener;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }

    @Override
    public Connection connection() {
        return connection;
    }

    @Override
    public int connectTimeoutMillis() {
        return connectTimeout;
    }

    @Override
    public Interceptor.Chain withConnectTimeout(int timeout, TimeUnit unit) {
        int millis = Builder.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, millis, readTimeout, writeTimeout);
    }

    @Override
    public int readTimeoutMillis() {
        return readTimeout;
    }

    @Override
    public Interceptor.Chain withReadTimeout(int timeout, TimeUnit unit) {
        int millis = Builder.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, connectTimeout, millis, writeTimeout);
    }

    @Override
    public int writeTimeoutMillis() {
        return writeTimeout;
    }

    @Override
    public Interceptor.Chain withWriteTimeout(int timeout, TimeUnit unit) {
        int millis = Builder.checkDuration("timeout", timeout, unit);
        return new RealInterceptorChain(interceptors, streamAllocation, httpCodec, connection, index,
                request, call, eventListener, connectTimeout, readTimeout, millis);
    }

    public StreamAllocation streamAllocation() {
        return streamAllocation;
    }

    public HttpCodec httpStream() {
        return httpCodec;
    }

    @Override
    public NewCall call() {
        return call;
    }

    public EventListener eventListener() {
        return eventListener;
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response proceed(Request request) throws IOException {
        return proceed(request, streamAllocation, httpCodec, connection);
    }

    public Response proceed(Request request, StreamAllocation streamAllocation, HttpCodec httpCodec,
                            RealConnection connection) throws IOException {
        if (index >= interceptors.size()) throw new AssertionError();

        calls++;

        if (this.httpCodec != null && !this.connection.supportsUrl(request.url())) {
            throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
                    + " must retain the same host and port");
        }

        if (this.httpCodec != null && calls > 1) {
            throw new IllegalStateException("network interceptor " + interceptors.get(index - 1)
                    + " must call proceed() exactly once");
        }

        RealInterceptorChain next = new RealInterceptorChain(interceptors, streamAllocation, httpCodec,
                connection, index + 1, request, call, eventListener, connectTimeout, readTimeout,
                writeTimeout);
        Interceptor interceptor = interceptors.get(index);
        Response response = interceptor.intercept(next);

        if (httpCodec != null && index + 1 < interceptors.size() && next.calls != 1) {
            throw new IllegalStateException("network interceptor " + interceptor
                    + " must call proceed() exactly once");
        }

        if (response == null) {
            throw new NullPointerException("interceptor " + interceptor + " returned null");
        }

        if (response.body() == null) {
            throw new IllegalStateException(
                    "interceptor " + interceptor + " returned a response with no body");
        }

        return response;
    }

}
