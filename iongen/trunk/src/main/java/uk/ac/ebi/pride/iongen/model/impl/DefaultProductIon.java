package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.Group;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonType;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultProductIon extends AbstractProductIon {
    public DefaultProductIon(PrecursorIon precursorIon, ProductIonType type, int position,
                             Peptide peptide, int charge) {
        super(precursorIon, type, position, peptide, charge);
    }

    public DefaultProductIon(PrecursorIon precursorIon, ProductIonType type, int position,
                             Peptide peptide) {
        super(precursorIon, type, position, peptide, 1);
    }

    public String getName() {
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

    @Override
    public double getMass() {
        double mass = super.getMass();

        switch (getType()) {
            case B:
                mass = mass - Group.H.getMass();
                break;
            case B_H2O:
                mass = mass - Group.H.getMass() - Group.H2O.getMass();
                break;
            case B_NH3:
                mass = mass - Group.H.getMass() - Group.NH3.getMass();
                break;
            case Y:
                mass = mass + Group.H.getMass();
                break;
            case Y_H2O:
                mass = mass + Group.H.getMass() - Group.H2O.getMass();
                break;
            case Y_NH3:
                mass = mass + Group.H.getMass() - Group.NH3.getMass();
                break;
            case A:
                mass = mass - Group.H.getMass() - Group.CO.getMass();
                break;
            case A_H2O:
                mass = mass - Group.H.getMass() - Group.CO.getMass() - Group.H2O.getMass();
                break;
            case A_NH3:
                mass = mass - Group.H.getMass() - Group.CO.getMass() - Group.NH3.getMass();
                break;
            case X:
                mass = mass - Group.H.getMass() + Group.CO.getMass();
                break;
            case X_H2O:
                mass = mass - Group.H.getMass() + Group.CO.getMass() - Group.H2O.getMass();
                break;
            case X_NH3:
                mass = mass - Group.H.getMass() + Group.CO.getMass() - Group.NH3.getMass();
                break;
            case C:
                mass = mass + Group.NH3.getMass() - Group.H.getMass() ;
                break;
            case C_H2O:
                mass = mass + Group.NH3.getMass() - Group.H.getMass() - Group.H2O.getMass();
                break;
            case C_NH3:
                mass = mass + Group.NH3.getMass() - Group.H.getMass() - Group.NH3.getMass();
                break;
            case Z:
                mass = mass - Group.NH.getMass() + Group.H.getMass() ;
                break;
            case Z_H2O:
                mass = mass - Group.NH.getMass() + Group.H.getMass() - Group.H2O.getMass();
                break;
            case Z_NH3:
                mass = mass - Group.NH.getMass() + Group.H.getMass() - Group.NH3.getMass();
                break;
        }

        return mass;
    }

}
