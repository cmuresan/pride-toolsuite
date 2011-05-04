package uk.ac.ebi.pride.data.core;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Feb-2010
 * Time: 15:59:50
 */
public class Instrument extends ParamGroup {

    private String id = null;
    private ScanSetting scanSetting = null;
    private Software software = null;
    /** This is a ordered list */
    private List<ParamGroup> source = null;
    /** This is a ordered list */
    private List<ParamGroup> analyzerList = null;
    /** This is a ordered list */
    private List<ParamGroup> detector = null;

    public Instrument(String id, ScanSetting scanSetting, Software software,
                      List<ParamGroup> source, List<ParamGroup> analyzerList,
                      List<ParamGroup> detector, ParamGroup params) {
        super(params);
        this.id = id;
        this.scanSetting = scanSetting;
        this.software = software;
        this.source = source;
        this.analyzerList = analyzerList;
        this.detector = detector;
    }

    public List<ParamGroup> getAnalyzerList() {
        return analyzerList;
    }

    public void setAnalyzerList(List<ParamGroup> analyzerList) {
        this.analyzerList = analyzerList;
    }

    public List<ParamGroup> getDetector() {
        return detector;
    }

    public void setDetector(List<ParamGroup> detector) {
        this.detector = detector;
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

    public List<ParamGroup> getSource() {
        return source;
    }

    public void setSource(List<ParamGroup> source) {
        this.source = source;
    }
}
