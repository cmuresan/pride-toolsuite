package uk.ac.ebi.pride.exception;

/**
 * <p>Exception class for throwing an illegal AminoAcid sequence error</p>
 *
 * @author Antonio Fabregat
 * Date: 24-sep-2010
 * Time: 12:07:37
 */
public class IllegalAminoAcidSequenceException extends IllegalArgumentException {
    public IllegalAminoAcidSequenceException() {
    }

    public IllegalAminoAcidSequenceException(String s) {
        super(s);
    }

    public IllegalAminoAcidSequenceException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public IllegalAminoAcidSequenceException(Throwable throwable) {
        super(throwable);
    }
}
