/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
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
package org.aoju.bus.image.nimble.reader;

import org.aoju.bus.core.lang.Normal;
import org.aoju.bus.image.galaxy.data.Implementation;

import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 */
public class ImageReaderSpi extends javax.imageio.spi.ImageReaderSpi {

    private static final String vendorName = "org.aoju.bus.image";
    private static final String version = Implementation.getVersionName();
    private static final String[] formatNames = {"rle", "RLE"};
    private static final Class<?>[] inputTypes = {ImageInputStream.class};
    private static final String[] entensions = {Normal.EMPTY};
    private static final String[] mimeType = {Normal.EMPTY};

    public ImageReaderSpi() {
        super(vendorName, version, formatNames,
                entensions,  // suffixes
                mimeType,  // MIMETypes
                ImageReader.class.getName(), inputTypes,
                null,  // writerSpiNames
                false, // supportsStandardStreamMetadataFormat
                null,  // nativeStreamMetadataFormatName
                null,  // nativeStreamMetadataFormatClassName
                null,  // extraStreamMetadataFormatNames
                null,  // extraStreamMetadataFormatClassNames
                false, // supportsStandardImageMetadataFormat
                null,  // nativeImageMetadataFormatName
                null,  // nativeImageMetadataFormatClassName
                null,  // extraImageMetadataFormatNames
                null); // extraImageMetadataFormatClassNames
    }

    @Override
    public String getDescription(Locale locale) {
        return "RLE Image Reader";
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        return false;
    }

    @Override
    public javax.imageio.ImageReader createReaderInstance(Object extension)
            throws IOException {
        return new ImageReader(this);
    }
}
