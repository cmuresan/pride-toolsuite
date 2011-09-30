package uk.ac.ebi.pride.chart.graphics.implementation.data;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * <p>A wrapper class for a JSONArray containing the chart intermediate series data</p>
 *
 * @author Antonio Fabregat
 * Date: 02-nov-2010
 * Time: 15:49:37
 */
public class PrideJSONArray<T> {
    /**
     * Contains the JSONArray to manage in this wrapper
     */
    private JSONArray array;

    /**
     * <p> Creates an instance of this PrideJSONArray object, setting all fields as per description below.</p>
     *
     * @param array the JSONArray to manage with this wrapper class
     */
    public PrideJSONArray(JSONArray array) {
        this.array = array;
    }

    /**
     * Returns the Object in the indicated position as an instance of the class T
     *
     * @param i the position
     * @param classT the T.class
     * @return the Object in the indicated position as an instance of the class T
     * @throws JSONException a JSONArray exception or a 'type not supported' exception
     */
    public T get(int i, Class<T> classT) throws JSONException {
        Object aux = null;
        if(String.class.equals(classT)) {
            aux = array.getString(i);
        } else if (Double.class.equals(classT)) {
            aux = array.getDouble(i);
        } else if (Integer.class.equals(classT)) {
            aux = array.getInt(i);
        } else {
            throw new JSONException("Type not supported");
        }
        return classT.cast(aux);
    }

    /**
     * Returns the size of the contained JSONArray
     *
     * @return the size of the contained JSONArray
     */
    public int size(){
        return array.length();
    }
}
