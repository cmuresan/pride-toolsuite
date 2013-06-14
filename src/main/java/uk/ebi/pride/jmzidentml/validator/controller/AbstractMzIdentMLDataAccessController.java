package uk.ebi.pride.jmzidentml.validator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractMzIdentMLDataAccessController provides an abstract implementation of DataAccessController.
 * This is solely based on getting the data directly from data source.
 * <p/>
 * User: rwang, yperez
 * Date: 03-Feb-2010
 * Time: 12:22:24
 */
public abstract class AbstractMzIdentMLDataAccessController implements MzIdentMLDataAccessControlller{

    private static final Logger logger = LoggerFactory.getLogger(AbstractMzIdentMLDataAccessController.class);

    /**
     * Unique id to identify the data access controller
     */
    private String uid;
    /**
     * The name of the data source for displaying purpose
     */
    private String name;
    /**
     * Data source, such as: File
     */
    private Object source;


    protected AbstractMzIdentMLDataAccessController() {
        this(null);
    }

    protected AbstractMzIdentMLDataAccessController(Object source) {
        setSource(source);
    }

    @Override
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getSource() {
        return source;
    }

    public void setSource(Object src) {
        this.source = src;
    }

    @Override
    public void close() {
    }

    public abstract boolean hasSpectrum();
}
