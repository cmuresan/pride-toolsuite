package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:50:55
 */
public class Sample extends ParamGroup {
    /** from mzML */
    private String id = null;
    private String name = null;

    public Sample(String id,
                  String name,
                  ParamGroup params) {
        super(params);
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
