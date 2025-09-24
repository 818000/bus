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

import org.miaixz.bus.core.lang.Normal;
import org.miaixz.bus.core.lang.Symbol;
import org.miaixz.bus.core.lang.exception.ValidateException;
import org.miaixz.bus.core.net.PORT;
import org.miaixz.bus.core.net.Protocol;
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

import java.util.Arrays;
import java.util.Optional;

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
     * 此方法从当前请求中提取主机名和端口（Authority），并将其作为验证主体传递给 {@link LicenseProvider}。 {@code provider.validate}
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
        // 获取域名和端口
        Optional<String> authorityOptional = getOriginalAuthority(exchange.getRequest());

        // 使用 Optional 的 API 来处理结果
        String authority = authorityOptional.orElseThrow(() -> {
            // 如果 Optional 为空，说明未找到任何有效的主机信息，构造异常并抛出
            String errorMessage = "Unable to determine the request authority (host:port) for license validation. The request will be ignored.";
            Format.error(exchange, "LICENSE_AUTHORITY_NOT_FOUND", errorMessage);
            return new ValidateException(ErrorCode._100527);
        });

        Format.info(exchange, "LICENSE_VALIDATING", "Validating license for authority: " + authority);

        // 调用提供者执行验证。
        // 如果验证失败，provider.validate(authority) 应抛出异常。
        this.provider.validate(authority);

        // 验证通过，请求继续。
        return chain.filter(exchange);
    }

    /**
     * 从多个渠道安全地获取请求的原始 Authority（主机+端口），专为代理环境设计。
     * <p>
     * 此方法按照以下优先级顺序查找主机信息，并确保返回结果始终包含端口号：
     * <ol>
     * <li><b>Forwarded Header (RFC 7239):</b> 最现代、最标准的头，优先解析。</li>
     * <li><b>X-Forwarded-Host Header:</b> 最常见的事实标准，广泛用于各类代理。</li>
     * <li><b>Host Header:</b> HTTP/1.1 标准头，一个正确配置的代理应该会传递它。</li>
     * <li><b>Request URI Host:</b> 最后的备选方案，直接从请求URI中获取。</li>
     * </ol>
     * 如果找到的主机信息不包含端口，将根据请求协议（http/https）自动附加默认的 80/443 端口。
     *
     * @param request ServerHttpRequest 对象
     * @return 包含主机和端口的 {@link Optional} 对象。如果所有渠道都无法找到有效主机，则返回 {@link Optional#empty()}。
     */
    public static Optional<String> getOriginalAuthority(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String protocol = getOriginalProtocol(request);

        // 优先级 1: 尝试解析 'Forwarded' (RFC 7239)
        String forwardedHeader = headers.getFirst("Forwarded");
        if (StringKit.hasText(forwardedHeader)) {
            Optional<String> authority = Arrays.stream(forwardedHeader.split(Symbol.SEMICOLON)).map(String::trim)
                    .filter(part -> part.toLowerCase().startsWith("host="))
                    .map(part -> part.substring(5).trim().replace("\"", Normal.EMPTY)).findFirst();
            if (authority.isPresent()) {
                Logger.debug("Authority '{}' found in 'Forwarded' header.", authority.get());
                return authority.map(host -> appendPortIfMissing(host, protocol));
            }
        }

        // 优先级 2: 尝试解析 'X-Forwarded-Host'
        String forwardedHostHeader = headers.getFirst("X-Forwarded-Host");
        if (StringKit.hasText(forwardedHostHeader)) {
            // 在多级代理中，此头可能包含多个域名，第一个是原始域名
            String authority = forwardedHostHeader.split(Symbol.COMMA)[0].trim();
            Logger.debug("Authority '{}' found in 'X-Forwarded-Host' header.", authority);
            return Optional.of(appendPortIfMissing(authority, protocol));
        }

        // 优先级 3: 尝试解析 'Host'
        String hostHeader = headers.getFirst("Host");
        if (StringKit.hasText(hostHeader)) {
            Logger.debug("Authority '{}' found in 'Host' header.", hostHeader);
            return Optional.of(appendPortIfMissing(hostHeader, protocol));
        }

        // 优先级 4: 使用 getURI().getHost() 作为最后备选
        String uriHost = request.getURI().getHost();
        if (StringKit.hasText(uriHost)) {
            Logger.debug("Authority host '{}' found via request.getURI().getHost() as fallback.", uriHost);
            return Optional.of(appendPortIfMissing(uriHost, protocol));
        }

        Logger.warn("Could not determine a valid authority from any source for request: {}", request.getPath());
        return Optional.empty();
    }

    /**
     * 获取请求的原始协议（http 或 https）。
     * <p>
     * 优先从代理头中获取，以确保在反向代理后也能得到正确的结果。 查找顺序: 'Forwarded' (proto=) -> 'X-Forwarded-Proto' -> request.getURI().getScheme()
     *
     * @param request ServerHttpRequest 对象
     * @return 协议字符串, "https" 或 "http".
     */
    private static String getOriginalProtocol(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();

        // 尝试从 'Forwarded' 头解析
        String forwardedHeader = headers.getFirst("Forwarded");
        if (StringKit.hasText(forwardedHeader)) {
            Optional<String> proto = Arrays.stream(forwardedHeader.split(Symbol.SEMICOLON)).map(String::trim)
                    .filter(part -> part.toLowerCase().startsWith("proto="))
                    .map(part -> part.substring(6).trim().replace("\"", Normal.EMPTY)).findFirst();
            if (proto.isPresent()) {
                return proto.get();
            }
        }

        // 尝试从 'X-Forwarded-Proto' 头解析
        String forwardedProtoHeader = headers.getFirst("X-Forwarded-Proto");
        if (StringKit.hasText(forwardedProtoHeader)) {
            return forwardedProtoHeader.split(Symbol.COMMA)[0].trim();
        }

        // 使用 URI scheme 作为最后备选
        return request.getURI().getScheme();
    }

    /**
     * 为给定的主机（可能包含端口）附加默认端口（如果缺少）。
     *
     * @param authority 主机信息，例如 "example.com" 或 "example.com:8080"
     * @param protocol  协议, "http" 或 "https"
     * @return 始终包含端口的主机信息，例如 "example.com:443" 或 "example.com:8080"
     */
    private static String appendPortIfMissing(String authority, String protocol) {
        if (authority.contains(Symbol.COLON)) {
            return authority; // 端口已存在
        }
        if (Protocol.HTTPS.name.equalsIgnoreCase(protocol)) {
            return authority + Symbol.COLON + PORT._443;
        }
        return authority + Symbol.COLON + PORT._80;
    }

}