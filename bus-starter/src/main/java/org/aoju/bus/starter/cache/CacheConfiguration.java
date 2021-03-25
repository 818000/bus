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
package org.aoju.bus.starter.cache;

import org.aoju.bus.cache.Context;
import org.aoju.bus.cache.provider.*;
import org.aoju.bus.core.toolkit.BeanKit;
import org.aoju.bus.core.toolkit.ClassKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 缓存配置
 *
 * @author Kimi Liu
 * @version 6.2.2
 * @since JDK 1.8+
 */
@EnableConfigurationProperties(value = {CacheProperties.class})
public class CacheConfiguration {

    @Autowired
    CacheProperties properties;

    @Bean
    public AspectjCacheProxy cacheConfigurer() {
        String type = StringKit.toString(this.properties.getType());
        try {
            if (!StringKit.isEmpty(type)) {
                Object provider = ClassKit.loadClass(type);
                Context config = Context.newConfig(this.properties.getMap());
                if (provider instanceof H2Hitting) {
                    config.setHitting(new H2Hitting(
                            this.properties.getProvider().getUrl(),
                            this.properties.getProvider().getUsername(),
                            this.properties.getProvider().getPassword()
                    ));
                } else if (provider instanceof MySQLHitting) {
                    config.setHitting(new MySQLHitting(
                            BeanKit.beanToMap(this.properties)
                    ));
                } else if (provider instanceof SqliteHitting) {
                    config.setHitting(new SqliteHitting(
                            this.properties.getProvider().getUrl(),
                            this.properties.getProvider().getUsername(),
                            this.properties.getProvider().getPassword()
                    ));
                } else if (provider instanceof ZookeeperHitting) {
                    config.setHitting(new ZookeeperHitting(
                            this.properties.getProvider().getUrl()
                    ));
                } else if (provider instanceof MemoryHitting) {
                    config.setHitting(new MemoryHitting());
                }
                return new AspectjCacheProxy(config);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("can not resolve class with type: " + type);
        }
        return null;
    }

}
