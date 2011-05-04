package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Abstract class for both GelFreeIdentification and TwoDimIdentification
 * ToDo: this is a replica of PRIDE XML data model, additional support is needed for mzIdentML
 * 
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:21:57
 */
public abstract class Identification extends ParamGroup {
    /**
     * This is a unique id for identification,
     * Note: accession is not unique within the same experiment
     */
    private String id;
    /** protein acdession from the search database */
    private String accession = null;
    /** protein accession version */
    private String accessionVersion = null;
    /** optional splice isoform */
    private String spliceIsoform =  null;
    /** search database name */
    private String searchDatabase = null;
    /** search database version */
    private String searchDatabaseVersion = null;
    /** peptides used for identification */
    private List<Peptide> peptides = null;
    /** spectrum */
    private Spectrum spectrum = null;
    /** optional search engine score */
    private double score = -1;
    /** optional search engine threshold */
    private double threshold = -1;
    /** search engine name */
    private String searchEngine = null;
    /** percentage of sequence coverage obtained through all identified peptides/masses */
    private double sequenceCoverage = -1;

    /**
     * Constructor
     * @param id    required.
     * @param accession required.
     * @param accessionVersion  optional.
     * @param peptides  optional.
     * @param score optional.
     * @param searchDatabase    required.
     * @param searchDatabaseVersion optional.
     * @param searchEngine  optional.
     * @param sequenceCoverage optional
     * @param spectrum  optional.
     * @param spliceIsoform optional.
     * @param threshold optional.
     * @param params    optional.
     */
    public Identification(String id,
                          String accession,
                          String accessionVersion,
                          List<Peptide> peptides,
                          double score,
                          String searchDatabase,
                          String searchDatabaseVersion,
                          String searchEngine,
                          double sequenceCoverage,
                          Spectrum spectrum,
                          String spliceIsoform,
                          double threshold,
                          ParamGroup params) {
        super(params);
        setId(id);
        setAccession(accession);
        setAccessionVersion(accessionVersion);
        setPeptides(peptides);
        setScore(score);
        setSearchDatabase(searchDatabase);
        setSearchDatabaseVersion(searchDatabaseVersion);
        setSearchEngine(searchEngine);
        setSequenceCoverage(sequenceCoverage);
        setSpectrum(spectrum);
        setSpliceIsoform(spliceIsoform);
        setThreshold(threshold);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null ) {
            throw new IllegalArgumentException("Identification Id can not be null");
        } else {
            this.id = id;
        }
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        if (accession ==  null) {
            throw new IllegalArgumentException("Identification accession can not be NULL");
        } else {
            this.accession = accession;
        }
    }

    public String getAccessionVersion() {
        return accessionVersion;
    }

    public void setAccessionVersion(String accessionVersion) {
        this.accessionVersion = accessionVersion;
    }

    public String getSpliceIsoform() {
        return spliceIsoform;
    }

    public void setSpliceIsoform(String spliceIsoform) {
        this.spliceIsoform = spliceIsoform;
    }

    public String getSearchDatabase() {
        return searchDatabase;
    }

    public void setSearchDatabase(String searchDatabase) {
        if (searchDatabase ==  null) {
            throw new IllegalArgumentException("Identification's search database can not be NULL");
        } else {
            this.searchDatabase = searchDatabase;
        }
    }

    public String getSearchDatabaseVersion() {
        return searchDatabaseVersion;
    }

    public void setSearchDatabaseVersion(String searchDatabaseVersion) {
        this.searchDatabaseVersion = searchDatabaseVersion;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }

    /**
     * This is convenient method for accessing peptide.
     *
     * @param index zero based index.
     * @return Peptide  peptide.
     */
    public Peptide getPeptide(int index) {
        Peptide peptide = null;
        if (peptides != null)  {
            peptide = peptides.get(index);
        }

        return peptide;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public String getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    public double getSequenceCoverage() {
        return sequenceCoverage;
    }

    public void setSequenceCoverage(double sequenceCoverage) {
        this.sequenceCoverage = sequenceCoverage;
    }

    public int getNumberOfPeptides() {
        return peptides == null ? 0 : peptides.size();
    }
}
