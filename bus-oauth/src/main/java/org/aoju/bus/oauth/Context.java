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
package org.aoju.bus.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aoju.bus.oauth.magic.Callback;

import java.util.List;

/**
 * 上下文配置类
 *
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Context {

    /**
     * 对应各平台的appKey
     */
    private String appKey;

    /**
     * 对应各平台的appSecret
     */
    private String appSecret;

    /**
     * 支付宝公钥：当选择支付宝登录时,该值可用
     * 对应“RSA2(SHA256)密钥”中的“支付宝公钥”
     */
    private String publicKey;

    /**
     * Stack Overflow Key
     */
    private String overflowKey;

    /**
     * 企业微信,授权方的网页应用ID
     */
    private String agentId;

    /**
     * 登录成功后的回调地址
     */
    private String redirectUri;

    /**
     * 是否需要申请unionid,目前只针对qq登录
     * 注：QQ授权登录时,获取unionid需要单独发送邮件申请权限 如果个人开发者账号中申请了该权限,
     * 可以将该值置为true,在获取openId时就会同步获取unionId
     * 参考链接：http://wiki.connect.qq.com/unionid%E4%BB%8B%E7%BB%8D
     */
    private boolean unionId;

    /**
     * 忽略校验 {@code state} 参数，默认不开启。当 {@code ignoreCheckState} 为 {@code true} 时，
     * {@link org.aoju.bus.oauth.provider.AbstractProvider#login(Callback callback)} 将不会校验 {@code state} 的合法性。
     * <p>
     * 使用场景：当且仅当使用自实现 {@code state} 校验逻辑时开启
     * <p>
     * 以下场景使用方案仅作参考：
     * 1. 授权、登录为同端，并且全部使用 JustAuth 实现时，该值建议设为 {@code false};
     * 2. 授权和登录为不同端实现时，比如前端页面拼装 {@code authorizeUrl}，并且前端自行对{@code state}进行校验，
     * 后端只负责使用{@code code}获取用户信息时，该值建议设为 {@code true};
     *
     * <strong>如非特殊需要，不建议开启这个配置</strong>
     * <p>
     * 该方案主要为了解决以下类似场景的问题：
     *
     * @see <a href="https://github.com/justauth/JustAuth/issues/83">https://github.com/justauth/JustAuth/issues/83</a>
     */
    private boolean ignoreCheckState;

    /**
     * 使用 Coding 登录时，需要传该值。
     * <p>
     * 团队域名前缀，比如以“ https://justauth.coding.net/ ”为例，{@code codingGroupName} = justauth
     */
    private String codingGroupName;

    /**
     * 支持自定义授权平台的 scope 内容
     */
    private List<String> scopes;

}
