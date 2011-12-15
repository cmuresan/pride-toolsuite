package uk.ac.ebi.pride.term;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * CvTerms contain a list of controlled vocabularies.
 * <p/>
 * User: rwang, yperez
 * Date: 24-Apr-2010
 * Time: 21:20:49
 */
public enum CvTermReference {

    SCAN_POLARITY("MS", "MS:1000465", "scan polarity", "MS:1000441"),
    NEGATIVE_SCAN("MS", "MS:1000129", "negative scan", "MS:1000441"),
    POSITIVE_SCAN("MS", "MS:1000130", "positive scan", "MS:1000441"),
    SPECTRUM_TYPE("MS", "MS:1000559", "spectrum type", "MS:1000442"),
    CHARGE_INVERSION_MASS_SPECTRUM("MS", "MS:1000322", "charge inversion mass spectrum", "MS:1000559"),
    CONSTANT_NEUTRAL_GAIN_SPECTRUM("MS", "MS:1000325", "constant neutral gain spectrum", "MS:1000559"),
    SPECTRUM_REPRESENTATION("MS", "MS:1000525", "spectrum representation", "MS:1000442"),
    CENTROID_SPECTRUM("MS", "MS:1000127", "centroid spectrum", "MS:1000525"),
    PROFILE_SPECTRUM("MS", "MS:1000128", "profile spectrum", "MS:1000525"),
    SPECTRUM_ATTRIBUTE("MS", "MS:1000499", "spectrum attribute", "MS:1000547;MS:1000442"),
    TOTAl_ION_CURRENT("MS", "MS:1000285", "total ion current", "MS:1000449"),
    MS_LEVEL("MS", "MS:1000511", "ms level", "MS:1000449"),
    SPECTRUM_TITLE("MS", "MS:1000796", "spectrum title", "MS:1000449"),
    BINARY_DATA_COMPRESSION_TYPE("MS", "MS:1000572", "binary data compression type", "MS:1000625;MS:1000442"),
    ZLIB_COMPRESSION("MS", "MS:1000574", "zlib compression", "MS:1000572"),
    NO_COMPRESSION("MS", "MS:1000576", "no compression", "MS:1000513"),
    BINARY_DATA_ARRAY("MS", "MS:1000513", "binary data array", "MS:1000625;MS:1000442"),
    MZ_ARRAY("MS", "MS:1000514", "m/z array", "MS:1000513"),
    INTENSITY_ARRAY("MS", "MS:1000515", "intensity array", "MS:1000513"),
    TIME_ARRAY("MS", "MS:1000595", "time array", "MS:1000513"),
    BINARY_DATA_TYPE("MS", "MS:1000518", "binary data type", "MS:1000625;MS:1000442"),
    FLOAT_16_BIT("MS", "MS:1000520", "16-bit float", "MS:1000518"),
    FLOAT_32_BIT("MS", "MS:1000521", "32-bit float", "MS:1000518"),
    FLOAT_64_BIT("MS", "MS:1000523", "64-bit float", "MS:1000518"),
    INT_32_BIT("MS", "MS:1000519", "32-bit integer", "MS:1000518"),
    INT_64_BIT("MS", "MS:1000522", "64-bit integer", "MS:1000518"),
    DATA_FILE_CONTENT("MS", "MS:1000524", "data file content", "MS:1000577"),
    MASS_SPECTRUM("MS", "MS:1000294", "mass spectrum", "MS:1000524;MS:1000559"),
    CONTACT_NAME("MS", "MS:1000586", "contact name", "MS:1000585"),
    CONTACT_ORG("MS", "MS:1000590", "contact organization", "MS:1000585"),
    CONTACT_EMAIL("MS", "MS:1000589", "contact email", "MS:1000585"),
    CONVERSION_TO_MZML("MS", "MS:1000544", "Conversion to mzML", "MS:1000452"),
    INSTRUMENT_MODEL("MS", "MS:1000031", "instrument model", "MS:1000463"),
    SCAN_WINDOW_LOWER_LIMIT("MS", "MS:1000501", "scan window lower limit", "MS:1000040"),
    SCAN_WINDOW_UPPER_LIMIT("MS", "MS:1000500", "scan window upper limit", "MS:1000549"),
    SUM_OF_SPECTRA("MS", "MS:1000571", "sum of spectra", "MS:1000570"),
    NO_COMBINATION("MS", "MS:1000795", "no combination", "MS:1000570"),
    PRODUCT_ION_MZ("PRIDE", "PRIDE:0000188", "product ion m/z", "PRIDE:0000187"),
    PRODUCT_ION_MZ_PLGS("Water", "PLGS:00024", "product ion m/z", null),
    PRODUCT_ION_INTENSITY("PRIDE", "PRIDE:0000189", "product ion intensity", "PRIDE:0000187"),
    PRODUCT_ION_INTENSITY_PLGS("Water", "PLGS:00025", "product ion intensity", null),
    PRODUCT_ION_MASS_ERROR("PRIDE", "PRIDE:0000190", "product ion mass error", "PRIDE:0000187"),
    PRODUCT_ION_MASS_ERROR_PLGS("Water", "PLGS:00026", "product ion mass error", null),
    PRODUCT_ION_RETENTION_TIME_ERROR("PRIDE", "PRIDE:0000191", "product ion retention time error", "PRIDE:0000187"),
    PRODUCT_ION_RETENTION_TIME_ERROR_PLGS("Water", "PLGS:00027", "product ion retention time error", null),
    PRODUCT_ION_CHARGE("PRIDE", "PRIDE:0000204", "product ion charge", "PRIDE:0000187"),
    PRODUCT_ION_TYPE("PRIDE", "PRIDE:0000192", "product ion type", "PRIDE:0000187"),
    Y_ION("PRIDE", "PRIDE:0000193", "y ion", "PRIDE:0000192"),
    Y_ION_H2O("PRIDE", "PRIDE:0000197", "y ion -H2O", "PRIDE:0000193"),
    Y_ION_NH3("PRIDE", "PRIDE:0000198", "y ion -NH3", "PRIDE:0000193"),
    B_ION("PRIDE", "PRIDE:0000194", "b ion", "PRIDE:0000192"),
    B_ION_H2O("PRIDE", "PRIDE:0000196", "b ion -H2O", "PRIDE:0000194"),
    B_ION_NH3("PRIDE", "PRIDE:0000195", "b ion -NH3", "PRIDE:0000194"),
    IN_SOURCE_ION("PRIDE", "PRIDE:0000199", "in source ion", "PRIDE:0000192"),
    NON_IDENTIFIED_ION("PRIDE", "PRIDE:0000200", "non-identified ion", "PRIDE:0000192"),
    CO_ELUTING_ION("PRIDE", "PRIDE:0000201", "co-eluting ion", "PRIDE:0000192"),
    X_ION("PRIDE", "PRIDE:0000227", "x ion", "PRIDE:0000192"),
    X_ION_H2O("PRIDE", "PRIDE:0000228", "x ion -H2O", "PRIDE:0000227"),
    X_ION_NH3("PRIDE", "PRIDE:0000229", "x ion -NH3", "PRIDE:0000227"),
    Z_ION("PRIDE", "PRIDE:0000230", "z ion", "PRIDE:0000192"),
    Z_ION_H2O("PRIDE", "PRIDE:0000231", "z ion -H2O", "PRIDE:0000230"),
    Z_ION_NH3("PRIDE", "PRIDE:0000232", "z ion -NH3", "PRIDE:0000230"),
    Z_H_ION("PRIDE", "PRIDE:0000280", "zH ion", "PRIDE:0000230"),
    Z_HH_ION("PRIDE", "PRIDE:0000281", "zHH ion", "PRIDE:0000230"),
    A_ION("PRIDE", "PRIDE:0000233", "a ion", "PRIDE:0000192"),
    A_ION_H2O("PRIDE", "PRIDE:0000234", "a ion -H2O", "PRIDE:0000233"),
    A_ION_NH3("PRIDE", "PRIDE:0000235", "a ion -NH3", "PRIDE:0000233"),
    C_ION("PRIDE", "PRIDE:0000236", "c ion", "PRIDE:0000192"),
    C_ION_H2O("PRIDE", "PRIDE:0000237", "c ion -H2O", "PRIDE:0000236"),
    C_ION_NH3("PRIDE", "PRIDE:0000238", "c ion -NH3", "PRIDE:0000236"),
    IMMONIUM_ION("PRIDE", "PRIDE:0000239", "immonium ion", "PRIDE:0000192"),
    IMMONIUM_ION_A("PRIDE", "PRIDE:0000240", "immonium A", "PRIDE:0000239"),
    IMMONIUM_ION_C("PRIDE", "PRIDE:0000241", "immonium C", "PRIDE:0000239"),
    IMMONIUM_ION_D("PRIDE", "PRIDE:0000242", "immonium D", "PRIDE:0000239"),
    IMMONIUM_ION_E("PRIDE", "PRIDE:0000243", "immonium E", "PRIDE:0000239"),
    IMMONIUM_ION_F("PRIDE", "PRIDE:0000244", "immonium F", "PRIDE:0000239"),
    IMMONIUM_ION_G("PRIDE", "PRIDE:0000245", "immonium G", "PRIDE:0000239"),
    IMMONIUM_ION_H("PRIDE", "PRIDE:0000246", "immonium H", "PRIDE:0000239"),
    IMMONIUM_ION_I("PRIDE", "PRIDE:0000247", "immonium I", "PRIDE:0000239"),
    IMMONIUM_ION_K("PRIDE", "PRIDE:0000248", "immonium K", "PRIDE:0000239"),
    IMMONIUM_ION_L("PRIDE", "PRIDE:0000249", "immonium L", "PRIDE:0000239"),
    IMMONIUM_ION_M("PRIDE", "PRIDE:0000250", "immonium M", "PRIDE:0000239"),
    IMMONIUM_ION_N("PRIDE", "PRIDE:0000251", "immonium N", "PRIDE:0000239"),
    IMMONIUM_ION_P("PRIDE", "PRIDE:0000252", "immonium P", "PRIDE:0000239"),
    IMMONIUM_ION_Q("PRIDE", "PRIDE:0000253", "immonium Q", "PRIDE:0000239"),
    IMMONIUM_ION_R("PRIDE", "PRIDE:0000254", "immonium R", "PRIDE:0000239"),
    IMMONIUM_ION_S("PRIDE", "PRIDE:0000255", "immonium S", "PRIDE:0000239"),
    IMMONIUM_ION_T("PRIDE", "PRIDE:0000256", "immonium T", "PRIDE:0000239"),
    IMMONIUM_ION_V("PRIDE", "PRIDE:0000257", "immonium V", "PRIDE:0000239"),
    IMMONIUM_ION_W("PRIDE", "PRIDE:0000258", "immonium W", "PRIDE:0000239"),
    IMMONIUM_ION_Y("PRIDE", "PRIDE:0000259", "immonium Y", "PRIDE:0000239"),
    ION_SELECTION_CHARGE_STATE("MS", "MS:1000041", "charge state", "MS:1000455"),
    ION_SELECTION_MZ("MS", "MS:1000744", "selected ion m/z", "MS:1000455"),
    ION_SELECTION_INTENSITY("MS", "MS:1000042", "peak intensity", "MS:1000455"),
    PSI_ION_SELECTION_CHARGE_STATE("PSI", "PSI:1000041", "Charge State", "PSI:1000455"),
    PSI_ION_SELECTION_MZ("PSI", "PSI:1000040", "Mass To Charge Ratio", "PSI:1000455"),
    PSI_ION_SELECTION_INTENSITY("PSI", "PSI:1000042", "Intensity", "PSI:1000455"),

    /*Definition of most important Search engines parameters from PRIDE */

    X_CORRELATION("PRIDE", "PRIDE:0000013", "X correlation", "PRIDE:0000049"),

    OMSSA_E_VALUE("PRIDE", "PRIDE:0000185", "OMSSA E-value", "PRIDE:0000049"),
    OMSSA_P_VALUE("PRIDE", "PRIDE:0000186", "OMSSA P-value", "PRIDE:0000049"),

    MASCOT_SCORE("PRIDE", "PRIDE:0000069", "Mascot Score", "PRIDE:0000049"),
    MASCOT_EXPECT_VALUE("PRIDE","PRIDE:0000212","Mascot expect value","PRIDE:0000046"),

    XTANDEM_HYPER_SCORE("PRIDE", "PRIDE:0000176", "X!Tandem Hyperscore", "PRIDE:0000049"),
    XTANDEM_EXPECTANCY_SCORE("PRIDE", "PRIDE:0000183", "X|Tandem expectancy score", "PRIDE:0000047"),
    XTANDEM_ZSCORE("PRIDE","PRIDE:0000182","X|Tandem Z score","PRIDE:0000047"),

    SEQUEST_CN("PRIDE","PRIDE:0000052","Cn","PRIDE:0000049"),
    SEQUEST_SCORE("PRIDE", "PRIDE:0000053", "SEQUEST SCORE", "PRIDE:0000049"),
    SEQUEST_DELTA_CN("PRIDE", "PRIDE:0000012", "Delta Cn", "PRIDE:0000049"),

    SPECTRUM_MILL_PEPTIDE_SCORE("PRIDE", "PRIDE:0000177", "SpectrumMill Peptide Score", "PRIDE:0000049"),

    PEPTIDE_PROPHET_DISCRIMINANT_SCORE("PRIDE", "PRIDE:0000138", "Discriminant score","PRIDE:0000049"),
    PEPTIDE_PROPHET_PROBABILITY("PRIDE","PRIDE:0000099", "PeptideProphet probability score","PRIDE:0000049"),

    /*Definition of most important Search engines parameters from MS */
    MS_MASCOT_SCORE("MS", "MS:1001171", "Mascot:score", "MS:1001143"),

    // OMSSA:evalue
    MS_OMSSA_E("MS","MS:1001328", "OMSSA:evalue", "MS:1001143"),

    // MyriMatch:MVH
    MS_MYRIMATCH_MVH("MS","MS:1001589", "MyriMatch:MVH","MS:1001143"),

    // Paragon:score
    MS_PARAGON_SCORE("MS","MS:1001166", "Paragon:score", "MS:1001153"),

    // Phenyx:Score
    MS_PHENYX_SCORE("MS", "MS:1001390", "Phenyx:Score", "MS:1001153"),

    // ProteinExtractor:Score
    MS_PROTEIN_EXTRACTOR_SCORE("MS", "MS:1001507", "ProteinExtractor:Score","MS:1001153"),

    // ProteinLync:Lputer Score
    MS_PROTEIN_LYNC_SCORE("MS", "MS:1001571", "ProteinLynx:Ladder Score","MS:1001143"),

    // ProteinScape:SequestMetaScore
    MS_PROTEINSCAPE_SEQUEST_METASCORE("MS", "MS:1001506", "ProteinScape:SequestMetaScore","MS:1001143"),

    // Sequest:consensus score
    MS_SEQUEST_CONSENSUS_SCORE("MS", "MS:1001163", "Sequest:consensus score", "MS:1001153"),

    // Sonar:Score
    MS_SONAR_SCORE("MS", "MS:1001502", "Sonar:Score", "MS:1001143"),

    // SpectrumMill:Score
    MS_SPECTRUMMILL_SCORE("MS", "MS:1001572", "SpectrumMill:Score", "MS:1001143"),

    // X!Tandem:hyperscore
    MS_XTANDEM_HYPERSCORE("MS", "MS:1001331", "X!Tandem:hyperscore", "MS:1001143"),

    // percolator:score
    MS_PERCULATOR_SCORE("MS", "MS:1001492", "percolator:score", "MS:1001143"),

    PROJECT_NAME("PRIDE", "PRIDE:0000097", "Project", "PRIDE:0000006"),
    EXPERIMENT_DESCRIPTION("PRIDE", "PRIDE:0000040", "Experiment Description", "PRIDE:0000006"),
    PEPTIDE_GLOBAL_FDR("MS", "MS:1001364", "pep:global FDR", "MS:1001405"),
    PROTEIN_GLOBAL_FDR("MS", "MS:1001214", "prot:global FDR", "MS:1001405");


    private final String cvLabel;
    private final String accession;
    private final String name;
    private final String parentAccession;

    private CvTermReference(String cvLabel, String accession, String name, String parentAccession) {
        this.cvLabel = cvLabel;
        this.accession = accession;
        this.name = name;
        this.parentAccession = parentAccession;
    }

    public String getCvLabel() {
        return cvLabel;
    }

    public String getAccession() {
        return accession;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getChildAccessions() {
        Collection<String> results = new ArrayList<String>();
        CvTermReference[] cvTerms = CvTermReference.values();
        for (CvTermReference cv : cvTerms) {
            if (cv.getParentAccessions().contains(accession)) {
                results.add(cv.getAccession());
            }
        }
        return results;
    }

    public Collection<String> getParentAccessions() {
        return Arrays.asList(parentAccession.split(";"));
    }

    /**
     * Get Cv term by accession.
     *
     * @param accession controlled vocabulary accession.
     * @return CvTermReference  Cv term.
     */
    public static CvTermReference getCvRefByAccession(String accession) {
        CvTermReference cvTerm = null;

        CvTermReference[] cvTerms = CvTermReference.values();
        for (CvTermReference cv : cvTerms) {
            if (cv.getAccession().equals(accession)) {
                cvTerm = cv;
            }
        }

        return cvTerm;
    }

    /**
     * Check whether the accession exists in the enum.
     *
     * @param accession controlled vocabulary accession
     * @return boolean  true if exists
     */
    public static boolean hasAccession(String accession) {
        boolean result = false;

        CvTermReference[] cvTerms = CvTermReference.values();
        for (CvTermReference cv : cvTerms) {
            if (cv.getAccession().equals(accession)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Check whether two accessions are parent-child relationship.
     *
     * @param parentAcc parent accession.
     * @param childAcc  child accession.
     * @return boolean  true if it is parent-child relationship.
     */
    public static boolean isChild(String parentAcc, String childAcc) {
        boolean isChild = false;
        CvTermReference childCvTerm = getCvRefByAccession(childAcc);
        if (childCvTerm != null && childCvTerm.getParentAccessions().contains(parentAcc)) {
            isChild = true;
        }
        return isChild;
    }
}