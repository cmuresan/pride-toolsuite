package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * mzIdentML protocols are arranged in a slightly different way
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:43:20
 */
public class Protocol extends ParamGroup {

    // id - mzidentml
    private String id = null;
    // name - mzidentml
    private String name = null;

    private List<ParamGroup> protocolSteps;

    public Protocol(String id, String name,
                    List<ParamGroup> steps,
                    ParamGroup params) {
        super(params);
        this.id = id;
        this.name = name;
        this.protocolSteps = steps;
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

    public List<ParamGroup> getProtocolSteps() {
        return protocolSteps;
    }

    public void setProtocolSteps(List<ParamGroup> protocolSteps) {
        this.protocolSteps = protocolSteps;
    }
}