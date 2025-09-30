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
package org.miaixz.bus.mapper.parsing;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.miaixz.bus.core.lang.Optional;
import org.miaixz.bus.mapper.builder.ClassMetaResolver;
import org.miaixz.bus.mapper.builder.GenericTypeResolver;

/**
 * 抽象实体类查找器，根据泛型从返回值、参数、接口泛型参数判断对应的实体类类型
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public abstract class SchemaTypeParser implements ClassMetaResolver {

    /**
     * 查找当前方法对应的实体类，依次检查方法返回值、参数和接口泛型
     *
     * @param mapperType   Mapper 接口，不能为空
     * @param mapperMethod Mapper 接口方法，可以为空
     * @return 实体类类型的 Optional 包装对象
     */
    @Override
    public Optional<Class<?>> findClass(Class<?> mapperType, Method mapperMethod) {
        // 先判断返回值
        Optional<Class<?>> optionalClass;
        if (mapperMethod != null) {
            optionalClass = getClassByMapperMethodReturnType(mapperType, mapperMethod);
            if (optionalClass.isPresent()) {
                return optionalClass;
            }
            // 再判断参数
            optionalClass = getClassByMapperMethodParamTypes(mapperType, mapperMethod);
            if (optionalClass.isPresent()) {
                return optionalClass;
            }
            // 最后从接口泛型中获取
            optionalClass = getClassByMapperMethodAndMapperType(mapperType, mapperMethod);
            if (optionalClass.isPresent()) {
                return optionalClass;
            }
        }
        return getClassByMapperType(mapperType);
    }

    /**
     * 根据方法返回值类型获取实体类
     *
     * @param mapperType   Mapper 接口
     * @param mapperMethod 方法
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByMapperMethodReturnType(Class<?> mapperType, Method mapperMethod) {
        Class<?> returnType = GenericTypeResolver.getReturnType(mapperMethod, mapperType);
        return isClass(returnType) ? Optional.of(returnType) : Optional.empty();
    }

    /**
     * 根据方法参数类型获取实体类
     *
     * @param mapperType   Mapper 接口
     * @param mapperMethod 方法
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByMapperMethodParamTypes(Class<?> mapperType, Method mapperMethod) {
        return getClassByTypes(GenericTypeResolver.resolveParamTypes(mapperMethod, mapperType));
    }

    /**
     * 根据方法所在接口的泛型获取实体类，仅适用于定义在泛型接口中的方法
     *
     * @param mapperType   Mapper 接口
     * @param mapperMethod 方法
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByMapperMethodAndMapperType(Class<?> mapperType, Method mapperMethod) {
        return getClassByTypes(GenericTypeResolver.resolveMapperTypes(mapperMethod, mapperType));
    }

    /**
     * 根据接口泛型获取实体类，优先级最低，与当前执行方法无关
     *
     * @param mapperType Mapper 接口
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByMapperType(Class<?> mapperType) {
        return getClassByTypes(GenericTypeResolver.resolveMapperTypes(mapperType));
    }

    /**
     * 根据单个类型获取可能的实体类类型
     *
     * @param type 类型
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByType(Type type) {
        if (type instanceof Class) {
            return Optional.of((Class<?>) type);
        } else if (type instanceof GenericTypeResolver.ParameterizedTypes) {
            return getClassByTypes(((GenericTypeResolver.ParameterizedTypes) type).getActualTypeArguments());
        } else if (type instanceof GenericTypeResolver.WildcardTypes) {
            Optional<Class<?>> optionalClass = getClassByTypes(
                    ((GenericTypeResolver.WildcardTypes) type).getLowerBounds());
            if (optionalClass.isPresent()) {
                return optionalClass;
            }
            return getClassByTypes(((GenericTypeResolver.WildcardTypes) type).getUpperBounds());
        } else if (type instanceof GenericTypeResolver.GenericArrayTypes) {
            return getClassByType(((GenericTypeResolver.GenericArrayTypes) type).getGenericComponentType());
        }
        return Optional.empty();
    }

    /**
     * 遍历类型数组获取可能的实体类类型
     *
     * @param types 类型数组
     * @return 实体类类型的 Optional 包装对象
     */
    protected Optional<Class<?>> getClassByTypes(Type[] types) {
        for (Type type : types) {
            Optional<Class<?>> optionalClass = getClassByType(type);
            if (optionalClass.isPresent() && isClass(optionalClass.getOrNull())) {
                return optionalClass;
            }
        }
        return Optional.empty();
    }

}
