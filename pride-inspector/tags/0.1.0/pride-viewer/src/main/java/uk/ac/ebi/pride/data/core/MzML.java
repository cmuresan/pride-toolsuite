package uk.ac.ebi.pride.data.core;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 08-Mar-2010
 * Time: 10:48:29
 */
public class MzML {
    
    /** id is used for referencing from external files, e.g. LSID */
    private String id = null;
    /** accession number for the document used for storage, e.g. PRIDE accession */
    private String accession = null;
    /** version of this document */
    private String version = null;
    /**
     * The number of CV definitions in this file
     */
    private List<CVLookup> cvLookups = null;
    /**
     * from mzML, this summarizes the different types of spectra that can be expected in the file
     * This is expected to aid processing software in skipping files that do not contain appropriate
     * spectrum types for it.
     */
    private ParamGroup fileContent = null;
    /**
     * List and description of the source files this mzML document was generated or derived from
     */
    private List<SourceFile> sourceFile =  null;

    /**
     * List of contacts
     */
    private List<ParamGroup> contacts = null;

    /**
     * A collection of CVParam and UserParam elements that can be referenced from elsewhere
     * in this mzML document
     */
    private Map<String, ParamGroup> referenceableParamGroups = null;

    /** list and description of samples */
    private List<Sample> samples = null;
    /**
     * list and description of software used to acquire and/or process the data in the file
     */
    private List<Software> softwares = null;
    /**
     * list and descriptions of the acquisition settings applied prior to the start of data acquisition.
     */
    private List<ScanSetting> scanSettings = null;
    private List<Instrument> instruments = null;
    /**
     * list and descriptions of data processing applied to this data
     */
    private List<DataProcessing> dataProcessing = null;

    public MzML(String id, String accession, String version,
                List<CVLookup> cvLookups, ParamGroup fileContent,
                List<SourceFile> sourceFile, List<ParamGroup> contacts,
                Map<String, ParamGroup> referenceableParamGroups,
                List<Sample> samples, List<Software> softwares,
                List<ScanSetting> scanSettings, List<Instrument> instruments,
                List<DataProcessing> dataProcessing) {
        this.id = id;
        this.accession = accession;
        this.version = version;
        this.cvLookups = cvLookups;
        this.fileContent = fileContent;
        this.sourceFile = sourceFile;
        this.contacts = contacts;
        this.referenceableParamGroups = referenceableParamGroups;
        this.samples = samples;
        this.softwares = softwares;
        this.scanSettings = scanSettings;
        this.instruments = instruments;
        this.dataProcessing = dataProcessing;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CVLookup> getCvLookups() {
        return cvLookups;
    }

    public void setCvLookups(List<CVLookup> cvLookups) {
        this.cvLookups = cvLookups;
    }

    public ParamGroup getFileContent() {
        return fileContent;
    }

    public void setFileContent(ParamGroup fileContent) {
        this.fileContent = fileContent;
    }

    public List<SourceFile> getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(List<SourceFile> sourceFile) {
        this.sourceFile = sourceFile;
    }

    public Map<String, ParamGroup> getReferenceableParamGroups() {
        return referenceableParamGroups;
    }

    public List<ParamGroup> getContacts() {
        return contacts;
    }

    public void setContacts(List<ParamGroup> contacts) {
        this.contacts = contacts;
    }

    public void setReferenceableParamGroups(Map<String, ParamGroup> referenceableParamGroups) {
        this.referenceableParamGroups = referenceableParamGroups;
    }

    public List<Sample> getSamples() {
        return samples;
    }

    public void setSamples(List<Sample> samples) {
        this.samples = samples;
    }

    public List<Software> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<Software> softwares) {
        this.softwares = softwares;
    }

    public List<ScanSetting> getScanSettings() {
        return scanSettings;
    }

    public void setScanSettings(List<ScanSetting> scanSettings) {
        this.scanSettings = scanSettings;
    }

    public List<Instrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(List<Instrument> instruments) {
        this.instruments = instruments;
    }

    public List<DataProcessing> getDataProcessing() {
        return dataProcessing;
    }

    public void setDataProcessing(List<DataProcessing> dataProcessing) {
        this.dataProcessing = dataProcessing;
    }
}
