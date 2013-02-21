package uk.ac.ebi.pride.data.core;

/**
 * ToDo: document this class
 * <p/>
 * User: rwang, yperez
 * Date: 03/08/11
 * Time: 10:23
 */
public class RunInfo extends ParamGroup {

    private MzGraphList chromatogramList = null;

    /**
     * default instrument configuration, important to have an overview of the instrument
     * configuration used in the experiment
     */
    private InstrumentConfiguration defaultInstrumentConfiguration = null;

    /**
     * Source file used to extract all the mass spectrum or chromatograms.
     */
    private SourceFile defaultSourceFile = null;

    /**
     * identifier of each run
     */
    private String id = null;

    /**
     * Reference to the sample used to obtain these spectrum or chromatograms list.
     */
    private Sample      sampleRef    = null;

    private MzGraphList spectrumList = null;

    /**
     * Timedate of the experiment measure
     */
    private String timeStamp = null;

    public RunInfo(ParamGroup params, String id, InstrumentConfiguration defaultInstrumentConfiguration,
                   SourceFile defaultSourceFile, Sample sampleRef, String timeStamp, MzGraphList spectrumList,
                   MzGraphList chromatogramList) {
        super(params);
        this.id                             = id;
        this.defaultInstrumentConfiguration = defaultInstrumentConfiguration;
        this.defaultSourceFile              = defaultSourceFile;
        this.sampleRef                      = sampleRef;
        this.timeStamp                      = timeStamp;
        this.spectrumList                   = spectrumList;
        this.chromatogramList               = chromatogramList;
    }

    public MzGraphList getSpectrumList() {
        return spectrumList;
    }

    public void setSpectrumList(MzGraphList spectrumList) {
        this.spectrumList = spectrumList;
    }

    public MzGraphList getChromatogramList() {
        return chromatogramList;
    }

    public void setChromatogramList(MzGraphList chromatogramList) {
        this.chromatogramList = chromatogramList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InstrumentConfiguration getDefaultInstrumentConfiguration() {
        return defaultInstrumentConfiguration;
    }

    public void setDefaultInstrumentConfiguration(InstrumentConfiguration defaultInstrumentConfiguration) {
        this.defaultInstrumentConfiguration = defaultInstrumentConfiguration;
    }

    public SourceFile getDefaultSourceFile() {
        return defaultSourceFile;
    }

    public void setDefaultSourceFile(SourceFile defaultSourceFile) {
        this.defaultSourceFile = defaultSourceFile;
    }

    public Sample getSampleRef() {
        return sampleRef;
    }

    public void setSampleRef(Sample sampleRef) {
        this.sampleRef = sampleRef;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        RunInfo runInfo = (RunInfo) o;

        if (chromatogramList != null ? !chromatogramList.equals(runInfo.chromatogramList) : runInfo.chromatogramList != null)
            return false;
        if (defaultInstrumentConfiguration != null ? !defaultInstrumentConfiguration.equals(runInfo.defaultInstrumentConfiguration) : runInfo.defaultInstrumentConfiguration != null)
            return false;
        if (defaultSourceFile != null ? !defaultSourceFile.equals(runInfo.defaultSourceFile) : runInfo.defaultSourceFile != null)
            return false;
        if (id != null ? !id.equals(runInfo.id) : runInfo.id != null) return false;
        if (sampleRef != null ? !sampleRef.equals(runInfo.sampleRef) : runInfo.sampleRef != null) return false;
        if (spectrumList != null ? !spectrumList.equals(runInfo.spectrumList) : runInfo.spectrumList != null)
            return false;
        if (timeStamp != null ? !timeStamp.equals(runInfo.timeStamp) : runInfo.timeStamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (chromatogramList != null ? chromatogramList.hashCode() : 0);
        result = 31 * result + (defaultInstrumentConfiguration != null ? defaultInstrumentConfiguration.hashCode() : 0);
        result = 31 * result + (defaultSourceFile != null ? defaultSourceFile.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (sampleRef != null ? sampleRef.hashCode() : 0);
        result = 31 * result + (spectrumList != null ? spectrumList.hashCode() : 0);
        result = 31 * result + (timeStamp != null ? timeStamp.hashCode() : 0);
        return result;
    }
}



