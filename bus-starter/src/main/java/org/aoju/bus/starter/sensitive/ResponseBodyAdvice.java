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
package org.aoju.bus.starter.sensitive;

import org.aoju.bus.base.advice.BaseAdvice;
import org.aoju.bus.base.entity.Message;
import org.aoju.bus.base.entity.Result;
import org.aoju.bus.core.lang.Charset;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.utils.ArrayUtils;
import org.aoju.bus.core.utils.ObjectUtils;
import org.aoju.bus.core.utils.ReflectUtils;
import org.aoju.bus.core.utils.StringUtils;
import org.aoju.bus.logger.Logger;
import org.aoju.bus.sensitive.Builder;
import org.aoju.bus.sensitive.annotation.Privacy;
import org.aoju.bus.sensitive.annotation.Sensitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求响应处理类
 * 对加了@Encrypt的方法的数据进行加密操作
 *
 * @author Kimi Liu
 * @version 5.9.0
 * @since JDK 1.8+
 */
public class ResponseBodyAdvice extends BaseAdvice
        implements org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice<Object> {

    @Autowired
    SensitiveProperties properties;

    /**
     * 依据对象的属性数组和值数组对进行赋值
     *
     * @param <T>    对象
     * @param entity 反射对象
     * @param fields 属性数组
     * @param value  值数组
     */
    private static <T> void setValue(T entity, String[] fields, Object[] value) {
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (ReflectUtils.hasField(entity, field)) {
                ReflectUtils.invokeSetter(entity, field, value[i]);
            }
        }
    }

    /**
     * 依据对象的属性获取对象值
     *
     * @param <T>    对象
     * @param entity 反射对象
     * @param field  属性数组
     */
    private static <T> Object getValue(T entity, String field) {
        if (ReflectUtils.hasField(entity, field)) {
            Object object = ReflectUtils.invokeGetter(entity, field);
            return object != null ? object.toString() : null;
        }
        return null;
    }

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        if (ArrayUtils.isNotEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Sensitive) {
                    return true;
                }
            }
        }
        return returnType.getMethod().isAnnotationPresent(Sensitive.class);
    }

    /**
     * 在选择{@code HttpMessageConverter}之后和之前调用 调用它的写方法
     *
     * @param body          需要操作的body
     * @param parameter     方法参数
     * @param mediaType     媒体类型
     * @param converterType 转换类型
     * @param request       当前 request
     * @param response      当前 response
     * @return 传入或修改(可能是新的)实例的主体
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter parameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (ObjectUtils.isNotEmpty(this.properties) && !this.properties.isDebug()) {
            try {
                final Sensitive sensitive = parameter.getMethod().getAnnotation(Sensitive.class);
                if (ObjectUtils.isEmpty(sensitive)) {
                    return body;
                }

                Object object = ((Message) body).getData();
                if (object instanceof Result) {
                    List list = new ArrayList<>();
                    for (Object obj : ((Result) object).getRows()) {
                        this.beforeBodyWrite(sensitive, obj);
                        list.add(obj);
                    }
                    ((Result) ((Message) body).getData()).setRows(list);
                } else if (object instanceof List) {
                    List list = new ArrayList<>();
                    for (Object obj : (List) object) {
                        this.beforeBodyWrite(sensitive, obj);
                        list.add(obj);
                    }
                    ((Message) body).setData(list);
                } else {
                    this.beforeBodyWrite(sensitive, object);
                    ((Message) body).setData(object);
                }
            } catch (Exception e) {
                Logger.error("Internal processing failure:" + e.getMessage());
            }
        }
        return body;
    }


    private void beforeBodyWrite(Sensitive sensitive, Object object) {
        if (ObjectUtils.isEmpty(object)) {
            return;
        }
        // 数据脱敏
        if ((Builder.ALL.equals(sensitive.value()) || Builder.SENS.equals(sensitive.value()))
                && (Builder.ALL.equals(sensitive.stage()) || Builder.OUT.equals(sensitive.stage()))) {
            Logger.debug("Response data sensitive enabled ...");
            Builder.on(object, sensitive);
        }
        // 数据解密
        if (Builder.ALL.equals(sensitive.value()) || Builder.SAFE.equals(sensitive.value())
                && (Builder.ALL.equals(sensitive.stage()) || Builder.OUT.equals(sensitive.stage()))) {
            Map<String, Privacy> map = getPrivacyMap(object.getClass());
            for (Map.Entry<String, Privacy> entry : map.entrySet()) {
                Privacy privacy = entry.getValue();
                if (ObjectUtils.isNotEmpty(privacy) && StringUtils.isNotEmpty(privacy.value())) {
                    if (Builder.ALL.equals(privacy.value()) || Builder.OUT.equals(privacy.value())) {
                        String property = entry.getKey();
                        String value = (String) getValue(object, property);
                        if (StringUtils.isNotEmpty(value)) {
                            if (ObjectUtils.isEmpty(properties)) {
                                throw new InstrumentException("Please check the request.crypto.encrypt");
                            }
                            Logger.debug("Response data encryption enabled ...");
                            value = org.aoju.bus.crypto.Builder.encrypt(properties.getEncrypt().getType(), properties.getEncrypt().getKey(), value, Charset.UTF_8);
                            setValue(object, new String[]{property}, new String[]{value});
                        }
                    }
                }
            }
        }
    }

    private Map<String, Privacy> getPrivacyMap(Class<?> clazz) {
        Map<String, Privacy> map = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            Privacy privacy = field.getAnnotation(Privacy.class);
            if (privacy != null) {
                map.put(field.getName(), privacy);
            }
        }
        return map;
    }

}
