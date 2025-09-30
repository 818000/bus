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
package org.miaixz.bus.crypto.center;

import java.io.InputStream;
import java.io.Serial;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSAEncoding;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.crypto.signers.StandardDSAEncoding;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;
import org.miaixz.bus.core.lang.Algorithm;
import org.miaixz.bus.core.lang.Assert;
import org.miaixz.bus.core.lang.exception.CryptoException;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.core.xyz.HexKit;
import org.miaixz.bus.crypto.Builder;
import org.miaixz.bus.crypto.Keeper;
import org.miaixz.bus.crypto.builtin.asymmetric.AbstractCrypto;
import org.miaixz.bus.crypto.builtin.asymmetric.KeyType;

/**
 * 国密SM2非对称算法实现，基于BC库 SM2算法只支持公钥加密，私钥解密 参考：https://blog.csdn.net/pridas/article/details/86118774
 *
 * <p>
 * 国密算法包括：
 * <ol>
 * <li>非对称加密和签名：SM2，asymmetric</li>
 * <li>摘要签名算法：SM3，digest</li>
 * <li>对称加密：SM4，symmetric</li>
 * </ol>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class SM2 extends AbstractCrypto<SM2> {

    @Serial
    private static final long serialVersionUID = 2852290382583L;

    /**
     * SM2引擎
     */
    protected SM2Engine engine;
    /**
     * 签名
     */
    protected SM2Signer signer;
    /**
     * EC私有参数
     */
    private ECPrivateKeyParameters privateKeyParams;
    /**
     * EC公共参数
     */
    private ECPublicKeyParameters publicKeyParams;
    /**
     * DSA编码
     */
    private DSAEncoding encoding = StandardDSAEncoding.INSTANCE;
    /**
     * SM3摘要
     */
    private Digest digest = new SM3Digest();
    /**
     * C1C2C3
     */
    private SM2Engine.Mode mode = SM2Engine.Mode.C1C3C2;
    /**
     * 自定义随机数
     */
    private SecureRandom random;
    /**
     * 是否去除压缩04压缩标识
     */
    private boolean removeCompressedFlag;

    /**
     * 构造，生成新的随机私钥公钥对
     */
    public SM2() {
        this(null, (byte[]) null);
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey 私钥Hex或Base64表示，必须使用PKCS#8规范
     * @param publicKey  公钥Hex或Base64表示，必须使用X509规范
     */
    public SM2(final String privateKey, final String publicKey) {
        this(Builder.decode(privateKey), Builder.decode(publicKey));
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey 私钥，可以使用PKCS#8、D值或PKCS#1规范
     * @param publicKey  公钥，可以使用X509、Q值或PKCS#1规范
     */
    public SM2(final byte[] privateKey, final byte[] publicKey) {
        this(Keeper.generateSm2PrivateKey(privateKey), Keeper.generateSm2PublicKey(publicKey));
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey  私钥16进制（私钥D值）
     * @param privateKeyX 公钥X16进制
     * @param privateKeyY 公钥Y16进制
     */
    public SM2(final String privateKey, final String privateKeyX, final String privateKeyY) {
        this(Builder.decode(privateKey), Builder.decode(privateKeyX), Builder.decode(privateKeyY));
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey 私钥（D值）
     * @param publicKeyX 公钥X
     * @param publicKeyY 公钥Y
     */
    public SM2(final byte[] privateKey, final byte[] publicKeyX, final byte[] publicKeyY) {
        this(Keeper.generateSm2PrivateKey(privateKey), Keeper.generateSm2PublicKey(publicKeyX, publicKeyY));
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey 私钥
     * @param publicKey  公钥
     */
    public SM2(final PrivateKey privateKey, final PublicKey publicKey) {
        super(Algorithm.SM2.getValue(), new KeyPair(publicKey, privateKey));
        this.privateKeyParams = Keeper.toPrivateParams(this.privateKey);
        this.publicKeyParams = Keeper.toPublicParams(this.publicKey);
        this.init();
    }

    /**
     * 构造 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密或者解密
     *
     * @param privateKey 私钥，可以为null
     * @param publicKey  公钥，可以为null
     */
    public SM2(final ECPrivateKeyParameters privateKey, final ECPublicKeyParameters publicKey) {
        super(Algorithm.SM2.getValue(), null);
        this.privateKeyParams = privateKey;
        this.publicKeyParams = publicKey;
        this.init();
    }

    /**
     * 去除04压缩标识 gmssl等库生成的密文不包含04前缀，此处兼容
     *
     * @param data 密文数据
     * @return 处理后的数据
     */
    private static byte[] removeCompressedFlag(final byte[] data) {
        if (data[0] != 0x04) {
            return data;
        }
        final byte[] result = new byte[data.length - 1];
        System.arraycopy(data, 1, result, 0, result.length);
        return result;
    }

    /**
     * 追加压缩标识 检查数据，gmssl等库生成的密文不包含04前缀（非压缩数据标识），此处检查并补充 参考：https://blog.csdn.net/softt/article/details/139978608
     * 根据公钥压缩形态不同，密文分为两种压缩形式： C1( 03 + X ) + C3（32个字节）+ C2 C1( 02 + X ) + C3（32个字节）+ C2 非压缩公钥正常形态为04 + X +
     * Y，由于各个算法库差异，04有时候会省略 非压缩密文正常形态为04 + C1 + C3 + C2
     *
     * @param data 待解密数据
     * @return 增加压缩标识后的数据
     */
    private static byte[] prependCompressedFlag(byte[] data) {
        if (data[0] != 0x04 && data[0] != 0x02 && data[0] != 0x03) {
            // 默认非压缩形态
            data = ArrayKit.insert(data, 0, 0x04);
        }
        return data;
    }

    /**
     * 初始化 私钥和公钥同时为空时生成一对新的私钥和公钥 私钥和公钥可以单独传入一个，如此则只能使用此钥匙来做加密（签名）或者解密（校验）
     *
     * @return this
     */
    public SM2 init() {
        if (null == this.privateKeyParams && null == this.publicKeyParams) {
            super.initKeys();
            this.privateKeyParams = Keeper.toPrivateParams(this.privateKey);
            this.publicKeyParams = Keeper.toPublicParams(this.publicKey);
        }
        return this;
    }

    @Override
    public SM2 initKeys() {
        // 阻断父类中自动生成密钥对的操作，此操作由本类中进行。
        // 由于用户可能传入Params而非key，因此此时key必定为null，故此不再生成
        return this;
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param data 被加密的字符串，UTF8编码
     * @return 加密后的Base64
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public String encryptBase64(final String data) {
        return encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param in 被加密的数据流
     * @return 加密后的Base64
     */
    public String encryptBase64(final InputStream in) {
        return encryptBase64(in, KeyType.PublicKey);
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param data 被加密的bytes
     * @return 加密后的Base64
     */
    public String encryptBase64(final byte[] data) {
        return encryptBase64(data, KeyType.PublicKey);
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param data 被加密的bytes
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public byte[] encrypt(final byte[] data) throws CryptoException {
        return encrypt(data, KeyType.PublicKey);
    }

    /**
     * 加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param data    被加密的bytes
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    @Override
    public byte[] encrypt(final byte[] data, final KeyType keyType) throws CryptoException {
        if (KeyType.PublicKey != keyType) {
            throw new IllegalArgumentException("Encrypt is only support by public data");
        }
        return encrypt(data, new ParametersWithRandom(getCipherParameters(keyType), this.random));
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param data 被加密的字符串，UTF8编码
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public byte[] encrypt(final String data) {
        return encrypt(data, KeyType.PublicKey);
    }

    /**
     * 使用公钥加密，SM2非对称加密的结果由C1,C3,C2三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C3 SM3的摘要值
     * C2 密文数据
     * </pre>
     *
     * @param in 被加密的数据流
     * @return 加密后的bytes
     */
    public byte[] encrypt(final InputStream in) {
        return encrypt(in, KeyType.PublicKey);
    }

    /**
     * 加密，SM2非对称加密的结果由C1,C2,C3三部分组成，其中：
     *
     * <pre>
     * C1 生成随机数的计算出的椭圆曲线点
     * C2 密文数据
     * C3 SM3的摘要值
     * </pre>
     *
     * @param data             被加密的bytes
     * @param pubKeyParameters 公钥参数
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public byte[] encrypt(final byte[] data, final CipherParameters pubKeyParameters) throws CryptoException {
        lock.lock();
        final SM2Engine engine = getEngine();
        try {
            engine.init(true, pubKeyParameters);
            final byte[] result = engine.processBlock(data, 0, data.length);
            return this.removeCompressedFlag ? removeCompressedFlag(result) : result;
        } catch (final InvalidCipherTextException e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 使用私钥解密
     *
     * @param data SM2密文数据，Hex（16进制）或Base64字符串
     * @return 解密后的字符串，UTF-8 编码
     */
    public String decryptString(final String data) {
        return decryptString(data, KeyType.PrivateKey);
    }

    /**
     * 使用私钥解密
     *
     * @param data    SM2密文数据，Hex（16进制）或Base64字符串
     * @param charset 编码
     * @return 解密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public String decryptString(final String data, final Charset charset) {
        return decryptString(data, KeyType.PrivateKey, charset);
    }

    /**
     * 使用私钥解密
     *
     * @param data SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public byte[] decrypt(final byte[] data) throws CryptoException {
        return decrypt(data, KeyType.PrivateKey);
    }

    /**
     * 解密
     *
     * @param data    SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @param keyType 私钥或公钥 {@link KeyType}
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    @Override
    public byte[] decrypt(final byte[] data, final KeyType keyType) throws CryptoException {
        if (KeyType.PrivateKey != keyType) {
            throw new IllegalArgumentException("Decrypt is only support by private data");
        }
        return decrypt(data, getCipherParameters(keyType));
    }

    /**
     * 使用私钥解密
     *
     * @param in 密文数据流
     * @return 解密后的bytes
     */
    public byte[] decrypt(final InputStream in) {
        return super.decrypt(in, KeyType.PrivateKey);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param dataHex 被签名的数据（Hex格式）
     * @return 签名
     */
    public String signHexFromHex(final String dataHex) {
        return signHexFromHex(dataHex, null);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param dataHex 被签名的数据（Hex格式）
     * @param idHex   可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @return 签名
     */
    public String signHexFromHex(final String dataHex, final String idHex) {
        return HexKit.encodeString(sign(HexKit.decode(dataHex), HexKit.decode(idHex)));
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 被签名的数据
     * @return 签名
     */
    public String signHex(final byte[] data) {
        return signHex(data, null);
    }

    /**
     * 用私钥对信息生成数字签名
     *
     * @param data 被签名的数据
     * @param id   可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @return 签名
     */
    public String signHex(final byte[] data, final byte[] id) {
        return HexKit.encodeString(sign(data, id));
    }

    /**
     * 用私钥对信息生成数字签名，签名格式为ASN1 * 在硬件签名中，返回结果为R+S，可以通过调用{@link Builder#rsAsn1ToPlain(byte[])}方法转换之。
     *
     * @param data 加密数据
     * @return 签名
     */
    public byte[] sign(final byte[] data) {
        return sign(data, null);
    }

    /**
     * 用私钥对信息生成数字签名，签名格式为ASN1 在硬件签名中，返回结果为R+S，可以通过调用{@link Builder#rsAsn1ToPlain(byte[])}方法转换之。
     *
     * @param data 被签名的数据
     * @param id   可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @return 签名
     */
    public byte[] sign(final byte[] data, final byte[] id) {
        lock.lock();
        final SM2Signer signer = getSigner();
        try {
            CipherParameters param = new ParametersWithRandom(getCipherParameters(KeyType.PrivateKey), this.random);
            if (id != null) {
                param = new ParametersWithID(param, id);
            }
            signer.init(true, param);
            signer.update(data, 0, data.length);
            return signer.generateSignature();
        } catch (final org.bouncycastle.crypto.CryptoException e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param dataHex 后的数据
     * @param signHex 签名
     * @return 是否验证通过
     */
    public boolean verifyHex(final String dataHex, final String signHex) {
        return verifyHex(dataHex, signHex, null);
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param data 数据
     * @param sign 签名
     * @return 是否验证通过
     */
    public boolean verify(final byte[] data, final byte[] sign) {
        return verify(data, sign, null);
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param dataHex 数据的Hex值
     * @param signHex 签名的Hex值
     * @param idHex   ID的Hex值
     * @return 是否验证通过
     */
    public boolean verifyHex(final String dataHex, final String signHex, final String idHex) {
        return verify(HexKit.decode(dataHex), HexKit.decode(signHex), HexKit.decode(idHex));
    }

    /**
     * 用公钥检验数字签名的合法性
     *
     * @param data 数据
     * @param sign 签名
     * @param id   可以为null，若为null，则默认withId为字节数组:"1234567812345678".getBytes()
     * @return 是否验证通过
     */
    public boolean verify(final byte[] data, final byte[] sign, final byte[] id) {
        lock.lock();
        final SM2Signer signer = getSigner();
        try {
            CipherParameters param = getCipherParameters(KeyType.PublicKey);
            if (id != null) {
                param = new ParametersWithID(param, id);
            }
            signer.init(false, param);
            signer.update(data, 0, data.length);
            return signer.verifySignature(sign);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SM2 setPrivateKey(final PrivateKey privateKey) {
        super.setPrivateKey(privateKey);

        // 重新初始化密钥参数，防止重新设置密钥时导致密钥无法更新
        this.privateKeyParams = Keeper.toPrivateParams(privateKey);

        return this;
    }

    /**
     * 设置私钥参数
     *
     * @param privateKeyParams 私钥参数
     * @return this
     */
    public SM2 setPrivateKeyParams(final ECPrivateKeyParameters privateKeyParams) {
        this.privateKeyParams = privateKeyParams;
        return this;
    }

    /**
     * 设置随机数生成器，可自定义随机数种子
     *
     * @param random 随机数生成器，可自定义随机数种子
     * @return this
     */
    public SM2 setRandom(final SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public SM2 setPublicKey(final PublicKey publicKey) {
        super.setPublicKey(publicKey);

        // 重新初始化密钥参数，防止重新设置密钥时导致密钥无法更新
        this.publicKeyParams = Keeper.toPublicParams(publicKey);

        return this;
    }

    /**
     * 设置公钥参数
     *
     * @param publicKeyParams 公钥参数
     * @return this
     */
    public SM2 setPublicKeyParams(final ECPublicKeyParameters publicKeyParams) {
        this.publicKeyParams = publicKeyParams;
        return this;
    }

    /**
     * 使用私钥解密
     *
     * @param data SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @return 解密后的bytes
     */
    public byte[] decrypt(final String data) {
        return super.decrypt(data, KeyType.PrivateKey);
    }

    /**
     * 设置DSA signatures的编码为PlainDSAEncoding
     *
     * @return this
     */
    public SM2 usePlainEncoding() {
        return setEncoding(PlainDSAEncoding.INSTANCE);
    }

    /**
     * 设置DSA signatures的编码
     *
     * @param encoding {@link DSAEncoding}实现
     * @return this
     */
    public SM2 setEncoding(final DSAEncoding encoding) {
        this.encoding = encoding;
        this.signer = null;
        return this;
    }

    /**
     * 设置Hash算法
     *
     * @param digest {@link Digest}实现
     * @return this
     */
    public SM2 setDigest(final Digest digest) {
        this.digest = digest;
        this.engine = null;
        this.signer = null;
        return this;
    }

    /**
     * 设置SM2模式，旧版是C1C2C3，新版本是C1C3C2
     *
     * @param mode {@link SM2Engine.Mode}
     * @return this
     */
    public SM2 setMode(final SM2Engine.Mode mode) {
        this.mode = mode;
        this.engine = null;
        return this;
    }

    /**
     * 获得私钥D值（编码后的私钥）
     *
     * @return D值
     */
    public byte[] getD() {
        return BigIntegers.asUnsignedByteArray(32, getDBigInteger());
    }

    /**
     * 获得私钥D值（编码后的私钥）
     *
     * @return D值
     */
    public String getDHex() {
        return new String(Hex.encode(getD()));
    }

    /**
     * 获得私钥D值
     *
     * @return D值
     */
    public BigInteger getDBigInteger() {
        return this.privateKeyParams.getD();
    }

    /**
     * 获得公钥Q值（编码后的公钥）
     *
     * @param isCompressed 是否压缩
     * @return Q值
     */
    public byte[] getQ(final boolean isCompressed) {
        return this.publicKeyParams.getQ().getEncoded(isCompressed);
    }

    /**
     * 获取密钥类型对应的加密参数对象{@link CipherParameters}
     *
     * @param keyType Key类型枚举，包括私钥或公钥
     * @return {@link CipherParameters}
     */
    private CipherParameters getCipherParameters(final KeyType keyType) {
        switch (keyType) {
            case PublicKey:
                Assert.notNull(this.publicKeyParams, "PublicKey must be not null !");
                return this.publicKeyParams;

            case PrivateKey:
                Assert.notNull(this.privateKeyParams, "PrivateKey must be not null !");
                return this.privateKeyParams;
        }

        return null;
    }

    /**
     * 获取{@link SM2Engine}，此对象为懒加载模式
     *
     * @return {@link SM2Engine}
     */
    private SM2Engine getEngine() {
        if (null == this.engine) {
            Assert.notNull(this.digest, "digest must be not null !");
            this.engine = new SM2Engine(this.digest, this.mode);
        }
        this.digest.reset();
        return this.engine;
    }

    /**
     * 获取{@link SM2Signer}，此对象为懒加载模式
     *
     * @return {@link SM2Signer}
     */
    private SM2Signer getSigner() {
        if (null == this.signer) {
            Assert.notNull(this.digest, "digest must be not null !");
            this.signer = new SM2Signer(this.encoding, this.digest);
        }
        this.digest.reset();
        return this.signer;
    }

    /**
     * 解密
     *
     * @param data                 SM2密文，实际包含三部分：ECC公钥、真正的密文、公钥和原文的SM3-HASH值
     * @param privateKeyParameters 私钥参数
     * @return 加密后的bytes
     * @throws CryptoException 包括InvalidKeyException和InvalidCipherTextException的包装异常
     */
    public byte[] decrypt(byte[] data, final CipherParameters privateKeyParameters) throws CryptoException {
        Assert.isTrue(data.length > 1, "Invalid SM2 cipher text, must be at least 1 byte long");
        data = prependCompressedFlag(data);

        lock.lock();
        final SM2Engine engine = getEngine();
        try {
            engine.init(false, privateKeyParameters);
            return engine.processBlock(data, 0, data.length);
        } catch (final InvalidCipherTextException e) {
            throw new CryptoException(e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置是否移除压缩标记，默认为false 移除后的密文兼容gmssl等库
     *
     * @param removeCompressedFlag 是否移除压缩标记
     * @return this
     */
    public SM2 setRemoveCompressedFlag(final boolean removeCompressedFlag) {
        this.removeCompressedFlag = removeCompressedFlag;
        return this;
    }

}
