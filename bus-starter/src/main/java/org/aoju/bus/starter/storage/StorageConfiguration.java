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
package org.aoju.bus.starter.storage;

import org.aoju.bus.starter.BusXExtend;
import org.aoju.bus.storage.metric.DefaultStorageCache;
import org.aoju.bus.storage.metric.StorageCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 授权配置
 *
 * @author Kimi Liu
 * @version 5.5.3
 * @since JDK 1.8+
 */
@EnableConfigurationProperties(value = {StorageProperties.class})
public class StorageConfiguration {

    @Bean
    public StorageProviderService storageProviderFactory(StorageProperties properties, StorageCache storageCache) {
        return new StorageProviderService(properties, storageCache);
    }

    @Bean
    @ConditionalOnMissingBean(StorageCache.class)
    @ConditionalOnProperty(name = BusXExtend.STORAGE + ".cache.type", havingValue = "default", matchIfMissing = true)
    public StorageCache storageCache() {
        return DefaultStorageCache.INSTANCE;
    }

}
