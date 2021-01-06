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
 *                                                                               *
 ********************************************************************************/
package org.aoju.bus.validate.strategy;

import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.validate.Context;
import org.aoju.bus.validate.annotation.Length;
import org.aoju.bus.validate.validators.Matcher;

import java.util.Collection;
import java.util.Map;

/**
 * 数据长度校验
 *
 * @author Kimi Liu
 * @version 6.1.8
 * @since JDK 1.8+
 */
public class LengthStrategy implements Matcher<Object, Length> {

    @Override
    public boolean on(Object object, Length annotation, Context context) {
        if (ObjectKit.isEmpty(object)) {
            return false;
        }

        int num;

        if (object instanceof String) {
            num = ((String) object).length();
        } else if (object.getClass().isArray()) {
            num = ((Object[]) object).length;
        } else if (object instanceof Collection) {
            num = ((Collection) object).size();
        } else if (object instanceof Map) {
            num = ((Map) object).keySet().size();
        } else {
            throw new IllegalArgumentException("不支持的检查长度的对象类型:" + object.getClass());
        }
        return (annotation.zeroAble() && num == 0) || (num >= annotation.min() && num <= annotation.max());
    }

}
