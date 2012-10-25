package uk.ac.ebi.pride.iongen.model.impl;


import uk.ac.ebi.pride.iongen.model.PeptideIon;
import uk.ac.ebi.pride.mol.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultPeptideIon implements PeptideIon {
    private Peptide peptide;
    private int charge;
    private double mass;

    private double calcuteMass() {
        double mass = 0;
        Group c_terminal = this.peptide.getCTerminalGroup();
        Group n_terminal = this.peptide.getNTerminalGroup();
        List<AminoAcid> acidList = this.peptide.getAminoAcids();

        mass += c_terminal == null ? 0 : c_terminal.getMass();
        for (AminoAcid acid : acidList) {
            mass += acid.getMonoMass();
        }
        mass += n_terminal == null ? 0 : n_terminal.getMass();

        //calcute modifications
        Map<Integer, PTModification> ptm = peptide.getPTM();
        Integer position;
        PTModification modification;
        Iterator<Integer> it = ptm.keySet().iterator();
        while (it.hasNext()) {
            position = it.next();
            modification = ptm.get(position);

            mass += modification.getMonoMassDeltas().get(0);
        }

        return mass;
    }

    public DefaultPeptideIon(Peptide peptide, int charge) {
        if (peptide == null) {
            throw new IllegalArgumentException("Peptide is null!");
        }

        this.peptide = peptide;
        this.charge = charge;
        this.mass = calcuteMass();
    }

    @Override
    public double getMassOverCharge() {
        double peptideMass = getMass();

        if (charge == 0) {
            return peptideMass;
        }

        //calculate ion mass by adding/substracting protons
        double ionMass = peptideMass + charge * Element.H.getMass();
        int z = charge < 0 ? charge * -1 : charge;

        return ionMass / z;
    }

    @Override
    public int getCharge() {
        return charge;
    }

    @Override
    public double getMass() {
        return this.mass;
    }

    public Peptide getPeptide() {
        return peptide;
    }

}
