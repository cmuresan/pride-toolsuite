package uk.ac.ebi.pride.iongen.model.impl;


import uk.ac.ebi.pride.iongen.model.PeptideIon;
import uk.ac.ebi.pride.mol.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class DefaultPeptideIon implements PeptideIon {
    private Peptide peptide;
    private int charge;
    private double mass;

    private double calculateMass() {
        double mass = 0;
        Group c_terminal = this.peptide.getCTerminalGroup();
        Group n_terminal = this.peptide.getNTerminalGroup();
        List<AminoAcid> acidList = this.peptide.getAminoAcids();

        mass += c_terminal == null ? 0 : c_terminal.getMass();
        for (AminoAcid acid : acidList) {
            mass += acid.getMonoMass();
        }
        mass += n_terminal == null ? 0 : n_terminal.getMass();

        //calculate modifications
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

    /**
     * Create a peptide ion.
     * @param peptide can not set null, otherwise throw NullPointerException.
     * @param charge
     */
    public DefaultPeptideIon(Peptide peptide, int charge) {
        if (peptide == null) {
            throw new NullPointerException("Peptide is null!");
        }

        this.peptide = peptide;
        this.charge = charge;
        this.mass = calculateMass();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultPeptideIon that = (DefaultPeptideIon) o;

        if (charge != that.charge) return false;
        if (Double.compare(that.mass, mass) != 0) return false;
        if (peptide != null ? !peptide.equals(that.peptide) : that.peptide != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = peptide != null ? peptide.hashCode() : 0;
        result = 31 * result + charge;
        temp = mass != +0.0d ? Double.doubleToLongBits(mass) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DefaultPeptideIon{" +
                "peptide=" + peptide +
                ", charge=" + charge +
                ", mass=" + mass +
                '}';
    }
}
