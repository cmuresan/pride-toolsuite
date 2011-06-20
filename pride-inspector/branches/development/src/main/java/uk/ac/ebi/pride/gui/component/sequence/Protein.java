package uk.ac.ebi.pride.gui.component.sequence;

import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.io.Serializable;

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

    public String getSubSequenceString(int start, int stop) {
        if (sequenceString != null && sequenceString.length() >= stop && start >= 1 && start <= stop) {
            return this.sequenceString.substring(start - 1, stop);
        } else {
            return null;
        }
    }

    public boolean hasSubSequenceString(String subSeq, int start, int stop) {
        // todo: to be implemented
        return false;
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
