package uk.ac.ebi.pride.gui.component.sequence;

import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Object to store protein related details
 * <p/>
 * User: rwang
 * Date: 08/06/11
 * Time: 16:42
 */
public class Protein implements Serializable {
    /**
     * Human readable name for the protein
     */
    private String name = null;
    /**
     * Protein accession
     */
    private String accession = null;
    /**
     * Source of the protein details
     */
    private String source = null;
    /**
     * Protein sequence
     */
    private String sequenceString = null;

    /**
     * Indicate whether the protein accession is still active from the original source
     */
    private boolean active = true;


    public Protein(String accession) {
        if (accession == null || "".equals(accession.trim())) {
            throw new IllegalArgumentException("Protein accession cannot be NULL");
        }

        this.accession = accession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSequenceString() {
        return sequenceString;
    }

    public void setSequenceString(String sequence) {
        this.sequenceString = sequence == null ? sequence : sequence.toUpperCase();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSubSequenceString(int start, int stop) {
        if (sequenceString != null && sequenceString.length() >= stop && start >= 1 && start <= stop) {
            return this.sequenceString.substring(start - 1, stop);
        } else {
            return null;
        }
    }

    /**
     * To check whether a given sequence is a sub sequence of the protein
     * @param subSeq    given sequence
     * @param start start position
     * @param stop  stop position
     * @return  boolean true means exist
     */
    public boolean hasSubSequenceString(String subSeq, int start, int stop) {
        String targetSeq = getSubSequenceString(start, stop);
        return targetSeq != null && subSeq != null && targetSeq.equals(subSeq.toUpperCase());
    }

    /**
     * To check whether a given sequence is a sub sequence of the protein
     * @param subSeq    given sequence
     * @return  boolean true means exist
     */
    public boolean hasSubSequenceString(String subSeq) {
        return sequenceString != null && subSeq != null && sequenceString.contains(subSeq);
    }


    /**
     * Search a given sub sequence within the protein sequence
     * return a set of starting positions which matches the given sub sequence
     *
     * @param subSeq    given sub sequence
     * @return  Set<Integer>    starting positions
     */
    public Set<Integer> searchStartingPosition(String subSeq) {
        Set<Integer> pos = new HashSet<Integer>();

        if (sequenceString != null && subSeq != null) {
            int previousIndex = -1;
            int index = -1;

            while((index = (previousIndex == -1 ? sequenceString.indexOf(subSeq) : sequenceString.indexOf(subSeq, previousIndex + 1))) > -1) {
                pos.add(index);
                previousIndex = index;
            }
        }

        return pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Protein)) return false;

        Protein protein = (Protein) o;

        if (!accession.equals(protein.accession)) return false;
        if (name != null ? !name.equals(protein.name) : protein.name != null) return false;
        if (sequenceString != null ? !sequenceString.equals(protein.sequenceString) : protein.sequenceString != null)
            return false;
        if (source != null ? !source.equals(protein.source) : protein.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + accession.hashCode();
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (sequenceString != null ? sequenceString.hashCode() : 0);
        return result;
    }
}