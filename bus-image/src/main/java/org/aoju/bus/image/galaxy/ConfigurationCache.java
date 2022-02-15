/*********************************************************************************
 *                                                                               *
 * The MIT License (MIT)                                                         *
 *                                                                               *
 * Copyright (c) 2015-2022 aoju.org and other contributors.                      *
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
package org.aoju.bus.image.galaxy;

import org.aoju.bus.core.lang.exception.InstrumentException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kimi Liu
 * @version 6.3.5
 * @since JDK 1.8+
 */
public abstract class ConfigurationCache<C, T> {

    private final Map<String, CacheEntry<T>> cache = new HashMap<>();
    private final C conf;
    private long staleTimeout;

    public ConfigurationCache(C conf) {
        if (null == conf)
            throw new NullPointerException();
        this.conf = conf;
    }

    public int getStaleTimeout() {
        return (int) (staleTimeout / 1000);
    }

    public void setStaleTimeout(int staleTimeout) {
        this.staleTimeout = staleTimeout * 1000L;
    }

    public void clear() {
        cache.clear();
    }

    public T get(String key) throws InstrumentException {
        long now = System.currentTimeMillis();
        CacheEntry<T> entry = cache.get(key);
        if (null == entry
                || (staleTimeout != 0 && now > entry.fetchTime + staleTimeout)) {
            T value = null;
            try {
                value = find(conf, key);
            } catch (InstrumentException e) {
            }
            entry = new CacheEntry<T>(value, now);
            cache.put(key, entry);
        }
        return entry.value;
    }

    protected abstract T find(C conf, String key)
            throws InstrumentException;

    private static final class CacheEntry<T> {
        final T value;
        final long fetchTime;

        CacheEntry(T value, long fetchTime) {
            this.value = value;
            this.fetchTime = fetchTime;
        }
    }

}
