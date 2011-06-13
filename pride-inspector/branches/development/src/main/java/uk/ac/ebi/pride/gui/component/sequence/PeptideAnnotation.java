package uk.ac.ebi.pride.gui.component.sequence;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08/06/11
 * Time: 13:52
 */
public class PeptideAnnotation extends ProteinSequenceAnnotation{

    private String sequence;

    public PeptideAnnotation(String seq, int start, int end) {
        super(start, end);
        this.sequence = seq;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
