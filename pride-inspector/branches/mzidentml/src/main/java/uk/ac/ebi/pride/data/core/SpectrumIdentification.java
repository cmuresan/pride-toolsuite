package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * An Analysis which tries to identify peptides in input spectra, referencing the database searched,
 * the input spectra, the output results and the protocol that is run.
 * User: yperez
 * Date: 05/08/11
 * Time: 15:49
 */
public class SpectrumIdentification extends ProtocolApplication {

    /**
     * A reference to the SpectraData List which locates the input spectra to an external file.
     */
    private List<SpectraData> inputSpectraList = null;

    /**
     * List of the search databases used.
     */
    private List<SearchDataBase> searchDataBaseList = null;

    /**
     * A reference to the SpectrumIdentificationList produced by this analysis.
     */
    private SpectrumIdentificationList spectrumIdentificationListRef = null;

    /**
     * A reference to the search protocol used for this SpectrumIdentification.
     */
    private SpectrumIdentificationProtocol spectrumIdentificationProtocolRef = null;

    /**
     * @param id
     * @param name
     * @param activeDate
     * @param spectrumIdentificationProtocolRef
     *
     * @param spectrumIdentificationListRef
     * @param inputSpectraList
     * @param searchDataBaseList
     */
    public SpectrumIdentification(String id, String name, String activeDate,
                                  SpectrumIdentificationProtocol spectrumIdentificationProtocolRef,
                                  SpectrumIdentificationList spectrumIdentificationListRef,
                                  List<SpectraData> inputSpectraList, List<SearchDataBase> searchDataBaseList) {
        super(id, name, activeDate);
        this.spectrumIdentificationProtocolRef = spectrumIdentificationProtocolRef;
        this.spectrumIdentificationListRef     = spectrumIdentificationListRef;
        this.inputSpectraList                  = inputSpectraList;
        this.searchDataBaseList                = searchDataBaseList;
    }

    public SpectrumIdentificationProtocol getSpectrumIdentificationProtocolRef() {
        return spectrumIdentificationProtocolRef;
    }

    public void setSpectrumIdentificationProtocolRef(SpectrumIdentificationProtocol spectrumIdentificationProtocolRef) {
        this.spectrumIdentificationProtocolRef = spectrumIdentificationProtocolRef;
    }

    public SpectrumIdentificationList getSpectrumIdentificationListRef() {
        return spectrumIdentificationListRef;
    }

    public void setSpectrumIdentificationListRef(SpectrumIdentificationList spectrumIdentificationListRef) {
        this.spectrumIdentificationListRef = spectrumIdentificationListRef;
    }

    public List<SpectraData> getInputSpectraList() {
        return inputSpectraList;
    }

    public void setInputSpectraList(List<SpectraData> inputSpectraList) {
        this.inputSpectraList = inputSpectraList;
    }

    public List<SearchDataBase> getSearchDataBaseList() {
        return searchDataBaseList;
    }

    public void setSearchDataBaseList(List<SearchDataBase> searchDataBaseList) {
        this.searchDataBaseList = searchDataBaseList;
    }
}



