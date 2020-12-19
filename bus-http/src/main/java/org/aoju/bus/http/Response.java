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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.http;

import org.aoju.bus.core.io.Buffer;
import org.aoju.bus.core.io.BufferSource;
import org.aoju.bus.core.lang.Header;
import org.aoju.bus.core.lang.Http;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.http.bodys.ResponseBody;
import org.aoju.bus.http.cache.CacheControl;
import org.aoju.bus.http.metric.Handshake;
import org.aoju.bus.http.metric.http.HttpHeaders;
import org.aoju.bus.http.secure.Challenge;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * HTTP响应。该类的实例不是不可变的:
 * 响应体是一次性的值，可能只使用一次，然后关闭。所有其他属性都是不可变的.
 *
 * @author Kimi Liu
 * @version 6.1.6
 * @since JDK 1.8+
 */
public final class Response implements Closeable {

    final Request request;
    final Protocol protocol;
    final int code;
    final String message;
    final Handshake handshake;
    final Headers headers;
    final ResponseBody body;
    final Response networkResponse;
    final Response cacheResponse;
    final Response priorResponse;
    final long sentRequestAtMillis;
    final long receivedResponseAtMillis;

    private volatile CacheControl cacheControl;

    Response(Builder builder) {
        this.request = builder.request;
        this.protocol = builder.protocol;
        this.code = builder.code;
        this.message = builder.message;
        this.handshake = builder.handshake;
        this.headers = builder.headers.build();
        this.body = builder.body;
        this.networkResponse = builder.networkResponse;
        this.cacheResponse = builder.cacheResponse;
        this.priorResponse = builder.priorResponse;
        this.sentRequestAtMillis = builder.sentRequestAtMillis;
        this.receivedResponseAtMillis = builder.receivedResponseAtMillis;
    }

    public Request request() {
        return request;
    }

    public Protocol protocol() {
        return protocol;
    }

    public int code() {
        return code;
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }

    public String message() {
        return message;
    }

    public Handshake handshake() {
        return handshake;
    }

    public List<String> headers(String name) {
        return headers.values(name);
    }

    public String header(String name) {
        return header(name, null);
    }

    public String header(String name, String defaultValue) {
        String result = headers.get(name);
        return result != null ? result : defaultValue;
    }

    public Headers headers() {
        return headers;
    }

    public ResponseBody peekBody(long byteCount) throws IOException {
        BufferSource source = body.source();
        source.request(byteCount);
        Buffer copy = source.buffer().clone();

        Buffer result;
        if (copy.size() > byteCount) {
            result = new Buffer();
            result.write(copy, byteCount);
            copy.clear();
        } else {
            result = copy;
        }

        return ResponseBody.create(body.contentType(), result.size(), result);
    }

    public ResponseBody body() {
        return body;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    /**
     * 如果此响应重定向到另一个资源，则返回true
     *
     * @return the true/false
     */
    public boolean isRedirect() {
        switch (code) {
            case Http.HTTP_PERM_REDIRECT:
            case Http.HTTP_TEMP_REDIRECT:
            case Http.HTTP_MULT_CHOICE:
            case Http.HTTP_MOVED_PERM:
            case Http.HTTP_MOVED_TEMP:
            case Http.HTTP_SEE_OTHER:
                return true;
            default:
                return false;
        }
    }

    public Response networkResponse() {
        return networkResponse;
    }

    public Response cacheResponse() {
        return cacheResponse;
    }

    public Response priorResponse() {
        return priorResponse;
    }

    public List<Challenge> challenges() {
        String responseField;
        if (code == Http.HTTP_UNAUTHORIZED) {
            responseField = Header.WWW_AUTHENTICATE;
        } else if (code == Http.HTTP_PROXY_AUTH) {
            responseField = Header.PROXY_AUTHENTICATE;
        } else {
            return Collections.emptyList();
        }
        return HttpHeaders.parseChallenges(headers(), responseField);
    }

    public CacheControl cacheControl() {
        CacheControl result = cacheControl;
        return result != null ? result : (cacheControl = CacheControl.parse(headers));
    }

    public long sentRequestAtMillis() {
        return sentRequestAtMillis;
    }

    public long receivedResponseAtMillis() {
        return receivedResponseAtMillis;
    }

    @Override
    public void close() {
        if (body == null) {
            throw new IllegalStateException("response is not eligible for a body and must not be closed");
        }
        body.close();
    }

    @Override
    public String toString() {
        return "Response{protocol="
                + protocol
                + ", code="
                + code
                + ", message="
                + message
                + ", url="
                + request.url()
                + Symbol.C_BRACE_RIGHT;
    }

    public static class Builder {
        Request request;
        Protocol protocol;
        int code = -1;
        String message;
        Handshake handshake;
        Headers.Builder headers;
        ResponseBody body;
        Response networkResponse;
        Response cacheResponse;
        Response priorResponse;
        long sentRequestAtMillis;
        long receivedResponseAtMillis;

        public Builder() {
            headers = new Headers.Builder();
        }

        Builder(Response response) {
            this.request = response.request;
            this.protocol = response.protocol;
            this.code = response.code;
            this.message = response.message;
            this.handshake = response.handshake;
            this.headers = response.headers.newBuilder();
            this.body = response.body;
            this.networkResponse = response.networkResponse;
            this.cacheResponse = response.cacheResponse;
            this.priorResponse = response.priorResponse;
            this.sentRequestAtMillis = response.sentRequestAtMillis;
            this.receivedResponseAtMillis = response.receivedResponseAtMillis;
        }

        public Builder request(Request request) {
            this.request = request;
            return this;
        }

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder code(int code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder handshake(Handshake handshake) {
            this.handshake = handshake;
            return this;
        }

        public Builder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        public Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        public Builder body(ResponseBody body) {
            this.body = body;
            return this;
        }

        public Builder networkResponse(Response networkResponse) {
            if (networkResponse != null) checkSupportResponse("networkResponse", networkResponse);
            this.networkResponse = networkResponse;
            return this;
        }

        public Builder cacheResponse(Response cacheResponse) {
            if (cacheResponse != null) checkSupportResponse("cacheResponse", cacheResponse);
            this.cacheResponse = cacheResponse;
            return this;
        }

        private void checkSupportResponse(String name, Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException(name + ".body != null");
            } else if (response.networkResponse != null) {
                throw new IllegalArgumentException(name + ".networkResponse != null");
            } else if (response.cacheResponse != null) {
                throw new IllegalArgumentException(name + ".cacheResponse != null");
            } else if (response.priorResponse != null) {
                throw new IllegalArgumentException(name + ".priorResponse != null");
            }
        }

        public Builder priorResponse(Response priorResponse) {
            if (priorResponse != null) checkPriorResponse(priorResponse);
            this.priorResponse = priorResponse;
            return this;
        }

        private void checkPriorResponse(Response response) {
            if (response.body != null) {
                throw new IllegalArgumentException("priorResponse.body != null");
            }
        }

        public Builder sentRequestAtMillis(long sentRequestAtMillis) {
            this.sentRequestAtMillis = sentRequestAtMillis;
            return this;
        }

        public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
            this.receivedResponseAtMillis = receivedResponseAtMillis;
            return this;
        }

        public Response build() {
            if (request == null) throw new IllegalStateException("request == null");
            if (protocol == null) throw new IllegalStateException("protocol == null");
            if (code < 0) throw new IllegalStateException("code < 0: " + code);
            if (message == null) throw new IllegalStateException("message == null");
            return new Response(this);
        }
    }

}
