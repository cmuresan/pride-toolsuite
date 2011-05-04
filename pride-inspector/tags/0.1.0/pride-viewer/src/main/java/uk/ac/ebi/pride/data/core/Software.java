package uk.ac.ebi.pride.data.core;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:06:45
 */
public class Software extends ParamGroup {
    
    private String id = null;
    private String name = null;
    private String version = null;
    // for PRIDE XML, comments and completionTime need to be stored in ParamGroup

    public Software(String id, String name, String version,
                    ParamGroup params) {
        super(params);
        this.id = id;
        this.name = name;
        this.version = version;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}