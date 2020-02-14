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
package org.aoju.bus.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 返回结果公用
 * </p>
 *
 * @author Kimi Liu
 * @version 5.6.0
 * @since JDK 1.8+
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Result<T> extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    protected int total;
    protected List<T> rows;

    public Result() {
    }

    public Result(int total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Result(List<T> rows, int pageSize) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("data must be not empty!");
        }
        new Result<>(rows, rows.size(), pageSize);
    }

    public Result(List<T> rows, int total, int pageSize) {
        this.total = total;
        this.pageSize = pageSize;
        this.rows = rows;
    }

    public static <T> Result<T> Result(List<T> rows, int pageSize) {
        return new Result<>(rows, pageSize);
    }

    /**
     * 得到分页后的数据
     *
     * @param pageNo 页码
     * @return 分页后结果
     */
    public List<T> get(int pageNo) {
        int fromIndex = (pageNo - 1) * this.pageSize;
        if (fromIndex >= this.rows.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNo * this.pageSize;
        if (toIndex >= this.rows.size()) {
            toIndex = this.rows.size();
        }
        return this.rows.subList(fromIndex, toIndex);
    }

}
