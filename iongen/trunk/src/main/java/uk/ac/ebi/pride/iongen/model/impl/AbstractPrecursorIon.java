package uk.ac.ebi.pride.iongen.model.impl;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.*;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public abstract class AbstractPrecursorIon extends DefaultPeptideIon implements PrecursorIon {
    public AbstractPrecursorIon(Peptide peptide, int charge) {
        super(peptide, charge);
    }

    private Map<Integer, PTModification> getProductIonPTM(int start, int end, Map<Integer, PTModification> precursorPTM) {
        Map<Integer, PTModification> ptm = new HashMap<Integer, PTModification>();


        Iterator<Integer> it = precursorPTM.keySet().iterator();
        Integer position;
        PTModification modification;
        while (it.hasNext()) {
            position = it.next();
            modification = precursorPTM.get(position);

            // [start, end)
            if (position >= start && position < end) {
                ptm.put(position, modification);
            }
        }

        return ptm;
    }

    /**
     * position [1..length)
     */
    @Override
    public ProductIon getProductIon(ProductIonType type, int position, int charge) {
        List<AminoAcid> acidList = getPeptide().getAminoAcids();
        Group n_terminal = getPeptide().getNTerminalGroup();
        Group c_terminal = getPeptide().getCTerminalGroup();
        int length = getPeptide().getLength();

        if (position <= 0) {
            throw new IllegalArgumentException("The product ion cleavages position (" + position + ") cannot be less than 1");
        } else if (position > getPeptide().getLength()) {
            throw new IllegalArgumentException("The product ion cleavages position (" + position + ") cannot be greater then the precursor ion length (" + length + "). ");
        }

        int start;
        int end;
        List<AminoAcid> prodAcidList;
        Peptide prodPeptide = null;
        ProductIon productIon = null;
        Map<Integer, PTModification> ptm;

        FragmentIonType fragmentIonType = type.getGroup();
        if (fragmentIonType.equals(FragmentIonType.A_ION) || fragmentIonType.equals(FragmentIonType.B_ION) || fragmentIonType.equals(FragmentIonType.C_ION)) {
            start = 0;
            end = position;
            prodAcidList = acidList.subList(start, end);
            ptm = getProductIonPTM(start, end, getPeptide().getPTM());
            prodPeptide = new Peptide(prodAcidList, n_terminal, null, ptm);
            productIon = new DefaultProductIon(this, type, position, prodPeptide, charge);
        } else if (fragmentIonType.equals(FragmentIonType.X_ION) || fragmentIonType.equals(FragmentIonType.Y_ION) || fragmentIonType.equals(FragmentIonType.Z_ION)) {
            start = position;
            end = length;
            prodAcidList = acidList.subList(start, end);
            ptm = getProductIonPTM(start, end, getPeptide().getPTM());
            prodPeptide = new Peptide(prodAcidList, null, c_terminal, ptm);
            productIon = new DefaultProductIon(this, type, length - position, prodPeptide, charge);
        } else {
            throw new IllegalArgumentException(type + " is not A, B, C, X, Y, Z ions");
        }

        return productIon;
    }

}
