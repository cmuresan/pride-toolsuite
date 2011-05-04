package uk.ac.ebi.pride.data.core;

import sun.security.util.BigInt;

import java.util.Date;
import java.util.List;

/**
 * ToDo: thread safety
 * ToDo: PropertyChange Notification
 * User: rwang
 * Date: 25-Jan-2010
 * Time: 14:47:55
 */
public class Experiment extends ParamGroup {

    private String title = null;
    private String accession = null;
    private String shortLabel = null;
    private Protocol protocol =  null;
    private List<Reference> references = null;
    private Date publicDate = null;
    private Date creationDate = null;
    private boolean isPublic = false;
    private BigInt submitterID = null;
    // ToDo: Curator
    // ToDo: PermittedPeople
    // ToDo: isPermittedToView
    // ToDo: Verified Date
    // ToDo: Verification State
    // ToDo: isVerified
    // ToDo: Verification Notes


    public Experiment(String title, String accession, String shortLabel,
                      Protocol protocol, List<Reference> references,
                      Date pDate, Date cDate, boolean isPublic, BigInt submitterID,
                      ParamGroup params) {
        super(params);
        this.title = title;
        this.accession = accession;
        this.shortLabel = shortLabel;
        this.protocol = protocol;
        this.references = references;
        setPublicDate(pDate);
        setCreationDate(cDate);
        this.isPublic = isPublic;
        this.submitterID = submitterID;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String t) {
        title = t;
    }

    // pride accession
    public String getAccession() {
        return accession;
    }

    public void setAccession(String acc) {
        accession = acc;
    }
    
    //short label
    public String getShortLabel() {
        return shortLabel;
    }
    public void setShortLabel(String label) {
        shortLabel = label;
    }

    // protocols
    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol prot) {
        protocol = prot;
    }

    // references
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Date getPublicDate() {
        return publicDate == null ? null : new Date(publicDate.getTime());
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = (publicDate == null ? null : new Date(publicDate.getTime()));
    }

    public BigInt getSubmitterID() {
        return submitterID;
    }

    public void setSubmitterID(BigInt submitterID) {
        this.submitterID = submitterID;
    }
}
