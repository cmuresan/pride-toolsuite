package uk.ac.ebi.pride.data.utils;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class Constants {

    private Constants() {
    }

    public static final String NOT_AVAILABLE = "N/A";
    public static final String MGF_EXT = ".mgf";
    public static final String DTA_EXT = ".dta";
    public static final String MS2_EXT = ".ms2";
    public static final String PKL_EXT = ".pkl";
    public static final String MZXML = ".mzxml";

    /**
     * Supported id format used in the spectrum file.
     */
    public static enum SpecIdFormat {
        MASCOT_QUERY_NUM,
        MULTI_PEAK_LIST_NATIVE_ID,
        SINGLE_PEAK_LIST_NATIVE_ID,
        SCAN_NUMBER_NATIVE_ID,
        MZML_ID,
        MZDATA_ID,
        WIFF_NATIVE_ID,
        NONE
    }

    /**
     * An enum of the supported spectra file types
     */
    public static enum SpecFileFormat {
        MZML,
        PKL,
        DTA,
        MGF,
        MZXML,
        MZDATA,
        NONE
    }

    public static SpecFileFormat getSpecFileFormat(String fileFormat) {
        if (fileFormat != null && fileFormat.length() > 0) {
            if (SpecFileFormat.MZXML.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZXML;
            if (SpecFileFormat.DTA.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.DTA;
            if (SpecFileFormat.MGF.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MGF;
            if (SpecFileFormat.MZDATA.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZDATA;
            if (SpecFileFormat.MZML.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.MZML;
            if (SpecFileFormat.PKL.toString().equalsIgnoreCase(fileFormat))
                return SpecFileFormat.PKL;
        }
        return SpecFileFormat.NONE;
    }


}
