package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Gel free identification.
 * <p/>
 * User: rwang
 * Date: 24-Mar-2010
 * Time: 16:38:43
 */
public class GelFreeIdentification extends Identification {

    /**
     *
     * @param id
     * @param name
     * @param dbSequence
     * @param passThreshold
     * @param peptides
     * @param peptideEvidence
     * @param score
     * @param threshold
     * @param searchEngine
     * @param sequenceCoverage
     */

    public GelFreeIdentification(Comparable id, String name, DBSequence dbSequence, boolean passThreshold, List<Peptide> peptides, PeptideEvidence peptideEvidence, double score, double threshold, String searchEngine, double sequenceCoverage) {
        super(id, name, dbSequence, passThreshold, peptides, peptideEvidence, score, threshold, searchEngine, sequenceCoverage);
    }

    /**
     *
     * @param params
     * @param id
     * @param name
     * @param dbSequence
     * @param passThreshold
     * @param peptides
     * @param peptideEvidence
     * @param score
     * @param threshold
     * @param searchEngine
     * @param sequenceCoverage
     */
    public GelFreeIdentification(ParamGroup params, Comparable id, String name, DBSequence dbSequence, boolean passThreshold, List<Peptide> peptides, PeptideEvidence peptideEvidence, double score, double threshold, String searchEngine, double sequenceCoverage) {
        super(params, id, name, dbSequence, passThreshold, peptides, peptideEvidence, score, threshold, searchEngine, sequenceCoverage);
    }
}
