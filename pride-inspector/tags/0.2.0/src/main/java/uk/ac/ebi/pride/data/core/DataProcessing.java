package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Description of the way in which a particular software was used.
 * <p/>
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:04:58
 */
public class DataProcessing implements MassSpecObject {

    /**
     * unique identifier for this data processing object
     */
    private String id = null;
    /**
     * description of the default peak processing method, this is a ordered List
     */
    private List<ProcessingMethod> processingMethods = null;

    /**
     * Constructor
     *
     * @param id      required.
     * @param methods required and cannot be empty.
     */
    public DataProcessing(String id, List<ProcessingMethod> methods) {
        setId(id);
        setProcessingMethods(methods);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("DataProcessing's Id can not be NULL");
        } else {
            this.id = id;
        }
    }

    public List<ProcessingMethod> getProcessingMethods() {
        return processingMethods;
    }

    public void setProcessingMethods(List<ProcessingMethod> processingMethods) {
        // in PRIDE XML, processing methods can be null or empty.
        // in mzML however, processing methods should be always available.
        this.processingMethods = processingMethods;
    }
}
