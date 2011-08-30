package uk.ac.ebi.pride.data.coreIdent;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 04/08/11
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class SubstitutionModification {

    private char originalResidue = '\u0000';

    private char replacementResidue = '\u0000';

    private int location = -1;

    private double avgMassDelta = 0.0;

    private double monoisotopicMassDelta = 0.0;

    public SubstitutionModification(char originalResidue, char replacementResidue, int location, double avgMassDelta, double monoisotopicMassDelta) {
        this.originalResidue = originalResidue;
        this.replacementResidue = replacementResidue;
        this.location = location;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    public char getOriginalResidue() {
        return originalResidue;
    }

    public void setOriginalResidue(char originalResidue) {
        this.originalResidue = originalResidue;
    }

    public char getReplacementResidue() {
        return replacementResidue;
    }

    public void setReplacementResidue(char replacementResidue) {
        this.replacementResidue = replacementResidue;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public double getAvgMassDelta() {
        return avgMassDelta;
    }

    public void setAvgMassDelta(double avgMassDelta) {
        this.avgMassDelta = avgMassDelta;
    }

    public double getMonoisotopicMassDelta() {
        return monoisotopicMassDelta;
    }

    public void setMonoisotopicMassDelta(double monoisotopicMassDelta) {
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }
}
