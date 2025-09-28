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
package org.miaixz.bus.auth.nimble.meituan;

import java.util.HashMap;
import java.util.Map;

import org.miaixz.bus.auth.magic.AuthToken;
import org.miaixz.bus.cache.CacheX;
import org.miaixz.bus.core.basic.entity.Message;
import org.miaixz.bus.core.lang.Gender;
import org.miaixz.bus.core.lang.exception.AuthorizedException;
import org.miaixz.bus.extra.json.JsonKit;
import org.miaixz.bus.http.Httpx;
import org.miaixz.bus.auth.Builder;
import org.miaixz.bus.auth.Context;
import org.miaixz.bus.auth.Registry;
import org.miaixz.bus.auth.magic.Callback;
import org.miaixz.bus.auth.magic.ErrorCode;
import org.miaixz.bus.auth.magic.Material;
import org.miaixz.bus.auth.nimble.AbstractProvider;

/**
 * 美团 登录
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class MeituanProvider extends AbstractProvider {

    public MeituanProvider(Context context) {
        super(context, Registry.MEITUAN);
    }

    public MeituanProvider(Context context, CacheX cache) {
        super(context, Registry.MEITUAN, cache);
    }

    @Override
    public AuthToken getAccessToken(Callback callback) {
        Map<String, String> form = new HashMap<>(7);
        form.put("app_id", context.getAppKey());
        form.put("secret", context.getAppSecret());
        form.put("code", callback.getCode());
        form.put("grant_type", "authorization_code");

        String response = Httpx.post(this.complex.accessToken(), form);
        try {
            Map<String, Object> object = JsonKit.toPojo(response, Map.class);
            if (object == null) {
                throw new AuthorizedException("Failed to parse access token response: empty response");
            }

            this.checkResponse(object);

            String accessToken = (String) object.get("access_token");
            if (accessToken == null) {
                throw new AuthorizedException("Missing access_token in response");
            }
            String refreshToken = (String) object.get("refresh_token");
            Object expiresInObj = object.get("expires_in");
            int expiresIn = expiresInObj instanceof Number ? ((Number) expiresInObj).intValue() : 0;

            return AuthToken.builder().accessToken(accessToken).refreshToken(refreshToken).expireIn(expiresIn).build();
        } catch (Exception e) {
            throw new AuthorizedException("Failed to parse access token response: " + e.getMessage());
        }
    }

    @Override
    public Material getUserInfo(AuthToken authToken) {
        Map<String, String> form = new HashMap<>(5);
        form.put("app_id", context.getAppKey());
        form.put("secret", context.getAppSecret());
        form.put("access_token", authToken.getAccessToken());

        String response = Httpx.post(this.complex.userinfo(), form);
        try {
            Map<String, Object> object = JsonKit.toPojo(response, Map.class);
            if (object == null) {
                throw new AuthorizedException("Failed to parse user info response: empty response");
            }

            this.checkResponse(object);

            String openid = (String) object.get("openid");
            if (openid == null) {
                throw new AuthorizedException("Missing openid in user info response");
            }
            String nickname = (String) object.get("nickname");
            String avatar = (String) object.get("avatar");

            return Material.builder().rawJson(JsonKit.toJsonString(object)).uuid(openid).username(nickname)
                    .nickname(nickname).avatar(avatar).gender(Gender.UNKNOWN).token(authToken)
                    .source(complex.toString()).build();
        } catch (Exception e) {
            throw new AuthorizedException("Failed to parse user info response: " + e.getMessage());
        }
    }

    @Override
    public Message refresh(AuthToken authToken) {
        Map<String, String> form = new HashMap<>(7);
        form.put("app_id", context.getAppKey());
        form.put("secret", context.getAppSecret());
        form.put("refresh_token", authToken.getRefreshToken());
        form.put("grant_type", "refresh_token");

        String response = Httpx.post(this.complex.refresh(), form);
        try {
            Map<String, Object> object = JsonKit.toPojo(response, Map.class);
            if (object == null) {
                throw new AuthorizedException("Failed to parse refresh token response: empty response");
            }

            this.checkResponse(object);

            String accessToken = (String) object.get("access_token");
            if (accessToken == null) {
                throw new AuthorizedException("Missing access_token in refresh response");
            }
            String refreshToken = (String) object.get("refresh_token");
            Object expiresInObj = object.get("expires_in");
            int expiresIn = expiresInObj instanceof Number ? ((Number) expiresInObj).intValue() : 0;

            return Message.builder().errcode(ErrorCode._SUCCESS.getKey()).data(
                    AuthToken.builder().accessToken(accessToken).refreshToken(refreshToken).expireIn(expiresIn).build())
                    .build();
        } catch (Exception e) {
            throw new AuthorizedException("Failed to parse refresh token response: " + e.getMessage());
        }
    }

    private void checkResponse(Map<String, Object> object) {
        if (object.containsKey("error_code")) {
            String errorMsg = (String) object.get("erroe_msg"); // 注意原代码中的拼写错误 "erroe_msg"
            throw new AuthorizedException(errorMsg != null ? errorMsg : "Unknown error");
        }
    }

    @Override
    public String authorize(String state) {
        return Builder.fromUrl(super.authorize(state)).queryParam("scope", "").build();
    }

}
