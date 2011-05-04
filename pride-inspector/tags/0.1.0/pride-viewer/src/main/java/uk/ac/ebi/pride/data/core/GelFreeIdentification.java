package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 24-Mar-2010
 * Time: 16:38:43
 */
public class GelFreeIdentification extends Identification {

    public GelFreeIdentification(String accession, String accessionVersion,
                                 List<Peptide> peptides, double score,
                                 String searchDatabase, String searchDatabaseVerison,
                                 String searchEngine, double sequenceConverage,
                                 Spectrum spectrum, String spliceIsoform,
                                 double threshold, ParamGroup params) {
        super(accession, accessionVersion,
              peptides, score,
              searchDatabase, searchDatabaseVerison,
              searchEngine, sequenceConverage,
              spectrum, spliceIsoform,
              threshold, params);
    }
}
