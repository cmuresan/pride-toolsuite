package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

/**
 * Other classes in the model can be specified as sub-classes, inheriting from IdentifiablePraramGroup.
 * IdentifiableParamGroup gives classes a unique identifier within the scope and a name that need not be unique and also the information of ParamGroup.
 * Identifiable also provides a mechanism for annotating objects with CV Parameters.
 * User: yperez
 * Date: 04/08/11
 * Time: 11:32
 */
public class IdentifiableParamGroup extends ParamGroup {

    /**
     * An identifier is an unambiguous string that is unique within the scope
     * (i.e. a document, a set of related documents, or a repository) of its use.
     */
    private Comparable id;

    /**
     * The potentially ambiguous common identifier, such as a human-readable name for the instance.
     */
    private String name;

    /**
     * Constructor without param groups
     *
     * @param id   ID
     * @param name Name
     */
    public IdentifiableParamGroup(Comparable id, String name) {
        super(null);
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor using the ParamGroup (CvTerms and User Params)
     *
     * @param params ParamGroup (CvTerms and User Params)
     * @param id     ID
     * @param name   Name
     */
    public IdentifiableParamGroup(ParamGroup params, Comparable id, String name) {
        super(params);
        this.id = id;
        this.name = name;
    }

    /**
     * Get Name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set Name
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Comparable ID
     *
     * @return Comparable
     */
    public Comparable getId() {
        return id;
    }

    /**
     * Set Comparable ID
     *
     * @param id ID
     */
    public void setId(Comparable id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentifiableParamGroup)) return false;
        if (!super.equals(o)) return false;

        IdentifiableParamGroup that = (IdentifiableParamGroup) o;

        return !(id != null ? !id.equals(that.id) : that.id != null) && !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}



