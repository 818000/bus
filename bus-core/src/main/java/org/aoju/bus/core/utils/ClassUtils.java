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
package org.aoju.bus.core.utils;

import org.aoju.bus.core.beans.BeanDesc;
import org.aoju.bus.core.beans.copier.BeanCopier;
import org.aoju.bus.core.beans.copier.CopyOptions;
import org.aoju.bus.core.beans.copier.ValueProvider;
import org.aoju.bus.core.convert.BasicType;
import org.aoju.bus.core.lang.*;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.lang.mutable.MutableObject;
import org.aoju.bus.core.loader.JarLoaders;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.System;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.*;

/**
 * Class工具类
 *
 * @author Kimi Liu
 * @version 5.8.5
 * @since JDK 1.8+
 */
public class ClassUtils {

    /**
     * 原始类型名和其class对应表,例如：int =》 int.class
     */
    private static final Map<String, Class<?>> primitiveWrapperMap = new HashMap<>();
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();
    private static SimpleCache<String, Class<?>> classCache = new SimpleCache<>();

    static {
        List<Class<?>> primitiveTypes = new ArrayList<>(32);
        // 加入原始类型
        primitiveTypes.addAll(BasicType.primitiveWrapperMap.keySet());
        // 加入原始类型数组类型
        primitiveTypes.add(boolean[].class);
        primitiveTypes.add(byte[].class);
        primitiveTypes.add(char[].class);
        primitiveTypes.add(double[].class);
        primitiveTypes.add(float[].class);
        primitiveTypes.add(int[].class);
        primitiveTypes.add(long[].class);
        primitiveTypes.add(short[].class);
        primitiveTypes.add(void.class);
        for (Class<?> primitiveType : primitiveTypes) {
            primitiveWrapperMap.put(primitiveType.getName(), primitiveType);
        }
    }

    /**
     * {@code null}安全的获取对象类型
     *
     * @param <T> 对象类型
     * @param obj 对象,如果为{@code null} 返回{@code null}
     * @return 对象类型, 提供对象如果为{@code null} 返回{@code null}
     */
    public static <T> Class<T> getClass(T obj) {
        return ((null == obj) ? null : (Class<T>) obj.getClass());
    }

    /**
     * 获取类名
     *
     * @param obj      获取类名对象
     * @param isSimple 是否简单类名,如果为true,返回不带包名的类名
     * @return 类名
     */
    public static String getClassName(Object obj, boolean isSimple) {
        if (null == obj) {
            return null;
        }
        final Class<?> clazz = obj.getClass();
        return getClassName(clazz, isSimple);
    }

    /**
     * 获取类名
     * 类名并不包含“.class”这个扩展名
     * 例如：ClassUtil这个类
     *
     * <pre>
     * isSimple为false: "org.aoju.core.utils.ClassUtils"
     * isSimple为true: "ClassUtils"
     * </pre>
     *
     * @param clazz    类
     * @param isSimple 是否简单类名,如果为true,返回不带包名的类名
     * @return 类名
     */
    public static String getClassName(Class<?> clazz, boolean isSimple) {
        if (null == clazz) {
            return null;
        }
        return isSimple ? clazz.getSimpleName() : clazz.getName();
    }

    /**
     * 获得对象数组的类数组
     *
     * @param objects 对象数组,如果数组中存在{@code null}元素,则此元素被认为是Object类型
     * @return 类数组
     */
    public static Class<?>[] getClasses(Object... objects) {
        Class<?>[] classes = new Class<?>[objects.length];
        Object obj;
        for (int i = 0; i < objects.length; i++) {
            obj = objects[i];
            classes[i] = (null == obj) ? Object.class : obj.getClass();
        }
        return classes;
    }

    /**
     * 指定类是否与给定的类名相同
     *
     * @param clazz      类
     * @param className  类名,可以是全类名（包含包名）,也可以是简单类名（不包含包名）
     * @param ignoreCase 是否忽略大小写
     * @return 指定类是否与给定的类名相同
     */
    public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
        if (null == clazz || StringUtils.isBlank(className)) {
            return false;
        }
        if (ignoreCase) {
            return className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName());
        } else {
            return className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
        }
    }

    /**
     * 获得指定类中的Public方法名
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     */
    public static Set<String> getPublicMethodNames(Class<?> clazz) {
        HashSet<String> methodSet = new HashSet<>();
        Method[] methodArray = getPublicMethods(clazz);
        for (Method method : methodArray) {
            String methodName = method.getName();
            methodSet.add(methodName);
        }
        return methodSet;
    }

    /**
     * 获得本类及其父类所有Public方法
     *
     * @param clazz 查找方法的类
     * @return 过滤后的方法列表
     */
    public static Method[] getPublicMethods(Class<?> clazz) {
        return clazz.getMethods();
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
        if (null == clazz) {
            return null;
        }

        Method[] methods = getPublicMethods(clazz);
        List<Method> methodList;
        if (null != filter) {
            methodList = new ArrayList<>();
            for (Method method : methods) {
                if (filter.accept(method)) {
                    methodList.add(method);
                }
            }
        } else {
            methodList = CollUtils.newArrayList(methods);
        }
        return methodList;
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz          查找方法的类
     * @param excludeMethods 不包括的方法
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
        final HashSet<Method> excludeMethodSet = CollUtils.newHashSet(excludeMethods);
        return getPublicMethods(clazz, method -> false == excludeMethodSet.contains(method));
    }

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz              查找方法的类
     * @param excludeMethodNames 不包括的方法名列表
     * @return 过滤后的方法列表
     */
    public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
        final HashSet<String> excludeMethodNameSet = CollUtils.newHashSet(excludeMethodNames);
        return getPublicMethods(clazz, method -> false == excludeMethodNameSet.contains(method.getName()));
    }

    /**
     * 查找指定Public方法 如果找不到对应的方法或方法不为public的则返回null
     *
     * @param clazz      类
     * @param methodName 方法名
     * @param paramTypes 参数类型
     * @return 方法
     * @throws SecurityException 无权访问抛出异常
     */
    public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    /**
     * 获得指定类中的Public方法名
     * 去重重载的方法
     *
     * @param clazz 类
     * @return 方法名Set
     */
    public static Set<String> getDeclaredMethodNames(Class<?> clazz) {
        return ReflectUtils.getMethodNames(clazz);
    }

    /**
     * 获得声明的所有方法,包括本类及其父类和接口的所有方法和Object类的方法
     *
     * @param clazz 类
     * @return 方法数组
     */
    public static Method[] getDeclaredMethods(Class<?> clazz) {
        return ReflectUtils.getMethods(clazz);
    }

    /**
     * 查找指定对象中的所有方法（包括非public方法）,也包括父对象和Object类的方法
     *
     * @param obj        被查找的对象
     * @param methodName 方法名
     * @param args       参数
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getDeclaredMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
        return getDeclaredMethod(obj.getClass(), methodName, getClasses(args));
    }

    /**
     * 查找指定类中的所有方法（包括非public方法）,也包括父类和Object类的方法 找不到方法会返回null
     *
     * @param clazz          被查找的类
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法
     * @throws SecurityException 无访问权限抛出异常
     */
    public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
        return ReflectUtils.getMethod(clazz, methodName, parameterTypes);
    }

    /**
     * 查找指定类中的所有字段（包括非public字段)
     *
     * @param clazz 被查找字段的类
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return clazz.getDeclaredFields();
    }

    /**
     * @return 获得Java ClassPath路径,不包括 jre
     */
    public static String[] getJavaClassPaths() {
        return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
    }

    /**
     * 比较判断types1和types2两组类,如果types1中所有的类都与types2对应位置的类相同,或者是其父类或接口,则返回true
     *
     * @param types1 类组1
     * @param types2 类组2
     * @return 是否相同、父类或接口
     */
    public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
        if (ArrayUtils.isEmpty(types1) && ArrayUtils.isEmpty(types2)) {
            return true;
        }
        if (types1.length != types2.length) {
            return false;
        }

        Class<?> type1;
        Class<?> type2;
        for (int i = 0; i < types1.length; i++) {
            type1 = types1[i];
            type2 = types2[i];
            if (isBasicType(type1) && isBasicType(type2)) {
                //原始类型和包装类型存在不一致情况
                if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
                    return false;
                }
            } else if (false == type1.isAssignableFrom(type2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 执行方法
     * 可执行Private方法,也可执行static方法
     * 执行非static方法时,必须满足对象有默认构造方法
     * 非单例模式,如果是非静态方法,每次创建一个新对象
     *
     * @param <T>                     对象类型
     * @param classNameWithMethodName 类名和方法名表达式,类名与方法名用<code>.</code>或<code>#</code>连接
     * @param args                    参数,必须严格对应指定方法的参数类型和数量
     * @return 返回结果
     */
    public static <T> T invoke(String classNameWithMethodName, Object[] args) {
        return invoke(classNameWithMethodName, false, args);
    }

    /**
     * 执行方法
     * 可执行Private方法,也可执行static方法
     * 执行非static方法时,必须满足对象有默认构造方法
     *
     * @param <T>                     对象类型
     * @param classNameWithMethodName 类名和方法名表达式
     * @param isSingleton             是否为单例对象,如果此参数为false,每次执行方法时创建一个新对象
     * @param args                    参数,必须严格对应指定方法的参数类型和数量
     * @return 返回结果
     */
    public static <T> T invoke(String classNameWithMethodName, boolean isSingleton, Object... args) {
        if (StringUtils.isBlank(classNameWithMethodName)) {
            throw new InstrumentException("Blank classNameDotMethodName!");
        }

        int splitIndex = classNameWithMethodName.lastIndexOf(Symbol.C_SHAPE);
        if (splitIndex <= 0) {
            splitIndex = classNameWithMethodName.lastIndexOf(Symbol.C_DOT);
        }
        if (splitIndex <= 0) {
            throw new InstrumentException("Invalid classNameWithMethodName [{}]!", classNameWithMethodName);
        }

        final String className = classNameWithMethodName.substring(0, splitIndex);
        final String methodName = classNameWithMethodName.substring(splitIndex + 1);

        return invoke(className, methodName, isSingleton, args);
    }

    /**
     * 执行方法
     * 可执行Private方法,也可执行static方法
     * 执行非static方法时,必须满足对象有默认构造方法
     * 非单例模式,如果是非静态方法,每次创建一个新对象
     *
     * @param <T>        对象类型
     * @param className  类名,完整类路径
     * @param methodName 方法名
     * @param args       参数,必须严格对应指定方法的参数类型和数量
     * @return 返回结果
     */
    public static <T> T invoke(String className, String methodName, Object[] args) {
        return invoke(className, methodName, false, args);
    }

    /**
     * 执行方法
     * 可执行Private方法,也可执行static方法
     * 执行非static方法时,必须满足对象有默认构造方法
     *
     * @param <T>         对象类型
     * @param className   类名,完整类路径
     * @param methodName  方法名
     * @param isSingleton 是否为单例对象,如果此参数为false,每次执行方法时创建一个新对象
     * @param args        参数,必须严格对应指定方法的参数类型和数量
     * @return 返回结果
     */
    public static <T> T invoke(String className, String methodName, boolean isSingleton, Object... args) {
        Class<Object> clazz = loadClass(className);
        try {
            final Method method = getDeclaredMethod(clazz, methodName, getClasses(args));
            if (null == method) {
                throw new NoSuchMethodException(StringUtils.format("No such method: [{}]", methodName));
            }
            if (isStatic(method)) {
                return ReflectUtils.invoke(null, method, args);
            } else {
                return ReflectUtils.invoke(isSingleton ? Singleton.get(clazz) : clazz.newInstance(), method, args);
            }
        } catch (Exception e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 是否为包装类型
     *
     * @param clazz 类
     * @return 是否为包装类型
     */
    public static boolean isPrimitiveWrapper(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return BasicType.wrapperPrimitiveMap.containsKey(clazz);
    }

    /**
     * 是否为基本类型（包括包装类和原始类）
     *
     * @param clazz 类
     * @return 是否为基本类型
     */
    public static boolean isBasicType(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
    }

    /**
     * 是否简单值类型或简单值类型的数组
     * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
     *
     * @param clazz 属性类
     * @return 是否简单值类型或简单值类型的数组
     */
    public static boolean isSimpleTypeOrArray(Class<?> clazz) {
        if (null == clazz) {
            return false;
        }
        return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
    }

    /**
     * 是否为简单值类型
     * 包括：
     * <pre>
     *     原始类型
     *     String
     *     CharSequence
     *     Number
     *     Date
     *     URI
     *     URL
     *     Locale
     *     Class
     * </pre>
     *
     * @param clazz 类
     * @param clazz 类
     * @return 是否为简单值类型
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return isBasicType(clazz)
                || clazz.isEnum()
                || CharSequence.class.isAssignableFrom(clazz)
                || Number.class.isAssignableFrom(clazz)
                || Date.class.isAssignableFrom(clazz)
                || clazz.equals(URI.class)
                || clazz.equals(URL.class)
                || clazz.equals(Locale.class)
                || clazz.equals(Class.class)
                || TemporalAccessor.class.isAssignableFrom(clazz);
    }

    /**
     * 检查目标类是否可以从原类转化
     * 转化包括：
     * 1、原类是对象,目标类型是原类型实现的接口
     * 2、目标类型是原类型的父类
     * 3、两者是原始类型或者包装类型（相互转换）
     *
     * @param classArray   目标类型
     * @param toClassArray 原类型
     * @return 是否可转化
     */
    public static boolean isAssignable(final Class<?>[] classArray, final Class<?>... toClassArray) {
        return isAssignable(classArray, toClassArray, true);
    }

    /**
     * 检查目标类是否可以从原类转化
     * 转化包括：
     * 1、原类是对象,目标类型是原类型实现的接口
     * 2、目标类型是原类型的父类
     * 3、两者是原始类型或者包装类型（相互转换）
     *
     * @param classArray   目标类型
     * @param toClassArray 原类型
     * @return 是否可转化
     */
    public static boolean isAssignable(final Class<?> classArray, final Class<?> toClassArray) {
        return isAssignable(classArray, toClassArray, true);
    }

    /**
     * 检查目标类是否可以从原类转化
     * 转化包括：
     * 1、原类是对象,目标类型是原类型实现的接口
     * 2、目标类型是原类型的父类
     * 3、两者是原始类型或者包装类型（相互转换）
     *
     * @param classArray   目标类型
     * @param toClassArray 原类型
     * @param autoboxing   自动操作
     * @return 是否可转化
     */
    public static boolean isAssignable(Class<?>[] classArray, Class<?>[] toClassArray, final boolean autoboxing) {
        if (!ArrayUtils.isSameLength(classArray, toClassArray)) {
            return false;
        }
        if (classArray == null) {
            classArray = Normal.EMPTY_CLASS_ARRAY;
        }
        if (toClassArray == null) {
            toClassArray = Normal.EMPTY_CLASS_ARRAY;
        }
        for (int i = 0; i < classArray.length; i++) {
            if (!isAssignable(classArray[i], toClassArray[i], autoboxing)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAssignable(Class<?> cls, final Class<?> toClass, final boolean autoboxing) {
        if (toClass == null) {
            return false;
        }
        if (cls == null) {
            return !toClass.isPrimitive();
        }
        if (autoboxing) {
            if (cls.isPrimitive() && !toClass.isPrimitive()) {
                cls = primitiveToWrapper(cls);
                if (cls == null) {
                    return false;
                }
            }
            if (toClass.isPrimitive() && !cls.isPrimitive()) {
                cls = wrapperToPrimitive(cls);
                if (cls == null) {
                    return false;
                }
            }
        }
        if (cls.equals(toClass)) {
            return true;
        }
        if (cls.isPrimitive()) {
            if (!toClass.isPrimitive()) {
                return false;
            }
            if (Integer.TYPE.equals(cls)) {
                return Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Long.TYPE.equals(cls)) {
                return Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Boolean.TYPE.equals(cls)) {
                return false;
            }
            if (Double.TYPE.equals(cls)) {
                return false;
            }
            if (Float.TYPE.equals(cls)) {
                return Double.TYPE.equals(toClass);
            }
            if (Character.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Short.TYPE.equals(cls)) {
                return Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            if (Byte.TYPE.equals(cls)) {
                return Short.TYPE.equals(toClass)
                        || Integer.TYPE.equals(toClass)
                        || Long.TYPE.equals(toClass)
                        || Float.TYPE.equals(toClass)
                        || Double.TYPE.equals(toClass);
            }
            // should never get here
            return false;
        }
        return toClass.isAssignableFrom(cls);
    }

    /**
     * 指定类是否为Public
     *
     * @param clazz 类
     * @return 是否为public
     */
    public static boolean isPublic(Class<?> clazz) {
        if (null == clazz) {
            throw new NullPointerException("Class to provided is null.");
        }
        return Modifier.isPublic(clazz.getModifiers());
    }

    /**
     * 指定方法是否为Public
     *
     * @param method 方法
     * @return 是否为public
     */
    public static boolean isPublic(Method method) {
        if (null == method) {
            throw new NullPointerException("Method to provided is null.");
        }
        return isPublic(method.getDeclaringClass());
    }

    /**
     * 指定类是否为非public
     *
     * @param clazz 类
     * @return 是否为非public
     */
    public static boolean isNotPublic(Class<?> clazz) {
        return false == isPublic(clazz);
    }

    /**
     * 指定方法是否为非public
     *
     * @param method 方法
     * @return 是否为非public
     */
    public static boolean isNotPublic(Method method) {
        return false == isPublic(method);
    }

    /**
     * 是否为静态方法
     *
     * @param method 方法
     * @return 是否为静态方法
     */
    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /**
     * 设置方法为可访问
     *
     * @param method 方法
     * @return 方法
     */
    public static Method setAccessible(Method method) {
        if (null != method && false == method.isAccessible()) {
            method.setAccessible(true);
        }
        return method;
    }

    /**
     * 是否为抽象类
     *
     * @param clazz 类
     * @return 是否为抽象类
     */
    public static boolean isAbstract(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    /**
     * 是否为标准的类
     * 这个类必须：
     * <pre>
     * 1、非接口
     * 2、非抽象类
     * 3、非Enum枚举
     * 4、非数组
     * 5、非注解
     * 6、非原始类型（int, long等）
     * </pre>
     *
     * @param clazz 类
     * @return 是否为标准类
     */
    public static boolean isNormalClass(Class<?> clazz) {
        return null != clazz
                && false == clazz.isInterface()
                && false == isAbstract(clazz)
                && false == clazz.isEnum()
                && false == clazz.isArray()
                && false == clazz.isAnnotation()
                && false == clazz.isSynthetic()
                && false == clazz.isPrimitive();
    }

    /**
     * 判断类是否为枚举类型
     *
     * @param clazz 类
     * @return 是否为枚举类型
     * @since 5.8.5
     */
    public static boolean isEnum(Class<?> clazz) {
        return null != clazz && clazz.isEnum();
    }

    /**
     * 获得给定类的第一个泛型参数
     *
     * @param clazz 被检查的类,必须是已经确定泛型类型的类
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz) {
        return getTypeArgument(clazz, 0);
    }

    /**
     * 获得给定类的泛型参数
     *
     * @param clazz 被检查的类,必须是已经确定泛型类型的类
     * @param index 泛型类型的索引号,既第几个泛型类型
     * @return {@link Class}
     */
    public static Class<?> getTypeArgument(Class<?> clazz, int index) {
        final Type argumentType = TypeUtils.getTypeArgument(clazz, index);
        if (null != argumentType && argumentType instanceof Class) {
            return (Class<?>) argumentType;
        }
        return null;
    }

    /**
     * 获得给定类所在包的名称
     * 例如：org.aoju.bus.core.utils
     *
     * @param clazz 类
     * @return 包名
     */
    public static String getPackage(Class<?> clazz) {
        if (clazz == null) {
            return Normal.EMPTY;
        }
        final String className = clazz.getName();
        int packageEndIndex = className.lastIndexOf(Symbol.DOT);
        if (packageEndIndex == -1) {
            return Normal.EMPTY;
        }
        return className.substring(0, packageEndIndex);
    }

    /**
     * 获得给定类所在包的路径
     * 例如：
     *
     * @param clazz 类
     * @return 包名
     */
    public static String getPackagePath(Class<?> clazz) {
        return getPackage(clazz).replace(Symbol.C_DOT, Symbol.C_SLASH);
    }

    /**
     * 获取指定类型分的默认值
     * 默认值规则为：
     * <pre>
     * 1、如果为原始类型,返回0
     * 2、非原始类型返回{@code null}
     * </pre>
     *
     * @param clazz 类
     * @return 默认值
     */
    public static Object getDefaultValue(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (long.class == clazz) {
                return 0L;
            } else if (int.class == clazz) {
                return 0;
            } else if (short.class == clazz) {
                return (short) 0;
            } else if (char.class == clazz) {
                return (char) 0;
            } else if (byte.class == clazz) {
                return (byte) 0;
            } else if (double.class == clazz) {
                return 0D;
            } else if (float.class == clazz) {
                return 0f;
            } else if (boolean.class == clazz) {
                return false;
            }
        }

        return null;
    }

    /**
     * 获得默认值列表
     *
     * @param classes 值类型
     * @return 默认值列表
     * @since 3.1.9
     */
    public static Object[] getDefaultValues(Class<?>... classes) {
        final Object[] values = new Object[classes.length];
        for (int i = 0; i < classes.length; i++) {
            values[i] = getDefaultValue(classes[i]);
        }
        return values;
    }

    /**
     * 判断是否为Bean对象
     * 判定方法是是否存在只有一个参数的setXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     */
    public static boolean isBean(Class<?> clazz) {
        if (isNormalClass(clazz)) {
            final Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getName().startsWith("set")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 对象转Map,不进行驼峰转下划线,不忽略值为空的字段
     *
     * @param bean bean对象
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean) {
        return beanToMap(bean, false, false);
    }

    /**
     * 对象转Map
     *
     * @param bean              bean对象
     * @param isToUnderlineCase 是否转换为下划线模式
     * @param ignoreNullValue   是否忽略值为空的字段
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
        return beanToMap(bean, new HashMap<>(), isToUnderlineCase, ignoreNullValue);
    }

    /**
     * 对象转Map
     *
     * @param bean              bean对象
     * @param targetMap         目标的Map
     * @param isToUnderlineCase 是否转换为下划线模式
     * @param ignoreNullValue   是否忽略值为空的字段
     * @return Map
     * @since 3.2.3
     */
    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, final boolean isToUnderlineCase, boolean ignoreNullValue) {
        if (bean == null) {
            return null;
        }

        return beanToMap(bean, targetMap, ignoreNullValue, key -> isToUnderlineCase ? StringUtils.toUnderlineCase(key) : key);
    }

    /**
     * 对象转Map
     * 通过实现{@link Editor} 可以自定义字段值,如果这个Editor返回null则忽略这个字段,以便实现：
     *
     * <pre>
     * 1. 字段筛选,可以去除不需要的字段
     * 2. 字段变换,例如实现驼峰转下划线
     * 3. 自定义字段前缀或后缀等等
     * </pre>
     *
     * @param bean            bean对象
     * @param targetMap       目标的Map
     * @param ignoreNullValue 是否忽略值为空的字段
     * @param keyEditor       属性字段（Map的key）编辑器,用于筛选、编辑key
     * @return Map
     */
    public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNullValue, Editor<String> keyEditor) {
        if (bean == null) {
            return null;
        }

        final Collection<BeanDesc.PropDesc> props = getBeanDesc(bean.getClass()).getProps();

        String key;
        Method getter;
        Object value;
        for (BeanDesc.PropDesc prop : props) {
            key = prop.getFieldName();
            // 过滤class属性
            // 得到property对应的getter方法
            getter = prop.getGetter();
            if (null != getter) {
                // 只读取有getter方法的属性
                try {
                    value = getter.invoke(bean);
                } catch (Exception ignore) {
                    continue;
                }
                if (false == ignoreNullValue || (null != value && false == value.equals(bean))) {
                    key = keyEditor.edit(key);
                    if (null != key) {
                        targetMap.put(key, value);
                    }
                }
            }
        }
        return targetMap;
    }

    /**
     * 获取{@link BeanDesc} Bean描述信息
     *
     * @param clazz Bean类
     * @return the object
     */
    public static BeanDesc getBeanDesc(Class<?> clazz) {
        return new BeanDesc(clazz);
    }

    /**
     * 获取{@link ClassLoader}
     * 获取顺序如下：
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取{@link ClassUtils}类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassUtils.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * 获得ClassPath,将编码后的中文路径解码为原字符
     * 这个ClassPath路径会文件路径被标准化处理
     *
     * @return ClassPath
     */
    public static String getClassPath() {
        return getClassPath(false);
    }

    /**
     * 获得ClassPath,这个ClassPath路径会文件路径被标准化处理
     *
     * @param isEncoded 是否编码路径中的中文
     * @return ClassPath
     */
    public static String getClassPath(boolean isEncoded) {
        final URL classPathURL = getClassPathURL();
        String url = isEncoded ? classPathURL.getPath() : UriUtils.getDecodedPath(classPathURL);
        return FileUtils.normalize(url);
    }

    /**
     * 获得ClassPath URL
     *
     * @return ClassPath URL
     */
    public static URL getClassPathURL() {
        return getResourceURL(Normal.EMPTY);
    }

    /**
     * 获得资源的URL
     * 路径用/分隔,例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param resource 资源（相对Classpath的路径）
     * @return 资源URL
     * @throws InstrumentException 异常
     * @see ResourceUtils#getResource(String)
     */
    public static URL getResourceURL(String resource) throws InstrumentException {
        return ResourceUtils.getResource(resource);
    }

    /**
     * 填充Bean的核心方法
     *
     * @param <T>           Bean类型
     * @param bean          Bean
     * @param valueProvider 值提供者
     * @param copyOptions   拷贝选项,见 {@link CopyOptions}
     * @return Bean
     */
    public static <T> T fillBean(T bean, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
        if (null == valueProvider) {
            return bean;
        }
        return BeanCopier.create(valueProvider, bean, copyOptions).copy();
    }

    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类,通过传入类的字符串,返回其对应的类名,使用默认ClassLoader并初始化类（调用static模块内容和初始化static属性）
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法,支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型,例如：int
     * 2、数组类型,例如：int[]、Long[]、String[]
     * 3、内部类,例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param <T>  对象
     * @param name 类名
     * @return 类名对应的类
     * @throws InstrumentException 没有类名对应的类时抛出此异常
     */
    public static <T> Class<T> loadClass(String name) throws InstrumentException {
        return loadClass(name, true);
    }

    /**
     * 加载类,通过传入类的字符串,返回其对应的类名,使用默认ClassLoader
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法,支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型,例如：int
     * 2、数组类型,例如：int[]、Long[]、String[]
     * 3、内部类,例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param <T>           对象
     * @param name          类名
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @throws InstrumentException 没有类名对应的类时抛出此异常
     */
    public static <T> Class<T> loadClass(String name, boolean isInitialized) throws InstrumentException {
        return (Class<T>) loadClass(name, null, isInitialized);
    }

    /**
     * 加载类,通过传入类的字符串,返回其对应的类名
     * 此方法支持缓存,第一次被加载的类之后会读取缓存中的类
     * 加载失败的原因可能是此类不存在或其关联引用类不存在
     * 扩展{@link Class#forName(String, boolean, ClassLoader)}方法,支持以下几类类名的加载：
     *
     * <pre>
     * 1、原始类型,例如：int
     * 2、数组类型,例如：int[]、Long[]、String[]
     * 3、内部类,例如：java.lang.Thread.State会被转为java.lang.Thread$State加载
     * </pre>
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader},{@code null} 则使用系统默认ClassLoader
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     * @throws InstrumentException 没有类名对应的类时抛出此异常
     */
    public static Class<?> loadClass(String name, ClassLoader classLoader, boolean isInitialized) throws InstrumentException {
        Assert.notNull(name, "Name must not be null");

        // 加载原始类型和缓存中的类
        Class<?> clazz = loadPrimitiveClass(name);
        if (clazz == null) {
            clazz = classCache.get(name);
        }
        if (clazz != null) {
            return clazz;
        }

        if (name.endsWith(Symbol.BRACKET)) {
            // 对象数组"java.lang.String[]"风格
            final String elementClassName = name.substring(0, name.length() - Symbol.BRACKET.length());
            final Class<?> elementClass = loadClass(elementClassName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(Symbol.NON_PREFIX) && name.endsWith(Symbol.SEMICOLON)) {
            // "[Ljava.lang.String;" 风格
            final String elementName = name.substring(Symbol.NON_PREFIX.length(), name.length() - 1);
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else if (name.startsWith(Symbol.BRACKET_LEFT)) {
            // "[[I" 或 "[[Ljava.lang.String;" 风格
            final String elementName = name.substring(Symbol.BRACKET_LEFT.length());
            final Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
            clazz = Array.newInstance(elementClass, 0).getClass();
        } else {
            // 加载普通类
            if (null == classLoader) {
                classLoader = getClassLoader();
            }
            try {
                clazz = Class.forName(name, isInitialized, classLoader);
            } catch (ClassNotFoundException ex) {
                // 尝试获取内部类,例如java.lang.Thread.State =》java.lang.Thread$State
                clazz = tryLoadInnerClass(name, classLoader, isInitialized);
                if (null == clazz) {
                    throw new InstrumentException(ex);
                }
            }
        }

        // 加入缓存并返回
        return classCache.put(name, clazz);
    }

    /**
     * 加载原始类型的类 包括原始类型、原始类型数组和void
     *
     * @param name 原始类型名,比如 int
     * @return 原始类型类
     */
    public static Class<?> loadPrimitiveClass(String name) {
        Class<?> result = null;
        if (StringUtils.isNotBlank(name)) {
            name = name.trim();
            if (name.length() <= 8) {
                result = primitiveWrapperMap.get(name);
            }
        }
        return result;
    }

    /**
     * 创建新的{@link JarLoaders},并使用此Classloader加载目录下的class文件和jar文件
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @return {@link JarLoaders}
     */
    public static JarLoaders getJarClassLoader(File jarOrDir) {
        return JarLoaders.load(jarOrDir);
    }

    /**
     * 加载外部类
     *
     * @param jarOrDir jar文件或者包含jar和class文件的目录
     * @param name     类名
     * @return 类
     */
    public static Class<?> loadClass(File jarOrDir, String name) {
        try {
            return getJarClassLoader(jarOrDir).loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new InstrumentException(e);
        }
    }

    /**
     * 指定类是否被提供,使用默认ClassLoader
     * 通过调用{@link #loadClass(String, ClassLoader, boolean)}方法尝试加载指定类名的类,如果加载失败返回false
     * 加载失败的原因可能是此类不存在或其关联引用类不存在
     *
     * @param className 类名
     * @return 是否被提供
     */
    public static boolean isPresent(String className) {
        return isPresent(className, null);
    }

    /**
     * 指定类是否被提供
     * 通过调用{@link #loadClass(String, ClassLoader, boolean)}方法尝试加载指定类名的类,如果加载失败返回false
     * 加载失败的原因可能是此类不存在或其关联引用类不存在
     *
     * @param className   类名
     * @param classLoader {@link ClassLoader}
     * @return 是否被提供
     */
    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            loadClass(className, classLoader, false);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * 尝试转换并加载内部类,例如java.lang.Thread.State =》java.lang.Thread$State
     *
     * @param name          类名
     * @param classLoader   {@link ClassLoader},{@code null} 则使用系统默认ClassLoader
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     */
    private static Class<?> tryLoadInnerClass(String name, ClassLoader classLoader, boolean isInitialized) {
        // 尝试获取内部类,例如java.lang.Thread.State =》java.lang.Thread$State
        final int lastDotIndex = name.lastIndexOf(Symbol.C_DOT);
        if (lastDotIndex > 0) {// 类与内部类的分隔符不能在第一位,因此>0
            final String innerClassName = name.substring(0, lastDotIndex) + Symbol.C_DOLLAR + name.substring(lastDotIndex + 1);
            try {
                return Class.forName(innerClassName, isInitialized, classLoader);
            } catch (ClassNotFoundException ex2) {
                // 尝试获取内部类失败时,忽略之
            }
        }
        return null;
    }

    /**
     * 获取给定类的包的名称.
     * 类似{@code java.lang.String} 字符串类
     *
     * @param clazz 类
     * @return 包名，如果类在默认包中定义，则为空字符串
     */
    public static String getPackageName(Class<?> clazz) {
        return getPackageName(clazz.getName());
    }

    /**
     * 获取给定类的包的名称.
     * 类似{@code java.lang.String} 字符串类.
     *
     * @param className 完整的类名
     * @return 包名，如果类在默认包中定义，则为空字符串
     */
    public static String getPackageName(String className) {
        Assert.notNull(className, "Class name must not be null");
        int lastDotIndex = className.lastIndexOf(Symbol.C_DOT);
        return (lastDotIndex != -1 ? className.substring(0, lastDotIndex) : Normal.EMPTY);
    }

    /**
     * 替换{@code Class. forname()}，它也返回原语的类实例(例如“int”)和数组类名(例如“[]”).
     * 还能够以Java源代码风格解析内部类名(例如，“java.lang.Thread 用" State"代替"java.lang.Thread$State").
     *
     * @param name        类的名称
     * @param classLoader 要使用的类装入器(可能是{@code null}，表示默认的类装入器)
     * @return 提供的名称的类实例
     * @throws ClassNotFoundException 如果没有找到该类
     * @throws LinkageError           如果无法加载类文件
     */
    public static Class<?> forName(String name, ClassLoader classLoader)
            throws ClassNotFoundException, LinkageError {

        Assert.notNull(name, "Name must not be null");

        Class<?> clazz = resolvePrimitiveClassName(name);
        if (clazz != null) {
            return clazz;
        }

        if (name.endsWith(Symbol.BRACKET)) {
            String elementClassName = name.substring(0, name.length() - Symbol.BRACKET.length());
            Class<?> elementClass = forName(elementClassName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        if (name.startsWith(Symbol.NON_PREFIX) && name.endsWith(Symbol.SEMICOLON)) {
            String elementName = name.substring(Symbol.NON_PREFIX.length(), name.length() - 1);
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        if (name.startsWith(Symbol.BRACKET_LEFT)) {
            String elementName = name.substring(Symbol.BRACKET_LEFT.length());
            Class<?> elementClass = forName(elementName, classLoader);
            return Array.newInstance(elementClass, 0).getClass();
        }

        ClassLoader clToUse = classLoader;
        if (clToUse == null) {
            clToUse = getDefaultClassLoader();
        }
        try {
            return Class.forName(name, false, clToUse);
        } catch (ClassNotFoundException ex) {
            int lastDotIndex = name.lastIndexOf(Symbol.C_DOT);
            if (lastDotIndex != -1) {
                String innerClassName =
                        name.substring(0, lastDotIndex) + Symbol.C_DOLLAR + name.substring(lastDotIndex + 1);
                try {
                    return Class.forName(innerClassName, false, clToUse);
                } catch (ClassNotFoundException ex2) {
                }
            }
            throw ex;
        }
    }

    public static Class<?> resolvePrimitiveClassName(String name) {
        Class<?> result = null;
        // 大多数类名都很长，因为它们应该放在包中，所以长度检查是值得的.
        if (name != null && name.length() <= 8) {
            // Could be a primitive - likely.
            result = primitiveWrapperMap.get(name);
        }
        return result;
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                }
            }
        }
        return cl;
    }

    /**
     * 获取类名减去{@code Class}的包名
     *
     * @param object      要获取其短名称的类可能为空
     * @param valueIfNull 如果为空，返回的值
     * @return 没有包名或空值的对象的类名
     */
    public static String getShortClassName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        return getShortClassName(object.getClass());
    }

    /**
     * 从{@code Class}中获取类名减去包名.
     *
     * @param cls 类的短名称.
     * @return 没有包名或空字符串的类名
     */
    public static String getShortClassName(final Class<?> cls) {
        if (cls == null) {
            return Normal.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    /**
     * 从字符串中获取类名减去包名
     *
     * @param className 获取短名称的类名
     * @return 没有包名或空字符串的类的类名
     */
    public static String getShortClassName(String className) {
        final List<String> packages = StringUtils.split(className, Symbol.C_DOT);
        if (null == packages || packages.size() < 2) {
            return className;
        }

        final int size = packages.size();
        final StringBuilder result = StringUtils.builder();
        result.append(packages.get(0).charAt(0));
        for (int i = 1; i < size - 1; i++) {
            result.append(Symbol.C_DOT).append(packages.get(i).charAt(0));
        }
        result.append(Symbol.C_DOT).append(packages.get(size - 1));
        return result.toString();
    }

    /**
     * 简单的类名
     *
     * @param cls 要为其获取简单名称的类;可能是零
     * @return 简单的类名
     * @see Class#getSimpleName()
     * @since 3.0
     */
    public static String getSimpleName(final Class<?> cls) {
        return getSimpleName(cls, Normal.EMPTY);
    }

    /**
     * 简单的类名
     *
     * @param cls         要为其获取简单名称的类
     * @param valueIfNull 如果为空，返回的值
     * @return 简单的类名
     * @see Class#getSimpleName()
     * @since 3.0
     */
    public static String getSimpleName(final Class<?> cls, String valueIfNull) {
        return cls == null ? valueIfNull : cls.getSimpleName();
    }

    /**
     * 简单的类名
     *
     * @param object 获取简单类名的对象;可能是零
     * @return 简单的类名
     * @see Class#getSimpleName()
     */
    public static String getSimpleName(final Object object) {
        return getSimpleName(object, Normal.EMPTY);
    }

    /**
     * 简单的类名
     *
     * @param object      要为其获取简单名称的对象
     * @param valueIfNull 对象或者null
     * @return 简单的类名
     * @see Class#getSimpleName()
     * @since 3.0
     */
    public static String getSimpleName(final Object object, final String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getSimpleName();
    }

    /**
     * 将指定的基元类对象转换为其对应的包装器类对象
     *
     * @param cls 要转换的类可以为null
     * @return 如果输入为{@code null},则返回{@code cls}，
     * 否则返回{@code cls} 的包装器类
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
    }

    /**
     * 将原语类对象的指定数组转换为其相应包装器类对象的数组
     *
     * @param classes 要转换的类数组可以为空或空
     * @return 包含每个给定类、包装器类或原始类(如果类不是原语)的数组.
     * {@code null}如果输入为空。如果传入的是空数组，则为空数组.
     */
    public static Class<?>[] primitivesToWrappers(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = primitiveToWrapper(classes[i]);
        }
        return convertedClasses;
    }

    /**
     * 将指定的包装器类转换为其对应的基元类
     *
     * @param cls 要转换的类，可以是null
     * @return 对应的原类型if {@code cls}是包装类，否则null
     * @see #primitiveToWrapper(Class)
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
    }

    /**
     * 将包装器类对象的指定数组转换为其相应基元类对象的数组
     *
     * @param classes 要转换的类数组可以为空或空
     * @return 一个数组，其中包含每个给定类的基元类或null(如果原始类不是包装类).
     * {@code null}如果输入为空。如果传入的是空数组，则为空数组.
     * @see #wrapperToPrimitive(Class)
     */
    public static Class<?>[] wrappersToPrimitives(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        for (int i = 0; i < classes.length; i++) {
            convertedClasses[i] = wrapperToPrimitive(classes[i]);
        }
        return convertedClasses;
    }

    /**
     * 获取由给定接口实现的所有接口的{@code List} 类及其超类.
     *
     * @param cls 要查找的类可能是{@code null}
     * @return 接口的{@code List}按顺序排列，{@code null}如果输入为空
     */
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    /**
     * 获取指定类的接口.
     *
     * @param cls             要查找的类可能是{@code null}
     * @param interfacesFound 类接口的{@code Set}
     */
    public static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * 获取给定类的超类的{@code List}.
     *
     * @param cls 要查找的类可能是{@code null}
     * @return 类的{@code List}从这个{@code null}开始，如果输入为空
     */
    public static List<Class<?>> getAllSuperclasses(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    public static Class<?>[] toClass(final Object... array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_CLASS_ARRAY;
        }
        final Class<?>[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }

    /**
     * 获取一个{@link Iterable}，它可以按照从子类到超类的升序遍历类层次结构，不包括接口.
     *
     * @param type 获取类层次结构的类型
     * @return 可迭代的在给定类的类层次结构上的可迭代的
     */
    public static Iterable<Class<?>> hierarchy(final Class<?> type) {
        return hierarchy(type, Interfaces.EXCLUDE);
    }

    /**
     * 获取一个{@link Iterable}，它可以按照从子类到超类的升序遍历类层次结构.
     *
     * @param type               获取类层次结构的类型
     * @param interfacesBehavior 指示是否包含或排除接口的开关
     * @return 可迭代的在给定类的类层次结构上的可迭代的
     */
    public static Iterable<Class<?>> hierarchy(final Class<?> type, final Interfaces interfacesBehavior) {
        final Iterable<Class<?>> classes = () -> {
            final MutableObject<Class<?>> next = new MutableObject<>(type);
            return new Iterator<Class<?>>() {

                @Override
                public boolean hasNext() {
                    return next.get() != null;
                }

                @Override
                public Class<?> next() {
                    final Class<?> result = next.get();
                    next.set(result.getSuperclass());
                    return result;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

            };
        };
        if (interfacesBehavior != Interfaces.INCLUDE) {
            return classes;
        }
        return () -> {
            final Set<Class<?>> seenInterfaces = new HashSet<>();
            final Iterator<Class<?>> wrapped = classes.iterator();

            return new Iterator<Class<?>>() {
                Iterator<Class<?>> interfaces = Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    return interfaces.hasNext() || wrapped.hasNext();
                }

                @Override
                public Class<?> next() {
                    if (interfaces.hasNext()) {
                        final Class<?> nextInterface = interfaces.next();
                        seenInterfaces.add(nextInterface);
                        return nextInterface;
                    }
                    final Class<?> nextSuperclass = wrapped.next();
                    final Set<Class<?>> currentInterfaces = new LinkedHashSet<>();
                    walkInterfaces(currentInterfaces, nextSuperclass);
                    interfaces = currentInterfaces.iterator();
                    return nextSuperclass;
                }

                private void walkInterfaces(final Set<Class<?>> addTo, final Class<?> c) {
                    for (final Class<?> iface : c.getInterfaces()) {
                        if (!seenInterfaces.contains(iface)) {
                            addTo.add(iface);
                        }
                        walkInterfaces(addTo, iface);
                    }
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

            };
        };
    }

    /**
     * 返回给定的{@code type}是原始包装器还是原始包装器
     * ({@link Boolean}， {@link Byte}， {@link Character}，
     * {@link Short}， {@link Integer}， {@link Long}，
     * {@link Double}， {@link Float}).
     *
     * @param type 要查询或空的类.
     * @return 如果给定的{@code type}是一个原始或原始包装器({@link Boolean}，
     * {@link Byte}， {@link Character}， {@link Short}， {@link Integer}，
     * {@link Long}， {@link Double}， {@link Float})，则为真..
     */
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    public static boolean isUserLevelMethod(Method method) {
        Assert.notNull(method, "Method must not be null");
        return (method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method)));
    }

    private static boolean isGroovyObjectMethod(Method method) {
        return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
    }

    /**
     * 把一个'.'的类路径转换为基于/的类路径
     *
     * @param className 完整雷鸣
     * @return 对应的资源路径，指向类
     */
    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace(Symbol.C_DOT, Symbol.C_SLASH);
    }

    /**
     * 获取对应类的默认变量名
     *
     * @param className 类名称
     * @return 类的默认变量名
     */
    public static String getClassVar(final String className) {
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * 获取类所有的字段信息
     * ps: 这个方法有个问题 如果子类和父类有相同的字段 会不会重复
     * 1. 还会获取到 serialVersionUID 这个字段
     *
     * @param clazz 类
     * @return 字段列表
     */
    public static List<Field> getAllFieldList(final Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        Class tempClass = clazz;
        while (tempClass != null) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }

        for (Field field : fieldList) {
            field.setAccessible(true);
        }
        return fieldList;
    }

    /**
     * 获取对象的实例化
     *
     * @param clazz 类
     * @param <T>   泛型
     * @return 实例化对象
     */
    public static <T> T newInstance(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有字段的 read 方法列表
     *
     * @param clazz 类信息
     * @return 方法列表
     * @throws IntrospectionException if any
     */
    public static List<Method> getAllFieldsReadMethods(final Class clazz) throws IntrospectionException {
        List<Field> fieldList = getAllFieldList(clazz);
        if (CollUtils.isEmpty(fieldList)) {
            return Collections.emptyList();
        }

        List<Method> methods = new ArrayList<>();
        for (Field field : fieldList) {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            //获得get方法
            Method getMethod = pd.getReadMethod();
            methods.add(getMethod);
        }
        return methods;
    }

    /**
     * 调用没有参数的命名方法.
     *
     * <p>此方法将委托给 {@link #getMatchingAccessibleMethod(Class, String, Class[])}.</p>
     * <p>这是包装器{@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.</p>
     *
     * @param object     调用此对象上的方法
     * @param methodName 具有此名称的get方法
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * @since 3.5.0
     */
    public static Object invokeMethod(final Object object,
                                      final String methodName) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, methodName, Normal.EMPTY_OBJECT_ARRAY, null);
    }

    /**
     * 调用没有参数的命名方法.
     *
     * <p>此方法将委托给 {@link #getMatchingAccessibleMethod(Class, String, Class[])}.</p>
     * <p>这是包装器{@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.</p>
     *
     * @param object      调用此对象上的方法
     * @param forceAccess 强制访问调用方法，即使该方法不可访问
     * @param methodName  具有此名称的get方法
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * @since 3.5.0
     */
    public static Object invokeMethod(final Object object,
                                      final boolean forceAccess,
                                      final String methodName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(object, forceAccess, methodName, Normal.EMPTY_OBJECT_ARRAY, null);
    }

    /**
     * 调用其参数类型与对象类型匹配的已命名方法.
     *
     * <p>此方法将委托给 {@link #getMatchingAccessibleMethod(Class, String, Class[])}.</p>
     * <p>这是包装器{@link #invokeMethod(Object object, String methodName, Object[] args, Class[] parameterTypes)}.</p>
     *
     * @param object     调用此对象上的方法
     * @param methodName 具有此名称的get方法
     * @param args       参数信息
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeMethod(final Object object,
                                      final String methodName,
                                      Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = toClass(args);
        return invokeMethod(object, methodName, args, parameterTypes);
    }

    /**
     * 调用其参数类型与对象类型匹配的已命名方法
     *
     * @param object      调用此对象上的方法
     * @param forceAccess 强制访问调用方法，即使该方法不可访问
     * @param methodName  具有此名称的get方法
     * @param args        参数信息
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * @since 3.5.0
     */
    public static Object invokeMethod(final Object object,
                                      final boolean forceAccess,
                                      final String methodName,
                                      Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = toClass(args);
        return invokeMethod(object, forceAccess, methodName, args, parameterTypes);
    }

    /**
     * 调用其参数类型与对象类型匹配的已命名方法.
     *
     * @param object         调用此对象上的方法
     * @param forceAccess    强制访问调用方法，即使该方法不可访问
     * @param methodName     具有此名称的get方法
     * @param args           参数信息
     * @param parameterTypes 参数类型
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * @since 3.5.0
     */
    public static Object invokeMethod(final Object object,
                                      final boolean forceAccess,
                                      final String methodName,
                                      Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        args = ArrayUtils.nullToEmpty(args);

        final String messagePrefix;
        Method method;

        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(),
                    methodName, parameterTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingAccessibleMethod(object.getClass(),
                    methodName, parameterTypes);
        }

        if (method == null) {
            throw new NoSuchMethodException(messagePrefix
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        args = toVarArgs(method, args);

        return method.invoke(object, args);
    }

    /**
     * 调用其参数类型与对象类型匹配的已命名方法.
     *
     * @param object         调用此对象上的方法
     * @param methodName     具有此名称的get方法
     * @param args           参数信息
     * @param parameterTypes 参数类型
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeMethod(final Object object,
                                      final String methodName,
                                      final Object[] args,
                                      final Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        return invokeMethod(object, false, methodName, args, parameterTypes);
    }

    /**
     * 调用参数类型与对象类型完全匹配的方法
     *
     * @param object     调用此对象上的方法
     * @param methodName 具有此名称的get方法
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * @since 3.5.0
     */
    public static Object invokeExactMethod(final Object object, final String methodName) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return invokeExactMethod(object, methodName, Normal.EMPTY_OBJECT_ARRAY, null);
    }

    /**
     * 调用没有参数的方法
     *
     * @param object     调用此对象上的方法
     * @param methodName 具有此名称的get方法
     * @param args       参数信息
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeExactMethod(final Object object,
                                           final String methodName,
                                           Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = toClass(args);
        return invokeExactMethod(object, methodName, args, parameterTypes);
    }

    /**
     * 调用参数类型与给定参数类型完全匹配的方法
     *
     * @param object         调用此对象上的方法
     * @param methodName     具有此名称的get方法
     * @param args           参数信息
     * @param parameterTypes 参数类型
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeExactMethod(final Object object,
                                           final String methodName,
                                           Object[] args,
                                           Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        final Method method = getAccessibleMethod(object.getClass(), methodName,
                parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    /**
     * 调用参数类型与给定参数类型完全匹配的{@code static}方法
     *
     * @param cls            调用该类上的静态方法
     * @param methodName     具有此名称的get方法
     * @param args           参数信息
     * @param parameterTypes 参数类型
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeExactStaticMethod(final Class<?> cls,
                                                 final String methodName,
                                                 Object[] args,
                                                 Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        final Method method = getAccessibleMethod(cls, methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on class: " + cls.getName());
        }
        return method.invoke(null, args);
    }

    /**
     * 调用一个名为{@code static}的方法，该方法的参数类型与对象类型匹配
     *
     * @param cls        调用该类上的静态方法
     * @param methodName 具有此名称的get方法
     * @param args       参数信息
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeStaticMethod(final Class<?> cls,
                                            final String methodName,
                                            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = toClass(args);
        return invokeStaticMethod(cls, methodName, args, parameterTypes);
    }

    /**
     * 调用一个名为{@code static}的方法，该方法的参数类型与对象类型匹配
     *
     * @param cls            调用该类上的静态方法
     * @param methodName     具有此名称的get方法
     * @param args           参数信息
     * @param parameterTypes 参数类型
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeStaticMethod(final Class<?> cls,
                                            final String methodName,
                                            Object[] args,
                                            Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
        final Method method = getMatchingAccessibleMethod(cls, methodName,
                parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on class: " + cls.getName());
        }
        args = toVarArgs(method, args);
        return method.invoke(null, args);
    }

    private static Object[] toVarArgs(final Method method, Object[] args) {
        if (method.isVarArgs()) {
            final Class<?>[] methodParameterTypes = method.getParameterTypes();
            args = getVarArgs(args, methodParameterTypes);
        }
        return args;
    }

    /**
     * 给定一个传递给varargs方法的参数数组，
     * 返回一个规范形式的参数数组，
     * 即一个声明了参数数量的数组，
     * 其最后一个参数是varargs类型的数组
     *
     * @param args                 传递给varags方法的参数数组
     * @param methodParameterTypes 方法参数类型的声明数组
     * @return 传递给方法的可变参数数组
     * @since 3.5.0
     */
    static Object[] getVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length
                && args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1])) {
            return args;
        }

        final Object[] newArgs = new Object[methodParameterTypes.length];
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);
        final Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        final int varArgLength = args.length - methodParameterTypes.length + 1;

        Object varArgsArray = Array.newInstance(primitiveToWrapper(varArgComponentType), varArgLength);
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);

        if (varArgComponentType.isPrimitive()) {
            varArgsArray = ArrayUtils.toPrimitive(varArgsArray);
        }

        newArgs[methodParameterTypes.length - 1] = varArgsArray;
        return newArgs;
    }

    /**
     * 调用参数类型与对象类型完全匹配的{@code static}方法
     *
     * @param cls        调用该类上的静态方法
     * @param methodName 具有此名称的get方法
     * @param args       参数信息
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的可访问方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     */
    public static Object invokeExactStaticMethod(final Class<?> cls,
                                                 final String methodName,
                                                 Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ArrayUtils.nullToEmpty(args);
        final Class<?>[] parameterTypes = toClass(args);
        return invokeExactStaticMethod(cls, methodName, args, parameterTypes);
    }

    /**
     * 返回具有给定名称和参数的可访问方法(即可以通过反射调用的方法)
     *
     * @param cls            从这个类获取方法
     * @param methodName     具有此名称的get方法
     * @param parameterTypes 参数类型
     * @return 访问方法
     */
    public static Method getAccessibleMethod(final Class<?> cls,
                                             final String methodName,
                                             final Class<?>... parameterTypes) {
        try {
            return getAccessibleMethod(cls.getMethod(methodName,
                    parameterTypes));
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 返回实现指定方法的可访问方法(即可以通过反射调用的方法)。如果找不到这样的方法，返回{@code null}
     *
     * @param method 我们希望调用的方法
     * @return 访问方法
     */
    public static Method getAccessibleMethod(Method method) {
        if (!MemberUtils.isAccessible(method)) {
            return null;
        }
        final Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();

        // 检查实现的接口和子接口
        method = getAccessibleMethodFromInterfaceNest(cls, methodName,
                parameterTypes);

        // 检查超类链
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName,
                    parameterTypes);
        }
        return method;
    }

    /**
     * 通过扫描超类返回可访问的方法(即可以通过反射调用的方法)
     *
     * @param cls            从这个类获取方法
     * @param methodName     要调用的方法的方法名
     * @param parameterTypes 参数类型
     * @return 如果没有找到可访问的方法返回{@code null}
     */
    private static Method getAccessibleMethodFromSuperclass(final Class<?> cls,
                                                            final String methodName,
                                                            final Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (parentClass != null) {
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    return parentClass.getMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    return null;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
        return null;
    }

    /**
     * 通过扫描所有实现的接口和子接口，返回实现指定方法的可访问方法(即可以通过反射调用的方法).
     *
     * @param cls            从这个类获取方法
     * @param methodName     要调用的方法的方法名
     * @param parameterTypes 参数类型
     * @return 如果没有找到可访问的方法返回{@code null}
     */
    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls,
                                                               final String methodName,
                                                               final Class<?>... parameterTypes) {

        for (; cls != null; cls = cls.getSuperclass()) {

            final Class<?>[] interfaces = cls.getInterfaces();
            for (final Class<?> anInterface : interfaces) {
                if (!Modifier.isPublic(anInterface.getModifiers())) {
                    continue;
                }
                try {
                    return anInterface.getDeclaredMethod(methodName,
                            parameterTypes);
                } catch (final NoSuchMethodException e) {
                }
                final Method method = getAccessibleMethodFromInterfaceNest(anInterface,
                        methodName, parameterTypes);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 查找与给定名称匹配且具有兼容参数的可访问方法.
     * 相容参数是指每个方法参数都可以从给定的参数中分配
     *
     * @param cls            在这个类中查找方法
     * @param methodName     使用此名称查找方法
     * @param parameterTypes 寻找参数最一致的方法
     * @return 访问方法
     */
    public static Method getMatchingAccessibleMethod(final Class<?> cls,
                                                     final String methodName,
                                                     final Class<?>... parameterTypes) {
        try {
            final Method method = cls.getMethod(methodName, parameterTypes);
            MemberUtils.setAccessibleWorkaround(method);
            return method;
        } catch (final NoSuchMethodException e) {
        }
        Method bestMatch = null;
        final Method[] methods = cls.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName) &&
                    MemberUtils.isMatchingMethod(method, parameterTypes)) {
                final Method accessibleMethod = getAccessibleMethod(method);
                if (accessibleMethod != null && (bestMatch == null || MemberUtils.compareMethodFit(
                        accessibleMethod,
                        bestMatch,
                        parameterTypes) < 0)) {
                    bestMatch = accessibleMethod;
                }
            }
        }
        if (bestMatch != null) {
            MemberUtils.setAccessibleWorkaround(bestMatch);
        }

        if (bestMatch != null && bestMatch.isVarArgs() && bestMatch.getParameterTypes().length > 0 && parameterTypes.length > 0) {
            final Class<?>[] methodParameterTypes = bestMatch.getParameterTypes();
            final Class<?> methodParameterComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
            final String methodParameterComponentTypeName = primitiveToWrapper(methodParameterComponentType).getName();
            final String parameterTypeName = parameterTypes[parameterTypes.length - 1].getName();
            final String parameterTypeSuperClassName = parameterTypes[parameterTypes.length - 1].getSuperclass().getName();

            if (!methodParameterComponentTypeName.equals(parameterTypeName)
                    && !methodParameterComponentTypeName.equals(parameterTypeSuperClassName)) {
                return null;
            }
        }

        return bestMatch;
    }

    /**
     * 检索是否可访问的方法
     *
     * @param cls            在这个类中查找方法
     * @param methodName     使用此名称查找方法
     * @param parameterTypes 寻找参数最一致的方法
     * @return 访问方法
     */
    public static Method getMatchingMethod(final Class<?> cls,
                                           final String methodName,
                                           final Class<?>... parameterTypes) {
        Assert.notNull(cls, "Null class not allowed.");
        Assert.notEmpty(methodName, "Null or blank methodName not allowed.");

        // Address methods in superclasses
        Method[] methodArray = cls.getDeclaredMethods();
        final List<Class<?>> superclassList = getAllSuperclasses(cls);
        for (final Class<?> klass : superclassList) {
            methodArray = ArrayUtils.addAll(methodArray, klass.getDeclaredMethods());
        }

        Method inexactMatch = null;
        for (final Method method : methodArray) {
            if (methodName.equals(method.getName()) &&
                    Objects.deepEquals(parameterTypes, method.getParameterTypes())) {
                return method;
            } else if (methodName.equals(method.getName()) &&
                    isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                if (inexactMatch == null) {
                    inexactMatch = method;
                } else if (distance(parameterTypes, method.getParameterTypes())
                        < distance(parameterTypes, inexactMatch.getParameterTypes())) {
                    inexactMatch = method;
                }
            }

        }
        return inexactMatch;
    }

    /**
     * 返回可分配参数类类型之间的继承总数
     *
     * @param classArray   被查找数组参数
     * @param toClassArray 查找参数信息
     * @return 可分配的参数类类型之间的继承总数.
     */
    private static int distance(final Class<?>[] classArray,
                                final Class<?>[] toClassArray) {
        int answer = 0;

        if (!isAssignable(classArray, toClassArray, true)) {
            return -1;
        }
        for (int offset = 0; offset < classArray.length; offset++) {
            if (classArray[offset].equals(toClassArray[offset])) {
                continue;
            } else if (isAssignable(classArray[offset], toClassArray[offset], true)
                    && !isAssignable(classArray[offset], toClassArray[offset], false)) {
                answer++;
            } else {
                answer = answer + 2;
            }
        }

        return answer;
    }

    /**
     * 将被覆盖方法的层次结构向下获取到{@code result}，其中包含泛型.
     *
     * @param method             方法信息
     * @param interfacesBehavior 接口，{@code null}
     * @return 按从子类到超类的升序设置方法
     * @since 3.2.0
     */
    public static Set<Method> getOverrideHierarchy(final Method method,
                                                   final Interfaces interfacesBehavior) {
        Assert.notNull(method);
        final Set<Method> result = new LinkedHashSet<>();
        result.add(method);

        final Class<?>[] parameterTypes = method.getParameterTypes();

        final Class<?> declaringClass = method.getDeclaringClass();

        final Iterator<Class<?>> hierarchy = hierarchy(declaringClass, interfacesBehavior).iterator();

        hierarchy.next();
        hierarchyTraversal:
        while (hierarchy.hasNext()) {
            final Class<?> c = hierarchy.next();
            final Method m = getMatchingAccessibleMethod(c, method.getName(), parameterTypes);
            if (m == null) {
                continue;
            }
            if (Arrays.equals(m.getParameterTypes(), parameterTypes)) {
                result.add(m);
                continue;
            }
            // 在包含接口的情况下，每次都需要获取参数
            final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(declaringClass, m.getDeclaringClass());
            for (int i = 0; i < parameterTypes.length; i++) {
                final Type childType = TypeUtils.unrollVariables(typeArguments, method.getGenericParameterTypes()[i]);
                final Type parentType = TypeUtils.unrollVariables(typeArguments, m.getGenericParameterTypes()[i]);
                if (!TypeUtils.equals(childType, parentType)) {
                    continue hierarchyTraversal;
                }
            }
            result.add(m);
        }
        return result;
    }

    /**
     * 获取使用给定注释进行注释的给定类的所有类级公共方法.
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @return 方法数组.
     * @since 3.5.0
     */
    public static Method[] getMethodsWithAnnotation(final Class<?> cls,
                                                    final Class<? extends Annotation> annotationCls) {
        return getMethodsWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取使用给定注释进行注释的给定类的所有类级公共方法.
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @return 方法列表
     * @since 3.5.0
     */
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        return getMethodsListWithAnnotation(cls, annotationCls, false, false);
    }

    /**
     * 获取使用给定注释进行注释的给定类的所有方法.
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @param searchSupers  确定是否应在给定类的整个继承层次结构中执行查找
     * @param ignoreAccess  确定是否应该考虑非公共方法
     * @return 方法数组
     */
    public static Method[] getMethodsWithAnnotation(final Class<?> cls,
                                                    final Class<? extends Annotation> annotationCls,
                                                    final boolean searchSupers,
                                                    final boolean ignoreAccess) {
        final List<Method> annotatedMethodsList = getMethodsListWithAnnotation(cls, annotationCls, searchSupers,
                ignoreAccess);
        return annotatedMethodsList.toArray(new Method[annotatedMethodsList.size()]);
    }

    /**
     * 获取使用给定注释进行注释的给定类的所有方法.
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @param searchSupers  确定是否应在给定类的整个继承层次结构中执行查找
     * @param ignoreAccess  确定是否应该考虑非公共方法
     * @return 方法数组
     */
    public static List<Method> getMethodsListWithAnnotation(final Class<?> cls,
                                                            final Class<? extends Annotation> annotationCls,
                                                            final boolean searchSupers,
                                                            final boolean ignoreAccess) {
        Assert.isTrue(cls != null, "The class must not be null");
        Assert.isTrue(annotationCls != null, "The annotation class must not be null");
        final List<Class<?>> classes = (searchSupers ? getAllSuperclassesAndInterfaces(cls) : new ArrayList<>());
        classes.add(0, cls);
        final List<Method> annotatedMethods = new ArrayList<>();
        for (final Class<?> acls : classes) {
            final Method[] methods = (ignoreAccess ? acls.getDeclaredMethods() : acls.getMethods());
            for (final Method method : methods) {
                if (method.getAnnotation(annotationCls) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        return annotatedMethods;
    }

    /**
     * 获取具有给定注释类型的注释对象，该注释类型出现在给定方法上，或可选地出现在超类和接口中的任何等效方法上。如果注释类型不存在，则返回null
     *
     * @param <A>           注解类型
     * @param method        要查询的 {@link Method} to query
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @param searchSupers  确定是否应在给定类的整个继承层次结构中执行查找
     * @param ignoreAccess  确定是否应该考虑非公共方法
     * @return 第一个匹配注释
     */
    public static <A extends Annotation> A getAnnotation(final Method method,
                                                         final Class<A> annotationCls,
                                                         final boolean searchSupers,
                                                         final boolean ignoreAccess) {

        Assert.isTrue(method != null, "The method must not be null");
        Assert.isTrue(annotationCls != null, "The annotation class must not be null");
        if (!ignoreAccess && !MemberUtils.isAccessible(method)) {
            return null;
        }

        A annotation = method.getAnnotation(annotationCls);

        if (annotation == null && searchSupers) {
            final Class<?> mcls = method.getDeclaringClass();
            final List<Class<?>> classes = getAllSuperclassesAndInterfaces(mcls);
            for (final Class<?> acls : classes) {
                Method equivalentMethod;
                try {
                    equivalentMethod = (ignoreAccess ? acls.getDeclaredMethod(method.getName(), method.getParameterTypes())
                            : acls.getMethod(method.getName(), method.getParameterTypes()));
                } catch (final NoSuchMethodException e) {
                    continue;
                }
                annotation = equivalentMethod.getAnnotation(annotationCls);
                if (annotation != null) {
                    break;
                }
            }
        }

        return annotation;
    }

    /**
     * 获取{@link ClassUtils#getAllSuperclasses}(Class)}
     * 和{@link ClassUtils#getAllInterfaces}(Class)}的组合
     *
     * @param cls t要查找的类
     * @return 超类和接口的组合{@code List}
     */
    private static List<Class<?>> getAllSuperclassesAndInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        final List<Class<?>> allSuperclasses = getAllSuperclasses(cls);
        int superClassIndex = 0;
        final List<Class<?>> allInterfaces = getAllInterfaces(cls);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() ||
                superClassIndex < allSuperclasses.size()) {
            Class<?> acls;
            if (interfaceIndex >= allInterfaces.size()) {
                acls = allSuperclasses.get(superClassIndex++);
            } else if (superClassIndex >= allSuperclasses.size()) {
                acls = allInterfaces.get(interfaceIndex++);
            } else if (interfaceIndex < superClassIndex) {
                acls = allInterfaces.get(interfaceIndex++);
            } else if (superClassIndex < interfaceIndex) {
                acls = allSuperclasses.get(superClassIndex++);
            } else {
                acls = allInterfaces.get(interfaceIndex++);
            }
            allSuperClassesAndInterfaces.add(acls);
        }
        return allSuperClassesAndInterfaces;
    }

    /**
     * 根据名称获取可访问的{@link Field}范围
     *
     * @param cls       要反射的{@link Class} 不能是{@code null}
     * @param fieldName 要获取的字段名
     * @return 字段对象
     */
    public static Field getField(final Class<?> cls, final String fieldName) {
        final Field field = getField(cls, fieldName, false);
        MemberUtils.setAccessibleWorkaround(field);
        return field;
    }

    /**
     * 按名称获取可访问的{@link Field}，如果请求则中断作用域.
     *
     * @param cls         要反映的{@link Class}不能是{@code null}
     * @param fieldName   要获取的字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} 方法打破范围限制.
     *                    {@code false}将只匹配{@code public}字段
     * @return 字段对象
     */
    public static Field getField(final Class<?> cls,
                                 final String fieldName,
                                 final boolean forceAccess) {
        Assert.isTrue(cls != null, "The class must not be null");
        Assert.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");

        // 检查超类层次结构
        for (Class<?> acls = cls; acls != null; acls = acls.getSuperclass()) {
            try {
                final Field field = acls.getDeclaredField(fieldName);
                // getDeclaredField 也会检查非公共作用域，并返回准确的结果
                if (!Modifier.isPublic(field.getModifiers())) {
                    if (forceAccess) {
                        field.setAccessible(true);
                    } else {
                        continue;
                    }
                }
                return field;
            } catch (final NoSuchFieldException ex) {

            }
        }
        // 检查公共接口用例。如果有一个公共超超类字段隐藏在一个私有/包超类字段中，则必须手动搜索它.
        Field match = null;
        for (final Class<?> class1 : ClassUtils.getAllInterfaces(cls)) {
            try {
                final Field test = class1.getField(fieldName);
                Assert.isTrue(match == null, "Reference to field %s is ambiguous relative to %s"
                        + "; a matching field exists on two or more implemented interfaces.", fieldName, cls);
                match = test;
            } catch (final NoSuchFieldException ex) {
            }
        }
        return match;
    }

    /**
     * 查找指定类中的所有字段（包括非public字段）, 字段不存在则返回null
     *
     * @param clazz     被查找字段的类
     * @param fieldName 字段名
     * @return 属性对象
     */
    public static Field getDeclaredField(final Class<?> clazz, final String fieldName) {
        if (null == clazz || StringUtils.isBlank(fieldName)) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 按名称获取可访问的{@link Field}，如果请求则中断作用域。只考虑指定的类
     *
     * @param clazz       被查找字段的类
     * @param fieldName   字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}方法打破范围限制.{@code false}将只匹配{@code public}字段
     * @return 属性对象
     */
    public static Field getDeclaredField(final Class<?> clazz,
                                         final String fieldName,
                                         final boolean forceAccess) {
        Assert.isTrue(clazz != null, "The class must not be null");
        Assert.isTrue(StringUtils.isNotBlank(fieldName), "The field name must not be blank/empty");
        try {
            // 使用getDeclaredField()只考虑指定的类
            final Field field = clazz.getDeclaredField(fieldName);
            if (!MemberUtils.isAccessible(field)) {
                if (forceAccess) {
                    field.setAccessible(true);
                } else {
                    return null;
                }
            }
            return field;
        } catch (final NoSuchFieldException e) {
        }
        return null;
    }

    /**
     * 获取给定类及其父类的所有字段(如果有).
     *
     * @param cls 要查询的 {@link Class}
     * @return 字段数组(可能为空)
     * @since 3.2.0
     */
    public static Field[] getAllFields(final Class<?> cls) {
        final List<Field> allFieldsList = getAllFieldsList(cls);
        return allFieldsList.toArray(new Field[allFieldsList.size()]);
    }

    /**
     * 获取给定类及其父类的所有字段(如果有).
     *
     * @param cls 要查询的 {@link Class}
     * @return 字段数组(可能为空)
     * @since 3.2.0
     */
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        Assert.isTrue(cls != null, "The class must not be null");
        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    /**
     * 获取用给定注释注释的给定类及其父类的所有字段(如果有).
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @return 字段数组(可能为空)
     * @since 3.5.0
     */
    public static Field[] getFieldsWithAnnotation(final Class<?> cls,
                                                  final Class<? extends Annotation> annotationCls) {
        final List<Field> annotatedFieldsList = getFieldsListWithAnnotation(cls, annotationCls);
        return annotatedFieldsList.toArray(new Field[annotatedFieldsList.size()]);
    }

    /**
     * 获取用给定注释注释的给定类及其父类的所有字段(如果有).
     *
     * @param cls           要查询的 {@link Class}
     * @param annotationCls 必须在要匹配的方法上出现的 {@link Annotation}
     * @return 字段列表(可能为空).
     * @since 3.5.0
     */
    public static List<Field> getFieldsListWithAnnotation(final Class<?> cls, final Class<? extends Annotation> annotationCls) {
        Assert.isTrue(annotationCls != null, "The annotation class must not be null");
        final List<Field> allFields = getAllFieldsList(cls);
        final List<Field> annotatedFields = new ArrayList<>();
        for (final Field field : allFields) {
            if (field.getAnnotation(annotationCls) != null) {
                annotatedFields.add(field);
            }
        }
        return annotatedFields;
    }

    /**
     * 读取可访问的{@code static} {@link Field}.
     *
     * @param field 字段信息
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readStaticField(final Field field) throws IllegalAccessException {
        return readStaticField(field, false);
    }

    /**
     * 读取一个静态 {@link Field}.
     *
     * @param field       字段信息
     * @param forceAccess 是否使用 {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readStaticField(final Field field, final boolean forceAccess) throws IllegalAccessException {
        Assert.isTrue(field != null, "The field must not be null");
        Assert.isTrue(Modifier.isStatic(field.getModifiers()), "The field '%s' is not static", field.getName());
        return readField(field, (Object) null, forceAccess);
    }

    /**
     * 读取指定的{@code public static} {@link Field}。将考虑超类.
     *
     * @param cls       要反映的{@link Class}不能是{@code null}
     * @param fieldName 要获取的字段名
     * @return 字段的值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readStaticField(final Class<?> cls, final String fieldName) throws IllegalAccessException {
        return readStaticField(cls, fieldName, false);
    }

    /**
     * 读取指定的{@code static} {@link Field}，将考虑超类
     *
     * @param cls         要反映的{@link Class}不能是{@code null}
     * @param fieldName   要获取的字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制.{@code false}将只匹配{@code public}字段
     * @return 字段对象
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readStaticField(final Class<?> cls, final String fieldName, final boolean forceAccess) throws IllegalAccessException {
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate field '%s' on %s", fieldName, cls);
        return readStaticField(field, false);
    }

    /**
     * 按名称获取{@code static} {@link Field}的值。字段必须是{@code public}.只考虑指定的类
     *
     * @param cls       要反映的{@link Class}不能是{@code null}
     * @param fieldName 要获取的字段名
     * @return 字段的值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readDeclaredStaticField(final Class<?> cls, final String fieldName) throws IllegalAccessException {
        return readDeclaredStaticField(cls, fieldName, false);
    }

    /**
     * 按名称获取{@code static} {@link Field}的值。字段必须是{@code public}.只考虑指定的类
     *
     * @param cls         要反映的{@link Class}不能是{@code null}
     * @param fieldName   要获取的字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制.{@code false}将只匹配{@code public}字段
     * @return 字段对象
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readDeclaredStaticField(final Class<?> cls, final String fieldName, final boolean forceAccess) throws IllegalAccessException {
        final Field field = getDeclaredField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
        return readStaticField(field, false);
    }

    /**
     * 读取一个可访问的 {@link Field}.
     *
     * @param field  要使用的字段
     * @param target 要调用的对象可以是{@code null}，用于{@code static}字段
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readField(final Field field, final Object target) throws IllegalAccessException {
        return readField(field, target, false);
    }

    /**
     * 读取一个可访问的 {@link Field}.
     *
     * @param field       要使用的字段
     * @param target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制.
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readField(final Field field, final Object target, final boolean forceAccess) throws IllegalAccessException {
        Assert.isTrue(field != null, "The field must not be null");
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            MemberUtils.setAccessibleWorkaround(field);
        }
        return field.get(target);
    }

    /**
     * Reads the named {@code public} {@link Field}. Superclasses will be considered.
     *
     * @param target    target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param fieldName 要获取的字段名
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readField(final Object target, final String fieldName) throws IllegalAccessException {
        return readField(target, fieldName, false);
    }

    /**
     * 读取指定的{@link Field} 将考虑超类.
     *
     * @param target      target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param fieldName   要获取的字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制.{@code false}将只匹配{@code public}字段
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readField(final Object target,
                                   final String fieldName,
                                   final boolean forceAccess) throws IllegalAccessException {
        Assert.isTrue(target != null, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate field %s on %s", fieldName, cls);
        return readField(field, target, false);
    }

    /**
     * 读取指定的{@code public} {@link Field} 只考虑指定对象的类.
     *
     * @param target    target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param fieldName 要获取的字段名
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readDeclaredField(final Object target, final String fieldName) throws IllegalAccessException {
        return readDeclaredField(target, fieldName, false);
    }

    /**
     * 按名称获取{@link Field}值 只考虑指定对象的类.
     *
     * @param target      target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param fieldName   要获取的字段名
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制.{@code false}将只匹配{@code public}字段
     * @return 字段值
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static Object readDeclaredField(final Object target,
                                           final String fieldName,
                                           final boolean forceAccess) throws IllegalAccessException {
        Assert.isTrue(target != null, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getDeclaredField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate declared field %s.%s", cls, fieldName);
        return readField(field, target, false);
    }

    /**
     * 写一个{@code public static} {@link Field}.
     *
     * @param field 字段
     * @param value 值
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeStaticField(final Field field, final Object value) throws IllegalAccessException {
        writeStaticField(field, value, false);
    }

    /**
     * 写一个静态方法 {@link Field}.
     *
     * @param field       字段
     * @param value       值
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeStaticField(final Field field,
                                        final Object value,
                                        final boolean forceAccess) throws IllegalAccessException {
        Assert.isTrue(field != null, "The field must not be null");
        Assert.isTrue(Modifier.isStatic(field.getModifiers()), "The field %s.%s is not static", field.getDeclaringClass().getName(),
                field.getName());
        writeField(field, (Object) null, value, forceAccess);
    }

    /**
     * 写一个命名{@code public static} {@link Field} 将考虑超类
     *
     * @param cls       查找的类{@link Class}
     * @param fieldName 字段名
     * @param value     值
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeStaticField(final Class<?> cls,
                                        final String fieldName,
                                        final Object value) throws IllegalAccessException {
        writeStaticField(cls, fieldName, value, false);
    }

    /**
     * 写一个命名的{@code static} {@link Field} 将考虑超类.
     *
     * @param cls         查找的类{@link Class}
     * @param fieldName   字段名
     * @param value       值
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeStaticField(final Class<?> cls,
                                        final String fieldName,
                                        final Object value,
                                        final boolean forceAccess)
            throws IllegalAccessException {
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate field %s on %s", fieldName, cls);
        writeStaticField(field, value, false);
    }

    /**
     * 写一个命名 {@code public static} {@link Field} 只考虑指定的类.
     *
     * @param cls       查找的类{@link Class}
     * @param fieldName 字段名
     * @param value     值
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeDeclaredStaticField(final Class<?> cls,
                                                final String fieldName,
                                                final Object value) throws IllegalAccessException {
        writeDeclaredStaticField(cls, fieldName, value, false);
    }

    /**
     * 写一个命名的{@code static} {@link Field}只考虑指定的类。
     *
     * @param cls         查找的类{@link Class}
     * @param fieldName   字段名
     * @param value       值
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段.
     * @throws IllegalAccessException 如果字段不是{@code public}或{@code final}
     */
    public static void writeDeclaredStaticField(final Class<?> cls,
                                                final String fieldName,
                                                final Object value,
                                                final boolean forceAccess)
            throws IllegalAccessException {
        final Field field = getDeclaredField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
        writeField(field, (Object) null, value, false);
    }

    /**
     * 写一个可访问的{@link Field}.
     *
     * @param field  字段
     * @param target 要调用的对象可以是{@code null}，用于{@code static}字段
     * @param value  值
     * @throws IllegalAccessException 如果字段或目标是{@code null}，那么该字段是不可访问的，
     *                                或者是{@code final}，或者{@code value}是不可分配的
     */
    public static void writeField(final Field field,
                                  final Object target,
                                  final Object value) throws IllegalAccessException {
        writeField(field, target, value, false);
    }

    /**
     * 写一个可访问的{@link Field}.
     *
     * @param field       字段
     * @param target      要调用的对象可以是{@code null}，用于{@code static}字段
     * @param value       值
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段
     * @throws IllegalAccessException 如果字段不可访问或{@code final}
     */
    public static void writeField(final Field field,
                                  final Object target,
                                  final Object value,
                                  final boolean forceAccess)
            throws IllegalAccessException {
        Assert.isTrue(field != null, "The field must not be null");
        if (forceAccess && !field.isAccessible()) {
            field.setAccessible(true);
        } else {
            MemberUtils.setAccessibleWorkaround(field);
        }
        field.set(target, value);
    }

    /**
     * 移除修饰符 {@link Field}.
     *
     * @param field 删除最后的修改器
     * @throws IllegalArgumentException 如果字段是{@code null}
     * @since 3.2.0
     */
    public static void removeFinalModifier(final Field field) {
        removeFinalModifier(field, true);
    }

    /**
     * 移除修饰符 {@link Field}.
     *
     * @param field       字段属性
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段.
     * @throws IllegalArgumentException 如果字段是{@code null}
     */
    public static void removeFinalModifier(final Field field, final boolean forceAccess) {
        Assert.isTrue(field != null, "The field must not be null");

        try {
            if (Modifier.isFinal(field.getModifiers())) {
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                final boolean doForceAccess = forceAccess && !modifiersField.isAccessible();
                if (doForceAccess) {
                    modifiersField.setAccessible(true);
                }
                try {
                    modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                } finally {
                    if (doForceAccess) {
                        modifiersField.setAccessible(false);
                    }
                }
            }
        } catch (final NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    /**
     * 处理{@code public} {@link Field} 将考虑超类
     *
     * @param target    目标对象不能是{@code null}
     * @param fieldName 要获取的字段名
     * @param value     值信息
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static void writeField(final Object target,
                                  final String fieldName,
                                  final Object value) throws IllegalAccessException {
        writeField(target, fieldName, value, false);
    }

    /**
     * 处理{@code public} {@link Field} 将考虑超类.
     *
     * @param target      目标对象不能是{@code null}
     * @param fieldName   要获取的字段名
     * @param value       值信息
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段.
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static void writeField(final Object target,
                                  final String fieldName,
                                  final Object value,
                                  final boolean forceAccess)
            throws IllegalAccessException {
        Assert.isTrue(target != null, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
        writeField(field, target, value, false);
    }

    /**
     * 写一个{@code public} {@link Field} 只考虑指定的类.
     *
     * @param target    目标对象不能是{@code null}
     * @param fieldName 要获取的字段名
     * @param value     值信息
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static void writeDeclaredField(final Object target,
                                          final String fieldName,
                                          final Object value) throws IllegalAccessException {
        writeDeclaredField(target, fieldName, value, false);
    }

    /**
     * 处理{@code public} {@link Field} 只考虑指定的类.
     *
     * @param target      目标对象不能是{@code null}
     * @param fieldName   要获取的字段名
     * @param value       值信息
     * @param forceAccess 是否使用{@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}
     *                    方法打破范围限制 {@code false}将只匹配{@code public}字段.
     * @throws IllegalAccessException 如果字段不可访问
     */
    public static void writeDeclaredField(final Object target,
                                          final String fieldName,
                                          final Object value,
                                          final boolean forceAccess)
            throws IllegalAccessException {
        Assert.isTrue(target != null, "target object must not be null");
        final Class<?> cls = target.getClass();
        final Field field = getDeclaredField(cls, fieldName, forceAccess);
        Assert.isTrue(field != null, "Cannot locate declared field %s.%s", cls.getName(), fieldName);
        writeField(field, target, value, false);
    }

    /**
     * 为给定实例返回用户定义的类:通常只是给定实例的类，
     * 但如果是cglib生成的子类，则返回原始类
     *
     * @param instance 要检查的实例
     * @return 用户定义的类
     */
    public static Class<?> getUserClass(Object instance) {
        return getUserClass(instance.getClass());
    }

    /**
     * 为给定的类返回用户定义的类:通常只是给定的类，
     * 但对于cglib生成的子类，则是原始类.
     *
     * @param clazz 要检查的类
     * @return 用户定义的类
     */
    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains(Symbol.DOLLAR + Symbol.DOLLAR)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
    }

    /**
     * 判断这个字段是否是数字类型或字符串类型
     *
     * @param type 字段类型
     * @return true：是数字或字符串类型
     */
    public static boolean isNumberOrStringType(Class<?> type) {
        if (type == String.class) {
            return true;
        }
        if (type.getGenericSuperclass() == Number.class) {
            return true;
        }
        if (type.isPrimitive()) {
            return true;
        }
        return false;
    }

    public enum Interfaces {
        INCLUDE, EXCLUDE
    }

}
