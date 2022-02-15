/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.core.loader;

import org.aoju.bus.core.io.resource.Resource;
import org.aoju.bus.core.lang.Symbol;

import java.io.IOException;
import java.util.Enumeration;

/**
 * 包名表达式资源加载器
 *
 * @author Kimi Liu
 * @version 6.3.5
 * @since JDK 1.8+
 */
public class PkgLoader extends DelegateLoader implements Loader {

    public PkgLoader() {
        this(new StdLoader());
    }

    public PkgLoader(ClassLoader classLoader) {
        this(new StdLoader(classLoader));
    }

    public PkgLoader(Loader delegate) {
        super(delegate);
    }

    public Enumeration<Resource> load(String pkg, boolean recursively, Filter filter) throws IOException {
        String path = pkg.replace(Symbol.C_DOT, Symbol.C_SLASH);
        return delegate.load(path, recursively, filter);
    }

}
