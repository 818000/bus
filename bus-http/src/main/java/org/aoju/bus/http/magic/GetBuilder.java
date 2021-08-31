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
package org.aoju.bus.http.magic;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.http.Httpd;

import java.util.Map;

/**
 * GET参数构造器
 *
 * @author Kimi Liu
 * @version 6.2.8
 * @since JDK 1.8+
 */
public class GetBuilder extends RequestBuilder<GetBuilder> {

    public GetBuilder(Httpd httpd) {
        super(httpd);
    }

    @Override
    public RequestCall build() {
        if (null != params) {
            url = appendParams(url, params);
        }
        return new GetRequest(url, tag, params, headers, id).build(httpd);
    }

    protected String appendParams(String url, Map<String, String> params) {
        if (null == url || null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder builder = new StringBuilder();
        params.forEach((k, v) -> {
            if (builder.length() == 0) {
                builder.append(Symbol.QUESTION_MARK);
            } else if (builder.length() > 0) {
                builder.append(Symbol.AND);
            }
            builder.append(k);
            builder.append(Symbol.EQUAL).append(v);
        });
        return url + builder.toString();
    }

}
