package uk.ac.ebi.pride.chart.graphics.implementation.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Manage the intermediate data for a PrideChart object (JSon format)</p>
 * Addition to store the series data, this object could contain other variables
 * for storing useful information for the chart generation (i.e. the experiment size)
 *
 * @author Antonio Fabregat
 * Date: 22-oct-2010
 * Time: 10:12:30
 */
public class IntermediateData {
    /**
     * Defines the key for the stored error messages
     */
    private static final String ERROR_KEY = "ErrorMessages";

    /**
     * Defines the key for the stored data series
     */
    private static final String SERIES_KEY = "Series";

    /**
     * Contains an unordered collection of name/value pairs
     */
    private JSONObject data;

    /**
     * <p> Creates an instance of this IntermediateData object.</p>
     */
    public IntermediateData() {
        data = new JSONObject();
    }

    /**
     * <p> Creates an instance of this IntermediateData object, setting all fields as per description below.</p>
     *
     * @param data a string containing the data in JSON format
     * @throws org.json.JSONException if there is a syntax error in the source string or a duplicated key
     */
    public IntermediateData(String data) throws JSONException {
        this.data = new JSONObject(data);
    }
    
    /**
     * <p> Creates an instance of this IntermediateData object, setting all fields as per description below.</p>
     *
     * @param series a DataSeries object containing a SeriesPair
     */
    public IntermediateData(DataSeries series) {
        data = new JSONObject();
        addPrideChartSerie(series);
    }

    /**
     * <p> Creates an instance of this IntermediateData object, setting all fields as per description below.</p>
     *
     * @param errorMessages a list of errors for which the graph could not be created
     */
    public IntermediateData(List<String> errorMessages){
        this.data = new JSONObject();
        try {
            this.data.put(ERROR_KEY, new JSONArray(errorMessages));
        } catch (JSONException e) {/*Nothing here*/}
    }


    public void addErrorMessage(String msg){
        try {
            this.data.append(ERROR_KEY, msg);
        } catch (JSONException e) {/*Nothing here*/}
    }
    /**
     * Add a pair series to the intermediate data
     *
     * @param series a DataSeries object containing a SeriesPair
     */
    public void addPrideChartSerie(DataSeries series){
        try {
            JSONObject jsonObj = series.getJSONObject();
            this.data.append(SERIES_KEY, jsonObj);
        } catch (JSONException e) {/*Nothing here*/}
    }

    /**
     * Returns a list of error messages (if the chart could not be generated)
     *
     * @return a list of error messages (if the chart could not be generated)
     */
    public List<String> getErrorMessages(){
        List<String> errorMessages = new ArrayList<String>();
        try {
            JSONArray list = data.getJSONArray(ERROR_KEY);
            for (int i=0 ; i < list.length(); i++){
                String msg = (String) list.get(i);
                errorMessages.add(msg);
            }
        } catch (JSONException e) {/*Nothing here*/}

        return errorMessages;
    }

    /**
     * Returns true if this object contains data for creating the chart
     *
     * @return true if this object contains data for creating the chart
     */
    public boolean isValid(){
        return !data.has(ERROR_KEY);
    }

    /**
     * Sets a variable with useful information for the chart generation
     *
     * @param name the name of the variable to store (if exists, is overwritten)
     * @param variable an object with useful information for the chart generation
     * @throws org.json.JSONException if the key is null
     */
    public void setVariable(String name, Object variable) throws JSONException {
        if(ERROR_KEY.equals(name) || SERIES_KEY.equals(name))
            throw new JSONException("Adding a variable with a PRIDE-Chart JSon reserved name");
        data.put(name, variable);
    }

    public String getString(String name) {
        try {
            return data.getString(name);
        } catch (JSONException e) {
            return "";
        }
    }

    public long getLong(String name){
        try {
            return data.getLong(name);
        } catch (JSONException e) {
            return 0;
        }
    }

    public int getInteger(String name){
        try {
            return data.getInt(name);
        } catch (JSONException e) {
            return 0;
        }
    }

    public double getDouble(String name){
        try {
            return data.getDouble(name);
        } catch (JSONException e) {
            return 0.0;
        }
    }

    /**
     * Returns a JSONArray object with the series data
     *
     * @return a JSONArray object with the series data 
     */
    public JSONArray getSeriesJSonArray(){
        JSONArray seriesObject = null;
        try {
            seriesObject = data.getJSONArray(SERIES_KEY);
        } catch (JSONException e) {/*Nothing here*/}
        return seriesObject;
    }

    /**
     * Make a JSON text of this JSONObject. For compactness, no whitespace is added. If this would not result in a syntactically correct JSON text, then null will be returned instead.
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @return a printable, displayable, portable, transmittable representation of the object, beginning with { (left brace) and ending with } (right brace).
     */
    @Override
    public String toString() {
        return data.toString();
    }
}
