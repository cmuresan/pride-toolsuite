package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * ToDo: this is a replica of PRIDE XML data model, additional support is needed for mzIdentML
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:21:57
 */
public class Identification extends ParamGroup {
    /** protein acdession from the search database */
    private String accession = null;
    /** protein accession version */
    private String accessionVersion = null;
    /** optional splice isoform */
    private String spliceIsoform =  null;
    /** search database name */
    private String searchDatabase = null;
    /** search database version */
    private String searchDatabaseVerison = null;
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
    private double sequenceConverage = -1;

    public Identification(String accession, String accessionVersion, List<Peptide> peptides,
                          double score, String searchDatabase, String searchDatabaseVerison,
                          String searchEngine, double sequenceConverage, Spectrum spectrum,
                          String spliceIsoform, double threshold, ParamGroup params) {
        super(params);
        this.accession = accession;
        this.accessionVersion = accessionVersion;
        this.peptides = peptides;
        this.score = score;
        this.searchDatabase = searchDatabase;
        this.searchDatabaseVerison = searchDatabaseVerison;
        this.searchEngine = searchEngine;
        this.sequenceConverage = sequenceConverage;
        this.spectrum = spectrum;
        this.spliceIsoform = spliceIsoform;
        this.threshold = threshold;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getAccessionVersion() {
        return accessionVersion;
    }

    public void setAccessionVersion(String accessionVersion) {
        this.accessionVersion = accessionVersion;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSearchDatabase() {
        return searchDatabase;
    }

    public void setSearchDatabase(String searchDatabase) {
        this.searchDatabase = searchDatabase;
    }

    public String getSearchDatabaseVerison() {
        return searchDatabaseVerison;
    }

    public void setSearchDatabaseVerison(String searchDatabaseVerison) {
        this.searchDatabaseVerison = searchDatabaseVerison;
    }

    public String getSearchEngine() {
        return searchEngine;
    }

    public void setSearchEngine(String searchEngine) {
        this.searchEngine = searchEngine;
    }

    public double getSequenceConverage() {
        return sequenceConverage;
    }

    public void setSequenceConverage(double sequenceConverage) {
        this.sequenceConverage = sequenceConverage;
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getSpliceIsoform() {
        return spliceIsoform;
    }

    public void setSpliceIsoform(String spliceIsoform) {
        this.spliceIsoform = spliceIsoform;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
