package uk.ac.ebi.pride.gui.component.sequence;

/**
 * This class is an abstract class for annotating protein sequences
 *
 * User: rwang
 * Date: 08/06/11
 * Time: 13:41
 */
public abstract class ProteinSequenceAnnotation {
    private int start, end;

    public ProteinSequenceAnnotation(int start, int end) {
        this.start = start;
        this.end = end;
    }

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
}
