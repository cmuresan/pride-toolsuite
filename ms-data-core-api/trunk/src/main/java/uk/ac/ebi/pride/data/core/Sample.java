package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.data.utils.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * <p/>
 * Description of the sample used to generate the Dataset.
 * This Sample class represent the basic information contained in the mzMl files.
 * If the Object instance is MzMl Sample the Sample parent and the contactRoleList must be null;
 * <p/>
 * User: rwang, yperez
 * Date: 04-Feb-2011
 * Time: 15:50:55
 */
public class Sample extends IdentifiableParamGroup {

    /**
     * Contact Role could be defined as a Person and a specific role (CVTerms)
     */
    private Map<AbstractContact, CvParam> contactRoleList;

    /**
     * Each sample could have a parent Sample, this relation is defined in the MzIdentMl Files.
     */
    private List<Sample> subSamples;


    /**
     * Constructor for MzML Sample Pride Object
     *
     * @param params  ParamGroup
     * @param id      ID
     * @param name    Name
     */
    public Sample(ParamGroup params, String id, String name) {
        this(params, id, name, null, null);
    }

    /**
     * Constructor for MZIndentMl Sample.
     *
     * @param params ParamGroup
     * @param id     ID
     * @param name   Name
     * @param subSamples  SubSamples
     * @param contactRoleList List of Contact Role
     */
    public Sample(ParamGroup params, String id, String name, List<Sample> subSamples,
                  Map<AbstractContact, CvParam> contactRoleList) {
        super(params, id, name);
        this.subSamples      = CollectionUtils.createListFromList(subSamples);
        this.contactRoleList = MapUtils.createMapFromMap(contactRoleList);
    }

    public List<Sample> getParentSample() {
        return subSamples;
    }

    public void setParentSample(List<Sample> subSamples) {
        CollectionUtils.replaceValuesInCollection(subSamples, this.subSamples);
    }

    public Map<AbstractContact, CvParam> getContactRoleList() {
        return contactRoleList;
    }

    public void setContactRoleList(Map<AbstractContact, CvParam> contactRoleList) {
        MapUtils.replaceValuesInMap(contactRoleList, this.contactRoleList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sample)) return false;
        if (!super.equals(o)) return false;

        Sample sample = (Sample) o;

        if (!contactRoleList.equals(sample.contactRoleList)) return false;
        if (!subSamples.equals(sample.subSamples)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + contactRoleList.hashCode();
        result = 31 * result + subSamples.hashCode();
        return result;
    }
}



