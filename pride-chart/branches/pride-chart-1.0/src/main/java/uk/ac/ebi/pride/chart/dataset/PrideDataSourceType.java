package uk.ac.ebi.pride.chart.dataset;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public enum PrideDataSourceType {
    UNKNOWN(""),
    ALL_SPECTRA("All Spectra"),
    IDENTIFIED_SPECTRA("Identified Spectra"),
    UNIDENTIFIED_SPECTRA("Unidentified Spectra");

    private String name;

    private PrideDataSourceType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
