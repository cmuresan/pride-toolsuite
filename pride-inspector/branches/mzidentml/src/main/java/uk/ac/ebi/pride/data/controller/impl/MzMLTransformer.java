package uk.ac.ebi.pride.data.controller.impl;

import uk.ac.ebi.jmzml.model.mzml.ComponentList;
import uk.ac.ebi.jmzml.model.mzml.FileDescription;
import uk.ac.ebi.jmzml.model.mzml.SoftwareRef;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.coreIdent.*;
import uk.ac.ebi.pride.data.utils.BinaryDataUtils;
import uk.ac.ebi.pride.term.CvTermReference;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang, yperez
 * Date: 08-Feb-2010
 * Time: 15:11:47
 */
public class MzMLTransformer {

    private uk.ac.ebi.jmzml.model.mzml.FileDescription fileDescription = null;

    public static Spectrum transformSpectrum(uk.ac.ebi.jmzml.model.mzml.Spectrum spectrum) {
        Spectrum newSpec = null;
        if (spectrum != null) {

            String specId = spectrum.getId();
            int index = spectrum.getIndex().intValue();
            String spotId = spectrum.getSpotID();
            DataProcessing dataProcessing = transformDataProcessing(spectrum.getDataProcessing());
            int arrLen = spectrum.getDefaultArrayLength();
            SourceFile sourceFile = transformSourceFile(spectrum.getSourceFile());
            ScanList scans = transformScanList(spectrum.getScanList());
            List<Precursor> precursors = transformPrecursorList(spectrum.getPrecursorList());
            List<ParamGroup> products = transformProductList(spectrum.getProductList());
            List<BinaryDataArray> binaryArray = transformBinaryDataArrayList(spectrum.getBinaryDataArrayList());
            ParamGroup paramGroup = transformParamGroup(spectrum);
            newSpec = new Spectrum(paramGroup,specId, null,index, dataProcessing, arrLen, binaryArray,spotId, sourceFile, scans, precursors,products);
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
            for (T oldParamGroup : oldParamGroupList) {
                newParamGroupList.add(transformParamGroup(oldParamGroup));
            }
        }
        return newParamGroupList;
    }

    public static ReferenceableParamGroup transformReferenceableParamGroupList(uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroupList oldRefParamGroupList) {
        Map<String, ParamGroup> refMap = null;
        if (oldRefParamGroupList != null) {
            refMap = new HashMap<String, ParamGroup>();
            List<uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroup> oldRefParamGroups = oldRefParamGroupList.getReferenceableParamGroup();
            for (uk.ac.ebi.jmzml.model.mzml.ReferenceableParamGroup oldRefParamGroup : oldRefParamGroups) {
                String id = oldRefParamGroup.getId();
                List<CvParam> cvParams = new ArrayList<CvParam>();
                List<UserParam> userParams = new ArrayList<UserParam>();

                transformCvParam(cvParams, oldRefParamGroup.getCvParam());
                transformUserParam(userParams, oldRefParamGroup.getUserParam());
                ParamGroup newParamGroup = new ParamGroup(cvParams, userParams);
                refMap.put(id, newParamGroup);
            }
        }
        return new ReferenceableParamGroup(refMap);
    }

    private static List<CvParam> transformCvParam(List<CvParam> newCvParams, List<uk.ac.ebi.jmzml.model.mzml.CVParam> oldCvParams) {

        if (oldCvParams != null) {
            for (uk.ac.ebi.jmzml.model.mzml.CVParam oldParam : oldCvParams) {
                String cvLookupID = null;
                uk.ac.ebi.jmzml.model.mzml.CV cv = oldParam.getCV();
                if (cv != null)
                    cvLookupID = cv.getId();
                String unitCVLookupID = null;
                cv = oldParam.getUnitCV();
                if (cv != null)
                    unitCVLookupID = cv.getId();
                CvParam newParam = new CvParam(oldParam.getAccession(), oldParam.getName(), cvLookupID,
                        oldParam.getValue(), oldParam.getUnitAccession(),
                        oldParam.getUnitName(), unitCVLookupID);
                newCvParams.add(newParam);
            }
        }
        return newCvParams;
    }

    private static List<UserParam> transformUserParam(List<UserParam> newUserParams, List<uk.ac.ebi.jmzml.model.mzml.UserParam> oldUserParams) {

        if (oldUserParams != null) {
            for (uk.ac.ebi.jmzml.model.mzml.UserParam oldParam : oldUserParams) {
                String unitCVLookupID = null;
                uk.ac.ebi.jmzml.model.mzml.CV cv = oldParam.getUnitCV();
                if (cv != null)
                    unitCVLookupID = cv.getId();
                UserParam newParam = new UserParam(oldParam.getName(), oldParam.getType(),
                        oldParam.getValue(), oldParam.getUnitAccession(),
                        oldParam.getUnitName(), unitCVLookupID);
                newUserParams.add(newParam);
            }
        }

        return newUserParams;
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

            for (uk.ac.ebi.jmzml.model.mzml.BinaryDataArray oldBinaryArr : oldDataArrs) {
                dataArrs.add(transformBinaryDataArray(oldBinaryArr));
            }
        }

        return dataArrs;
    }

    private static BinaryDataArray transformBinaryDataArray(uk.ac.ebi.jmzml.model.mzml.BinaryDataArray oldBinaryArr) {
        BinaryDataArray newBinaryArr = null;

        if (oldBinaryArr != null) {
            byte[] binary = oldBinaryArr.getBinary();

            ParamGroup paramGroup = transformParamGroup(oldBinaryArr);

            CvTermReference binaryDataType = null;
            boolean isCompressed = false;
            List<CvParam> cvParams = paramGroup.getCvParams();
            for (CvParam cvParam : cvParams) {
                String acc = cvParam.getAccession();
                if (CvTermReference.isChild(CvTermReference.BINARY_DATA_TYPE.getAccession(), acc)) {
                    binaryDataType = CvTermReference.getCvRefByAccession(acc);
                } else if (CvTermReference.ZLIB_COMPRESSION.getAccession().equals(acc)) {
                    isCompressed = true;
                }
            }

            if (isCompressed) {
                binary = BinaryDataUtils.decompress(binary);
            }

            double[] binaryDoubleArr = BinaryDataUtils.toDoubleArray(binary, binaryDataType, ByteOrder.LITTLE_ENDIAN);
            DataProcessing dataProcessing = transformDataProcessing(oldBinaryArr.getDataProcessing());

            newBinaryArr = new BinaryDataArray(dataProcessing, binaryDoubleArr, paramGroup);
        }

        return newBinaryArr;
    }

    private static List<ParamGroup> transformProductList(uk.ac.ebi.jmzml.model.mzml.ProductList productList) {
        List<ParamGroup> products = null;

        if (productList != null) {
            products = new ArrayList<ParamGroup>();
            List<uk.ac.ebi.jmzml.model.mzml.Product> oldProducts = productList.getProduct();

            for (uk.ac.ebi.jmzml.model.mzml.Product oldProduct : oldProducts) {
                uk.ac.ebi.jmzml.model.mzml.ParamGroup isowin = oldProduct.getIsolationWindow();
                ParamGroup newIsoWin = transformParamGroup(isowin);
                products.add(newIsoWin);
            }
        }

        return products;
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
            Spectrum parentSpectrum = transformSpectrum(oldPrecursor.getSpectrum());
            SourceFile sourceFile = transformSourceFile(oldPrecursor.getSourceFile());
            String externalSpectrumID = oldPrecursor.getExternalSpectrumID();
            ParamGroup isolationWindow = transformParamGroup(oldPrecursor.getIsolationWindow());
            uk.ac.ebi.jmzml.model.mzml.SelectedIonList oldSelectedIonList = oldPrecursor.getSelectedIonList();
            List<ParamGroup> selectedIon = null;
            if (oldSelectedIonList != null)
                selectedIon = transformParamGroupList(oldSelectedIonList.getSelectedIon());
            ParamGroup activation = transformParamGroup(oldPrecursor.getActivation());
            newPrecursor = new Precursor(parentSpectrum, sourceFile,
                    externalSpectrumID, isolationWindow,
                    selectedIon, activation);
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
            String externalSpecRef = oldScan.getExternalSpectrumID();
            SourceFile sourceFile = transformSourceFile(oldScan.getSourceFile());
            InstrumentConfiguration instrumentConfiguration = transformInstrumentConfiguration(oldScan.getInstrumentConfiguration());
            List<ParamGroup> scanWindows = null;
            uk.ac.ebi.jmzml.model.mzml.ScanWindowList oldScanWinList = oldScan.getScanWindowList();
            if (oldScanWinList != null) {
                scanWindows = transformParamGroupList(oldScanWinList.getScanWindow());
            }
            ParamGroup paramGroup = transformParamGroup(oldScan);
            newScan = new Scan(spectrum, externalSpecRef, sourceFile, instrumentConfiguration, scanWindows, paramGroup);
        }

        return newScan;
    }

    public static List<SourceFile> transformSourceFileList(uk.ac.ebi.jmzml.model.mzml.SourceFileList oldSourceFileList) {
        List<SourceFile> sourceFiles = null;
        if (oldSourceFileList != null) {
            sourceFiles = new ArrayList<SourceFile>();
            List<uk.ac.ebi.jmzml.model.mzml.SourceFile> oldSourceFiles = oldSourceFileList.getSourceFile();
            for (uk.ac.ebi.jmzml.model.mzml.SourceFile oldSourceFile : oldSourceFiles) {
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
            newSourceFile = new SourceFile(paramGroup, id, name, path);
        }

        return newSourceFile;
    }

    private static ScanSetting transformScanSettings(uk.ac.ebi.jmzml.model.mzml.ScanSettings oldScanSettings) {
        ScanSetting newScanSetting = null;

        if (oldScanSettings != null) {
            String id = oldScanSettings.getId();
            List<SourceFile> sourceFile = new ArrayList<SourceFile>();
            // ToDo: this might need to improve
            List<uk.ac.ebi.jmzml.model.mzml.SourceFileRef> oldSourceFileRefs = oldScanSettings.getSourceFileRefList().getSourceFileRef();
            for (uk.ac.ebi.jmzml.model.mzml.SourceFileRef oldSourceFileRef : oldSourceFileRefs) {
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
            int order = oldProcMethod.getOrder().intValue();
            Software software = transformSoftware(oldProcMethod.getSoftware());
            ParamGroup paramGroup = transformParamGroup(oldProcMethod);
            newProcessingMethod = new ProcessingMethod(order, software, paramGroup);
        }
        return newProcessingMethod;
    }

    public static Chromatogram transformChromatogram(uk.ac.ebi.jmzml.model.mzml.Chromatogram chroma) {
        Chromatogram newChroma = null;

        if (chroma != null) {
            String id = chroma.getId();
            int index = chroma.getIndex().intValue();
            DataProcessing dataProcessing = transformDataProcessing(chroma.getDataProcessing());
            int arrLength = chroma.getDefaultArrayLength();
            List<BinaryDataArray> binaryArr = transformBinaryDataArrayList(chroma.getBinaryDataArrayList());
            ParamGroup paramGroup = transformParamGroup(chroma);
            newChroma = new Chromatogram(paramGroup,id, null,index, dataProcessing, arrLength, binaryArr);
        }

        return newChroma;
    }

    public static List<CVLookup> transformCVList(uk.ac.ebi.jmzml.model.mzml.CVList oldCvList) {
        List<CVLookup> cvLookups = null;
        if (oldCvList != null) {
            cvLookups = new ArrayList<CVLookup>();
            List<uk.ac.ebi.jmzml.model.mzml.CV> oldCvs = oldCvList.getCv();
            for (uk.ac.ebi.jmzml.model.mzml.CV oldCV : oldCvs) {
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
            for (uk.ac.ebi.jmzml.model.mzml.Sample oldSample : oldSamples) {
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
            newSample = new Sample(paramGroup,id, name);
        }

        return newSample;
    }

    public static List<Software> transformSoftwareList(uk.ac.ebi.jmzml.model.mzml.SoftwareList oldSoftwareList) {
        List<Software> softwares = null;

        if (oldSoftwareList != null) {
            softwares = new ArrayList<Software>();
            List<uk.ac.ebi.jmzml.model.mzml.Software> oldSoftwares = oldSoftwareList.getSoftware();
            for (uk.ac.ebi.jmzml.model.mzml.Software oldSoftware : oldSoftwares) {
                softwares.add(transformSoftware(oldSoftware));
            }
        }
        return softwares;
    }

    public static Software transformSoftware(uk.ac.ebi.jmzml.model.mzml.Software oldSoftware) {
        Software newSoftware = null;

        if (oldSoftware != null) {
            String id = oldSoftware.getId();
            String version = oldSoftware.getVersion();
            ParamGroup paramGroup = transformParamGroup(oldSoftware);
            newSoftware = new Software(paramGroup, null,id, version);
        }
        return newSoftware;
    }

    public static List<ScanSetting> transformScanSettingList(uk.ac.ebi.jmzml.model.mzml.ScanSettingsList oldScanSettingsList) {
        List<ScanSetting> scanSettings = null;

        if (oldScanSettingsList != null) {
            scanSettings = new ArrayList<ScanSetting>();
            List<uk.ac.ebi.jmzml.model.mzml.ScanSettings> oldScanSettings = oldScanSettingsList.getScanSettings();
            for (uk.ac.ebi.jmzml.model.mzml.ScanSettings oldScanSetting : oldScanSettings) {
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
            for (uk.ac.ebi.jmzml.model.mzml.SourceFileRef oldSourceFileRef : oldSourceFileRefs) {
                sourceFiles.add(transformSourceFile(oldSourceFileRef.getSourceFile()));
            }
            List<ParamGroup> targets = transformParamGroupList(oldScanSetting.getTargetList().getTarget());
            ParamGroup paramGroup = transformParamGroup(oldScanSetting);
            scanSetting = new ScanSetting(id, sourceFiles, targets, paramGroup);
        }

        return scanSetting;
    }

    public static List<InstrumentConfiguration> transformInstrumentConfigurationList(uk.ac.ebi.jmzml.model.mzml.InstrumentConfigurationList oldInstrumentList) {
        List<InstrumentConfiguration> instrumentConfigurations = null;

        if (oldInstrumentList != null) {
            instrumentConfigurations = new ArrayList<InstrumentConfiguration>();
            List<uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration> oldInstruments = oldInstrumentList.getInstrumentConfiguration();
            for (uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration oldInstrument : oldInstruments) {
                instrumentConfigurations.add(transformInstrumentConfiguration(oldInstrument));
            }
        }

        return instrumentConfigurations;
    }

    public static InstrumentConfiguration transformInstrumentConfiguration(uk.ac.ebi.jmzml.model.mzml.InstrumentConfiguration oldInstrument) {
        InstrumentConfiguration instrumentConfiguration = null;

        if (oldInstrument != null) {
            String id = oldInstrument.getId();
            ScanSetting scanSetting = transformScanSetting(oldInstrument.getScanSettings());

            // convert software
            SoftwareRef oldSoftware = oldInstrument.getSoftwareRef();
            Software software = null;
            if (oldSoftware != null) {
                software = transformSoftware(oldInstrument.getSoftwareRef().getSoftware());
            }
            // convert component list
            ComponentList componentList = oldInstrument.getComponentList();
            List<InstrumentComponent> source = new ArrayList<InstrumentComponent>();
            List<InstrumentComponent> analyzer = new ArrayList<InstrumentComponent>();
            List<InstrumentComponent> detector = new ArrayList<InstrumentComponent>();
            if (componentList != null) {
                for(uk.ac.ebi.jmzml.model.mzml.SourceComponent oldSource: componentList.getSource()){
                    InstrumentComponent newSource = transformInstrumentComponent(oldSource);
                    source.add(newSource);
                }
                for(uk.ac.ebi.jmzml.model.mzml.AnalyzerComponent oldAnalyzer: componentList.getAnalyzer()){
                    InstrumentComponent newAnalyzer = transformInstrumentComponent(oldAnalyzer);
                    source.add(newAnalyzer);
                }
                for(uk.ac.ebi.jmzml.model.mzml.DetectorComponent oldDetector: componentList.getDetector()){
                    InstrumentComponent newDetector = transformInstrumentComponent(oldDetector);
                    source.add(newDetector);
                }
            }
            ParamGroup paramGroup = transformParamGroup(oldInstrument);
            instrumentConfiguration = new InstrumentConfiguration(id, scanSetting, software, source, analyzer, detector, paramGroup);
        }

        return instrumentConfiguration;
    }

    private static InstrumentComponent transformInstrumentComponent(uk.ac.ebi.jmzml.model.mzml.Component rawComponent) {
        InstrumentComponent component = null;

        if (rawComponent != null) {
            component = new InstrumentComponent(rawComponent.getOrder(), transformParamGroup(rawComponent));
        }

        return component;
    }

    public static List<DataProcessing> transformDataProcessingList(uk.ac.ebi.jmzml.model.mzml.DataProcessingList oldDataProcessingList) {
        List<DataProcessing> dataProcessings = null;

        if (oldDataProcessingList != null) {
            dataProcessings = new ArrayList<DataProcessing>();
            List<uk.ac.ebi.jmzml.model.mzml.DataProcessing> oldDataProcessings = oldDataProcessingList.getDataProcessing();
            for (uk.ac.ebi.jmzml.model.mzml.DataProcessing oldDataProcessing : oldDataProcessings) {
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
            for (uk.ac.ebi.jmzml.model.mzml.ProcessingMethod oldProcMethod : oldProcMethods) {
                methods.add(transformProcessingMethod(oldProcMethod));
            }
            newDataProcessing = new DataProcessing(id, methods);
        }

        return newDataProcessing;
    }

    /**
     * Retrieve the file contenct
     * @param rawFileDescription
     * @return
     */
    public static ParamGroup transformFileDescriptionToFileContent(FileDescription rawFileDescription){
           if (rawFileDescription != null) {
               ParamGroup fileContent = transformParamGroup(rawFileDescription.getFileContent());
               return fileContent;
           }
        return null;
    }

    /**
     * Transform FileDescription object to List of SourceFile
     * @param rawFileDescription jmzml FileDescription Object
     * @return List<SourceFile> List of Source Files used in the MzMl
     */
    public static List<SourceFile> transformFileDescriptionToFileSource(FileDescription rawFileDescription){
        if(rawFileDescription != null){
            uk.ac.ebi.jmzml.model.mzml.SourceFileList rawSourceFileList = rawFileDescription.getSourceFileList();
            List<SourceFile> sourceFiles = transformSourceFileList(rawSourceFileList);
            return sourceFiles;
        }
        return null;
    }

    /**
     * Method to retrieve the Contact Persons From the FileDescription Object in the MzMl Files
     * @param rawFileDescription
     * @return List<Person> List of Person Contacts
     */
    public static List<Person> transformFileDescriptionToPerson(FileDescription rawFileDescription){
        if(rawFileDescription != null){
            List<ParamGroup> contacts = transformParamGroupList(rawFileDescription.getContact());
            List<Person> persons = new ArrayList<Person>();
            for (ParamGroup contact : contacts){
              CvTermReference contactTerm = CvTermReference.CONTACT_NAME;
              List<CvParam> contactsValues = DataAccessUtilities.getCvParam(contact,contactTerm.getCvLabel(),contactTerm.getAccession());
              String name = null;
              if(!contactsValues.isEmpty()){
                  name = contactsValues.get(0).getValue();
              }
              contactTerm = CvTermReference.CONTACT_EMAIL;
              contactsValues = DataAccessUtilities.getCvParam(contact,contactTerm.getCvLabel(),contactTerm.getAccession());
              String mail = null;
              if(!contactsValues.isEmpty()){
                  name = contactsValues.get(0).getValue();
              }
              Person contactPerson = new Person(contact,name,mail);
              persons.add(contactPerson);
            }
            return persons;
        }
        return null;
    }

    /**
     * Method to transform FileDescription in the MzMl file to an Organization Objet List
     * @param rawFileDescription
     * @return List<Organization> Organization List
     */
    public static List<Organization> transformFileDescriptionOrganization(FileDescription rawFileDescription){
        if(rawFileDescription != null){
            List<ParamGroup> contacts = transformParamGroupList(rawFileDescription.getContact());
            List<Organization> organizations = new ArrayList<Organization>();
            for (ParamGroup contact : contacts){
              CvTermReference contactTerm = CvTermReference.CONTACT_ORG;
              List<CvParam> contactsValues = DataAccessUtilities.getCvParam(contact,contactTerm.getCvLabel(),contactTerm.getAccession());
              String name = null;
              if(!contactsValues.isEmpty()){
                  name = contactsValues.get(0).getValue();
              }
              contactTerm = CvTermReference.CONTACT_EMAIL;
              contactsValues = DataAccessUtilities.getCvParam(contact,contactTerm.getCvLabel(),contactTerm.getAccession());
              String mail = null;
              if(!contactsValues.isEmpty()){
                  name = contactsValues.get(0).getValue();
              }
              Organization contactOrganization = new Organization(contact,name,mail);
              organizations.add(contactOrganization);
            }
            return organizations;
        }
        return null;
    }
}
