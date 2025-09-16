/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.vortex.filter;

import java.util.*;

import org.miaixz.bus.core.lang.Charset;
import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.lang.exception.ValidateException;
import org.miaixz.bus.extra.json.JsonKit;
import org.miaixz.bus.vortex.Context;
import org.miaixz.bus.vortex.Format;
import org.miaixz.bus.vortex.magic.ErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 * 参数过滤和校验过滤器，负责处理和验证请求参数，设置上下文
 *
 * @author Justubborn
 * @since Java 17+
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PrimaryFilter extends AbstractFilter {

    /**
     * 需要拦截的非法请求路径列表
     */
    private static final List<String> BLOCKED_PATHS = Arrays.asList("/favicon.ico", "/robots.txt", "/sitemap.xml",
            "/apple-touch-icon.png", "/apple-touch-icon-precomposed.png",
            "/.well-known/appspecific/com.chrome.devtools.json");

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * 重试延迟（毫秒）
     */
    private static final long RETRY_DELAY_MS = 1000;

    /**
     * 内部过滤方法，处理请求参数并进行校验
     *
     * @param exchange 当前的 ServerWebExchange 对象
     * @param chain    过滤器链
     * @param context  请求上下文
     * @return {@link Mono<Void>} 表示异步处理完成
     */
    @Override
    protected Mono<Void> doFilter(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        String path = exchange.getRequest().getPath().value();
        // 检查是否为需要拦截的路径
        if (BLOCKED_PATHS.contains(path)) {
            Format.warn(exchange, "BLOCKED_REQUEST", "Blocked request to path: " + path);
            // 抛出异常，避免继续处理
            throw new ValidateException(ErrorCode._BLOCKED);
        }
        // 检查是否为浏览器地址遍历攻击
        if (isPathTraversalAttempt(path)) {
            Format.warn(exchange, "PATH_TRAVERSAL_ATTEMPT", "Path traversal attempt detected: " + path);
            // 抛出异常，避免继续处理
            throw new ValidateException(ErrorCode._LIMITER);
        }

        ServerWebExchange mutate = setContentType(exchange);
        context.setStartTime(System.currentTimeMillis());
        ServerHttpRequest request = mutate.getRequest();

        if (Objects.equals(request.getMethod(), HttpMethod.GET)) {
            return handleGetRequest(mutate, chain, context);
        } else {
            MediaType contentType = mutate.getRequest().getHeaders().getContentType();
            if (contentType == null) {
                return handleFormRequest(mutate, chain, context);
            } else if (MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                return handleJsonRequest(mutate, chain, context);
            } else if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
                // 对于 multipart 请求，直接处理，不要预先读取请求体
                return handleMultipartRequest(mutate, chain, context);
            } else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return handleFormRequest(mutate, chain, context);
            }
            // 其他类型，尝试作为表单处理
            return handleFormRequest(mutate, chain, context);
        }
    }

    /**
     * 处理GET请求
     */
    private Mono<Void> handleGetRequest(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
        context.setRequestMap(params.toSingleValueMap());
        checkParams(exchange);
        Format.info(exchange, "GET_PARAMS_PROCESSED", "Path: " + exchange.getRequest().getURI().getPath() + ", Params: "
                + JsonKit.toJsonString(context.getRequestMap()));

        return chain.filter(exchange).doOnSuccess(
                v -> Format.info(exchange, "REQUEST_PROCESSED", "Path: " + exchange.getRequest().getURI().getPath()
                        + ", ExecutionTime: " + (System.currentTimeMillis() - context.getStartTime()) + "ms"));
    }

    /**
     * 处理JSON请求
     */
    private Mono<Void> handleJsonRequest(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        // 收集请求体数据
        return exchange.getRequest().getBody().collectList()
                .flatMap(dataBuffers -> processJsonData(exchange, chain, context, dataBuffers))
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, java.time.Duration.ofMillis(RETRY_DELAY_MS))
                        .maxBackoff(java.time.Duration.ofMillis(500)).jitter(0.75).doBeforeRetry(retrySignal -> {
                            Format.warn(exchange, "JSON_RETRY",
                                    "Retrying JSON request processing, attempt: " + (retrySignal.totalRetries() + 1)
                                            + ", error: " + retrySignal.failure().getMessage());
                        }).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Format.error(exchange, "JSON_RETRY_EXHAUSTED", "JSON request processing failed after "
                                    + MAX_RETRY_ATTEMPTS + " attempts, error: " + retrySignal.failure().getMessage());
                            return new InternalException(ErrorCode._80010002);
                        }));
    }

    /**
     * 处理JSON数据
     */
    private Mono<Void> processJsonData(ServerWebExchange exchange, WebFilterChain chain, Context context,
            List<DataBuffer> dataBuffers) {
        try {
            // 合并所有数据缓冲区
            byte[] bytes = new byte[dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum()];
            int pos = 0;
            for (DataBuffer buffer : dataBuffers) {
                int length = buffer.readableByteCount();
                buffer.read(bytes, pos, length);
                pos += length;
            }

            String jsonBody = new String(bytes, Charset.UTF_8);
            Map<String, String> jsonMap = JsonKit.toMap(jsonBody);
            context.setRequestMap(jsonMap);

            // 创建新的请求装饰器，重写getBody()方法以返回原始数据
            ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                    return Flux.just(bufferFactory.wrap(bytes));
                }
            };

            // 创建新的交换机，使用新的请求
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

            checkParams(newExchange);
            Format.info(newExchange, "JSON_PARAMS_PROCESSED", "Path: " + newExchange.getRequest().getURI().getPath()
                    + ", Params: " + JsonKit.toJsonString(jsonMap));
            return chain.filter(newExchange)
                    .doOnTerminate(() -> Format.info(newExchange, "REQUEST_PROCESSED",
                            "Path: " + newExchange.getRequest().getURI().getPath() + ", ExecutionTime: "
                                    + (System.currentTimeMillis() - context.getStartTime()) + "ms"));
        } catch (Exception e) {
            Format.error(exchange, "JSON_PROCESSING_ERROR", "Failed to process JSON: " + e.getMessage());
            return Mono.error(e);
        }
    }

    /**
     * 处理表单请求
     */
    private Mono<Void> handleFormRequest(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        // 收集请求体数据
        return exchange.getRequest().getBody().collectList()
                .flatMap(dataBuffers -> processFormData(exchange, chain, context, dataBuffers))
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, java.time.Duration.ofMillis(RETRY_DELAY_MS))
                        .maxBackoff(java.time.Duration.ofMillis(500)).jitter(0.75).doBeforeRetry(retrySignal -> {
                            Format.warn(exchange, "FORM_RETRY",
                                    "Retrying form request processing, attempt: " + (retrySignal.totalRetries() + 1)
                                            + ", error: " + retrySignal.failure().getMessage());
                        }).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Format.error(exchange, "FORM_RETRY_EXHAUSTED", "Form request processing failed after "
                                    + MAX_RETRY_ATTEMPTS + " attempts, error: " + retrySignal.failure().getMessage());
                            return new InternalException(ErrorCode._80010002);
                        }));
    }

    /**
     * 处理表单数据
     */
    private Mono<Void> processFormData(ServerWebExchange exchange, WebFilterChain chain, Context context,
            List<DataBuffer> dataBuffers) {
        try {
            // 合并所有数据缓冲区
            byte[] bytes = new byte[dataBuffers.stream().mapToInt(DataBuffer::readableByteCount).sum()];
            int pos = 0;
            for (DataBuffer buffer : dataBuffers) {
                int length = buffer.readableByteCount();
                buffer.read(bytes, pos, length);
                pos += length;
            }

            // 创建新的请求装饰器，重写getBody()方法以返回原始数据
            ServerHttpRequest newRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
                    return Flux.just(bufferFactory.wrap(bytes));
                }
            };

            // 创建新的交换机，使用新的请求
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();

            return newExchange.getFormData().flatMap(params -> {
                context.setRequestMap(params.toSingleValueMap());
                checkParams(newExchange);
                Format.info(newExchange, "FORM_PARAMS_PROCESSED", "Path: " + newExchange.getRequest().getURI().getPath()
                        + ", Params: " + JsonKit.toJsonString(context.getRequestMap()));
                return chain.filter(newExchange)
                        .doOnTerminate(() -> Format.info(newExchange, "REQUEST_PROCESSED",
                                "Path: " + newExchange.getRequest().getURI().getPath() + ", ExecutionTime: "
                                        + (System.currentTimeMillis() - context.getStartTime()) + "ms"));
            });
        } catch (Exception e) {
            Format.error(exchange, "FORM_PROCESSING_ERROR", "Failed to process form: " + e.getMessage());
            return Mono.error(e);
        }
    }

    /**
     * 处理文件上传请求
     */
    private Mono<Void> handleMultipartRequest(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        // 对于 multipart 请求，直接处理，不要预先读取请求体
        return exchange.getMultipartData().flatMap(params -> processMultipartData(exchange, chain, context, params))
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, java.time.Duration.ofMillis(RETRY_DELAY_MS))
                        .maxBackoff(java.time.Duration.ofMillis(500)).jitter(0.75).doBeforeRetry(retrySignal -> {
                            Format.warn(exchange, "MULTIPART_RETRY",
                                    "Retrying multipart request processing, attempt: "
                                            + (retrySignal.totalRetries() + 1) + ", error: "
                                            + retrySignal.failure().getMessage());
                        }).onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            Format.error(exchange, "MULTIPART_RETRY_EXHAUSTED",
                                    "Multipart request processing failed after " + MAX_RETRY_ATTEMPTS
                                            + " attempts, error: " + retrySignal.failure().getMessage());

                            // 检查是否是特定错误，如边界错误
                            if (retrySignal.failure().getMessage() != null
                                    && retrySignal.failure().getMessage().contains("Could not find first boundary")) {
                                return new InternalException(ErrorCode._100303);
                            }
                            return new InternalException(ErrorCode._80010002);
                        }));
    }

    /**
     * 处理multipart数据
     */
    private Mono<Void> processMultipartData(ServerWebExchange exchange, WebFilterChain chain, Context context,
            MultiValueMap<String, Part> params) {
        try {
            Map<String, String> formMap = new LinkedHashMap<>();
            Map<String, Part> fileMap = new LinkedHashMap<>();

            params.toSingleValueMap().forEach((k, v) -> {
                if (v instanceof FormFieldPart) {
                    formMap.put(k, ((FormFieldPart) v).value());
                }
                if (v instanceof FilePart) {
                    fileMap.put(k, v);
                }
            });

            context.setRequestMap(formMap);
            context.setFilePartMap(fileMap);
            checkParams(exchange);

            Format.info(exchange, "MULTIPART_PARAMS_PROCESSED",
                    "Path: " + exchange.getRequest().getURI().getPath() + ", Params: " + JsonKit.toJsonString(formMap));

            return chain.filter(exchange).doOnTerminate(
                    () -> Format.info(exchange, "REQUEST_PROCESSED", "Path: " + exchange.getRequest().getURI().getPath()
                            + ", ExecutionTime: " + (System.currentTimeMillis() - context.getStartTime()) + "ms"));
        } catch (Exception e) {
            Format.error(exchange, "MULTIPART_PROCESSING_ERROR", "Failed to process multipart: " + e.getMessage());
            return Mono.error(e);
        }
    }

    /**
     * 检查是否为路径遍历攻击尝试
     */
    private boolean isPathTraversalAttempt(String path) {
        // 检查路径遍历攻击特征
        return path.contains("../") || path.contains("..\\") || path.contains("%2e%2e%2f") || path.contains("%2e%2e\\")
                || path.contains("..%2f") || path.contains("..%5c");
    }

}