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
package org.miaixz.bus.image.galaxy.dict.SIEMENS_MED_NM;

/**
 * @author Kimi Liu
 * @since Java 17+
 */
public class PrivateKeyword {

    public static final String PrivateCreator = "";

    public static String valueOf(int tag) {

        switch (tag & 0xFFFF00FF) {
            case PrivateTag._0009_xx80_:
                return "_0009_xx80_";

            case PrivateTag._0011_xx10_:
                return "_0011_xx10_";

            case PrivateTag._0017_xx00_:
                return "_0017_xx00_";

            case PrivateTag._0017_xx20_:
                return "_0017_xx20_";

            case PrivateTag._0017_xx70_:
                return "_0017_xx70_";

            case PrivateTag._0017_xx80_:
                return "_0017_xx80_";

            case PrivateTag._0019_xx08_:
                return "_0019_xx08_";

            case PrivateTag.SiemensICONDataType:
                return "SiemensICONDataType";

            case PrivateTag._0019_xx16_:
                return "_0019_xx16_";

            case PrivateTag.NumberOfRepeatsPerPhase:
                return "NumberOfRepeatsPerPhase";

            case PrivateTag.CyclesPerRepeat:
                return "CyclesPerRepeat";

            case PrivateTag.RepeatStartTime:
                return "RepeatStartTime";

            case PrivateTag.RepeatStopTime:
                return "RepeatStopTime";

            case PrivateTag.EffectiveRepeatTime:
                return "EffectiveRepeatTime";

            case PrivateTag.AcquiredCyclesPerRepeat:
                return "AcquiredCyclesPerRepeat";

            case PrivateTag._0019_xx93_:
                return "_0019_xx93_";

            case PrivateTag._0019_xxA1_:
                return "_0019_xxA1_";

            case PrivateTag._0019_xxA3_:
                return "_0019_xxA3_";

            case PrivateTag._0019_xxC3_:
                return "_0019_xxC3_";

            case PrivateTag.ECATMainHeader:
                return "ECATMainHeader";

            case PrivateTag.ECATImageSubheader:
                return "ECATImageSubheader";

            case PrivateTag._0021_xx10_:
                return "_0021_xx10_";

            case PrivateTag.DICOMReaderFlag:
                return "DICOMReaderFlag";

            case PrivateTag.ModalityImageHeaderType:
                return "ModalityImageHeaderType";

            case PrivateTag.ModalityImageHeaderVersion:
                return "ModalityImageHeaderVersion";

            case PrivateTag.ModalityImageHeaderInfo:
                return "ModalityImageHeaderInfo";

            case PrivateTag._0031_xx01_:
                return "_0031_xx01_";

            case PrivateTag._0031_xx0C_:
                return "_0031_xx0C_";

            case PrivateTag._0031_xx0F_:
                return "_0031_xx0F_";

            case PrivateTag._0031_xx10_:
                return "_0031_xx10_";

            case PrivateTag._0031_xx12_:
                return "_0031_xx12_";

            case PrivateTag._0031_xx13_:
                return "_0031_xx13_";

            case PrivateTag._0031_xx14_:
                return "_0031_xx14_";

            case PrivateTag._0031_xx15_:
                return "_0031_xx15_";

            case PrivateTag._0031_xx16_:
                return "_0031_xx16_";

            case PrivateTag._0031_xx17_:
                return "_0031_xx17_";

            case PrivateTag._0031_xx20_:
                return "_0031_xx20_";

            case PrivateTag._0031_xx21_:
                return "_0031_xx21_";

            case PrivateTag.FloodCorrectionMatrixDetector1:
                return "FloodCorrectionMatrixDetector1";

            case PrivateTag.FloodCorrectionMatrixDetector2:
                return "FloodCorrectionMatrixDetector2";

            case PrivateTag.CORDataForDetector1:
                return "CORDataForDetector1";

            case PrivateTag.CORDataForDetector2:
                return "CORDataForDetector2";

            case PrivateTag.MHRDataForDetector1:
                return "MHRDataForDetector1";

            case PrivateTag.MHRDataForDetector2:
                return "MHRDataForDetector2";

            case PrivateTag.NCODataForDetector1:
                return "NCODataForDetector1";

            case PrivateTag.NCODataForDetector2:
                return "NCODataForDetector2";

            case PrivateTag._0033_xx1A_:
                return "_0033_xx1A_";

            case PrivateTag.BedCorrectionAngle:
                return "BedCorrectionAngle";

            case PrivateTag.GantryCorrectionAngle:
                return "GantryCorrectionAngle";

            case PrivateTag.BedUDCorrectionData:
                return "BedUDCorrectionData";

            case PrivateTag.GantryLRCorrectionData:
                return "GantryLRCorrectionData";

            case PrivateTag.BackProjectionCorrectionAngleHead1:
                return "BackProjectionCorrectionAngleHead1";

            case PrivateTag.BackProjectionCorrectionAngleHead2:
                return "BackProjectionCorrectionAngleHead2";

            case PrivateTag.MHRCalibrations:
                return "MHRCalibrations";

            case PrivateTag.CrystalThickness:
                return "CrystalThickness";

            case PrivateTag.PresetNameUsedForAcquisition:
                return "PresetNameUsedForAcquisition";

            case PrivateTag.CameraConfigAngle:
                return "CameraConfigAngle";

            case PrivateTag.CrystalType:
                return "CrystalType";

            case PrivateTag.CoinGantryStep:
                return "CoinGantryStep";

            case PrivateTag.WholebodyBedStep:
                return "WholebodyBedStep";

            case PrivateTag.WeightFactorTableForCoincidenceAcquisitions:
                return "WeightFactorTableForCoincidenceAcquisitions";

            case PrivateTag.CoincidenceWeightFactorTable:
                return "CoincidenceWeightFactorTable";

            case PrivateTag.StarburstFlagsAtImageAcqTime:
                return "StarburstFlagsAtImageAcqTime";

            case PrivateTag.PixelScaleFactor:
                return "PixelScaleFactor";

            case PrivateTag.SpecializedTomoType:
                return "SpecializedTomoType";

            case PrivateTag.EnergyWindowType:
                return "EnergyWindowType";

            case PrivateTag.StartandEndRowIlluminatedByWindPosition:
                return "StartandEndRowIlluminatedByWindPosition";

            case PrivateTag.BlankScanImageForProfile:
                return "BlankScanImageForProfile";

            case PrivateTag.RepeatNumberOfTheOriginalDynamicSPECT:
                return "RepeatNumberOfTheOriginalDynamicSPECT";

            case PrivateTag.PhaseNumberOfTheOriginalDynamicSPECT:
                return "PhaseNumberOfTheOriginalDynamicSPECT";

            case PrivateTag.SiemensProfile2ImageSubtype:
                return "SiemensProfile2ImageSubtype";

            case PrivateTag.ToshibaCBFActivityResults:
                return "ToshibaCBFActivityResults";

            case PrivateTag.RelatedCTSeriesInstanceUID:
                return "RelatedCTSeriesInstanceUID";

            case PrivateTag.WholeBodyTomoPositionIndex:
                return "WholeBodyTomoPositionIndex";

            case PrivateTag.WholeBodyTomoNumberOfPositions:
                return "WholeBodyTomoNumberOfPositions";

            case PrivateTag.HorizontalTablePositionOfCTScan:
                return "HorizontalTablePositionOfCTScan";

            case PrivateTag.EffectiveEnergyOfCTScan:
                return "EffectiveEnergyOfCTScan";

            case PrivateTag.LongLinearDriveInformationForDetector1:
                return "LongLinearDriveInformationForDetector1";

            case PrivateTag.LongLinearDriveInformationForDetector2:
                return "LongLinearDriveInformationForDetector2";

            case PrivateTag.TrunnionInformationForDetector1:
                return "TrunnionInformationForDetector1";

            case PrivateTag.TrunnionInformationForDetector2:
                return "TrunnionInformationForDetector2";

            case PrivateTag.BroadBeamFactor:
                return "BroadBeamFactor";

            case PrivateTag.OriginalWholebodyPosition:
                return "OriginalWholebodyPosition";

            case PrivateTag.WholebodyScanRange:
                return "WholebodyScanRange";

            case PrivateTag.EffectiveEmissionEnergy:
                return "EffectiveEmissionEnergy";

            case PrivateTag.GatedFrameDuration:
                return "GatedFrameDuration";

            case PrivateTag._0041_xx30_:
                return "_0041_xx30_";

            case PrivateTag._0041_xx32_:
                return "_0041_xx32_";

            case PrivateTag.DetectorViewAngle:
                return "DetectorViewAngle";

            case PrivateTag.TransformationMatrix:
                return "TransformationMatrix";

            case PrivateTag.ViewDependentYShiftMHRForDetector1:
                return "ViewDependentYShiftMHRForDetector1";

            case PrivateTag.ViewDependentYShiftMHRForDetector2:
                return "ViewDependentYShiftMHRForDetector2";

            case PrivateTag.PlanarProcessingString:
                return "PlanarProcessingString";

            case PrivateTag.PromptWindowWidth:
                return "PromptWindowWidth";

            case PrivateTag.RandomWindowWidth:
                return "RandomWindowWidth";

            case PrivateTag._0055_xx20_:
                return "_0055_xx20_";

            case PrivateTag._0055_xx22_:
                return "_0055_xx22_";

            case PrivateTag._0055_xx24_:
                return "_0055_xx24_";

            case PrivateTag._0055_xx30_:
                return "_0055_xx30_";

            case PrivateTag._0055_xx32_:
                return "_0055_xx32_";

            case PrivateTag._0055_xx34_:
                return "_0055_xx34_";

            case PrivateTag._0055_xx40_:
                return "_0055_xx40_";

            case PrivateTag._0055_xx42_:
                return "_0055_xx42_";

            case PrivateTag._0055_xx44_:
                return "_0055_xx44_";

            case PrivateTag._0055_xx4C_:
                return "_0055_xx4C_";

            case PrivateTag._0055_xx4D_:
                return "_0055_xx4D_";

            case PrivateTag._0055_xx51_:
                return "_0055_xx51_";

            case PrivateTag._0055_xx52_:
                return "_0055_xx52_";

            case PrivateTag._0055_xx53_:
                return "_0055_xx53_";

            case PrivateTag._0055_xx55_:
                return "_0055_xx55_";

            case PrivateTag._0055_xx5C_:
                return "_0055_xx5C_";

            case PrivateTag._0055_xx6D_:
                return "_0055_xx6D_";

            case PrivateTag.CollimatorThickness:
                return "CollimatorThickness";

            case PrivateTag.CollimatorAngularResolution:
                return "CollimatorAngularResolution";

            case PrivateTag._0055_xxA8_:
                return "_0055_xxA8_";

            case PrivateTag.UsefulFieldOfView:
                return "UsefulFieldOfView";

            case PrivateTag._0055_xxC2_:
                return "_0055_xxC2_";

            case PrivateTag._0055_xxC3_:
                return "_0055_xxC3_";

            case PrivateTag._0055_xxC4_:
                return "_0055_xxC4_";

            case PrivateTag._0055_xxD0_:
                return "_0055_xxD0_";

            case PrivateTag.SyngoMIDICOMOriginalImageType:
                return "SyngoMIDICOMOriginalImageType";

            case PrivateTag.DoseCalibrationFactor:
                return "DoseCalibrationFactor";

            case PrivateTag.Units:
                return "Units";

            case PrivateTag.DecayCorrection:
                return "DecayCorrection";

            case PrivateTag.RadionuclideHalfLife:
                return "RadionuclideHalfLife";

            case PrivateTag.RescaleIntercept:
                return "RescaleIntercept";

            case PrivateTag.RescaleSlope:
                return "RescaleSlope";

            case PrivateTag.FrameReferenceTime:
                return "FrameReferenceTime";

            case PrivateTag.NumberofRadiopharmaceuticalInformationSequence:
                return "NumberofRadiopharmaceuticalInformationSequence";

            case PrivateTag.DecayFactor:
                return "DecayFactor";

            case PrivateTag.CountsSource:
                return "CountsSource";

            case PrivateTag.RadionuclidePositronFraction:
                return "RadionuclidePositronFraction";

            case PrivateTag.TriggerTimeOfCTSlice:
                return "TriggerTimeOfCTSlice";

            case PrivateTag.XPrincipalRayOffset:
                return "XPrincipalRayOffset";

            case PrivateTag.YPrincipalRayOffset:
                return "YPrincipalRayOffset";

            case PrivateTag.XPrincipalRayAngle:
                return "XPrincipalRayAngle";

            case PrivateTag.YPrincipalRayAngle:
                return "YPrincipalRayAngle";

            case PrivateTag.XShortFocalLength:
                return "XShortFocalLength";

            case PrivateTag.YShortFocalLength:
                return "YShortFocalLength";

            case PrivateTag.XLongFocalLength:
                return "XLongFocalLength";

            case PrivateTag.YLongFocalLength:
                return "YLongFocalLength";

            case PrivateTag.XFocalScaling:
                return "XFocalScaling";

            case PrivateTag.YFocalScaling:
                return "YFocalScaling";

            case PrivateTag.XMotionCorrectionShift:
                return "XMotionCorrectionShift";

            case PrivateTag.YMotionCorrectionShift:
                return "YMotionCorrectionShift";

            case PrivateTag.XHeartCenter:
                return "XHeartCenter";

            case PrivateTag.YHeartCenter:
                return "YHeartCenter";

            case PrivateTag.ZHeartCenter:
                return "ZHeartCenter";

            case PrivateTag.ImagePixelContentType:
                return "ImagePixelContentType";

            case PrivateTag.AutoSaveCorrectedSeries:
                return "AutoSaveCorrectedSeries";

            case PrivateTag.DistortedSeriesInstanceUID:
                return "DistortedSeriesInstanceUID";

            case PrivateTag.ReconRange:
                return "ReconRange";

            case PrivateTag.ReconOrientation:
                return "ReconOrientation";

            case PrivateTag.ReconSelectedAngularRange:
                return "ReconSelectedAngularRange";

            case PrivateTag.ReconTransverseAngle:
                return "ReconTransverseAngle";

            case PrivateTag.ReconSagittalAngle:
                return "ReconSagittalAngle";

            case PrivateTag.ReconXMaskSize:
                return "ReconXMaskSize";

            case PrivateTag.ReconYMaskSize:
                return "ReconYMaskSize";

            case PrivateTag.ReconXImageCenter:
                return "ReconXImageCenter";

            case PrivateTag.ReconYImageCenter:
                return "ReconYImageCenter";

            case PrivateTag.ReconZImageCenter:
                return "ReconZImageCenter";

            case PrivateTag.ReconXZoom:
                return "ReconXZoom";

            case PrivateTag.ReconYZoom:
                return "ReconYZoom";

            case PrivateTag.ReconThreshold:
                return "ReconThreshold";

            case PrivateTag.ReconOutputPixelSize:
                return "ReconOutputPixelSize";

            case PrivateTag.ScatterEstimationMethod:
                return "ScatterEstimationMethod";

            case PrivateTag.ScatterEstimationMethodMode:
                return "ScatterEstimationMethodMode";

            case PrivateTag.ScatterEstimationLowerWindowWeights:
                return "ScatterEstimationLowerWindowWeights";

            case PrivateTag.ScatterEstimationUpperWindowWeights:
                return "ScatterEstimationUpperWindowWeights";

            case PrivateTag.ScatterEstimationWindowMode:
                return "ScatterEstimationWindowMode";

            case PrivateTag.ScatterEstimationFilter:
                return "ScatterEstimationFilter";

            case PrivateTag.ReconRawTomoInputUID:
                return "ReconRawTomoInputUID";

            case PrivateTag.ReconCTInputUID:
                return "ReconCTInputUID";

            case PrivateTag.ReconZMaskSize:
                return "ReconZMaskSize";

            case PrivateTag.ReconXMaskCenter:
                return "ReconXMaskCenter";

            case PrivateTag.ReconYMaskCenter:
                return "ReconYMaskCenter";

            case PrivateTag.ReconZMaskCenter:
                return "ReconZMaskCenter";

            case PrivateTag.RawTomoSeriesUID:
                return "RawTomoSeriesUID";

            case PrivateTag.LowResCTSeriesUID:
                return "LowResCTSeriesUID";

            case PrivateTag.HighResCTSeriesUID:
                return "HighResCTSeriesUID";

            case PrivateTag.MinimumPixelInFrame:
                return "MinimumPixelInFrame";

            case PrivateTag.MaximumPixelInFrame:
                return "MaximumPixelInFrame";

            case PrivateTag._7FE3_xx16_:
                return "_7FE3_xx16_";

            case PrivateTag._7FE3_xx1B_:
                return "_7FE3_xx1B_";

            case PrivateTag._7FE3_xx1C_:
                return "_7FE3_xx1C_";

            case PrivateTag._7FE3_xx1E_:
                return "_7FE3_xx1E_";

            case PrivateTag._7FE3_xx26_:
                return "_7FE3_xx26_";

            case PrivateTag._7FE3_xx27_:
                return "_7FE3_xx27_";

            case PrivateTag._7FE3_xx28_:
                return "_7FE3_xx28_";

            case PrivateTag.NumberOfRWavesInFrame:
                return "NumberOfRWavesInFrame";
        }
        return "";
    }

}
