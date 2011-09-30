package uk.ac.ebi.pride.chart.graphics.implementation;

import uk.ac.ebi.pride.chart.graphics.implementation.charts.*;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The PRIDE-Chart objects factory.</p>
 *
 * @author Antonio Fabregat
 * Date: 04-oct-2010
 * Time: 11:39:15
 */
public abstract class PrideChartFactory {
    /**
     * Next lines contains the associated number of each chart in the PRIDE database
     */
    public static final int IntensityHistogram = 1;
    public static final int FrequencyPrecursorIonCharge = 2;
    public static final int MZHistogram = 3;
    public static final int PrecursorMassesDistribution = 4;
    public static final int ProteinsPeptides = 5;
    public static final int PeaksHistogram = 6;
    public static final int MassDelta = 7;
    public static final int MissedCleavages = 8;
    public static final int MascotScore = 9;

    /**
     * Defines the charts to be displayed by default as well as the order
     */
    private static List<Integer> chartOrder = new ArrayList<Integer>();
    static{
        chartOrder.add(MassDelta);
        chartOrder.add(ProteinsPeptides);
        chartOrder.add(MissedCleavages);
        chartOrder.add(MZHistogram);
        chartOrder.add(FrequencyPrecursorIonCharge);
        chartOrder.add(PrecursorMassesDistribution);
        chartOrder.add(PeaksHistogram);
        chartOrder.add(IntensityHistogram);
        //chartOrder.add(MascotScore);
    }

    /**
     * Returns a list of all the charts
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     * @return a list of all the charts
     */
    public static List<PrideChart> getAllCharts(ExperimentSummaryData summaryData){
        List<PrideChart> prideCharts = new ArrayList<PrideChart>();

        for (Integer chart_id : chartOrder) {
            switch(chart_id){
                case IntensityHistogram:
                    prideCharts.add(new IntensityHistogramChartSpectra(summaryData));
                    break;
                case FrequencyPrecursorIonCharge:
                    prideCharts.add(new FreqPrecIonChargeChart(summaryData));
                    break;
                case MZHistogram:
                    prideCharts.add(new MZHistogramChartSpectra(summaryData));
                    break;
                case PrecursorMassesDistribution:
                    prideCharts.add(new PreMSvsTheMSChartSpectra(summaryData));
                    break;
                case ProteinsPeptides:
                    prideCharts.add(new ProteinsPeptidesChart(summaryData));
                    break;
                case PeaksHistogram:
                    prideCharts.add(new PeaksHistogramChart(summaryData));
                    break;
                case MassDelta:
                    prideCharts.add(new MassDeltaChart(summaryData));
                    break;
                case MissedCleavages:
                    prideCharts.add(new MissedCleavagesChart(summaryData));
                    break;
                case MascotScore:
                    prideCharts.add(new MascotScoreChart(summaryData));
                    break;
            }
        }
        return prideCharts;
    }

    /**
     * Returns a list of all the charts which correspond to the indicated category
     *
     * @param summaryData all the summaryData chartData of a PRIDE experiment
     * @param category the expected category
     * @return a list of all the charts which correspond to the indicated category
     */
    public static List<PrideChart> getAllCategoryCharts(ExperimentSummaryData summaryData, PrideChartCategory category){
        List<PrideChart> prideCharts = new ArrayList<PrideChart>();

        for (Integer chart_id : chartOrder) {
            if(PrideChartCategory.isOfCategory(chart_id, category))
                prideCharts.add(getChart(chart_id, summaryData));
        }

        return prideCharts;
    }

    /**
     * Returns a specified pride chart object in function of the id number and the json object
     *
     * @param id the id of chart to return
     * @param jsonData the intermediate chart data in JSon format 
     * @return a specified pride chart object
     * @throws IllegalArgumentException if the chart id does not match with any of the predefined chart identifiers
     */
    public static PrideChart getChart(int id, String jsonData) throws IllegalArgumentException{
        PrideChart prideChart;
        switch(id){
            case FrequencyPrecursorIonCharge:
                prideChart = new FreqPrecIonChargeChart(jsonData);
                break;
            case IntensityHistogram:
                prideChart = new IntensityHistogramChartSpectra(jsonData);
                break;
            case MassDelta:
                prideChart = new MassDeltaChart(jsonData);
                break;
            case MZHistogram:
                prideChart = new MZHistogramChartSpectra(jsonData);
                break;
            case PeaksHistogram:
                prideChart = new PeaksHistogramChart(jsonData);
                break;
            case PrecursorMassesDistribution:
                prideChart = new PreMSvsTheMSChartSpectra(jsonData);
                break;
            case ProteinsPeptides:
                prideChart = new ProteinsPeptidesChart(jsonData);
                break;
            case MissedCleavages:
                prideChart = new MissedCleavagesChart(jsonData);
                break;
            case MascotScore:
                prideChart = new MascotScoreChart(jsonData);
                break;

            default:
                throw new IllegalArgumentException("\'" + id + "\' does not match with any of the predefined chart types");
        }
        return prideChart;
    }

    /**
     * Returns a specified pride chart object in function of the id number and the summary data object
     *
     * @param id the id of chart to return
     * @param summaryData all the summaryData chartData of a PRIDE experiment 
     * @return a specified pride chart object
     * @throws IllegalArgumentException if the chart id does not match with any of the predefined chart identifiers
     */
    public static PrideChart getChart(int id, ExperimentSummaryData summaryData) throws IllegalArgumentException{
        PrideChart prideChart;
        switch(id){
            case FrequencyPrecursorIonCharge:
                prideChart = new FreqPrecIonChargeChart(summaryData);
                break;
            case IntensityHistogram:
                prideChart = new IntensityHistogramChartSpectra(summaryData);
                break;
            case MassDelta:
                prideChart = new MassDeltaChart(summaryData);
                break;
            case MZHistogram:
                prideChart = new MZHistogramChartSpectra(summaryData);
                break;
            case PeaksHistogram:
                prideChart = new PeaksHistogramChart(summaryData);
                break;
            case PrecursorMassesDistribution:
                prideChart = new PreMSvsTheMSChartSpectra(summaryData);
                break;
            case ProteinsPeptides:
                prideChart = new ProteinsPeptidesChart(summaryData);
                break;
            case MissedCleavages:
                prideChart = new MissedCleavagesChart(summaryData);
                break;
            case MascotScore:
                prideChart = new MascotScoreChart(summaryData);
                break;

            default:
                throw new IllegalArgumentException("\'" + id + "\' does not match with any of the predefined chart types");
        }
        return prideChart;
    }

    /**
     * Returns the identifier for a given PrideChart instance
     *
     * @param prideChart a PrideChart object
     * @return the identifier associated to the PrideChart instance
     * @throws PrideChartException a pride chart exception
     */
    public static int getPrideChartIdentifier(PrideChart prideChart) throws PrideChartException {
        if (prideChart instanceof FreqPrecIonChargeChart)
            return FrequencyPrecursorIonCharge;

        if (prideChart instanceof IntensityHistogramChartSpectra)
            return IntensityHistogram;

        if (prideChart instanceof MassDeltaChart)
            return MassDelta;

        if (prideChart instanceof MZHistogramChartSpectra)
            return MZHistogram;

        if (prideChart instanceof PeaksHistogramChart)
            return PeaksHistogram;

        if (prideChart instanceof PreMSvsTheMSChartSpectra)
            return PrecursorMassesDistribution;

        if (prideChart instanceof ProteinsPeptidesChart)
            return ProteinsPeptides;

        if (prideChart instanceof MissedCleavagesChart)
            return MissedCleavages;

        if (prideChart instanceof MascotScoreChart)
            return MascotScore;

        String className = prideChart.getClass().getSimpleName();
        throw new PrideChartException( className + " has not an associated identifier");
    }

    /**
     * Returns the default chart order defined in this class
     *
     * @return the default chart order defined in this class
     */
    public static List<Integer> getChartOrder(){
        return chartOrder;
    }
}