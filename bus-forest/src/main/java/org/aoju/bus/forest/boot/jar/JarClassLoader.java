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
package org.aoju.bus.forest.boot.jar;

import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.forest.Builder;
import org.aoju.bus.forest.algorithm.Key;
import org.aoju.bus.forest.provider.DecryptorProvider;
import org.aoju.bus.forest.provider.EncryptorProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

/**
 * JAR包类加载器
 *
 * @author Kimi Liu
 * @version 6.0.9
 * @since JDK 1.8+
 */
public class JarClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    private final JarURLHandler jarURLHandler;

    public JarClassLoader(URL[] urls, ClassLoader parent, DecryptorProvider decryptorProvider, EncryptorProvider encryptorProvider, Key key) throws Exception {
        super(urls, parent);
        this.jarURLHandler = new JarURLHandler(decryptorProvider, encryptorProvider, key, this);
    }

    @Override
    public URL findResource(String name) {
        URL url = super.findResource(name);
        if (url == null) {
            return null;
        }
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile(), jarURLHandler);
        } catch (MalformedURLException e) {
            return url;
        }
    }

    @Override
    public Enumeration<URL> findResources(String name) throws IOException {
        Enumeration<URL> enumeration = super.findResources(name);
        if (enumeration == null) {
            return null;
        }
        return new XJarEnumeration(enumeration);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return super.findClass(name);
        } catch (ClassFormatError e) {
            URL resource = findResource(name.replace(Symbol.C_DOT, Symbol.C_SLASH) + ".class");
            if (resource == null) {
                throw new ClassNotFoundException(name, e);
            }
            try (InputStream in = resource.openStream()) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Builder.transfer(in, bos);
                byte[] bytes = bos.toByteArray();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Throwable t) {
                throw new ClassNotFoundException(name, t);
            }
        }
    }

    private class XJarEnumeration implements Enumeration<URL> {
        private final Enumeration<URL> enumeration;

        XJarEnumeration(Enumeration<URL> enumeration) {
            this.enumeration = enumeration;
        }

        @Override
        public boolean hasMoreElements() {
            return enumeration.hasMoreElements();
        }

        @Override
        public URL nextElement() {
            URL url = enumeration.nextElement();
            if (url == null) {
                return null;
            }
            try {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile(), jarURLHandler);
            } catch (MalformedURLException e) {
                return url;
            }
        }
    }

}
