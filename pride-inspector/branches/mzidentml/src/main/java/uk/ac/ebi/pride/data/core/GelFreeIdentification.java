package uk.ac.ebi.pride.data.core;

import java.util.List;
import java.util.Map;

/**
 * Gel free identification.
 * <p/>
 * User: rwang
 * Date: 24-Mar-2010
 * Time: 16:38:43
 */
public class GelFreeIdentification extends Identification {

    public GelFreeIdentification(Comparable id,
                                 String name,
                                 DBSequence dbSequence,
                                 boolean passThreshold,
                                 Map<PeptideEvidence,List<Peptide>> peptides,
                                 double score,
                                 double threshold,
                                 SearchEngine searchEngine,
                                 double sequenceCoverage) {
        super(id, name, dbSequence, passThreshold, peptides, score, threshold, searchEngine, sequenceCoverage);
    }

    public GelFreeIdentification(ParamGroup params,
                                 Comparable id,
                                 String name,
                                 DBSequence dbSequence,
                                 boolean passThreshold,
                                 Map<PeptideEvidence, List<Peptide>> peptides,
                                 double score,
                                 double threshold,
                                 SearchEngine searchEngine,
                                 double sequenceCoverage) {
        super(params, id, name, dbSequence, passThreshold, peptides, score, threshold, searchEngine, sequenceCoverage);
    }


}