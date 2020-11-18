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
package org.aoju.bus.http;

import org.aoju.bus.core.lang.*;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.ArrayKit;
import org.aoju.bus.core.toolkit.MapKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.http.accord.ConnectionPool;
import org.aoju.bus.http.bodys.FormBody;
import org.aoju.bus.http.bodys.MultipartBody;
import org.aoju.bus.http.bodys.RequestBody;
import org.aoju.bus.http.magic.HttpProxy;
import org.aoju.bus.http.metric.Dispatcher;
import org.aoju.bus.http.secure.X509TrustManager;
import org.aoju.bus.logger.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 发送HTTP请求辅助类
 *
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
public class Httpx {

    /**
     * 懒汉安全加同步
     * 私有的静态成员变量 只声明不创建
     * 私有的构造方法
     * 提供返回实例的静态方法
     */
    private static Httpd httpd;

    static {
        new Httpx(new X509TrustManager());
    }

    /**
     * 提供返回实例的静态方法
     */
    public Httpx() {
        this(30, 30, 30);
    }

    /**
     * 提供返回实例的静态方法
     *
     * @param x509TrustManager 信任管理器
     */
    public Httpx(X509TrustManager x509TrustManager) {
        this(null, null, 30, 30, 30, 64, 5, 5, 5, org.aoju.bus.http.secure.SSLSocketFactory.createTrustAllSSLFactory(x509TrustManager), x509TrustManager, (hostname, session) -> true);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     *
     * @param connTimeout  连接
     * @param readTimeout  读取
     * @param writeTimeout 输出
     */
    public Httpx(int connTimeout,
                 int readTimeout,
                 int writeTimeout) {
        this(null, null, connTimeout, readTimeout, writeTimeout, 64, 5, 5, 5);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     *
     * @param connTimeout        连接
     * @param readTimeout        读取
     * @param writeTimeout       输出
     * @param maxRequests        最大请求
     * @param maxRequestsPerHost 主机最大请求
     * @param maxIdleConnections 最大连接
     * @param keepAliveDuration  链接时长
     */
    public Httpx(int connTimeout,
                 int readTimeout,
                 int writeTimeout,
                 int maxRequests,
                 int maxRequestsPerHost,
                 int maxIdleConnections,
                 int keepAliveDuration) {
        this(null, null, connTimeout, readTimeout, writeTimeout, maxRequests, maxRequestsPerHost, maxIdleConnections, keepAliveDuration);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     *
     * @param dns                DNS 信息
     * @param httpProxy          代理信息
     * @param connTimeout        连接
     * @param readTimeout        读取
     * @param writeTimeout       输出
     * @param maxRequests        最大请求
     * @param maxRequestsPerHost 主机最大请求
     * @param maxIdleConnections 最大连接
     * @param keepAliveDuration  链接时长
     */
    public Httpx(DnsX dns,
                 HttpProxy httpProxy,
                 int connTimeout,
                 int readTimeout,
                 int writeTimeout,
                 int maxRequests,
                 int maxRequestsPerHost,
                 int maxIdleConnections,
                 int keepAliveDuration
    ) {
        this(dns, httpProxy, connTimeout, readTimeout, writeTimeout, maxRequests, maxRequestsPerHost, maxIdleConnections, keepAliveDuration, null, null, null);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     *
     * @param dns                DNS 信息
     * @param httpProxy          代理信息
     * @param connTimeout        连接
     * @param readTimeout        读取
     * @param writeTimeout       输出
     * @param maxRequests        最大请求
     * @param maxRequestsPerHost 主机最大请求
     * @param maxIdleConnections 最大连接
     * @param keepAliveDuration  链接时长
     * @param sslSocketFactory   抽象类,扩展自SocketFactory, SSLSocket的工厂
     * @param x509TrustManager   证书信任管理器
     * @param hostnameVerifier   主机名校验信息
     */
    public Httpx(final DnsX dns,
                 final HttpProxy httpProxy,
                 int connTimeout,
                 int readTimeout,
                 int writeTimeout,
                 int maxRequests,
                 int maxRequestsPerHost,
                 int maxIdleConnections,
                 int keepAliveDuration,
                 SSLSocketFactory sslSocketFactory,
                 javax.net.ssl.X509TrustManager x509TrustManager,
                 HostnameVerifier hostnameVerifier
    ) {
        synchronized (Httpx.class) {
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(maxRequests);
            dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
            ConnectionPool connectPool = new ConnectionPool(maxIdleConnections,
                    keepAliveDuration, TimeUnit.MINUTES);
            Httpd.Builder builder = new Httpd.Builder();

            builder.dispatcher(dispatcher);
            builder.connectionPool(connectPool);
            builder.addNetworkInterceptor(chain -> {
                Request request = chain.request();
                return chain.proceed(request);
            });
            if (ObjectKit.isNotEmpty(dns)) {
                builder.dns(hostname -> {
                    try {
                        return dns.lookup(hostname);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return DnsX.SYSTEM.lookup(hostname);
                });
            }
            if (ObjectKit.isNotEmpty(httpProxy)) {
                builder.proxy(httpProxy.proxy());
                if (httpProxy.user != null && httpProxy.password != null) {
                    builder.proxyAuthenticator(httpProxy.authenticator());
                }
            }
            builder.connectTimeout(connTimeout, TimeUnit.SECONDS);
            builder.readTimeout(readTimeout, TimeUnit.SECONDS);
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
            if (ObjectKit.isNotEmpty(sslSocketFactory)) {
                builder.sslSocketFactory(sslSocketFactory, x509TrustManager);
            }
            if (ObjectKit.isNotEmpty(hostnameVerifier)) {
                builder.hostnameVerifier(hostnameVerifier);
            }
            httpd = builder.build();
        }
    }

    /**
     * 简单的 GET 请求 使用默认编码 UTF-8
     *
     * @param url URL地址 String
     * @return String
     */
    public static String get(final String url) {
        return get(url, Charset.DEFAULT_UTF_8);
    }

    /**
     * 简单的 GET 请求 使用自定义编码
     *
     * @param url     URL地址 String
     * @param charset 自定义编码 String
     * @return String
     */
    public static String get(final String url, final String charset) {
        return execute(Builder.builder().url(url).requestCharset(charset).responseCharset(charset).build());
    }

    /**
     * 异步get请求,回调
     *
     * @param url     URL地址
     * @param isAsync 是否异步
     * @return String
     */
    public static String get(final String url, final boolean isAsync) {
        if (isAsync) {
            return enqueue(Builder.builder().url(url).method(Http.GET).build());
        }
        return get(url);
    }

    /**
     * 带查询参数 GET 请求 使用默认编码 UTF-8
     *
     * @param url      URL地址 String
     * @param queryMap 查询参数 Map
     * @return String
     */
    public static String get(final String url, final Map<String, Object> queryMap) {
        return get(url, queryMap, null, Charset.DEFAULT_UTF_8);
    }

    /**
     * 带查询参数 GET 请求 使用默认编码 UTF-8
     *
     * @param url       URL地址 String
     * @param queryMap  查询参数 Map
     * @param headerMap Header参数 Map
     * @return String
     */
    public static String get(final String url, final Map<String, Object> queryMap, Map<String, String> headerMap) {
        return get(url, queryMap, headerMap, Charset.DEFAULT_UTF_8);
    }

    /**
     * 带查询参数 GET 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param queryMap  查询参数 Map
     * @param headerMap Header参数 Map
     * @param charset   自定义编码 String
     * @return String
     */
    public static String get(final String url, final Map<String, Object> queryMap, Map<String, String> headerMap,
                             final String charset) {
        return execute(Builder.builder().url(url).headerMap(headerMap).queryMap(queryMap)
                .requestCharset(charset).responseCharset(charset).build());
    }

    /**
     * 异步处理的GET请求,自定义请求类型
     *
     * @param url      URL地址 String
     * @param callback 回调信息 callback
     */
    public static void get(String url, Callback callback) {
        Request request = new Request.Builder().url(url).get().build();
        NewCall call = httpd.newCall(request);
        call.enqueue(callback);
    }

    /**
     * 异步处理的POST请求,自定义请求类型
     *
     * @param url      URL地址 String
     * @param queryMap 查询参数 Map
     * @param callback 回调信息 callback
     */
    public static void post(String url, Map<String, Object> queryMap, Callback callback) {
        StringBuilder data = new StringBuilder();
        if (ObjectKit.isNotEmpty(queryMap)) {
            Set<String> keys = queryMap.keySet();
            for (String key : keys) {
                data.append(key).append(Symbol.EQUAL).append(queryMap.get(key)).append(Symbol.AND);
            }
        }
        RequestBody requestBody = RequestBody.create(MediaType.TEXT_HTML_TYPE, data.toString());
        Request request = new Request.Builder().url(url).post(requestBody).build();
        NewCall call = httpd.newCall(request);
        call.enqueue(callback);
    }

    /**
     * form 方式 POST 请求
     *
     * @param url URL地址 String
     * @return String
     */
    public static String post(final String url) {
        return post(url, null);
    }

    /**
     * form 方式 POST 请求
     * application/x-www-form-urlencoded
     *
     * @param url     URL地址 String
     * @param formMap 查询参数 Map
     * @return String
     */
    public static String post(final String url, final Map<String, Object> formMap) {
        String data = Normal.EMPTY;
        if (MapKit.isNotEmpty(formMap)) {
            data = formMap.entrySet().stream()
                    .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(Symbol.AND));
        }
        return post(url, data, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 带查询参数 POST 请求 使用默认编码 UTF-8
     *
     * @param url       URL地址 String
     * @param data      请求数据 String
     * @param mediaType 类型 String
     * @return String
     */
    public static String post(final String url, final String data,
                              final String mediaType) {
        return post(url, data, mediaType, Charset.DEFAULT_UTF_8);
    }

    /**
     * 带查询参数 POST 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param data      请求数据 String
     * @param mediaType 类型 String
     * @param charset   自定义编码 String
     * @return String
     */
    public static String post(final String url, final String data, final String mediaType,
                              final String charset) {
        return execute(Builder.builder().url(url).method(Http.POST).data(data).mediaType(mediaType)
                .requestCharset(charset).responseCharset(charset).build());
    }

    /**
     * 带查询参数 POST 请求 使用默认编码 UTF-8
     *
     * @param url       URL地址 String
     * @param queryMap  请求数据 Map
     * @param mediaType 类型 String
     * @return String
     */
    public static String post(final String url, final Map<String, Object> queryMap,
                              final String mediaType) {
        return post(url, queryMap, mediaType, Charset.DEFAULT_UTF_8);
    }

    /**
     * 带查询参数 POST 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param headerMap 头部数据 Map
     * @param queryMap  请求数据 Map
     * @return String
     */
    public static String post(final String url, final Map<String, Object> queryMap,
                              final Map<String, String> headerMap) {
        return post(url, queryMap, headerMap, MediaType.APPLICATION_FORM_URLENCODED);
    }

    /**
     * 带查询参数 POST 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param queryMap  请求数据 Map
     * @param mediaType 类型 String
     * @param charset   自定义编码 String
     * @return String
     */
    public static String post(final String url, final Map<String, Object> queryMap,
                              final String mediaType, final String charset) {
        return execute(Builder.builder().url(url).method(Http.POST).queryMap(queryMap).mediaType(mediaType)
                .requestCharset(charset).responseCharset(charset).build());
    }

    /**
     * 带查询参数 POST 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param headerMap 头部数据 Map
     * @param queryMap  请求数据 Map
     * @param mediaType 类型 String
     * @return String
     */
    public static String post(final String url, final Map<String, Object> queryMap,
                              final Map<String, String> headerMap, final String mediaType) {
        return post(url, queryMap, headerMap, mediaType, Charset.DEFAULT_UTF_8);
    }

    /**
     * 带查询参数 POST 请求 使用自定义编码
     *
     * @param url       URL地址 String
     * @param headerMap 头部数据 Map
     * @param queryMap  请求数据 Map
     * @param mediaType 类型 String
     * @param charset   自定义编码 String
     * @return String
     */
    public static String post(final String url, final Map<String, Object> queryMap,
                              final Map<String, String> headerMap, final String mediaType,
                              final String charset) {
        return execute(Builder.builder().url(url).method(Http.POST).headerMap(headerMap).queryMap(queryMap)
                .mediaType(mediaType).requestCharset(charset).responseCharset(charset).build());
    }

    /**
     * 表单提交带文件上传
     *
     * @param url      请求地址 String
     * @param params   请求参数 Map
     * @param pathList 上传文件 List
     * @return String
     */
    public static String post(final String url, final Map<String, Object> params,
                              final List<String> pathList) {
        MediaType mediaType = MediaType.valueOf(MediaType.APPLICATION_FORM_URLENCODED + Symbol.SEMICOLON + Charset.DEFAULT_UTF_8);
        RequestBody bodyParams = RequestBody.create(mediaType, params.toString());
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder().setType(MediaType.MULTIPART_FORM_DATA_TYPE)
                .addFormDataPart("params", Normal.EMPTY, bodyParams);

        File file;
        for (String path : pathList) {
            file = new File(path);
            requestBodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(mediaType, new File(path)));
        }
        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        String result = Normal.EMPTY;
        try {
            Response response = httpd.newCall(request).execute();
            if (response.body() != null) {
                byte[] bytes = response.body().bytes();
                result = new String(bytes, Charset.DEFAULT_UTF_8);
            }
        } catch (Exception e) {
            Logger.error(">>>>>>>>Requesting HTTP Error [%s]<<<<<<<<", e);
        }
        return result;
    }

    /**
     * 通用同步执行方法
     *
     * @param builder Builder
     * @return Request 信息
     */
    private static Request.Builder builder(final Builder builder) {
        if (StringKit.isBlank(builder.requestCharset)) {
            builder.requestCharset = Charset.DEFAULT_UTF_8;
        }
        if (StringKit.isBlank(builder.responseCharset)) {
            builder.responseCharset = Charset.DEFAULT_UTF_8;
        }
        if (StringKit.isBlank(builder.method)) {
            builder.method = Http.GET;
        }
        if (StringKit.isBlank(builder.mediaType)) {
            builder.mediaType = MediaType.APPLICATION_FORM_URLENCODED;
        }
        if (builder.tracer) {
            Logger.info(">>>>>>>>Builder[{}]<<<<<<<<", builder.toString());
        }

        Request.Builder request = new Request.Builder();

        if (MapKit.isNotEmpty(builder.headerMap)) {
            builder.headerMap.forEach(request::addHeader);
        }
        String method = builder.method.toUpperCase();
        String mediaType = String.format("%s;charset=%s", builder.mediaType, builder.requestCharset);
        if (StringKit.equals(method, Http.GET)) {
            if (MapKit.isNotEmpty(builder.queryMap)) {
                String queryParams = builder.queryMap.entrySet().stream()
                        .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining(Symbol.AND));
                builder.url = String.format("%s%s%s", builder.url, builder.url.contains(Symbol.QUESTION_MARK) ? Symbol.AND : Symbol.QUESTION_MARK, queryParams);
            }
            request.get();
        } else if (ArrayKit.contains(new String[]{Http.POST, Http.PUT, Http.DELETE, Http.PATCH}, method)) {
            if (StringKit.isNotEmpty(builder.data)) {
                RequestBody requestBody = RequestBody.create(MediaType.valueOf(mediaType), builder.data);
                request.method(method, requestBody);
            }
            if (MapKit.isNotEmpty(builder.queryMap)) {
                FormBody.Builder form = new FormBody.Builder(Charset.UTF_8);
                builder.queryMap.forEach((key, value) -> form.add(key, StringKit.toString(value)));
                request.method(method, form.build());
            }
        } else {
            throw new InstrumentException(String.format(">>>>>>>>Request Method Not found[%s]<<<<<<<<", method));
        }
        return request;
    }

    /**
     * 通用同步执行方法
     *
     * @param builder Builder
     * @return String 执行结果
     */
    private static String execute(final Builder builder) {
        Request.Builder request = builder(builder);
        String result = Normal.EMPTY;
        try {
            Response response = httpd.newCall(request.url(builder.url).build()).execute();
            if (response.body() != null) {
                byte[] bytes = response.body().bytes();
                result = new String(bytes, builder.responseCharset);
            }
            if (builder.tracer) {
                Logger.info(">>>>>>>>Url[{}],Response[{}]<<<<<<<<", builder.url, result);
            }
        } catch (Exception e) {
            Logger.error(e, ">>>>>>>>Builder[{}] Error<<<<<<<<", builder.toString());
        }
        return result;
    }

    /**
     * 通用异步执行方法
     *
     * @param builder Builder
     * @return String 执行结果
     */
    private static String enqueue(final Builder builder) {
        Request.Builder request = builder(builder);
        NewCall call = httpd.newCall(request.url(builder.url).build());
        String[] result = {Normal.EMPTY};
        call.enqueue(new Callback() {
            @Override
            public void onFailure(NewCall call, IOException e) {
                Logger.info(String.format(">>>>>>>>Url[%s] Failure<<<<<<<<", builder.url));
            }

            @Override
            public void onResponse(NewCall call, Response response) throws IOException {
                if (response.body() != null) {
                    byte[] bytes = response.body().bytes();
                    result[0] = new String(bytes, builder.responseCharset);
                    if (builder.tracer) {
                        Logger.info(">>>>>>>>Url[{}],Response[{}]<<<<<<<<", builder.url, result[0]);
                    }
                }
            }
        });
        return result[0];
    }

    @lombok.Builder
    @lombok.ToString
    private static class Builder {
        /**
         * 请求 url
         */
        private String url;
        /**
         * 方法类型
         */
        private String method;
        /**
         * 请求参数
         */
        private String data;
        /**
         * 数据格式类型
         */
        private String mediaType;
        /**
         * 请求参数
         */
        private Map<String, Object> queryMap;
        /**
         * 头部参数
         */
        private Map<String, String> headerMap;

        /**
         * 请求编码
         */
        private String requestCharset;
        /**
         * 响应编码
         */
        private String responseCharset;
        /**
         * 日志追踪
         */
        private boolean tracer;
    }

}
