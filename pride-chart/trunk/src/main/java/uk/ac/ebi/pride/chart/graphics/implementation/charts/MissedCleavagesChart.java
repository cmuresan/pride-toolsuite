package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.graphics.implementation.labelers.PercentageLabelGenerator;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptide;
import uk.ac.ebi.pride.mol.MoleculeUtilities;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

/**
 * <p>Class to plot a bar chart of the missed cleavages of the peptide sequences.</p>
 *
 * @author Antonio Fabregat
 * Date: 19-oct-2010
 * Time: 13:50:47
 */
public class MissedCleavagesChart extends PrideChart {
    /**
     * Defines the top value to show separately
     */
    private static final int TOP_VALUE = 3;

    /**
     * Contains the key for the values greater than the top value
     */
    private static final String GT_TOP_VALUE_KEY = ">" + TOP_VALUE;

    /**
     * <p> Creates an instance of this MissedCleavagesChart object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     */
    public MissedCleavagesChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this MissedCleavagesChart object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public MissedCleavagesChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();

        for (ProteinPeptide proteinPeptide : summaryData.getProteinsPeptides()) {
            String seq = proteinPeptide.getSequence().trim();
            if(MoleculeUtilities.isAminoAcidSequence(seq)){
                //Always remove the last K or R from sequence
                seq = seq.replaceAll("[K|R]$", "");

                //We assume the hypothesis KR|P
                seq = seq.replaceAll("[K|R]P","");
                int initialLength = seq.length();

                seq = seq.replaceAll("[K|R]","");
                int bin = initialLength - seq.length();
                int val = histogram.containsKey(bin)? histogram.get(bin) + 1: 1;
                histogram.put(bin, val);
            }
        }

        if(histogram.size()==0){
            recordError("The experiment does not contain valid AminoAcid sequences");
            return;
        }

        List<SeriesPair<Integer,Integer>> data = new ArrayList<SeriesPair<Integer,Integer>>();
        int max = Collections.max(histogram.keySet());
        //With the 'for' loop we ensure that the SeriesPair are added in an ordered way
        for(int i = 0 ; i<= max ; i++)
            //Only the missed cleavages found are stored int the intermediate data
            if(histogram.containsKey(i))
                data.add(new SeriesPair<Integer, Integer>(i, histogram.get(i)));

        DataSeries series = new DataSeries<Integer, Integer>(null, "MissedCleavage", data);
        intermediateData = new IntermediateData(series);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        if(summaryData.getProteinsPeptides().size()==0)
            recordError("The experiment does not contain identifications data");

        return isValid();
    }

    @Override
    protected void initializeTypes() {
        this.supportedTypes.add(DataSeriesType.ALL_SPECTRA);

        this.visibleTypes.add(DataSeriesType.ALL_SPECTRA);
    }

    /**
     * Set the bar chart from the chartData contained in the param and the ordered keys for the X axis
     */
    @Override
    protected void setChart() {
        SeriesManager<Integer,Integer> sf = new SeriesManager<Integer,Integer>(intermediateData);
        List<DataSeries<Integer,Integer>> seriesList = sf.getSeries();

        DataSeries<Integer,Integer> series = seriesList.get(0);
        List<SeriesPair<Integer,Integer>> values = series.getSeriesValues(Integer.class, Integer.class);

        Map<String,Integer> map = new HashMap<String,Integer>();
        int greaterThanTopValue = 0;
        for (SeriesPair<Integer, Integer> value : values) {
            int xValue = value.getX();
            int yValue = value.getY();
            if(xValue>TOP_VALUE)
                greaterThanTopValue += yValue;
            else
                map.put(""+xValue, yValue);
        }
        map.put(GT_TOP_VALUE_KEY, greaterThanTopValue);

        DefaultKeyedValues data = new DefaultKeyedValues();
        for(int i=0; i<=TOP_VALUE; i++){
            String key = ""+i;
            int value = map.containsKey(key)?map.get(key):0;
            data.addValue(key, value);
        }
        data.addValue(GT_TOP_VALUE_KEY, greaterThanTopValue);
        String id = series.getIdentifier();
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(id, data);

        chart = ChartFactory.createBarChart(
                getChartTitle(),
                "Missed Cleavages",
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        chart.addSubtitle(new TextTitle(getChartSubTitle()));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getRangeAxis().setUpperMargin(CHART_UPPER_MARGIN);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);

        ((BarRenderer) plot.getRenderer()).setShadowVisible(false);

        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabelGenerator(new DecimalFormat("#.#")));
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
    }

    @Override
    public String getChartTitle() {
        return "Number of Missed Tryptic Cleavages";
    }

    @Override
    public String getChartShortTitle() {
        return "Missed Tryptic Cleavages";
    }

    @Override
    public String getChartSubTitle() {
        return "per PRIDE experiment";
    }
}
