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
package org.miaixz.bus.image.galaxy.dict.SIEMENS_MR_HEADER;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateKeyword {

    public static final String PrivateCreator = "";

    public static String valueOf(int tag) {

        switch (tag & 0xFFFF00FF) {
            case PrivateTag._0019_xx08_:
                return "_0019_xx08_";

            case PrivateTag._0019_xx09_:
                return "_0019_xx09_";

            case PrivateTag.NumberOfImagesInMosaic:
                return "NumberOfImagesInMosaic";

            case PrivateTag.SliceMeasurementDuration:
                return "SliceMeasurementDuration";

            case PrivateTag.BValue:
                return "BValue";

            case PrivateTag.DiffusionDirectionality:
                return "DiffusionDirectionality";

            case PrivateTag.DiffusionGradientDirection:
                return "DiffusionGradientDirection";

            case PrivateTag.GradientMode:
                return "GradientMode";

            case PrivateTag.FlowCompensation:
                return "FlowCompensation";

            case PrivateTag.TablePositionOrigin:
                return "TablePositionOrigin";

            case PrivateTag.ImaAbsTablePosition:
                return "ImaAbsTablePosition";

            case PrivateTag.ImaRelTablePosition:
                return "ImaRelTablePosition";

            case PrivateTag.SlicePosition_PCS:
                return "SlicePosition_PCS";

            case PrivateTag.TimeAfterStart:
                return "TimeAfterStart";

            case PrivateTag.SliceResolution:
                return "SliceResolution";

            case PrivateTag.RealDwellTime:
                return "RealDwellTime";

            case PrivateTag._0019_xx23_:
                return "_0019_xx23_";

            case PrivateTag._0019_xx25_:
                return "_0019_xx25_";

            case PrivateTag._0019_xx26_:
                return "_0019_xx26_";

            case PrivateTag.BMatrix:
                return "BMatrix";

            case PrivateTag.BandwidthPerPixelPhaseEncode:
                return "BandwidthPerPixelPhaseEncode";

            case PrivateTag.MosaicRefAcqTimes:
                return "MosaicRefAcqTimes";

            case PrivateTag.CSAImageHeaderType:
                return "CSAImageHeaderType";

            case PrivateTag.CSAImageHeaderVersion:
                return "CSAImageHeaderVersion";

            case PrivateTag._0051_xx0A_:
                return "_0051_xx0A_";

            case PrivateTag.AcquisitionMatrixText:
                return "AcquisitionMatrixText";

            case PrivateTag._0051_xx0C_:
                return "_0051_xx0C_";

            case PrivateTag._0051_xx0D_:
                return "_0051_xx0D_";

            case PrivateTag._0051_xx0E_:
                return "_0051_xx0E_";

            case PrivateTag.CoilString:
                return "CoilString";

            case PrivateTag._0051_xx11_:
                return "_0051_xx11_";

            case PrivateTag._0051_xx12_:
                return "_0051_xx12_";

            case PrivateTag.PositivePCSDirections:
                return "PositivePCSDirections";

            case PrivateTag._0051_xx15_:
                return "_0051_xx15_";

            case PrivateTag._0051_xx16_:
                return "_0051_xx16_";

            case PrivateTag._0051_xx17_:
                return "_0051_xx17_";

            case PrivateTag._0051_xx18_:
                return "_0051_xx18_";

            case PrivateTag._0051_xx19_:
                return "_0051_xx19_";
        }
        return "";
    }

}
