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
package org.aoju.bus.starter.mapper;

import org.aoju.bus.core.lang.Assert;
import org.aoju.bus.mapper.builder.MapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;

/**
 * 增加mapperBuilder
 *
 * @author Kimi Liu
 * @version 5.6.5
 * @since JDK 1.8+
 */
public class MapperFactoryBean<T> extends SqlSessionDaoSupport
        implements FactoryBean<T> {

    private Class<T> mapperInterface;
    private boolean addToConfig = true;
    private MapperBuilder mapperBuilder;

    public MapperFactoryBean() {
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();

        Assert.notNull(this.mapperInterface, "Property 'mapperInterface' is required");

        Configuration configuration = getSqlSession().getConfiguration();
        if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
            try {
                configuration.addMapper(this.mapperInterface);
            } catch (Exception e) {
                logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", e);
                throw new IllegalArgumentException(e);
            } finally {
                ErrorContext.instance().reset();
            }
        }
        //直接针对接口处理通用接口方法对应的 MappedStatement 是安全的,通用方法不会出现 IncompleteElementException 的情况
        if (configuration.hasMapper(this.mapperInterface) && mapperBuilder != null && mapperBuilder.isExtendCommonMapper(this.mapperInterface)) {
            mapperBuilder.processConfiguration(getSqlSession().getConfiguration(), this.mapperInterface);
        }
    }

    @Override
    public T getObject() {
        return getSqlSession().getMapper(this.mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * 返回MyBatis mapper的mapper接口
     *
     * @return the object
     */
    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    /**
     * 设置MyBatis mapper的mapper接口
     *
     * @param mapperInterface 接口
     */
    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * 将添加的标志返回到MyBatis配置中
     * 如果映射器将被添加到MyBatis,则返回true,
     * 如果它还没有被添加到MyBatis中注册
     *
     * @return the boolean
     */
    public boolean isAddToConfig() {
        return addToConfig;
    }

    /**
     * 如果addToConfig为false,映射器将不会添加到MyBatis 这意味着
     * 它必须包含在mybatisconfig .xml中
     * 如果是真的,映射器将被添加到MyBatis中,如果还没有
     * 注册 默认情况下addToCofig为真
     *
     * @param addToConfig 是否添加
     */
    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    /**
     * 设置通用 Mapper 配置
     *
     * @param mapperBuilder 绑定器
     */
    public void setMapperBuilder(MapperBuilder mapperBuilder) {
        this.mapperBuilder = mapperBuilder;
    }

}
