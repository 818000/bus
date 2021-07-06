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
package org.aoju.bus.oauth.provider;

import com.alibaba.fastjson.JSONObject;
import org.aoju.bus.cache.metric.ExtendCache;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.AuthorizedException;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.oauth.Builder;
import org.aoju.bus.oauth.Context;
import org.aoju.bus.oauth.Registry;
import org.aoju.bus.oauth.magic.AccToken;
import org.aoju.bus.oauth.magic.Callback;
import org.aoju.bus.oauth.magic.Message;
import org.aoju.bus.oauth.magic.Property;
import org.aoju.bus.oauth.metric.OauthScope;

/**
 * 微信公众平台登录
 *
 * @author Kimi Liu
 * @version 6.2.5
 * @since JDK 1.8+
 */
public class WeChatMpProvider extends AbstractProvider {

    public WeChatMpProvider(Context context) {
        super(context, Registry.WECHAT_MP);
    }

    public WeChatMpProvider(Context context, ExtendCache extendCache) {
        super(context, Registry.WECHAT_MP, extendCache);
    }

    /**
     * 微信的特殊性，此时返回的信息同时包含 openid 和 access_token
     *
     * @param callback 回调返回的参数
     * @return 所有信息
     */
    @Override
    public AccToken getAccessToken(Callback callback) {
        return this.getToken(accessTokenUrl(callback.getCode()));
    }

    @Override
    public Property getUserInfo(AccToken accToken) {
        String openId = accToken.getOpenId();

        JSONObject object = JSONObject.parseObject(doGetUserInfo(accToken));
        this.checkResponse(object);

        String location = String.format("%s-%s-%s", object.getString("country"), object.getString("province"), object.getString("city"));

        if (object.containsKey("unionid")) {
            accToken.setUnionId(object.getString("unionid"));
        }

        return Property.builder()
                .rawJson(object)
                .username(object.getString("nickname"))
                .nickname(object.getString("nickname"))
                .avatar(object.getString("headimgurl"))
                .location(location)
                .uuid(openId)
                .gender(Normal.Gender.of(object.getString("sex")))
                .token(accToken)
                .source(source.toString())
                .build();
    }

    @Override
    public Message refresh(AccToken accToken) {
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .data(this.getToken(refreshTokenUrl(accToken.getRefreshToken())))
                .build();
    }

    /**
     * 检查响应内容是否正确
     *
     * @param object 请求响应内容
     */
    private void checkResponse(JSONObject object) {
        if (object.containsKey("errcode")) {
            throw new AuthorizedException(StringKit.toString(object.getIntValue("errcode")), object.getString("errmsg"));
        }
    }

    /**
     * 获取token，适用于获取access_token和刷新token
     *
     * @param accessTokenUrl 实际请求token的地址
     * @return token对象
     */
    private AccToken getToken(String accessTokenUrl) {
        JSONObject object = JSONObject.parseObject(Httpx.get(accessTokenUrl));

        this.checkResponse(object);

        return AccToken.builder()
                .accessToken(object.getString("access_token"))
                .refreshToken(object.getString("refresh_token"))
                .expireIn(object.getIntValue("expires_in"))
                .openId(object.getString("openid"))
                .scope(object.getString("scope"))
                .build();
    }

    /**
     * 返回带{@code state}参数的授权url，授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数，可以防止csrf
     * @return 返回授权地址
     */
    @Override
    public String authorize(String state) {
        return Builder.fromUrl(source.authorize())
                .queryParam("appid", context.getAppKey())
                .queryParam("redirect_uri", context.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", this.getScopes(Symbol.COMMA, false, getScopes(true, OauthScope.WechatMp.values())))
                .queryParam("state", getRealState(state).concat("#wechat_redirect"))
                .build();
    }

    /**
     * 返回获取accessToken的url
     *
     * @param code 授权码
     * @return 返回获取accessToken的url
     */
    @Override
    public String accessTokenUrl(String code) {
        return Builder.fromUrl(source.accessToken())
                .queryParam("appid", context.getAppKey())
                .queryParam("secret", context.getAppSecret())
                .queryParam("code", code)
                .queryParam("grant_type", "authorization_code")
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param accToken 用户授权后的token
     * @return 返回获取userInfo的url
     */
    @Override
    public String userInfoUrl(AccToken accToken) {
        return Builder.fromUrl(source.userInfo())
                .queryParam("access_token", accToken.getAccessToken())
                .queryParam("openid", accToken.getOpenId())
                .queryParam("lang", "zh_CN")
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param refreshToken getAccessToken方法返回的refreshToken
     * @return 返回获取userInfo的url
     */
    @Override
    public String refreshTokenUrl(String refreshToken) {
        return Builder.fromUrl(source.refresh())
                .queryParam("appid", context.getAppKey())
                .queryParam("grant_type", "refresh_token")
                .queryParam("refresh_token", refreshToken)
                .build();
    }

}
