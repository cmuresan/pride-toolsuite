package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.List;

/**
 * ToDo: document this class
 * <p/>
 * User: yperez
 * Date: 04/08/11
 * Time: 10:16
 */
public class MzGraphList {

    private DataProcessing defaultDataProcessingRef;

    private List<MzGraph>  mzGraphList;

    public MzGraphList(DataProcessing defaultDataProcessingRef, List<MzGraph> mzGraphList) {
        this.defaultDataProcessingRef = defaultDataProcessingRef;
        this.mzGraphList = CollectionUtils.createListFromList(mzGraphList);
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
        CollectionUtils.replaceValuesInCollection(mzGraphList, this.mzGraphList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MzGraphList)) return false;

        MzGraphList that = (MzGraphList) o;

        if (defaultDataProcessingRef != null ? !defaultDataProcessingRef.equals(that.defaultDataProcessingRef) : that.defaultDataProcessingRef != null)
            return false;
        if (!mzGraphList.equals(that.mzGraphList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = defaultDataProcessingRef != null ? defaultDataProcessingRef.hashCode() : 0;
        result = 31 * result + mzGraphList.hashCode();
        return result;
    }
}



