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
package org.aoju.bus.image.galaxy.io;

/**
 * @author Kimi Liu
 * @version 6.1.9
 * @since JDK 1.8+
 */
public class ImageEncodingOptions {

    public static final ImageEncodingOptions DEFAULT =
            new ImageEncodingOptions(false, true, false, true, false);

    public final boolean groupLength;
    public final boolean undefSequenceLength;
    public final boolean undefEmptySequenceLength;
    public final boolean undefItemLength;
    public final boolean undefEmptyItemLength;

    public ImageEncodingOptions(boolean groupLength, boolean undefSeqLength,
                                boolean undefEmptySeqLength, boolean undefItemLength,
                                boolean undefEmptyItemLength) {
        if (undefEmptySeqLength && !undefSeqLength)
            throw new IllegalArgumentException(
                    "undefEmptySeqLength && !undefSeqLength");
        if (undefEmptyItemLength && !undefItemLength)
            throw new IllegalArgumentException(
                    "undefEmptyItemLength && !undefItemLength");
        this.groupLength = groupLength;
        this.undefSequenceLength = undefSeqLength;
        this.undefEmptySequenceLength = undefEmptySeqLength;
        this.undefItemLength = undefItemLength;
        this.undefEmptyItemLength = undefEmptyItemLength;
    }

}
