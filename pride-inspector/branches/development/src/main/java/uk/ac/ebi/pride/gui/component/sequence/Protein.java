package uk.ac.ebi.pride.gui.component.sequence;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08/06/11
 * Time: 16:42
 * To change this template use File | Settings | File Templates.
 */
public class Protein {
    private String name;
    private String accession;
    private String sequenceSource;
    private String sequenceString;

    public Protein(String sequence) {
        if (sequence == null || sequence.length() <= 0) {
            throw new IllegalArgumentException("Input protein sequence can not be NULL or sequenceString length can not zero");
        }
        this.sequenceString = sequence.toUpperCase();
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

    public String getSequenceSource() {
        return sequenceSource;
    }

    public void setSequenceSource(String sequenceSource) {
        this.sequenceSource = sequenceSource;
    }

    public String getSequenceString() {
        return sequenceString;
    }

    public void setSequenceString(String sequenceString) {
        this.sequenceString = sequenceString;
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
