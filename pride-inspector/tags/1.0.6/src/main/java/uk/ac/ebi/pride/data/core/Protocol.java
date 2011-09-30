package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * Protocol used to generate the dataset, added by PRIDE XML 2.0.
 * <p/>
 * mzIdentML protocols are arranged in a slightly different way
 * <p/>
 * User: rwang
 * Date: 27-Jan-2010
 * Time: 09:43:20
 */
public class Protocol extends ParamGroup {

    /**
     * identifier for the protocol
     */
    private String id = null;
    /**
     * name of the protocol
     */
    private String name = null;
    /**
     * protocol steps
     */
    private List<ParamGroup> protocolSteps;

    /**
     * Constructor
     *
     * @param id     required.
     * @param name   optional.
     * @param steps  optional.
     * @param params optional.
     */
    public Protocol(String id,
                    String name,
                    List<ParamGroup> steps,
                    ParamGroup params) {
        super(params);
        setId(id);
        setName(name);
        setProtocolSteps(steps);
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