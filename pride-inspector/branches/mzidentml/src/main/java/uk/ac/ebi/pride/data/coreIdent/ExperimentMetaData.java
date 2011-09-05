package uk.ac.ebi.pride.data.coreIdent;


import java.util.Date;
import java.util.List;

/**
 * General descriptions shared or used for the whole file.
 * MetaData Description at the Experimental level contain the information for:
 *    - Samples Description.
 *    - File Version.
 *    - List of the Softwares used in the Experimental Protocol.
 *    - File Creation Date.
 *    - An a List of References.
 * <p/>
 * User: rwang, yperez
 * Date: 08-Mar-2010
 * Time: 10:48:29
 */
public class ExperimentMetaData extends IdentifiableParamGroup {
    /**
     * version of this document used for PRIDE and MzIdentML
     */
    private String version = null;
    /**
     * list and description of samples in the case of mzIdentML.
     */
    private List<Sample> sampleList = null;
    /**
     * list and description of software used to acquire and/or process
     * the data in the file.
     */
    private List<Software> softwareList = null;
    /**
     * The complete set of Contacts People for this file.
     * In case of MzMl the Person Object Must be constructed whit a set of Params Group.
     * Extracted From CvParams.
     * List of contact details
     * In mzMl 1.1.0.1, each contact must have the following cv terms:
     * May include one or more child terms of "contact person attribute"
     * (contact name, contact address, contact email and et al)
     */
    private List<Person> personList = null;
     /**
     * The complete set of Contacts Organisations for this file.
     */
    private List<Organization> organizationList = null;
    /**
     * List and descriptions of the source files
     */
    private List<SourceFile> sourceFiles = null;
    /**
     * The Provider of the mzIdentML record in terms of the contact and software. The provider
     * is an instance with the the last final software that generate the file.
     */
    private Provider provider = null;
   /**
     * List of publications of the experiment
     */
    private List<Reference> references = null;
     /**
     * The date when the experiment is created
     */
    private Date creationDate = null;
    /**
     * The date when the experiment is made public
     */
    private Date publicDate = null;
    /**
     * The Experiment Protocols is an small view of the protocols used in the Experiment
     * the idea is to define an small object like pride experiments that could be used also for MzIdentMl in order
     * to put and small description of the steps in the experiment.
     */
    private ExperimentProtocol protocol = null;
    /*
     * Short Label used by Pride XML Object
     */
    private String shortLabel = null;
    /**
     *
     * @param id
     * @param name
     * @param version
     * @param sampleList
     * @param softwareList
     * @param personList
     * @param sourceFiles
     * @param provider
     * @param organizationList
     * @param references
     * @param creationDate
     * @param publicDate
     * @param protocol
     */
    public ExperimentMetaData(Comparable id,
                              String name,
                              String version,
                              String shortLabel,
                              List<Sample> sampleList,
                              List<Software> softwareList,
                              List<Person> personList,
                              List<SourceFile> sourceFiles,
                              Provider provider,
                              List<Organization> organizationList,
                              List<Reference> references,
                              Date creationDate,
                              Date publicDate,
                              ExperimentProtocol protocol) {
        super(id, name);
        this.version = version;
        this.sampleList = sampleList;
        this.softwareList = softwareList;
        this.personList = personList;
        this.sourceFiles = sourceFiles;
        this.provider = provider;
        this.organizationList = organizationList;
        this.references = references;
        this.creationDate = creationDate;
        this.publicDate = publicDate;
        this.protocol = protocol;
        this.shortLabel = shortLabel;
    }

    public ExperimentMetaData(ParamGroup params,
                              Comparable id,
                              String name,
                              String version,
                              String shortLabel,
                              List<Sample> sampleList,
                              List<Software> softwareList,
                              List<Person> personList,
                              List<SourceFile> sourceFiles,
                              Provider provider,
                              List<Organization> organizationList,
                              List<Reference> references,
                              Date creationDate,
                              Date publicDate,
                              ExperimentProtocol protocol) {
        super(params, id, name);
        this.version = version;
        this.sampleList = sampleList;
        this.softwareList = softwareList;
        this.personList = personList;
        this.sourceFiles = sourceFiles;
        this.provider = provider;
        this.organizationList = organizationList;
        this.references = references;
        this.creationDate = creationDate;
        this.publicDate = publicDate;
        this.protocol = protocol;
        this.shortLabel = shortLabel;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public List<Organization> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Organization> organizationList) {
        this.organizationList = organizationList;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public void setReferences(List<Reference> references) {
        this.references = references;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getPublicDate() {
        return publicDate;
    }

    public void setPublicDate(Date publicDate) {
        this.publicDate = publicDate;
    }

    public ExperimentProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(ExperimentProtocol protocol) {
        this.protocol = protocol;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Sample> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<Sample> sampleList) {
        this.sampleList = sampleList;
    }

    public List<Software> getSoftwareList() {
        return softwareList;
    }

    public void setSoftwareList(List<Software> softwareList) {
        this.softwareList = softwareList;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles) {
        this.sourceFiles = sourceFiles;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public void setShortLabel(String shortLabel) {
        this.shortLabel = shortLabel;
    }

    public ParamGroup getAdditional(){
        return new ParamGroup(this.getCvParams(),this.getUserParams());
    }
}
