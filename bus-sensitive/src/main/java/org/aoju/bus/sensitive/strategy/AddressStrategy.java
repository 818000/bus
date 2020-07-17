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
package org.aoju.bus.sensitive.strategy;

import org.aoju.bus.core.toolkit.CollKit;
import org.aoju.bus.core.toolkit.ObjectKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.extra.emoji.EmojiKit;
import org.aoju.bus.sensitive.Context;
import org.aoju.bus.sensitive.annotation.Shield;
import org.aoju.bus.sensitive.provider.AbstractProvider;

/**
 * 收货地址脱敏处理类
 * 地址只显示到地区,不显示详细地址；我们要对个人信息增强保护
 * 例子：北京市海淀区****
 *
 * @author Kimi Liu
 * @version 6.0.3
 * @since JDK 1.8+
 */
public class AddressStrategy extends AbstractProvider {

    @Override
    public String build(Object object, Context context) {
        if (ObjectKit.isEmpty(object)) {
            return null;
        }
        String value = object.toString();
        if (CollKit.isNotEmpty(EmojiKit.extractEmojis(value))) {
            return value;
        }
        final int RIGHT = 10;
        final int LEFT = 6;

        final Shield shield = context.getShield();
        int length = StringKit.length(value);
        if (length > RIGHT + LEFT) {
            return StringKit.rightPad(StringKit.left(value, length - RIGHT), length, shield.shadow());
        }
        if (length <= LEFT) {
            return value;
        } else {
            return value.substring(0, LEFT + 1).concat(StringKit.fill(5, shield.shadow()));
        }
    }

}
