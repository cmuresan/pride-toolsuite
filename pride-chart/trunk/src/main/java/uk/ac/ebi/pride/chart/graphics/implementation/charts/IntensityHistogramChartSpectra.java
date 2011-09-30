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
import uk.ac.ebi.pride.chart.graphics.implementation.labelers.PercentageLabelGenerator;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartSpectraOptions;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * <p>Class to plot a histogram of the number of peaks of all intensity values of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 20-jul-2010
 * Time: 9:46:49
 */
public class IntensityHistogramChartSpectra extends PrideChart implements PrideChartSpectraOptions {
    /**
     * <p> Creates an instance of this IntensityHistogramChartSpectra object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     */
    public IntensityHistogramChartSpectra(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this IntensityHistogramChartSpectra object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public IntensityHistogramChartSpectra(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        Map<Integer, Integer>  dataIdentified = summaryData.getIntensityHist(true);
        Map<Integer, Integer>  dataUnidentified = summaryData.getIntensityHist(false);
        if(dataIdentified.size()==0 && dataUnidentified.size()==0)
            recordError("The experiment does not contain intensity values");

        return isValid();
    }

    @Override
    protected void initializeTypes(){
        this.supportedTypes.add(DataSeriesType.ALL_SPECTRA);
        this.supportedTypes.add(DataSeriesType.IDENTIFIED_SPECTRA);
        this.supportedTypes.add(DataSeriesType.UNIDENTIFIED_SPECTRA);

        this.visibleTypes.add(DataSeriesType.ALL_SPECTRA);
    }

    /**
     * Distribute in logarithmic scale bins the intensity chartData of the experiment
     */
    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        //Contains the result of the processData method
        Map<String, Integer> valuesIdentified = new HashMap<String, Integer>();
        Map<String, Integer> valuesUnidentified = new HashMap<String, Integer>();

        //Contains a list of the ordered keys for the x axis
        List<String> orderedKeys = createOrderedKeys();
        for (String key : orderedKeys) valuesIdentified.put(key, 0);
        for (String key : orderedKeys) valuesUnidentified.put(key, 0);

        Map<Integer, Integer>  intensityIdenHist = summaryData.getIntensityHist(true);
        for (int key : intensityIdenHist.keySet()) {
            int value = intensityIdenHist.get(key);
            String label = getLabel(orderedKeys, key);
            valuesIdentified.put(label, valuesIdentified.get(label) + value);
        }

        Map<Integer, Integer>  intensityUnidenHist = summaryData.getIntensityHist(false);
        for (int key : intensityUnidenHist.keySet()) {
            int value = intensityUnidenHist.get(key);
            String label = getLabel(orderedKeys, key);
            valuesUnidentified.put(label, valuesUnidentified.get(label) + value);
        }

        List<SeriesPair<String,Integer>> dataIdentified = new ArrayList<SeriesPair<String,Integer>>();
        List<SeriesPair<String,Integer>> dataUnidentified = new ArrayList<SeriesPair<String,Integer>>();
        for (String key : orderedKeys) {
            dataIdentified.add(new SeriesPair<String,Integer>(key, valuesIdentified.get(key)));
            dataUnidentified.add(new SeriesPair<String,Integer>(key, valuesUnidentified.get(key)));
        }

        intermediateData = new IntermediateData();
        DataSeries seriesIdentified = new DataSeries<String,Integer>(DataSeriesType.IDENTIFIED_SPECTRA, "Identified Spectra", dataIdentified);
        intermediateData.addPrideChartSerie(seriesIdentified);
        DataSeries seriesUnidentified = new DataSeries<String,Integer>(DataSeriesType.UNIDENTIFIED_SPECTRA, "Unidentified Spectra", dataUnidentified);
        intermediateData.addPrideChartSerie(seriesUnidentified);
        try {
            intermediateData.setVariable("experimentSize", summaryData.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> createOrderedKeys(){
        //WARNING: If the ordered keys change, take care of the labels!
        List<String> orderedKeys = new ArrayList<String>();
        orderedKeys.add("1-10");
        orderedKeys.add("10-100");
        orderedKeys.add("100-1,000");
        orderedKeys.add("1,000-10,000");
        orderedKeys.add(">10,000");
        return orderedKeys;
    }

    private String getLabel(List<String> orderedKeys, int key){
        //WARNING: If the ordered keys change, take care of the labels!
        String label;
        if (key <= 10) {
            label = orderedKeys.get(0);
        } else if (key <= 100) {
            label = orderedKeys.get(1);
        } else if (key <= 1000) {
            label = orderedKeys.get(2);
        } else if (key <= 10000) {
            label = orderedKeys.get(3);
        } else {
            label = orderedKeys.get(4);
        }
        return label;
    }

    private DataSeries<String,Integer> getAllSpectraSeries(List<DataSeries<String,Integer>> seriesList){
        List<String> keys = new ArrayList<String>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (DataSeries<String, Integer> series : seriesList) {
            for (SeriesPair<String, Integer> sp : series.getSeriesValues(String.class, Integer.class)) {
                String key = sp.getX();
                Integer value = map.containsKey(key)? map.get(key) + sp.getY(): sp.getY();

                if(!keys.contains(key)) keys.add(key);
                map.put(key, value);
            }
        }

        List<SeriesPair<String,Integer>> seriesPairs = new ArrayList<SeriesPair<String, Integer>>();
        for (String key : keys) {
            Integer value = map.get(key);
            SeriesPair<String,Integer> seriesPair = new SeriesPair<String,Integer>(key, value);
            seriesPairs.add(seriesPair);
        }

        return new DataSeries<String,Integer>(DataSeriesType.ALL_SPECTRA,
                        DataSeriesType.ALL_SPECTRA.getType(),
                        seriesPairs);
    }

    /**
     * Set the histogram from the chartData contained in the parameter and a set of bins manually defined
     */
    @Override
    protected void setChart() {
        SeriesManager<String,Integer> sf = new SeriesManager<String,Integer>(intermediateData);
        List<DataSeries<String,Integer>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        seriesList.add(getAllSpectraSeries(seriesList));

        List<Color> seriesColor = new ArrayList<Color>();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DataSeries<String, Integer> series : seriesList) {
            DataSeriesType seriesType = series.getType();
            if(!visibleTypes.contains(seriesType)) continue;

            String serieKey = series.getIdentifier();
            List<SeriesPair<String,Integer>> values = series.getSeriesValues(String.class, Integer.class);
            for (SeriesPair<String, Integer> value : values)
                dataset.addValue(value.getY(), serieKey, value.getX());
            seriesColor.add(seriesType.getColor());
        }

        chart = ChartFactory.createBarChart(
                getChartTitle(),            // chart title
                "Intensity",                // x axis label
                "Frequency",                // y axis label
                dataset,                    // chartData
                PlotOrientation.VERTICAL,
                true,                       // include legend
                true,                       // tooltips
                false                       // urls
        );
        chart.addSubtitle(new TextTitle(getChartSubTitle()));

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelGenerator(new PercentageLabelGenerator());
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
        return "Peak Intensity Distribution";
    }

    @Override
    public String getChartShortTitle() {
        return "Peak Intensity";
    }

    @Override
    public String getChartSubTitle() {
        int size = intermediateData.getInteger("experimentSize");
        return "of " + size + " MS/MS spectra";
    }

    @Override
    public void setSpectraTypeVisibility(DataSeriesType type, boolean visible) {
        super.setTypeVisibility(type, visible);
    }

    @Override
    public boolean isSpectraMultipleChoice() {
        return true;
    }

    @Override
    public boolean isSpectraSeriesEmpty(DataSeriesType type) {
        SeriesManager<String,Integer> sf = new SeriesManager<String,Integer>(intermediateData);
        List<DataSeries<String,Integer>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        seriesList.add(getAllSpectraSeries(seriesList));

        for (DataSeries<String, Integer> series : seriesList) {
            if(series.getType()==type)
                return series.isEmpty(String.class, Integer.class);
        }
        return true;
    }
}