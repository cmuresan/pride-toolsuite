package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.jaxb.pridexml.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * User: rwang
 * Date: 17-Mar-2010
 * Time: 14:14:12
 */
public class PrideXmlTransformer {

    /**
     * 
     * @param rawSpec
     * @return
     */
    public static Spectrum transformSpectrum(SpectrumType rawSpec) {
        if (rawSpec == null) {
            throw new IllegalArgumentException("SpectrumType cannot be NULL");
        }
        //ToDo: supDesc, supDataArrayBinary are not handled at the moment.
        // scan list - required
        ScanList scanList = transformSpectrumDesc(rawSpec.getSpectrumDesc());
        // precursor list
        SpectrumDescType.PrecursorList rawPrecursorList = rawSpec.getSpectrumDesc().getPrecursorList();
        List<Precursor> precursors = rawPrecursorList == null ? null : transformPrecursorList(rawPrecursorList);
        // binary array - required
        BinaryDataArray mz = transformBinaryDataArray(rawSpec.getMzArrayBinary());
        BinaryDataArray inten = transformBinaryDataArray(rawSpec.getIntenArrayBinary());
        List<BinaryDataArray> dataArr = new ArrayList<BinaryDataArray>();
        dataArr.add(mz);
        dataArr.add(inten);

        // ToDo: arr length ?
       return new Spectrum(rawSpec.getId()+"", null, null,
                                        null, null, -1, null, null, null,
                                        scanList, precursors, null,
                                        dataArr, null);
    }

    public static ScanList transformSpectrumDesc(SpectrumDescType rawSpecDesc) {
        if (rawSpecDesc == null) {
            throw new IllegalArgumentException("SpectrumDescType cannot be NULL");
        }

        // spectrum settings
        List<Scan> scans = null;
        List<CvParam> cvParams = new ArrayList<CvParam>();
        List<UserParam> userParams = new ArrayList<UserParam>();
        SpectrumSettingsType rawSpecSettings = rawSpecDesc.getSpectrumSettings();
        if (rawSpecSettings != null) {
            ParamGroup scanWindow = null;
            SpectrumSettingsType.SpectrumInstrument rawInstrut = rawSpecSettings.getSpectrumInstrument();
            if (rawInstrut != null) {
                // SpectrumInstrument is required to have both cv params and user params
                ParamGroup params = transformParams(rawInstrut);
                cvParams.addAll(params.getCvParams());
                userParams.addAll(params.getUserParams());

                // mz range start and stop are optional
                Float mzRangeStart = rawInstrut.getMzRangeStart();
                Float mzRangeStop = rawInstrut.getMzRangeStop();
                if (mzRangeStart !=  null && mzRangeStop != null) {
                    CvParam start = new CvParam("MS:1000501", "scan window lower limit", "MS",
                                                mzRangeStart+"", "MS:1000040",
                                                "m/z", "MS", -1 ,false);
                    CvParam end = new CvParam("MS:1000500", "scan window upper limit", "MS",
                                              mzRangeStop +"", "MS:1000040",
                                              "m/z", "MS", -1 ,false);
                    List<CvParam> sw = new ArrayList<CvParam>();
                    sw.add(start);
                    sw.add(end);
                    scanWindow = new ParamGroup(sw, null);
                }
            }

            SpectrumSettingsType.AcqSpecification rawAcq = rawSpecSettings.getAcqSpecification();
            if (rawAcq != null) {
                UserParam userParam = new UserParam("spectrum type", rawAcq.getSpectrumType(), null, null, null, -1, false);
                userParams.add(userParam);
                userParam = new UserParam("method of combination", rawAcq.getMethodOfCombination(), null, null, null, -1, false);
                userParams.add(userParam);
                List<SpectrumSettingsType.AcqSpecification.Acquisition> rawAcqs = rawAcq.getAcquisition();
                if (rawAcqs != null) {
                    scans = new ArrayList<Scan>();
                    List<ParamGroup> scanWindowList = null;
                    if (scanWindow != null) {
                        scanWindowList = new ArrayList<ParamGroup>();
                        scanWindowList.add(scanWindow);
                    }

                    for(SpectrumSettingsType.AcqSpecification.Acquisition ra : rawAcqs) {
                        ParamGroup params = transformParams(ra);
                        Scan scan = new Scan(null, null, null, scanWindowList, params);
                        scans.add(scan);
                    }
                }
            }
        }

        // comments
        List<String> rawComments = rawSpecDesc.getComments();
        if (rawComments != null) {
            for(String rawComment : rawComments) {
                UserParam userParam = new UserParam("comments", rawComment, null, null, null, -1 ,false);
                userParams.add(userParam);
            }
        }
        ParamGroup params = new ParamGroup(cvParams, userParams);
        return new ScanList(scans, params);
    }

    public static List<Precursor> transformPrecursorList(SpectrumDescType.PrecursorList rawPrecursors) {
        if (rawPrecursors == null) {
            throw new IllegalArgumentException("SpectrumDescType.PrecursorList cannot be NULL");
        }

        List<Precursor> precursors = new ArrayList<Precursor>();

        for(PrecursorType rawPrecursor : rawPrecursors.getPrecursor()) {
            precursors.add(transformPrecursor(rawPrecursor));
        }

        return precursors;
    }

    public static Precursor transformPrecursor(PrecursorType rawPrecursor) {
        if (rawPrecursor == null) {
            throw new IllegalArgumentException("PrecursorType cannot be NULL");
        }

        // ion selection - required
        ParamType rawIonSelection = rawPrecursor.getIonSelection();
        ParamGroup ionSelection = transformParams(rawIonSelection);
        List<ParamGroup> ionSelections = null;
        if (ionSelection != null) {
            ionSelections = new ArrayList<ParamGroup>();
            ionSelections.add(ionSelection);
        }

        // activation - required
        ParamType rawActivation = rawPrecursor.getActivation();
        ParamGroup activation = transformParams(rawActivation);

        // ToDo: experiment ref to implement

        // spectrum - required
        // Note: although this is required, it could be that the referenced spectrum is not included
        // in the original file.
        SpectrumType rawSpectrum = rawPrecursor.getSpectrum();
        Spectrum spectrum = rawSpectrum == null ? null : transformSpectrum(rawSpectrum);

        return new Precursor(activation, null, null, rawPrecursor.getMsLevel(), ionSelections, null, spectrum);
    }

    public static BinaryDataArray transformBinaryDataArray(PeakListBinaryType rawArr) {
        if (rawArr == null) {
            throw new IllegalArgumentException("PeakListBinaryType cannot be NULL");
        }

        PeakListBinaryType.Data rawData = rawArr.getData();
        byte[] binary = rawData.getValue();

        //check precision
        String precision = rawData.getPrecision();
        BinaryDataUtils.BinaryDataType dataType = "32".equals(precision) ?
                                        BinaryDataUtils.BinaryDataType.FLOAT32BIT : BinaryDataUtils.BinaryDataType.FLOAT64BIT;
        //check endianess
        String endian = rawData.getEndian();
        ByteOrder order = "big".equals(endian) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;

        double[] binaryDoubleArr = BinaryDataUtils.toDoubleArray(binary, dataType, order);
        return new BinaryDataArray(null, binaryDoubleArr, null);
    }


    /**
     * ToDo: there are code dupliation between transformTwoDimIdent and transformGelFreeIdent
     * @param rawIdent
     * @return
     */
    public static TwoDimIdentification transformTwoDimIdent(TwoDimensionalIdentificationType rawIdent) {
        if (rawIdent == null) {
            throw new IllegalArgumentException("TwoDimensionalIdentificationType cannot be NULL");
        }

        // peptides
        List<uk.ac.ebi.pride.data.jaxb.pridexml.Peptide> rawPeptides = rawIdent.getPeptideItem();
        List<Peptide> peptides = null;
        if (rawPeptides != null && !rawPeptides.isEmpty()) {
            peptides = new ArrayList<Peptide>();
            for(uk.ac.ebi.pride.data.jaxb.pridexml.Peptide rawPeptide : rawPeptides) {
                peptides.add(transformPeptide(rawPeptide));
            }
        }
        // spectrum
        SpectrumType rawSpectrum = rawIdent.getSpectrum();
        Spectrum spectrum = rawSpectrum == null ? null : transformSpectrum(rawSpectrum);
        // params
        ParamType rawParams = rawIdent.getAdditional();
        ParamGroup params = rawParams == null ? null : transformParams(rawParams);
        // gel
        SimpleGel rawGel = rawIdent.getGel();
        Gel gel = transformGel(rawGel, rawIdent.getGelLocation(), rawIdent.getMolecularWeight(), rawIdent.getPI());

        Double score = rawIdent.getScore();
        double scoreVal = score == null ? -1 : score;
        Double seqConverage = rawIdent.getSequenceCoverage();
        double seqConverageVal = seqConverage == null ? -1 : seqConverage;
        Double threshold = rawIdent.getThreshold();
        double thresholdVal = threshold == null ? -1 : threshold;

        return new TwoDimIdentification(rawIdent.getAccession(), rawIdent.getAccessionVersion(), peptides,
                                                               scoreVal, rawIdent.getDatabase(), rawIdent.getDatabaseVersion(),
                                                               rawIdent.getSearchEngine(), seqConverageVal, spectrum,
                                                               rawIdent.getSpliceIsoform(), thresholdVal, params, gel);
    }

    public static GelFreeIdentification transformGelFreeIdent(GelFreeIdentificationType rawIdent) {
        if (rawIdent == null) {
            throw new IllegalArgumentException("GelFreeIdentificationType cannot be NULL");
        }

        // peptides
        List<uk.ac.ebi.pride.data.jaxb.pridexml.Peptide> rawPeptides = rawIdent.getPeptideItem();
        List<Peptide> peptides = null;
        if (rawPeptides != null && !rawPeptides.isEmpty()) {
            peptides = new ArrayList<Peptide>();
            for(uk.ac.ebi.pride.data.jaxb.pridexml.Peptide rawPeptide : rawPeptides) {
                peptides.add(transformPeptide(rawPeptide));
            }
        }
        // spectrum
        SpectrumType rawSpectrum = rawIdent.getSpectrum();
        Spectrum spectrum = (rawSpectrum == null ? null : transformSpectrum(rawSpectrum));
        // params
        ParamType rawParams = rawIdent.getAdditional();
        ParamGroup params = (rawParams == null ? null : transformParams(rawParams));

        Double score = rawIdent.getScore();
        double scoreVal = score == null ? -1 : score;
        Double seqConverage = rawIdent.getSequenceCoverage();
        double seqConverageVal = seqConverage == null ? -1 : seqConverage;
        Double threshold = rawIdent.getThreshold();
        double thresholdVal = threshold == null ? -1 : threshold;


        return new GelFreeIdentification(rawIdent.getAccession(), rawIdent.getAccessionVersion(),
                                                                  peptides, scoreVal,
                                                                  rawIdent.getDatabase(), rawIdent.getDatabaseVersion(),
                                                                  rawIdent.getSearchEngine(), seqConverageVal,
                                                                  spectrum, rawIdent.getSpliceIsoform(),
                                                                  thresholdVal, params);
    }

    private static Gel transformGel(SimpleGel rawGel,
                                    Point gelLocation,
                                    Double mw, Double pI) {
        if (rawGel == null && gelLocation == null && mw == null && pI == null) {
            throw new IllegalArgumentException("Gel parameters cannot be NULL");
        }

        ParamType rawParams = rawGel == null ? null : rawGel.getAdditional();
        String gelLink = rawGel == null ? null : rawGel.getGelLink();
        ParamGroup params = (rawParams == null ? null : transformParams(rawParams));

        return new Gel(params, gelLink,
                          gelLocation.getXCoordinate(), gelLocation.getYCoordinate(),
                          mw, pI);
    }

    private static Peptide transformPeptide(uk.ac.ebi.pride.data.jaxb.pridexml.Peptide rawPeptide) {
        if (rawPeptide == null) {
            throw new IllegalArgumentException("Peptide cannot be NULL");
        }

        // spectrum
        SpectrumType rawSpectrum = rawPeptide.getSpectrum();
        Spectrum spectrum = transformSpectrum(rawSpectrum);
        // modifications
        List<uk.ac.ebi.pride.data.jaxb.pridexml.Modification> rawMods = rawPeptide.getModificationItem();
        List<Modification> modifications = null;
        if (rawMods != null && !rawMods.isEmpty()) {
            modifications = new ArrayList<Modification>();
            for(uk.ac.ebi.pride.data.jaxb.pridexml.Modification rawMod : rawMods) {
                modifications.add(transformModification(rawMod));
            }
        }
        // fragmentIons
        List<FragmentIonType> rawFragIons = rawPeptide.getFragmentIon();
        List<ParamGroup> fragmentIons = null;
        if (rawFragIons !=  null && !rawFragIons.isEmpty()) {
            fragmentIons = new ArrayList<ParamGroup>();
            for(FragmentIonType rawFrag : rawFragIons) {
                fragmentIons.add(transformParams(rawFrag));
            }
        }
        // params
        ParamType rawParams = rawPeptide.getAdditional();
        ParamGroup params = rawParams == null ? null : transformParams(rawParams);

        return new Peptide(params, null, null, rawPeptide.getSequence(),
                              rawPeptide.getStart(), rawPeptide.getEnd(),
                              modifications, fragmentIons, spectrum);
    }

    public static Modification transformModification(uk.ac.ebi.pride.data.jaxb.pridexml.Modification rawMod) {
        if (rawMod == null) {
            throw new IllegalArgumentException("Modification cannot be NULL");
        }

        // mono delta
        List<String> rawMonoDelta = rawMod.getModMonoDelta();
        List<Double> monoDelta = null;
        if (rawMonoDelta != null && !rawMonoDelta.isEmpty()) {
            monoDelta = new ArrayList<Double>();
            for(String delta : rawMonoDelta) {
                monoDelta.add(new Double(delta));
            }
        }
        // mono avg delta
        List<String> rawAvgDelta = rawMod.getModAvgDelta();
        List<Double> avgDelta = null;
        if (rawAvgDelta != null && !rawAvgDelta.isEmpty()) {
            avgDelta = new ArrayList<Double>();
            for(String delta : rawAvgDelta) {
                avgDelta.add(new Double(delta));
            }
        }
        //params
        ParamType rawParams = rawMod.getAdditional();
        ParamGroup params = rawParams == null ? null : transformParams(rawParams);
        return  new Modification(params, rawMod.getModAccession(), rawMod.getModDatabase(),
                                        rawMod.getModDatabaseVersion(), monoDelta,
                                        avgDelta, rawMod.getModLocation());
    }

    public static Protocol transformProtocol(ExperimentType.Protocol rawProt) {
        if (rawProt == null) {
            throw new IllegalArgumentException("ExperimentType.Protocol cannot be NULL");
        }
        
        List<ParamType> rawSteps = rawProt.getProtocolSteps().getStepDescription();
        List<ParamGroup> protocolSteps = null;
        if (rawSteps != null) {
            protocolSteps = new ArrayList<ParamGroup>();
            for(ParamType rawStep : rawSteps) {
                protocolSteps.add(transformParams(rawStep));
            }
        }

        return new Protocol(null, rawProt.getProtocolName(), protocolSteps, null);
    }

    public static Reference transformReference(ReferenceType rawRef) {
        if (rawRef == null) {
            throw new IllegalArgumentException("ReferenceType cannot be NULL");
        }

        ParamType rawParamType = rawRef.getAdditional();
        ParamGroup params = rawParamType == null ? null : transformParams(rawParamType);

        return new Reference(rawRef.getRefLine(), params);
    }

    public static ParamGroup transformParams(ParamType rawAdditional) {

        List<Object> rawParams = rawAdditional.getCvParamOrUserParam();
        List<CvParam> cvParams = new ArrayList<CvParam>();
        List<UserParam> userParams = new ArrayList<UserParam>();
        for(Object rawParam : rawParams) {
            if (rawParam instanceof CvParamType) {
                CvParam cvParam = transformCvParam((CvParamType)rawParam);
                cvParams.add(cvParam);
            } else if (rawParam instanceof UserParamType) {
                UserParam userParam = transformUserParam((UserParamType)rawParam);
                userParams.add(userParam);
            }
        }

        return new ParamGroup(cvParams, userParams);
    }

    public static UserParam transformUserParam(UserParamType rawUserParam) {

        return new UserParam(rawUserParam.getName(), rawUserParam.getValue(),
                                    null, null, null, -1, false);
    }

    public static CvParam transformCvParam(CvParamType rawCvParam) {

        return new CvParam(rawCvParam.getAccession(), rawCvParam.getName(),
                              rawCvParam.getCvLabel(), rawCvParam.getValue(),
                              null, null, null, -1, false);
    }
}
