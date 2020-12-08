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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.oauth.provider;

import com.alibaba.fastjson.JSONObject;
import org.aoju.bus.cache.metric.ExtendCache;
import org.aoju.bus.core.lang.Charset;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.lang.exception.AuthorizedException;
import org.aoju.bus.core.toolkit.UriKit;
import org.aoju.bus.http.Httpx;
import org.aoju.bus.oauth.Builder;
import org.aoju.bus.oauth.Context;
import org.aoju.bus.oauth.Registry;
import org.aoju.bus.oauth.magic.AccToken;
import org.aoju.bus.oauth.magic.Callback;
import org.aoju.bus.oauth.magic.Property;
import org.aoju.bus.oauth.metric.OauthScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Github登录
 *
 * @author Kimi Liu
 * @version 6.1.5
 * @since JDK 1.8+
 */
public class GithubProvider extends AbstractProvider {

    public GithubProvider(Context context) {
        super(context, Registry.GITHUB);
    }

    public GithubProvider(Context context, ExtendCache extendCache) {
        super(context, Registry.GITHUB, extendCache);
    }

    @Override
    public AccToken getAccessToken(Callback callback) {

        Map<String, String> paramMap = UriKit.decodeVal(doPostAuthorizationCode(callback.getCode()), Charset.DEFAULT_UTF_8);

        this.checkResponse(paramMap.containsKey("error"), paramMap.get("error_description"));

        return AccToken.builder()
                .accessToken(paramMap.get("access_token"))
                .scope(paramMap.get("scope"))
                .tokenType(paramMap.get("token_type"))
                .build();
    }

    @Override
    public Property getUserInfo(AccToken accToken) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "token " + accToken.getAccessToken());

        JSONObject object = JSONObject.parseObject(Httpx.get(Builder.fromUrl(source.userInfo()).build(), null, header));

        this.checkResponse(object.containsKey("error"), object.getString("error_description"));

        return Property.builder()
                .rawJson(object)
                .uuid(object.getString("id"))
                .username(object.getString("login"))
                .avatar(object.getString("avatar_url"))
                .blog(object.getString("blog"))
                .nickname(object.getString("name"))
                .company(object.getString("company"))
                .location(object.getString("location"))
                .email(object.getString("email"))
                .remark(object.getString("bio"))
                .gender(Normal.Gender.UNKNOWN)
                .token(accToken)
                .source(source.toString())
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
        return Builder.fromUrl(super.authorize(state))
                .queryParam("scope", this.getScopes(Symbol.SPACE, true, getScopes(true, OauthScope.Github.values())))
                .build();
    }

    private void checkResponse(boolean error, String error_description) {
        if (error) {
            throw new AuthorizedException(error_description);
        }
    }

}
