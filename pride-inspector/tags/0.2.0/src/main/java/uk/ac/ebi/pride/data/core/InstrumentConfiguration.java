package uk.ac.ebi.pride.data.core;

/**
 * Description of a particular hardware configuration of a mass spectrometer.
 * each instrument can only contain three components : source, analyser, detector.
 *
 * In mzML 1.1.0.1, the following cv terms must be added:
 * 1. May include one or more child terms of "ion optics attribute". (field-free region,
 * accelerating voltage and et al)
 *
 * 2. May include only one child term of "ion optics type". (magnetic deflection,
 * delayed extraction, collision quadrupole and et al)
 *
 * 3. May include one or more child terms of "instrument attribute". (customization,
 * transmission, instrument serial number)
 *
 * 4. Must include only one "instrument mode" or any of its children. (4000 Q TRAP,
 * API 4000 and et al)
 *
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:59:50
 */
public class InstrumentConfiguration extends ParamGroup {
    /** identifier of this instrument */
    private String id = null;
    /** scan settings */
    private ScanSetting scanSetting = null;
    /** software used */
    private Software software = null;
    /** only one source */
    private InstrumentComponent source = null;
    /** only one analyzer */
    private InstrumentComponent analyzer = null;
    /** only one detector */
    private InstrumentComponent detector = null;

    /**
     * Constructor
     * @param id    required.
     * @param scanSetting   optional
     * @param software  optional.
     * @param source    required.
     * @param analyzer  required.
     * @param detector  required.
     * @param params    optional.
     */
    public InstrumentConfiguration(String id,
                      ScanSetting scanSetting,
                      Software software,
                      InstrumentComponent source,
                      InstrumentComponent analyzer,
                      InstrumentComponent detector,
                      ParamGroup params) {
        super(params);
        setId(id);
        setScanSetting(scanSetting);
        setSoftware(software);
        setSource(source);
        setAnalyzer(analyzer);
        setDetector(detector);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Instrument ID can not be NULL");
        } else {
            this.id = id;
        }
    }

    public ScanSetting getScanSetting() {
        return scanSetting;
    }

    public void setScanSetting(ScanSetting scanSetting) {
        this.scanSetting = scanSetting;
    }

    public Software getSoftware() {
        return software;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public InstrumentComponent getSource() {
        return source;
    }

    public void setSource(InstrumentComponent source) {
        if (source == null) {
            throw new IllegalArgumentException("Instrument sources can not be NULL or empty");
        } else {
            this.source = source;
        }
    }

    public InstrumentComponent getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(InstrumentComponent analyzer) {
        if (analyzer == null) {
            throw new IllegalArgumentException("Instrument analyzer can not be NULL or empty");
        } else {
            this.analyzer = analyzer;
        }
    }

    public InstrumentComponent getDetector() {
        return detector;
    }

    public void setDetector(InstrumentComponent detector) {
        if (detector == null) {
            throw new IllegalArgumentException("Instrument detectors can not be NULL or empty");
        } else {
            this.detector = detector;
        }
    }
}
