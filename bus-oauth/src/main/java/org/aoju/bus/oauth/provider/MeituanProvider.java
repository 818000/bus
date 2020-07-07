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
package org.aoju.bus.oauth.provider;

import com.alibaba.fastjson.JSONObject;
import org.aoju.bus.cache.metric.ExtendCache;
import org.aoju.bus.core.lang.Normal;
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
 * 美团登录
 *
 * @author Kimi Liu
 * @version 6.0.2
 * @since JDK 1.8+
 */
public class MeituanProvider extends AbstractProvider {

    public MeituanProvider(Context context) {
        super(context, Registry.MEITUAN);
    }

    public MeituanProvider(Context context, ExtendCache extendCache) {
        super(context, Registry.MEITUAN, extendCache);
    }

    @Override
    public AccToken getAccessToken(Callback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", context.getAppKey());
        params.put("secret", context.getAppSecret());
        params.put("code", callback.getCode());
        params.put("grant_type", "authorization_code");

        String response = Httpx.post(source.accessToken(), params);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return AccToken.builder()
                .accessToken(object.getString("access_token"))
                .refreshToken(object.getString("refresh_token"))
                .expireIn(object.getIntValue("expires_in"))
                .build();
    }

    @Override
    public Property getUserInfo(AccToken accToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", context.getAppKey());
        params.put("secret", context.getAppSecret());
        params.put("access_token", accToken.getAccessToken());

        String response = Httpx.post(source.refresh(), params);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return Property.builder()
                .rawJson(object)
                .uuid(object.getString("openid"))
                .username(object.getString("nickname"))
                .nickname(object.getString("nickname"))
                .avatar(object.getString("avatar"))
                .gender(Normal.Gender.UNKNOWN)
                .token(accToken)
                .source(source.toString())
                .build();
    }

    @Override
    public Message refresh(AccToken accToken) {
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", context.getAppKey());
        params.put("secret", context.getAppSecret());
        params.put("refresh_token", accToken.getRefreshToken());
        params.put("grant_type", "refresh_token");

        String response = Httpx.post(source.refresh(), params);
        JSONObject object = JSONObject.parseObject(response);

        this.checkResponse(object);

        return Message.builder()
                .errcode(Builder.ErrorCode.SUCCESS.getCode())
                .data(AccToken.builder()
                        .accessToken(object.getString("access_token"))
                        .refreshToken(object.getString("refresh_token"))
                        .expireIn(object.getIntValue("expires_in"))
                        .build())
                .build();
    }

    @Override
    public String authorize(String state) {
        return Builder.fromUrl(source.authorize())
                .queryParam("response_type", "code")
                .queryParam("app_id", context.getAppKey())
                .queryParam("redirect_uri", context.getRedirectUri())
                .queryParam("state", getRealState(state))
                .queryParam("scope", Normal.EMPTY)
                .build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("error_code")) {
            throw new AuthorizedException(object.getString("erroe_msg"));
        }
    }

}
