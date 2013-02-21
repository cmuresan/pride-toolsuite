package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * Description of a particular hardware configuration of a mass spectrometer.
 * each instrument can only contain three components : source, analyser, detector.
 * <p/>
 * In mzML 1.1.0.1, the following cv terms must be added:
 * 1. May include one or more child terms of "ion optics attribute". (field-free region,
 * accelerating voltage and et al)
 * <p/>
 * 2. May include only one child term of "ion optics type". (magnetic deflection,
 * delayed extraction, collision quadrupole and et al)
 * <p/>
 * 3. May include one or more child terms of "instrument attribute". (customization,
 * transmission, instrument serial number)
 * <p/>
 * 4. Must include only one "instrument mode" or any of its children. (4000 Q TRAP,
 * API 4000 and et al)
 * <p/>
 * User: rwang, yperez
 * Date: 04-Feb-2011
 * Time: 15:59:50
 */
public class InstrumentConfiguration extends ParamGroup {

    /**
     * only one analyzer
     */
    private List<InstrumentComponent> analyzer = null;

    /**
     * only one detector
     */
    private List<InstrumentComponent> detector = null;

    /**
     * identifier of this instrument
     */
    private String id = null;

    /**
     * scan settings
     */
    private ScanSetting scanSetting = null;

    /**
     * software used
     */
    private Software software = null;

    /**
     * only one source
     */
    private List<InstrumentComponent> source = null;

    /**
     * Constructor
     *
     * @param id          required.
     * @param scanSetting optional
     * @param software    optional.
     * @param source      required.
     * @param analyzer    required.
     * @param detector    required.
     * @param params      optional.
     */
    public InstrumentConfiguration(String id, ScanSetting scanSetting, Software software,
                                   List<InstrumentComponent> source, List<InstrumentComponent> analyzer,
                                   List<InstrumentComponent> detector, ParamGroup params) {
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
        this.id = id;
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

    public List<InstrumentComponent> getSource() {
        return source;
    }

    public void setSource(List<InstrumentComponent> source) {
        this.source = source;
    }

    public List<InstrumentComponent> getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(List<InstrumentComponent> analyzer) {
        this.analyzer = analyzer;
    }

    public List<InstrumentComponent> getDetector() {
        return detector;
    }

    public void setDetector(List<InstrumentComponent> detector) {
        this.detector = detector;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        InstrumentConfiguration that = (InstrumentConfiguration) o;

        if (analyzer != null ? !analyzer.equals(that.analyzer) : that.analyzer != null) return false;
        if (detector != null ? !detector.equals(that.detector) : that.detector != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (scanSetting != null ? !scanSetting.equals(that.scanSetting) : that.scanSetting != null) return false;
        if (software != null ? !software.equals(that.software) : that.software != null) return false;
        if (source != null ? !source.equals(that.source) : that.source != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (analyzer != null ? analyzer.hashCode() : 0);
        result = 31 * result + (detector != null ? detector.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (scanSetting != null ? scanSetting.hashCode() : 0);
        result = 31 * result + (software != null ? software.hashCode() : 0);
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}



