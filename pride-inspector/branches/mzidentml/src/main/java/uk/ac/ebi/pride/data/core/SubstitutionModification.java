package uk.ac.ebi.pride.data.core;

/**
 * Substitution Modification is a Modification where One amino acid is replaced by another amino acid.
 * For example Deamidation Post-Translational Modification is a Substitution Modification where the amino
 * acid Asparagine (N) is replaced by Aspartic Acid (D)
 *
 * <p/>
 * User: yperez
 * Date: 04/08/11
 * Time: 14:18
 */
public class SubstitutionModification {

    private double avgMassDelta          = 0.0;

    private int    location              = -1;

    private double monoisotopicMassDelta = 0.0;

    private String originalResidue       = null;

    private String replacementResidue    = null;

    public SubstitutionModification(String originalResidue, String replacementResidue, int location,
                                    double avgMassDelta, double monoisotopicMassDelta) {
        this.originalResidue       = originalResidue;
        this.replacementResidue    = replacementResidue;
        this.location              = location;
        this.avgMassDelta          = avgMassDelta;
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



