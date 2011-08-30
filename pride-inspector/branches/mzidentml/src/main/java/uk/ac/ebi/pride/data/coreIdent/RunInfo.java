package uk.ac.ebi.pride.data.coreIdent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang, yperez
 * Date: 03/08/11
 * Time: 10:23
 *
 */
public class RunInfo extends ParamGroup{
    /**
     * identifier of each run
     */
    private String id = null;
    /**
     * default instrument configuration, important to have an overview of the instrument
     * configuration used in the experiment
     */
    private InstrumentConfiguration defaultInstrumentConfiguration = null;
    /**
     * Source file used to extract all the mass spectrum or cromatograms.
     */
    private SourceFile defaultSourceFile = null;
    /**
     * Reference to the sample used to obtain these spectrum or cromatograms list.
     *
     * */
    private Sample sampleRef = null;
    /**
     * Timedate of the experiment measure
     */
    private String timeStamp = null;


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

    private MzGraphList spectrumList = null;

    private MzGraphList chromatogramList = null;


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
}
