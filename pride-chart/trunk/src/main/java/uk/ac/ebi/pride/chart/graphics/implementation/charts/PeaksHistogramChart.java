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
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.graphics.implementation.labelers.PercentageLabelGenerator;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * <p>Class to plot a histogram of number of peaks per MS/MS spectrum in a single PRIDE experiment</p>
 *
 * @author Antonio Fabregat
 * Date: 21-sep-2010
 * Time: 16:38:35
 */
public class PeaksHistogramChart extends PrideChart {
    private static final int BAR_NUMBER = 10;
    private static final float WIDTH_MULTIPLE = 25.0f;

    /**
     * <p> Creates an instance of this PeaksHistogramChart object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     */
    public PeaksHistogramChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this PeaksHistogramChart object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public PeaksHistogramChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        Map<Integer, Integer> data = summaryData.getPeaksHist();
        if(data.size()==0)
            recordError("The experiment does not contain intensity values");

        return isValid();
    }

    @Override
    protected void initializeTypes() {
        this.supportedTypes.add(DataSeriesType.ALL_SPECTRA);
//        this.supportedTypes.add(DataSeriesType.IDENTIFIED_SPECTRA);
//        this.supportedTypes.add(DataSeriesType.UNIDENTIFIED_SPECTRA);

        this.visibleTypes.add(DataSeriesType.ALL_SPECTRA);
    }

    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        //Contains a list of the ordered keys for the x axis
        List<String> orderedKeys = new ArrayList<String>();
        //Contains the result of the processData method
        Map<String, Integer> values = new HashMap<String, Integer>();
        //Contains the histogram pre-calculated in advance
        Map<Integer, Integer>  intensityHist = summaryData.getPeaksHist();

        int max = Collections.max(intensityHist.keySet());
        int width = (int) (Math.ceil((max / BAR_NUMBER) / WIDTH_MULTIPLE) * WIDTH_MULTIPLE);

        if(width==0) width = max;
        
        for(int aux=0; aux<=max; aux += width){
            String b = NumberFormat.getInstance().format(aux);
            String t = NumberFormat.getInstance().format(aux + width);
            String key = b + "-" + t;
            orderedKeys.add(key);
            values.put(key, 0);
        }
        
        for (int length : intensityHist.keySet()) {
            int value = intensityHist.get(length);

            int bin = (int) Math.floor(length / (width * 1.0));
            int pos = (bin<orderedKeys.size()) ? bin : orderedKeys.size()-1;

            String label = orderedKeys.get(pos);
            values.put(label, values.get(label) + value);
        }

        List<SeriesPair<String,Integer>> intensities = new ArrayList<SeriesPair<String,Integer>>();
        for (String key : orderedKeys) {
            intensities.add(new SeriesPair<String,Integer>(key, values.get(key)));
        }

        DataSeries series = new DataSeries<String, Integer>(null, "Intensity", intensities);
        intermediateData = new IntermediateData(series);
        try {
            intermediateData.setVariable("experimentSize", summaryData.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setChart() {
        SeriesManager<String,Integer> sf = new SeriesManager<String,Integer>(intermediateData);
        List<DataSeries<String,Integer>> seriesList = sf.getSeries();

        DataSeries<String, Integer> series = seriesList.get(0);
        List<SeriesPair<String, Integer>> values = series.getSeriesValues(String.class, Integer.class);

        DefaultKeyedValues data = new DefaultKeyedValues();
        for (SeriesPair<String, Integer> value : values) {
            data.addValue(value.getX(), value.getY());
        }
        String id = series.getIdentifier();
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(id, data);


        chart = ChartFactory.createBarChart(
                getChartTitle(),
                "Number of Peaks",
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
        return "Number of Peaks per MS/MS Spectrum";
    }

    @Override
    public String getChartShortTitle() {
        return "Peaks per MS/MS Spectrum";
    }

    @Override
    public String getChartSubTitle() {
        int size = intermediateData.getInteger("experimentSize");
        return "of " + size + " MS/MS spectra";
    }
}