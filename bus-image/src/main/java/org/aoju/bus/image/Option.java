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
package org.aoju.bus.image;

import lombok.Data;
import org.aoju.bus.image.metric.Connection;
import org.aoju.bus.image.metric.internal.pdu.ExtendedNegotiate;

import java.util.EnumSet;

/**
 * 服务请求选项
 *
 * @author Kimi Liu
 * @version 6.2.3
 * @since JDK 1.8+
 */
@Data
public class Option {

    // 密码套件
    public static final String[] TLS =
            {"SSL_RSA_WITH_NULL_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA"};
    public static final String[] TLS_NULL = {"SSL_RSA_WITH_NULL_SHA"};
    public static final String[] TLS_3DES = {"SSL_RSA_WITH_3DES_EDE_CBC_SHA"};
    public static final String[] TLS_AES = {"TLS_RSA_WITH_AES_128_CBC_SHA", "SSL_RSA_WITH_3DES_EDE_CBC_SHA"};
    // TLS协议
    public static final String[] defaultProtocols = {"TLSv1", "SSLv3"};
    public static final String[] tls1 = {"TLSv1"};
    public static final String[] tls11 = {"TLSv1.1"};
    public static final String[] tls12 = {"TLSv1.2"};
    public static final String[] ssl3 = {"SSLv3"};
    public static final String[] ssl2Hello = {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2"};
    /* 此AE可以异步执行的最大操作数，无限制为0，而非异步为1 */
    private int maxOpsInvoked = Connection.SYNCHRONOUS_MODE;
    private int maxOpsPerformed = Connection.SYNCHRONOUS_MODE;
    private int maxPdulenRcv = Connection.DEF_MAX_PDU_LENGTH;
    private int maxPdulenSnd = Connection.DEF_MAX_PDU_LENGTH;
    private boolean packPDV = true;
    private int backlog = Connection.DEF_BACKLOG;
    private int connectTimeout = Connection.NO_TIMEOUT;
    private int requestTimeout = Connection.NO_TIMEOUT;
    private int acceptTimeout = Connection.NO_TIMEOUT;
    private int releaseTimeout = Connection.NO_TIMEOUT;
    private int responseTimeout = Connection.NO_TIMEOUT;
    private int retrieveTimeout = Connection.NO_TIMEOUT;
    private int idleTimeout = Connection.NO_TIMEOUT;
    private int socloseDelay = Connection.DEF_SOCKETDELAY;
    private int sosndBuffer = Connection.DEF_BUFFERSIZE;
    private int sorcvBuffer = Connection.DEF_BUFFERSIZE;
    private boolean tcpNoDelay = true;
    private String[] cipherSuites;
    private String[] tlsProtocols;

    private boolean tlsNeedClientAuth;

    private String keystoreURL;
    private String keystoreType;
    private String keystorePass;
    private String keyPass;
    private String truststoreURL;
    private String truststoreType;
    private String truststorePass;

    public Option() {

    }

    public Option(boolean tlsNeedClientAuth,
                  String keystoreURL,
                  String keystoreType,
                  String keystorePass,
                  String keyPass,
                  String truststoreURL,
                  String truststoreType,
                  String truststorePass) {
        this(
                TLS,
                defaultProtocols,
                tlsNeedClientAuth,
                keystoreURL,
                keystoreType,
                keystorePass,
                keyPass,
                truststoreURL,
                truststoreType,
                truststorePass
        );
    }

    public Option(String[] cipherSuites,
                  String[] tlsProtocols,
                  boolean tlsNeedClientAuth,
                  String keystoreURL,
                  String keystoreType,
                  String keystorePass,
                  String keyPass,
                  String truststoreURL,
                  String truststoreType,
                  String truststorePass) {
        if (null == cipherSuites) {
            throw new IllegalArgumentException("cipherSuites cannot be null");
        }
        this.cipherSuites = cipherSuites;
        this.tlsProtocols = tlsProtocols;
        this.tlsNeedClientAuth = tlsNeedClientAuth;
        this.keystoreURL = keystoreURL;
        this.keystoreType = keystoreType;
        this.keystorePass = keystorePass;
        this.keyPass = keyPass;
        this.truststoreURL = truststoreURL;
        this.truststoreType = truststoreType;
        this.truststorePass = truststorePass;
    }

    public enum Type {

        RELATIONAL,
        DATETIME,
        FUZZY,
        TIMEZONE;

        public static byte[] toExtendedNegotiationInformation(EnumSet<Type> opts) {
            byte[] info = new byte[opts.contains(TIMEZONE) ? 4
                    : opts.contains(FUZZY) || opts.contains(DATETIME) ? 3
                    : 1];
            for (Type query : opts)
                info[query.ordinal()] = 1;
            return info;
        }

        public static EnumSet<Type> toOptions(ExtendedNegotiate extNeg) {
            EnumSet<Type> opts = EnumSet.noneOf(Type.class);
            if (null != extNeg) {
                toOption(extNeg, Type.RELATIONAL, opts);
                toOption(extNeg, Type.DATETIME, opts);
                toOption(extNeg, Type.FUZZY, opts);
                toOption(extNeg, Type.TIMEZONE, opts);
            }
            return opts;
        }

        private static void toOption(ExtendedNegotiate extNeg,
                                     Type opt,
                                     EnumSet<Type> opts) {
            if (extNeg.getField(opt.ordinal(), (byte) 0) == 1)
                opts.add(opt);
        }

    }

}
