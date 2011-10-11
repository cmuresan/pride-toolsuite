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
     * mail
     */
    private String mail = null;

    /**
     * The Person's middle initial.
     */
    private String midInitials = null;

    /**
     * Constructor for MzMl and Pride Person Contact
     *
     * @param params
     * @param firstname
     * @param mail
     */
    public Person(ParamGroup params, String firstname, String mail) {
        this(params, null, null, null, firstname, null, null, mail);
    }

    /**
     * @param id
     * @param name
     * @param lastname
     * @param firstname
     * @param midInitials
     * @param affiliation
     * @param mail
     */
    public Person(Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String mail) {
        this(null, id, name, lastname, firstname, midInitials, affiliation, mail);
    }

    /**
     * @param params
     * @param id
     * @param name
     * @param lastname
     * @param firstname
     * @param midInitials
     * @param affiliation
     * @param mail
     */
    public Person(ParamGroup params, Comparable id, String name, String lastname, String firstname, String midInitials,
                  List<Organization> affiliation, String mail) {
        super(params, id, name);
        this.lastname    = lastname;
        this.firstname   = firstname;
        this.midInitials = midInitials;
        this.affiliation = affiliation;
        this.mail        = mail;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
