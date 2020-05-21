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
package org.aoju.bus.tracer.transport.jaxb;

import org.aoju.bus.tracer.consts.TraceConsts;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * @author Kimi Liu
 * @version 5.9.2
 * @since JDK 1.8+
 */
@XmlRootElement(name = TraceConsts.TPIC_HEADER)
@XmlAccessorType(XmlAccessType.NONE)
public final class TpicMap {

    @XmlElement(name = "entry")
    public final List<Entry> entries;

    public TpicMap(List<Entry> entries) {
        this.entries = entries;
    }

    private TpicMap() {
        entries = null;
    }

    public static TpicMap wrap(Map<String, String> map) {
        final List<Entry> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            values.add(new Entry(entry.getKey(), entry.getValue()));
        }
        return new TpicMap(values);
    }

    public Map<String, String> unwrapValues() {
        if (entries == null) {
            return Collections.emptyMap();
        }
        final Map<String, String> map = new HashMap<>();
        for (Entry value : this.entries) {
            map.put(value.key, value.value);
        }
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TpicMap tpicMap = (TpicMap) o;

        if (entries != null ? !entries.equals(tpicMap.entries) : tpicMap.entries != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return entries != null ? entries.hashCode() : 0;
    }

    public static final class Entry {

        @XmlAttribute(name = "key", required = true)
        public final String key;
        @XmlValue
        public final String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        protected Entry() {
            this.key = null;
            this.value = null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Entry entry = (Entry) o;

            if (key != null ? !key.equals(entry.key) : entry.key != null) return false;
            if (value != null ? !value.equals(entry.value) : entry.value != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

    }

}
