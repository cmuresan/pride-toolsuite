package uk.ac.ebi.pride.iongen.model;

import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public interface PrecursorIon extends PeptideIon {
    /**
     * precursor ion cleavage some product ions.
     * @param position: cleavage position.
     */
    public ProductIon getProductIon(ProductIonType type, int position, int charge);
}
