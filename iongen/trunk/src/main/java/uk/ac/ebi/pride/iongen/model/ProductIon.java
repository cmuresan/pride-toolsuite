package uk.ac.ebi.pride.iongen.model;

import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public interface ProductIon extends PeptideIon, Comparable<ProductIon> {
    public ProductIonType getType();

    public PrecursorIon getPrecursorIon();

    public int getCharge();

    public int getPosition();

    public String getName();
}
