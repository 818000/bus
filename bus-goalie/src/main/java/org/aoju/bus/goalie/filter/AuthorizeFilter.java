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
package org.aoju.bus.goalie.filter;

import org.aoju.bus.base.consts.ErrorCode;
import org.aoju.bus.base.entity.OAuth2;
import org.aoju.bus.core.lang.exception.BusinessException;
import org.aoju.bus.core.toolkit.BeanKit;
import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.goalie.Assets;
import org.aoju.bus.goalie.Config;
import org.aoju.bus.goalie.Context;
import org.aoju.bus.goalie.metric.Authorize;
import org.aoju.bus.goalie.metric.Delegate;
import org.aoju.bus.goalie.metric.Token;
import org.aoju.bus.goalie.registry.AssetsRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;

/**
 * 访问鉴权
 *
 * @author Justubborn
 * @version 6.2.8
 * @since JDK 1.8+
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 2)
public class AuthorizeFilter implements WebFilter {

    private final Authorize authorize;

    private final AssetsRegistry registry;

    public AuthorizeFilter(Authorize authorize, AssetsRegistry registry) {
        this.authorize = authorize;
        this.registry = registry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Context context = Context.get(exchange);
        Map<String, String> params = context.getRequestMap();

        context.setFormat(Context.Format.valueOf(params.get(Config.FORMAT)));
        context.setChannel(Context.Channel.getChannel(params.get(Config.X_REMOTE_CHANNEL)));
        context.setToken(exchange.getRequest().getHeaders().getFirst(Config.X_ACCESS_TOKEN));

        String method = params.get(Config.METHOD);
        String version = params.get(Config.VERSION);
        Assets assets = registry.getAssets(method, version);

        if (null == assets) {
            return Mono.error(new BusinessException(ErrorCode.EM_100500));
        }
        //校验方法
        checkMethod(exchange.getRequest(), assets);
        //校验参数
        checkTokenIfNecessary(context, assets, params);
        //填充Ip
        fillXParam(exchange, params);

        //清理 method 和 version
        cleanParam(params);
        context.setAssets(assets);

        return chain.filter(exchange);
    }

    /**
     * 校验方法
     *
     * @param request 请求
     * @param assets  路由
     */
    private void checkMethod(ServerHttpRequest request, Assets assets) {
        if (!Objects.equals(request.getMethod(), assets.getHttpMethod())) {
            if (Objects.equals(assets.getHttpMethod(), HttpMethod.GET)) {
                throw new BusinessException(ErrorCode.EM_100200);
            } else if (Objects.equals(assets.getHttpMethod(), HttpMethod.POST)) {
                throw new BusinessException(ErrorCode.EM_100201);
            } else {
                throw new BusinessException(ErrorCode.EM_100508);
            }

        }
    }

    /**
     * 校验 token 并 填充参数
     *
     * @param context 请求
     * @param assets  路由
     * @param params  参数
     */
    private void checkTokenIfNecessary(Context context, Assets assets, Map<String, String> params) {
        // 访问授权校验
        if (assets.isToken()) {
            if (StringKit.isBlank(context.getToken())) {
                throw new BusinessException(ErrorCode.EM_100106);
            }
            Token access = new Token(context.getToken(), context.getChannel().getTokenType());
            Delegate delegate = authorize.authorize(access);
            if (delegate.isOk()) {
                OAuth2 auth2 = delegate.getOAuth2();
                //api permissions
                if (!apiPermissions(auth2, assets)) {
                    throw new BusinessException(ErrorCode.EM_100500, "没有权限");
                }

                Map<String, Object> map = BeanKit.beanToMap(auth2, false, true);
                map.forEach((k, v) -> params.put(k, v.toString()));
            } else {
                throw new BusinessException(delegate.getMessage().errcode, delegate.getMessage().errmsg);
            }
        }
    }

    /**
     * 清理网关参数
     *
     * @param params 参数
     */
    private void cleanParam(Map<String, String> params) {
        params.remove(Config.METHOD);
        params.remove(Config.FORMAT);
        params.remove(Config.VERSION);
        params.remove(Config.SIGN);
    }

    /**
     * 填充参数
     *
     * @param exchange     exchange
     * @param requestParam 请求参数
     */
    private void fillXParam(ServerWebExchange exchange, Map<String, String> requestParam) {
        String ip = exchange.getRequest().getHeaders().getFirst("x_remote_ip");
        if (StringKit.isBlank(ip)) {
            InetSocketAddress address = exchange.getRequest().getRemoteAddress();
            if (null != address) {
                ip = address.getAddress().getHostAddress();
            }
        }
        requestParam.put("x_remote_ip", ip);
    }

    /**
     * 是否有权限
     *
     * @param oAuth2 认证
     * @param assets 路由
     * @return 是否
     */
    private boolean apiPermissions(OAuth2 oAuth2, Assets assets) {
        if (CollKit.isEmpty(assets.getRoleIds())) {
            return false;
        }
        if (StringKit.isEmpty(oAuth2.getX_role_id())) {
            return false;
        }
        boolean result = false;
        for (String roleId : assets.getRoleIds()) {
            if (oAuth2.getX_role_id().contains(roleId)) {
                result = true;
                break;
            }
        }
        return result;
    }

}
