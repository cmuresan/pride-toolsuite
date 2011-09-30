package uk.ac.ebi.pride.chart.model.implementation;

/**
 * <p>Extends the Exception class for creating a specific one for the ProteinPeptide.</p>
 *
 * @author Antonio Fabregat
 * Date: 03-nov-2010
 * Time: 9:55:41
 */
public class ProteinPeptideException extends Exception {
    public ProteinPeptideException() {
    }

    public ProteinPeptideException(String msg) {
        super(msg);
    }
}
