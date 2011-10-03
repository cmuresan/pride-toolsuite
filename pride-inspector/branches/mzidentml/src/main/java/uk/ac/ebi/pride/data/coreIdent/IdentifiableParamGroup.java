package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Other classes in the model can be specified as sub-classes, inheriting from IdentifiablePraramGroup.
 * IdentifiableParamGroup gives classes a unique identifier within the scope and a name that need not be unique and also the information of ParamGroup.
 * Identifiable also provides a mechanism for annotating objects with CV Parameters.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 04/08/11
 * Time: 11:32
 */
public class IdentifiableParamGroup extends ParamGroup {
    /**
     * An identifier is an unambiguous string that is unique within the scope
     * (i.e. a document, a set of related documents, or a repository) of its use.
     */
    private Comparable id = null;
    /**
     * The potentially ambiguous common identifier, such as a human-readable name for the instance.
     */
    private String name = null;

    /**
     * Constructor without param groups
     * @param id
     * @param name
     */
    public IdentifiableParamGroup(Comparable id,
                                  String name) {
        super(null);
        this.id = id;
        this.name = name;
    }

    public IdentifiableParamGroup(ParamGroup params,
                                  Comparable id,
                                  String name) {
        super(params);
        this.id = id;
        this.name = name;
    }

    public IdentifiableParamGroup(List<CvParam> cvParams,
                                  List<UserParam> userParams,
                                  Comparable id, String name) {
        super(cvParams, userParams);
        this.id = id;
        this.name = name;
    }

    public IdentifiableParamGroup(List<CvParam> cvParams,
                                  Comparable id,
                                  String name){
        this(cvParams,null,id,name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Comparable getId() {
        return id;
    }

    public void setId(Comparable id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        IdentifiableParamGroup that = (IdentifiableParamGroup) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
