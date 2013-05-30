package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptide;
import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptideException;
import uk.ac.ebi.pride.chart.model.implementation.SpectrumData;
import uk.ac.ebi.pride.mol.MoleculeUtilities;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * <p>Class to plot a relative frequency distribution of identified peptide mass - precursor ion mass</p>
 *
 * @author Antonio Fabregat
 *         Date: 24-sep-2010
 *         Time: 14:17:28
 */
public class MassDeltaChart extends PrideChart {
    private static final int BINS = 200;

    private static final double MIN_BIN_WIDTH = 0.0005;

    /**
     * <p> Creates an instance of this MassDeltaChart object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     */
    public MassDeltaChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this MassDeltaChart object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public MassDeltaChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        if (!summaryData.getState().isPrecursorChargesLoaded())
            recordError("The experiment does not contain precursor charges");

        if (!summaryData.getState().isPrecursorMassesLoaded())
            recordError("The experiment does not contain precursor masses");

        if (summaryData.getProteinsPeptides().size() == 0)
            recordError("The experiment does not contain identifications data");

        return isValid();
    }

    @Override
    protected void initializeTypes() {
        this.supportedTypes.add(DataSeriesType.PEPTIDE);

        this.visibleTypes.add(DataSeriesType.PEPTIDE);
    }

    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        List<Double> data = new ArrayList<Double>();

        for (ProteinPeptide pp : summaryData.getProteinsPeptides()) {
            SpectrumData sData;
            try {
                Comparable spectrumID = pp.getSpectrumID();
                sData = summaryData.getSpectrum(String.valueOf(spectrumID));
            } catch (ProteinPeptideException e) {
                //If there is not spectrumID, the precursor mass and charge could not be found
                continue;
            }

            double mz;
            int charge;
            try {
                mz = sData.getPrecursorMass();
                charge = (int) sData.getPrecursorCharge();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }
            // the sum of the PTM masses is stored in the ProteinPeptide object, but for calling
            // the calculateDeltaMz method, we need a list of PTM (the Water loss mono mass value
            // will be added by the method)
            List<Double> ptmMasses = new ArrayList<Double>();
            ptmMasses.add(pp.getPtmMass());
            Double deltaMass = MoleculeUtilities.calculateDeltaMz(pp.getSequence(), mz, charge, ptmMasses);

            if (deltaMass != null) data.add(deltaMass);
        }

        if (data.size() == 0) {
            recordError("Peptide sequences could not be associated to a MS 1 precursor");
            return;
        }

        Map<Double, Integer> histogram = new HashMap<Double, Integer>();

        double min = Collections.min(data);
        double max = Collections.max(data);

        double binWidth = Math.abs((max - min) / (double) BINS);
        binWidth = binWidth < MIN_BIN_WIDTH ? MIN_BIN_WIDTH : binWidth;
        for (double value : data) {
            int bin = (int) Math.round(value / binWidth);
            double pos = bin * binWidth;
            if (histogram.keySet().contains(pos)) {
                histogram.put(pos, histogram.get(pos) + 1);
            } else {
                histogram.put(pos, 1);
            }
        }

        List<SeriesPair<Double, Double>> deltaMasses = new ArrayList<SeriesPair<Double, Double>>();

        if (histogram.keySet().size() == 1) {
            for (Double key : histogram.keySet()) {
                if (key > 0.0) { //To center the chart on 0
                    deltaMasses.add(new SeriesPair<Double, Double>(-key - MIN_BIN_WIDTH, 0.0));
                }

                deltaMasses.add(new SeriesPair<Double, Double>(key - MIN_BIN_WIDTH, 0.0));
                deltaMasses.add(new SeriesPair<Double, Double>(key, 1.0));
                deltaMasses.add(new SeriesPair<Double, Double>(key + MIN_BIN_WIDTH, 0.0));

                if (key < 0.0) { //To center the chart on 0
                    deltaMasses.add(new SeriesPair<Double, Double>(-key + MIN_BIN_WIDTH, 0.0));
                }
            }
        } else {
            int maxFreq = Collections.max(histogram.values());
            for (int pos = -BINS; pos < BINS; pos++) {
                double key = pos * binWidth;
                double value = histogram.containsKey(key) ? histogram.get(key) / (double) maxFreq : 0.0;
                deltaMasses.add(new SeriesPair<Double, Double>(key, value));
            }
        }

        DataSeries series = new DataSeries<Double, Double>(null, "deltaMasses", deltaMasses);
        intermediateData = new IntermediateData(series);
        try {
            intermediateData.setVariable("sequenceNumber", data.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setChart() {
        SeriesManager<Double, Double> sf = new SeriesManager<Double, Double>(intermediateData);
        List<DataSeries<Double, Double>> seriesList = sf.getSeries();

        DataSeries<Double, Double> series = seriesList.get(0);
        List<SeriesPair<Double, Double>> values = series.getSeriesValues(Double.class, Double.class);

        XYSeries deltaMassSeries = new XYSeries(series.getIdentifier());
        for (SeriesPair<Double, Double> value : values) {
            deltaMassSeries.add(value.getX(), value.getY());
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(deltaMassSeries);

        chart = ChartFactory.createXYLineChart(
                getChartTitle(),                        // chart title
                "Experimental m/z - Theoretical m/z",   // x axis label
                "Relative Frequency",                   // y axis label
                dataset,                                // chartData
                PlotOrientation.VERTICAL,
                false,                                  // include legend
                true,                                   // tooltips
                false                                   // urls
        );
        chart.addSubtitle(new TextTitle(getChartSubTitle()));

        XYPlot plot = chart.getXYPlot();
        plot.setDomainZeroBaselineVisible(true);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);
    }

    @Override
    public String getChartTitle() {
        return "Delta m/z";
    }

    @Override
    public String getChartShortTitle() {
        return "Delta m/z";
    }

    @Override
    public String getChartSubTitle() {
        int seqNum = intermediateData.getInteger("sequenceNumber");
        return "for " + NumberFormat.getInstance().format(seqNum) + " analyzed sequences";
    }
}
