package uk.ac.ebi.pride.gui.component.table.model;

/**
 * This enum is used to indicate the type of the table content.
 *
 * This enum is created for table models to listen to different type incoming data content,
 * and filter out the irrelevant ones.
 *
 * User: rwang
 * Date: 14-Sep-2010
 * Time: 11:38:09
 */
public enum TableContentType {
    /**
     * spectrum table
     */
    SPECTRUM,
    /**
     * chromatogram table
     */
    CHROMATOGRAM,
    /**
     * identification table
     */
    IDENTIFICATION,
    /**
     * peptide table
     */
    PEPTIDE,
    /**
     * ptm table
     */
    PTM,
    /**
     * Review table
     */
    REVIEW,
    /**
     * Protein details
     */
    PROTEIN_DETAILS,
    /**
     * Protein sequence coverage
     */
    PROTEIN_SEQUENCE_COVERAGE,

    /**
     * whether peptide fit the protein sequence
     */
    PEPTIDE_FIT
}
