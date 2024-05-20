/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2024 miaixz.org and other contributors.                    *
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
package org.miaixz.bus.crypto.builtin.symmetric;

import org.miaixz.bus.core.lang.Algorithm;
import org.miaixz.bus.core.lang.Assert;
import org.miaixz.bus.core.lang.Normal;
import org.miaixz.bus.core.lang.Optional;
import org.miaixz.bus.core.lang.exception.CryptoException;
import org.miaixz.bus.core.lang.exception.InternalException;
import org.miaixz.bus.core.toolkit.*;
import org.miaixz.bus.crypto.Keeper;
import org.miaixz.bus.crypto.Padding;
import org.miaixz.bus.crypto.builtin.SaltMagic;
import org.miaixz.bus.crypto.builtin.SaltParser;
import org.miaixz.bus.crypto.cipher.JceCipher;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对称加密算法
 * 在对称加密算法中，数据发信方将明文（原始数据）和加密密钥一起经过特殊加密算法处理后，使其变成复杂的加密密文发送出去。
 * 收信方收到密文后，若想解读原文，则需要使用加密用过的密钥及相同算法的逆算法对密文进行解密，才能使其恢复成可读明文。
 * 在对称加密算法中，使用的密钥只有一个，发收信双方都使用这个密钥对数据进行加密和解密，这就要求解密方事先必须知道加密密钥。
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class SymmetricCrypto implements SymmetricEncryptor, SymmetricDecryptor, Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 锁操作
     */
    private final Lock lock = new ReentrantLock();
    /**
     * 密码
     */
    private JceCipher cipher;
    /**
     * 算法参数
     */
    private AlgorithmParameterSpec algorithmParameterSpec;
    /**
     * 自定义随机数
     */
    private SecureRandom random;
    /**
     * SecretKey 负责保存对称密钥
     */
    private SecretKey secretKey;
    /**
     * 是否0填充
     */
    private boolean isZeroPadding;

    /**
     * 构造，使用随机密钥
     *
     * @param algorithm {@link Algorithm}
     */
    public SymmetricCrypto(final Algorithm algorithm) {
        this(algorithm, (byte[]) null);
    }

    /**
     * 构造，使用随机密钥
     *
     * @param algorithm 算法，可以是"algorithm/mode/padding"或者"algorithm"
     */
    public SymmetricCrypto(final String algorithm) {
        this(algorithm, (byte[]) null);
    }

    /**
     * 构造
     *
     * @param algorithm 算法 {@link Algorithm}
     * @param key       自定义KEY
     */
    public SymmetricCrypto(final Algorithm algorithm, final byte[] key) {
        this(algorithm.getValue(), key);
    }

    /**
     * 构造
     *
     * @param algorithm 算法 {@link Algorithm}
     * @param key       自定义KEY
     */
    public SymmetricCrypto(final Algorithm algorithm, final SecretKey key) {
        this(algorithm.getValue(), key);
    }

    /**
     * 构造
     *
     * @param algorithm 算法
     * @param key       密钥
     */
    public SymmetricCrypto(final String algorithm, final byte[] key) {
        this(algorithm, Keeper.generateKey(algorithm, key));
    }

    /**
     * 构造
     *
     * @param algorithm 算法
     * @param key       密钥
     */
    public SymmetricCrypto(final String algorithm, final SecretKey key) {
        this(algorithm, key, null);
    }

    /**
     * 构造
     *
     * @param algorithm  算法
     * @param key        密钥
     * @param paramsSpec 算法参数，例如加盐等
     */
    public SymmetricCrypto(final String algorithm, final SecretKey key, final AlgorithmParameterSpec paramsSpec) {
        init(algorithm, key);
        initParams(algorithm, paramsSpec);
    }

    /**
     * 拷贝解密后的流
     *
     * @param in        {@link CipherInputStream}
     * @param out       输出流
     * @param blockSize 块大小
     * @throws IOException IO异常
     */
    private static void copyForZeroPadding(final CipherInputStream in, final OutputStream out, final int blockSize) throws IOException {
        int n = 1;
        if (Normal.DEFAULT_BUFFER_SIZE > blockSize) {
            n = Math.max(n, Normal.DEFAULT_BUFFER_SIZE / blockSize);
        }
        // 此处缓存buffer使用blockSize的整数倍，方便读取时可以正好将补位的0读在一个buffer中
        final int bufSize = blockSize * n;
        final byte[] preBuffer = new byte[bufSize];
        final byte[] buffer = new byte[bufSize];

        boolean isFirst = true;
        int preReadSize = 0;
        for (int readSize; (readSize = in.read(buffer)) != IoKit.EOF; ) {
            if (isFirst) {
                isFirst = false;
            } else {
                // 将前一批数据写出
                out.write(preBuffer, 0, preReadSize);
            }
            ArrayKit.copy(buffer, preBuffer, readSize);
            preReadSize = readSize;
        }
        // 去掉末尾所有的补位0
        int i = preReadSize - 1;
        while (i >= 0 && 0 == preBuffer[i]) {
            i--;
        }
        out.write(preBuffer, 0, i + 1);
        out.flush();
    }

    /**
     * 初始化
     *
     * @param algorithm 算法
     * @param key       密钥，如果为{@code null}自动生成一个key
     * @return SymmetricCrypto的子对象，即子对象自身
     */
    public SymmetricCrypto init(String algorithm, final SecretKey key) {
        Assert.notBlank(algorithm, "'algorithm' must be not blank !");
        this.secretKey = key;

        // 检查是否为ZeroPadding，是则替换为NoPadding，并标记以便单独处理
        if (algorithm.contains(Padding.ZeroPadding.name())) {
            algorithm = StringKit.replace(algorithm, Padding.ZeroPadding.name(), Padding.NoPadding.name());
            this.isZeroPadding = true;
        }

        this.cipher = new JceCipher(algorithm);
        return this;
    }

    /**
     * 获得对称密钥
     *
     * @return 获得对称密钥
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * 获得加密或解密器
     *
     * @return 加密或解密
     */
    public Cipher getCipher() {
        return cipher.getRaw();
    }

    /**
     * 设置{@link AlgorithmParameterSpec}，通常用于加盐或偏移向量
     *
     * @param algorithmParameterSpec {@link AlgorithmParameterSpec}
     * @return this
     */
    public SymmetricCrypto setAlgorithmParameterSpec(final AlgorithmParameterSpec algorithmParameterSpec) {
        this.algorithmParameterSpec = algorithmParameterSpec;
        return this;
    }

    /**
     * 设置偏移向量
     *
     * @param iv {@link IvParameterSpec}偏移向量
     * @return 自身
     */
    public SymmetricCrypto setIv(final IvParameterSpec iv) {
        return setAlgorithmParameterSpec(iv);
    }

    /**
     * 设置偏移向量
     *
     * @param iv 偏移向量，加盐
     * @return 自身
     */
    public SymmetricCrypto setIv(final byte[] iv) {
        return setIv(new IvParameterSpec(iv));
    }

    /**
     * 设置随机数生成器，可自定义随机数种子
     *
     * @param random 随机数生成器，可自定义随机数种子
     * @return this
     */
    public SymmetricCrypto setRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }

    /**
     * 初始化模式并清空数据
     *
     * @param mode 模式枚举
     * @return this
     */
    public SymmetricCrypto setMode(final Algorithm.Type mode) {
        return setMode(mode, null);
    }

    /**
     * 初始化模式并清空数据
     *
     * @param mode 模式枚举
     * @param salt 加盐值，用于
     * @return this
     */
    public SymmetricCrypto setMode(final Algorithm.Type mode, final byte[] salt) {
        lock.lock();
        try {
            initMode(mode, salt);
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 更新数据，分组加密中间结果可以当作随机数
     * 第一次更新数据前需要调用{@link #setMode(Algorithm.Type)}初始化加密或解密模式，然后每次更新数据都是累加模式
     *
     * @param data 被加密的bytes
     * @return update之后的bytes
     */
    public byte[] update(final byte[] data) {
        final Cipher cipher = this.cipher.getRaw();
        lock.lock();
        try {
            return cipher.update(paddingDataWithZero(data, cipher.getBlockSize()));
        } catch (final Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 更新数据，分组加密中间结果可以当作随机数
     * 第一次更新数据前需要调用{@link #setMode(Algorithm.Type)}初始化加密或解密模式，然后每次更新数据都是累加模式
     *
     * @param data 被加密的bytes
     * @return update之后的hex数据
     */
    public String updateHex(final byte[] data) {
        return HexKit.encodeString(update(data));
    }

    @Override
    public byte[] encrypt(final byte[] data) {
        return encrypt(data, null);
    }

    /**
     * 加密
     *
     * @param data 被加密的bytes
     * @param salt 加盐值，如果为{@code null}不设置，否则生成带Salted__头的密文数据
     * @return 加密后的bytes
     */
    public byte[] encrypt(final byte[] data, final byte[] salt) {
        byte[] result;
        lock.lock();
        try {
            final JceCipher cipher = initMode(Algorithm.Type.ENCRYPT, salt);
            result = cipher.processFinal(paddingDataWithZero(data, cipher.getBlockSize()));
        } finally {
            lock.unlock();
        }
        return SaltMagic.addMagic(result, salt);
    }

    @Override
    public void encrypt(final InputStream data, final OutputStream out, final boolean isClose) throws InternalException {
        CipherOutputStream cipherOutputStream = null;
        lock.lock();
        try {
            final JceCipher cipher = initMode(Algorithm.Type.ENCRYPT, null);
            cipherOutputStream = new CipherOutputStream(out, cipher.getRaw());
            final long length = IoKit.copy(data, cipherOutputStream);
            if (this.isZeroPadding) {
                final int blockSize = cipher.getBlockSize();
                if (blockSize > 0) {
                    // 按照块拆分后的数据中多余的数据
                    final int remainLength = (int) (length % blockSize);
                    if (remainLength > 0) {
                        // 补充0
                        cipherOutputStream.write(new byte[blockSize - remainLength]);
                        cipherOutputStream.flush();
                    }
                }
            }
        } catch (final InternalException e) {
            throw e;
        } catch (final Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
            // CipherOutputStream必须关闭，才能完全写出
            IoKit.closeQuietly(cipherOutputStream);
            if (isClose) {
                IoKit.closeQuietly(data);
            }
        }
    }

    @Override
    public byte[] decrypt(final byte[] bytes) {
        final int blockSize;
        final byte[] decryptData;
        lock.lock();
        try {
            final byte[] salt = SaltMagic.getSalt(bytes);
            final JceCipher cipher = initMode(Algorithm.Type.DECRYPT, salt);
            blockSize = cipher.getBlockSize();
            decryptData = cipher.processFinal(SaltMagic.getData(bytes));
        } catch (final Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }

        return removePadding(decryptData, blockSize);
    }

    @Override
    public void decrypt(final InputStream data, final OutputStream out, final boolean isClose) throws InternalException {
        CipherInputStream cipherInputStream = null;
        lock.lock();
        try {
            final JceCipher cipher = initMode(Algorithm.Type.DECRYPT, null);
            cipherInputStream = new CipherInputStream(data, cipher.getRaw());
            if (this.isZeroPadding) {
                final int blockSize = cipher.getBlockSize();
                if (blockSize > 0) {
                    copyForZeroPadding(cipherInputStream, out, blockSize);
                    return;
                }
            }
            IoKit.copy(cipherInputStream, out);
        } catch (final IOException e) {
            throw new InternalException(e);
        } catch (final InternalException e) {
            throw e;
        } catch (final Exception e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
            // CipherOutputStream必须关闭，才能完全写出
            IoKit.closeQuietly(cipherInputStream);
            if (isClose) {
                IoKit.closeQuietly(data);
            }
        }
    }

    /**
     * 初始化加密解密参数，如IV等
     *
     * @param algorithm  算法
     * @param paramsSpec 用户定义的{@link AlgorithmParameterSpec}
     * @return this
     */
    private SymmetricCrypto initParams(final String algorithm, AlgorithmParameterSpec paramsSpec) {
        if (null == paramsSpec) {
            byte[] iv = Optional.ofNullable(cipher)
                    .map(JceCipher::getRaw).map(Cipher::getIV).get();

            // 随机IV
            if (StringKit.startWithIgnoreCase(algorithm, "PBE")) {
                // 对于PBE算法使用随机数加盐
                if (null == iv) {
                    iv = RandomKit.randomBytes(8);
                }
                paramsSpec = new PBEParameterSpec(iv, 100);
            } else if (StringKit.startWithIgnoreCase(algorithm, "AES")) {
                if (null != iv) {
                    //AES使用Cipher默认的随机盐
                    paramsSpec = new IvParameterSpec(iv);
                }
            }
        }

        return setAlgorithmParameterSpec(paramsSpec);
    }

    /**
     * 初始化{@link JceCipher}为加密或者解密模式
     *
     * @param mode 模式，见{@link Algorithm.Type#ENCRYPT} 或 {@link Algorithm.Type#DECRYPT}
     * @return {@link Cipher}
     */
    private JceCipher initMode(final Algorithm.Type mode, final byte[] salt) {
        SecretKey secretKey = this.secretKey;
        if (null != salt) {
            // /提供OpenSSL格式兼容支持
            final String algorithm = getCipher().getAlgorithm();
            final byte[][] keyAndIV = SaltParser.ofMd5(32, algorithm)
                    .getKeyAndIV(secretKey.getEncoded(), salt);
            secretKey = Keeper.generateKey(algorithm, keyAndIV[0]);
            if (ArrayKit.isNotEmpty(keyAndIV[1])) {
                setAlgorithmParameterSpec(new IvParameterSpec(keyAndIV[1]));
            }
        }

        final JceCipher cipher = this.cipher;
        cipher.init(mode,
                new JceCipher.JceParameters(secretKey, this.algorithmParameterSpec, this.random));
        return cipher;
    }

    /**
     * 数据按照blockSize的整数倍长度填充填充0
     * <p>
     * 在{@link Padding#ZeroPadding} 模式下，且数据长度不是blockSize的整数倍才有效，否则返回原数据
     *
     * <p>
     * 见：https://blog.csdn.net/OrangeJack/article/details/82913804
     *
     * @param data      数据
     * @param blockSize 块大小
     * @return 填充后的数据，如果isZeroPadding为false或长度刚好，返回原数据
     */
    private byte[] paddingDataWithZero(final byte[] data, final int blockSize) {
        if (this.isZeroPadding) {
            final int length = data.length;
            // 按照块拆分后的数据中多余的数据
            final int remainLength = length % blockSize;
            if (remainLength > 0) {
                // 新长度为blockSize的整数倍，多余部分填充0
                return ArrayKit.resize(data, length + blockSize - remainLength);
            }
        }
        return data;
    }

    /**
     * 数据按照blockSize去除填充部分，用于解密
     * 在{@link Padding#ZeroPadding} 模式下，且数据长度不是blockSize的整数倍才有效，否则返回原数据
     *
     * @param data      数据
     * @param blockSize 块大小，必须大于0
     * @return 去除填充后的数据，如果isZeroPadding为false或长度刚好，返回原数据
     */
    private byte[] removePadding(final byte[] data, final int blockSize) {
        if (this.isZeroPadding && blockSize > 0) {
            final int length = data.length;
            final int remainLength = length % blockSize;
            if (remainLength == 0) {
                // 解码后的数据正好是块大小的整数倍，说明可能存在补0的情况，去掉末尾所有的0
                int i = length - 1;
                while (i >= 0 && 0 == data[i]) {
                    i--;
                }
                return ArrayKit.resize(data, i + 1);
            }
        }
        return data;
    }

}
