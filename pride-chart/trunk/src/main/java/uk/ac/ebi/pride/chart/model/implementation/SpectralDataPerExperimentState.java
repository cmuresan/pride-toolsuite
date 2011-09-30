package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Container for the Spectral chartData of a PRIDE experiment state.</p>
 *
 * @author Antonio Fabregat
 * Date: 03-sep-2010
 * Time: 11:40:57
 */
public class SpectralDataPerExperimentState {

    private boolean precursorChargesLoaded = false;

    private boolean precursorMassesLoaded = false;

    public synchronized boolean isPrecursorChargesLoaded() {
        return precursorChargesLoaded;
    }

    public synchronized void setPrecursorChargesLoaded(boolean precursorChargesLoaded) {
        this.precursorChargesLoaded = precursorChargesLoaded;
    }

    public synchronized boolean isPrecursorMassesLoaded() {
        return precursorMassesLoaded;
    }

    public synchronized void setPrecursorMassesLoaded(boolean precursorMassesLoaded) {
        this.precursorMassesLoaded = precursorMassesLoaded;
    }
}
