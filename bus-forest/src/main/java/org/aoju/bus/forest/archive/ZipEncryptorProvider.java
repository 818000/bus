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
package org.aoju.bus.forest.archive;

import org.aoju.bus.forest.Builder;
import org.aoju.bus.forest.Complex;
import org.aoju.bus.forest.algorithm.Key;
import org.aoju.bus.forest.provider.EncryptorProvider;
import org.aoju.bus.forest.provider.EntryEncryptorProvider;
import org.aoju.bus.forest.streams.AlwaysOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.util.zip.Deflater;

/**
 * ZIP压缩包加密器
 *
 * @author Kimi Liu
 * @version 6.1.3
 * @since JDK 1.8+
 */
public class ZipEncryptorProvider extends EntryEncryptorProvider<ZipArchiveEntry> implements EncryptorProvider {
    private final int level;

    public ZipEncryptorProvider(EncryptorProvider encryptorProvider) {
        this(encryptorProvider, null);
    }

    public ZipEncryptorProvider(EncryptorProvider encryptorProvider, Complex<ZipArchiveEntry> filter) {
        this(encryptorProvider, Deflater.DEFLATED, filter);
    }

    public ZipEncryptorProvider(EncryptorProvider encryptorProvider, int level) {
        this(encryptorProvider, level, null);
    }

    public ZipEncryptorProvider(EncryptorProvider encryptorProvider, int level, Complex<ZipArchiveEntry> filter) {
        super(encryptorProvider, filter);
        this.level = level;
    }

    @Override
    public void encrypt(Key key, File src, File dest) throws IOException {
        try (
                FileInputStream fis = new FileInputStream(src);
                FileOutputStream fos = new FileOutputStream(dest)
        ) {
            encrypt(key, fis, fos);
        }
    }

    @Override
    public void encrypt(Key key, InputStream in, OutputStream out) throws IOException {
        ZipArchiveInputStream zis = null;
        ZipArchiveOutputStream zos = null;
        try {
            zis = new ZipArchiveInputStream(in);
            zos = new ZipArchiveOutputStream(out);
            zos.setLevel(level);
            AlwaysOutputStream nos = new AlwaysOutputStream(zos);
            ZipArchiveEntry entry;
            while ((entry = zis.getNextZipEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                zos.putArchiveEntry(new ZipArchiveEntry(entry.getName()));
                EncryptorProvider encryptor = on(entry) ? this : xNopEncryptor;
                try (OutputStream eos = encryptor.encrypt(key, nos)) {
                    Builder.transfer(zis, eos);
                }
                zos.closeArchiveEntry();
            }
            zos.finish();
        } finally {
            Builder.close(zis);
            Builder.close(zos);
        }
    }
}
