/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2021 aoju.org and other contributors.                      *
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
package org.aoju.bus.extra.pinyin;

import org.aoju.bus.core.instance.Instances;
import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.core.toolkit.ClassKit;
import org.aoju.bus.core.toolkit.StringKit;
import org.aoju.bus.logger.Logger;

/**
 * 用于根据用户引入的拼音库jar
 * 自动创建对应的拼音引擎对象
 *
 * @author Kimi Liu
 * @version 6.2.2
 * @since JDK 1.8+
 */
public class PinyinFactory {

    /**
     * 获得单例的 {@link PinyinProvider}
     *
     * @return 单例的 {@link PinyinProvider}
     */
    public static PinyinProvider get() {
        return Instances.singletion(PinyinFactory.class).create();
    }

    /**
     * 根据用户引入的拼音引擎jar，自动创建对应的拼音引擎对象
     * 推荐创建的引擎单例使用，此方法每次调用会返回新的引擎
     *
     * @return {@link PinyinProvider}
     */
    public static PinyinProvider create() {
        final PinyinProvider engine = ClassKit.loadFirstAvailable(PinyinProvider.class);
        if (null == engine) {
            throw new InstrumentException("No pinyin jar found ! Please add one of it to your project !");
        }
        Logger.debug("Use [{}] provider as default.", StringKit.removeSuffix(engine.getClass().getSimpleName(), "Provider"));
        return engine;
    }

}
