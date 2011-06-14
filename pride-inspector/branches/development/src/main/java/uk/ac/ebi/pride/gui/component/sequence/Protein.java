package uk.ac.ebi.pride.gui.component.sequence;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08/06/11
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class Protein {
    /**
     * Human readable name for the protein
     */
    private String name;
    /**
     * Protein accession
     */
    private String accession;
    /**
     * Source of the protein details
     */
    private String source;
    /**
     * Protein sequence
     */
    private String sequenceString;

    public Protein(String accession) {
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
        if (sequence == null || sequence.length() <= 0) {
            throw new IllegalArgumentException("Input protein sequence can not be NULL or sequenceString length can not zero");
        }
        this.sequenceString = sequence.toUpperCase();
    }

    public String getSubSequenceString(int start, int stop) {
        if (sequenceString.length() <= stop && start >= 0 && start <= stop) {
            return this.sequenceString.substring(start, stop);
        } else {
            return null;
        }
    }

    public boolean hasSubSequenceString(String subSeq, int start, int stop) {
        // todo: to be implemented
        return false;
    }
}
