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

    /**
     *
     * @param id
     * @param name
     * @param parentOrganization
     */
    public Organization(Comparable id,
                        String name,
                        Organization parentOrganization) {
        super(id, name);
        this.parentOrganization = parentOrganization;
    }

    /**
     *
     * @param params
     * @param id
     * @param name
     * @param parentOrganization
     */
    public Organization(ParamGroup params,
                        Comparable id,
                        String name,
                        Organization parentOrganization) {
        super(params, id, name);
        this.parentOrganization = parentOrganization;
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
}
