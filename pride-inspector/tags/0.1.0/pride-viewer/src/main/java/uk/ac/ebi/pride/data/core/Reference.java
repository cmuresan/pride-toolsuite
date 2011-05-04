package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:45:42
 */
public class Reference extends ParamGroup {

    private String fullReference = null;
    //ToDo: toHTML() ?


    public Reference(String fullReference,
                     ParamGroup params) {
        super(params);
        this.fullReference = fullReference;
    }

    public String getFullReference() {
        return fullReference;
    }

    public void setFullReference(String fullReference) {
        this.fullReference = fullReference;
    }
}
