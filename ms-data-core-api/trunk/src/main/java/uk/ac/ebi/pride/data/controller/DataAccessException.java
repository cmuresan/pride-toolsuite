package uk.ac.ebi.pride.data.controller;

/**
 * DataAccessException is thrown when there is an error during i/o via data access controller
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:45:27
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}



