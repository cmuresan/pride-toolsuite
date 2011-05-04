package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:04:58
 */
public class DataProcessing {

    private String id = null;
    /** This is a ordered collection */
    private List<ProcessingMethod> processingMethods = null;

    public DataProcessing(String id, List<ProcessingMethod> methods) {
        this.id = id;
        this.processingMethods = methods;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ProcessingMethod> getProcessingMethods() {
        return processingMethods;
    }

    public void setProcessingMethods(List<ProcessingMethod> processingMethods) {
        this.processingMethods = processingMethods;
    }
}
