package uk.ebi.pride.jmzidentml.validator.controller;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: yperez
 * Date: 5/30/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MzIdentMLDataAccessControlller {

    /**
     * Get the unique id represent the uniqueness of the data source
     *
     * @return String    uid
     */
    public String getUid();

    /**
     * Get the display name for this controller
     *
     * @return String the name of this DataAccessController
     */
    public String getName();

    /**
     * Get the original data source object
     *
     * @return Object   data source object
     */
    public Object getSource();

    /**
     * shutdown this controller, release all the resources.
     */
    public void close();

    public void setName(String name);

    public void populateCache();




}
