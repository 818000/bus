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
package org.aoju.bus.starter.cache;

import lombok.Data;
import org.aoju.bus.cache.CacheX;
import org.aoju.bus.starter.BusXExtend;
import org.aoju.bus.starter.druid.DruidProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 缓存相关配置
 *
 * @author Kimi Liu
 * @version 5.9.8
 * @since JDK 1.8+
 */
@Data
@ConfigurationProperties(prefix = BusXExtend.CACHE)
public class CacheProperties {

    /**
     * 缓存类型
     */
    private String type;
    /**
     * 缓存配置
     */
    private Map<String, CacheX> map;
    /**
     * 缓存前缀,目前只对redis缓存生效,默认 OAUTH::STATE::
     */
    private String prefix;
    /**
     * 超时时长,目前只对redis缓存生效,默认3分钟
     */
    private String timeout;
    /**
     * 存储信息
     */
    private DruidProperties provider;
}
