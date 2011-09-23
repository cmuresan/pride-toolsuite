package uk.ac.ebi.pride.data.coreIdent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
    private Sample parentSample = null;
    /**
     * Contact Role could be defined as a Person and a specific role (CVTerms)
     */
    private Map<AbstractContact, CvParam> contactRoleList = null;

    /**
     * Constructor for MZIndentMl Sample.
     * @param id
     * @param name
     * @param parentSample
     * @param contactRoleList
     */
    public Sample(String id,
                  String name,
                  Sample parentSample,
                  Map<AbstractContact, CvParam> contactRoleList) {
        super(id, name);
        this.parentSample = parentSample;
        this.contactRoleList = contactRoleList;
    }

    /**
     * Constructor for MZIndentMl Sample.
     * @param params
     * @param id
     * @param name
     * @param parentSample
     * @param contactRoleList
     */
    public Sample(ParamGroup params,
                  String id,
                  String name,
                  Sample parentSample,
                  Map<AbstractContact, CvParam> contactRoleList) {
        super(params, id, name);
        this.parentSample = parentSample;
        this.contactRoleList = contactRoleList;
    }

    /**
     * Constructor for MZIndentMl Sample.
     * @param cvParams
     * @param userParams
     * @param id
     * @param name
     * @param parentSample
     * @param contactRoleList
     */
    public Sample(List<CvParam> cvParams,
                  List<UserParam> userParams,
                  String id,
                  String name,
                  Sample parentSample,
                  Map<AbstractContact,CvParam> contactRoleList) {
        super(cvParams, userParams, id, name);
        this.parentSample = parentSample;
        this.contactRoleList = contactRoleList;
    }

    /**
     * Constructor for MzML Sample Pride Object.
     * @param id
     * @param name
     */
    public Sample(String id,
                  String name) {
        super(id, name);
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
        super(params, id, name);
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
        super(cvParams, userParams, id, name);
    }

    public Sample getParentSample() {
        return parentSample;
    }

    public void setParentSample(Sample parentSample) {
        this.parentSample = parentSample;
    }

    public Map<AbstractContact, CvParam> getContactRoleList() {
        return contactRoleList;
    }

    public void setContactRoleList(Map<AbstractContact, CvParam> contactRoleList) {
        this.contactRoleList = contactRoleList;
    }
}
