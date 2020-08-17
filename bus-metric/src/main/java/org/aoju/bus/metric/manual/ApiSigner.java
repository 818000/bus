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
package org.aoju.bus.metric.manual;

import java.util.HashMap;
import java.util.Map;

/**
 * 签名验证实现
 *
 * @author Kimi Liu
 * @version 6.0.6
 * @since JDK 1.8++
 */
public class ApiSigner implements Signer {

    private Map<String, Verifier> checker = new HashMap<>();

    public ApiSigner() {
        checker.put("md5", new DefaultVerifier());
    }

    /**
     * 添加签名检查器
     *
     * @param algorithmName 算法
     * @param checker       检查器
     */
    public void addChecker(String algorithmName, Verifier checker) {
        this.checker.put(algorithmName, checker);
    }

    @Override
    public boolean isRightSign(ApiParam apiParam, String secret, String signMethod) {
        Verifier verifier = checker.get(signMethod.toLowerCase());
        if (verifier == null) {
            throw Errors.ERROR_ALGORITHM.getException(signMethod);
        }
        return verifier.verify(apiParam, secret);
    }

}
