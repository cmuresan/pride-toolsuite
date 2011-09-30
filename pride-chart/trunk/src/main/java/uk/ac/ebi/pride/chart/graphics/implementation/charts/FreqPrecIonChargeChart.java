package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.graphics.implementation.labelers.NumberLegendGenerator;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Class to plot a bar chart of precursor charge for all spectra.</p>
 *
 * @author Antonio Fabregat
 * Date: 16-jul-2010
 * Time: 14:48:15
 */
public class FreqPrecIonChargeChart extends PrideChart{ // implements PrideChartSpectraOptions {
    /**
     * Defines the top value to show separately
     */
    private static final int TOP_VALUE = 7;

    /**
     * Contains the key for the values greater than the top value
     */
    private static final String GT_TOP_VALUE_KEY = ">" + TOP_VALUE;

    /**
     * <p> Creates an instance of this FreqPrecIonChargeChart object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     */
    public FreqPrecIonChargeChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this FreqPrecIonChargeChart object, setting all fields as per description below.</p>
     *
     * @param jsonData a String containing the chart intermediate data (in JSon Format)
     */
    public FreqPrecIonChargeChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        if(!summaryData.getState().isPrecursorChargesLoaded())
            recordError("The experiment does not contain precursor charge values");

        return isValid();
    }

    @Override
    protected void initializeTypes() {
        //this.supportedTypes.add(DataSeriesType.ALL_SPECTRA);
        this.supportedTypes.add(DataSeriesType.IDENTIFIED_SPECTRA);
        //this.supportedTypes.add(DataSeriesType.UNIDENTIFIED_SPECTRA);

        //this.visibleTypes.add(DataSeriesType.ALL_SPECTRA);
        this.visibleTypes.add(DataSeriesType.IDENTIFIED_SPECTRA);
    }

    /**
     * Calculate the frequency of every one of the keys of the X axis
     */
    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        Map<Integer, Integer> valuesIdentified = new HashMap<Integer, Integer>();
        Map<Integer, Integer> valuesUnidentified = new HashMap<Integer, Integer>();

        List<Double> chargeIdentified = summaryData.getPrecChargeVecExp(true);
        int maxChargeFound = 0;
        for (double key : chargeIdentified) {
            int auxKey = (int) key;
            int value = valuesIdentified.containsKey(auxKey)?valuesIdentified.get(auxKey)+1:1;
            valuesIdentified.put(auxKey, value);
            if(auxKey > maxChargeFound) maxChargeFound = auxKey;
        }

        List<Double> chargeUnidentified = summaryData.getPrecChargeVecExp(false);
        for (double key : chargeUnidentified) {
            int auxKey = (int) key;
            int value = valuesUnidentified.containsKey(auxKey)?valuesUnidentified.get(auxKey)+1:1;
            valuesUnidentified.put(auxKey, value);
            if(auxKey > maxChargeFound) maxChargeFound = auxKey;
        }

        if(maxChargeFound==0){
            recordError("No correct charges has been found");
            return;
        }

        List<SeriesPair<Integer,Integer>> identified = new ArrayList<SeriesPair<Integer,Integer>>();
        //List<SeriesPair<Integer,Integer>> unidentified = new ArrayList<SeriesPair<Integer,Integer>>();
        //With the 'for' loop we ensure that the SeriesPair are added in an ordered way
        for(int key=1; key<=maxChargeFound; key++){
            //Only the charges found are stored int the intermediate identified
            if(valuesIdentified.containsKey(key))
                identified.add(new SeriesPair<Integer, Integer>(key,valuesIdentified.get(key)));

            //if(valuesUnidentified.containsKey(key))
            //    unidentified.add(new SeriesPair<Integer, Integer>(key,valuesUnidentified.get(key)));
        }

        if(identified.isEmpty()){
            recordError("The experiment does not contain identified spectra");
            return;
        }

        intermediateData = new IntermediateData();
        //In this case the type and the name of the series are the same
        intermediateData.addPrideChartSerie(new DataSeries<Integer,Integer>(DataSeriesType.IDENTIFIED_SPECTRA, "Identified Spectra", identified));
        //intermediateData.addPrideChartSerie(new DataSeries<Integer,Integer>(DataSeriesType.UNIDENTIFIED_SPECTRA, "Unidentified Spectra", unidentified));

        try {
            intermediateData.setVariable("experimentSize", summaryData.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private DataSeries<Integer,Integer> getAllSpectraSeries(List<DataSeries<Integer,Integer>> seriesList){
        List<Integer> keys = new ArrayList<Integer>();
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (DataSeries<Integer, Integer> series : seriesList) {
            for (SeriesPair<Integer, Integer> sp : series.getSeriesValues(Integer.class, Integer.class)) {
                Integer key = sp.getX();
                Integer value = map.containsKey(key)? map.get(key) + sp.getY(): sp.getY();

                if(!keys.contains(key)) keys.add(key);
                map.put(key, value);
            }
        }

        List<SeriesPair<Integer,Integer>> seriesPairs = new ArrayList<SeriesPair<Integer, Integer>>();
        for (Integer key : keys) {
            Integer value = map.get(key);
            SeriesPair<Integer,Integer> seriesPair = new SeriesPair<Integer,Integer>(key, value);
            seriesPairs.add(seriesPair);
        }

        return new DataSeries<Integer,Integer>(DataSeriesType.ALL_SPECTRA,
                        DataSeriesType.ALL_SPECTRA.getType(),
                        seriesPairs);
    }

    @Override
    protected void setChart() {
        SeriesManager<Integer,Integer> sf = new SeriesManager<Integer,Integer>(intermediateData);
        List<DataSeries<Integer,Integer>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        seriesList.add(getAllSpectraSeries(seriesList));

        List<Color> seriesColor = new ArrayList<Color>();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataSeries<Integer, Integer> series : seriesList) {
            DataSeriesType seriesType = series.getType();
            if(!visibleTypes.contains(seriesType)) continue;

            String serieKey = series.getIdentifier();
            List<SeriesPair<Integer,Integer>> values = series.getSeriesValues(Integer.class, Integer.class);

            Map<String, Integer> map = new HashMap<String,Integer>();
            //The map is filled with the intermediate data values
            int greaterThanTopValue = 0;
            for (SeriesPair<Integer, Integer> value : values) {
                int xValue = value.getX();
                int yValue = value.getY();
                if(xValue> TOP_VALUE){
                    greaterThanTopValue += yValue;
                }else{
                    map.put(""+xValue, yValue);
                }
            }

            //Finally the JFreeChart data object is created and filled with the processed data
            //DefaultKeyedValues data = new DefaultKeyedValues();
            for(int i=1; i<= TOP_VALUE; i++){
                String category = "" + i;
                int value = map.containsKey(category)?map.get(category):0;
                dataset.addValue(value, serieKey, category);
            }
            dataset.addValue(greaterThanTopValue, serieKey, GT_TOP_VALUE_KEY);
            seriesColor.add(seriesType.getColor());
        }

        chart = ChartFactory.createBarChart(
                getChartTitle(),           // chart title
                "Precursor Ion Charge",    // x axis label
                "Frequency",               // y axis label
                dataset,                   // chartData
                PlotOrientation.VERTICAL,
                true,                      // include legend
                true,                      // tooltips
                false                      // urls
        );
        chart.addSubtitle(new TextTitle());

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new NumberLegendGenerator());
        for(int i=0; i<dataset.getRowCount(); i++){
            renderer.setSeriesItemLabelsVisible(i, Boolean.TRUE);
            renderer.setSeriesPaint(i, seriesColor.get(i));
        }

        plot.getRangeAxis().setUpperMargin(CHART_UPPER_MARGIN);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);

        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0);
    }


    @Override
    public String getChartTitle() {
        return "Precursor Ion Charge Distribution";
    }

    @Override
    public String getChartShortTitle() {
        return "Precursor Ion Charge";
    }

    @Override
    public String getChartSubTitle() {
        int size = intermediateData.getInteger("experimentSize");
        return "of " + size + " MS/MS spectra";
    }

    @Override
    public void setTypeVisibility(DataSeriesType type, boolean visible) {
        super.setTypeVisibility(type, visible);
    }

    //@Override
    public boolean isMultipleChoice() {
        return true;
    }

    //@Override
    public boolean isSeriesEmpty(DataSeriesType type) {
        SeriesManager<Integer,Integer> sf = new SeriesManager<Integer,Integer>(intermediateData);
        List<DataSeries<Integer,Integer>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        seriesList.add(getAllSpectraSeries(seriesList));

        for (DataSeries<Integer, Integer> series : seriesList) {
            if(series.getType()==type)
                return series.isEmpty(Integer.class, Integer.class);
        }
        return true;
    }
}
