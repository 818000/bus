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
package org.aoju.bus.core.io.resource;

import java.io.File;
import java.util.Collection;

/**
 * 多文件组合资源
 * 此资源为一个利用游标自循环资源,只有调用{@link #next()} 方法才会获取下一个资源,使用完毕后调用{@link #reset()}方法重置游标
 *
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
public class MultiFileResource extends MultiResource {

    /**
     * 构造
     *
     * @param files 集合
     */
    public MultiFileResource(Collection<File> files) {
        super();
        add(files);
    }

    /**
     * 构造
     *
     * @param file 文件
     */
    public MultiFileResource(File... file) {
        super();
        add(file);
    }

    /**
     * 增加文件资源
     *
     * @param file 文件资源
     * @return this
     */
    public MultiFileResource add(File... file) {
        for (File f : file) {
            this.add(new FileResource(f));
        }
        return this;
    }

    /**
     * 增加文件资源
     *
     * @param files 文件资源
     * @return this
     */
    public MultiFileResource add(Collection<File> files) {
        for (File file : files) {
            this.add(new FileResource(file));
        }
        return this;
    }

    @Override
    public MultiFileResource add(Resource resource) {
        return (MultiFileResource) super.add(resource);
    }

}
