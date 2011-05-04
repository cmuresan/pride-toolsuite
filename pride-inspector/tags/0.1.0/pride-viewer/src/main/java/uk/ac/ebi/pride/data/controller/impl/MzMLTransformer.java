package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;

import java.math.BigInteger;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 15:11:47
 */
public class  MzMLTransformer {
    public static Spectrum transformSpectrum(uk.ac.ebi.jmzml.model.mzml.Spectrum spectrum) {
        Spectrum newSpec = null;
        if (spectrum != null) {

            DataProcessing dataProcessing = transformDataProcessing(spectrum.getDataProcessing());
            SourceFile sourceFile = transformSourceFile(spectrum.getSourceFile());
            ScanList scans = transformScanList(spectrum.getScanList());
            List<Precursor> precursors = transformPrecursorList(spectrum.getPrecursorList());
            List<Product> products = transformProductList(spectrum.getProductList());
            List<BinaryDataArray> binaryArray = transformBinaryDataArrayList(spectrum.getBinaryDataArrayList());
            ParamGroup paramGroup = transformParamGroup(spectrum);

            //ToDo: instrument, sample, startTimeStamp is null at the moment
            newSpec = new Spectrum(spectrum.getId(), spectrum.getIndex(),
                                   spectrum.getSpotID(), null, dataProcessing,
                                   spectrum.getDefaultArrayLength(), sourceFile,
                                   null, null, scans, precursors,
                                   products, binaryArray,
                                   paramGroup);
        }
        return newSpec;
    }

    public static <T extends uk.ac.ebi.jmzml.model.mzml.ParamGroup> ParamGroup transformParamGroup(T paramGroup) {
        ParamGroup newParamGroup = null;

        if (paramGroup != null) {
            List<CvParam> cvParams = new ArrayList<CvParam>();
            List<UserParam> userParams = new ArrayList<UserParam>();

            transformReferenceableParamGroup(cvParams, userParams, paramGroup.getReferenceableParamGroupRef());
            transformCvParam(cvParams, paramGroup.getCvParam());
            transformUserParam(userParams, paramGroup.getUserParam());
            newParamGroup = new ParamGroup(cvParams, userParams);
        }

        return newParamGroup;
    }

    public static <T extends uk.ac.ebi.jmzml.model.mzml.ParamGroup> List<ParamGroup> transformParamGroupList(
            List<T> oldParamGroupList) {
        List<ParamGroup> newParamGroupList = null;

        if (oldParamGroupList != null) {
            newParamGroupList = new ArrayList<ParamGroup>();
            for(T oldParamGroup : oldParamGroupList ) {
                newParamGroupList.add(transformParamGroup(oldParamGroup));
            }
        }
        return newParamGroupList;
    }

    public static Map<String, ParamGroup> transformReferenceableParamGroupList(uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroupList oldRefParamGroupList) {
        Map<String, ParamGroup> refMap = null;
        if (oldRefParamGroupList != null) {
            refMap = new HashMap<String, ParamGroup>();
            List<uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroup> oldRefParamGroups = oldRefParamGroupList.getReferenceableParamGroup();
            for(uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroup oldRefParamGroup : oldRefParamGroups) {
                String id = oldRefParamGroup.getId();
                List<CvParam> cvParams = new ArrayList<CvParam>();
                List<UserParam> userParams = new ArrayList<UserParam>();

                transformCvParam(cvParams, oldRefParamGroup.getCvParam());
                transformUserParam(userParams, oldRefParamGroup.getUserParam());
                ParamGroup newParamGroup = new ParamGroup(cvParams, userParams);
                refMap.put(id, newParamGroup);
            }
        }
        return refMap;
    }

    private static List<CvParam> transformCvParam(List<CvParam> newCvParams, List<uk.ac.ebi.jmzml.model.mzml.CVParam> oldCvParams) {
        List<CvParam> results = newCvParams;

        if (oldCvParams != null) {
            for (uk.ac.ebi.jmzml.model.mzml.CVParam oldParam : oldCvParams) {
                String cvLookupID = null;
                uk.ac.ebi.jmzml.model.mzml.CV cv = oldParam.getCV();
                if (cv != null)
                    cvLookupID = cv.getId();
                String unitCVLookupID = null;
                cv = oldParam.getUnitCV();
                if (cv !=  null)
                    unitCVLookupID = cv.getId();
                CvParam newParam = new CvParam(oldParam.getAccession(), oldParam.getName(), cvLookupID,
                                               oldParam.getValue(), oldParam.getUnitAccession(),
                                               oldParam.getUnitName(), unitCVLookupID, -1, false);
                results.add(newParam);
            }
        }
        return results;
    }

    private static List<UserParam> transformUserParam(List<UserParam> newUserParams, List<uk.ac.ebi.jmzml.model.mzml.UserParam> oldUserParams) {
        List<UserParam> results = newUserParams;

        if (oldUserParams != null) {
            for (uk.ac.ebi.jmzml.model.mzml.UserParam oldParam : oldUserParams) {
                String unitCVLookupID = null;
                uk.ac.ebi.jmzml.model.mzml.CV cv = oldParam.getUnitCV();
                if (cv !=  null)
                    unitCVLookupID = cv.getId();
                UserParam newParam = new UserParam(oldParam.getName(), oldParam.getValue(), oldParam.getUnitAccession(),
                                               oldParam.getUnitName(), unitCVLookupID, -1, false);
                results.add(newParam);
            }
        }
        
        return results;
    }

    private static void transformReferenceableParamGroup(List<CvParam> cvParams,
                                                         List<UserParam> userParams,
                                                         List<uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroupRef> paramRefs) {
        if (paramRefs != null) {
            for (uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroupRef ref : paramRefs) {
                transformCvParam(cvParams, ref.getReferenceableParamGroup().getCvParam());
                transformUserParam(userParams, ref.getReferenceableParamGroup().getUserParam());
            }
        }
    }

    private static List<BinaryDataArray> transformBinaryDataArrayList(uk.ac.ebi.jmzml.model.mzml.BinaryDataArrayList binaryDataArrayList) {
        List<BinaryDataArray> dataArrs = null;

        if (binaryDataArrayList != null) {
            dataArrs = new ArrayList<BinaryDataArray>();
            List<uk.ac.ebi.jmzml.model.mzml.BinaryDataArray> oldDataArrs = binaryDataArrayList.getBinaryDataArray();

            for(uk.ac.ebi.jmzml.model.mzml.BinaryDataArray oldBinaryArr : oldDataArrs) {
                dataArrs.add(transformBinaryDataArray(oldBinaryArr));
            }
        }

        return dataArrs;
    }

    private static BinaryDataArray transformBinaryDataArray(uk.ac.ebi.jmzml.model.mzml.BinaryDataArray oldBinaryArr) {
        BinaryDataArray newBinaryArr = null;

        if (oldBinaryArr != null) {
            byte[] binary = oldBinaryArr.getBinary();
            double[] binaryDoubleArr = BinaryDataUtils.toDoubleArray(binary,
                                    BinaryDataUtils.BinaryDataType.FLOAT64BIT, ByteOrder.LITTLE_ENDIAN);
            BigInteger arrLength = oldBinaryArr.getArrayLength();
            DataProcessing dataProcessing = transformDataProcessing(oldBinaryArr.getDataProcessing());
            BigInteger encodedLength = oldBinaryArr.getEncodedLength();
            ParamGroup paramGroup = transformParamGroup(oldBinaryArr);

            newBinaryArr = new BinaryDataArray(dataProcessing, binaryDoubleArr, paramGroup);
        }

        return newBinaryArr;
    }

    private static List<Product> transformProductList(uk.ac.ebi.jmzml.model.mzml.ProductList productList) {
        List<Product> products = null;

        if (productList != null) {
            products = new ArrayList<Product>();
            List<uk.ac.ebi.jmzml.model.mzml.Product> oldProducts = productList.getProduct();

            for(uk.ac.ebi.jmzml.model.mzml.Product oldProduct : oldProducts) {
                products.add(transformProduct(oldProduct));
            }
        }
        
        return products;
    }

    private static Product transformProduct(uk.ac.ebi.jmzml.model.mzml.Product oldProduct) {
        Product newProduct = null;

        if (oldProduct != null) {
            uk.ac.ebi.jmzml.model.mzml.ParamGroup isowin = oldProduct.getIsolationWindow();
            ParamGroup newIsoWin = transformParamGroup(isowin);
            newProduct = new Product(newIsoWin);
        }

        return newProduct;
    }

    private static List<Precursor> transformPrecursorList(uk.ac.ebi.jmzml.model.mzml.PrecursorList precursorList) {
        List<Precursor> precursors = null;

        if (precursorList != null) {
            precursors = new ArrayList<Precursor>();
            List<uk.ac.ebi.jmzml.model.mzml.Precursor> oldPrecursors = precursorList.getPrecursor();
            for (uk.ac.ebi.jmzml.model.mzml.Precursor oldPrecursor : oldPrecursors) {
                precursors.add(transformPrecursor(oldPrecursor));
            }
        }

        return precursors;
    }

    private static Precursor transformPrecursor(uk.ac.ebi.jmzml.model.mzml.Precursor oldPrecursor) {
        Precursor newPrecursor = null;

        if (oldPrecursor != null) {
            int msLevel = -1;
            Spectrum parentSpectrum = transformSpectrum(oldPrecursor.getSpectrum());
            SourceFile sourceFile = transformSourceFile(oldPrecursor.getSourceFile());
            String externalSpectrumID = oldPrecursor.getExternalSpectrumID();
            ParamGroup isolationWindow = transformParamGroup(oldPrecursor.getIsolationWindow());
            uk.ac.ebi.jmzml.model.mzml.SelectedIonList oldSelectedIonList = oldPrecursor.getSelectedIonList();
            List<ParamGroup> selectedIon = null;
            if (oldSelectedIonList != null)
                selectedIon = transformParamGroupList(oldSelectedIonList.getSelectedIon());
            ParamGroup activation = transformParamGroup(oldPrecursor.getActivation());
            newPrecursor = new Precursor(activation, externalSpectrumID, isolationWindow,
                                         msLevel, selectedIon, sourceFile, parentSpectrum);
        }

        return newPrecursor;
    }

    private static ScanList transformScanList(uk.ac.ebi.jmzml.model.mzml.ScanList scanList) {
        ScanList newScanList = null;

        if (scanList != null) {
            List<Scan> scans = new ArrayList<Scan>();
            List<uk.ac.ebi.jmzml.model.mzml.Scan> oldScans = scanList.getScan();

            for (uk.ac.ebi.jmzml.model.mzml.Scan oldScan : oldScans) {
                scans.add(transformScan(oldScan));
            }
            ParamGroup paramGroup = transformParamGroup(scanList);
            newScanList = new ScanList(scans, paramGroup);
        }

        return newScanList;
    }

    private static Scan transformScan(uk.ac.ebi.jmzml.model.mzml.Scan oldScan) {
        Scan newScan = null;

        if (oldScan != null) {
            String spectrum = oldScan.getSpectrumRef();
            SourceFile sourceFile = transformSourceFile(oldScan.getSourceFile());
            Instrument instrument = transformInstrument(oldScan.getInstrumentConfiguration());
            List<ParamGroup> scanWindows = null;
            uk.ac.ebi.jmzml.model.mzml.ScanWindowList oldScanWinList = oldScan.getScanWindowList();
            if (oldScanWinList != null) {
                scanWindows = transformParamGroupList(oldScanWinList.getScanWindow());
            }
            ParamGroup paramGroup = transformParamGroup(oldScan);
            newScan = new Scan(spectrum, sourceFile, instrument, scanWindows, paramGroup);
        }

        return newScan;
    }

    public static List<SourceFile> transformSourceFileList(uk.ac.ebi.jmzml.model.mzml.SourceFileList oldSourceFileList) {
        List<SourceFile> sourceFiles = null;
        if (oldSourceFileList !=  null) {
            sourceFiles = new ArrayList<SourceFile>();
            List<uk.ac.ebi.jmzml.model.mzml.SourceFile> oldSourceFiles = oldSourceFileList.getSourceFile();
            for(uk.ac.ebi.jmzml.model.mzml.SourceFile oldSourceFile : oldSourceFiles) {
                sourceFiles.add(transformSourceFile(oldSourceFile));
            }
        }
        return sourceFiles;
    }

    private static SourceFile transformSourceFile(uk.ac.ebi.jmzml.model.mzml.SourceFile oldSourceFile) {
        SourceFile newSourceFile = null;

        if (oldSourceFile != null) {
            String name = oldSourceFile.getName();
            String id = oldSourceFile.getId();
            String path = oldSourceFile.getLocation();
            ParamGroup paramGroup = transformParamGroup(oldSourceFile);
            newSourceFile = new SourceFile(id, name, path, paramGroup);
        }

        return newSourceFile;
    }

    private static Instrument transformInstrument(uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration oldInstrument) {
        Instrument newInstrument = null;

        if (oldInstrument != null) {
            String id = oldInstrument.getId();
            ScanSetting scanSetting = transformScanSettings(oldInstrument.getScanSettings());
            Software software = transformSoftware(oldInstrument.getSoftwareRef().getSoftware());
            List<ParamGroup> source = transformParamGroupList(oldInstrument.getComponentList().getSource());
            List<ParamGroup> analyzerList = transformParamGroupList(oldInstrument.getComponentList().getAnalyzer());
            List<ParamGroup> detector = transformParamGroupList(oldInstrument.getComponentList().getDetector());
            ParamGroup paramGroup = transformParamGroup(oldInstrument);
            newInstrument = new Instrument(id, scanSetting, software, source, analyzerList, detector, paramGroup);
        }

        return newInstrument;
    }

    private static ScanSetting transformScanSettings(uk.ac.ebi.jmzml.model.mzml.ScanSettings oldScanSettings) {
        ScanSetting newScanSetting = null;

        if (oldScanSettings != null) {
            String id = oldScanSettings.getId();
            List<SourceFile> sourceFile = new ArrayList<SourceFile>();
            // ToDo: this might need to improve
            List<uk.ac.ebi.jmzml.model.mzml.SourceFileRef> oldSourceFileRefs = oldScanSettings.getSourceFileRefList().getSourceFileRef();
            for(uk.ac.ebi.jmzml.model.mzml.SourceFileRef oldSourceFileRef : oldSourceFileRefs) {
                sourceFile.add(transformSourceFile(oldSourceFileRef.getSourceFile()));
            }
            List<ParamGroup> targets = transformParamGroupList(oldScanSettings.getTargetList().getTarget());
            ParamGroup paramGroup = transformParamGroup(oldScanSettings);
            newScanSetting = new ScanSetting(id, sourceFile, targets, paramGroup);
        }
        return newScanSetting;
    }

    private static ProcessingMethod transformProcessingMethod(uk.ac.ebi.jmzml.model.mzml.ProcessingMethod oldProcMethod) {
        ProcessingMethod newProcessingMethod = null;

        if (oldProcMethod != null) {
            Software software = transformSoftware(oldProcMethod.getSoftware());
            ParamGroup paramGroup = transformParamGroup(oldProcMethod);
            newProcessingMethod = new ProcessingMethod(software, paramGroup);
        }
        return newProcessingMethod;
    }

    public static Chromatogram transformChromatogram(uk.ac.ebi.jmzml.model.mzml.Chromatogram chroma) {
        Chromatogram newChroma = null;

        if (chroma != null) {
            String id = chroma.getId();
            BigInteger index = chroma.getIndex();
            Instrument instrument = null;
            DataProcessing dataProcessing = transformDataProcessing(chroma.getDataProcessing());
            int arrLength = chroma.getDefaultArrayLength();
            SourceFile sourceFile = null;
            Sample sample = null;
            Date timeStamp = null;
            Precursor precursor = transformPrecursor(chroma.getPrecursor());
            List<Precursor> precursors = null;
            if (precursor != null) {
                precursors = new ArrayList<Precursor>();
                precursors.add(precursor);
            }
            Product product = transformProduct(chroma.getProduct());
            List<Product> products = null;
            if (product != null) {
                products = new ArrayList<Product>();
                products.add(product);
            }
            List<BinaryDataArray> binaryArr = transformBinaryDataArrayList(chroma.getBinaryDataArrayList());
            ParamGroup paramGroup = transformParamGroup(chroma);
            newChroma = new Chromatogram(id, index, instrument, dataProcessing,
                                arrLength, sourceFile, sample,
                                timeStamp, precursors, products, binaryArr,
                                paramGroup);
        }

        return newChroma;
    }

    public static List<CVLookup> transformCVList(uk.ac.ebi.jmzml.model.mzml.CVList oldCvList) {
        List<CVLookup> cvLookups = null;
        if (oldCvList != null) {
            cvLookups = new ArrayList<CVLookup>();
            List<uk.ac.ebi.jmzml.model.mzml.CV> oldCvs = oldCvList.getCv();
            for(uk.ac.ebi.jmzml.model.mzml.CV oldCV : oldCvs) {
                cvLookups.add(transformCVLookup(oldCV));
            }
        }
        return cvLookups;
    }

    public static CVLookup transformCVLookup(uk.ac.ebi.jmzml.model.mzml.CV oldCv) {
        CVLookup cvLookup = null;
        if (oldCv != null) {
            cvLookup = new CVLookup(oldCv.getId(), oldCv.getFullName(),
                                    oldCv.getVersion(), oldCv.getURI());
        }
        return cvLookup;
    }

    public static List<Sample> transformSampleList(uk.ac.ebi.jmzml.model.mzml.SampleList oldSampleList) {
        List<Sample> samples = null;

        if (oldSampleList != null) {
            samples = new ArrayList<Sample>();
            List<uk.ac.ebi.jmzml.model.mzml.Sample> oldSamples = oldSampleList.getSample();
            for(uk.ac.ebi.jmzml.model.mzml.Sample oldSample : oldSamples) {
                samples.add(transformSample(oldSample));
            }
        }
        return samples;
    }

    public static Sample transformSample(uk.ac.ebi.jmzml.model.mzml.Sample oldSample) {
        Sample newSample = null;

        if (oldSample != null) {
            String id = oldSample.getId();
            String name = oldSample.getName();
            ParamGroup paramGroup = transformParamGroup(oldSample);
            newSample = new Sample(id, name, paramGroup);
        }

        return newSample;  //To change body of created methods use File | Settings | File Templates.
    }

    public static List<Software> transformSoftwareList(uk.ac.ebi.jmzml.model.mzml.SoftwareList oldSoftwareList) {
        List<Software> softwares = null;

        if (oldSoftwareList != null) {
            softwares = new ArrayList<Software>();
            List<uk.ac.ebi.jmzml.model.mzml.Software> oldSoftwares = oldSoftwareList.getSoftware();
            for(uk.ac.ebi.jmzml.model.mzml.Software oldSoftware : oldSoftwares) {
                softwares.add(transformSoftware(oldSoftware));
            }
        }
        return softwares;
    }

    public static Software transformSoftware(uk.ac.ebi.jmzml.model.mzml.Software oldSoftware) {
        Software newSoftware = null;

        if (oldSoftware != null) {
            String id = oldSoftware.getId();
            String name = null;
            String version = oldSoftware.getVersion();
            ParamGroup paramGroup = transformParamGroup(oldSoftware);
            newSoftware = new Software(id, name, version, paramGroup);
        }
        return newSoftware;
    }

    public static List<ScanSetting> transformScanSettingList(uk.ac.ebi.jmzml.model.mzml.ScanSettingsList oldScanSettingsList) {
        List<ScanSetting> scanSettings = null;

        if (oldScanSettingsList != null) {
            scanSettings = new ArrayList<ScanSetting>();
            List<uk.ac.ebi.jmzml.model.mzml.ScanSettings> oldScanSettings = oldScanSettingsList.getScanSettings();
            for(uk.ac.ebi.jmzml.model.mzml.ScanSettings oldScanSetting : oldScanSettings) {
                scanSettings.add(transformScanSetting(oldScanSetting));
            }
        }

        return scanSettings;
    }

    public static ScanSetting transformScanSetting(uk.ac.ebi.jmzml.model.mzml.ScanSettings oldScanSetting) {
        ScanSetting scanSetting = null;

        if (oldScanSetting != null) {
            String id = oldScanSetting.getId();
            List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
            List<uk.ac.ebi.jmzml.model.mzml.SourceFileRef> oldSourceFileRefs = oldScanSetting.getSourceFileRefList().getSourceFileRef();
            for(uk.ac.ebi.jmzml.model.mzml.SourceFileRef oldSourceFileRef : oldSourceFileRefs) {
                sourceFiles.add(transformSourceFile(oldSourceFileRef.getSourceFile()));
            }
            List<ParamGroup> targets = transformParamGroupList(oldScanSetting.getTargetList().getTarget());
            ParamGroup paramGroup = transformParamGroup(oldScanSetting);
            scanSetting =  new ScanSetting(id, sourceFiles, targets, paramGroup);
        }

        return scanSetting;  //To change body of created methods use File | Settings | File Templates.
    }

    public static List<Instrument> transformInstrumentConfigurationList(uk.ac.ebi.jmzml.model.mzml.InstrumentConfigurationList oldInstrumentList) {
        List<Instrument> instruments = null;

        if (oldInstrumentList != null) {
            instruments = new ArrayList<Instrument>();
            List<uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration> oldInstruments = oldInstrumentList.getInstrumentConfiguration();
            for(uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration oldInstrument : oldInstruments) {
                instruments.add(transformInstrumentConfiguration(oldInstrument));
            }
        }

        return instruments;
    }

    public static Instrument transformInstrumentConfiguration(uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration oldInstrument) {
        Instrument instrument = null;

        if (oldInstrument != null) {
            String id = oldInstrument.getId();
            ScanSetting scanSetting = transformScanSetting(oldInstrument.getScanSettings());
            Software software = transformSoftware(oldInstrument.getSoftwareRef().getSoftware());
            List<ParamGroup> source = transformParamGroupList(oldInstrument.getComponentList().getSource());
            List<ParamGroup> analyzer = transformParamGroupList(oldInstrument.getComponentList().getAnalyzer());
            List<ParamGroup> detector = transformParamGroupList(oldInstrument.getComponentList().getDetector());
            ParamGroup paramGroup = transformParamGroup(oldInstrument);
            instrument = new Instrument(id, scanSetting, software, source, analyzer, detector, paramGroup);
        }

        return instrument;
    }

    public static List<DataProcessing> transformDataProcessingList(uk.ac.ebi.jmzml.model.mzml.DataProcessingList oldDataProcessingList) {
        List<DataProcessing> dataProcessings = null;

        if (oldDataProcessingList != null) {
            dataProcessings = new ArrayList<DataProcessing>();
            List<uk.ac.ebi.jmzml.model.mzml.DataProcessing> oldDataProcessings = oldDataProcessingList.getDataProcessing();
            for(uk.ac.ebi.jmzml.model.mzml.DataProcessing oldDataProcessing : oldDataProcessings) {
                dataProcessings.add(transformDataProcessing(oldDataProcessing));
            }
        }

        return dataProcessings;
    }

    public static DataProcessing transformDataProcessing(uk.ac.ebi.jmzml.model.mzml.DataProcessing oldDataProcessing) {
        DataProcessing newDataProcessing = null;

        if (oldDataProcessing != null) {
            String id = oldDataProcessing.getId();
            List<ProcessingMethod> methods = new ArrayList<ProcessingMethod>();
            List<uk.ac.ebi.jmzml.model.mzml.ProcessingMethod> oldProcMethods = oldDataProcessing.getProcessingMethod();
            for(uk.ac.ebi.jmzml.model.mzml.ProcessingMethod oldProcMethod : oldProcMethods) {
                methods.add(transformProcessingMethod(oldProcMethod));
            }
            newDataProcessing = new DataProcessing(id, methods);
        }

        return newDataProcessing;
    }
}
