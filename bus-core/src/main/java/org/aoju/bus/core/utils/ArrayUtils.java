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

import org.aoju.bus.core.builder.HashCodeBuilder;
import org.aoju.bus.core.builder.ToStringBuilder;
import org.aoju.bus.core.builder.ToStringStyle;
import org.aoju.bus.core.lang.Editor;
import org.aoju.bus.core.lang.Filter;
import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.lang.mutable.MutableInt;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * 数组工具类
 *
 * @author Kimi Liu
 * @version 5.9.1
 * @since JDK 1.8+
 */
public class ArrayUtils {

    /**
     * 列表或数组中没有找到元素时的索引值 : {@code -1}.
     * 此值由该类中的方法返回,也可用于与所返回的值进行比较{@link java.util.List}.
     */
    private static final int INDEX_NOT_FOUND = -1;

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象, 如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * 数组是否为空
     * 此方法会匹配单一对象,如果此对象为{@code null}则返回true
     * 如果此对象为非数组,理解为此对象为数组的第一个元素,则返回false
     * 如果此对象为数组对象,数组长度大于0情况下返回false,否则返回true
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(Object array) {
        if (null == array) {
            return true;
        } else if (isArray(array)) {
            return 0 == Array.getLength(array);
        }
        throw new InstrumentException("Object to provide is not a Array !");
    }

    /**
     * 数组是否为空
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final long[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final int[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final short[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final char[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final byte[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final double[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final float[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @return 是否为空
     */
    public static boolean isEmpty(final boolean[] array) {
        return getLength(array) == 0;
    }

    /**
     * 数组是否为非空
     * 此方法会匹配单一对象,如果此对象为{@code null}则返回false
     * 如果此对象为非数组,理解为此对象为数组的第一个元素,则返回true
     * 如果此对象为数组对象,数组长度大于0情况下返回true,否则返回false
     *
     * @param <T>   对象
     * @param array 数组
     * @return 是否为非空
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final long[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final int[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final short[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final char[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final byte[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final double[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final float[] array) {
        return !isEmpty(array);
    }

    /**
     * 数组是否为非空
     *
     * @param array 数组
     * @return 是否为非空
     */
    public static boolean isNotEmpty(final boolean[] array) {
        return !isEmpty(array);
    }

    /**
     * 是否存都为{@code null}或空对象，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param args 被检查的对象,一个或者多个
     * @return 是否都为空
     */
    public static boolean isAllEmpty(Object... args) {
        int count = 0;
        if (isNotEmpty(args)) {
            for (Object element : args) {
                if (ObjectUtils.isEmpty(element)) {
                    count++;
                }
            }
        }
        return count == args.length;
    }

    /**
     * 是否存都不为{@code null}或空对象，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param args 被检查的对象,一个或者多个
     * @return 是否都不为空
     */
    public static boolean isAllNotEmpty(Object... args) {
        return false == hasEmpty(args);
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 要检查{@code null}或为空的数组
     * @param type  所需数组的类表示形式
     * @param <T>   class 类型
     * @return 如果{@code null}
     * @throws IllegalArgumentException 如果类型参数为空
     */
    public static <T> T[] nullToEmpty(final T[] array, final Class<T[]> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }

        if (array == null) {
            return type.cast(Array.newInstance(type.getComponentType(), 0));
        }
        return array;
    }


    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Object[] nullToEmpty(final Object[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Class<?>[] nullToEmpty(final Class<?>[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_CLASS_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static String[] nullToEmpty(final String[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_STRING_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static long[] nullToEmpty(final long[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_LONG_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static int[] nullToEmpty(final int[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_INT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static short[] nullToEmpty(final short[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_SHORT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static char[] nullToEmpty(final char[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_CHAR_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static byte[] nullToEmpty(final byte[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_BYTE_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static double[] nullToEmpty(final double[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_DOUBLE_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static float[] nullToEmpty(final float[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_FLOAT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static boolean[] nullToEmpty(final boolean[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_BOOLEAN_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Long[] nullToEmpty(final Long[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_LONG_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Integer[] nullToEmpty(final Integer[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Short[] nullToEmpty(final Short[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_SHORT_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Character[] nullToEmpty(final Character[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Byte[] nullToEmpty(final Byte[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_BYTE_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Double[] nullToEmpty(final Double[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Float[] nullToEmpty(final Float[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 输入数组返回一个空数组或原始数组
     *
     * @param array 数组
     * @return 空数组或原始数组
     */
    public static Boolean[] nullToEmpty(final Boolean[] array) {
        if (isEmpty(array)) {
            return Normal.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        return array;
    }

    /**
     * 是否存在{@code null}或空对象，通过{@link ObjectUtils#isEmpty(Object)} 判断元素
     *
     * @param args 被检查对象
     * @return 是否存在
     */
    public static boolean hasEmpty(Object... args) {
        if (isNotEmpty(args)) {
            for (Object element : args) {
                if (ObjectUtils.isEmpty(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     * @return 是否包含null元素
     */

    public static <T> boolean hasNull(T... array) {
        if (isNotEmpty(array)) {
            for (T element : array) {
                if (null == element) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 返回数组中第一个非空元素
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 非空元素, 如果不存在非空元素或数组为空, 返回{@code null}
     */

    public static <T> T firstNonNull(T... array) {
        if (isNotEmpty(array)) {
            for (final T val : array) {
                if (null != val) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * 找到第一个不为 null 的元素
     *
     * @param objects 对象
     * @return 不为 null 的元素
     */
    public static Optional<Object> firstNotNull(Object[] objects) {
        if (isEmpty(objects)) {
            return Optional.empty();
        }

        for (Object elem : objects) {
            if (ObjectUtils.isNotNull(elem)) {
                return Optional.of(elem);
            }
        }
        return Optional.empty();
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     * @return 数组字符串, 与集合转字符串格式相同
     */
    public static String toString(final Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (ArrayUtils.isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }

    /**
     * 数组或集合转String
     *
     * @param array        集合或数组对象
     * @param stringIfNull 是否null
     * @return 数组字符串, 与集合转字符串格式相同
     */
    public static String toString(final Object array, final String stringIfNull) {
        if (array == null) {
            return stringIfNull;
        }
        return new ToStringBuilder(array, ToStringStyle.SIMPLE_STYLE).append(array).toString();
    }

    /**
     * 将集合转数组
     *
     * @param list 需要转换的集合信息
     * @return 转换后的数组
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
    }

    /**
     * {@link ByteBuffer} 转byte数组
     *
     * @param bytebuffer {@link ByteBuffer}
     * @return byte数组
     */
    public static byte[] toArray(ByteBuffer bytebuffer) {
        if (false == bytebuffer.hasArray()) {
            int oldPosition = bytebuffer.position();
            bytebuffer.position(0);
            int size = bytebuffer.limit();
            byte[] buffers = new byte[size];
            bytebuffer.get(buffers);
            bytebuffer.position(oldPosition);
            return buffers;
        } else {
            return Arrays.copyOfRange(bytebuffer.array(), bytebuffer.position(), bytebuffer.limit());
        }
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param iterator      {@link Iterator}
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Iterator<T> iterator, Class<T> componentType) {
        return toArray(CollUtils.newArrayList(iterator), componentType);
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param iterable      {@link Iterable}
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Iterable<T> iterable, Class<T> componentType) {
        return toArray(CollUtils.toCollection(iterable), componentType);
    }

    /**
     * 将集合转为数组
     *
     * @param <T>           数组元素类型
     * @param collection    集合
     * @param componentType 集合元素类型
     * @return 数组
     */
    public static <T> T[] toArray(Collection<T> collection, Class<T> componentType) {
        return collection.toArray(newArray(componentType, 0));
    }

    /**
     * 返回比参数大1的给定数组的副本
     * 数组的最后一个值保留为默认值
     *
     * @param array                 被克隆的数组{@code null}
     * @param newArrayComponentType 如果{@code array}是{@code null}，
     *                              则创建此类型的大小为1的数组
     * @return 比输入大1的数组的新副本
     */
    private static Object clone(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }

    /**
     * 克隆数组
     *
     * @param <T>   对象
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static <T> T[] clone(final T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组,如果非数组返回null
     *
     * @param <T> 数组元素类型
     * @param obj 数组对象
     * @return 克隆后的数组对象
     */
    public static <T> T clone(T obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            final Object result;
            final Class<?> componentType = obj.getClass().getComponentType();
            if (componentType.isPrimitive()) {// 原始类型
                int length = Array.getLength(obj);
                result = Array.newInstance(componentType, length);
                while (length-- > 0) {
                    Array.set(result, length, Array.get(obj, length));
                }
            } else {
                result = ((Object[]) obj).clone();
            }
            return (T) result;
        }
        return null;
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static long[] clone(final long[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static int[] clone(final int[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static short[] clone(final short[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static char[] clone(final char[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static byte[] clone(final byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static double[] clone(final double[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static float[] clone(final float[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 克隆数组
     *
     * @param array 被克隆的数组
     * @return 新数组
     */
    public static boolean[] clone(final boolean[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param <T>        对象
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static <T> T[] subarray(final T[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        final Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            final T[] emptyArray = (T[]) Array.newInstance(type, 0);
            return emptyArray;
        }
        final T[] subarray = (T[]) Array.newInstance(type, newSize);
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static long[] subarray(final long[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_LONG_ARRAY;
        }

        final long[] subarray = new long[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static int[] subarray(final int[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_INT_ARRAY;
        }

        final int[] subarray = new int[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static short[] subarray(final short[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_SHORT_ARRAY;
        }

        final short[] subarray = new short[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static char[] subarray(final char[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_CHAR_ARRAY;
        }

        final char[] subarray = new char[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static byte[] subarray(final byte[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_BYTE_ARRAY;
        }

        final byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static double[] subarray(final double[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_DOUBLE_ARRAY;
        }

        final double[] subarray = new double[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static float[] subarray(final float[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_FLOAT_ARRAY;
        }

        final float[] subarray = new float[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 生成一个新的数组,其中包含元素开始索引取值至结束位置索引
     * 开始索引包含,结束索引不包含,空数组输入产生空输出
     *
     * @param array      对象数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @return 新数组
     */
    public static boolean[] subarray(final boolean[] array, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex > array.length) {
            endIndex = array.length;
        }
        final int newSize = endIndex - startIndex;
        if (newSize <= 0) {
            return Normal.EMPTY_BOOLEAN_ARRAY;
        }

        final boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndex, subarray, 0, newSize);
        return subarray;
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final Object[] array1, final Object[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final long[] array1, final long[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final int[] array1, final int[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final short[] array1, final short[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final char[] array1, final char[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final byte[] array1, final byte[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final double[] array1, final double[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final float[] array1, final float[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 检查两个数组是否相同长度
     *
     * @param array1 参数1
     * @param array2 参数2
     * @return 如果长度匹配返回true否则false
     */
    public static boolean isSameLength(final boolean[] array1, final boolean[] array2) {
        return getLength(array1) == getLength(array2);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final Object[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final long[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final int[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final short[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final char[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final byte[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final double[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final float[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array 数组,会变更
     */
    public static void reverse(final boolean[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }

    /**
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final boolean[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        boolean tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final byte[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        byte tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final char[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        char tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final double[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        double tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final float[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        float tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final int[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        int tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final long[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        long tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final Object[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        Object tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    /**
     * 反转数组,会变更原数组
     *
     * @param array      数组,会变更
     * @param startIndex 其实位置(包含)
     * @param endIndex   结束位置(不包含)
     */
    public static void reverse(final short[] array, final int startIndex, final int endIndex) {
        if (array == null) {
            return;
        }
        int i = startIndex < 0 ? 0 : startIndex;
        int j = Math.min(array.length, endIndex) - 1;
        short tmp;
        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }


    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap(["1", "2", "3"], 0, 2) -&gt; ["3", "2", "1"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3"], 0, 0) -&gt; ["1", "2", "3"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3"], 1, 0) -&gt; ["2", "1", "3"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3"], 0, 5) -&gt; ["1", "2", "3"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3"], -1, 1) -&gt; ["2", "1", "3"]</li>
     *                </ul>
     */
    public static void swap(final Object[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([true, false, true], 0, 2) -&gt; [true, false, true]</li>
     *                <li>ArrayUtils.swap([true, false, true], 0, 0) -&gt; [true, false, true]</li>
     *                <li>ArrayUtils.swap([true, false, true], 1, 0) -&gt; [false, true, true]</li>
     *                <li>ArrayUtils.swap([true, false, true], 0, 5) -&gt; [true, false, true]</li>
     *                <li>ArrayUtils.swap([true, false, true], -1, 1) -&gt; [false, true, true]</li>
     *                </ul>
     */
    public static void swap(final long[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final int[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final short[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final char[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final byte[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final double[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final float[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 2) -&gt; [3, 2, 1]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 0) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 1, 0) -&gt; [2, 1, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], 0, 5) -&gt; [1, 2, 3]</li>
     *                <li>ArrayUtils.swap([1, 2, 3], -1, 1) -&gt; [2, 1, 3]</li>
     *                </ul>
     */
    public static void swap(final boolean[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([true, false, true, false], 0, 2, 1) -&gt; [true, false, true, false]</li>
     *                <li>ArrayUtils.swap([true, false, true, false], 0, 0, 1) -&gt; [true, false, true, false]</li>
     *                <li>ArrayUtils.swap([true, false, true, false], 0, 2, 2) -&gt; [true, false, true, false]</li>
     *                <li>ArrayUtils.swap([true, false, true, false], -3, 2, 2) -&gt; [true, false, true, false]</li>
     *                <li>ArrayUtils.swap([true, false, true, false], 0, 3, 3) -&gt; [false, false, true, true]</li>
     *                </ul>
     */
    public static void swap(final boolean[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final boolean aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final byte[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final byte aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final char[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final char aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final double[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final double aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final float[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final float aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }

    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final int[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final int aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final long[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final long aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                <p>
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap(["1", "2", "3", "4"], 0, 2, 1) -&gt; ["3", "2", "1", "4"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3", "4"], 0, 0, 1) -&gt; ["1", "2", "3", "4"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3", "4"], 2, 0, 2) -&gt; ["3", "4", "1", "2"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3", "4"], -3, 2, 2) -&gt; ["3", "4", "1", "2"]</li>
     *                <li>ArrayUtils.swap(["1", "2", "3", "4"], 0, 3, 3) -&gt; ["4", "2", "3", "1"]</li>
     *                </ul>
     */
    public static void swap(final Object[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final Object aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }

    /**
     * 交换数组中连个位置的值
     *
     * @param array   数组
     * @param offset1 位置1
     * @param offset2 位置2
     * @param len     从给定索引开始交换的元素数量
     *                Examples:
     *                <ul>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 2, 1) -&gt; [3, 2, 1, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 0, 1) -&gt; [1, 2, 3, 4]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 2, 0, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], -3, 2, 2) -&gt; [3, 4, 1, 2]</li>
     *                <li>ArrayUtils.swap([1, 2, 3, 4], 0, 3, 3) -&gt; [4, 2, 3, 1]</li>
     *                </ul>
     */
    public static void swap(final short[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        if (offset1 == offset2) {
            return;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; i++, offset1++, offset2++) {
            final short aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }


    /**
     * Shifts the order of the given array.
     *
     * <p>There is no special handling for multi-dimensional arrays. This method
     * does nothing for {@code null} or empty input arrays.</p>
     *
     * @param array  the array to shift, may be {@code null}
     * @param offset The number of positions to rotate the elements.  If the offset is larger than the number of elements to
     *               rotate, than the effective offset is modulo the number of elements to rotate.
     */
    public static void shift(final Object[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * Shifts the order of the given long array.
     *
     * <p>There is no special handling for multi-dimensional arrays. This method
     * does nothing for {@code null} or empty input arrays.</p>
     *
     * @param array  the array to shift, may be {@code null}
     * @param offset The number of positions to rotate the elements.  If the offset is larger than the number of elements to
     *               rotate, than the effective offset is modulo the number of elements to rotate.
     */
    public static void shift(final long[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * Shifts the order of the given int array.
     *
     * <p>There is no special handling for multi-dimensional arrays. This method
     * does nothing for {@code null} or empty input arrays.</p>
     *
     * @param array  the array to shift, may be {@code null}
     * @param offset The number of positions to rotate the elements.  If the offset is larger than the number of elements to
     *               rotate, than the effective offset is modulo the number of elements to rotate.
     */
    public static void shift(final int[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final short[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final char[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final byte[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final double[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final float[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array  原数组
     * @param offset 偏移量
     */
    public static void shift(final boolean[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final boolean[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final byte[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        // For algorithm explanations and proof of O(n) time complexity and O(1) space complexity
        // see https://beradrian.wordpress.com/2015/04/07/shift-an-array-in-on-in-place/
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final char[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final double[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final float[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final int[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final long[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final Object[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 改变数组的顺序
     *
     * @param array      原数组
     * @param startIndex 开始索引
     * @param endIndex   结束索引
     * @param offset     偏移量
     */
    public static void shift(final short[] array, int startIndex, int endIndex, int offset) {
        if (array == null) {
            return;
        }
        if (startIndex >= array.length - 1 || endIndex <= 0) {
            return;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= array.length) {
            endIndex = array.length;
        }
        int n = endIndex - startIndex;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;

            if (offset > n_offset) {
                swap(array, startIndex, startIndex + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            } else if (offset < n_offset) {
                swap(array, startIndex, startIndex + n_offset, offset);
                startIndex += offset;
                n = n_offset;
            } else {
                swap(array, startIndex, startIndex + n_offset, offset);
                break;
            }
        }
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final Object[] array, final Object value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final Object[] array, final Object value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        if (value == null) {
            for (int i = index; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = index; i < array.length; i++) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final Object[] array, final Object value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final Object[] array, final Object value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        if (value == null) {
            for (int i = index; i >= 0; i--) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(value)) {
            for (int i = index; i >= 0; i--) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final Object[] array, final Object value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }


    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final long[] array, final long value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final long[] array, final long value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final long[] array, final long value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final long[] array, final long value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final long[] array, final long value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final int[] array, final int value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final int[] array, final int value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final int[] array, final int value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final int[] array, final int value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final int[] array, final int value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }


    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final short[] array, final short value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final short[] array, final short value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final short[] array, final short value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final short[] array, final short value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final short[] array, final short value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }


    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final char[] array, final char value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final char[] array, final char value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final char[] array, final char value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final char[] array, final char value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final char[] array, final char value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final byte[] array, final byte value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final byte[] array, final byte value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final byte[] array, final byte value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final byte[] array, final byte value, int index) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final byte[] array, final byte value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final double[] array, final double value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array     数组
     * @param value     被检查的元素
     * @param tolerance 容差
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final double[] array, final double value, final double tolerance) {
        return indexOf(array, value, 0, tolerance);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final double[] array, final double value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array     数组
     * @param value     被检查的元素
     * @param index     索引
     * @param tolerance 容差
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final double[] array, final double value, int index, final double tolerance) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        final double min = value - tolerance;
        final double max = value + tolerance;
        for (int i = index; i < array.length; i++) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final double[] array, final double value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array     数组
     * @param value     被检查的元素
     * @param tolerance 容差
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final double[] array, final double value, final double tolerance) {
        return lastIndexOf(array, value, Integer.MAX_VALUE, tolerance);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final double[] array, final double value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array     数组
     * @param value     被检查的元素
     * @param index     索引
     * @param tolerance 容差
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final double[] array, final double value, int index, final double tolerance) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        final double min = value - tolerance;
        final double max = value + tolerance;
        for (int i = index; i >= 0; i--) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final double[] array, final double value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array     数组
     * @param value     被检查的元素
     * @param tolerance 容差
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static boolean contains(final double[] array, final double value, final double tolerance) {
        return indexOf(array, value, 0, tolerance) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final float[] array, final float value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final float[] array, final float value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final float[] array, final float value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final float[] array, final float value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final float[] array, final float value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final boolean[] array, final boolean value) {
        return indexOf(array, value, 0);
    }

    /**
     * 返回数组中指定元素所在位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOf(final boolean[] array, final boolean value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            index = 0;
        }
        for (int i = index; i < array.length; i++) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final boolean[] array, final boolean value) {
        return lastIndexOf(array, value, Integer.MAX_VALUE);
    }

    /**
     * 返回数组中指定元素所在最后的位置,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @param index 索引
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int lastIndexOf(final boolean[] array, final boolean value, int index) {
        if (isEmpty(array)) {
            return INDEX_NOT_FOUND;
        }
        if (index < 0) {
            return INDEX_NOT_FOUND;
        } else if (index >= array.length) {
            index = array.length - 1;
        }
        for (int i = index; i >= 0; i--) {
            if (value == array[i]) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 数组中是否包含元素
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean contains(final boolean[] array, final boolean value) {
        return indexOf(array, value) != INDEX_NOT_FOUND;
    }

    /**
     * 将对象Character数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Character}数组，可以是{@code null}
     * @return {@code char}数组，{@code null}如果为空数组输入
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static char[] toPrimitive(final Character[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].charValue();
        }
        return result;
    }

    /**
     * 将对象Character数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Character}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Character}数组，{@code null}如果为空数组输入
     */
    public static char[] toPrimitive(final Character[] array, final char valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            final Character b = array[i];
            result[i] = (b == null ? valueForNull : b.charValue());
        }
        return result;
    }

    /**
     * 将对象char数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code char}数组，可以是{@code null}
     * @return {@code Character}数组，{@code null}如果为空数组输入
     */
    public static Character[] toObject(final char[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        final Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Character.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Long数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Long}数组，可以是{@code null}
     * @return {@code long}数组，{@code null}如果为空数组输入
     */
    public static long[] toPrimitive(final Long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].longValue();
        }
        return result;
    }

    /**
     * 将对象Long数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Long}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Long}数组，{@code null}如果为空数组输入
     */
    public static long[] toPrimitive(final Long[] array, final long valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            final Long b = array[i];
            result[i] = (b == null ? valueForNull : b.longValue());
        }
        return result;
    }

    /**
     * 将对象long数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code long}数组，可以是{@code null}
     * @return {@code Long}数组，{@code null}如果为空数组输入
     */
    public static Long[] toObject(final long[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_LONG_OBJECT_ARRAY;
        }
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Long.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Integer数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Integer}数组，可以是{@code null}
     * @return {@code int}数组，{@code null}如果为空数组输入
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static int[] toPrimitive(final Integer[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].intValue();
        }
        return result;
    }

    /**
     * 将对象Integer数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Integer}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Integer}数组，{@code null}如果为空数组输入
     */
    public static int[] toPrimitive(final Integer[] array, final int valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            final Integer b = array[i];
            result[i] = (b == null ? valueForNull : b.intValue());
        }
        return result;
    }

    /**
     * 将对象int数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code int}数组，可以是{@code null}
     * @return {@code Integer}数组，{@code null}如果为空数组输入
     */
    public static Integer[] toObject(final int[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Integer.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Short数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Short}数组，可以是{@code null}
     * @return {@code short}数组，{@code null}如果为空数组输入
     */
    public static short[] toPrimitive(final Short[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].shortValue();
        }
        return result;
    }

    /**
     * 将对象Short数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Short}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Short}数组，{@code null}如果为空数组输入
     */
    public static short[] toPrimitive(final Short[] array, final short valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            final Short b = array[i];
            result[i] = (b == null ? valueForNull : b.shortValue());
        }
        return result;
    }

    /**
     * 将对象short数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code short}数组，可以是{@code null}
     * @return {@code Short}数组，{@code null}如果为空数组输入
     */
    public static Short[] toObject(final short[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_SHORT_OBJECT_ARRAY;
        }
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Short.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Byte数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Byte}数组，可以是{@code null}
     * @return {@code byte}数组，{@code null}如果为空数组输入
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    /**
     * 将对象Byte数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Byte}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Byte}数组，{@code null}如果为空数组输入
     */
    public static byte[] toPrimitive(final Byte[] array, final byte valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            final Byte b = array[i];
            result[i] = (b == null ? valueForNull : b.byteValue());
        }
        return result;
    }

    /**
     * 将对象byte数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code byte}数组，可以是{@code null}
     * @return {@code Byte}数组，{@code null}如果为空数组输入
     */
    public static Byte[] toObject(final byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BYTE_OBJECT_ARRAY;
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Byte.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Double数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Double}数组，可以是{@code null}
     * @return {@code double}数组，{@code null}如果为空数组输入
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static double[] toPrimitive(final Double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].doubleValue();
        }
        return result;
    }

    /**
     * 将对象Double数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Double}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code Double}数组，{@code null}如果为空数组输入
     */
    public static double[] toPrimitive(final Double[] array, final double valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            final Double b = array[i];
            result[i] = (b == null ? valueForNull : b.doubleValue());
        }
        return result;
    }

    /**
     * 将对象double数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code double}数组，可以是{@code null}
     * @return {@code double}数组，{@code null}如果为空数组输入
     */
    public static Double[] toObject(final double[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Double.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 将对象Float数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Float}数组，可以是{@code null}
     * @return {@code float}数组，{@code null}如果为空数组输入
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static float[] toPrimitive(final Float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].floatValue();
        }
        return result;
    }

    /**
     * 将对象Float数组转换为处理{@code null}的原始对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Float}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return {@code float}数组，{@code null}如果为空数组输入
     */
    public static float[] toPrimitive(final Float[] array, final float valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            final Float b = array[i];
            result[i] = (b == null ? valueForNull : b.floatValue());
        }
        return result;
    }

    /**
     * 将基元float组转换为对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code float}数组
     * @return {@code Float}数组，{@code null}如果为空数组输入
     */
    public static Float[] toObject(final float[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = Float.valueOf(array[i]);
        }
        return result;
    }

    /**
     * 从包装器类型数组创建基元类型数组
     * 该方法为一个{@code null}输入数组返回{@code null}
     *
     * @param array 包装器对象的数组
     * @return 对应基元类型的数组，或原始数组
     */
    public static Object toPrimitive(final Object array) {
        if (array == null) {
            return null;
        }
        final Class<?> ct = array.getClass().getComponentType();
        final Class<?> pt = ClassUtils.wrapperToPrimitive(ct);
        if (Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[]) array);
        }
        if (Long.TYPE.equals(pt)) {
            return toPrimitive((Long[]) array);
        }
        if (Short.TYPE.equals(pt)) {
            return toPrimitive((Short[]) array);
        }
        if (Double.TYPE.equals(pt)) {
            return toPrimitive((Double[]) array);
        }
        if (Float.TYPE.equals(pt)) {
            return toPrimitive((Float[]) array);
        }
        return array;
    }

    /**
     * 将对象布尔值数组转换为处理{@code null}的原始的类型
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code Boolean}数组，可以是{@code null}
     * @return 返回{@code boolean}数组, 如果输入为null则为{@code null}
     * @throws NullPointerException 如果数组内容是{@code null}
     */
    public static boolean[] toPrimitive(final Boolean[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].booleanValue();
        }
        return result;
    }

    /**
     * 将对象布尔值数组转换为处理{@code null}的原始的类型
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array        {@code Boolean}数组，可以是{@code null}
     * @param valueForNull 找到{@code null}时要插入的值
     * @return 返回{@code boolean}数组, 如果输入为null则为{@code null}
     */
    public static boolean[] toPrimitive(final Boolean[] array, final boolean valueForNull) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            final Boolean b = array[i];
            result[i] = (b == null ? valueForNull : b.booleanValue());
        }
        return result;
    }

    /**
     * 将原始布尔值数组转换为对象
     * 此方法为{@code null}输入数组返回{@code null}
     *
     * @param array {@code boolean}数组
     * @return 返回{@code boolean}数组, 如果输入为null则为{@code null}
     */
    public static Boolean[] toObject(final boolean[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        final Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = (array[i] ? Boolean.TRUE : Boolean.FALSE);
        }
        return result;
    }

    /**
     * 将多个数组合并在一起
     * 忽略null的数组
     *
     * @param <T>    数组元素类型
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static <T> T[] addAll(T[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        int length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }
        T[] result = newArray(arrays.getClass().getComponentType().getComponentType(), length);

        length = 0;
        for (T[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将多个数组合并在一起
     * 忽略null的数组
     *
     * @param arrays 数组集合
     * @return 合并后的数组
     */
    public static byte[] addAll(byte[]... arrays) {
        if (arrays.length == 1) {
            return arrays[0];
        }

        // 计算总长度
        int length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                length += array.length;
            }
        }

        final byte[] result = new byte[length];
        length = 0;
        for (byte[] array : arrays) {
            if (null != array) {
                System.arraycopy(array, 0, result, length, array.length);
                length += array.length;
            }
        }
        return result;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(null, null)     = null
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * ArrayUtils.addAll([null], [null]) = [null, null]
     * ArrayUtils.addAll(["a", "b", "c"], ["1", "2", "3"]) = ["a", "b", "c", "1", "2", "3"]
     * </pre>
     *
     * @param <T>    数组的组件类型
     * @param array1 将元素添加到新数组的第一个数组可以是{@code null}
     * @param array2 将元素添加到新数组的第二个数组可以是{@code null}
     * @return T如果两个数组都是{@code null}，则为{@code null}.
     * 新数组的类型是第一个数组的类型，除非第一个数组为空，否则类型与第二个数组相同.
     * @throws IllegalArgumentException 如果数组类型不兼容
     */
    public static <T> T[] addAll(final T[] array1, final T... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
                        + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 boolean[] 数组.
     */
    public static boolean[] addAll(final boolean[] array1, final boolean... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final boolean[] joinedArray = new boolean[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 char[] 数组.
     */
    public static char[] addAll(final char[] array1, final char... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final char[] joinedArray = new char[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 byte[] 数组.
     */
    public static byte[] addAll(final byte[] array1, final byte... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 short[] 数组.
     */
    public static short[] addAll(final short[] array1, final short... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final short[] joinedArray = new short[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 int[] 数组.
     */
    public static int[] addAll(final int[] array1, final int... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 long[] 数组.
     */
    public static long[] addAll(final long[] array1, final long... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 float[] 数组.
     */
    public static float[] addAll(final float[] array1, final float... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final float[] joinedArray = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 将给定数组的所有元素添加到新数组中.
     * 新数组包含{@code array1}的所有元素，然后是{@code array2}的所有元素.
     * 当一个数组被返回时，它总是一个新的数组.
     *
     * <pre>
     * ArrayUtils.addAll(array1, null)   = cloned copy of array1
     * ArrayUtils.addAll(null, array2)   = cloned copy of array2
     * ArrayUtils.addAll([], [])         = []
     * </pre>
     *
     * @param array1 将元素添加到新数组中的第一个数组.
     * @param array2 第二个数组，其元素被添加到新数组中.
     * @return 新的 double[] 数组.
     */
    public static double[] addAll(final double[] array1, final double... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final double[] joinedArray = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，
     * 其组件类型与元素相同，除非元素本身为空，否则返回类型为Object[]
     *
     * <pre>
     * ArrayUtils.add(null, null)      = IllegalArgumentException
     * ArrayUtils.add(null, "a")       = ["a"]
     * ArrayUtils.add(["a"], null)     = ["a", null]
     * ArrayUtils.add(["a"], "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param <T>     数组的组件类型
     * @param array   要“add”元素的数组可以是{@code null}
     * @param element 要添加的对象可以是{@code null}
     * @return 包含现有元素的新数组加上返回的新元素数组类型将是输入数组的类型(除非为空)，
     * 在这种情况下，它将具有与元素相同的类型。如果两者都为空，则抛出IllegalArgumentException
     * @throws IllegalArgumentException 如果两个参数都为空
     */
    public static <T> T[] add(final T[] array, final T element) {
        Class<?> type;
        if (array != null) {
            type = array.getClass().getComponentType();
        } else if (element != null) {
            type = element.getClass();
        } else {
            throw new IllegalArgumentException("Arguments cannot both be null");
        }
        final T[] newArray = (T[]) clone(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, true)          = [true]
     * ArrayUtils.add([true], false)       = [true, false]
     * ArrayUtils.add([true, false], true) = [true, false, true]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static boolean[] add(final boolean[] array, final boolean element) {
        final boolean[] newArray = (boolean[]) clone(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static byte[] add(final byte[] array, final byte element) {
        final byte[] newArray = (byte[]) clone(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, '0')       = ['0']
     * ArrayUtils.add(['1'], '0')      = ['1', '0']
     * ArrayUtils.add(['1', '0'], '1') = ['1', '0', '1']
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static char[] add(final char[] array, final char element) {
        final char[] newArray = (char[]) clone(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static double[] add(final double[] array, final double element) {
        final double[] newArray = (double[]) clone(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static float[] add(final float[] array, final float element) {
        final float[] newArray = (float[]) clone(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static int[] add(final int[] array, final int element) {
        final int[] newArray = (int[]) clone(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static long[] add(final long[] array, final long element) {
        final long[] newArray = (long[]) clone(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0)   = [0]
     * ArrayUtils.add([1], 0)    = [1, 0]
     * ArrayUtils.add([1, 0], 1) = [1, 0, 1]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static short[] add(final short[] array, final short element) {
        final short[] newArray = (short[]) clone(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0, null)      = IllegalArgumentException
     * ArrayUtils.add(null, 0, "a")       = ["a"]
     * ArrayUtils.add(["a"], 1, null)     = ["a", null]
     * ArrayUtils.add(["a"], 1, "b")      = ["a", "b"]
     * ArrayUtils.add(["a", "b"], 3, "c") = ["a", "b", "c"]
     * </pre>
     *
     * @param <T>     数组的组件类型
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static <T> T[] add(final T[] array, final int index, final T element) {
        Class<?> clss;
        if (array != null) {
            clss = array.getClass().getComponentType();
        } else if (element != null) {
            clss = element.getClass();
        } else {
            throw new IllegalArgumentException("Array and element cannot both be null");
        }
        return (T[]) add(array, index, element, clss);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0, true)          = [true]
     * ArrayUtils.add([true], 0, false)       = [false, true]
     * ArrayUtils.add([false], 1, true)       = [false, true]
     * ArrayUtils.add([true, false], 1, true) = [true, true, false]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static boolean[] add(final boolean[] array, final int index, final boolean element) {
        return (boolean[]) add(array, index, Boolean.valueOf(element), Boolean.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add(null, 0, 'a')            = ['a']
     * ArrayUtils.add(['a'], 0, 'b')           = ['b', 'a']
     * ArrayUtils.add(['a', 'b'], 0, 'c')      = ['c', 'a', 'b']
     * ArrayUtils.add(['a', 'b'], 1, 'k')      = ['a', 'k', 'b']
     * ArrayUtils.add(['a', 'b', 'c'], 1, 't') = ['a', 't', 'b', 'c']
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static char[] add(final char[] array, final int index, final char element) {
        return (char[]) add(array, index, Character.valueOf(element), Character.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 3)      = [2, 6, 3]
     * ArrayUtils.add([2, 6], 0, 1)      = [1, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static byte[] add(final byte[] array, final int index, final byte element) {
        return (byte[]) add(array, index, Byte.valueOf(element), Byte.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static short[] add(final short[] array, final int index, final short element) {
        return (short[]) add(array, index, Short.valueOf(element), Short.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1], 0, 2)         = [2, 1]
     * ArrayUtils.add([2, 6], 2, 10)     = [2, 6, 10]
     * ArrayUtils.add([2, 6], 0, -4)     = [-4, 2, 6]
     * ArrayUtils.add([2, 6, 3], 2, 1)   = [2, 6, 1, 3]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static int[] add(final int[] array, final int index, final int element) {
        return (int[]) add(array, index, Integer.valueOf(element), Integer.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1L], 0, 2L)           = [2L, 1L]
     * ArrayUtils.add([2L, 6L], 2, 10L)      = [2L, 6L, 10L]
     * ArrayUtils.add([2L, 6L], 0, -4L)      = [-4L, 2L, 6L]
     * ArrayUtils.add([2L, 6L, 3L], 2, 1L)   = [2L, 6L, 1L, 3L]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static long[] add(final long[] array, final int index, final long element) {
        return (long[]) add(array, index, Long.valueOf(element), Long.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1.1f], 0, 2.2f)               = [2.2f, 1.1f]
     * ArrayUtils.add([2.3f, 6.4f], 2, 10.5f)        = [2.3f, 6.4f, 10.5f]
     * ArrayUtils.add([2.6f, 6.7f], 0, -4.8f)        = [-4.8f, 2.6f, 6.7f]
     * ArrayUtils.add([2.9f, 6.0f, 0.3f], 2, 1.0f)   = [2.9f, 6.0f, 1.0f, 0.3f]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static float[] add(final float[] array, final int index, final float element) {
        return (float[]) add(array, index, Float.valueOf(element), Float.TYPE);
    }

    /**
     * 复制给定数组并在新数组的末尾添加给定元素.
     * 新数组的组件类型与输入数组的组件类型相同.
     * 如果输入数组是{@code null}，则返回一个新元素数组，其组件类型与元素相同.
     *
     * <pre>
     * ArrayUtils.add([1.1], 0, 2.2)              = [2.2, 1.1]
     * ArrayUtils.add([2.3, 6.4], 2, 10.5)        = [2.3, 6.4, 10.5]
     * ArrayUtils.add([2.6, 6.7], 0, -4.8)        = [-4.8, 2.6, 6.7]
     * ArrayUtils.add([2.9, 6.0, 0.3], 2, 1.0)    = [2.9, 6.0, 1.0, 0.3]
     * </pre>
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 包含现有元素和新元素的新数组
     */
    public static double[] add(final double[] array, final int index, final double element) {
        return (double[]) add(array, index, Double.valueOf(element), Double.TYPE);
    }

    /**
     * add(array, index, element) 方法的底层实现.
     * 最后一个参数是类，它可能不等于元素element.getClass
     *
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param index   新对象的位置
     * @param element 要在新数组的最后一个索引处添加的对象
     * @param clss    要添加的元素的类型
     * @return 包含现有元素和新元素的新数组
     */
    private static Object add(final Object array, final int index, final Object element, final Class<?> clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            final Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        final int length = Array.getLength(array);
        if (index > length || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final Object result = Array.newInstance(clss, length + 1);
        System.arraycopy(array, 0, result, 0, index);
        Array.set(result, index, element);
        if (index < length) {
            System.arraycopy(array, index, result, index + 1, length - index);
        }
        return result;
    }

    /**
     * 去重数组中的元素，去重后生成新的数组，原数组不变
     * 此方法通过{@link LinkedHashSet} 去重
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @return 去重后的数组
     */
    public static <T> T[] remove(T[] array) {
        if (isEmpty(array)) {
            return array;
        }

        final Set<T> set = new LinkedHashSet<>(array.length, 1);
        Collections.addAll(set, array);
        return toArray(set, (Class<T>) getComponentType(array));
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove(["a"], 0)           = []
     * ArrayUtils.remove(["a", "b"], 0)      = ["b"]
     * ArrayUtils.remove(["a", "b"], 1)      = ["a"]
     * ArrayUtils.remove(["a", "b", "c"], 1) = ["a", "c"]
     * </pre>
     *
     * @param <T>   数组的组件类型
     * @param array 要从中删除的元素
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static <T> T[] remove(final T[] array, final int index) {
        return (T[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, "a")            = null
     * ArrayUtils.removeElement([], "a")              = []
     * ArrayUtils.removeElement(["a"], "b")           = ["a"]
     * ArrayUtils.removeElement(["a", "b"], "a")      = ["b"]
     * ArrayUtils.removeElement(["a", "b", "a"], "a") = ["b", "a"]
     * </pre>
     *
     * @param <T>     数组的组件类型
     * @param array   要复制并添加元素的数组可以是{@code null}
     * @param element 要在新数组的最后一个索引处添加的对象
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static <T> T[] removeElement(final T[] array, final Object element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([true], 0)              = []
     * ArrayUtils.remove([true, false], 0)       = [false]
     * ArrayUtils.remove([true, false], 1)       = [true]
     * ArrayUtils.remove([true, true, false], 1) = [true, false]
     * </pre>
     *
     * @param array 要从中删除的元素
     * @param index 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static boolean[] remove(final boolean[] array, final int index) {
        return (boolean[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, true)                = null
     * ArrayUtils.removeElement([], true)                  = []
     * ArrayUtils.removeElement([true], false)             = [true]
     * ArrayUtils.removeElement([true, false], false)      = [true]
     * ArrayUtils.removeElement([true, false, true], true) = [false, true]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static boolean[] removeElement(final boolean[] array, final boolean element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1], 0)          = []
     * ArrayUtils.remove([1, 0], 0)       = [0]
     * ArrayUtils.remove([1, 0], 1)       = [1]
     * ArrayUtils.remove([1, 0, 1], 1)    = [1, 1]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static byte[] remove(final byte[] array, final int index) {
        return (byte[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)        = null
     * ArrayUtils.removeElement([], 1)          = []
     * ArrayUtils.removeElement([1], 0)         = [1]
     * ArrayUtils.removeElement([1, 0], 0)      = [1]
     * ArrayUtils.removeElement([1, 0, 1], 1)   = [0, 1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static byte[] removeElement(final byte[] array, final byte element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove(['a'], 0)           = []
     * ArrayUtils.remove(['a', 'b'], 0)      = ['b']
     * ArrayUtils.remove(['a', 'b'], 1)      = ['a']
     * ArrayUtils.remove(['a', 'b', 'c'], 1) = ['a', 'c']
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素..
     */
    public static char[] remove(final char[] array, final int index) {
        return (char[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 'a')            = null
     * ArrayUtils.removeElement([], 'a')              = []
     * ArrayUtils.removeElement(['a'], 'b')           = ['a']
     * ArrayUtils.removeElement(['a', 'b'], 'a')      = ['b']
     * ArrayUtils.removeElement(['a', 'b', 'a'], 'a') = ['b', 'a']
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static char[] removeElement(final char[] array, final char element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static double[] remove(final double[] array, final int index) {
        return (double[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1.1)            = null
     * ArrayUtils.removeElement([], 1.1)              = []
     * ArrayUtils.removeElement([1.1], 1.2)           = [1.1]
     * ArrayUtils.removeElement([1.1, 2.3], 1.1)      = [2.3]
     * ArrayUtils.removeElement([1.1, 2.3, 1.1], 1.1) = [2.3, 1.1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static double[] removeElement(final double[] array, final double element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1.1], 0)           = []
     * ArrayUtils.remove([2.5, 6.0], 0)      = [6.0]
     * ArrayUtils.remove([2.5, 6.0], 1)      = [2.5]
     * ArrayUtils.remove([2.5, 6.0, 3.8], 1) = [2.5, 3.8]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static float[] remove(final float[] array, final int index) {
        return (float[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1.1)            = null
     * ArrayUtils.removeElement([], 1.1)              = []
     * ArrayUtils.removeElement([1.1], 1.2)           = [1.1]
     * ArrayUtils.removeElement([1.1, 2.3], 1.1)      = [2.3]
     * ArrayUtils.removeElement([1.1, 2.3, 1.1], 1.1) = [2.3, 1.1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static float[] removeElement(final float[] array, final float element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static int[] remove(final int[] array, final int index) {
        return (int[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static int[] removeElement(final int[] array, final int element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static long[] remove(final long[] array, final int index) {
        return (long[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static long[] removeElement(final long[] array, final long element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.remove([1], 0)         = []
     * ArrayUtils.remove([2, 6], 0)      = [6]
     * ArrayUtils.remove([2, 6], 1)      = [2]
     * ArrayUtils.remove([2, 6, 3], 1)   = [2, 3]
     * </pre>
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static short[] remove(final short[] array, final int index) {
        return (short[]) remove((Object) array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * <pre>
     * ArrayUtils.removeElement(null, 1)      = null
     * ArrayUtils.removeElement([], 1)        = []
     * ArrayUtils.removeElement([1], 2)       = [1]
     * ArrayUtils.removeElement([1, 3], 1)    = [3]
     * ArrayUtils.removeElement([1, 3, 1], 1) = [3, 1]
     * </pre>
     *
     * @param array   要从中移除元素的数组
     * @param element 要删除的元素
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    public static short[] removeElement(final short[] array, final short element) {
        final int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }
        return remove(array, index);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有后续元素都向左移动(从它们的索引中减去1)
     * 此方法返回一个新数组，除了指定位置上的元素外，该数组具有与输入数组相同的元素
     * 如果是{@code null}，则会抛出IndexOutOfBoundsException，无法指定有效的索引
     *
     * @param array 要从中移除元素的数组
     * @param index 要删除的元素的位置
     * @return 一个新数组，包含指定位置的元素以外的现有元素.
     */
    private static Object remove(final Object array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }

        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }

        return result;
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll(["a", "b", "c"], 0, 2) = ["b"]
     * ArrayUtils.removeAll(["a", "b", "c"], 1, 2) = ["a"]
     * </pre>
     *
     * @param <T>     数组的组件类型
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static <T> T[] removeAll(final T[] array, final int... indices) {
        return (T[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, "a", "b")            = null
     * ArrayUtils.removeElements([], "a", "b")              = []
     * ArrayUtils.removeElements(["a"], "b", "c")           = ["a"]
     * ArrayUtils.removeElements(["a", "b"], "a", "c")      = ["b"]
     * ArrayUtils.removeElements(["a", "b", "a"], "a")      = ["b", "a"]
     * ArrayUtils.removeElements(["a", "b", "a"], "a", "a") = ["b"]
     * </pre>
     *
     * @param <T>    数组的组件类型
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static <T> T[] removeElements(final T[] array, final T... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<T, MutableInt> occurrences = new HashMap<>(values.length);
        for (final T v : values) {
            final MutableInt count = occurrences.get(v);
            if (count == null) {
                occurrences.put(v, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final T key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (T[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static byte[] removeAll(final byte[] array, final int... indices) {
        return (byte[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static byte[] removeElements(final byte[] array, final byte... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final Map<Byte, MutableInt> occurrences = new HashMap<>(values.length);
        for (final byte v : values) {
            final Byte boxed = Byte.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final byte key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (byte[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static short[] removeAll(final short[] array, final int... indices) {
        return (short[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改.
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static short[] removeElements(final short[] array, final short... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Short, MutableInt> occurrences = new HashMap<>(values.length);
        for (final short v : values) {
            final Short boxed = Short.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final short key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (short[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static int[] removeAll(final int[] array, final int... indices) {
        return (int[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移.
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改
     * 返回数组的组件类型始终与输入数组的组件类型相同
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static int[] removeElements(final int[] array, final int... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Integer, MutableInt> occurrences = new HashMap<>(values.length);
        for (final int v : values) {
            final Integer boxed = Integer.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final int key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (int[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static char[] removeAll(final char[] array, final int... indices) {
        return (char[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改.
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static char[] removeElements(final char[] array, final char... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Character, MutableInt> occurrences = new HashMap<>(values.length);
        for (final char v : values) {
            final Character boxed = Character.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final char key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (char[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static long[] removeAll(final long[] array, final int... indices) {
        return (long[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改
     * 返回数组的组件类型始终与输入数组的组件类型相同
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static long[] removeElements(final long[] array, final long... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Long, MutableInt> occurrences = new HashMap<>(values.length);
        for (final long v : values) {
            final Long boxed = Long.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final long key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (long[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static float[] removeAll(final float[] array, final int... indices) {
        return (float[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改
     * 返回数组的组件类型始终与输入数组的组件类型相同
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static float[] removeElements(final float[] array, final float... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Float, MutableInt> occurrences = new HashMap<>(values.length);
        for (final float v : values) {
            final Float boxed = Float.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final float key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (float[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([1], 0)             = []
     * ArrayUtils.removeAll([2, 6], 0)          = [6]
     * ArrayUtils.removeAll([2, 6], 0, 1)       = []
     * ArrayUtils.removeAll([2, 6, 3], 1, 2)    = [2]
     * ArrayUtils.removeAll([2, 6, 3], 0, 2)    = [6]
     * ArrayUtils.removeAll([2, 6, 3], 0, 1, 2) = []
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static double[] removeAll(final double[] array, final int... indices) {
        return (double[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改.
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, 1, 2)      = null
     * ArrayUtils.removeElements([], 1, 2)        = []
     * ArrayUtils.removeElements([1], 2, 3)       = [1]
     * ArrayUtils.removeElements([1, 3], 1, 2)    = [3]
     * ArrayUtils.removeElements([1, 3, 1], 1)    = [3, 1]
     * ArrayUtils.removeElements([1, 3, 1], 1, 1) = [3]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static double[] removeElements(final double[] array, final double... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Double, MutableInt> occurrences = new HashMap<>(values.length);
        for (final double v : values) {
            final Double boxed = Double.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final double key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (double[]) removeAll(array, toRemove);
    }

    /**
     * 从指定数组中移除指定位置的元素。所有剩余的元素都向左移动
     * 返回数组的组件类型始终与输入数组的组件类型相同
     * 此方法返回一个新数组，该数组具有与输入数组相同的元素
     *
     * <pre>
     * ArrayUtils.removeAll([true, false, true], 0, 2) = [false]
     * ArrayUtils.removeAll([true, false, true], 1, 2) = [true]
     * </pre>
     *
     * @param array   要从中删除元素的数组可能不是{@code null}
     * @param indices 要删除的元素的位置
     * @return 一个新数组，包含指定位置之外的现有元素
     */
    public static boolean[] removeAll(final boolean[] array, final int... indices) {
        return (boolean[]) removeAll((Object) array, indices);
    }

    /**
     * 从指定数组中移除指定数量*的指定元素。所有后续元素都左移
     * 对于指定的要删除的元素，其数量大于原始数组中包含的数量，
     * 除了删除现有的匹配项外，不会发生任何更改.
     * 返回数组的组件类型始终与输入数组的组件类型相同.
     *
     * <pre>
     * ArrayUtils.removeElements(null, true, false)               = null
     * ArrayUtils.removeElements([], true, false)                 = []
     * ArrayUtils.removeElements([true], false, false)            = [true]
     * ArrayUtils.removeElements([true, false], true, true)       = [false]
     * ArrayUtils.removeElements([true, false, true], true)       = [false, true]
     * ArrayUtils.removeElements([true, false, true], true, true) = [false]
     * </pre>
     *
     * @param array  要从中删除元素的数组可能不是{@code null}
     * @param values 要删除的元素
     * @return 一个新数组，包含指定位置之外的现有元素.
     */
    public static boolean[] removeElements(final boolean[] array, final boolean... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Boolean, MutableInt> occurrences = new HashMap<>(2); // only two possible values here
        for (final boolean v : values) {
            final Boolean boxed = Boolean.valueOf(v);
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            } else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; i++) {
            final boolean key = array[i];
            final MutableInt count = occurrences.get(key);
            if (count != null) {
                if (count.get() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (boolean[]) removeAll(array, toRemove);
    }

    /**
     * 删除由索引指定的多个数组元素.
     *
     * @param array   源
     * @param indices 删除
     * @return 由{@code索引}的唯一值指定的相同类型的减元素的新数组
     */
    static Object removeAll(final Object array, final int... indices) {
        final int length = getLength(array);
        int diff = 0; // 不同索引的数量，即要删除的条目的数量
        final int[] clonedIndices = clone(indices);
        Arrays.sort(clonedIndices);

        // 标识结果数组的长度
        if (isNotEmpty(clonedIndices)) {
            int i = clonedIndices.length;
            int prevIndex = length;
            while (--i >= 0) {
                final int index = clonedIndices[i];
                if (index < 0 || index >= length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
                }
                if (index >= prevIndex) {
                    continue;
                }
                diff++;
                prevIndex = index;
            }
        }

        // 创建结果数组
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
        if (diff < length) {
            // 在最后一个拷贝之后建立索引
            int end = length;
            // 到目前为止没有复制条目的数量
            int dest = length - diff;
            for (int i = clonedIndices.length - 1; i >= 0; i--) {
                final int index = clonedIndices[i];
                if (end - index > 1) { // same as (cp > 0)
                    final int cp = end - index - 1;
                    dest -= cp;
                    System.arraycopy(array, index + 1, result, dest, cp);
                }
                end = index;
            }
            if (end > 0) {
                System.arraycopy(array, 0, result, 0, end);
            }
        }
        return result;
    }

    /**
     * 删除由索引指定的多个数组元素.
     *
     * @param array   源
     * @param indices 删除
     * @return 由{@code索引}的唯一值指定的相同类型的减元素的新数组
     */
    static Object removeAll(final Object array, final BitSet indices) {
        final int srcLength = getLength(array);
        final int removals = indices.cardinality();
        final Object result = Array.newInstance(array.getClass().getComponentType(), srcLength - removals);
        int srcIndex = 0;
        int destIndex = 0;
        int count;
        int set;
        while ((set = indices.nextSetBit(srcIndex)) != -1) {
            count = set - srcIndex;
            if (count > 0) {
                System.arraycopy(array, srcIndex, result, destIndex, count);
                destIndex += count;
            }
            srcIndex = indices.nextClearBit(set);
        }
        count = srcLength - srcIndex;
        if (count > 0) {
            System.arraycopy(array, srcIndex, result, destIndex, count);
        }
        return result;
    }

    /**
     * 该方法检查提供的数组是否按照类的{@code compareTo}方法排序.
     *
     * @param array 要检查的数组
     * @param <T>   要检查数组的数据类型，必须实现{@code Comparable}
     * @return 数组是否已排序
     */
    public static <T extends Comparable<? super T>> boolean isSorted(final T[] array) {
        return isSorted(array, (o1, o2) -> o1.compareTo(o2));
    }


    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array      要检查的数组
     * @param comparator 要比较的{@code Comparator}
     * @param <T>        数组的数据类型
     * @return 数组是否已排序
     */
    public static <T> boolean isSorted(final T[] array, final Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator should not be null.");
        }

        if (array == null || array.length < 2) {
            return true;
        }

        T previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final T current = array[i];
            if (comparator.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final int[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        int previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final int current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final long[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        long previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final long current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final short[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        short previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final short current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final double[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        double previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final double current = array[i];
            if (Double.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final float[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        float previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final float current = array[i];
            if (Float.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final byte[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        byte previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final byte current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final char[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        char previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final char current = array[i];
            if (CharUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 该方法检查提供的数组是否按照提供的{@code Comparator}排序.
     *
     * @param array 要检查的数组
     * @return 数组是否已排序
     */
    public static boolean isSorted(final boolean[] array) {
        if (array == null || array.length < 2) {
            return true;
        }

        boolean previous = array[0];
        final int n = array.length;
        for (int i = 1; i < n; i++) {
            final boolean current = array[i];
            if (BooleanUtils.compare(previous, current) > 0) {
                return false;
            }

            previous = current;
        }
        return true;
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static boolean[] removeAllOccurences(final boolean[] array, final boolean element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static char[] removeAllOccurences(final char[] array, final char element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static byte[] removeAllOccurences(final byte[] array, final byte element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static short[] removeAllOccurences(final short[] array, final short element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static int[] removeAllOccurences(final int[] array, final int element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static long[] removeAllOccurences(final long[] array, final long element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static float[] removeAllOccurences(final float[] array, final float element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static double[] removeAllOccurences(final double[] array, final double element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 从指定的布尔数组中移除指定元素.
     * 所有随后的元素都向左移动(从它们的索引中减去1).
     * 如果数组不包含此类元素，则不会从数组中删除任何元素.
     * 如果输入的数组是null，则返回的是null
     *
     * @param <T>     对象
     * @param element 要删除的元素
     * @param array   输入数组
     * @return 一个新数组，包含除指定元素之外的现有元素.
     */
    public static <T> T[] removeAllOccurences(final T[] array, final T element) {
        int index = indexOf(array, element);
        if (index == INDEX_NOT_FOUND) {
            return clone(array);
        }

        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count = 1;

        while ((index = indexOf(array, element, indices[count - 1] + 1)) != INDEX_NOT_FOUND) {
            indices[count++] = index;
        }

        return removeAll(array, Arrays.copyOf(indices, count));
    }

    /**
     * 返回一个数组，该数组包含参数数组中每个元素的字符串表示形式.
     * 该方法为一个{@code null}输入数组返回{@code null}.
     *
     * @param array 要处理的{@code Object[]} 可以为null
     * @return {@code String[]}的大小与其元素的字符串表示形式相同，{@code null}如果是空数组输入
     */
    public static String[] toStringArray(final Object[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].toString();
        }

        return result;
    }

    /**
     * 返回一个数组，该数组包含处理{@code null}元素的参数数组中每个元素的字符串表示形式
     * 该方法为一个{@code null}输入数组返回{@code null}
     *
     * @param array                要处理的{@code Object[]} 可以为null
     * @param valueForNullElements 找到{@code null} 时要插入的值
     * @return (@ code String)阵列，如果没有阵列输入
     */
    public static String[] toStringArray(final Object[] array, final String valueForNullElements) {
        if (null == array) {
            return null;
        } else if (array.length == 0) {
            return Normal.EMPTY_STRING_ARRAY;
        }

        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            final Object object = array[i];
            result[i] = (object == null ? valueForNullElements : object.toString());
        }

        return result;
    }

    /**
     * 将新元素插入到到已有数组中的某个位置
     * 添加新元素会生成一个新的数组，不影响原数组
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    public static <T> T[] insert(T[] buffer, int index, T... newElements) {
        return (T[]) insert((Object) buffer, index, newElements);
    }

    /**
     * 将新元素插入到到已有数组中的某个位置
     * 添加新元素会生成一个新的数组，不影响原数组
     * 如果插入位置为为负数，从原数组从后向前计数，若大于原数组长度，则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置，此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     */
    public static <T> Object insert(Object array, int index, T... newElements) {
        if (isEmpty(newElements)) {
            return array;
        }
        if (isEmpty(array)) {
            return newElements;
        }

        final int len = getLength(array);
        if (index < 0) {
            index = (index % len) + len;
        }

        final T[] result = newArray(array.getClass().getComponentType(), Math.max(len, index) + newElements.length);
        System.arraycopy(array, 0, result, 0, Math.min(len, index));
        System.arraycopy(newElements, 0, result, index, newElements.length);
        if (index < len) {
            System.arraycopy(array, index, result, index + newElements.length, len - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static boolean[] insert(final int index, final boolean[] array, final boolean... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final boolean[] result = new boolean[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static byte[] insert(final int index, final byte[] array, final byte... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final byte[] result = new byte[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static char[] insert(final int index, final char[] array, final char... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final char[] result = new char[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static double[] insert(final int index, final double[] array, final double... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final double[] result = new double[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static float[] insert(final int index, final float[] array, final float... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final float[] result = new float[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static int[] insert(final int index, final int[] array, final int... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final int[] result = new int[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static long[] insert(final int index, final long[] array, final long... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final long[] result = new long[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static short[] insert(final int index, final short[] array, final short... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final short[] result = new short[array.length + values.length];

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 将元素插入到给定索引处的数组中(从零开始)
     * 当一个数组被返回时，它总是一个新的数组
     *
     * <pre>
     * ArrayUtils.insert(index, null, null)      = null
     * ArrayUtils.insert(index, array, null)     = cloned copy of 'array'
     * ArrayUtils.insert(index, null, values)    = null
     * </pre>
     *
     * @param <T>    对象
     * @param index  插入位置,此位置为对应此位置元素之前的空档
     * @param array  已有数组
     * @param values 要插入的新值可以是{@code null}
     * @return 新数组.
     */
    public static <T> T[] insert(final int index, final T[] array, final T... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }

        final Class<?> type = array.getClass().getComponentType();
        final T[] result = (T[]) Array.newInstance(type, array.length + values.length);

        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final Object[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final Object[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final boolean[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final boolean[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final byte[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final byte[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final char[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final char[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final short[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final short[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final int[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final int[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final long[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final long[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final float[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final float[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array 要洗牌的数组
     */
    public static void shuffle(final double[] array) {
        shuffle(array, new Random());
    }

    /**
     * 使用Fisher-Yates算法随机遍历指定数组的元素.
     *
     * @param array  要洗牌的数组
     * @param random 用于排列元素的随机性的来源
     */
    public static void shuffle(final double[] array, final Random random) {
        for (int i = array.length; i > 1; i--) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     * @return 空数组
     */

    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 新建一个空数组
     *
     * @param <T>     数组元素类型
     * @param newSize 大小
     * @return 空数组
     */

    public static <T> T[] newArray(int newSize) {
        return (T[]) new Object[newSize];
    }

    /**
     * 强转数组类型
     * 强制转换的前提是数组元素类型可被强制转换
     * 强制转换后会生成一个新数组
     *
     * @param type     数组类型或数组元素类型
     * @param arrayObj 原数组
     * @return 转换后的数组类型
     * @throws NullPointerException     提供参数为空
     * @throws IllegalArgumentException 参数arrayObj不是数组
     */
    public static Object[] newArray(Class<?> type, Object arrayObj) {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (false == arrayObj.getClass().isArray()) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }
        if (null == type) {
            return (Object[]) arrayObj;
        }

        final Class<?> componentType = type.isArray() ? type.getComponentType() : type;
        final Object[] array = (Object[]) arrayObj;
        final Object[] result = newArray(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     * @return 元素类型
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param arrayClass 数组类
     * @return 元素类型
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return null == arrayClass ? null : arrayClass.getComponentType();
    }

    /**
     * 根据数组元素类型,获取数组的类型
     * 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     * @return 数组类型
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /**
     * 将新元素添加到已有数组中
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param buffer      已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    public static <T> T[] append(T[] buffer, T... newElements) {
        if (isEmpty(buffer)) {
            return newElements;
        }
        return insert(buffer, buffer.length, newElements);
    }

    /**
     * 将新元素添加到已有数组中
     * 添加新元素会生成一个新的数组，不影响原数组
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param newElements 新元素
     * @return 新数组
     */
    public static <T> Object append(Object array, T... newElements) {
        if (isEmpty(array)) {
            return newElements;
        }
        return insert(array, getLength(array), newElements);
    }

    /**
     * 将元素值设置为数组的某个位置,当给定的index大于数组长度,则追加
     *
     * @param <T>    数组元素类型
     * @param buffer 已有数组
     * @param index  位置,大于长度追加,否则替换
     * @param value  新值
     * @return 新数组或原有数组
     */
    public static <T> T[] setOrAppend(T[] buffer, int index, T value) {
        if (index < buffer.length) {
            Array.set(buffer, index, value);
            return buffer;
        } else {
            return append(buffer, value);
        }
    }

    /**
     * 将新元素插入到到已有数组中的某个位置
     * 添加新元素会生成一个新的数组,不影响原数组
     * 如果插入位置为为负数,从原数组从后向前计数,若大于原数组长度,则空白处用null填充
     *
     * @param <T>         数组元素类型
     * @param array       已有数组
     * @param index       插入位置,此位置为对应此位置元素之前的空档
     * @param newElements 新元素
     * @return 新数组
     *
     */
    /**
     * 将元素值设置为数组的某个位置,当给定的index大于数组长度,则追加
     *
     * @param array 已有数组
     * @param index 位置,大于长度追加,否则替换
     * @param value 新值
     * @return 新数组或原有数组
     */
    public static Object setOrAppend(Object array, int index, Object value) {
        if (index < getLength(array)) {
            Array.set(array, index, value);
            return array;
        } else {
            return append(array, value);
        }
    }

    /**
     * 生成一个新的重新设置大小的数组
     * 调整大小后拷贝原数组到新数组下 扩大则占位前N个位置,缩小则截断
     *
     * @param <T>           数组元素类型
     * @param buffer        原数组
     * @param newSize       新的数组大小
     * @param componentType 数组元素类型
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize, Class<?> componentType) {
        T[] newArray = newArray(componentType, newSize);
        if (isNotEmpty(buffer)) {
            System.arraycopy(buffer, 0, newArray, 0, Math.min(buffer.length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组
     * 新数组的类型为原数组的类型,调整大小后拷贝原数组到新数组下 扩大则占位前N个位置,缩小则截断
     *
     * @param <T>     数组元素类型
     * @param buffer  原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize) {
        return resize(buffer, newSize, buffer.getClass().getComponentType());
    }

    /**
     * 生成一个新的重新设置大小的数组
     * 调整大小后拷贝原数组到新数组下 扩大则占位前N个位置,其它位置补充0,缩小则截断
     *
     * @param bytes   原数组
     * @param newSize 新的数组大小
     * @return 调整后的新数组
     */
    public static byte[] resize(byte[] bytes, int newSize) {
        if (newSize < 0) {
            return bytes;
        }
        final byte[] newArray = new byte[newSize];
        if (newSize > 0 && isNotEmpty(bytes)) {
            System.arraycopy(bytes, 0, newArray, 0, Math.min(bytes.length, newSize));
        }
        return newArray;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}
     * 数组复制
     *
     * @param src     源数组
     * @param srcPos  源数组开始位置
     * @param dest    目标数组
     * @param destPos 目标数组开始位置
     * @param length  拷贝数组长度
     * @return 目标数组
     */
    public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}
     * 数组复制,缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     * @return 目标数组
     */
    public static Object copy(Object src, Object dest, int length) {
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 生成一个从0开始的数字列表
     *
     * @param excludedEnd 结束的数字(不包含)
     * @return 数字列表
     */
    public static int[] range(int excludedEnd) {
        return range(0, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字(包含)
     * @param excludedEnd   结束的数字(不包含)
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd) {
        return range(includedStart, excludedEnd, 1);
    }

    /**
     * 生成一个数字列表
     * 自动判定正序反序
     *
     * @param includedStart 开始的数字(包含)
     * @param excludedEnd   结束的数字(不包含)
     * @param step          步进
     * @return 数字列表
     */
    public static int[] range(int includedStart, int excludedEnd, int step) {
        if (includedStart > excludedEnd) {
            int tmp = includedStart;
            includedStart = excludedEnd;
            excludedEnd = tmp;
        }

        if (step <= 0) {
            step = 1;
        }

        int deviation = excludedEnd - includedStart;
        int length = deviation / step;
        if (deviation % step != 0) {
            length += 1;
        }
        int[] range = new int[length];
        for (int i = 0; i < length; i++) {
            range[i] = includedStart;
            includedStart += step;
        }
        return range;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static int[] unWrap(Integer... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_INT_ARRAY;
        }

        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].intValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Long[] wrap(long... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_LONG_OBJECT_ARRAY;
        }

        Long[] array = new Long[length];
        for (int i = 0; i < length; i++) {
            array[i] = Long.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static long[] unWrap(Long... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_LONG_ARRAY;
        }

        long[] array = new long[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].longValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Character[] wrap(char... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_CHARACTER_OBJECT_ARRAY;
        }

        Character[] array = new Character[length];
        for (int i = 0; i < length; i++) {
            array[i] = Character.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static char[] unWrap(Character... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_CHAR_ARRAY;
        }

        char[] array = new char[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].charValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Byte[] wrap(byte... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_BYTE_OBJECT_ARRAY;
        }

        Byte[] array = new Byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = Byte.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static byte[] unWrap(Byte... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_BYTE_ARRAY;
        }

        byte[] array = new byte[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].byteValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Short[] wrap(short... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_SHORT_OBJECT_ARRAY;
        }

        Short[] array = new Short[length];
        for (int i = 0; i < length; i++) {
            array[i] = Short.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static short[] unWrap(Short... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_SHORT_ARRAY;
        }

        short[] array = new short[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].shortValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Float[] wrap(float... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_FLOAT_OBJECT_ARRAY;
        }

        Float[] array = new Float[length];
        for (int i = 0; i < length; i++) {
            array[i] = Float.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static float[] unWrap(Float... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_FLOAT_ARRAY;
        }

        float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].floatValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Double[] wrap(double... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_DOUBLE_OBJECT_ARRAY;
        }

        Double[] array = new Double[length];
        for (int i = 0; i < length; i++) {
            array[i] = Double.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static double[] unWrap(Double... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_DOUBLE_ARRAY;
        }

        double[] array = new double[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].doubleValue();
        }
        return array;
    }

    /**
     * 将原始类型数组包装为包装类型
     *
     * @param values 原始类型数组
     * @return 包装类型数组
     */
    public static Boolean[] wrap(boolean... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }

        Boolean[] array = new Boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = Boolean.valueOf(values[i]);
        }
        return array;
    }

    /**
     * 包装类数组转为原始类型数组
     *
     * @param values 包装类型数组
     * @return 原始类型数组
     */
    public static boolean[] unWrap(Boolean... values) {
        if (null == values) {
            return null;
        }
        int length = values.length;
        if (0 == length) {
            return Normal.EMPTY_BOOLEAN_ARRAY;
        }

        boolean[] array = new boolean[length];
        for (int i = 0; i < length; i++) {
            array[i] = values[i].booleanValue();
        }
        return array;
    }

    /**
     * 包装数组对象
     *
     * @param obj 对象,可以是对象数组或者基本类型数组
     * @return 包装类型数组或对象数组
     * @throws InstrumentException 对象为非数组
     */
    public static Object[] wrap(Object obj) {
        if (null == obj) {
            return null;
        }
        if (isArray(obj)) {
            try {
                return (Object[]) obj;
            } catch (Exception e) {
                String className = obj.getClass().getComponentType().getName();
                switch (className) {
                    case "long":
                        return wrap((long[]) obj);
                    case "int":
                        return wrap(obj);
                    case "short":
                        return wrap((short[]) obj);
                    case "char":
                        return wrap((char[]) obj);
                    case "byte":
                        return wrap((byte[]) obj);
                    case "boolean":
                        return wrap((boolean[]) obj);
                    case "float":
                        return wrap((float[]) obj);
                    case "double":
                        return wrap((double[]) obj);
                    default:
                        throw new InstrumentException(e);
                }
            }
        }
        throw new InstrumentException("is not Array!");
    }

    /**
     * 获取数组对象中指定index的值,支持负数,例如-1表示倒数第一个值
     *
     * @param <T>   数组元素类型
     * @param array 数组对象
     * @param index 下标,支持负数
     * @return 值
     */
    public static <T> T get(Object array, int index) {
        if (index < 0) {
            index += Array.getLength(array);
        }
        return (T) Array.get(array, index);
    }

    /**
     * 获取数组中指定多个下标元素值,组成新数组
     *
     * @param <T>     数组元素类型
     * @param array   数组
     * @param indexes 下标列表
     * @return 结果
     */
    public static <T> T[] get(Object array, int... indexes) {
        T[] result = newArray(indexes.length);
        for (int i : indexes) {
            result[i] = get(array, i);
        }
        return result;
    }

    /**
     * 获取子数组
     *
     * @param <T>   数组元素类型
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static <T> T[] sub(T[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return newArray(array.getClass().getComponentType(), 0);
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return newArray(array.getClass().getComponentType(), 0);
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static byte[] sub(byte[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new byte[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new byte[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static int[] sub(int[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new int[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new int[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static long[] sub(long[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new long[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new long[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static short[] sub(short[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new short[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new short[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static char[] sub(char[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new char[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new char[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static double[] sub(double[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new double[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new double[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static float[] sub(float[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new float[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new float[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     * @see Arrays#copyOfRange(Object[], int, int)
     */
    public static boolean[] sub(boolean[] array, int start, int end) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return new boolean[0];
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return new boolean[0];
            }
            end = length;
        }
        return Arrays.copyOfRange(array, start, end);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @return 新的数组
     */
    public static Object[] sub(Object array, int start, int end) {
        return sub(array, start, end, 1);
    }

    /**
     * 获取子数组
     *
     * @param array 数组
     * @param start 开始位置(包括)
     * @param end   结束位置(不包括)
     * @param step  步进
     * @return 新的数组
     */
    public static Object[] sub(Object array, int start, int end, int step) {
        int length = getLength(array);
        if (start < 0) {
            start += length;
        }
        if (end < 0) {
            end += length;
        }
        if (start == length) {
            return Normal.EMPTY_OBJECT_ARRAY;
        }
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (end > length) {
            if (start >= length) {
                return Normal.EMPTY_OBJECT_ARRAY;
            }
            end = length;
        }

        if (step <= 1) {
            step = 1;
        }

        ArrayList<Object> list = new ArrayList<>();
        for (int i = start; i < end; i += step) {
            list.add(get(array, i));
        }

        return list.toArray();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction) {
        return join(array, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @param prefix      每个元素添加的前缀，null表示不添加
     * @param suffix      每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction, String prefix, String suffix) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (T item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            if (ArrayUtils.isArray(item)) {
                sb.append(join(ArrayUtils.wrap(item), conjunction, prefix, suffix));
            } else if (item instanceof Iterable<?>) {
                sb.append(CollUtils.join((Iterable<?>) item, conjunction, prefix, suffix));
            } else if (item instanceof Iterator<?>) {
                sb.append(IterUtils.join((Iterator<?>) item, conjunction, prefix, suffix));
            } else {
                sb.append(StringUtils.wrap(StringUtils.toString(item), prefix, suffix));
            }
        }
        return sb.toString();
    }


    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @param editor      每个元素的编辑器，null表示不编辑
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (T item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            if (null != editor) {
                item = editor.edit(item);
            }
            if (null != item) {
                sb.append(StringUtils.toString(item));
            }
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(long[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (long item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(int[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (int item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(short[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (short item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(char[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (char item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(byte[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (byte item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(boolean[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (boolean item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(float[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (float item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(double[] array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (double item : array) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(conjunction);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object array, CharSequence conjunction) {
        if (isArray(array)) {
            final Class<?> componentType = array.getClass().getComponentType();
            if (componentType.isPrimitive()) {
                final String componentTypeName = componentType.getName();
                switch (componentTypeName) {
                    case "long":
                        return join((long[]) array, conjunction);
                    case "int":
                        return join((int[]) array, conjunction);
                    case "short":
                        return join((short[]) array, conjunction);
                    case "char":
                        return join((char[]) array, conjunction);
                    case "byte":
                        return join((byte[]) array, conjunction);
                    case "boolean":
                        return join((boolean[]) array, conjunction);
                    case "float":
                        return join((float[]) array, conjunction);
                    case "double":
                        return join((double[]) array, conjunction);
                    default:
                        throw new InstrumentException("Unknown primitive type: [{}]", componentTypeName);
                }
            } else {
                return join((Object[]) array, conjunction);
            }
        }
        throw new InstrumentException(StringUtils.format("[{}] is not a Array!", array.getClass()));
    }

    /**
     * 取最小值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static <T extends Comparable<? super T>> T min(T[] numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (ObjectUtils.compare(min, numberArray[i]) > 0) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static long min(long... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static int min(int... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static short min(short... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static char min(char... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static byte min(byte... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static double min(double... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最小值
     *
     * @param numberArray 数字数组
     * @return 最小值
     */
    public static float min(float... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float min = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (min > numberArray[i]) {
                min = numberArray[i];
            }
        }
        return min;
    }

    /**
     * 取最大值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static <T extends Comparable<? super T>> T max(T[] numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (ObjectUtils.compare(max, numberArray[i]) < 0) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static long max(long... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        long max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static int max(int... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        int max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static short max(short... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        short max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static char max(char... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        char max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static byte max(byte... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        byte max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static double max(double... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        double max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param numberArray 数字数组
     * @return 最大值
     */
    public static float max(float... numberArray) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        float max = numberArray[0];
        for (int i = 0; i < numberArray.length; i++) {
            if (max < numberArray[i]) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 取最大值
     *
     * @param <T>         元素类型
     * @param numberArray 数字数组
     * @param comparator  比较器，null表示默认比较器
     * @return 最大值
     */
    public static <T extends Comparable<? super T>> T max(T[] numberArray, Comparator<T> comparator) {
        if (isEmpty(numberArray)) {
            throw new IllegalArgumentException("Number array must not empty !");
        }
        T max = numberArray[0];
        for (int i = 1; i < numberArray.length; i++) {
            if (ObjectUtils.compare(max, numberArray[i], comparator) < 0) {
                max = numberArray[i];
            }
        }
        return max;
    }

    /**
     * 过滤
     * 过滤过程通过传入的Editor实现来返回需要的元素内容,这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象,如果返回null表示这个元素对象抛弃
     * 2、修改元素对象,返回集合中为修改后的对象
     * </pre>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param editor 编辑器接口
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Editor<T> editor) {
        ArrayList<T> list = new ArrayList<>(array.length);
        T modified;
        for (T t : array) {
            modified = editor.edit(t);
            if (null != modified) {
                list.add(modified);
            }
        }
        return list.toArray(Arrays.copyOf(array, list.size()));
    }

    /**
     * 过滤
     * 过滤过程通过传入的Filter实现来过滤返回需要的元素内容,这个Editor实现可以实现以下功能：
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param filter 过滤器接口,用于定义过滤规则
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Filter<T> filter) {
        ArrayList<T> list = new ArrayList<>(array.length);
        boolean isAccept;
        for (T t : array) {
            isAccept = filter.accept(t);
            if (isAccept) {
                list.add(t);
            }
        }
        return list.toArray(Arrays.copyOf(array, list.size()));
    }

    /**
     * 映射键值(参考Python的zip()函数)，返回Map无序
     * 例如：
     * keys = [a,b,c,d]
     * values = [1,2,3,4]
     * 则得到的Map是 {a=1, b=2, c=3, d=4}
     * 如果两个数组长度不同，则只对应最短部分
     *
     * @param <K>    Key类型
     * @param <V>    Value类型
     * @param keys   键列表
     * @param values 值列表
     * @return Map
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values) {
        return zip(keys, values, false);
    }

    /**
     * 映射键值(参考Python的zip()函数)
     * 例如：
     * keys = [a,b,c,d]
     * values = [1,2,3,4]
     * 则得到的Map是 {a=1, b=2, c=3, d=4}
     * 如果两个数组长度不同,则只对应最短部分
     *
     * @param <K>     Key类型
     * @param <V>     Value类型
     * @param keys    键列表
     * @param values  值列表
     * @param isOrder 是否有序
     * @return Map
     */
    public static <K, V> Map<K, V> zip(K[] keys, V[] values, boolean isOrder) {
        if (isEmpty(keys) || isEmpty(values)) {
            return null;
        }

        int size = Math.min(keys.length, values.length);
        Map<K, V> map = CollUtils.newHashMap(size, isOrder);
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }

    /**
     * 检查两个数组是否为同一类型
     *
     * @param array1 第1个数组不能是{@code null}
     * @param array2 第2个数组不能是{@code null}
     * @return 如果数组类型匹配，则为{@code true}
     * @throws IllegalArgumentException 如果其中一个数组是{@code null}
     */
    public static boolean isSameType(final Object array1, final Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }

    /**
     * 返回数组中指定元素所在位置,忽略大小写,未找到返回{@link #INDEX_NOT_FOUND}
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 数组中指定元素所在位置, 未找到返回{@link #INDEX_NOT_FOUND}
     */
    public static int indexOfIgnoreCase(CharSequence[] array, CharSequence value) {
        if (null != array) {
            for (int i = 0; i < array.length; i++) {
                if (StringUtils.equalsIgnoreCase(array[i], value)) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * 返回是否可以在给定索引处安全地访问给定数组
     *
     * @param <T>   数组的组件类型
     * @param array 要检查的数组可能为空
     * @param index 要检查的数组的索引
     * @return 给定索引在给定数组中是否可安全访问
     */
    public static <T> boolean isArrayIndexValid(T[] array, int index) {
        if (getLength(array) == 0 || array.length <= index) {
            return false;
        }

        return index >= 0;
    }

    /**
     * 数组中是否包含元素,忽略大小写
     *
     * @param array 数组
     * @param value 被检查的元素
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(CharSequence[] array, CharSequence value) {
        return indexOfIgnoreCase(array, value) > INDEX_NOT_FOUND;
    }

    /**
     * 返回指定数组的长度
     *
     * <pre>
     * ArrayUtils.getLength(null)            = 0
     * ArrayUtils.getLength([])              = 0
     * ArrayUtils.getLength([null])          = 1
     * ArrayUtils.getLength([true, false])   = 2
     * ArrayUtils.getLength([1, 2, 3])       = 3
     * ArrayUtils.getLength(["a", "b", "c"]) = 3
     * </pre>
     *
     * @param array 要从中检索长度的数组可以为空
     * @return 数组的长度，如果数组是{@code null}，则为{@code 0}
     * @throws IllegalArgumentException 如果对象参数不是数组
     */
    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }

    /**
     * 获得一个数组哈希码,用于正确处理多维数组
     * 多维基元数组也可以用该方法正确处理
     *
     * @param array 数组
     * @return 返回数组的哈希码
     */
    public static int hashCode(final Object array) {
        return new HashCodeBuilder().append(array).toHashCode();
    }

    /**
     * 强转数组类型
     * 强制转换的前提是数组元素类型可被强制转换
     * 强制转换后会生成一个新数组
     *
     * @param type     数组类型或数组元素类型
     * @param arrayObj 原数组
     * @return 转换后的数组类型
     * @throws NullPointerException     提供参数为空
     * @throws IllegalArgumentException 参数arrayObj不是数组
     */
    public static Object[] cast(Class<?> type, Object arrayObj) throws NullPointerException, IllegalArgumentException {
        if (null == arrayObj) {
            throw new NullPointerException("Argument [arrayObj] is null !");
        }
        if (false == arrayObj.getClass().isArray()) {
            throw new IllegalArgumentException("Argument [arrayObj] is not array !");
        }
        if (null == type) {
            return (Object[]) arrayObj;
        }

        final Class<?> componentType = type.isArray() ? type.getComponentType() : type;
        final Object[] array = (Object[]) arrayObj;
        final Object[] result = newArray(componentType, array.length);
        System.arraycopy(array, 0, result, 0, array.length);
        return result;
    }

}
