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

import java.util.Arrays;
import java.util.Optional;

import org.miaixz.bus.core.lang.Normal;
import org.miaixz.bus.core.lang.Symbol;
import org.miaixz.bus.core.lang.exception.ValidateException;
import org.miaixz.bus.core.xyz.StringKit;
import org.miaixz.bus.logger.Logger;
import org.miaixz.bus.vortex.Context;
import org.miaixz.bus.vortex.Format;
import org.miaixz.bus.vortex.magic.ErrorCode;
import org.miaixz.bus.vortex.provider.LicenseProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * 许可证强制校验过滤器。
 * <p>
 * 作为系统中最高优先级的过滤器之一，它在所有请求的最前端强制执行许可证有效性检查。 如果许可证校验失败，请求将被立即中断，不会进入后续的业务逻辑。
 * </p>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LicenseFilter extends AbstractFilter {

    /**
     * 授权提供者，用于处理许可证验证的实际逻辑。
     */
    private final LicenseProvider provider;

    /**
     * 构造器，注入许可证校验提供者。
     *
     * @param provider 授权提供者，负责处理许可证验证。
     */
    public LicenseFilter(LicenseProvider provider) {
        this.provider = provider;
    }

    /**
     * 过滤器的核心执行方法。
     * <p>
     * 此方法从当前请求中提取主机名（Host），并将其作为验证主体传递给 {@link LicenseProvider}。 {@code provider.validate}
     * 方法若校验失败，应抛出异常，该异常将被框架的全局异常处理器捕获， 从而中断请求并返回统一的错误响应。
     * </p>
     *
     * @param exchange 当前的 ServerWebExchange 对象
     * @param chain    过滤器链
     * @param context  请求上下文
     * @return {@link Mono<Void>} 表示异步过滤操作的完成
     * @throws ValidateException 如果许可证校验失败
     */
    @Override
    protected Mono<Void> doFilter(ServerWebExchange exchange, WebFilterChain chain, Context context) {
        // 获取域名
        Optional<String> domainOptional = getOriginalDomain(exchange.getRequest());

        // 使用 Optional 的 API 来处理结果
        String domain = domainOptional.orElseThrow(() -> {
            // 如果 Optional 为空，说明未找到任何有效域名，构造异常并抛出
            String errorMessage = "Unable to determine the request domain for license validation. The request will be ignored.";
            Format.error(exchange, "LICENSE_DOMAIN_NOT_FOUND", errorMessage);
            return new ValidateException(ErrorCode._100527);
        });

        Format.info(exchange, "LICENSE_VALIDATING", "Validating license for domain: " + domain);

        // 调用提供者执行验证。
        // 如果验证失败，provider.validate(domain) 应抛出异常。
        this.provider.validate(domain);

        // 验证通过，请求继续。
        return chain.filter(exchange);
    }

    /**
     * 从多个渠道安全地获取请求的原始域名，专为代理环境设计。
     * <p>
     * 此方法按照以下优先级顺序查找域名，一旦找到便立即返回：
     * <ol>
     * <li><b>Forwarded Header (RFC 7239):</b> 最现代、最标准的头，优先解析。</li>
     * <li><b>X-Forwarded-Host Header:</b> 最常见的事实标准，广泛用于各类代理。</li>
     * <li><b>Host Header:</b> HTTP/1.1 标准头，一个正确配置的代理应该会传递它。</li>
     * <li><b>Request URI Host:</b> 最后的备选方案，直接从请求URI中获取。</li>
     * </ol>
     *
     * @param request ServerHttpRequest 对象
     * @return 包含纯净域名（无端口）的 {@link Optional} 对象。如果所有渠道都无法找到有效域名，则返回 {@link Optional#empty()}。
     */
    public static Optional<String> getOriginalDomain(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        // 优先级 1: 尝试解析 'Forwarded' (RFC 7239)
        String forwardedHeader = headers.getFirst("Forwarded");
        if (StringKit.hasText(forwardedHeader)) {
            Optional<String> domain = Arrays.stream(forwardedHeader.split(Symbol.SEMICOLON)).map(String::trim)
                    .filter(part -> part.toLowerCase().startsWith("host="))
                    .map(part -> part.substring(5).trim().replace("\"", Normal.EMPTY)) // 移除 host= 和引号
                    .findFirst().map(host -> host.contains(Symbol.COLON) ? host.split(Symbol.COLON)[0] : host); // 内联移除端口的逻辑
            if (domain.isPresent()) {
                Logger.debug("Domain '{}' found in 'Forwarded' header.", domain.get());
                return domain;
            }
        }

        // 优先级 2: 尝试解析 'X-Forwarded-Host'
        String forwardedHostHeader = headers.getFirst("X-Forwarded-Host");
        if (StringKit.hasText(forwardedHostHeader)) {
            // 在多级代理中，此头可能包含多个域名，第一个是原始域名
            String domainWithPort = forwardedHostHeader.split(Symbol.COMMA)[0].trim();
            String domain = domainWithPort.contains(Symbol.COLON) ? domainWithPort.split(Symbol.COLON)[0]
                    : domainWithPort; // 内联移除端口的逻辑
            Logger.debug("Domain '{}' found in 'X-Forwarded-Host' header.", domain);
            return Optional.of(domain);
        }

        // 优先级 3: 尝试解析 'Host'
        String hostHeader = headers.getFirst("Host");
        if (StringKit.hasText(hostHeader)) {
            String domain = hostHeader.contains(Symbol.COLON) ? hostHeader.split(Symbol.COLON)[0] : hostHeader; // 内联移除端口的逻辑
            Logger.debug("Domain '{}' found in 'Host' header.", domain);
            return Optional.of(domain);
        }

        // 优先级 4: 使用 getURI().getHost() 作为最后备选
        // 在未正确配置代理或Spring Boot未开启forward-headers-strategy时，这可能是内部IP或主机名
        String uriHost = request.getURI().getHost();
        if (StringKit.hasText(uriHost)) {
            Logger.debug("Domain '{}' found via request.getURI().getHost() as fallback.", uriHost);
            return Optional.of(uriHost);
        }

        Logger.warn("Could not determine a valid domain from any source for request: {}", request.getPath());
        return Optional.empty();
    }

}