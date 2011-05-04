package uk.ac.ebi.pride.data.controller;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:45:27
 */
public class DataAccessException extends Exception {
    private String desc;
    private Exception origin;

    public DataAccessException(String desc, Exception ex) {
        this.desc = desc;
        this.origin = ex;
    }

    public String toString(){
        return desc + origin.toString();
    }
}
