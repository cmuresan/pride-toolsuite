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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ScanList scanList = (ScanList) o;

        return !(scans != null ? !scans.equals(scanList.scans) : scanList.scans != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (scans != null ? scans.hashCode() : 0);
        return result;
    }
}



