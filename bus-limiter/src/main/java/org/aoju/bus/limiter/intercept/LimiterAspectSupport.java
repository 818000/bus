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
package org.aoju.bus.limiter.intercept;

import org.aoju.bus.core.utils.CollUtils;
import org.aoju.bus.limiter.execute.LimitContextsValueWrapper;
import org.aoju.bus.limiter.execute.LimiterExecutionContext;
import org.aoju.bus.limiter.metadata.LimitedResourceMetadata;
import org.aoju.bus.limiter.metadata.LimitedResourceMetadataCache;
import org.aoju.bus.limiter.resource.LimitedResource;
import org.aoju.bus.limiter.resource.LimitedResourceSource;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 实际的advisor
 * 使用beanfactory的一些基础设施
 *
 * @author Kimi Liu
 * @version 5.5.2
 * @since JDK 1.8+
 */
public abstract class LimiterAspectSupport implements BeanFactoryAware, InitializingBean, SmartInitializingSingleton {

    private BeanFactory beanFactory;

    private boolean initialized = false;

    private LimitedResourceSource limitedResourceSource;

    private LimitedResourceMetadataCache limitedResourceMetadataCache;

    /**
     * 执行
     *
     * @param invocation 反射
     * @param target     目标
     * @param method     方法
     * @param args       参数
     * @return the object
     * @throws Throwable 异常
     */
    protected Object execute(final MethodInvocation invocation, Object target, Method method, Object[] args) throws Throwable {

        if (this.initialized) {
            Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
            LimitedResourceSource limitedResourceSource = getLimitedResourceSource();
            if (limitedResourceSource != null) {
                Collection<LimitedResource> limitedResources = limitedResourceSource.getLimitedResource(targetClass, method);
                if (!CollUtils.isEmpty(limitedResources)) {
                    Collection<LimiterExecutionContext> contexts = getLimiterOperationContexts(limitedResources, method, args, target, targetClass);
                    LimitContextsValueWrapper limitContextsValueWrapper = limitContexts(contexts);
                    if (limitContextsValueWrapper.value()) {
                        try {
                            return invocation.proceed();
                        } catch (Throwable e) {
                            throw e;
                        } finally {
                            releaseContexts(contexts);
                        }
                    } else {
                        return limitContextsValueWrapper.getLimiterFailResolveResult();
                    }

                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.initialized = true;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        this.limitedResourceMetadataCache = new LimitedResourceMetadataCache(beanFactory);
    }

    protected LimitContextsValueWrapper limitContexts(Collection<LimiterExecutionContext> contexts) {
        Collection<LimiterExecutionContext> limited = new ArrayList<>();
        for (LimiterExecutionContext context : contexts) {
            if (context.limit() && context.getThrowable() == null) {
                limited.add(context);
            } else {
                releaseContexts(limited);
                Object result = context.getFallbackResult();
                return new LimitContextsValueWrapper(false, result);
            }

        }
        return new LimitContextsValueWrapper(true, null);
    }

    protected void releaseContexts(Collection<LimiterExecutionContext> contexts) {
        if (contexts != null && !contexts.isEmpty()) {
            for (LimiterExecutionContext context : contexts) {
                context.release();
            }
        }
    }


    protected Collection<LimiterExecutionContext> getLimiterOperationContexts(Collection<LimitedResource> limitedResources, Method method, Object[] args, Object target, Class<?> targetClass) {
        Collection<LimiterExecutionContext> retVal = new ArrayList<>();
        for (LimitedResource limitedResource : limitedResources) {
            LimitedResourceMetadata metadata = limitedResourceMetadataCache.getLimitedResourceMetadata(limitedResource, method, targetClass);
            retVal.add(new LimiterExecutionContext(metadata, args, target, this.beanFactory));
        }
        return retVal;
    }


    public LimitedResourceSource getLimitedResourceSource() {
        return limitedResourceSource;
    }

    public void setLimitedResourceSource(LimitedResourceSource limitedResourceSource) {
        this.limitedResourceSource = limitedResourceSource;
    }

    @Override
    public void afterPropertiesSet() {

    }

}
