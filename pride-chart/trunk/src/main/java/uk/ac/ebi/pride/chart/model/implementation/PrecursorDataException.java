package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Extends the Exception class for creating a specific one for the Precursor Data.</p>
 *
 * @author Antonio Fabregat
 * Date: 15-oct-2010
 * Time: 11:06:22
 */
public class PrecursorDataException extends Exception {
    public PrecursorDataException() {}

    public PrecursorDataException(String msg) {
        super(msg);
    }
}
