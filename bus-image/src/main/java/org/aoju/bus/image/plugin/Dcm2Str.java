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
package org.aoju.bus.image.plugin;

import org.aoju.bus.core.lang.exception.InstrumentException;
import org.aoju.bus.image.Format;
import org.aoju.bus.image.galaxy.data.Attributes;
import org.aoju.bus.image.galaxy.io.DicomInputStream;
import org.aoju.bus.logger.Logger;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Kimi Liu
 * @author Kimi Liu
 * @version 5.8.8
 * @since JDK 1.8+
 * @since JDK 1.8+
 * @since Jan 2019
 */
public class Dcm2Str extends SimpleFileVisitor<Path> {

    private final Format format;
    private final Attributes cliAttrs;

    public Dcm2Str(Format format, Attributes cliAttrs) {
        this.format = format;
        this.cliAttrs = cliAttrs;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        try (DicomInputStream dis = new DicomInputStream(path.toFile())) {
            Attributes dataset = dis.readDataset(-1, -1);
            dataset.addAll(cliAttrs);
            Logger.error(format.format(dataset));
        } catch (IOException e) {
            Logger.error("Failed to parse DICOM file " + path);
            throw new InstrumentException(e);
        }
        return FileVisitResult.CONTINUE;
    }

}
