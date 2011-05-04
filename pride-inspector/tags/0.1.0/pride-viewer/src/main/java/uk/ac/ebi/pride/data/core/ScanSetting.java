package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 05-Feb-2010
 * Time: 11:00:53
 */
public class ScanSetting extends ParamGroup {
    private String id = null;
    private List<SourceFile> sourceFile = null;
    private List<ParamGroup> targets = null;

    public ScanSetting(String id, List<SourceFile> sourceFile,
                       List<ParamGroup> targets,
                       ParamGroup params) {
        super(params);
        this.id = id;
        this.sourceFile = sourceFile;
        this.targets = targets;
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
