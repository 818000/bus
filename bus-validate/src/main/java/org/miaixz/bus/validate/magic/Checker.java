/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.validate.magic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.miaixz.bus.core.lang.Normal;
import org.miaixz.bus.core.lang.exception.NoSuchException;
import org.miaixz.bus.core.lang.exception.ValidateException;
import org.miaixz.bus.core.xyz.ArrayKit;
import org.miaixz.bus.core.xyz.FieldKit;
import org.miaixz.bus.core.xyz.ObjectKit;
import org.miaixz.bus.logger.Logger;
import org.miaixz.bus.validate.*;
import org.miaixz.bus.validate.magic.annotation.Inside;
import org.miaixz.bus.validate.magic.annotation.NotBlank;

/**
 * 校验检查器
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public class Checker {

    /**
     * 根据指定的校验器校验对象
     *
     * @param verified 被校验对象
     * @param material 校验器属性
     * @return 校验结果
     * @throws ValidateException 如果校验环境的fast设置为true, 则校验失败时立刻抛出该异常
     */
    public Collector object(Verified verified, Material material) throws ValidateException {
        Collector collector = new Collector(verified);
        Context context = verified.getContext();

        if (Provider.isGroup(material.getGroup(), context.getGroup())) {
            collector.collect(doObject(verified, material));
        }
        List<Material> list = material.getList();
        for (Material p : list) {
            collector.collect(doObject(verified, p));
        }
        return collector;
    }

    /**
     * 校验对象内部的所有字段
     *
     * @param verified 被校验对象
     * @return 校验结果
     */
    public Collector inside(Verified verified) {
        Collector collector = new Collector(verified);

        Object object = verified.getObject();
        if (ObjectKit.isEmpty(object)) {
            Logger.debug("The verified object is null, skip validation of internal fields: {}", verified);
            return collector;
        }

        Field[] fields = FieldKit.getFields(object.getClass());
        for (Field field : fields) {
            Object value = FieldKit.getFieldValue(object, field);
            Annotation[] annotations = field.getDeclaredAnnotations();
            String[] xFields = verified.getContext().getField();
            String[] xSkip = null == verified.getContext().getSkip() ? null : verified.getContext().getSkip();
            // 过滤当前需跳过的属性
            if (ArrayKit.isNotEmpty(xSkip) && Arrays.asList(xSkip).contains(field.getName())) {
                continue;
            }
            // 过滤当前需要校验的属性
            if (ArrayKit.isNotEmpty(xFields) && !Arrays.asList(xFields).contains(field.getName())) {
                continue;
            }

            // 属性校验开始
            verified.getContext().setInside(false);
            verified = new Verified(value, annotations, verified.getContext(), field.getName());
            if (null != value && Provider.isCollection(value) && hasInside(annotations)) {
                collector.collect(doCollectionInside(verified));
            } else if (null != value && Provider.isArray(value) && hasInside(annotations)) {
                collector.collect(doArrayInside(verified));
            }

            if (verified.getList().isEmpty()) {
                Logger.warn("==>    Request: Please check the annotation on property: {}", field.getName());
                // throw new ValidateException(ErrorCode._100511);
                // 创建包含默认Material的Verified对象
                verified = new Verified(value, new Annotation[0], verified.getContext(), field.getName());
                verified.getList().add(without(field));
            }

            collector.collect(verified.access());
        }
        return collector;
    }

    /**
     * 创建一个表示字段不能为空的校验规则Material对象
     *
     * @param field 需要校验的字段
     * @return 配置好校验规则的Material对象
     */
    public Material without(Field field) {
        // 创建新的Material对象用于存储校验规则
        Material material = new Material();

        // 设置校验器名称为"NOT_BLANK"
        material.setName(Builder._NOT_BLANK);

        // 从Registry获取NotBlank校验器的类对象并设置
        // 这里使用require方法确保校验器存在
        material.setClazz(Registry.getInstance().require(Builder._NOT_BLANK).getClass());

        // 使用动态代理创建NotBlank注解实例
        // 代理对象会返回注解方法的默认值
        material.setAnnotation((NotBlank) Proxy.newProxyInstance(
                // 使用注解的类加载器
                NotBlank.class.getClassLoader(),
                // 实现的接口
                new Class<?>[] { NotBlank.class },
                // 调用处理器
                (proxy, method, args) -> {
                    // 返回注解方法的默认值
                    return method.getDefaultValue();
                }));

        // 设置错误消息模板，使用${field}占位符
        material.setErrmsg("请检查${field}参数");

        // 设置默认错误码
        material.setErrcode(ErrorCode._116000);

        // 设置需要校验的字段名称
        material.setField(field.getName());

        // 设置校验分组为空数组（表示不分组）
        material.setGroup(new String[0]);

        // 添加校验参数：
        // 1. FIELD参数：字段名称
        material.addParam(Builder.FIELD, field.getName());
        // 2. VALUE参数：空字符串（表示校验空值）
        material.addParam(Builder.VALUE, Normal.EMPTY);

        return material;
    }

    /**
     * 根据校验器属性校验对象
     *
     * @param verified 被校验的对象
     * @param material 校验器属性
     * @return 校验结果
     */
    public Collector doObject(Verified verified, Material material) {
        Matcher matcher = (Matcher) Registry.getInstance().require(material.getName(), material.getClazz());
        if (ObjectKit.isEmpty(matcher)) {
            throw new NoSuchException(String.format("Cannot find the specified validator, name:%s, class:%s",
                    material.getName(), null == material.getClazz() ? Normal.NULL : material.getClazz().getName()));
        }
        Object validatedTarget = verified.getObject();
        if (ObjectKit.isNotEmpty(validatedTarget) && material.isArray() && Provider.isArray(validatedTarget)) {
            return doArrayObject(verified, material);
        } else if (ObjectKit.isNotEmpty(validatedTarget) && material.isArray()
                && Provider.isCollection(validatedTarget)) {
            return doCollection(verified, material);
        } else {
            boolean result = matcher.on(validatedTarget, material.getAnnotation(), verified.getContext());
            if (!result && verified.getContext().isFast()) {
                throw Provider.resolve(material, verified.getContext());
            }
            return new Collector(verified, material, result);
        }
    }

    /**
     * 校验集合对象元素
     *
     * @param verified 被校验对象
     * @param material 校验器属性
     * @return 校验结果
     */
    public Collector doCollection(Verified verified, Material material) {
        Collector collector = new Collector(verified);
        Collection<?> collection = (Collection<?>) verified.getObject();
        for (Object item : collection) {
            Verified itemTarget = new Verified(item, new Annotation[] { material.getAnnotation() },
                    verified.getContext());
            Collector checked = itemTarget.access();
            collector.collect(checked);
        }

        return collector;
    }

    /**
     * 校验数组对象元素
     *
     * @param verified 被校验对象
     * @param material 校验器属性
     * @return 校验结果
     */
    public Collector doArrayObject(Verified verified, Material material) {
        Collector collector = new Collector(verified);
        Object[] array = (Object[]) verified.getObject();
        for (int i = 0; i < array.length; i++) {
            Verified itemTarget = new Verified(array[i], new Annotation[] { material.getAnnotation() },
                    verified.getContext());
            Collector checked = itemTarget.access();
            collector.collect(checked);
        }
        return collector;
    }

    /**
     * 校验数组对象元素
     *
     * @param verified 被校验对象
     * @return 校验结果
     */
    public Collector doArrayInside(Verified verified) {
        Collector collector = new Collector(verified);
        Object[] array = (Object[]) verified.getObject();
        for (Object object : array) {
            collector.collect(inside(new Verified(object, verified.getContext())));
        }
        return collector;
    }

    /**
     * 校验集合对象元素
     *
     * @param verified 被校验对象
     * @return 校验结果
     */
    private Collector doCollectionInside(Verified verified) {
        Collector collector = new Collector(verified);
        Collection<?> collection = (Collection<?>) verified.getObject();
        for (Object item : collection) {
            collector.collect(inside(new Verified(item, verified.getContext())));
        }
        return collector;
    }

    /**
     * 是否为内部校验注解
     *
     * @param annotations 注解
     * @return 校验结果
     */
    public boolean hasInside(Annotation[] annotations) {
        return Arrays.stream(annotations).anyMatch(an -> an instanceof Inside);
    }

}