package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 04/08/11
 * Time: 10:16
 */
public class MzGraphList {

    private DataProcessing defaultDataProcessingRef = null;

    private List<MzGraph>  mzGraphList              = null;

    public MzGraphList(DataProcessing defaultDataProcessingRef, List<MzGraph> mzGraphList) {
        this.defaultDataProcessingRef = defaultDataProcessingRef;
        this.mzGraphList              = mzGraphList;
    }

    public DataProcessing getDefaultDataProcessingRef() {
        return defaultDataProcessingRef;
    }

    public void setDefaultDataProcessingRef(DataProcessing defaultDataProcessingRef) {
        this.defaultDataProcessingRef = defaultDataProcessingRef;
    }

    public List<MzGraph> getMzGraphList() {
        return mzGraphList;
    }

    public void setMzGraphList(List<MzGraph> mzGraphList) {
        this.mzGraphList = mzGraphList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MzGraphList that = (MzGraphList) o;

        return !(defaultDataProcessingRef != null ? !defaultDataProcessingRef.equals(that.defaultDataProcessingRef) : that.defaultDataProcessingRef != null) && !(mzGraphList != null ? !mzGraphList.equals(that.mzGraphList) : that.mzGraphList != null);

    }

    @Override
    public int hashCode() {
        int result = defaultDataProcessingRef != null ? defaultDataProcessingRef.hashCode() : 0;
        result = 31 * result + (mzGraphList != null ? mzGraphList.hashCode() : 0);
        return result;
    }
}



