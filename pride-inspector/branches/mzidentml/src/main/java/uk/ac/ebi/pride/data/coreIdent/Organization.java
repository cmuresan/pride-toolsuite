package uk.ac.ebi.pride.data.coreIdent;

/**
 * Organizations are entities like companies, universities, government agencies. Any additional information such as the address,
 * email etc. should be supplied either as CV parameters or as user parameters.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 04/08/11
 * Time: 11:46

 */
public class Organization extends AbstractContact {

    private Organization parentOrganization = null;

    private String mail = null;

    /**
     * Organization Constructor
     * @param id  Identifier for Organization Object
     * @param name Name of the Organization
     * @param parentOrganization  Parent Organization
     */
    public Organization(Comparable id,
                        String name,
                        String mail,
                        Organization parentOrganization) {
        super(id, name);
        this.parentOrganization = parentOrganization;
        this.mail = mail;
    }

    /**
     * Organization Constructor
     * @param params
     * @param id
     * @param name
     * @param parentOrganization
     */
    public Organization(ParamGroup params,
                        Comparable id,
                        String name,
                        Organization parentOrganization,
                        String mail) {
        super(params, id, name);
        this.parentOrganization = parentOrganization;
        this.mail = mail;
    }

    /**
     * Create a PRIDE and MZML Organization Objects
     * @param params
     * @param name
     * @param mail
     */
    public Organization(ParamGroup params, String name, String mail){
        super(params,null,name);
        this.parentOrganization = null;
        this.mail = null;
    }

    /**
     *  The containing organization (the university or business which a lab belongs to, etc.)
     */
    public Organization getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(Organization parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
