package uk.ac.ebi.pride.iongen.model;

import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * A kind of {@link PeptideIon}, which have one {@link PrecursorIon}.
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public interface ProductIon extends PeptideIon, Comparable<ProductIon> {
    /**
     *
     * @return {@link ProductIonType}
     */
    public ProductIonType getType();

    /**
     *
     * @return {@link PrecursorIon}
     */
    public PrecursorIon getPrecursorIon();

    /**
     *
     * @return the product ion charge.
     */
    public int getCharge();

    /**
     *
     * @return the cleavage position of product ion.
     */
    public int getPosition();
}
