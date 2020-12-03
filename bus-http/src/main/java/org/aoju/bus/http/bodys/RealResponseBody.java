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
package org.aoju.bus.http.bodys;

import org.aoju.bus.core.io.BufferSource;
import org.aoju.bus.core.lang.MediaType;

/**
 * 响应体只能使用一次
 *
 * @author Kimi Liu
 * @version 6.1.3
 * @since JDK 1.8+
 */
public final class RealResponseBody extends ResponseBody {

    /**
     * 使用字符串避免在需要时才解析内容类型。这也避免了由格式不正确的内容类型引起的问题.
     */
    private final String contentTypeString;
    private final long contentLength;
    private final BufferSource source;

    public RealResponseBody(
            String contentTypeString, long contentLength, BufferSource source) {
        this.contentTypeString = contentTypeString;
        this.contentLength = contentLength;
        this.source = source;
    }

    @Override
    public MediaType contentType() {
        return contentTypeString != null ? MediaType.valueOf(contentTypeString) : null;
    }

    @Override
    public long contentLength() {
        return contentLength;
    }

    @Override
    public BufferSource source() {
        return source;
    }

}
