package uk.ac.ebi.pride.gui.component.sequence;

import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Protein sequence with its optional annotations
 * <p/>
 * User: rwang
 * Date: 08/06/11
 * Time: 11:37
 */
public class AnnotatedProtein extends Protein {
    public static final String PEPTIDE_SELECTION_PROP = "selectedAnnotation";

    private List<PeptideAnnotation> annotations;
    private PeptideAnnotation selectedAnnotation;
    private PropertyChangeHelper propertyChangeHelper;
    /**
     * these peptides can be duplicated
     */
    private int numOfValidPeptides = -1;
    /**
     * these peptides must be valid as well
     */
    private int numOfUniquePeptides = -1;
    /**
     * protein sequence coverage by peptide
     */
    private double sequenceCoverage = -1;

    /**
     * Create an annotable protein from an existing protein
     *
     * @param protein given protein
     */
    public AnnotatedProtein(Protein protein) {
        this(protein.getAccession());
        setName(protein.getName());
        setSource(protein.getSource());
        setSequenceString(protein.getSequenceString());
    }

    public AnnotatedProtein(String accession) {
        super(accession);
        this.annotations = new ArrayList<PeptideAnnotation>();
        this.propertyChangeHelper = new PropertyChangeHelper();
    }

    /**
     * Check whether a peptide is valid considering the protein sequence
     *
     * @param annotation peptide annotation
     * @return boolean true means valid
     */
    public boolean isValidPeptideAnnotation(PeptideAnnotation annotation) {
        String subSequence = getSubSequenceString(annotation.getStart(), annotation.getEnd());
        return subSequence != null && subSequence.equals(annotation.getSequence());
    }

    public void addAnnotation(PeptideAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void removeAnnotation(PeptideAnnotation annotation) {
        this.annotations.remove(annotation);
    }

    public List<PeptideAnnotation> getAnnotations() {
        return new ArrayList<PeptideAnnotation>(annotations);
    }

    public PeptideAnnotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    /**
     * Set a new selected peptide annotation, and notify all the property change listeners
     *
     * @param selectedAnnotation selected peptide annotation
     */
    public void setSelectedAnnotation(PeptideAnnotation selectedAnnotation) {
        PeptideAnnotation oldPeptide, newPeptide;
        synchronized (this) {
            oldPeptide = this.selectedAnnotation;
            this.selectedAnnotation = selectedAnnotation;
            newPeptide = this.selectedAnnotation;
        }
        propertyChangeHelper.firePropertyChange(PEPTIDE_SELECTION_PROP, oldPeptide, newPeptide);
    }

    public int getNumOfValidPeptides() {
        return numOfValidPeptides;
    }

    public void setNumOfValidPeptides(int numOfValidPeptides) {
        this.numOfValidPeptides = numOfValidPeptides;
    }

    public int getNumOfUniquePeptides() {
        return numOfUniquePeptides;
    }

    public void setNumOfUniquePeptides(int numOfUniquePeptides) {
        this.numOfUniquePeptides = numOfUniquePeptides;
    }

    public double getSequenceCoverage() {
        if (sequenceCoverage == -1) {

            double coverage = 0;
            for (PeptideAnnotation annotation : annotations) {
                // todo: to implement
            }
        }

        return sequenceCoverage;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeHelper.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeHelper.removePropertyChangeListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotatedProtein)) return false;
        if (!super.equals(o)) return false;

        AnnotatedProtein that = (AnnotatedProtein) o;

        if (!annotations.equals(that.annotations)) return false;
        if (selectedAnnotation != null ? !selectedAnnotation.equals(that.selectedAnnotation) : that.selectedAnnotation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + (selectedAnnotation != null ? selectedAnnotation.hashCode() : 0);
        return result;
    }
}
