package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public abstract class AbstractProductIon extends DefaultPeptideIon implements ProductIon, Comparable<ProductIon> {
    private PrecursorIon precursorIon;
    private ProductIonType type;
    private int position;

    public AbstractProductIon(PrecursorIon precursorIon, ProductIonType type, int position, Peptide peptide, int charge) {
        super(peptide, charge);

        // if precursor ion charge is 1, one of the product ion charge is 1, the other is 0.
        // if precursor ion charge great than 1, the product ion charges up to (n-1)
        int precursorZ = precursorIon.getCharge() > 0 ? precursorIon.getCharge() : precursorIon.getCharge() * -1;
        int productZ = charge > 0 ? charge : charge * -1;

        if (precursorZ <= 0) {
            throw new IllegalArgumentException("precursor ion charge should be great than zero! " + precursorIon);
        }

        if (precursorZ > 0 && productZ > precursorZ) {
            throw new IllegalArgumentException("product ion change (" + productZ + ") should not great than precursor ion (" + precursorZ + ")");
        }

        if (productZ > 3) {
            throw new IllegalArgumentException("product ion change (" + productZ + ") should not great than 3!");
        }

        this.precursorIon = precursorIon;
        this.type = type;
        this.position = position;
    }

    @Override
    public ProductIonType getType() {
        return type;
    }

    public PrecursorIon getPrecursorIon() {
        return precursorIon;
    }

    @Override
    public int getPosition() {
        return position;
    }

    /**
     * Based on product ion's position. The ascendent order is 1..length-1.
     */
    @Override
    public int compareTo(ProductIon o) {
        return getPosition() - o.getPosition();
    }
}
