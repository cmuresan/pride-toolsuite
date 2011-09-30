package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Extends the Exception class for creating a specific one for the spectrum chartData.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 14:53:56
 */
public class SpectrumDataException extends Exception {
    public SpectrumDataException() {}

    public SpectrumDataException(String msg) {
        super(msg);
    }
}
