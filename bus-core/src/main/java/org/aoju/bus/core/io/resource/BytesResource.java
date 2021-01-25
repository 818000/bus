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
package org.aoju.bus.core.io.resource;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.StringKit;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 基于byte[]的资源获取器
 * 注意：此对象中getUrl方法始终返回null
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
public class BytesResource implements Resource {

    private final byte[] bytes;
    private final String name;

    /**
     * 构造
     *
     * @param bytes 字节数组
     */
    public BytesResource(byte[] bytes) {
        this(bytes, null);
    }

    /**
     * 构造
     *
     * @param bytes 字节数组
     * @param name  资源名称
     */
    public BytesResource(byte[] bytes, String name) {
        this.bytes = bytes;
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public URL getUrl() {
        return null;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public String readString(Charset charset) throws InstrumentException {
        return StringKit.toString(this.bytes, charset);
    }

    @Override
    public byte[] readBytes() throws InstrumentException {
        return this.bytes;
    }

}
