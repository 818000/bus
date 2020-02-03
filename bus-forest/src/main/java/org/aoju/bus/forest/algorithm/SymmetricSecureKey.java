/*
 * The MIT License
 *
 * Copyright (c) 2015-2020 aoju.org All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.aoju.bus.forest.algorithm;

/**
 * 对称密钥
 *
 * @author Kimi Liu
 * @version 5.5.5
 * @since JDK 1.8+
 */
public final class SymmetricSecureKey extends SecureKey implements SymmetricKey {

    private final byte[] secretKey;
    private final byte[] iv;

    public SymmetricSecureKey(String algorithm, int keysize, int ivsize, String password, byte[] key, byte[] iv) {
        super(algorithm, keysize, ivsize, password);
        this.secretKey = key;
        this.iv = iv;
    }

    public byte[] getEncryptKey() {
        return secretKey;
    }

    public byte[] getDecryptKey() {
        return secretKey;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public byte[] getIvParameter() {
        return iv;
    }

}
