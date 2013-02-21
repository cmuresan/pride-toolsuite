package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for both GelFreeIdentification and TwoDimIdentification
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 12:21:57
 */
public class Protein extends IdentifiableParamGroup {

    /**
     * DEB Sequence
     */
    private DBSequence dbSequence = null;

    /**
     * Pass Threshold of the Search Engine
     */
    private boolean passThreshold = false;

    /**
     * Peptide Identifications
     */
    private List<Peptide> peptides = null;

    /**
     * The score is the score value in a SearchEngine Context
     */

    private Score score = null;

    /**
     * percentage of sequence coverage obtained through all identified peptides/masses
     */
    private double sequenceCoverage = -1;

    /**
     * optional search engine threshold
     */
    private double threshold = -1;

    /**
     * Gel related details
     */
    private Gel gel = null;

    /**
     * The group in wich we can find the Protein Identification
     */
    IdentifiableParamGroup proteinAmbiguityGroup = null;

    public Protein(Comparable id, String name, DBSequence dbSequence, boolean passThreshold,
                   List<Peptide> peptides, Score score, double threshold, double sequenceCoverage, Gel gel) {
        this(null, id, name, dbSequence, passThreshold, peptides, score, threshold, sequenceCoverage, gel);
    }

    public Protein(ParamGroup params, Comparable id, String name, DBSequence dbSequence, boolean passThreshold,
                   List<Peptide> peptides, Score score, double threshold, double sequenceCoverage, Gel gel) {
        super(params, id, name);
        this.dbSequence       = dbSequence;
        this.passThreshold    = passThreshold;
        this.peptides         = peptides;
        this.score            = score;
        this.threshold        = threshold;
        this.sequenceCoverage = sequenceCoverage;
        this.gel = gel;
    }

    public DBSequence getDbSequence() {
        return dbSequence;
    }

    public void setDbSequence(DBSequence dbSequence) {
        this.dbSequence = dbSequence;
    }

    public boolean isPassThreshold() {
        return passThreshold;
    }

    public void setPassThreshold(boolean passThreshold) {
        this.passThreshold = passThreshold;
    }

    public List<Peptide> getPeptides() {
        return peptides;
    }

    public void setPeptides(List<Peptide> peptides) {
        this.peptides = peptides;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getSequenceCoverage() {
        return sequenceCoverage;
    }

    public void setSequenceCoverage(double sequenceCoverage) {
        this.sequenceCoverage = sequenceCoverage;
    }

    public List<PeptideSequence> getPeptidesSequence() {
        ArrayList<PeptideSequence> result = new ArrayList<PeptideSequence>();
        List<Peptide> identifiedPeptideList = this.getPeptides();

        for (Peptide peptide : identifiedPeptideList) {
            result.add(peptide.getPeptideEvidence().getPeptideSequence());
        }
        return result;
    }

    public Gel getGel() {
        return gel;
    }

    public void setGel(Gel gel) {
        this.gel = gel;
    }

    public IdentifiableParamGroup getProteinAmbiguityGroup() {
        return proteinAmbiguityGroup;
    }

    public void setProteinAmbiguityGroup(IdentifiableParamGroup proteinAmbiguityGroup) {
        this.proteinAmbiguityGroup = proteinAmbiguityGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Protein protein = (Protein) o;

        if (passThreshold != protein.passThreshold) return false;
        if (Double.compare(protein.sequenceCoverage, sequenceCoverage) != 0) return false;
        if (Double.compare(protein.threshold, threshold) != 0) return false;
        if (dbSequence != null ? !dbSequence.equals(protein.dbSequence) : protein.dbSequence != null) return false;
        if (gel != null ? !gel.equals(protein.gel) : protein.gel != null) return false;
        if (peptides != null ? !peptides.equals(protein.peptides) : protein.peptides != null) return false;
        if (proteinAmbiguityGroup != null ? !proteinAmbiguityGroup.equals(protein.proteinAmbiguityGroup) : protein.proteinAmbiguityGroup != null)
            return false;
        if (score != null ? !score.equals(protein.score) : protein.score != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (dbSequence != null ? dbSequence.hashCode() : 0);
        result = 31 * result + (passThreshold ? 1 : 0);
        result = 31 * result + (peptides != null ? peptides.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        temp = sequenceCoverage != +0.0d ? Double.doubleToLongBits(sequenceCoverage) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = threshold != +0.0d ? Double.doubleToLongBits(threshold) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (gel != null ? gel.hashCode() : 0);
        result = 31 * result + (proteinAmbiguityGroup != null ? proteinAmbiguityGroup.hashCode() : 0);
        return result;
    }
}



