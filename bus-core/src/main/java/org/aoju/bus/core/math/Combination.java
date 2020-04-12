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
package org.aoju.bus.core.math;

import org.aoju.bus.core.utils.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 组合,即C(n, m)
 * 排列组合相关类
 *
 * @author Kimi Liu
 * @version 5.8.3
 * @since JDK 1.8+
 */
public class Combination {

    private String[] datas;

    /**
     * 组合,即C(n, m)
     * 排列组合相关类
     *
     * @param datas 用于组合的数据
     */
    public Combination(String[] datas) {
        this.datas = datas;
    }

    /**
     * 计算组合数,即C(n, m) = n!/((n-m)! * m!)
     *
     * @param n 总数
     * @param m 选择的个数
     * @return 组合数
     */
    public static long count(int n, int m) {
        if (0 == m) {
            return 1;
        }
        if (n == m) {
            return NumberUtils.factorial(n) / NumberUtils.factorial(m);
        }
        return (n > m) ? NumberUtils.factorial(n, n - m) / NumberUtils.factorial(m) : 0;
    }

    /**
     * 计算组合总数,即C(n, 1) + C(n, 2) + C(n, 3)...
     *
     * @param n 总数
     * @return 组合数
     */
    public static long countAll(int n) {
        long total = 0;
        for (int i = 1; i <= n; i++) {
            total += count(n, i);
        }
        return total;
    }

    /**
     * 组合选择（从列表中选择m个组合）
     *
     * @param m 选择个数
     * @return 组合结果
     */
    public List<String[]> select(int m) {
        final List<String[]> result = new ArrayList<>((int) count(this.datas.length, m));
        select(0, new String[m], 0, result);
        return result;
    }

    /**
     * 全组合
     *
     * @return 全排列结果
     */
    public List<String[]> selectAll() {
        final List<String[]> result = new ArrayList<>((int) countAll(this.datas.length));
        for (int i = 1; i <= this.datas.length; i++) {
            result.addAll(select(i));
        }
        return result;
    }

    /**
     * 组合选择
     *
     * @param dataIndex   待选开始索引
     * @param resultList  前面（resultIndex-1）个的组合结果
     * @param resultIndex 选择索引,从0开始
     * @param result      待选列表
     */
    private void select(int dataIndex, String[] resultList, int resultIndex, List<String[]> result) {
        int resultLen = resultList.length;
        int resultCount = resultIndex + 1;
        if (resultCount > resultLen) { // 全部选择完时,输出组合结果
            result.add(Arrays.copyOf(resultList, resultList.length));
            return;
        }

        // 递归选择下一个
        for (int i = dataIndex; i < datas.length + resultCount - resultLen; i++) {
            resultList[resultIndex] = datas[i];
            select(i + 1, resultList, resultIndex + 1, result);
        }
    }

}
