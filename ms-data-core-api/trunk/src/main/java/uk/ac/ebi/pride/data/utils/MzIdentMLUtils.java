package uk.ac.ebi.pride.data.utils;


import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.*;

import java.io.File;

/**
 * MzIdentML utilities class. It contains all functions related with mzidentMl validation
 * file format,
 *
 * User: yperez
 * Date: 8/14/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class MzIdentMLUtils {



    public static Constants.SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.pride.data.core.SpectraData spectraData){
        uk.ac.ebi.pride.data.core.CvParam specIdFormat = spectraData.getSpectrumIdFormat();
        return getSpectraDataIdFormat(specIdFormat.getAccession());
    }

    public static Constants.SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData){
        uk.ac.ebi.jmzidml.model.mzidml.CvParam specIdFormat = spectraData.getSpectrumIDFormat().getCvParam();
        return getSpectraDataIdFormat(specIdFormat.getAccession());
    }

    public static Constants.SpecFileFormat getSpectraDataFormat(uk.ac.ebi.pride.data.core.SpectraData spectraData){
        uk.ac.ebi.pride.data.core.CvParam specFileFormat = spectraData.getFileFormat();
        if (specFileFormat.getAccession().equals("MS:1000613"))
            return Constants.SpecFileFormat.DTA;
        if (specFileFormat.getAccession().equals("MS:1001062"))
            return Constants.SpecFileFormat.MGF;
        if (specFileFormat.getAccession().equals("MS:1000565"))
            return Constants.SpecFileFormat.PKL;
        if (specFileFormat.getAccession().equals("MS:1000584"))
            return Constants.SpecFileFormat.MZML;
        return Constants.SpecFileFormat.NONE;
    }

    private static Constants.SpecIdFormat getSpectraDataIdFormat(String accession){
        if (accession.equals("MS:1001528"))
            return Constants.SpecIdFormat.MASCOT_QUERY_NUM;
        if (accession.equals("MS:1000774"))
            return Constants.SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1000775"))
            return Constants.SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1001530"))
            return Constants.SpecIdFormat.MZML_ID;
        return Constants.SpecIdFormat.NONE;
    }


    public static String getSpectrumId(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData, String spectrumID) {
        Constants.SpecIdFormat fileIdFormat = getSpectraDataIdFormat(spectraData);
        if(fileIdFormat == Constants.SpecIdFormat.MASCOT_QUERY_NUM){
            return Integer.toString(Integer.parseInt(spectrumID.replaceAll("query=", "")) + 1);
        }else if(fileIdFormat == Constants.SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID){
            return Integer.toString(Integer.parseInt(spectrumID.replaceAll("index=", "")) + 1);
        }else if(fileIdFormat == Constants.SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID){
            return spectrumID.replaceAll("file=","");
        }else if(fileIdFormat == Constants.SpecIdFormat.MZML_ID){
            return spectrumID.replaceAll("mzMLid=","");
        }
        return null;
    }

    /**
     * Check the file type
     * @param file input file
     * @return Class    the class type of the data access controller
     **/

    public static Class getFileType(File file){
        Class classType = null;

        // check file type
        if (MzMLControllerImpl.isValidFormat(file)) {
            classType = MzMLControllerImpl.class;
        }else if (PrideXmlControllerImpl.isValidFormat(file)) {
            classType = PrideXmlControllerImpl.class;
        }else if(MzIdentMLControllerImpl.isValidFormat(file)){
            classType = MzIdentMLControllerImpl.class;
        }else if(MzXmlControllerImpl.isValidFormat(file)){
            classType = MzXmlControllerImpl.class;
        }else if(MzDataControllerImpl.isValidFormat(file)){
            classType = MzDataControllerImpl.class;
        }else if(PeakControllerImpl.isValidFormat(file) != null){
            classType = PeakControllerImpl.class;
        }
        return classType;
    }
}
