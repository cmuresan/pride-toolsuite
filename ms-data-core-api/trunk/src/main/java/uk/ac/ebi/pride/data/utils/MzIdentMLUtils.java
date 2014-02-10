package uk.ac.ebi.pride.data.utils;


import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.*;
import uk.ac.ebi.pride.data.core.SpectraData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * MzIdentML utilities class. It contains all functions related with mzidentMl validation
 * file format,
 * <p/>
 * User: yperez
 * Date: 8/14/12
 * Time: 4:17 PM
 */
public final class MzIdentMLUtils {

    private MzIdentMLUtils() {
    }

    public static Constants.SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.pride.data.core.SpectraData spectraData) {
        uk.ac.ebi.pride.data.core.CvParam specIdFormat = spectraData.getSpectrumIdFormat();
        return getSpectraDataIdFormat(specIdFormat.getAccession());
    }

    public static Constants.SpecIdFormat getSpectraDataIdFormat(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData) {
        uk.ac.ebi.jmzidml.model.mzidml.CvParam specIdFormat = spectraData.getSpectrumIDFormat().getCvParam();
        return getSpectraDataIdFormat(specIdFormat.getAccession());
    }

    public static Constants.SpecFileFormat getSpectraDataFormat(uk.ac.ebi.pride.data.core.SpectraData spectraData) {
        uk.ac.ebi.pride.data.core.CvParam specFileFormat = spectraData.getFileFormat();
        if (specFileFormat != null) {
            if (specFileFormat.getAccession().equals("MS:1000613"))
                return Constants.SpecFileFormat.DTA;
            if (specFileFormat.getAccession().equals("MS:1001062"))
                return Constants.SpecFileFormat.MGF;
            if (specFileFormat.getAccession().equals("MS:1000565"))
                return Constants.SpecFileFormat.PKL;
            if (specFileFormat.getAccession().equals("MS:1000584") || specFileFormat.getAccession().equals("MS:1000562"))
                return Constants.SpecFileFormat.MZML;
            if (specFileFormat.getAccession().equals("MS:1000566"))
                return Constants.SpecFileFormat.MZXML;
        }
        return Constants.SpecFileFormat.NONE;
    }

    private static Constants.SpecIdFormat getSpectraDataIdFormat(String accession) {
        if (accession.equals("MS:1001528"))
            return Constants.SpecIdFormat.MASCOT_QUERY_NUM;
        if (accession.equals("MS:1000774"))
            return Constants.SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1000775"))
            return Constants.SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID;
        if (accession.equals("MS:1001530"))
            return Constants.SpecIdFormat.MZML_ID;
        if (accession.equals("MS:1000776"))
            return Constants.SpecIdFormat.SCAN_NUMBER_NATIVE_ID;
        if (accession.equals("MS:1000770"))
            return Constants.SpecIdFormat.WIFF_NATIVE_ID;
        if (accession.equals("MS:1000777"))
            return Constants.SpecIdFormat.MZDATA_ID;
        if(accession.equals(("MS:1000768")))
            return Constants.SpecIdFormat.SPECTRUM_NATIVE_ID;
        return Constants.SpecIdFormat.NONE;
    }

    public static String getSpectrumId(uk.ac.ebi.jmzidml.model.mzidml.SpectraData spectraData, String spectrumID) {
        Constants.SpecIdFormat fileIdFormat = getSpectraDataIdFormat(spectraData);

        if (fileIdFormat == Constants.SpecIdFormat.MASCOT_QUERY_NUM) {
            String rValueStr = spectrumID.replaceAll("query=", "");
            String id = null;
            if(rValueStr.matches(Constants.INTEGER)){
                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
            }
            return id;
        } else if (fileIdFormat == Constants.SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID) {
            String rValueStr = spectrumID.replaceAll("index=", "");
            String id = null;
            if(rValueStr.matches(Constants.INTEGER)){
                id = Integer.toString(Integer.parseInt(rValueStr) + 1);
            }
            return id;
        } else if (fileIdFormat == Constants.SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
            return spectrumID.replaceAll("file=", "");
        } else if (fileIdFormat == Constants.SpecIdFormat.MZML_ID) {
            return spectrumID.replaceAll("mzMLid=", "");
        } else if (fileIdFormat == Constants.SpecIdFormat.SCAN_NUMBER_NATIVE_ID) {
            return spectrumID.replaceAll("scan=", "");
        } else {
            return spectrumID;
        }
    }

    public static List<Constants.SpecFileFormat> getFileTypeSupported(SpectraData spectraData) {
        List<Constants.SpecFileFormat> fileFormats = new ArrayList<Constants.SpecFileFormat>();
        Constants.SpecFileFormat spectraDataFormat = MzIdentMLUtils.getSpectraDataFormat(spectraData);
        if (spectraDataFormat == Constants.SpecFileFormat.NONE) {
            Constants.SpecIdFormat spectIdFormat = MzIdentMLUtils.getSpectraDataIdFormat(spectraData);
            if (spectIdFormat == Constants.SpecIdFormat.MASCOT_QUERY_NUM) {
                fileFormats.add(Constants.SpecFileFormat.MGF);
            } else if (spectIdFormat == Constants.SpecIdFormat.MULTI_PEAK_LIST_NATIVE_ID) {
                fileFormats.add(Constants.SpecFileFormat.NONE);
                fileFormats.add(Constants.SpecFileFormat.DTA);
                fileFormats.add(Constants.SpecFileFormat.MGF);
                fileFormats.add(Constants.SpecFileFormat.PKL);
            } else if (spectIdFormat == Constants.SpecIdFormat.SINGLE_PEAK_LIST_NATIVE_ID) {
                fileFormats.add(Constants.SpecFileFormat.NONE);
                fileFormats.add(Constants.SpecFileFormat.DTA);
                fileFormats.add(Constants.SpecFileFormat.PKL);
            } else if (spectIdFormat == Constants.SpecIdFormat.MZML_ID) {
                fileFormats.add(Constants.SpecFileFormat.MZML);
            } else if (spectIdFormat == Constants.SpecIdFormat.SCAN_NUMBER_NATIVE_ID) {
                fileFormats.add(Constants.SpecFileFormat.MZXML);
            } else if (spectIdFormat == Constants.SpecIdFormat.MZDATA_ID) {
                fileFormats.add(Constants.SpecFileFormat.MZDATA);
            }
        } else {
            fileFormats.add(spectraDataFormat);
        }
        return fileFormats;
    }

    /**
     * Check the file type
     *
     * @param file input file
     * @return Class    the class type of the data access controller
     */

    public static Class getFileType(File file) {
        Class classType = null;

        // check file type
        if (MzMLControllerImpl.isValidFormat(file)) {
            classType = MzMLControllerImpl.class;
        } else if (PrideXmlControllerImpl.isValidFormat(file)) {
            classType = PrideXmlControllerImpl.class;
        } else if (MzIdentMLControllerImpl.isValidFormat(file)) {
            classType = MzIdentMLControllerImpl.class;
        } else if (MzXmlControllerImpl.isValidFormat(file)) {
            classType = MzXmlControllerImpl.class;
        } else if (MzDataControllerImpl.isValidFormat(file)) {
            classType = MzDataControllerImpl.class;
        } else if (PeakControllerImpl.isValidFormat(file) != null) {
            classType = PeakControllerImpl.class;
        }
        return classType;
    }
}
