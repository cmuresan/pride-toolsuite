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
}


//~ Formatted by Jindent --- http://www.jindent.com
