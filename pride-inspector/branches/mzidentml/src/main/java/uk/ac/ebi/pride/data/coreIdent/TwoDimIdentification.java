package uk.ac.ebi.pride.data.coreIdent;

   import java.util.HashMap;
   import java.util.List;

/**
 * Two dimensional identification.
 * <p/>
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 12:57:42
 */
public class TwoDimIdentification extends Identification {
    /**
     * Gel related details
     */
    private Gel gel = null;

    public TwoDimIdentification(Comparable id, String name, DBSequence dbSequence, boolean passThreshold, HashMap<PeptideEvidence, List<Peptide>> peptides, double score, double threshold, SearchEngine searchEngine, double sequenceCoverage, Gel gel) {
        super(id, name, dbSequence, passThreshold, peptides, score, threshold, searchEngine, sequenceCoverage);
        this.gel = gel;
    }

    public TwoDimIdentification(ParamGroup params, Comparable id, String name, DBSequence dbSequence, boolean passThreshold, HashMap<PeptideEvidence, List<Peptide>> peptides, double score, double threshold, SearchEngine searchEngine, double sequenceCoverage, Gel gel) {
        super(params, id, name, dbSequence, passThreshold, peptides, score, threshold, searchEngine, sequenceCoverage);
        this.gel = gel;
    }

    public Gel getGel() {
        return gel;
    }

    public void setGel(Gel gel) {
        this.gel = gel;
    }
}
