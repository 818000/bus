/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org OSHI and other contributors.                 *
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
package org.aoju.bus.health.builtin.hardware;

import org.aoju.bus.core.annotation.Immutable;

/**
 * 抽象声卡
 *
 * @author Kimi Liu
 * @version 6.0.2
 * @since JDK 1.8+
 */
@Immutable
public abstract class AbstractSoundCard implements SoundCard {

    private String kernelVersion;
    private String name;
    private String codec;

    /**
     * 抽象声卡构造函数
     *
     * @param kernelVersion 版本
     * @param name          名称
     * @param codec         编解码器
     */
    public AbstractSoundCard(String kernelVersion, String name, String codec) {
        this.kernelVersion = kernelVersion;
        this.name = name;
        this.codec = codec;
    }

    @Override
    public String getDriverVersion() {
        return this.kernelVersion;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCodec() {
        return this.codec;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SoundCard@");
        builder.append(Integer.toHexString(hashCode()));
        builder.append(" [name=");
        builder.append(this.name);
        builder.append(", kernelVersion=");
        builder.append(this.kernelVersion);
        builder.append(", codec=");
        builder.append(this.codec);
        builder.append(']');
        return builder.toString();
    }

}
