package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Description of the way in which a List of Software were used. This structure is used by mzMl to store
 * the information of each Step of Data Processing. The Map structure represent the relation between an
 * specific Software and a List of CvPrarams related with this software.
 *
 * User: rwang, yperez
 * Date: 04-Feb-2010
 * Time: 16:04:58
 */
public class DataProcessing extends Identifiable {

    /**
     * Description of the default peak processing method, this is a ordered List
     * processing Methods is the relation between a Software an a Group of Param.
     *
     */
    private List<ProcessingMethod> processingMethods = null;

    /**
     *
     * @param id
     * @param name
     * @param processingMethods
     */
    public DataProcessing(Comparable id,
                          String name,
                          List<ProcessingMethod> processingMethods) {
        super(id, name);
        this.processingMethods = processingMethods;
    }

    /**
     *
     * @param id
     * @param processingMethods
     */
    public DataProcessing(Comparable id, List<ProcessingMethod> processingMethods) {
        super(id, null);
        this.processingMethods = processingMethods;
    }

    public List<ProcessingMethod> getProcessingMethods() {
        return processingMethods;
    }

    public void setProcessingMethods(List<ProcessingMethod> processingMethods) {
        this.processingMethods = processingMethods;
    }
}
