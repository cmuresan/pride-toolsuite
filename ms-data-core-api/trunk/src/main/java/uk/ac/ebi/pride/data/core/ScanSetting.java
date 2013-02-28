package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

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
    public ScanSetting(String id, List<SourceFile> sourceFile, List<ParamGroup> targets, ParamGroup params) {
        super(params);
        this.id         = id;
        this.sourceFile = sourceFile;
        this.targets    = targets;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ScanSetting that = (ScanSetting) o;

        return !(id != null ? !id.equals(that.id) : that.id != null) && !(sourceFile != null ? !sourceFile.equals(that.sourceFile) : that.sourceFile != null) && !(targets != null ? !targets.equals(that.targets) : that.targets != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (sourceFile != null ? sourceFile.hashCode() : 0);
        result = 31 * result + (targets != null ? targets.hashCode() : 0);
        return result;
    }
}



