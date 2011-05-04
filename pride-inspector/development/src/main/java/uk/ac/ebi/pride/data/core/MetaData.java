package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * General descriptions shared or used for the whole file.
 * <p/>
 * User: rwang
 * Date: 08-Mar-2010
 * Time: 10:48:29
 */
public class MetaData extends ParamGroup {

    /**
     * id is used for referencing from external files, e.g. LSID
     */
    private Comparable id = null;
    /**
     * accession number for the document used for storage, e.g. PRIDE accession
     */
    private String accession = null;
    /**
     * version of this document
     */
    private String version = null;
    /**
     * General file description
     */
    private FileDescription fileDescription = null;
    /**
     * list and description of samples
     */
    private List<Sample> samples = null;
    /**
     * list and description of software used to acquire and/or process the data in the file
     */
    private List<Software> softwares = null;
    /**
     * list and descriptions of the acquisition settings applied prior to the start of data acquisition.
     */
    private List<ScanSetting> scanSettings = null;
    /**
     * list and descriptions of instruments settings
     */
    private List<InstrumentConfiguration> instrumentConfigurations = null;
    /**
     * list and descriptions of data processing applied to this data
     */
    private List<DataProcessing> dataProcessings = null;

    /**
     * Constructor
     *
     * @param id                       optional.
     * @param accession                optional.
     * @param version                  required.
     * @param fileDesc                 required.
     * @param samples                  optional.
     * @param softwares                required.
     * @param scanSettings             optional.
     * @param instrumentConfigurations required.
     * @param dataProcessing           required.
     */
    public MetaData(Comparable id,
                    String accession,
                    String version,
                    FileDescription fileDesc,
                    List<Sample> samples,
                    List<Software> softwares,
                    List<ScanSetting> scanSettings,
                    List<InstrumentConfiguration> instrumentConfigurations,
                    List<DataProcessing> dataProcessing,
                    ParamGroup params) {
        super(params);
        setId(id);
        setAccession(accession);
        setVersion(version);
        setFileDescription(fileDesc);
        setSamples(samples);
        setSoftwares(softwares);
        setScanSettings(scanSettings);
        setInstrumentConfigurations(instrumentConfigurations);
        setDataProcessings(dataProcessing);
    }

    public Comparable getId() {
        return id;
    }

    public void setId(Comparable id) {
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

    public FileDescription getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(FileDescription fileDescription) {
        this.fileDescription = fileDescription;
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

    public List<InstrumentConfiguration> getInstrumentConfigurations() {
        return instrumentConfigurations;
    }

    public void setInstrumentConfigurations(List<InstrumentConfiguration> instrumentConfigurations) {
        this.instrumentConfigurations = instrumentConfigurations;
    }

    public List<DataProcessing> getDataProcessings() {
        return dataProcessings;
    }

    public void setDataProcessings(List<DataProcessing> dataProcessings) {
        this.dataProcessings = dataProcessings;
    }
}
