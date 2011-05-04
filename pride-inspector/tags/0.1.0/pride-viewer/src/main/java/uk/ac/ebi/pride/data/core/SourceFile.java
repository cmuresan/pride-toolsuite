package uk.ac.ebi.pride.data.core;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:53:36
 */
public class SourceFile extends ParamGroup {
    
    private String id = null;
    private String name = null;
    private String path = null;

    public SourceFile(String id,
                      String name,
                      String path,
                      ParamGroup params) {
        super(params);
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}