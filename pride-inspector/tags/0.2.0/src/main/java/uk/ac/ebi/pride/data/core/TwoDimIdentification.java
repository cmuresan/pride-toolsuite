package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Two dimensional identification.
 *
 * User: rwang
 * Date: 08-Feb-2010
 * Time: 12:57:42
 */
public class TwoDimIdentification extends Identification {
    /** Gel related details */
    private Gel gel = null;

    /**
     * Constructor
     * @param id    required.
     * @param accession required.
     * @param accessionVersion  optional.
     * @param peptides  required and non empty.
     * @param score optional.
     * @param searchDatabase    required.
     * @param searchDatabaseVersion optional.
     * @param searchEngine  required.
     * @param sequenceCoverage optional ?
     * @param spectrum  optional ?
     * @param spliceIsoform optional.
     * @param threshold optional.
     * @param params    optional.
     * @param gel   required.
     */
    public TwoDimIdentification(Comparable id,
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
                                ParamGroup params,
                                Gel gel) {
        super(id,
              accession,
              accessionVersion,
              peptides,
              score,
              searchDatabase,
              searchDatabaseVersion,
              searchEngine,
              sequenceCoverage,
              spectrum,
              spliceIsoform,
              threshold,
              params);

        setGel(gel);
    }

    public Gel getGel() {
        return gel;
    }

    public void setGel(Gel gel) {
        if (gel == null) {
            throw new IllegalArgumentException("Two dimensional identification's gel can not be NULL");
        } else {
            this.gel = gel;
        }
    }
}
