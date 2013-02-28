package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * Description of the way in which a List of Software were used. This structure is used by mzMl to store
 * the information of each Step of Data Processing. The Map structure represent the relation between an
 * specific Software and a List of CvPrarams related with this software.
 * <p/>
 * User: rwang, yperez
 * Date: 04-Feb-2010
 * Time: 16:04:58
 */
public class DataProcessing extends Identifiable {

    /**
     * Description of the default peak processing method, this is a ordered List
     * processing Methods is the relation between a Software an a Group of Param.
     */
    private List<ProcessingMethod> processingMethods = null;

    /**
     * @param id  ID of the DataProcessing Object
     * @param processingMethods Processing Method List
     */
    public DataProcessing(Comparable id, List<ProcessingMethod> processingMethods) {
        super(id, null);
        this.processingMethods = processingMethods;
    }

    /**
     * Create of a DataProcessing Object
     *
     * @param id ID of the DataProcessing Object
     * @param name Name of the DataProcessing Object
     * @param processingMethods Processing Method List
     */
    public DataProcessing(Comparable id, String name, List<ProcessingMethod> processingMethods) {
        super(id, name);
        this.processingMethods = processingMethods;
    }

    /**
     * Get List of Processing Methods
     *
     * @return List<ProcessingMethod>
     */
    public List<ProcessingMethod> getProcessingMethods() {
        return processingMethods;
    }

    /**
     * Set a List of Processing Methods
     *
     * @param processingMethods A list of Processing Methods
     */
    public void setProcessingMethods(List<ProcessingMethod> processingMethods) {
        this.processingMethods = processingMethods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataProcessing that = (DataProcessing) o;

        return !(processingMethods != null ? !processingMethods.equals(that.processingMethods) : that.processingMethods != null);

    }

    @Override
    public int hashCode() {
        return processingMethods != null ? processingMethods.hashCode() : 0;
    }
}



