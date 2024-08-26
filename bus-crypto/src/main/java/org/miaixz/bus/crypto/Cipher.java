/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2024 miaixz.org and other contributors.                    ~
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
package org.miaixz.bus.crypto;

import java.util.Arrays;

import org.miaixz.bus.core.lang.Algorithm;

/**
 * 密码接口，提供统一的API，用于兼容和统一JCE和BouncyCastle等库的操作
 * <ul>
 * <li>process和doFinal组合使用，用于分块加密或解密。 例如处理块的大小为8，实际需要加密的报文长度为23，那么需要分三块进行加密，前面2块长度为8的报文需要调用process进行部分加密，
 * 部分加密的结果可以从process的返回值获取到， 最后的7长度(其实一般会填充到长度为块长度8)的报文则调用doFinal进行加密，结束整个部分加密的操作。</li>
 * <li>processFinal默认处理并输出小于块的数据，或一次性数据。</li>
 * </ul>
 *
 * @author Kimi Liu
 * @since Java 17+
 */
public interface Cipher {

    /**
     * 获取算法名称
     *
     * @return 算法名称
     */
    String getAlgorithm();

    /**
     * 获取块大小，当为Stream方式加密时返回0
     *
     * @return 块大小，-1表示非块加密
     */
    int getBlockSize();

    /**
     * 初始化模式和参数
     *
     * @param mode       模式，如加密模式或解密模式
     * @param parameters Cipher所需参数，包括Key、Random、IV等信息
     */
    void init(Algorithm.Type mode, Parameters parameters);

    /**
     * 返回输出缓冲区为了保存下一个update或doFinal操作的结果所需的长度（以字节为单位） 下一个update或doFinal调用的实际输出长度可能小于此方法返回的长度。 一般为块大小对应的输出大小
     *
     * @param len 输入长度
     * @return 输出长度，-1表示非块加密
     */
    int getOutputSize(int len);

    /**
     * 执行运算，可以是加密运算或解密运算 此方法主要处理一块数据，一块数据处理完毕后，应调用{@link #doFinal(byte[], int)}处理padding等剩余数据。
     *
     * @param in     输入数据
     * @param inOff  输入数据开始位置
     * @param len    被处理数据长度
     * @param out    输出数据
     * @param outOff 输出数据开始位置
     * @return 处理长度
     */
    int process(byte[] in, int inOff, int len, byte[] out, int outOff);

    /**
     * 处理最后一块数据 当{@link #process(byte[], int, int, byte[], int)}处理完数据后非完整块数据，此方法用于处理块中剩余的bytes
     * 如加密数据要求128bit，即16byes的整数，单数处理数据后为15bytes，此时根据padding方式不同，可填充剩余1byte为指定值（如填充0）
     * 当对数据进行分段加密时，需要首先多次执行process方法，在最后一块数据处理完后执行此方法。
     *
     * @param out    经过process执行过运算的结果数据
     * @param outOff 数据处理开始位置
     * @return 处理长度
     */
    int doFinal(byte[] out, int outOff);

    /**
     * 处理数据，并返回最终结果 此方法用于完整处理一块数据并返回。
     *
     * @param in 输入数据
     * @return 结果数据
     */
    default byte[] processFinal(final byte[] in) {
        return processFinal(in, 0, in.length);
    }

    /**
     * 处理数据，并返回最终结果 此方法用于完整处理一块数据并返回。
     *
     * @param in       输入数据
     * @param inOffset 输入开始的 input中的偏移量
     * @param inputLen 输入长度
     * @return 结果数据
     * @see #process(byte[], int, int, byte[], int)
     * @see #doFinal(byte[], int)
     */
    default byte[] processFinal(final byte[] in, final int inOffset, final int inputLen) {
        final byte[] buf = new byte[getOutputSize(in.length)];
        int len = process(in, inOffset, inputLen, buf, 0);
        // 处理剩余数据，如Padding数据等
        len += doFinal(buf, len);
        return (len == buf.length) ? buf : Arrays.copyOfRange(buf, 0, len);
    }

    /**
     * Cipher所需参数，包括Key、Random、IV等信息
     */
    interface Parameters {

    }

}
