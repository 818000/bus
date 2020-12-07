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
package org.aoju.bus.starter.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.core.toolkit.EscapeKit;
import org.aoju.bus.core.toolkit.IoKit;
import org.aoju.bus.extra.json.JsonKit;
import org.aoju.bus.logger.Logger;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @author Kimi Liu
 * @version 6.1.3
 * @since JDK 1.8+
 */
public class CacheRequestWrapper extends HttpServletRequestWrapper {

    private static final byte[] DEFAULT_BYTE = Normal.EMPTY_BYTE_ARRAY;
    private byte[] body;
    private ServletInputStreamWrapper inputStreamWrapper;

    CacheRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 从ParameterMap获取参数，并保存以便多次获取
        Logger.info(Symbol.DELIM, JsonKit.toJsonString(request.getParameterMap()));
        // 从InputStream获取参数，并保存以便多次获取
        this.body = IoKit.readBytes(request.getInputStream());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body != null ? this.body : DEFAULT_BYTE);
        // 初始 ServletInputStreamWrapper
        this.inputStreamWrapper = new ServletInputStreamWrapper(byteArrayInputStream);
        // 设置 InputStream 到我们自己的包装类中
        this.inputStreamWrapper.setInputStream(byteArrayInputStream);
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() {
        return this.inputStreamWrapper;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.inputStreamWrapper));
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null || values.length <= 0) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = values[i];
            if (!JsonKit.isJson(values[i])) {
                encodedValues[i] = EscapeKit.escapeHtml4(values[i]);
            }
        }
        return encodedValues;
    }

    @Override
    public String getParameter(String name) {
        String content = super.getParameter(name);
        if (!JsonKit.isJson(content)) {
            content = EscapeKit.escapeHtml4(content);
        }
        return content;
    }

    @Override
    public String getHeader(String name) {
        String content = super.getHeader(name);
        if (!JsonKit.isJson(content)) {
            content = EscapeKit.escapeHtml4(content);
        }
        return content;
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    private static class ServletInputStreamWrapper extends ServletInputStream {

        private InputStream inputStream;

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }
    }

}
