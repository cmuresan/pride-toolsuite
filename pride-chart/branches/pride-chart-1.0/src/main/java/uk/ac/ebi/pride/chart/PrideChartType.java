package uk.ac.ebi.pride.chart;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public enum PrideChartType {
    DELTA_MASS             (1, "Delta m/z",                 "Experimental m/z - Theoretical m/z",    "Relative Frequency"),
    PEPTIDES_PROTEIN       (2, "Peptides per Protein",      "Number of Peptides",                    "Frequency"),
    MISSED_TYPTIC_CLEAVAGES(3, "Missed Typtic Cleavages",   "Missed Cleavages",                      "Frequency"),
    AVERAGE_MS             (4, "Average MS/MS Spectrum",    "m/z",                                   "Intensity"),
    PRECURSOR_CHARGE       (5, "Precursor Ion Charge",      "Precursor Ion Charge",                  "Frequency"),
    PRECURSOR_Masses       (6, "Precursor Ion Masses",      "Mass (Daltons)",                        "Relative Frequency"),
    PEAKS_MS               (7, "Peaks per MS/MS Spectrum",  "Number of Peaks",                       "Frequency"),
    PEAK_INTENSITY         (8, "Peak Intensity",            "Intensity",                             "Frequency"),
    ;

    private int order;
    private String title;
    private String xLabel;
    private String yLabel;

    private PrideChartType(int order, String title, String xLabel, String yLabel) {
        this.order = order;
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
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

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getYLabel() {
        return yLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
    }
}
