package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 12:57:42
 */
public class TwoDimIdentification extends Identification {
    private Gel gel = null;

    public TwoDimIdentification(String accession, String accessionVersion, List<Peptide> peptides,
                                double score, String searchDatabase, String searchDatabaseVerison,
                                String searchEngine, double sequenceConverage, Spectrum spectrum,
                                String spliceIsoform, double threshold, ParamGroup params, Gel gel) {
        super(accession, accessionVersion, peptides,
              score, searchDatabase, searchDatabaseVerison,
              searchEngine, sequenceConverage, spectrum,
              spliceIsoform, threshold, params);
        this.gel = gel;
    }

    public Gel getGel() {
        return gel;
    }

    public void setGel(Gel gel) {
        this.gel = gel;
    }
}
