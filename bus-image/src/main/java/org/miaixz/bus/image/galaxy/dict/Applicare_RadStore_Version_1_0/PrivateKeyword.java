/*
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
 ~                                                                               ~
 ~ The MIT License (MIT)                                                         ~
 ~                                                                               ~
 ~ Copyright (c) 2015-2025 miaixz.org and other contributors.                    ~
 ~                                                                               ~
 ~ Permission is hereby granted, free of charge, to any person obtaining a copy  ~
 ~ of this software and associated documentation files (the "Software"), to deal ~
 ~ in the Software without restriction, including without limitation the rights  ~
 ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     ~
 ~ copies of the Software, and to permit persons to whom the Software is         ~
 ~ furnished to do so, subject to the following conditions:                      ~
 ~                                                                               ~
 ~ The above copyright notice and this permission notice shall be included in    ~
 ~ all copies or substantial portions of the Software.                           ~
 ~                                                                               ~
 ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    ~
 ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      ~
 ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   ~
 ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        ~
 ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, ~
 ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     ~
 ~ THE SOFTWARE.                                                                 ~
 ~                                                                               ~
 ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~ ~
*/
package org.miaixz.bus.image.galaxy.dict.Applicare_RadStore_Version_1_0;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateKeyword {

    public static final String PrivateCreator = "";

    public static String valueOf(int tag) {

        switch (tag & 0xFFFF00FF) {
            case PrivateTag._3113_xx01_:
                return "_3113_xx01_";

            case PrivateTag.Id1:
                return "Id1";

            case PrivateTag.Id2:
                return "Id2";

            case PrivateTag.Id3:
                return "Id3";

            case PrivateTag._3113_xx11_:
                return "_3113_xx11_";

            case PrivateTag.InstanceState:
                return "InstanceState";

            case PrivateTag.DateLastModified:
                return "DateLastModified";

            case PrivateTag.DateLastAccessed:
                return "DateLastAccessed";

            case PrivateTag._3113_xx15_:
                return "_3113_xx15_";

            case PrivateTag.InstanceSizeInBytes:
                return "InstanceSizeInBytes";

            case PrivateTag.LibraryId:
                return "LibraryId";

            case PrivateTag.Pathnames:
                return "Pathnames";

            case PrivateTag.DriverPath:
                return "DriverPath";

            case PrivateTag.Source:
                return "Source";

            case PrivateTag.Destination:
                return "Destination";

            case PrivateTag.MediumId:
                return "MediumId";

            case PrivateTag.ArchiveId:
                return "ArchiveId";

            case PrivateTag.InstanceOrigin:
                return "InstanceOrigin";

            case PrivateTag.InstanceVersion:
                return "InstanceVersion";

            case PrivateTag._3113_xx22_:
                return "_3113_xx22_";

            case PrivateTag.InstanceFileLocation:
                return "InstanceFileLocation";

            case PrivateTag._3113_xx31_:
                return "_3113_xx31_";

            case PrivateTag._3113_xx32_:
                return "_3113_xx32_";

            case PrivateTag._3113_xx33_:
                return "_3113_xx33_";

            case PrivateTag.ImageMediumLocation:
                return "ImageMediumLocation";

            case PrivateTag.ImageMediumLabel:
                return "ImageMediumLabel";

            case PrivateTag.ImageMediumState:
                return "ImageMediumState";

            case PrivateTag.SeriesMediumLocation:
                return "SeriesMediumLocation";

            case PrivateTag.SeriesMediumLabel:
                return "SeriesMediumLabel";

            case PrivateTag.SeriesMediumState:
                return "SeriesMediumState";

            case PrivateTag.StudyMediumLocation:
                return "StudyMediumLocation";

            case PrivateTag.StudyMediumLabel:
                return "StudyMediumLabel";

            case PrivateTag.StudyMediumState:
                return "StudyMediumState";

            case PrivateTag.StudyState:
                return "StudyState";

            case PrivateTag.SeriesState:
                return "SeriesState";

            case PrivateTag.ImageStateText:
                return "ImageStateText";

            case PrivateTag.SeriesStateText:
                return "SeriesStateText";

            case PrivateTag.StudyStateText:
                return "StudyStateText";

            case PrivateTag.Expiration:
                return "Expiration";

            case PrivateTag.DeletedTags:
                return "DeletedTags";
        }
        return "";
    }

}
