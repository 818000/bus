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
package org.aoju.bus.image.galaxy.data;

import org.aoju.bus.image.UID;
import org.aoju.bus.image.galaxy.Property;

import java.util.Map;

/**
 * @author Kimi Liu
 * @version 5.9.2
 * @since JDK 1.8+
 */
public class RemapUIDAttributesCoercion implements AttributesCoercion {

    private final Map<String, String> uidMap;
    private final AttributesCoercion next;

    public RemapUIDAttributesCoercion(Map<String, String> uidMap, AttributesCoercion next) {
        this.uidMap = uidMap;
        this.next = next;
    }

    @Override
    public String remapUID(String uid) {
        String remappedUID = uidMap != null ? Property.maskNull(uidMap.get(uid), uid) : uid;
        return next != null ? next.remapUID(remappedUID) : remappedUID;
    }

    @Override
    public void coerce(Attributes attrs, Attributes modified) {
        if (uidMap != null && !uidMap.isEmpty())
            UID.remapUIDs(attrs, uidMap);
        if (next != null)
            next.coerce(attrs, modified);
    }

}
