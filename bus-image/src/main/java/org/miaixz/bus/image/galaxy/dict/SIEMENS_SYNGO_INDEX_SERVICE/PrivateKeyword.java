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
package org.miaixz.bus.image.galaxy.dict.SIEMENS_SYNGO_INDEX_SERVICE;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateKeyword {

    public static final String PrivateCreator = "";

    public static String valueOf(int tag) {

        switch (tag & 0xFFFF00FF) {
            case PrivateTag.ObjectInsertionDate:
                return "ObjectInsertionDate";

            case PrivateTag.SenderSystemDeviceName:
                return "SenderSystemDeviceName";

            case PrivateTag.LastAccessTime:
                return "LastAccessTime";

            case PrivateTag.DeleteProtectedStatus:
                return "DeleteProtectedStatus";

            case PrivateTag.ReceivedfromArchiveStatus:
                return "ReceivedfromArchiveStatus";

            case PrivateTag.ArchiveStatus:
                return "ArchiveStatus";

            case PrivateTag.Location:
                return "Location";

            case PrivateTag.LogicalDeletedStatus:
                return "LogicalDeletedStatus";

            case PrivateTag.InsertTime:
                return "InsertTime";

            case PrivateTag.VisibleInstancesonSeriesLevel:
                return "VisibleInstancesonSeriesLevel";

            case PrivateTag.UnarchivedInstances:
                return "UnarchivedInstances";

            case PrivateTag.VisibleInstancesonStudyLevel:
                return "VisibleInstancesonStudyLevel";

            case PrivateTag.SeriesObjectStates:
                return "SeriesObjectStates";

            case PrivateTag.InstanceObjectStates:
                return "InstanceObjectStates";

            case PrivateTag.HiddenInstance:
                return "HiddenInstance";
        }
        return "";
    }

}
