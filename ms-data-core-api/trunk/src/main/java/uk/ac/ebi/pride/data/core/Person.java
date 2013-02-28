package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * A person's name and contact details. Any additional information such as the address, contact email etc.
 * should be supplied using CV parameters or user parameters.
 * User: yperez
 * Date: 08/08/11
 * Time: 16:35
 */
public class Person extends AbstractContact {

    /**
     * The organization a person belongs to.
     */
    private List<Organization> affiliation = null;

    /**
     * The Person's first name.
     */
    private String firstname = null;

    /**
     * The Person's last/family name.
     */
    private String lastname = null;

    /**
     * contactInfo
     */
    private String contactInfo = null;

    /**
     * The Person's middle initial.
     */
    private String midInitials = null;

    /**
     * Constructor for MzMl and Pride Person Contact
     *
     * @param params ParamGroup of Person
     * @param firstname First Name of the Person
     * @param contactInfo Contact Info
     */
    public Person(ParamGroup params, String firstname, String contactInfo) {
        this(params, null, null, null, firstname, null, null, contactInfo);
    }

    /**
     * @param id         Generic Id
     * @param name       Generic Name
     * @param lastname   LastName
     * @param firstname  FirstName
     * @param midInitials Middle Initials
     * @param affiliation Affiliation
     * @param contactInfo Contact Info
     */
    public Person(Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String contactInfo) {
        this(null, id, name, lastname, firstname, midInitials, affiliation, contactInfo);
    }

    /**
     * @param params     ParamGroup of Person
     * @param id         Generic Id
     * @param name       Generic Name
     * @param lastname   LastName
     * @param firstname  FirstName
     * @param midInitials Middle Initials
     * @param affiliation Affiliation
     * @param contactInfo Contact Info
     */
    public Person(ParamGroup params, Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String contactInfo) {
        super(params, id, name);
        this.lastname    = lastname;
        this.firstname   = firstname;
        this.midInitials = midInitials;
        this.affiliation = affiliation;
        this.contactInfo = contactInfo;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMidInitials() {
        return midInitials;
    }

    public void setMidInitials(String midInitials) {
        this.midInitials = midInitials;
    }

    public List<Organization> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<Organization> affiliation) {
        this.affiliation = affiliation;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Person person = (Person) o;

        return !(affiliation != null ? !affiliation.equals(person.affiliation) : person.affiliation != null) && !(contactInfo != null ? !contactInfo.equals(person.contactInfo) : person.contactInfo != null) && !(firstname != null ? !firstname.equals(person.firstname) : person.firstname != null) && !(lastname != null ? !lastname.equals(person.lastname) : person.lastname != null) && !(midInitials != null ? !midInitials.equals(person.midInitials) : person.midInitials != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (affiliation != null ? affiliation.hashCode() : 0);
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (contactInfo != null ? contactInfo.hashCode() : 0);
        result = 31 * result + (midInitials != null ? midInitials.hashCode() : 0);
        return result;
    }
}



