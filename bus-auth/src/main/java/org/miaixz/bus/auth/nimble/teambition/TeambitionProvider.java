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
package org.miaixz.bus.auth.nimble.teambition;

import org.miaixz.bus.auth.magic.AuthToken;
import org.miaixz.bus.cache.CacheX;
import org.miaixz.bus.core.basic.entity.Message;
import org.miaixz.bus.core.lang.Gender;
import org.miaixz.bus.core.lang.exception.AuthorizedException;
import org.miaixz.bus.extra.json.JsonKit;
import org.miaixz.bus.http.Httpx;
import org.miaixz.bus.auth.Context;
import org.miaixz.bus.auth.Registry;
import org.miaixz.bus.auth.magic.Callback;
import org.miaixz.bus.auth.magic.ErrorCode;
import org.miaixz.bus.auth.magic.Material;
import org.miaixz.bus.auth.nimble.AbstractProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Teambition 登录
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class TeambitionProvider extends AbstractProvider {

    public TeambitionProvider(Context context) {
        super(context, Registry.TEAMBITION);
    }

    public TeambitionProvider(Context context, CacheX cache) {
        super(context, Registry.TEAMBITION, cache);
    }

    /**
     * @param callback 回调返回的参数
     * @return 所有信息
     */
    @Override
    public AuthToken getAccessToken(Callback callback) {
        Map<String, String> form = new HashMap<>(7);
        form.put("client_id", context.getAppKey());
        form.put("client_secret", context.getAppSecret());
        form.put("code", callback.getCode());
        form.put("grant_type", "code");

        String response = Httpx.post(this.complex.accessToken(), form);
        Map<String, Object> accessTokenObject = JsonKit.toPojo(response, Map.class);

        this.checkResponse(accessTokenObject);

        return AuthToken.builder().accessToken((String) accessTokenObject.get("access_token"))
                .refreshToken((String) accessTokenObject.get("refresh_token")).build();
    }

    @Override
    public Material getUserInfo(AuthToken authToken) {
        String accessToken = authToken.getAccessToken();
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "OAuth2 " + accessToken);

        String response = Httpx.get(this.complex.userinfo(), null, header);
        Map<String, Object> object = JsonKit.toPojo(response, Map.class);

        this.checkResponse(object);

        authToken.setUid((String) object.get("_id"));

        return Material.builder().rawJson(JsonKit.toJsonString(object)).uuid((String) object.get("_id"))
                .username((String) object.get("name")).nickname((String) object.get("name"))
                .avatar((String) object.get("avatarUrl")).blog((String) object.get("website"))
                .location((String) object.get("location")).email((String) object.get("email")).gender(Gender.UNKNOWN)
                .token(authToken).source(complex.toString()).build();
    }

    @Override
    public Message refresh(AuthToken authToken) {
        String uid = authToken.getUid();
        String refreshToken = authToken.getRefreshToken();

        Map<String, String> form = new HashMap<>(4);
        form.put("_userId", uid);
        form.put("refresh_token", refreshToken);
        String response = Httpx.post(this.complex.refresh(), form);
        Map<String, Object> refreshTokenObject = JsonKit.toPojo(response, Map.class);

        this.checkResponse(refreshTokenObject);

        return Message.builder().errcode(ErrorCode._SUCCESS.getKey())
                .data(
                        AuthToken.builder().accessToken((String) refreshTokenObject.get("access_token"))
                                .refreshToken((String) refreshTokenObject.get("refresh_token")).build())
                .build();
    }

    /**
     * 检查响应内容是否正确
     *
     * @param object 请求响应内容
     */
    private void checkResponse(Map<String, Object> object) {
        if (object.containsKey("message") && object.containsKey("name")) {
            throw new AuthorizedException((String) object.get("name") + ", " + (String) object.get("message"));
        }
    }

}
