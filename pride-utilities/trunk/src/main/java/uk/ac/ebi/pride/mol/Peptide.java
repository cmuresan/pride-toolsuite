package uk.ac.ebi.pride.mol;

import java.util.*;

/**
 * A peptide including three parts: N terminal group, C terminal group and a couple of AminoAcids.
 * In this class, we provide two mainly constructor methods to create a peptide, but there are
 * a litter different between them. If user create a peptide instance by using sequence (String)
 * argument, we will create a couple AminoAcids without any modifications. That is, these AminoAcids
 * modified value always false.
 *
 * @author Qingwei XU
 * @version 0.1-SNAPSHOT
 */
public class Peptide {
    private List<AminoAcid> acidList;
    private Group n_terminal;
    private Group c_terminal;

    // the ptm position from [0, length-1]
    private Map<Integer, PTModification> ptm;

    private List<AminoAcid> generateAminoAcids(String sequence) {
        List<AminoAcid> acidList = new ArrayList<AminoAcid>();

        char[] cList = sequence.toCharArray();
        int position = 0;
        for (char c : cList) {
            AminoAcid acid = AminoAcid.getAminoAcid(c);
            if (null == acid) {
                throw new IllegalArgumentException("There exist unrecognized AminoAcids in the sequence. position=" + position);
            }

            acidList.add(acid);
            position++;
        }


        return acidList;
    }

    public Peptide(String sequence) {
        this(sequence, null);
    }

    public Peptide(String sequence, Group n_terminal, Group c_terminal) {
        this(sequence, n_terminal, c_terminal, null);
    }

    public Peptide(String sequence, Map<Integer, PTModification> ptm) {
        this(sequence, Group.H, Group.OH, ptm);
    }

    public Peptide(String sequence, Group n_terminal, Group c_terminal, Map<Integer, PTModification> ptm) {
        if (sequence == null || sequence.trim().length() == 0) {
            throw new IllegalArgumentException("peptide ion sequence is empty! ");
        }

        this.acidList = generateAminoAcids(sequence);
        this.n_terminal = n_terminal;
        this.c_terminal = c_terminal;

        if (ptm == null) {
            this.ptm = new HashMap<Integer, PTModification>();
        } else {
            this.ptm = ptm;
            addALLModification(ptm);
        }
    }

    public Peptide(List<AminoAcid> AminoAcids) {
        this(AminoAcids, null);
    }

    public Peptide(List<AminoAcid> AminoAcids, Group n_terminal, Group c_terminal) {
        this(AminoAcids, n_terminal, c_terminal, null);
    }

    public Peptide(List<AminoAcid> AminoAcids, Map<Integer, PTModification> ptm) {
        this(AminoAcids, Group.H, Group.OH, ptm);
    }

    public Peptide(List<AminoAcid> acidList, Group n_terminal, Group c_terminal, Map<Integer, PTModification> ptm) {
        if (ptm == null) {
            this.ptm = new HashMap<Integer, PTModification>();
        } else {
            this.ptm = ptm;
            addALLModification(ptm);
        }

        this.acidList = acidList;
        this.n_terminal = n_terminal;
        this.c_terminal = c_terminal;
    }

    public void addModification(Integer position, PTModification modification) {
        ptm.put(position, modification);
    }

    /**
     * if patch add modifications, we will rollback to the point of before patch add.
     */
    public void addALLModification(Map<Integer, PTModification> modifications) {
        ptm.putAll(modifications);
    }

    public void removeModification(PTModification modification) {
        ptm.remove(modification);
    }

    public void clearModifications() {
        ptm.clear();
    }

    public String getSequence() {
        if (acidList == null || acidList.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (AminoAcid acid : acidList) {
            sb.append(acid.getOneLetterCode());
        }

        return sb.toString();
    }

    public List<AminoAcid> getAminoAcids() {
        return acidList;
    }

    public Group getNTerminalGroup() {
        return n_terminal;
    }

    public Group getCTerminalGroup() {
        return c_terminal;
    }

    public int getLength() {
        return acidList.size();
    }

    /**
     * @return a unmodifiable collection, which just be used to browse the ptm contents.
     */
    public Map<Integer, PTModification> getPTM() {
        return Collections.unmodifiableMap(ptm);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (n_terminal != null && ! n_terminal.equals(Group.H)) {
            sb.append(n_terminal.getName() + "-");
        }

        sb.append(getSequence());

        if (c_terminal != null && ! c_terminal.equals(Group.OH)) {
            sb.append("-" + c_terminal.getName());
        }

        return sb.toString();
    }

}
