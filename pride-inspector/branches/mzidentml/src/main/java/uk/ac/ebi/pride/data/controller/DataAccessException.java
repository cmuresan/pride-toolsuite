package uk.ac.ebi.pride.data.controller;

/**
 * DataAccessException is thrown when there is an error during i/o via data access controller
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:45:27
 */
public class DataAccessException extends Exception {
    private final String    desc;
    private final Exception origin;

    public DataAccessException(String desc, Exception ex) {
        this.desc   = desc;
        this.origin = ex;
    }

    public String toString() {
        return desc + origin.toString();
    }
}



