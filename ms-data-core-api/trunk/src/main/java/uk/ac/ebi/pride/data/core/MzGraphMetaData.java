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
        this(null, id, name, scanSettings, instrumentConfigurations, dataProcessingList);
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
        super(params, id, name);
        this.scanSettings             = scanSettings;
        this.instrumentConfigurations = instrumentConfigurations;
        this.dataProcessingList       = dataProcessingList;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MzGraphMetaData that = (MzGraphMetaData) o;

        if (dataProcessingList != null ? !dataProcessingList.equals(that.dataProcessingList) : that.dataProcessingList != null)
            return false;
        if (instrumentConfigurations != null ? !instrumentConfigurations.equals(that.instrumentConfigurations) : that.instrumentConfigurations != null)
            return false;
        if (scanSettings != null ? !scanSettings.equals(that.scanSettings) : that.scanSettings != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataProcessingList != null ? dataProcessingList.hashCode() : 0);
        result = 31 * result + (instrumentConfigurations != null ? instrumentConfigurations.hashCode() : 0);
        result = 31 * result + (scanSettings != null ? scanSettings.hashCode() : 0);
        return result;
    }
}



