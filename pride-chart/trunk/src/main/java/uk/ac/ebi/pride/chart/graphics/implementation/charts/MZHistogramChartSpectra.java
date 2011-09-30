package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartSpectraOptions;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.PrideHistogram;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * <p>Class to plot a histogram of all m/z values of a PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 19-jul-2010
 * Time: 11:20:52
 */
public class MZHistogramChartSpectra extends PrideChart implements PrideChartSpectraOptions {
    /**
     * <p> Bin size used for data process and retrieval data algorithms
     */
    public static final int BIN_SIZE = 1;

    /**
     * <p> Creates an instance of this MZHistogramChartSpectra object, setting all fields as per description below.</p>
     *
     * @param summaryData all the summaryData of a PRIDE experiment
     */
    public MZHistogramChartSpectra(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    /**
     * <p> Creates an instance of this MZHistogramChartSpectra object, setting all fields as per description below.</p>
     *
     * @param jsonData a Pride Chart Json Data object containing the chart values
     */
    public MZHistogramChartSpectra(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        if(summaryData.getMzHist(true).size() == 0 && summaryData.getMzHist(false).size() == 0)
            recordError("The experiment does not contain precursor M/Z values and/or no precursor charges");

        if(!summaryData.getState().isPrecursorChargesLoaded())
            recordError("The experiment does not contain precursor charge values");

        return isValid();
    }

    @Override
    protected void initializeTypes() {
        this.supportedTypes.add(DataSeriesType.ALL_SPECTRA);
        this.supportedTypes.add(DataSeriesType.IDENTIFIED_SPECTRA);
        this.supportedTypes.add(DataSeriesType.UNIDENTIFIED_SPECTRA);

        this.visibleTypes.add(DataSeriesType.ALL_SPECTRA);
    }

    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        Map<Integer, PrideHistogram> identified = summaryData.getMzHist(true);
        Map<Integer, PrideHistogram> unidentified = summaryData.getMzHist(false);

        Map<DataSeriesType, Map<Integer,PrideHistogram>> map = new HashMap<DataSeriesType, Map<Integer,PrideHistogram>>();
        map.put(DataSeriesType.IDENTIFIED_SPECTRA, identified);
        map.put(DataSeriesType.UNIDENTIFIED_SPECTRA, unidentified);
        //map.put(DataSeriesType.ALL_SPECTRA, joinData(identified, unidentified));

        intermediateData = new IntermediateData();
        Map<Integer,Double> mzAllSeries = new HashMap<Integer,Double>();
        for (DataSeriesType type : map.keySet()) {
            Map<Integer, PrideHistogram> data = map.get(type);
            //The data in the summary data is depending on the charge (BE CAREFUL WITH charge==0)
            //Here we put it all together for this chart
            for (Integer charge : data.keySet()) {
                PrideHistogram histogram = data.get(charge);
                int max = Collections.max(histogram.keySet());
                for (int i = 0; i <= max; i+=BIN_SIZE) {
                    int key = i * BIN_SIZE;
                    double value = histogram.get(i)!=null?histogram.get(i):0;
                    if(mzAllSeries.containsKey(key)) value += mzAllSeries.get(key);
                    mzAllSeries.put(key,value);
                }
            }

            List<SeriesPair<Integer,Double>> sps = new ArrayList<SeriesPair<Integer,Double>>();
            for (Integer x : mzAllSeries.keySet()) {
                sps.add(new SeriesPair<Integer, Double>(x, mzAllSeries.get(x)));
            }
            DataSeries<Integer,Double> series = new DataSeries<Integer,Double>(type,type.getType(),sps);
            intermediateData.addPrideChartSerie(series);
        }

        try {
            intermediateData.setVariable("experimentSize", summaryData.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private DataSeries<Integer,Double> getAllSpectraSeries(List<DataSeries<Integer,Double>> seriesList){
        List<Integer> keys = new ArrayList<Integer>();
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        for (DataSeries<Integer, Double> series : seriesList) {
            for (SeriesPair<Integer, Double> sp : series.getSeriesValues(Integer.class, Double.class)) {
                Integer key = sp.getX();
                Double value = map.containsKey(key)? map.get(key) + sp.getY(): sp.getY();

                if(!keys.contains(key)) keys.add(key);
                map.put(key, value);
            }
        }

        List<SeriesPair<Integer,Double>> seriesPairs = new ArrayList<SeriesPair<Integer, Double>>();
        for (Integer key : keys) {
            Double value = map.get(key);
            SeriesPair<Integer,Double> seriesPair = new SeriesPair<Integer,Double>(key, value);
            seriesPairs.add(seriesPair);
        }

        return new DataSeries<Integer,Double>(DataSeriesType.ALL_SPECTRA,
                        DataSeriesType.ALL_SPECTRA.getType(),
                        seriesPairs);
    }

    /**
     * Set the histogram from the chartData contained in the parameter and the number of bins defined
     */
    @Override
    protected void setChart() {
        SeriesManager<Integer,Double> sf = new SeriesManager<Integer,Double>(intermediateData);
        List<DataSeries<Integer,Double>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        //int maxCharge = intermediateData.getInteger("maxCharge");
        //for (DataSeries<Integer, Double> series : getAllSpectraSeries(seriesList, maxCharge)) {
        //    seriesList.add(series);
        //}
        seriesList.add(getAllSpectraSeries(seriesList));

        Map<Integer,Double> mzAllSeries = new HashMap<Integer,Double>();
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (DataSeries<Integer,Double> series : seriesList) {
            DataSeriesType seriesType = series.getType();
            if(!visibleTypes.contains(seriesType)) continue;

            XYSeries mzSeries = new XYSeries(series.getIdentifier());
            for (SeriesPair<Integer, Double> value : series.getSeriesValues(Integer.class, Double.class)) {
                int x = value.getX();
                double y = value.getY();
                if(mzAllSeries.containsKey(x)) y += mzAllSeries.get(x);

                mzAllSeries.put(x,y);

                mzSeries.add(value.getX(), value.getY());

            }
            dataset.addSeries(mzSeries);
        }
        
        chart = ChartFactory.createXYLineChart(
                getChartTitle(),            // chart title
                "m/z",                      // x axis label
                "Intensity",                // y axis label
                dataset,                    // chartData
                PlotOrientation.VERTICAL,
                true,                       // include legend
                true,                       // tooltips
                false                       // urls
        );
        chart.addSubtitle(new TextTitle(getChartSubTitle()));

        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setUpperMargin(CHART_UPPER_MARGIN - 0.05);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);

        XYSplineRenderer sr = new XYSplineRenderer();
        chart.getXYPlot().setRenderer(sr);
        for(int i=0; i<dataset.getSeries().size(); i++){
            sr.setSeriesShapesVisible(i, false);
        }
    }


    @Override
    public String getChartTitle() {
        return "Average MS/MS Spectrum";
    }

    @Override
    public String getChartShortTitle() {
        return "Average MS/MS Spectrum";
    }

    @Override
    public String getChartSubTitle() {
        int size = intermediateData.getInteger("experimentSize");
        return "in " + size + " MS/MS spectra";
    }

    @Override
    public void setSpectraTypeVisibility(DataSeriesType type, boolean visible) {
        super.setTypeVisibility(type, visible);
    }

    @Override
    public boolean isSpectraMultipleChoice() {
        return false;
    }

    @Override
    public boolean isSpectraSeriesEmpty(DataSeriesType type) {
        SeriesManager<Integer,Double> sf = new SeriesManager<Integer,Double>(intermediateData);
        List<DataSeries<Integer,Double>> seriesList = sf.getSeries();

        //Adds the 'All Spectra' series at the end in the local variable 'seriesList'
        //**NOTE** The intermediateData is NOT modified
        seriesList.add(getAllSpectraSeries(seriesList));

        for (DataSeries<Integer,Double> series : seriesList) {
            if(series.getType()==type)
                return series.isEmpty(Integer.class, Double.class);
        }
        return true;
    }
}
