/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.http.cache;

import org.aoju.bus.core.io.Sink;
import org.aoju.bus.core.utils.IoUtils;
import org.aoju.bus.http.Request;
import org.aoju.bus.http.Response;

import java.io.IOException;
import java.io.OutputStream;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 适配 {@link ResponseCache} 到 {@link InternalCache}.
 *
 * @author Kimi Liu
 * @version 5.9.0
 * @since JDK 1.8+
 */
public final class CacheAdapter implements InternalCache {

    private final ResponseCache delegate;

    public CacheAdapter(ResponseCache delegate) {
        this.delegate = delegate;
    }

    public ResponseCache getDelegate() {
        return delegate;
    }

    @Override
    public Response get(Request request) throws IOException {
        CacheResponse javaResponse = getJavaCachedResponse(request);
        if (javaResponse == null) {
            return null;
        }
        return NetApiConvert.createResponseForCacheGet(request, javaResponse);
    }

    @Override
    public CacheRequest put(Response response) throws IOException {
        URI uri = response.request().url().uri();
        HttpURLConnection connection = NetApiConvert.createJavaUrlConnectionForCachePut(response);
        final java.net.CacheRequest request = delegate.put(uri, connection);
        if (request == null) {
            return null;
        }
        return new CacheRequest() {
            @Override
            public Sink body() throws IOException {
                OutputStream body = request.getBody();
                return body != null ? IoUtils.sink(body) : null;
            }

            @Override
            public void abort() {
                request.abort();
            }
        };
    }

    @Override
    public void remove(Request request) {

    }

    @Override
    public void update(Response cached, Response network) {
    }

    @Override
    public void trackConditionalCacheHit() {

    }

    @Override
    public void trackResponse(CacheStrategy cacheStrategy) {

    }

    private CacheResponse getJavaCachedResponse(Request request) throws IOException {
        Map<String, List<String>> headers = NetApiConvert.extractJavaHeaders(request);
        return delegate.get(request.url().uri(), request.method(), headers);
    }

}