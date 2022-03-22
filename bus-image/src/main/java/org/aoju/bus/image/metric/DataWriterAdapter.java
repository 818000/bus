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
package org.aoju.bus.image.metric;

import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.io.ImageOutputStream;

import java.io.IOException;

/**
 * @author Kimi Liu
 * @version 6.3.5
 * @since Java 17+
 */
public class DataWriterAdapter implements DataWriter {

    private final Attributes data;

    public DataWriterAdapter(Attributes data) {
        if (null == data)
            throw new NullPointerException();
        this.data = data;
    }

    public static DataWriterAdapter forAttributes(Attributes data) {
        return null != data ? new DataWriterAdapter(data) : null;
    }

    @Override
    public void writeTo(PDVOutputStream out, String tsuid) throws IOException {
        ImageOutputStream dos = new ImageOutputStream(out, tsuid);
        dos.writeDataset(null, data);
        dos.finish();
    }

    public final Attributes getDataset() {
        return data;
    }

}
