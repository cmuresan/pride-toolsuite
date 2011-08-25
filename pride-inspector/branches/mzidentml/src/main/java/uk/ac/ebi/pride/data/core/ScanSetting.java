package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Description of the acquisition settings or the instrument prior to the start of
 * the run.
 * <p/>
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 11:00:53
 */
public class ScanSetting extends ParamGroup {
    /**
     * identifier of the scan setting
     */
    private String id = null;
    /**
     * source file
     */
    private List<SourceFile> sourceFile = null;
    /**
     * target list
     */
    private List<ParamGroup> targets = null;

    /**
     * Constructor
     *
     * @param id         required.
     * @param sourceFile optional.
     * @param targets    optional.
     * @param params     optional.
     */
    public ScanSetting(String id,
                       List<SourceFile> sourceFile,
                       List<ParamGroup> targets,
                       ParamGroup params) {
        super(params);
        setId(id);
        setSourceFile(sourceFile);
        setTargets(targets);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<SourceFile> getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(List<SourceFile> sourceFile) {
        this.sourceFile = sourceFile;
    }

    public List<ParamGroup> getTargets() {
        return targets;
    }

    public void setTargets(List<ParamGroup> targets) {
        this.targets = targets;
    }
}
