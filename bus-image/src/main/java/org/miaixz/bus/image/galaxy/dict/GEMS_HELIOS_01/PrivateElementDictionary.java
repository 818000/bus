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
package org.miaixz.bus.image.galaxy.dict.GEMS_HELIOS_01;

import org.miaixz.bus.image.galaxy.data.ElementDictionary;
import org.miaixz.bus.image.galaxy.data.VR;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateElementDictionary extends ElementDictionary {

    public static final String PrivateCreator = "";

    public PrivateElementDictionary() {
        super("", PrivateTag.class);
    }

    @Override
    public String keywordOf(int tag) {
        return PrivateKeyword.valueOf(tag);
    }

    @Override
    public VR vrOf(int tag) {

        switch (tag & 0xFFFF00FF) {

            case PrivateTag.TableDirection:
            case PrivateTag.CardiacReconAlgorithm:
            case PrivateTag.AvgHeartRateForImage:
            case PrivateTag.PctRpeakDelay:
            case PrivateTag._0045_xx34_:
            case PrivateTag.EkgFullMaStartPhase:
            case PrivateTag.EkgFullMaEndPhase:
            case PrivateTag.EkgModulationMaxMa:
            case PrivateTag.EkgModulationMinMa:
            case PrivateTag.WideConeMasking:
            case PrivateTag.InternalReconAlgorithm:
                return VR.CS;

            case PrivateTag.MacroWidthAtISOCenter:
            case PrivateTag.ZSmoothingFactor:
            case PrivateTag.MinimumDASValue:
            case PrivateTag.MaximumOffsetValue:
            case PrivateTag.MeanZError:
            case PrivateTag.ZTrackingError:
            case PrivateTag.TemporalResolution:
            case PrivateTag.PatientCentering:
            case PrivateTag.PatientAttenuation:
            case PrivateTag.WaterEquivalentDiameter:
            case PrivateTag.ProjectionMeasure:
            case PrivateTag.OvalRatio:
            case PrivateTag.EllipseOrientation:
                return VR.FL;

            case PrivateTag.TemporalCenterViewAngle:
            case PrivateTag.ReconCenterViewAngle:
            case PrivateTag.WideConeCornerBlendingRadius:
            case PrivateTag.WideConeCornerBlendingRadiusOffset:
                return VR.FD;

            case PrivateTag.NoiseReductionImageFilterDesc:
                return VR.LO;

            case PrivateTag.NumberOfMacroRowsInDetector:
            case PrivateTag.DASType:
            case PrivateTag.DASGain:
            case PrivateTag.DASTemprature:
            case PrivateTag.ViewWeightingMode:
            case PrivateTag.SigmaRowNumber:
            case PrivateTag.NumberOfViewsShifted:
            case PrivateTag.ZTrackingFlag:
            case PrivateTag.StartView2A:
            case PrivateTag.NumberOfViews2A:
            case PrivateTag.StartView1A:
            case PrivateTag.SigmaMode:
            case PrivateTag.NumberOfViews1A:
            case PrivateTag.StartView2B:
            case PrivateTag.NumberViews2B:
            case PrivateTag.StartView1B:
            case PrivateTag.NumberOfViews1B:
            case PrivateTag.IterboneFlag:
            case PrivateTag.PeristalticFlag:
                return VR.SS;
        }
        return VR.UN;
    }

}
