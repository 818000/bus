/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.starter.cors;

import lombok.Data;
import org.aoju.bus.core.lang.Symbol;
import org.aoju.bus.starter.BusXExtend;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Core 跨域相关配置
 *
 * @author Kimi Liu
 * @version 5.8.6
 * @since JDK 1.8+
 */
@Data
@ConfigurationProperties(BusXExtend.CORS)
public class CorsProperties {

    /**
     * 允许方法路径
     */
    private String path = "/**";
    /**
     * 允许的域名
     */
    private String[] allowedOrigins = new String[]{Symbol.STAR};

    /**
     * 允许的请求头
     */
    private String[] allowedHeaders = new String[]{Symbol.STAR};
    /**
     * 允许的方法
     */
    private String[] allowedMethods = new String[]{"GET", "DELETE", "POST", "PUT", "OPTIONS"};

    /**
     * 响应头信息公开
     */
    private String[] exposedHeaders;
    /**
     * 是否允许用户发送、处理 cookie
     */
    private Boolean allowCredentials = true;
    /**
     * 预检请求的有效期,单位为秒 有效期内,不会重复发送预检请求
     */
    private Long maxAge = 1800L;

}
