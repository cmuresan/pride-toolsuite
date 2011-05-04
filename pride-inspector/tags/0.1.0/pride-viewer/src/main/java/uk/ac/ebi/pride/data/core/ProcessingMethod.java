package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 16:58:59
 */
public class ProcessingMethod extends ParamGroup {

    private Software software = null;

    public ProcessingMethod(Software software,
                            ParamGroup params) {
        super(params);
        this.software = software;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }
}
