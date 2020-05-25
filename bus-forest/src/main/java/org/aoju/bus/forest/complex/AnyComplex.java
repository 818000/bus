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
 * ANY逻辑混合规则,即任意一个规则满足时就满足,
 * 当没有规则的时候,就是不满足
 *
 * @author Kimi Liu
 * @version 5.9.3
 * @since JDK 1.8+
 */
public class AnyComplex<E> extends MixComplex<E> implements Complex<E> {

    public AnyComplex() {
        super(null);
    }

    public AnyComplex(Collection<? extends Complex<? extends E>> filters) {
        super(filters);
    }

    @Override
    public AnyComplex<E> mix(Complex<? extends E> filter) {
        add(filter);
        return this;
    }

    @Override
    public boolean on(E entry) {
        Complex[] filters = this.filters.toArray(new Complex[0]);
        for (Complex filter : filters) {
            if (filter.on(entry)) {
                return true;
            }
        }
        return false;
    }

}
