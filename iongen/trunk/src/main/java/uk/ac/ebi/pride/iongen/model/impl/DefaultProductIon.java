package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.Group;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * Default implement of {@link AbstractProductIon}.
 * In tandem mass spectrum, the level 1 mass generate precursor ions. For a special precursor ion, based on
 * product ion type, cleavage position, product ion charge to generate a product ion.
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultProductIon extends AbstractProductIon {
    /**
     * Create product ion instance. There are some constraints among the {@link PrecursorIon},
     * {@link ProductIonType}, cleavage position, and the {@link uk.ac.ebi.pride.iongen.model.ProductIon}.
     * <P>
     *     Notice: Not suggest user to create a instance by calling construct method directly.
     * </P>
     * Reference {@link AbstractPrecursorIon#getProductIon(uk.ac.ebi.pride.mol.ProductIonType, int, int)}
     * see how to generate the product ion.
     *
     * @param precursorIon should not set null! Otherwise throw NullPointerException.
     * @param type should not set null! Otherwise throw NullPointerException.
     * @param position from [1..precursor.peptide.length-1]
     * @param peptide product ion peptide. should not set null! Otherwise throw NullPointerException.
     * @param charge from [1..3], should not great than precursor ion charges.
     */
    public DefaultProductIon(PrecursorIon precursorIon, ProductIonType type, int position,
                             Peptide peptide, int charge) {
        super(precursorIon, type, position, peptide, charge);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int charge = getCharge();

        builder.append(getType().getName());
        if (charge > 1) {
            // if positive charge == 1 do not display +
            for (int i = 1; i <= charge; i++) {
                builder.append('+');
            }
        } else if (charge < 0) {
            for (int i = -1; i >= charge; i--) {
                builder.append('-');
            }
        }

        return builder.toString();
    }



}
