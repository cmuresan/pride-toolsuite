package uk.ac.ebi.pride.data.coreIdent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract class for both GelFreeIdentification and TwoDimIdentification
 * ToDo: this is a replica of PRIDE XML data model, additional support is needed for mzIdentML
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:21:57
 */
public abstract class Identification extends IdentifiableParamGroup{
    /**
     * DEB Sequence
     */
    DBSequence dbSequence = null;
    /**
     * Pass Threshold of the Search Engine
     */
    boolean passThreshold = false;
    /**
     * Peptide Evidence in which is based the identification
     */
    private HashMap<PeptideEvidence,List<Peptide>> peptides = null;
    /**
     * optional search engine score
     */
    private double score = -1;
    /**
     * optional search engine threshold
     */
    private double threshold = -1;
    /**
     * search engine name
     */
    private String searchEngine = null;
    /**
     * percentage of sequence coverage obtained through all identified peptides/masses
     */
    private double sequenceCoverage = -1;

    public Identification(Comparable id,
                          String name,
                          DBSequence dbSequence,
                          boolean passThreshold,
                          HashMap<PeptideEvidence,List<Peptide>> peptides,
                          double score,
                          double threshold,
                          String searchEngine,
                          double sequenceCoverage) {
        super(id, name);
        this.dbSequence = dbSequence;
        this.passThreshold = passThreshold;
        this.peptides = peptides;
        this.score = score;
        this.threshold = threshold;
        this.searchEngine = searchEngine;
        this.sequenceCoverage = sequenceCoverage;
    }

    public Identification(ParamGroup params,
                          Comparable id,
                          String name,
                          DBSequence dbSequence,
                          boolean passThreshold,
                          HashMap<PeptideEvidence,List<Peptide>> peptides,
                          double score,
                          double threshold,
                          String searchEngine,
                          double sequenceCoverage) {
        super(params, id, name);
        this.dbSequence = dbSequence;
        this.passThreshold = passThreshold;
        this.peptides = peptides;

        this.score = score;
        this.threshold = threshold;
        this.searchEngine = searchEngine;
        this.sequenceCoverage = sequenceCoverage;
    }

    public DBSequence getDbSequence() {
        return dbSequence;
    }

    public void setDbSequenceRef(DBSequence dbSequence) {
        this.dbSequence = dbSequence;
    }

    public boolean isPassThreshold() {
        return passThreshold;
    }

    public void setPassThreshold(boolean passThreshold) {
        this.passThreshold = passThreshold;
    }

    public HashMap<PeptideEvidence,List<Peptide>> getPeptides() {
        return peptides;
    }

    public void setPeptides(HashMap<PeptideEvidence,List<Peptide>> peptides) {
        this.peptides = peptides;
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

    public List<PeptideSequence> getPeptidesSequence(){
        ArrayList<PeptideSequence> result = new ArrayList<PeptideSequence>();
        return result;
    }
}
