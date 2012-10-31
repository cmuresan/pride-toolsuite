package uk.ac.ebi.pride.mol;

import uk.ac.ebi.pride.mol.ion.FragmentIonType;

/**
 * Creator: Qingwei-XU
 * Date: 23/10/12
 */

public enum ProductIonType {
    A("a", FragmentIonType.A_ION, null, ProductIonPair.A_X),
    A_NH3("a_NH3", FragmentIonType.A_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.A_X),
    A_H2O("a_H2O", FragmentIonType.A_ION, NeutralLoss.WATER_LOSS, ProductIonPair.A_X),
    B("b", FragmentIonType.B_ION, null, ProductIonPair.B_Y),
    B_NH3("b_NH3", FragmentIonType.B_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.B_Y),
    B_H2O("b_H2O", FragmentIonType.B_ION, NeutralLoss.WATER_LOSS, ProductIonPair.B_Y),
    C("c", FragmentIonType.C_ION, null, ProductIonPair.C_Z),
    C_NH3("c_NH3", FragmentIonType.C_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.C_Z),
    C_H2O("c_H2O", FragmentIonType.C_ION, NeutralLoss.WATER_LOSS, ProductIonPair.C_Z),
    X("x", FragmentIonType.X_ION, null, ProductIonPair.A_X),
    X_NH3("x_NH3", FragmentIonType.X_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.A_X),
    X_H2O("x_H2O", FragmentIonType.X_ION, NeutralLoss.WATER_LOSS, ProductIonPair.A_X),
    Y("y", FragmentIonType.Y_ION, null, ProductIonPair.B_Y),
    Y_NH3("y_NH3", FragmentIonType.Y_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.B_Y),
    Y_H2O("y_H2O", FragmentIonType.Y_ION, NeutralLoss.WATER_LOSS, ProductIonPair.B_Y),
    Z("z", FragmentIonType.Z_ION, null, ProductIonPair.C_Z),
    Z_NH3("z_NH3", FragmentIonType.Z_ION, NeutralLoss.AMMONIA_LOSS, ProductIonPair.C_Z),
    Z_H2O("z_H2O", FragmentIonType.Z_ION, NeutralLoss.WATER_LOSS, ProductIonPair.C_Z);;
    
    private String name;
    private FragmentIonType group;
    private NeutralLoss loss;
    private ProductIonPair pair;

    private ProductIonType(String name, FragmentIonType group, NeutralLoss loss, ProductIonPair pair) {
        this.name = name;
        this.group = group;
        this.loss = loss;
        this.pair = pair;
    }

    public String getName() {
        return name;
    }

    public FragmentIonType getGroup() {
        return group;
    }

    public NeutralLoss getLoss() {
        return loss;
    }

    public ProductIonPair getPair() {
        return pair;
    }
}
