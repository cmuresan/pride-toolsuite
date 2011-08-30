package uk.ac.ebi.pride.data.coreIdent;

/**
 *  Provider object.
 * <p/>
 * In mzIdentML 1.1.0., the following the description of this object:
 * <p/>
 * The Provider of the mzIdentML record in terms of the contact and software.
 * This object contains a reference to the last software used to generate the file
 * this software is called the provider.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 04/08/11
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */

public class Provider extends Identifiable {

    private Software analysisSoftware = null;
    /**
     * The Software that produced the document instance. mzIdentML
     */

    private IdentifiableParamGroup contactRef  = null;
    /**
     * A reference to the Contact person that provide the mzIdentMl File.
     * (mzIndetMl description: When a ContactRole is used, it specifies which Contact the role is associated with.
     */
    private CvParam role = null;

    /**
     *
     * @param id
     * @param name
     * @param analysisSoftware
     * @param contactRef
     * @param role
     */
    public Provider(String id, String name, Software analysisSoftware, IdentifiableParamGroup contactRef, CvParam role) {
        super(id, name);
        this.analysisSoftware = analysisSoftware;
        this.contactRef = contactRef;
        this.role = role;
    }

    public IdentifiableParamGroup getContactRef() {
        return contactRef;
    }

    public void setContactRef(IdentifiableParamGroup contactRef) {
        this.contactRef = contactRef;
    }

    public CvParam getRole() {
        return role;
    }

    public void setRole(CvParam role) {
        this.role = role;
    }

    /**
     * CV term for contact roles, such as software provider.
     */


    public Software getSoftware() {
        return analysisSoftware;
    }

    public void setSoftware(Software analysisSoftware) {
        this.analysisSoftware = analysisSoftware;
    }
}
