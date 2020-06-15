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
package org.aoju.bus.forest.complex;

import org.aoju.bus.forest.Complex;

import java.util.Collection;

/**
 * ALL逻辑混合规则
 * 只要有一个规则不满足就立刻返回,
 * 如果没有规则的时候则认为所有规则都满足
 *
 * @author Kimi Liu
 * @version 5.9.9
 * @since JDK 1.8+
 */
public class AllComplex<E> extends MixComplex<E> implements Complex<E> {

    public AllComplex() {
        super(null);
    }

    public AllComplex(Collection<? extends Complex<? extends E>> filters) {
        super(filters);
    }

    @Override
    public AllComplex<E> mix(Complex<? extends E> filter) {
        add(filter);
        return this;
    }

    @Override
    public boolean on(E entry) {
        Complex[] filters = this.filters.toArray(new Complex[0]);
        for (Complex filter : filters) {
            if (!filter.on(entry)) {
                return false;
            }
        }
        return true;
    }

}
