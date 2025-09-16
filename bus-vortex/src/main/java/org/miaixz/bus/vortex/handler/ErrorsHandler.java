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
package org.miaixz.bus.vortex.handler;

import lombok.*;
import org.miaixz.bus.core.lang.Charset;
import org.miaixz.bus.core.lang.exception.BusinessException;
import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.lang.exception.ValidateException;
import org.miaixz.bus.core.xyz.StringKit;
import org.miaixz.bus.vortex.Context;
import org.miaixz.bus.vortex.Format;
import org.miaixz.bus.vortex.magic.ErrorCode;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.net.UnknownHostException;

/**
 * 全局异常处理器，处理 Web 应用中的异常并返回标准化的 JSON 响应
 *
 * @author Justubborn
 * @since Java 17+
 */
public class ErrorsHandler implements WebExceptionHandler {

    /**
     * 处理异常，生成标准化的错误响应
     *
     * @param exchange 当前的 ServerWebExchange 对象，包含请求和响应
     * @param ex       捕获的异常对象
     * @return {@link Mono<Void>} 表示异步处理完成
     */
    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 获取响应对象并设置状态码和内容类型
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 获取请求上下文
        Context context = Context.get(exchange);

        // 根据异常类型生成错误消息
        Message message = buildErrorMessage(ex, exchange);

        // 强制使用 JSON 序列化，确保返回JSON格式
        String formatBody = context.getFormat().getProvider().serialize(message);

        // 将格式化后的响应写入 DataBuffer
        DataBuffer db = response.bufferFactory().wrap(formatBody.getBytes(Charset.UTF_8));

        // 返回响应并记录执行耗时
        return response.writeWith(Mono.just(db)).doOnTerminate(() -> logErrorHandling(exchange, context, ex));
    }

    /**
     * 构建错误消息
     *
     * @param ex       异常对象
     * @param exchange ServerWebExchange 对象
     * @return 错误消息对象
     */
    protected Message buildErrorMessage(Throwable ex, ServerWebExchange exchange) {
        // 处理 WebClientException
        if (ex instanceof WebClientException) {
            return handleWebClientException((WebClientException) ex, exchange);
        }

        // 处理 InternalException
        if (ex instanceof InternalException) {
            return handleInternalException((InternalException) ex, exchange);
        }

        // 处理 ValidateException
        if (ex instanceof ValidateException) {
            return handleValidateException((ValidateException) ex, exchange);
        }

        // 处理 BusinessException
        if (ex instanceof BusinessException) {
            return handleBusinessException((BusinessException) ex, exchange);
        }

        // 处理未知异常
        return handleUnknownException(ex, exchange);
    }

    /**
     * 处理 WebClientException
     */
    protected Message handleWebClientException(WebClientException ex, ServerWebExchange exchange) {
        if (ex.getCause() instanceof UnknownHostException) {
            Format.error(exchange, "WEBCLIENT_UNKNOWN_HOST", "UnknownHostException: " + ex.getCause().getMessage());
            return Message.builder().errcode(ErrorCode._80010001.getKey()).errmsg(ErrorCode._80010001.getValue())
                    .build();
        } else {
            Format.error(exchange, "WEBCLIENT_EXCEPTION", "WebClientException: " + ex.getMessage());
            return Message.builder().errcode(ErrorCode._80010002.getKey()).errmsg(ErrorCode._80010002.getValue())
                    .build();
        }
    }

    /**
     * 处理 InternalException
     */
    protected Message handleInternalException(InternalException ex, ServerWebExchange exchange) {
        if (StringKit.isNotBlank(ex.getErrcode())) {
            Format.error(exchange, "INTERNAL_EXCEPTION",
                    "ErrorCode: " + ex.getErrcode() + ", Message: " + ex.getErrmsg());
            return Message.builder().errcode(ex.getErrcode()).errmsg(ex.getErrmsg()).build();
        } else {
            Format.error(exchange, "INTERNAL_EXCEPTION", "Generic InternalException: " + ex.getMessage());
            return Message.builder().errcode(ErrorCode._100807.getKey()).errmsg(ErrorCode._100807.getValue()).build();
        }
    }

    /**
     * 处理 ValidateException
     */
    protected Message handleValidateException(ValidateException ex, ServerWebExchange exchange) {
        if (StringKit.isNotBlank(ex.getErrcode())) {
            Format.error(exchange, "VALIDATE_EXCEPTION",
                    "ErrorCode: " + ex.getErrcode() + ", Message: " + ex.getErrmsg());
            return Message.builder().errcode(ex.getErrcode()).errmsg(ex.getErrmsg()).build();
        } else {
            Format.error(exchange, "VALIDATE_EXCEPTION", "Generic ValidateException: " + ex.getMessage());
            return Message.builder().errcode(ErrorCode._100807.getKey()).errmsg(ErrorCode._100807.getValue()).build();
        }
    }

    /**
     * 处理 BusinessException
     */
    protected Message handleBusinessException(BusinessException ex, ServerWebExchange exchange) {
        if (StringKit.isNotBlank(ex.getErrcode())) {
            Format.error(exchange, "BUSINESS_EXCEPTION",
                    "ErrorCode: " + ex.getErrcode() + ", Message: " + ex.getErrmsg());
            return Message.builder().errcode(ex.getErrcode()).errmsg(ex.getErrmsg()).build();
        } else {
            Format.error(exchange, "BUSINESS_EXCEPTION", "Generic BusinessException: " + ex.getMessage());
            return Message.builder().errcode(ErrorCode._100807.getKey()).errmsg(ErrorCode._100807.getValue()).build();
        }
    }

    /**
     * 处理未知异常
     */
    protected Message handleUnknownException(Throwable ex, ServerWebExchange exchange) {
        Format.error(exchange, "UNKNOWN_EXCEPTION",
                "Unknown exception type: " + ex.getClass().getName() + ", Message: " + ex.getMessage());
        return Message.builder().errcode(ErrorCode._100807.getKey()).errmsg(ErrorCode._100807.getValue()).build();
    }

    /**
     * 记录错误处理日志
     *
     * @param exchange ServerWebExchange 对象
     * @param context  请求上下文
     * @param ex       异常对象
     */
    private void logErrorHandling(ServerWebExchange exchange, Context context, Throwable ex) {
        if (context != null) {
            long executionTime = System.currentTimeMillis() - context.getStartTime();
            Format.info(exchange, "ERROR_HANDLED", "Error handled, execution time: " + executionTime + "ms, exception: "
                    + ex.getClass().getSimpleName());
        } else {
            Format.info(exchange, "ERROR_HANDLED", "Error handled, exception: " + ex.getClass().getSimpleName());
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        /**
         * 响应码
         */
        private String errcode;

        /**
         * 提示信息
         */
        private String errmsg;

    }

}