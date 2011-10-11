package uk.ac.ebi.pride.data.core;

/**
 * Contact Role is a class to define the role of an Specific Contact (Organization or Person)in the context
 * of the Experiment a role is defined as CvParams ()
 * The role that a Contact plays in an organization or with respect to the associating class.
 * A Contact may have several Roles within scope, and as such, associations to ContactRole allow
 * the use of a Contact in a certain manner. Examples might include a provider, or a data analyst.
 *
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/08/11
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
public class ContactRole {
    /**
     * Could be an Organization or a Person
     */
    AbstractContact contact = null;
    /**
     * Role of an specific Contact
     */
    CvParam role = null;

    /**
     *
     * @param contact
     * @param role
     */
    public ContactRole(AbstractContact contact, CvParam role) {
        this.contact = contact;
        this.role = role;
    }

    public AbstractContact getContact() {
        return contact;
    }

    public void setContact(AbstractContact contact) {
        this.contact = contact;
    }

    public CvParam getRole() {
        return role;
    }

    public void setRole(CvParam role) {
        this.role = role;
    }
}
