package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * This class is used to manage and store the information of the metadata for protein and spectrum
 * identifications.
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 19/08/11
 * Time: 11:56
 *
 */
public class IdentificationMetaData extends IdentifiableParamGroup{
    /**
     * List of the parameters and settings of a SpectrumIdentification analysis.
     */
    private List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = null;
    /**
     * The parameters and settings of a ProteinDetection process.
     */
    private Protocol proteinDetectionProtocol = null;
    /**
     * List of database for searching mass spectra. Examples include a set of amino acid sequence entries, or annotated spectra libraries.
     */
    private List<SearchDataBase> searchDataBaseList = null;
    /**
     * List of all modifications listed in the experiment.
     */
    /**
     *
     * @param id
     * @param name
     * @param spectrumIdentificationProtocolList
     * @param proteinDetectionProtocol
     * @param searchDataBaseList
     */
    public IdentificationMetaData(Comparable id,
                                  String name,
                                  List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList,
                                  Protocol proteinDetectionProtocol,
                                  List<SearchDataBase> searchDataBaseList) {
        super(id, name);
        this.spectrumIdentificationProtocolList = spectrumIdentificationProtocolList;
        this.proteinDetectionProtocol = proteinDetectionProtocol;
        this.searchDataBaseList = searchDataBaseList;
    }

    /**
     *
     * @param params
     * @param id
     * @param name
     * @param spectrumIdentificationProtocolList
     * @param proteinDetectionProtocol
     * @param searchDataBaseList
     * @param modificationList
     */
    public IdentificationMetaData(ParamGroup params,
                                  Comparable id,
                                  String name,
                                  List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList,
                                  Protocol proteinDetectionProtocol,
                                  List<SearchDataBase> searchDataBaseList,
                                  List<Modification> modificationList) {
        super(params, id, name);
        this.spectrumIdentificationProtocolList = spectrumIdentificationProtocolList;
        this.proteinDetectionProtocol = proteinDetectionProtocol;
        this.searchDataBaseList = searchDataBaseList;
    }

    public List<SpectrumIdentificationProtocol> getSpectrumIdentificationProtocolList() {
        return spectrumIdentificationProtocolList;
    }

    public void setSpectrumIdentificationProtocolList(List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList) {
        this.spectrumIdentificationProtocolList = spectrumIdentificationProtocolList;
    }

    public Protocol getProteinDetectionProtocol() {
        return proteinDetectionProtocol;
    }

    public void setProteinDetectionProtocol(Protocol proteinDetectionProtocol) {
        this.proteinDetectionProtocol = proteinDetectionProtocol;
    }

    public List<SearchDataBase> getSearchDataBaseList() {
        return searchDataBaseList;
    }

    public void setSearchDataBaseList(List<SearchDataBase> searchDataBaseList) {
        this.searchDataBaseList = searchDataBaseList;
    }
}
