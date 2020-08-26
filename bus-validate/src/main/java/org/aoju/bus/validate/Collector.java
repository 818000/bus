/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
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
package org.aoju.bus.validate;

import lombok.Data;
import org.aoju.bus.validate.validators.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * 校验结果收集器
 *
 * @author Kimi Liu
 * @version 6.0.8
 * @since JDK 1.8+
 */
@Data
public class Collector {

    /**
     * 被校验对象
     */
    private Validated target;

    /**
     * 校验结果
     */
    private List<Collector> result;

    private Property property;

    private boolean pass;


    public Collector(Validated target) {
        this.target = target;
        this.result = new ArrayList<>();
    }

    public Collector(Collector collector) {
        this.target = collector.getTarget();
        this.result = new ArrayList<>();
        this.result.add(collector);
    }

    public Collector(Validated target, Property property, boolean pass) {
        this.target = target;
        this.property = property;
        this.pass = pass;
    }

    /**
     * 收集校验结果
     *
     * @param collector 校验结果
     */
    public void collect(Collector collector) {
        this.result.add(collector);
    }

    /**
     * 获取所有的基础校验结果
     *
     * @return 基础校验结果集合
     */
    public List<Collector> getResult() {
        List<Collector> list = new ArrayList<>(16);
        for (Collector collector : this.result) {
            if (collector instanceof Collector) {
                list.addAll(collector.getResult());
            } else {
                throw new IllegalArgumentException("不支持收集的校验结果对象：" + collector);
            }
        }
        return list;
    }

    public Validated getTarget() {
        return target;
    }

    public boolean isPass() {
        return this.result.stream().allMatch(Collector::isPass);
    }

}
