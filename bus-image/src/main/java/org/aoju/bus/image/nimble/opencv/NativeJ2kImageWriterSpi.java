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
package org.aoju.bus.image.nimble.opencv;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.util.Locale;

/**
 * @author Kimi Liu
 * @version 6.1.2
 * @since JDK 1.8+
 */
public class NativeJ2kImageWriterSpi extends ImageWriterSpi {

    public NativeJ2kImageWriterSpi() {
        this(NativeJLSImageWriter.class);
    }

    public NativeJ2kImageWriterSpi(Class<? extends NativeJLSImageWriter> writer) {
        super("Bus Team", "1.5", NativeJ2kImageReaderSpi.NAMES, NativeJ2kImageReaderSpi.SUFFIXES,
                NativeJ2kImageReaderSpi.MIMES, writer.getName(), new Class[]{ImageOutputStream.class},
                new String[]{NativeJ2kImageReaderSpi.class.getName()}, false, null, null, null, null, false, null, null,
                null, null);
    }

    @Override
    public boolean canEncodeImage(ImageTypeSpecifier type) {
        return NativeJPEGImageWriterSpi.checkCommonJpgRequirement(type);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Natively-accelerated JPEG2000 Image Writer (OpenJPEG based)";
    }

    @Override
    public ImageWriter createWriterInstance(Object extension) {
        return new NativeJ2kImageWriter(this);
    }

}
