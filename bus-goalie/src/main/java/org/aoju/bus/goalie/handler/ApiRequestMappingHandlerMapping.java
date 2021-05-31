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
package org.aoju.bus.goalie.handler;

import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.goalie.annotation.ApiVersion;
import org.aoju.bus.goalie.annotation.ClientVersion;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author Kimi Liu
 * @version 6.2.3
 * @since JDK 1.8++
 */
public class ApiRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    /**
     * 重写此处，保证读取我们的注解apiversion
     *
     * @param method      请求方法
     * @param handlerType 拦截器
     * @return 处理结果
     */
    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo mappinginfo = super.getMappingForMethod(method, handlerType);
        if (null != mappinginfo) {
            RequestMappingInfo apiVersionMappingInfo = getApiVersionMappingInfo(method, handlerType);
            return null == apiVersionMappingInfo ? mappinginfo : apiVersionMappingInfo.combine(mappinginfo);
        }
        return mappinginfo;
    }

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        ClientVersion clientVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ClientVersion.class);
        return createRequestCondtion(clientVersion);
    }

    /**
     * 重新定义clientversion的条件匹配
     *
     * @param method 请求方法
     * @return 匹配规则
     */
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        ClientVersion clientVersion = AnnotatedElementUtils.findMergedAnnotation(method, ClientVersion.class);
        return createRequestCondtion(clientVersion);
    }

    private RequestCondition<?> createRequestCondtion(ClientVersion clientVersion) {
        if (null == clientVersion) {
            return null;
        }
        if (null != clientVersion.value() && clientVersion.value().length > 0) {
            return new ApiVersionRequestCondition(clientVersion.value());
        }
        if (null != clientVersion.expression() && clientVersion.expression().length > 0) {
            return new ApiVersionRequestCondition(clientVersion.expression());
        }
        return null;
    }

    /**
     * @param method      请求方法
     * @param handlerType 拦截器
     * @return 处理结果
     */
    private RequestMappingInfo getApiVersionMappingInfo(Method method, Class<?> handlerType) {
        // 优先查找method
        ApiVersion apiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
        if (null == apiVersion || StringKit.isBlank(apiVersion.value())) {
            apiVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
        }
        return null == apiVersion || StringKit.isBlank(apiVersion.value()) ? null : RequestMappingInfo
                .paths(apiVersion.value())
                .build();
    }

}
