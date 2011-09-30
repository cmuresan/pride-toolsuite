package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.DefaultKeyedValues;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.graphics.implementation.labelers.PercentageLabelGenerator;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptide;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Class to plot a bar chart of number of peptides found for identifying each protein.</p>
 *
 * @author Antonio Fabregat
 * Date: 19-ago-2010
 * Time: 10:54:22
 */
public class ProteinsPeptidesChart extends PrideChart {
    /**
     * Defines the top value to show separately
     */
    private static final int TOP_VALUE = 5;

    /**
     * Contains the key for the values greater than the top value
     */
    private static final String GT_TOP_VALUE_KEY = ">" + TOP_VALUE;

    /**
     * <p> Creates an instance of this ProteinsPeptidesChart object, setting all fields as per description below.</p>
     *
     * @param summaryData all the spectralSummaryData chartData of a PRIDE experiment
     */
    public ProteinsPeptidesChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this ProteinsPeptidesChart object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public ProteinsPeptidesChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        if(summaryData.getProteinsPeptides().size()==0)
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
        Map<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
        List<ProteinPeptide> proteinsPeptides = summaryData.getProteinsPeptides();
        for (ProteinPeptide proteinPeptide : proteinsPeptides) {
            int protein_ID = proteinPeptide.getProteinID();
            if (occurrences.keySet().contains(protein_ID)) {
                occurrences.put(protein_ID, occurrences.get(protein_ID) + 1);
            } else {
                occurrences.put(protein_ID, 1);
            }
        }

        //A HashMap to be used for counting the frequency
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int maxKeyFound = 0;
        for (Integer protein_ID : occurrences.keySet()) {
            int key = occurrences.get(protein_ID);
            int value = map.containsKey(key)?map.get(key)+1:1;
            map.put(key, value);
            if(key>maxKeyFound) maxKeyFound=key;
        }

        List<SeriesPair<Integer,Integer>> data = new ArrayList<SeriesPair<Integer,Integer>>();

        //Used to store only the columns containing map
        for(int i=1; i<=maxKeyFound; i++){
            if(map.containsKey(i))
                data.add(new SeriesPair<Integer,Integer>(i, map.get(i)));
        }

        DataSeries<Integer,Integer> series = new DataSeries<Integer,Integer>(null, "Freq", data);
        intermediateData = new IntermediateData(series);
    }

    /**
     * Set the bar chart from the chartData contained in the param and the ordered keys for the X axis
     */
    @Override
    protected void setChart() {
        SeriesManager<Integer,Integer> sf = new SeriesManager<Integer,Integer>(intermediateData);
        List<DataSeries<Integer,Integer>> seriesList = sf.getSeries();

        DataSeries<Integer, Integer> series = seriesList.get(0);
        List<SeriesPair<Integer, Integer>> values = series.getSeriesValues(Integer.class, Integer.class);

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
        for(int i=1; i<=TOP_VALUE; i++){
            String key = ""+i;
            int value = map.containsKey(key)?map.get(key):0;
            data.addValue(key, value);
        }
        data.addValue(GT_TOP_VALUE_KEY, greaterThanTopValue);
        String id = series.getIdentifier();
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(id, data);

        chart = ChartFactory.createBarChart(
                getChartTitle(),            // chart title
                "Number of Peptides",       // x axis label
                "Frequency",                // y axis label
                dataset,                    // chartData
                PlotOrientation.VERTICAL,
                false,                      // include legend
                true,                       // tooltips
                false                       // urls
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getRangeAxis().setUpperMargin(CHART_UPPER_MARGIN);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);

        ((BarRenderer) plot.getRenderer()).setShadowVisible(false);
        
        CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabelGenerator());
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
    }

    @Override
    public String getChartTitle() {
        return "Number of Peptides Identified per Protein";
    }

    @Override
    public String getChartShortTitle() {
        return "Peptides per Protein";
    }

    @Override
    public String getChartSubTitle() {
        return "";
    }
}

