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
package org.aoju.bus.http.secure;

import org.aoju.bus.core.lang.Charset;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * RFC 7235兼容的认证
 *
 * @author Kimi Liu
 * @version 6.0.0
 * @since JDK 1.8+
 */
public final class Challenge {

    /**
     * 身份验证方案，如{@code Basic}
     */
    private final String scheme;
    /**
     * 返回验证参数，包括{@code realm}和{@code charset}(如果存在的话)，
     * 但以字符串的形式返回。映射的键是小写的，应该不区分大小写
     */
    private final Map<String, String> authParams;

    public Challenge(String scheme, Map<String, String> authParams) {
        if (scheme == null) throw new NullPointerException("scheme == null");
        if (authParams == null) throw new NullPointerException("authParams == null");
        this.scheme = scheme;
        Map<String, String> newAuthParams = new LinkedHashMap<>();
        for (Entry<String, String> authParam : authParams.entrySet()) {
            String key = (authParam.getKey() == null) ? null : authParam.getKey().toLowerCase(Locale.US);
            newAuthParams.put(key, authParam.getValue());
        }
        this.authParams = Collections.unmodifiableMap(newAuthParams);
    }

    public Challenge(String scheme, String realm) {
        if (scheme == null) throw new NullPointerException("scheme == null");
        if (realm == null) throw new NullPointerException("realm == null");
        this.scheme = scheme;
        this.authParams = Collections.singletonMap("realm", realm);
    }

    /**
     * 该副本需要使用{@code charset}编码的凭据.
     *
     * @param charset 字符集
     * @return 返回此字符集的副本
     */
    public Challenge withCharset(java.nio.charset.Charset charset) {
        if (charset == null) throw new NullPointerException("charset == null");
        Map<String, String> authParams = new LinkedHashMap<>(this.authParams);
        authParams.put("charset", charset.name());
        return new Challenge(scheme, authParams);
    }

    public String scheme() {
        return scheme;
    }

    public Map<String, String> authParams() {
        return authParams;
    }

    public String realm() {
        return authParams.get("realm");
    }

    public java.nio.charset.Charset charset() {
        String charset = authParams.get("charset");
        if (charset != null) {
            try {
                return java.nio.charset.Charset.forName(charset);
            } catch (Exception ignore) {
            }
        }
        return Charset.ISO_8859_1;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Challenge
                && ((Challenge) other).scheme.equals(scheme)
                && ((Challenge) other).authParams.equals(authParams);
    }

    @Override
    public int hashCode() {
        int result = 29;
        result = 31 * result + scheme.hashCode();
        result = 31 * result + authParams.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return scheme + " authParams=" + authParams;
    }

}
