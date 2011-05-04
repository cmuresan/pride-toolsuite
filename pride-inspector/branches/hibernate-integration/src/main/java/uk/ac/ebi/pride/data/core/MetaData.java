package uk.ac.ebi.pride.data.core;

import java.util.List;

/**
 * General descriptions shared or used for the whole file.
 *
 * User: rwang
 * Date: 08-Mar-2010
 * Time: 10:48:29
 */
public class MetaData extends ParamGroup {
    
    /** id is used for referencing from external files, e.g. LSID */
    private String id = null;
    /** accession number for the document used for storage, e.g. PRIDE accession */
    private String accession = null;
    /** version of this document */
    private String version = null;
    /** General file description */
    private FileDescription fileDescription =  null;
    /** list and description of samples */
    private List<Sample> samples = null;
    /** list and description of software used to acquire and/or process the data in the file */
    private List<Software> softwares = null;
    /** list and descriptions of the acquisition settings applied prior to the start of data acquisition. */
    private List<ScanSetting> scanSettings = null;
    /** list and descriptions of instruments settings */
    private List<InstrumentConfiguration> instrumentConfigurations = null;
    /** list and descriptions of data processing applied to this data */
    private List<DataProcessing> dataProcessings = null;

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
     */
    public MetaData(String id,
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
        if (version == null) {
            throw new IllegalArgumentException("Version can not be NULL");
        } else {
            this.version = version;
        }
    }

    public FileDescription getFileDescription() {
        return fileDescription;
    }

    public void setFileDescription(FileDescription fileDescription) {
        if (fileDescription == null) {
            throw new IllegalArgumentException("File description can not be NULL");
        } else {
            this.fileDescription = fileDescription;
        }
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
        if (softwares == null || softwares.isEmpty()) {
            throw new IllegalArgumentException("Softwares can not be NULL or empty");
        } else {
            this.softwares = softwares;
        }
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
        if (instrumentConfigurations == null || instrumentConfigurations.isEmpty()) {
            throw new IllegalArgumentException("Instrument settings can be NULL or empty");
        } else {
            this.instrumentConfigurations = instrumentConfigurations;
        }
    }

    public List<DataProcessing> getDataProcessings() {
        return dataProcessings;
    }

    public void setDataProcessings(List<DataProcessing> dataProcessings) {
        if (dataProcessings == null || dataProcessings.isEmpty()) {
            throw new IllegalArgumentException("Data processings can be NULL or empty");
        } else {
            this.dataProcessings = dataProcessings;
        }
    }
}
