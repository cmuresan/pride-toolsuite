package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Extends the Exception class for creating a specific one for the spectral chartData per PRIDE experiment.</p>
 *
 * @author Antonio Fabregat
 * Date: 13-jul-2010
 * Time: 16:48:16
 */
public class SpectralDataPerExperimentException extends Exception {
    public SpectralDataPerExperimentException() {}

    public SpectralDataPerExperimentException(String msg) {
        super(msg);
    }
}
