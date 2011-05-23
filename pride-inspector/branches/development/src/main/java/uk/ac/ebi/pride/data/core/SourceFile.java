package uk.ac.ebi.pride.data.core;

/**
 * Description of source file, including location and type.
 * <p/>
 * In mzML 1.1.0.1, the following cv terms must be added:
 * <p/>
 * 1. Must include only one a child term of "native spectrum identifier format"
 * (thermo nativeID format, waters nativeID format, WIFF nativeID format and et al)
 * <p/>
 * 2. Must include one or more child terms of "data file check-sum type" (MD5, SHA-1)
 * <p/>
 * 3. Must include only one child term of "source file type" (waters raw file,
 * ABI WIFF file, Thermo RAW file and et al)
 * <p/>
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:53:36
 */
public class SourceFile extends ParamGroup {
    /**
     * identifier for this file
     */
    private String id = null;
    /**
     * name of the source file
     */
    private String name = null;
    /**
     * location of the source file
     */
    private String path = null;

    /**
     * Constructor
     *
     * @param id     required.
     * @param name   required.
     * @param path   required.
     * @param params optional.
     */
    public SourceFile(String id,
                      String name,
                      String path,
                      ParamGroup params) {
        super(params);
        setId(id);
        setName(name);
        setPath(path);
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