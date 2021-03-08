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
package org.aoju.bus.starter.sensitive;

import lombok.Data;
import org.aoju.bus.starter.BusXExtend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Kimi Liu
 * @version 6.2.1
 * @since JDK 1.8+
 */
@Data
@EnableConfigurationProperties(value = {SensitiveProperties.Encrypt.class, SensitiveProperties.Decrypt.class})
@ConfigurationProperties(prefix = BusXExtend.SENSITIVE)
public class SensitiveProperties {

    @Autowired
    private Encrypt encrypt;
    @Autowired
    private Decrypt decrypt;

    private boolean debug;

    @Data
    @ConfigurationProperties(prefix = BusXExtend.SENSITIVE + ".encrypt")
    public class Encrypt {
        private String key;
        private String type;
    }

    @Data
    @ConfigurationProperties(prefix = BusXExtend.SENSITIVE + ".decrypt")
    public class Decrypt {
        private String key;
        private String type;
    }

}
