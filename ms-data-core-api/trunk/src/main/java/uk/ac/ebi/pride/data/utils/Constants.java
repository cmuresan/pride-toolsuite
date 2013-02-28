package uk.ac.ebi.pride.data.utils;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class Constants {

    private Constants(){}

    public static final String NOT_AVAILABLE = "N/A";
    public static final String MGF_EXT = ".mgf";
    public static final String DTA_EXT = ".dta";
    public static final String MS2_EXT = ".ms2";
    public static final String PKL_EXT = ".pkl";

    /**
     *  Supported id format used in the spectrum file.
     *
     **/
    public static enum SpecIdFormat{ MASCOT_QUERY_NUM, MULTI_PEAK_LIST_NATIVE_ID, SINGLE_PEAK_LIST_NATIVE_ID, MZML_ID, NONE
    }
    /**
     * An enum of the supported spectra file types
     *
     **/
    public static enum SpecFileFormat {
        MZML, PKL, DTA, MGF, NONE
    }


}
