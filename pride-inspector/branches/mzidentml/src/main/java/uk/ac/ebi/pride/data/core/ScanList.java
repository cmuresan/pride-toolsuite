package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * List and descriptions of scans.
 * <p/>
 * In mzML 1.1.0.1, the following cv terms must be added:
 * <p/>
 * 1. Must include only one child term of "spectra combination".
 * (sum of spectra, no combination)
 * <p/>
 * User: rwang
 * Date: 20-Feb-2010
 * Time: 18:57:52
 */
public class ScanList extends ParamGroup {
    private List<Scan> scans = null;

    /**
     * Constructor
     *
     * @param scans  required.
     * @param params optional.
     */
    public ScanList(List<Scan> scans, ParamGroup params) {
        super(params);
        this.scans = scans;
    }

    public List<Scan> getScans() {
        return scans;
    }

    public void setScans(List<Scan> scans) {
        this.scans = scans;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
