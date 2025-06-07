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
package org.miaixz.bus.core.io.resource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.xyz.ListKit;

/**
 * 多资源组合资源 此资源为一个利用游标自循环资源，只有调用{@link #next()} 方法才会获取下一个资源，使用完毕后调用{@link #reset()}方法重置游标
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class MultiResource implements Resource, Iterable<Resource>, Iterator<Resource>, Serializable {

    @Serial
    private static final long serialVersionUID = 2852232202695L;

    private final List<Resource> resources;
    private int cursor;

    /**
     * 构造
     *
     * @param resources 资源数组
     */
    public MultiResource(final Resource... resources) {
        this(ListKit.of(resources));
    }

    /**
     * 构造
     *
     * @param resources 资源列表
     */
    public MultiResource(final Collection<Resource> resources) {
        if (resources instanceof List) {
            this.resources = (List<Resource>) resources;
        } else {
            this.resources = ListKit.of(resources);
        }
    }

    @Override
    public String getName() {
        return resources.get(cursor).getName();
    }

    @Override
    public URL getUrl() {
        return resources.get(cursor).getUrl();
    }

    @Override
    public long size() {
        return resources.get(cursor).size();
    }

    @Override
    public InputStream getStream() {
        return resources.get(cursor).getStream();
    }

    @Override
    public boolean isModified() {
        return resources.get(cursor).isModified();
    }

    @Override
    public BufferedReader getReader(final Charset charset) {
        return resources.get(cursor).getReader(charset);
    }

    @Override
    public String readString(final Charset charset) throws InternalException {
        return resources.get(cursor).readString(charset);
    }

    @Override
    public String readString() throws InternalException {
        return resources.get(cursor).readString();
    }

    @Override
    public byte[] readBytes() throws InternalException {
        return resources.get(cursor).readBytes();
    }

    @Override
    public Iterator<Resource> iterator() {
        return resources.iterator();
    }

    @Override
    public boolean hasNext() {
        return cursor < resources.size();
    }

    @Override
    public synchronized Resource next() {
        if (cursor >= resources.size()) {
            throw new ConcurrentModificationException();
        }
        this.cursor++;
        return this;
    }

    @Override
    public void remove() {
        this.resources.remove(this.cursor);
    }

    /**
     * 重置游标
     */
    public synchronized void reset() {
        this.cursor = 0;
    }

    /**
     * 增加资源
     *
     * @param resource 资源
     * @return this
     */
    public MultiResource add(final Resource resource) {
        this.resources.add(resource);
        return this;
    }

    /**
     * 增加多个资源
     *
     * @param iterable 资源列表
     * @return this
     */
    public MultiResource addAll(final Iterable<? extends Resource> iterable) {
        iterable.forEach((this::add));
        return this;
    }

}
