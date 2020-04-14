/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.oauth.provider;

import com.alibaba.fastjson.JSONObject;
import org.aoju.bus.cache.metric.ExtendCache;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.AuthorizedException;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.oauth.Builder;
import org.aoju.bus.oauth.Context;
import org.aoju.bus.oauth.Registry;
import org.aoju.bus.oauth.magic.AccToken;
import org.aoju.bus.oauth.magic.Callback;
import org.aoju.bus.oauth.magic.Message;
import org.aoju.bus.oauth.magic.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * 华为授权登录
 *
 * @author Kimi Liu
 * @version 5.8.6
 * @since JDK 1.8+
 */
public class HuaweiProvider extends DefaultProvider {

    public HuaweiProvider(Context context) {
        super(context, Registry.HUAWEI);
    }

    public HuaweiProvider(Context context, ExtendCache extendCache) {
        super(context, Registry.HUAWEI, extendCache);
    }

    /**
     * 获取access token
     *
     * @param Callback 授权成功后的回调参数
     * @return token
     * @see DefaultProvider#authorize(String)
     */
    @Override
    protected AccToken getAccessToken(Callback Callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("code", Callback.getAuthorization_code());
        params.put("client_id", context.getAppKey());
        params.put("client_secret", context.getAppSecret());
        params.put("redirect_uri", context.getRedirectUri());

        Httpx.post(source.accessToken(), params);

        return getAuthToken(params);
    }

    /**
     * 使用token换取用户信息
     *
     * @param accToken token信息
     * @return 用户信息
     * @see DefaultProvider#getAccessToken(Callback)
     */
    @Override
    protected Property getUserInfo(AccToken accToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("nsp_ts", System.currentTimeMillis());
        params.put("access_token", accToken.getAccessToken());
        params.put("nsp_fmt", "JS");
        params.put("nsp_svc", "OpenUP.User.getInfo");

        String response = Httpx.post(source.userInfo(), params);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return Property.builder()
                .uuid(object.getString("userID"))
                .username(object.getString("userName"))
                .nickname(object.getString("userName"))
                .gender(getRealGender(object))
                .avatar(object.getString("headPictureURL"))
                .token(accToken)
                .source(source.toString())
                .build();
    }

    /**
     * 刷新access token （续期）
     *
     * @param token 登录成功后返回的Token信息
     * @return AuthResponse
     */
    @Override
    public Message refresh(AccToken token) {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", context.getAppKey());
        params.put("client_secret", context.getAppSecret());
        params.put("refresh_token", token.getRefreshToken());
        params.put("grant_type", "refresh_token");
        Httpx.post(source.accessToken(), params);
        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .data(getAuthToken(params))
                .build();
    }

    /**
     * 返回带{@code state}参数的授权url,授权回调时会带上这个{@code state}
     *
     * @param state state 验证授权流程的参数,可以防止csrf
     * @return 返回授权地址
     * @since 1.9.3
     */
    @Override
    public String authorize(String state) {
        return Builder.fromUrl(source.authorize())
                .queryParam("response_type", "code")
                .queryParam("client_id", context.getAppKey())
                .queryParam("redirect_uri", context.getRedirectUri())
                .queryParam("access_type", "offline")
                .queryParam("scope", "https%3A%2F%2Fwww.huawei.com%2Fauth%2Faccount%2Fbase.profile")
                .queryParam("state", getRealState(state))
                .build();
    }

    /**
     * 返回获取accessToken的url
     *
     * @param code 授权码
     * @return 返回获取accessToken的url
     */
    @Override
    protected String accessTokenUrl(String code) {
        return Builder.fromUrl(source.accessToken())
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("client_id", context.getAppKey())
                .queryParam("client_secret", context.getAppSecret())
                .queryParam("redirect_uri", context.getRedirectUri())
                .build();
    }

    /**
     * 返回获取userInfo的url
     *
     * @param token token
     * @return 返回获取userInfo的url
     */
    @Override
    protected String userInfoUrl(AccToken token) {
        return Builder.fromUrl(source.userInfo())
                .queryParam("nsp_ts", System.currentTimeMillis())
                .queryParam("access_token", token.getAccessToken())
                .queryParam("nsp_fmt", "JS")
                .queryParam("nsp_svc", "OpenUP.User.getInfo")
                .build();
    }

    /**
     * 校验响应结果
     *
     * @param object 接口返回的结果
     */
    private void checkResponse(JSONObject object) {
        if (object.containsKey("NSP_STATUS")) {
            throw new AuthorizedException(object.getString("error"));
        }
        if (object.containsKey("error")) {
            throw new AuthorizedException(object.getString("sub_error") + Symbol.COLON + object.getString("error_description"));
        }
    }

    private AccToken getAuthToken(Map<String, Object> params) {
        String response = Httpx.post(source.accessToken(), params);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return AccToken.builder()
                .accessToken(object.getString("access_token"))
                .expireIn(object.getIntValue("expires_in"))
                .refreshToken(object.getString("refresh_token"))
                .build();
    }

}
