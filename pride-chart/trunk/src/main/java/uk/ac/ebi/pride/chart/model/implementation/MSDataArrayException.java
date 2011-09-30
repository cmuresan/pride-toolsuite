package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Extends the Exception class for creating a specific one for the MS chartData array.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 11:02:31
 */
public class MSDataArrayException extends Exception {
    public MSDataArrayException() {}

    public MSDataArrayException(String msg) {
        super(msg);
    }
}
