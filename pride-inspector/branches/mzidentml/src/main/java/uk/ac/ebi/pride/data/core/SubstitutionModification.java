package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 04/08/11
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public class SubstitutionModification {

    private String originalResidue = null;

    private String replacementResidue = null;

    private int location = -1;

    private double avgMassDelta = 0.0;

    private double monoisotopicMassDelta = 0.0;

    public SubstitutionModification(String originalResidue, String replacementResidue, int location, double avgMassDelta, double monoisotopicMassDelta) {
        this.originalResidue = originalResidue;
        this.replacementResidue = replacementResidue;
        this.location = location;
        this.avgMassDelta = avgMassDelta;
        this.monoisotopicMassDelta = monoisotopicMassDelta;
    }

    public String getOriginalResidue() {
        return originalResidue;
    }

    public void setOriginalResidue(String originalResidue) {
        this.originalResidue = originalResidue;
    }

    public String getReplacementResidue() {
        return replacementResidue;
    }

    public void setReplacementResidue(String replacementResidue) {
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
