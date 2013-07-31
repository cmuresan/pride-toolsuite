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

    private DataProcessing defaultDataProcessing;

    private final List<MzGraph> mzGraphs;

    public MzGraphList(DataProcessing defaultDataProcessing, List<MzGraph> mzGraphs) {
        this.defaultDataProcessing = defaultDataProcessing;
        this.mzGraphs = CollectionUtils.createListFromList(mzGraphs);
    }

    public DataProcessing getDefaultDataProcessing() {
        return defaultDataProcessing;
    }

    public void setDefaultDataProcessing(DataProcessing defaultDataProcessing) {
        this.defaultDataProcessing = defaultDataProcessing;
    }

    public List<MzGraph> getMzGraphs() {
        return mzGraphs;
    }

    public void setMzGraphs(List<MzGraph> mzGraphs) {
        CollectionUtils.replaceValuesInCollection(mzGraphs, this.mzGraphs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MzGraphList)) return false;

        MzGraphList that = (MzGraphList) o;

        return !(defaultDataProcessing != null ? !defaultDataProcessing.equals(that.defaultDataProcessing) : that.defaultDataProcessing != null) && mzGraphs.equals(that.mzGraphs);

    }

    @Override
    public int hashCode() {
        int result = defaultDataProcessing != null ? defaultDataProcessing.hashCode() : 0;
        result = 31 * result + mzGraphs.hashCode();
        return result;
    }
}



