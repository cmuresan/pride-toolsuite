package uk.ac.ebi.pride.gui.component.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Protein sequence with its optional annotations
 *
 * User: rwang
 * Date: 08/06/11
 * Time: 11:37
 */
public class AnnotatedProtein extends Protein {
    private List<ProteinSequenceAnnotation> annotations;
    private ProteinSequenceAnnotation selectedAnnotation;

    public AnnotatedProtein(String sequence) {
        super(sequence);
        this.annotations = new ArrayList<ProteinSequenceAnnotation>();
    }

    public void addAnnotation(ProteinSequenceAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void removeAnnotation(ProteinSequenceAnnotation annotation) {
        this.annotations.remove(annotation);
    }

    public ProteinSequenceAnnotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    public void setSelectedAnnotation(ProteinSequenceAnnotation selectedAnnotation) {
        this.selectedAnnotation = selectedAnnotation;
    }

    public double getSequenceCoverage() {
        double coverage = 0;
        for (ProteinSequenceAnnotation annotation : annotations) {
            if (annotation instanceof PeptideAnnotation) {

            }
        }

        return coverage;
    }
}
