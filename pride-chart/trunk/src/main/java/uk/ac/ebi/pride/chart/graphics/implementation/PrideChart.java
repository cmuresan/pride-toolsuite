package uk.ac.ebi.pride.chart.graphics.implementation;

import org.jfree.chart.JFreeChart;
import org.json.JSONException;
import uk.ac.ebi.pride.chart.graphics.implementation.data.DataSeriesType;
import uk.ac.ebi.pride.chart.graphics.implementation.data.IntermediateData;
import uk.ac.ebi.pride.chart.graphics.implementation.data.QuartilesType;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartLegend;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartSpectraOptions;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartQuartiles;
import uk.ac.ebi.pride.chart.model.implementation.ExperimentSummaryData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Defines all common functions for a Pride Chart class generator.</p>
 *
 * @author Antonio Fabregat
 * Date: 16-jul-2010
 * Time: 11:48:43
  */

public abstract class PrideChart {
    /**
     * Contains the top margin default value
     */
    protected static final double CHART_UPPER_MARGIN = 0.15;

    /**
     * Contains the intermediate data for creating the chart (in JSON format)
     */
    protected IntermediateData intermediateData = new IntermediateData();

    /**
     * Contains the histogram chart generated from the chartData of a PRIDE experiment
     */
    protected JFreeChart chart = null;

     /**
     * Contains the supported Data Series Types
     */
    protected List<DataSeriesType> supportedTypes = new ArrayList<DataSeriesType>();

    /**
     * Contains the visible Data Series Types
     */
    protected Set<DataSeriesType> visibleTypes = new HashSet<DataSeriesType>();

    /**
     * Contains the supported quartiles as Data Series Types
     */
    protected List<QuartilesType> supportedQuartiles = new ArrayList<QuartilesType>();

    /**
     * Contains the visible quatiles as Data Series Types
     */
    protected QuartilesType visibleQuartiles = QuartilesType.NONE;

    /**
     * <p> Creates an instance of this PrideChart object, setting all fields as per description below.</p>
     * All the child classes has to call this constructor with super(spectralSummaryData) and later call the methods
     * processData and setChart
     *
     * @param summaryData all the spectralSummaryData chartData of a PRIDE experiment
     */
    public PrideChart(ExperimentSummaryData summaryData) {
        if(summaryData == null)
            recordError("Summary data could not be retrieved from the experiment");

        if(isDataConsistent(summaryData))
            processData(summaryData);
        
        initializeTypes();
        initializeQuartiles();
    }

    /**
     * <p> Creates an instance of this PrideChart object, setting all fields as per description below.</p>
     *
     * @param jsonData the chart intermediate data (JSon format)
     */
    protected PrideChart(String jsonData) {
        try {
            this.intermediateData = new IntermediateData(jsonData);
        } catch (JSONException e) {
            recordError(e.getMessage());
        }

        initializeTypes();
        initializeQuartiles();
    }

    /**
     * Returns true if the chartData is consistent
     *
     * @param summaryData the summary data retrieved from the database or files
     * @return true if the chartData is consistent
     */
    protected abstract boolean isDataConsistent(ExperimentSummaryData summaryData);

    /**
     * Initialize the supported and visible types for each chart 
     */
    protected abstract void initializeTypes();

    /**
     * Initialize the supported and visible quartiles 
     */
    protected void initializeQuartiles(){
       /*Nothing here - To be override when necessary*/
    }

    /**
     * Process the summaryData chartData and save the result in an intermediate structure
     *
     * @param summaryData the summary data retrieved from the database or files
     */
    protected abstract void processData(ExperimentSummaryData summaryData);

    /**
     * Add the error message to the intermediate data error list
     *
     * @param msg error message to be added
     */
    protected void recordError(String msg){
        intermediateData.addErrorMessage(msg);
    }
    
    /**
     * Use the chartData from the intermediate structure and create the jFreeChart object
     */
    protected abstract void setChart();

    /**
     * Returns a JFreeChart object containing the bar chart of precursor charge for all spectra.
     *
     * @return JFreeChart an object containing the chart
     * @throws PrideChartException a pride chart exception
     */
    public final JFreeChart getChart() throws PrideChartException {
        // setChart is called if there is enough data for creating the chart and it does not exist
        if( intermediateData.isValid() && chart==null ) setChart();
        // Otherwise, if something has happened isValid will be false
        // and errorMessages will contain the reasons why the chart could not be created
        if(!isValid()){
            throw new PrideChartException(intermediateData.getErrorMessages());
        }
        return chart;
    }

    /**
     * Return a list of supported types
     *
     * @return a list of supported types
     */
    public List<DataSeriesType> getSupportedTypes() {
        return this.supportedTypes;
    }

    /**
     * Returns a set of visible types
     *
     * @return a set of visible types
     */
    public Set<DataSeriesType> getVisibleTypes() {
        return this.visibleTypes;
    }

    /**
     * Return a list of supported quartiles
     *
     * @return a list of supported quartiles
     */
    public List<QuartilesType> getSupportedQuartiles() {
        return this.supportedQuartiles;
    }

    /**
     * Returns a set of visible quartiles
     *
     * @return a set of visible quartiles
     */
    public QuartilesType getVisibleQuartile() {
        return this.visibleQuartiles;
    }

    /**
     * Returns true if there are not errors reported
     *
     * @return true if there are not errors reported
     */
    public boolean isValid(){
        return intermediateData.isValid();
    }

    /**
     * Returns a list of the reported error messages
     *
     * @return a list of the reported error messages
     */
    public List<String> getErrorMessages(){
        return intermediateData.getErrorMessages();
    }

    /**
     * Returns the chart intermediate data (in JSon format)
     *
     * @return the chart intermediate data (in JSon format)
     */
    public String getChartJSonData(){
        return intermediateData.toString();
    }

    /**
     * In this method the supportedTypes and visibleTypes has to be initialized
     */
    //protected abstract void initializeTypes();

    /**
     * Returns the title of the chart
     *
     * @return the title of the chart
     */
    public abstract String getChartTitle();

    /**
     * Returns the short title of the chart
     *
     * @return the title of the chart
     */
    public abstract String getChartShortTitle();

    /**
     * Returns the subtitle of the chart
     *
     * @return the subtitle of the chart
     */
    public abstract String getChartSubTitle();

    /**
     * Returns true if the chart has an extra legend
     *
     * @return true if the chart has an extra legend
     */
    public boolean hasLegend(){
        return this instanceof PrideChartLegend;
    }

    /**
     * Returns true if the chart has options to be displayed
     *
     * @return true if the chart has options to be displayed
     */
    public boolean hasSpectraOptions(){
        return this instanceof PrideChartSpectraOptions;
    }

    /**
     * Returns true if the chart has quartiles to be displayed
     *
     * @return true if the chart has quartiles to be displayed
     */
    public boolean hasQuartiles(){
        return this instanceof PrideChartQuartiles;
    }

    /**
     * To be called with super.setSpectraTypeVisibility when the child class implements the
     * PrideChartSpectraOptions interface
     *
     * @param type the type to change the visibility
     * @param visible the new visibility state
     */
    protected void setTypeVisibility(DataSeriesType type, boolean visible) {
        if(visible){
            if(supportedTypes.contains(type)) this.visibleTypes.add(type);
        }else{
            this.visibleTypes.remove(type);
        }
    }

    public void refreshChart(){
        setChart();
    }

    /**
     * To be called with super.setSpectraTypeVisibility when the child class implements the
     * PrideChartSpectraOptions interface
     *
     * @param type the type to change the visibility
     * @param visible the new visibility state
     */
    protected void setQuartileVisibility(QuartilesType type, boolean visible) {
        if(visible){
            if(supportedQuartiles.contains(type))
                this.visibleQuartiles = type;
        }else{
            this.visibleQuartiles = QuartilesType.NONE;
        }
    }
}