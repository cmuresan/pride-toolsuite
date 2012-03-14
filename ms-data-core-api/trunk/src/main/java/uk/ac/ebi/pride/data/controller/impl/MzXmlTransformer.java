package uk.ac.ebi.pride.data.controller.impl;

import sun.nio.cs.ext.DoubleByteEncoder;
import uk.ac.ebi.jmzml.model.mzml.ComponentList;
import uk.ac.ebi.jmzml.model.mzml.SoftwareRef;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.term.CvTermReference;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 2/27/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MzXmlTransformer {

    /**
     * Convert spectrum
     *
     * @param  spectrum spectrum object
     * @return Spectrum    spectrum
     */
    public static Spectrum transformSpectrum(uk.ac.ebi.pride.tools.jmzreader.model.Spectrum spectrum) {
        Spectrum newSpec = null;
        if (spectrum != null) {

            String specId = spectrum.getId();
            int index = -1; //spectrum.getIndex().intValue();
            String spotId = null; //spectrum.getSpotID();
            DataProcessing dataProcessing = null;  //transformDataProcessing(spectrum.getDataProcessing());
            int arrLen = -1; // spectrum.getDefaultArrayLength();
            SourceFile sourceFile = null; //transformSourceFile(spectrum.getSourceFile());
            ScanList scans = null; //transformScanList(spectrum.getScanList());
            List<ParamGroup> products = null; //transformProductList(spectrum.getProductList());
            List<Precursor> precursors = null;
            List<BinaryDataArray> binaryArray = transformBinaryDataArrayList(spectrum.getPeakList());

            ParamGroup paramGroup = new ParamGroup();
            CvTermReference cvTerm = CvTermReference.MS_LEVEL;
            CvParam cvParam = new CvParam(cvTerm.getAccession(),cvTerm.getName(),cvTerm.getCvLabel(),spectrum.getMsLevel().toString(),null,null,null);
            paramGroup.addCvParam(cvParam);
            if(spectrum.getPrecursorMZ() != null || spectrum.getPrecursorIntensity() !=null || spectrum.getPrecursorCharge() !=null){
                precursors = new ArrayList<Precursor>();
                ParamGroup ionSelected = new ParamGroup();
                if(spectrum.getPrecursorMZ()!=null){
                    cvTerm = CvTermReference.ION_SELECTION_MZ;
                    cvParam = new CvParam(cvTerm.getAccession(),cvTerm.getName(),cvTerm.getCvLabel(),spectrum.getPrecursorMZ().toString(),null,null,null);
                    ionSelected.addCvParam(cvParam);
                }
                if(spectrum.getPrecursorCharge()!=null){
                    cvTerm = CvTermReference.ION_SELECTION_CHARGE_STATE;
                    cvParam = new CvParam(cvTerm.getAccession(),cvTerm.getName(),cvTerm.getCvLabel(),spectrum.getPrecursorCharge().toString(),null,null,null);
                    ionSelected.addCvParam(cvParam);
                }
                if(spectrum.getPrecursorIntensity()!=null){
                    cvTerm = CvTermReference.ION_SELECTION_INTENSITY;
                    cvParam = new CvParam(cvTerm.getAccession(),cvTerm.getName(),cvTerm.getCvLabel(),spectrum.getPrecursorIntensity().toString(),null,null,null);
                    ionSelected.addCvParam(cvParam);
                }
                List<ParamGroup> listIons = new ArrayList<ParamGroup>();
                listIons.add(ionSelected);
                Precursor precursor = new Precursor(null,null,null,null,listIons,null);
                precursors.add(precursor);
            }

            newSpec = new Spectrum(paramGroup, specId, null, index, dataProcessing, arrLen,
                    binaryArray, spotId, sourceFile, scans, precursors, products);
        }
        return newSpec;
    }

    private static List<BinaryDataArray> transformBinaryDataArrayList(Map<Double, Double> peakList) {
        List<BinaryDataArray> binaryDataArrays = new ArrayList<BinaryDataArray>();
        uk.ac.ebi.pride.term.CvTermReference cvRefMz = CvTermReference.MZ_ARRAY;
        CvParam cvParamMz = new CvParam(cvRefMz.getAccession(),cvRefMz.getName(),cvRefMz.getCvLabel(),"",cvRefMz.getAccession(),cvRefMz.getName(),cvRefMz.getCvLabel());
        ParamGroup mzParam = new ParamGroup(cvParamMz,null);

        uk.ac.ebi.pride.term.CvTermReference cvRefInt = CvTermReference.INTENSITY_ARRAY;
        CvParam cvParam = new CvParam(cvRefInt.getAccession(),cvRefInt.getName(),cvRefInt.getCvLabel(),"",cvRefInt.getAccession(),cvRefInt.getName(),cvRefInt.getCvLabel());
        ParamGroup intParam = new ParamGroup(cvParam,null);

        double[] intArray = new double[peakList.keySet().size()];
        double[] mzArray  = new double[peakList.keySet().size()];
        int i = 0;
        for(Double mz: peakList.keySet()){
            mzArray[i]  = mz.doubleValue();
            intArray[i] = peakList.get(mz).doubleValue();
            i++;
        }

        //Todo: How you can know if the intensity correspond with the mz value?

        BinaryDataArray intBinaryArr = new BinaryDataArray(null, intArray,intParam);
        binaryDataArrays.add(intBinaryArr);
        BinaryDataArray mzBinaryArr = new BinaryDataArray(null, mzArray,mzParam);
        binaryDataArrays.add(mzBinaryArr);

        return binaryDataArrays;


    }

    public static List<Person> transformPersonContacts(List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Operator> operators) {
        List<Person> persons = null;
        if(operators != null){
            persons = new ArrayList<Person>();
            for(uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Operator operator: operators){
                ParamGroup paramGroup = new ParamGroup();
                CvTermReference contactTerm = CvTermReference.CONTACT_NAME;
                CvParam cvParam = new CvParam(contactTerm.getAccession(),contactTerm.getName(),contactTerm.getCvLabel(),operator.getFirst() + " " + operator.getLast(),null,null,null);
                paramGroup.addCvParam(cvParam);
                contactTerm = CvTermReference.CONTACT_EMAIL;
                cvParam = new CvParam(contactTerm.getAccession(),contactTerm.getName(),contactTerm.getCvLabel(),operator.getEmail(),null,null,null);
                paramGroup.addCvParam(cvParam);
                Person person = new Person(paramGroup,null,operator.getFirst(),operator.getLast(),operator.getFirst(),null,null,operator.getEmail());
                persons.add(person);
            }
        }
        return persons;
    }




    public static List<SourceFile> transformFileSources(List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.ParentFile> rawParentFiles) {
        List<SourceFile> sourceFiles = null;
        if(rawParentFiles != null){
            sourceFiles = new ArrayList<SourceFile>();
            for(uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.ParentFile parentFile: rawParentFiles){
                SourceFile sourceFile = new SourceFile(null,null,parentFile.getFileName(),null,null,null);
                //Todo: It could be interesting to convert typeFormat from mzXml to a CvParam.
            }
        }
        return sourceFiles;
    }

    public static List<Software> transformSoftwares(List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Software> rawSoftwares){
        List<Software> softwares = null;
        if(rawSoftwares != null){
            softwares = new ArrayList<Software>();
            for(uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Software rawSoftware: rawSoftwares){
                softwares.add(transformSoftware(rawSoftware));
            }
        }
        return softwares;
    }

    public static Software transformSoftware(uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Software rawSoftware){
        return new Software(null,rawSoftware.getName(),null,null,null,rawSoftware.getVersion());
    }


    public static List<InstrumentConfiguration> transformMsInstrument(List<uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.MsInstrument> rawInstrumentList) {
        List<InstrumentConfiguration> instrumentConfigurations = null;
        if(rawInstrumentList != null){
            instrumentConfigurations = new ArrayList<InstrumentConfiguration>();
            for(uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.MsInstrument msInstrument: rawInstrumentList){
                Software software = transformSoftware(msInstrument.getSoftware());
                List<InstrumentComponent> source = new ArrayList<InstrumentComponent>();
                List<InstrumentComponent> analyzer = new ArrayList<InstrumentComponent>();
                List<InstrumentComponent> detector = new ArrayList<InstrumentComponent>();
                if(msInstrument.getMsIonisation() != null){
                    //Todo: In the future it could be interesting to have something like a probability system to match from an Ontology Term and a CVParam

                }
                if(msInstrument.getMsMassAnalyzer() != null){
                    //Todo: In the future it could be interesting to have something like a probability system to match from an Ontology Term and a CVParam

                }
                if(msInstrument.getMsMassAnalyzer() != null){
                    //Todo: In the future it could be interesting to have something like a probability system to match from an Ontology Term and a CVParam
                }
                InstrumentConfiguration instrumentConfiguration = new InstrumentConfiguration(null, null, software, source, analyzer, detector, null);
                instrumentConfigurations.add(instrumentConfiguration);
            }

        }
        return instrumentConfigurations;
    }

}
