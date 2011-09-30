package uk.ac.ebi.pride.chart.graphics.implementation.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.IntervalXYDataset;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.*;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;
import uk.ac.ebi.pride.chart.model.implementation.PeptideScore;
import uk.ac.ebi.pride.chart.model.implementation.ProteinPeptide;
import uk.ac.ebi.pride.chart.utils.ScoreIntervalXYDataset;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * <p></p>
 *
 * User: fabregat
 * Date: 10-dic-2010
 * Time: 11:53:03
 */
public class MascotScoreChart extends PrideChart {

    private final int NUMBER_OF_BARS = 16;

    public MascotScoreChart(ExperimentSummaryData summaryData) {
        super(summaryData);
    }

    public MascotScoreChart(String jsonData) {
        super(jsonData);
    }

    @Override
    protected boolean isDataConsistent(ExperimentSummaryData summaryData) {
        return true;
    }

    @Override
    protected void initializeTypes() {
        this.supportedTypes.add(DataSeriesType.PEPTIDE);

        this.visibleTypes.add(DataSeriesType.PEPTIDE);
    }

    @Override
    protected void processData(ExperimentSummaryData summaryData) {
        List<Double> scores = new ArrayList<Double>();
        for (ProteinPeptide proteinPeptide : summaryData.getProteinsPeptides()) {
            PeptideScore ps = proteinPeptide.getScores();
            if(ps!=null){
                Map<CvTermReference, Number> map = ps.getPeptideScores(SearchEngineType.MASCOT);
                for (CvTermReference term : map.keySet()) {
                    if(CvTermReference.MASCOT_SCORE.equals(term)){
                        scores.add(map.get(term).doubleValue());
                    }
                }
            }
        }

        if(scores.size()==0){
            recordError("No Mascot scores found");
            return;
        }

        Double minScore = 0.0; // Collections.min(scores);
        Double maxScore = Collections.max(scores);

        if(maxScore==minScore || maxScore==0){
            recordError("The scores do not contain correct values");
            return;
        }

        Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
        double barWidth = (maxScore - minScore) / NUMBER_OF_BARS;
        for (Double score : scores) {
            int bin = (int) Math.floor(score/barWidth);
            int value = histogram.containsKey(bin)? histogram.get(bin)+1: 1;
            histogram.put(bin, value);
        }

        List<SeriesPair<Double,Integer>> data = new ArrayList<SeriesPair<Double,Integer>>();
        for(int bin= 0; bin<= NUMBER_OF_BARS; bin++){
            double x = bin * barWidth;
            int y = histogram.containsKey(bin)? histogram.get(bin)+1: 0;
            data.add(new SeriesPair<Double, Integer>(x, y));
        }
        DataSeries series = new DataSeries<Double,Integer>(DataSeriesType.PEPTIDE, "Scores", data);
        intermediateData = new IntermediateData(series);

        try {
            intermediateData.setVariable("binWidth",barWidth);
        } catch (JSONException e) {/*Nothing here*/}
    }

    @Override
    protected void setChart() {
        SeriesManager<Double,Integer> sf = new SeriesManager<Double,Integer>(intermediateData);
        List<DataSeries<Double,Integer>> seriesList = sf.getSeries();
        double width = intermediateData.getDouble("binWidth");
        double barWidth = width;// - width/3;
        IntervalXYDataset dataset = new ScoreIntervalXYDataset(NUMBER_OF_BARS, barWidth, seriesList);

        chart = ChartFactory.createHistogram(
                getChartTitle(),            // chart title
                "Mascot Score",             // x axis label
                "Frequency",                // y axis label
                dataset,                    // chartData
                PlotOrientation.VERTICAL,
                false,                      // include legend
                true,                       // tooltips
                false                       // urls
        );
        chart.addSubtitle(new TextTitle(getChartSubTitle()));

        XYPlot plot = (XYPlot) chart.getPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();

        plot.getRangeAxis().setUpperMargin(CHART_UPPER_MARGIN);
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinePaint(Color.red);
        plot.setRangeGridlinePaint(Color.blue);

        renderer.setShadowVisible(false);
        renderer.setDrawBarOutline(false);
    }

    @Override
    public String getChartTitle() {
        return "Mascot Score Representation";
    }

    @Override
    public String getChartShortTitle() {
        return "Mascot Score";
    }

    @Override
    public String getChartSubTitle() {
        return "per PRIDE experiment";
    }
}
