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
package org.aoju.bus.cache.serialize;

import org.aoju.bus.logger.Logger;

/**
 * @author Kimi Liu
 * @version 5.5.8
 * @since JDK 1.8+
 */
public abstract class AbstractSerializer implements BaseSerializer {

    protected abstract byte[] doSerialize(Object obj) throws Throwable;

    protected abstract Object doDeserialize(byte[] bytes) throws Throwable;

    @Override
    public <T> byte[] serialize(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return doSerialize(obj);
        } catch (Throwable t) {
            Logger.error("{} serialize error.", this.getClass().getName(), t);
            return null;
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return (T) doDeserialize(bytes);
        } catch (Throwable t) {
            Logger.error("{} deserialize error.", this.getClass().getName(), t);
            return null;
        }
    }

}
