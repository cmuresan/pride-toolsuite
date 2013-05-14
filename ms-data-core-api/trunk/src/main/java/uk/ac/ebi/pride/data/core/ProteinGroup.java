package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * A set of logically related results from a protein detection, for example to represent conflicting assignments of
 * peptides to proteins
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ProteinGroup extends IdentifiableParamGroup{

    private final List<Protein> proteinDetectionHypothesis;

    public ProteinGroup(Comparable id, String name, List<Protein> proteinDetectionHypothesis) {
        super(id, name);
        this.proteinDetectionHypothesis = CollectionUtils.createListFromList(proteinDetectionHypothesis);
    }

    public ProteinGroup(ParamGroup params, Comparable id, String name, List<Protein> proteinDetectionHypothesis) {
        super(params, id, name);
        this.proteinDetectionHypothesis = CollectionUtils.createListFromList(proteinDetectionHypothesis);
    }

    public List<Protein> getProteinDetectionHypothesis() {
        return proteinDetectionHypothesis;
    }

    public void setProteinDetectionHypothesis(Collection<Protein> proteinDetectionHypothesis) {
        CollectionUtils.replaceValuesInCollection(proteinDetectionHypothesis, this.proteinDetectionHypothesis);
    }
}
