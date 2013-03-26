package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.List;

/**
 * This class is used to manage and store the information of the metadata for protein and spectrum
 * identifications.
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 19/08/11
 * Time: 11:56
 */
public class IdentificationMetaData extends IdentifiableParamGroup {

    /**
     * The parameters and settings of a ProteinDetection process.
     */
    private Protocol proteinDetectionProtocol;

    /**
     * List of database for searching mass spectra. Examples include a set of amino acid sequence entries, or annotated spectra libraries.
     */
    private List<SearchDataBase> searchDataBaseList;

    /**
     * List of the parameters and settings of a SpectrumIdentification analysis.
     */
    private List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList;


    /**
     * Constructor of Identification MetaData Objects
     * @param id ID
     * @param name Name
     * @param spectrumIdentificationProtocolList Spectrum Identification Protocol
     * @param proteinDetectionProtocol Protein Detection Protocol
     * @param searchDataBaseList List of the DataBases used in the experiment.
     */
    public IdentificationMetaData(Comparable id, String name,
                                  List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList,
                                  Protocol proteinDetectionProtocol, List<SearchDataBase> searchDataBaseList) {
        super(id, name);
        this.spectrumIdentificationProtocolList = CollectionUtils.createListFromList(spectrumIdentificationProtocolList);
        this.searchDataBaseList = CollectionUtils.createListFromList(searchDataBaseList);
        this.proteinDetectionProtocol = proteinDetectionProtocol;
    }

    /**
     * Constructor of Identification MetaData Objects
     * @param params ParamGroup (CvTerms and User Params)
     * @param id ID
     * @param name Name
     * @param spectrumIdentificationProtocolList Spectrum Identification Protocol
     * @param proteinDetectionProtocol Protein Detection Protocol
     * @param searchDataBaseList List of the DataBases used in the experiment.
     */
    public IdentificationMetaData(ParamGroup params, Comparable id, String name,
                                  List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList,
                                  Protocol proteinDetectionProtocol, List<SearchDataBase> searchDataBaseList) {
        super(params, id, name);
        this.spectrumIdentificationProtocolList = CollectionUtils.createListFromList(spectrumIdentificationProtocolList);
        this.searchDataBaseList = CollectionUtils.createListFromList(searchDataBaseList);
        this.proteinDetectionProtocol = proteinDetectionProtocol;
    }

    /**
     * Get Spectrum Identification Protocol List
     *
     * @return Spectrum Identification Protocol List
     */
    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocolList() {
        return spectrumIdentificationProtocolList;
    }

    /**
     * Set Spectrum Identification Protocol List
     *
     * @param spectrumIdentificationProtocolList Spectrum Identification Protocol List
     */
    public void setSpectrumIdentificationProtocolList(List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList) {
        CollectionUtils.replaceValuesInCollection(spectrumIdentificationProtocolList, this.spectrumIdentificationProtocolList);
    }

    /**
     * Get Protein Detection Protocol
     *
     * @return Protein Detection Protocol
     */
    public Protocol getProteinDetectionProtocol() {
        return proteinDetectionProtocol;
    }

    /**
     * Set Protein Detection Protocol
     *
     * @param proteinDetectionProtocol Protein Detection Protocol
     */
    public void setProteinDetectionProtocol(Protocol proteinDetectionProtocol) {
        this.proteinDetectionProtocol = proteinDetectionProtocol;
    }

    /**
     * Get List of Search DataBases
     *
     * @return Search Databases List
     */
    public List<SearchDataBase> getSearchDataBaseList() {
        return searchDataBaseList;
    }

    /**
     * Set List of Search DataBases
     *
     * @param searchDataBaseList Search Databases
     */
    public void setSearchDataBaseList(List<SearchDataBase> searchDataBaseList) {
        CollectionUtils.replaceValuesInCollection(searchDataBaseList, this.searchDataBaseList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentificationMetaData)) return false;
        if (!super.equals(o)) return false;

        IdentificationMetaData that = (IdentificationMetaData) o;

        if (proteinDetectionProtocol != null ? !proteinDetectionProtocol.equals(that.proteinDetectionProtocol) : that.proteinDetectionProtocol != null)
            return false;
        if (!searchDataBaseList.equals(that.searchDataBaseList)) return false;
        if (!spectrumIdentificationProtocolList.equals(that.spectrumIdentificationProtocolList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (proteinDetectionProtocol != null ? proteinDetectionProtocol.hashCode() : 0);
        result = 31 * result + searchDataBaseList.hashCode();
        result = 31 * result + spectrumIdentificationProtocolList.hashCode();
        return result;
    }
}



