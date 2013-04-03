package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.List;

/**
 * A person's name and contact details. Any additional information such as the address, contact email etc.
 * should be supplied using CV parameters or user parameters.
 * User: yperez
 * Date: 08/08/11
 * Time: 16:35
 *
 * todo: review contactInfo
 */
public class Person extends Contact {

    private List<Organization> affiliation;

    private String firstname;

    private String lastname;

    private String contactInfo;

    private String midInitials;

    public Person(ParamGroup params, String firstname, String contactInfo) {
        this(params, null, null, null, firstname, null, null, contactInfo);
    }

    public Person(Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String contactInfo) {
        this(null, id, name, lastname, firstname, midInitials, affiliation, contactInfo);
    }

    public Person(ParamGroup params, Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String contactInfo) {
        super(params, id, name);
        this.lastname    = lastname;
        this.firstname   = firstname;
        this.midInitials = midInitials;
        this.affiliation = CollectionUtils.createListFromList(affiliation);
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
        CollectionUtils.replaceValuesInCollection(affiliation, this.affiliation);
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
        if (!(o instanceof Person)) return false;
        if (!super.equals(o)) return false;

        Person person = (Person) o;

        if (!affiliation.equals(person.affiliation)) return false;
        if (contactInfo != null ? !contactInfo.equals(person.contactInfo) : person.contactInfo != null) return false;
        if (firstname != null ? !firstname.equals(person.firstname) : person.firstname != null) return false;
        if (lastname != null ? !lastname.equals(person.lastname) : person.lastname != null) return false;
        if (midInitials != null ? !midInitials.equals(person.midInitials) : person.midInitials != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + affiliation.hashCode();
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (contactInfo != null ? contactInfo.hashCode() : 0);
        result = 31 * result + (midInitials != null ? midInitials.hashCode() : 0);
        return result;
    }
}



