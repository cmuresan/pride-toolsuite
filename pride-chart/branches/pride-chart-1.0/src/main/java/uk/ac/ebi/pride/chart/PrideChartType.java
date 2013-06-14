package uk.ac.ebi.pride.chart;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public enum PrideChartType {
    DELTA_MASS        (1, "Delta m/z",                 "Experimental m/z - Theoretical m/z",    "Relative Frequency",  false),
    PEPTIDES_PROTEIN  (2, "Peptides per Protein",      "Number of Peptides",                    "Frequency",           false),
    MISSED_CLEAVAGES  (3, "Missed Typtic Cleavages",   "Missed Cleavages",                      "Frequency",           false),
    AVERAGE_MS        (4, "Average MS/MS Spectrum",    "m/z",                                   "Intensity",           true),
    PRECURSOR_CHARGE  (5, "Precursor Ion Charge",      "Precursor Ion Charge",                  "Frequency",           true),
    PRECURSOR_MASSES  (6, "Precursor Ion Masses",      "Mass (Daltons)",                        "Relative Frequency",  true),
    PEAKS_MS          (7, "Peaks per MS/MS Spectrum",  "Number of Peaks",                       "Frequency",           false),
    PEAK_INTENSITY    (8, "Peak Intensity",            "Intensity",                             "Frequency",           true),
    ;

    private int order;
    private String title;
    private String domainLabel;
    private String rangeLabel;
    private boolean legend;

    private PrideChartType(int order, String title, String domainLabel, String rangeLabel, boolean legend) {
        this.order = order;
        this.title = title;
        this.domainLabel = domainLabel;
        this.rangeLabel = rangeLabel;
        this.legend = legend;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomainLabel() {
        return domainLabel;
    }

    public void setDomainLabel(String domainLabel) {
        this.domainLabel = domainLabel;
    }

    public String getRangeLabel() {
        return rangeLabel;
    }

    public void setRangeLabel(String rangeLabel) {
        this.rangeLabel = rangeLabel;
    }

    public boolean isLegend() {
        return legend;
    }

    public void setLegend(boolean legend) {
        this.legend = legend;
    }
}
