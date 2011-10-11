package uk.ac.ebi.pride.data.core;

import java.util.List;
import java.util.Map;

/**
 * ToDo: too many constructors
 *
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
     * Each sample could have a parent Sample, this relation is defined in the MzIdentMl Files.
     */
    private List<Sample> subSamples = null;
    /**
     * Contact Role could be defined as a Person and a specific role (CVTerms)
     */
    private Map<AbstractContact, CvParam> contactRoleList = null;

    /**
     * Constructor for MZIndentMl Sample.
     * @param id
     * @param name
     * @param subSamples
     * @param contactRoleList
     */
    public Sample(String id,
                  String name,
                  List<Sample> subSamples,
                  Map<AbstractContact, CvParam> contactRoleList) {
        this(null, id, name, subSamples, contactRoleList);
    }

    /**
     * Constructor for MZIndentMl Sample.
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param subSamples
     * @param contactRoleList
     */
    public Sample(List<CvParam> cvParams,
                  List<UserParam> userParams,
                  String id,
                  String name,
                  List<Sample> subSamples,
                  Map<AbstractContact,CvParam> contactRoleList) {
        this(new ParamGroup(cvParams, userParams), id, name, subSamples, contactRoleList);
    }

    /**
     * Constructor for MzML Sample Pride Object.
     * @param id
     * @param name
     */
    public Sample(String id,
                  String name) {
        this(null, id, name, null, null);
    }

    /**
     * Constructor for MzML Sample Pride Object
     * @param params
     * @param id
     * @param name
     */
    public Sample(ParamGroup params,
                  String id,
                  String name) {
        this(params, id, name, null, null);
    }

    /**
     * Constructor for MzML Sample Object.
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     */
    public Sample(List<CvParam> cvParams,
                  List<UserParam> userParams,
                  String id,
                  String name) {
        this(new ParamGroup(cvParams, userParams), id, name, null, null);
    }

    /**
     * Constructor for MZIndentMl Sample.
     * @param params
     * @param id
     * @param name
     * @param subSamples
     * @param contactRoleList
     */
    public Sample(ParamGroup params,
                  String id,
                  String name,
                  List<Sample> subSamples,
                  Map<AbstractContact, CvParam> contactRoleList) {
        super(params, id, name);
        this.subSamples = subSamples;
        this.contactRoleList = contactRoleList;
    }

    public List<Sample> getParentSample() {
        return subSamples;
    }

    public void setParentSample(List<Sample> subSamples) {
        this.subSamples = subSamples;
    }

    public Map<AbstractContact, CvParam> getContactRoleList() {
        return contactRoleList;
    }

    public void setContactRoleList(Map<AbstractContact, CvParam> contactRoleList) {
        this.contactRoleList = contactRoleList;
    }
}
