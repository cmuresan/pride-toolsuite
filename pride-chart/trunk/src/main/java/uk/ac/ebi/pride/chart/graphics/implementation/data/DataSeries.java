package uk.ac.ebi.pride.chart.graphics.implementation.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A wrapper class for a JSONObject containing the chart intermediate series data</p>
 *
 * @author Antonio Fabregat
 * Date: 25-oct-2010
 * Time: 13:49:49
 */
public class DataSeries<T,U> {
    /**
     * Defines the key for the series type
     */
    private static final String TYPE_KEY = "type";

    /**
     * Defines the key for the series identifier
     */
    private static final String ID_KEY = "id";

    /**
     * Defines the key for the stored x axis values
     */
    private static final String X_AXIS_KEY = "XAxis";

    /**
     * Defines the key for the stored y axis values
     */
    private static final String Y_AXIS_KEY = "YAxis";

    /**
     * Contains an unordered collection of name/value pairs
     */
    private JSONObject data;

    /**
     * <p> Creates an instance of this DataSeries object, setting all fields as per description below.</p>
     *
     * @param data the JSONObject to be managed in this wrapper class
     */
    public DataSeries(JSONObject data) {
        this.data = data;
    }

    /**
     * <p> Creates an instance of this DataSeries object, setting all fields as per description below.</p>
     *
     * @param type The series type
     * @param identifier The series identifier
     * @param data a list of SeriesPair containing the chart intermediate data
     */
    public DataSeries(DataSeriesType type, String identifier, List<SeriesPair<T,U>> data) {
        this.data = new JSONObject();
        try {
            if(type!=null) this.data.put(TYPE_KEY, type.getType());
            this.data.put(ID_KEY, identifier);
            for (SeriesPair<T, U> sp : data) {
                this.data.append(X_AXIS_KEY, sp.getX());
                this.data.append(Y_AXIS_KEY, sp.getY());
            }
        } catch (JSONException e) {/*Nothing here*/}
    }

    /**
     * Returns a list of SeriesPair containing the chart intermediate data
     *
     * @param classT the T.class
     * @param classU the U.class
     * @return a list of SeriesPair containing the chart intermediate data
     */
    public List<SeriesPair<T,U>> getSeriesValues(Class<T> classT, Class<U> classU){
        List<SeriesPair<T,U>> valuesArray = new ArrayList<SeriesPair<T,U>>();
        try {
            PrideJSONArray<T> xAxis = new PrideJSONArray<T>(data.getJSONArray(X_AXIS_KEY));
            PrideJSONArray<U> yAxis = new PrideJSONArray<U>(data.getJSONArray(Y_AXIS_KEY));

            for(int i=0; i<xAxis.size(); i++){
                T x = xAxis.get(i, classT);
                U y = yAxis.get(i, classU);
                valuesArray.add(new SeriesPair<T,U>(x, y));
            }
        } catch (JSONException e) {/*Nothing here*/}
        return valuesArray;
    }

    /**
     * Returns the Series identifier
     * 
     * @return the Series identifier
     */
    public String getIdentifier(){
        String identifier;
        try {
            identifier = (String) data.get(ID_KEY);
        } catch (JSONException e) {
            identifier = "";
        }
        return identifier;
    }

    /**
     * Returns the Series type label
     *
     * @return the Series type label
     */
    public String getTypeLabel(){
        String typeLabel;
        try {
            typeLabel = data.getString(TYPE_KEY);
        } catch (JSONException e) {
            typeLabel = "";
        }
        return typeLabel;
    }

    /**
     * Returns true if the series contains empty values in all the Y values
     *
     * @param classT the T.class
     * @param classU the U.class
     * @return true if the series contains empty values in all the Y values
     */
    public boolean isEmpty(Class<T> classT, Class<U> classU) {
        List<SeriesPair<T,U>> values = getSeriesValues(classT, classU);
        boolean isEmpty = true;
        for (SeriesPair<T, U> value : values) {
            U y = value.getY();
            if(String.class.equals(classU)) {
                isEmpty &= y.equals("");
            } else if (Double.class.equals(classU)) {
                isEmpty &= (Double) y == 0.0;
            } else if (Integer.class.equals(classU)) {
                isEmpty &= (Integer) y == 0.0;
            } else {
                //If the type is not supported, by default returns isEmpty<-true
                return true;
            }
        }
        return isEmpty;
    }

    public DataSeriesType getType(){
        return DataSeriesType.getSeriesType(getTypeLabel());
    }

    /**
     * Returns the JSONObject wrapped by the class
     *
     * @return the JSONObject wrapped by the class
     */
    public JSONObject getJSONObject(){
        return data;
    }

    /**
     * Returns the data contained in JSon Format
     *
     * @return the data contained in JSon Format
     */
    @Override
    public String toString() {
        return data.toString();
    }
}
