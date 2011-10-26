package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * This class storage the information related with metadata at the spectrum level. the object that are support by this
 * class are PRIDE Object, mzML, mgf, etc.
 * User: yperez
 * Date: 15/08/11
 * Time: 11:35
 */
public class MzGraphMetaData extends IdentifiableParamGroup {

    /**
     * List and descriptions of data processing applied to this data, this structure is used by mzML
     * files to represent each procedure applied to the data by steps. Each DataProcessing step have the
     * following structure:
     * - id
     * - name
     * - Map of Software and PraramGroup.
     */
    private List<DataProcessing> dataProcessingList = null;

    /**
     * list and descriptions of instruments settings
     */
    private List<InstrumentConfiguration> instrumentConfigurations = null;

    /**
     * list and descriptions of the acquisition settings applied prior to the start of data acquisition.
     */
    private List<ScanSetting> scanSettings = null;

    /**
     * List of SpectraData Objects used by MZIdentML to refereed the original mass spectra files.
     * A data set containing spectra data (consisting of one or more spectra).
     */
    private List<SpectraData> spectraDataList = null;

    /**
     * Constructor for MzGraphMetaData
     *
     * @param id
     * @param name
     * @param scanSettings
     * @param instrumentConfigurations
     * @param dataProcessingList
     */
    public MzGraphMetaData(Comparable id, String name, List<ScanSetting> scanSettings,
                           List<InstrumentConfiguration> instrumentConfigurations,
                           List<DataProcessing> dataProcessingList) {
        this(null, id, name, scanSettings, instrumentConfigurations, dataProcessingList, null);
    }

    /**
     * @param params
     * @param id
     * @param name
     * @param scanSettings
     * @param instrumentConfigurations
     * @param dataProcessingList
     */
    public MzGraphMetaData(ParamGroup params, Comparable id, String name, List<ScanSetting> scanSettings,
                           List<InstrumentConfiguration> instrumentConfigurations,
                           List<DataProcessing> dataProcessingList) {
        this(params, id, name, scanSettings, instrumentConfigurations, dataProcessingList, null);
    }

    public MzGraphMetaData(ParamGroup params, Comparable id, String name, List<ScanSetting> scanSettings,
                           List<InstrumentConfiguration> instrumentConfigurations,
                           List<DataProcessing> dataProcessingList, List<SpectraData> spectraDataList) {
        super(params, id, name);
        this.scanSettings             = scanSettings;
        this.instrumentConfigurations = instrumentConfigurations;
        this.dataProcessingList       = dataProcessingList;
        this.spectraDataList          = spectraDataList;
    }

    public ParamGroup getFileContent() {
        return new ParamGroup(this.getCvParams(), this.getUserParams());
    }

    public void setFileContent(ParamGroup fileContent) {
        this.setCvParams(fileContent.getCvParams());
        this.setUserParams(fileContent.getUserParams());
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

    public List<DataProcessing> getDataProcessingList() {
        return dataProcessingList;
    }

    public void setDataProcessingList(List<DataProcessing> dataProcessingList) {
        this.dataProcessingList = dataProcessingList;
    }

    public List<SpectraData> getSpectraDataList() {
        return spectraDataList;
    }

    public void setSpectraDataList(List<SpectraData> spectraDataList) {
        this.spectraDataList = spectraDataList;
    }
}


