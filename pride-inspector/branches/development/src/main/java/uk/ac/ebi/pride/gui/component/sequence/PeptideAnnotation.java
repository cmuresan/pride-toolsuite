package uk.ac.ebi.pride.gui.component.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Annotation to describe a peptide
 *
 * User: rwang
 * Date: 08/06/11
 * Time: 13:52
 */
public class PeptideAnnotation {

    private int start, end;
    /**
     * peptide sequence
     */
    private String sequence;
    /**
     * optional, ptm annotations
     */
    private List<PTMAnnotation> ptmAnnotations;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence == null ? sequence : sequence.toUpperCase();
    }

    public List<PTMAnnotation> getPtmAnnotations() {
        return ptmAnnotations;
    }

    public void setPtmAnnotations(List<PTMAnnotation> ptmAnnotations) {
        this.ptmAnnotations = new ArrayList<PTMAnnotation>(ptmAnnotations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeptideAnnotation)) return false;

        PeptideAnnotation that = (PeptideAnnotation) o;

        if (end != that.end) return false;
        if (start != that.start) return false;
        if (ptmAnnotations != null ? !ptmAnnotations.equals(that.ptmAnnotations) : that.ptmAnnotations != null)
            return false;
        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (ptmAnnotations != null ? ptmAnnotations.hashCode() : 0);
        return result;
    }
}
