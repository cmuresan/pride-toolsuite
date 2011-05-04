package uk.ac.ebi.pride.data.core;

import java.util.Date;
import java.util.List;

/**
 * Meta data for PRIDE xml experiment.
 *
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 14:47:55
 */
public class Experiment extends MetaData {

    /** Title of the experiment */
    private String title = null;
    /** Short label of the experiment */
    private String shortLabel = null;
    /** Protocol of the experiment */
    private Protocol protocol =  null;
    /** List of publications of the experiment */
    private List<Reference> references = null;
    /** The date when the experiment is made public */
    private Date publicDate = null;
    /** The date when the experiment is created */
    private Date creationDate = null;
    // ToDo: Curator
    // ToDo: PermittedPeople
    // ToDo: isPermittedToView
    // ToDo: Verified Date
    // ToDo: Verification State
    // ToDo: isVerified
    // ToDo: Verification Notes


    /**
     * Constructor
     * @param id    optional.
     * @param accession optional.
     * @param version   required.
     * @param fileDesc  required.
     * @param samples   optional.
     * @param softwares required.
     * @param scanSettings  optional.
     * @param instrumentConfigurations   required.
     * @param dataProcessing    required.
     * @param params    optional.
     * @param title required.
     * @param shortLabel    required.
     * @param protocol  required.
     * @param references    optional.
     * @param publicDate    optional.
     * @param creationDate  optional.
     */
    public Experiment(String id,
                      String accession,
                      String version,
                      FileDescription fileDesc,
                      List<Sample> samples,
                      List<Software> softwares,
                      List<ScanSetting> scanSettings,
                      List<InstrumentConfiguration> instrumentConfigurations,
                      List<DataProcessing> dataProcessing,
                      ParamGroup params,
                      String title,
                      String shortLabel,
                      Protocol protocol,
                      List<Reference> references,
                      Date publicDate,
                      Date creationDate) {
        super(id,
              accession,
              version,
              fileDesc,
              samples,
              softwares,
              scanSettings,
              instrumentConfigurations,
              dataProcessing,
              params);
        setTitle(title);
        setShortLabel(shortLabel);
        setProtocol(protocol);
        setReferences(references);
        setPublicDate(publicDate);
        setCreationDate(creationDate);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        if (t == null) {
            throw new IllegalArgumentException("Experiment title can not be NULL");
        } else {
            title = t;
        }
    }

    public String getShortLabel() {
        return shortLabel;
    }
    public void setShortLabel(String label) {
        if (label == null) {
            throw new IllegalArgumentException("Experiment short label can not be NULL");
        } else {
            shortLabel = label;
        }
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol prot) {
        if (prot == null) {
            throw new IllegalArgumentException("Excperiment protocol can not be NULL");
        } else {
            protocol = prot;
        }
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> refs){
        references = refs;
    }

    public Date getCreationDate() {
        return creationDate ==  null ? null : new Date(creationDate.getTime());
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = (creationDate == null ? null : new Date(creationDate.getTime()));
    }

    public Date getPublicDate() {
        return publicDate == null ? null : new Date(publicDate.getTime());
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = (publicDate == null ? null : new Date(publicDate.getTime()));
    }
}
