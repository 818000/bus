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
package org.miaixz.bus.auth.nimble.aliyun;

import org.miaixz.bus.auth.magic.AuthToken;
import org.miaixz.bus.cache.CacheX;
import org.miaixz.bus.core.lang.Gender;
import org.miaixz.bus.extra.json.JsonKit;
import org.miaixz.bus.auth.Context;
import org.miaixz.bus.auth.Registry;
import org.miaixz.bus.auth.magic.Callback;
import org.miaixz.bus.auth.magic.Material;
import org.miaixz.bus.auth.nimble.AbstractProvider;

import java.util.Map;

/**
 * 阿里云登录
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class AliyunProvider extends AbstractProvider {

    public AliyunProvider(Context context) {
        super(context, Registry.ALIYUN);
    }

    public AliyunProvider(Context context, CacheX cache) {
        super(context, Registry.ALIYUN, cache);
    }

    @Override
    public AuthToken getAccessToken(Callback callback) {
        String response = doPostAuthorizationCode(callback.getCode());
        Map<String, Object> accessTokenObject = JsonKit.toPojo(response, Map.class);
        return AuthToken.builder().accessToken((String) accessTokenObject.get("access_token"))
                .expireIn(((Number) accessTokenObject.get("expires_in")).intValue())
                .tokenType((String) accessTokenObject.get("token_type"))
                .idToken((String) accessTokenObject.get("id_token"))
                .refreshToken((String) accessTokenObject.get("refresh_token")).build();
    }

    @Override
    public Material getUserInfo(AuthToken authToken) {
        String userInfo = doGetUserInfo(authToken);
        Map<String, Object> object = JsonKit.toPojo(userInfo, Map.class);
        return Material.builder().rawJson(JsonKit.toJsonString(object)).uuid((String) object.get("sub"))
                .username((String) object.get("login_name")).nickname((String) object.get("name"))
                .gender(Gender.UNKNOWN).token(authToken).source(complex.toString()).build();
    }

}
