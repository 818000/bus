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
package org.aoju.bus.forest.boot;

import org.aoju.bus.forest.Builder;
import org.aoju.bus.forest.algorithm.Key;
import org.aoju.bus.forest.provider.DecryptorProvider;
import org.aoju.bus.forest.provider.EncryptorProvider;
import org.springframework.boot.loader.jar.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 加密的URL处理器
 *
 * @author Kimi Liu
 * @version 5.9.3
 * @since JDK 1.8+
 */
public class BootURLHandler extends Handler {

    private final DecryptorProvider decryptorProvider;
    private final EncryptorProvider encryptorProvider;
    private final Key key;
    private final Set<String> indexes;

    public BootURLHandler(DecryptorProvider decryptorProvider, EncryptorProvider encryptorProvider, Key key, ClassLoader classLoader) throws Exception {
        this.decryptorProvider = decryptorProvider;
        this.encryptorProvider = encryptorProvider;
        this.key = key;
        this.indexes = new LinkedHashSet<>();
        Enumeration<URL> resources = classLoader.getResources(Builder.XJAR_INF_DIR + Builder.XJAR_INF_IDX);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String url = resource.toString();
            String classpath = url.substring(0, url.lastIndexOf("!/") + 2);
            InputStream in = resource.openStream();
            InputStreamReader isr = new InputStreamReader(in);
            LineNumberReader lnr = new LineNumberReader(isr);
            String name;
            while ((name = lnr.readLine()) != null) indexes.add(classpath + name);
        }
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        URLConnection urlConnection = super.openConnection(url);
        return indexes.contains(url.toString())
                && urlConnection instanceof JarURLConnection
                ? new BootURLConnection((JarURLConnection) urlConnection, decryptorProvider, encryptorProvider, key)
                : urlConnection;
    }

}
