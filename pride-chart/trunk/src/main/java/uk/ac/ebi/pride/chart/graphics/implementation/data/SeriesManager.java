package uk.ac.ebi.pride.chart.graphics.implementation.data;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A generic class to handle the list of data series creation in every pride chart object</p>
 *
 * @author Antonio Fabregat
 * Date: 03-nov-2010
 * Time: 11:38:10
 */
public class SeriesManager<T,U> {
    /**
     * Contains the intermediate to manage
     */
    private IntermediateData intermediateData;

    /**
     * <p> Creates an instance of this SeriesManager object, setting all fields as per description below.</p>
     *
     * @param intermediateData the pride chart intermediate data
     */
    public SeriesManager(IntermediateData intermediateData) {
        this.intermediateData = intermediateData;
    }

    /**
     * Returns the series stored in the intermediate data
     *
     * @return the series stored in the intermediate data
     */
    public List<DataSeries<T,U>> getSeries(){
        List<DataSeries<T,U>> seriesList = new ArrayList<DataSeries<T,U>>();
        JSONArray seriesAux = intermediateData.getSeriesJSonArray();
        try {
            for(int i=0; i<seriesAux.length(); i++){
                seriesList.add(new DataSeries<T,U>(seriesAux.getJSONObject(i)));
            }
        } catch (JSONException e) {/*Nothing here*/}
        return seriesList;
    }
}
