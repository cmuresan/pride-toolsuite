package uk.ac.ebi.pride.data.core;

/**
 * Other classes in the model can be specified as sub-classes, inheriting from Identifiable.
 * Especially the MZIdentMl Classes. Identifiable gives classes a unique identifier within the scope and
 * a name that need not be unique. Identifiable also provides a mechanism for annotating objects with
 * BibliographicReference(s) and DatabaseEntry(s).
 * Created by IntelliJ IDEA.
 * User: yperez, rwang
 * Date: 08/08/11
 * Time: 16:11
 */
public class Identifiable implements MassSpecObject {

    /**
     * An identifier is an unambiguous string that is unique within the scope
     * (i.e. a document, a set of related documents, or a repository) of its use.
     */
    private Comparable id = null;

    /**
     * The potentially ambiguous common identifier, such as a human-readable name for the instance.
     */
    String name = null;

    public Identifiable(Comparable id, String name) {
        this.id   = id;
        this.name = name;
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
}


//~ Formatted by Jindent --- http://www.jindent.com
