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
package org.aoju.bus.starter.druid;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.logger.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 多数据源支持
 *
 * @author Kimi Liu
 * @version 5.9.6
 * @since JDK 1.8+
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    /**
     * 所有数据源的key集合
     */
    private static final Set<Object> keySet = new LinkedHashSet<>();
    /**
     * 单例句柄
     */
    private static DynamicDataSource instance;
    private static byte[] lock = Normal.EMPTY_BYTE_ARRAY;

    /**
     * 单例方法
     *
     * @return the DynamicDataSource
     */
    public static synchronized DynamicDataSource getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DynamicDataSource();
                }
            }
        }
        return instance;
    }

    /**
     * 动态增加数据源
     *
     * @param key        数据源key
     * @param dataSource 数据源信息
     */
    public synchronized static void addDataSource(String key, javax.sql.DataSource dataSource) {
        if (dataSource != null && dataSource instanceof AbstractRoutingDataSource) {
            try {
                Field sourceMapField = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
                sourceMapField.setAccessible(true);
                Map<Object, javax.sql.DataSource> sourceMap = (Map<Object, javax.sql.DataSource>) sourceMapField.get(getInstance().getDefaultDataSource());
                sourceMap.put(key, dataSource);
                keySet.add(key);
                sourceMapField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setTargetDataSources(Map<Object, Object> map) {
        super.setTargetDataSources(map);
        this.keySet.add(map.keySet());
        this.afterPropertiesSet();
    }

    /**
     * AbstractRoutingDataSource
     * 抽象类实现方法，
     * 即获取当前线程数据源的key
     *
     * @return 当前数据源key
     */
    @Override
    protected Object determineCurrentLookupKey() {
        String key = DataSourceHolder.getKey();
        if (!keySet.contains(key)) {
            logger.info(String.format("can not found datasource by key: '%s',this session may use default datasource", key));
        }
        if (key == null) {
            Logger.info("The current datasource key ：{}", "dataSource");
        } else {
            Logger.info("The current datasource key ：{}", key);
        }
        return key;
    }

    /**
     * 在获取key的集合,目的只是为了添加一些告警日志
     */
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        try {
            Field sourceMapField = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
            sourceMapField.setAccessible(true);
            Map<Object, javax.sql.DataSource> sourceMap = (Map<Object, javax.sql.DataSource>) sourceMapField.get(this);
            keySet.addAll(sourceMap.keySet());
            sourceMapField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 判断指定DataSrouce当前是否存在
     *
     * @param key 数据源key
     * @return the true/false
     */
    public boolean containsKey(String key) {
        return keySet.contains(key);
    }

    /**
     * 获取默认数据源
     *
     * @return the dataSource
     */
    public javax.sql.DataSource getDefaultDataSource() {
        return super.determineTargetDataSource();
    }

}
