package uk.ac.ebi.pride.data.core;



import java.util.List;

/**
 * The parameters and settings of a SpectrumIdentification analysis.
 * User: yperez
 * Date: 05/08/11
 * Time: 15:56
 */
public class SpectrumIdentificationProtocol extends Protocol {

    /**
     * A specification of how a nucleic acid sequence database was translated for searching.
     */
    private DataBaseTranslation dataBaseTranslation = null;

    /**
     * The list of enzymes used in experiment
     */
    private List<Enzyme> enzymeList = null;

    /**
     * The specification of filters applied to the database searched.
     */
    private List<Filter> filterList = null;

    /**
     * The tolerance of the search given as a plus and minus value with units.
     */
    private List<CvParam> fragmentTolerance = null;

    /**
     * The masses of residues used in the search.
     */
    private List<MassTable> massTableList = null;

    /**
     * The tolerance of the search given as a plus and minus value with units.
     */
    private List<CvParam> parentTolerance = null;

    /**
     * The specification of static/variable modifications
     * (e.g. Oxidation of Methionine) that are to be considered in the spectra search.
     */
    private List<SearchModification> searchModificationList = null;

    /**
     * The type of search performed e.g. PMF, Tag searches, MS-MS
     */
    private ParamGroup searchType = null;

    /**
     * If there are multiple enzymes specified, this attribute is set to true if
     * cleavage with different enzymes is performed independently.
     */
    private boolean enzymeIndependent = false;

    /**
     * SpectrumIdentificationProtocol Constructor
     *
     * @param id                 Generic Id for Spectrum Identification Protocol
     * @param name               Generic Name for Spectrum Identification Protocol
     * @param analysisSoftware   Analysis Software
     * @param analysisParam      Analysis CvPram Group
     * @param threshold          Threshold
     * @param searchType         Search Type
     * @param searchModificationList   Modification List
     * @param enzymeIndependent        Enzyme Independent
     * @param enzymeList               List of Enzymes used in the Experiment
     * @param massTableList            Mass Table used in the Experiment
     * @param fragmentTolerance        Fragment Tolerance
     * @param parentTolerance          Precursor or Parent Tolerance
     * @param filterList               List of Filters used during the identification
     * @param dataBaseTranslation      Translation Database
     */
    public SpectrumIdentificationProtocol(Comparable id, String name, Software analysisSoftware,
            ParamGroup analysisParam, ParamGroup threshold, ParamGroup searchType,
            List<SearchModification> searchModificationList, boolean enzymeIndependent, List<Enzyme> enzymeList,
            List<MassTable> massTableList, List<CvParam> fragmentTolerance, List<CvParam> parentTolerance,
            List<Filter> filterList, DataBaseTranslation dataBaseTranslation) {
        this(null, id, name, analysisSoftware, threshold, searchType, searchModificationList, enzymeIndependent,
             enzymeList, massTableList, fragmentTolerance, parentTolerance, filterList, dataBaseTranslation);
    }


    public SpectrumIdentificationProtocol(ParamGroup analysisParam, Comparable id, String name,
            Software analysisSoftware, ParamGroup threshold, ParamGroup searchType,
            List<SearchModification> searchModificationList, boolean enzymeIndependent, List<Enzyme> enzymeList,
            List<MassTable> massTableList, List<CvParam> fragmentTolerance, List<CvParam> parentTolerance,
            List<Filter> filterList, DataBaseTranslation dataBaseTranslation) {
        super(analysisParam, id, name, analysisSoftware, threshold);
        this.searchType             = searchType;
        this.searchModificationList = searchModificationList;
        this.enzymeIndependent      = enzymeIndependent;
        this.enzymeList             = enzymeList;
        this.massTableList          = massTableList;
        this.fragmentTolerance      = fragmentTolerance;
        this.parentTolerance        = parentTolerance;
        this.filterList             = filterList;
        this.dataBaseTranslation    = dataBaseTranslation;
    }

    public ParamGroup getSearchType() {
        return searchType;
    }

    public void setSearchType(ParamGroup searchType) {
        this.searchType = searchType;
    }

    public List<SearchModification> getSearchModificationList() {
        return searchModificationList;
    }

    public void setSearchModificationList(List<SearchModification> searchModificationList) {
        this.searchModificationList = searchModificationList;
    }

    public boolean isEnzymeIndependent() {
        return enzymeIndependent;
    }

    public void setEnzymeIndependent(boolean enzymeIndependent) {
        this.enzymeIndependent = enzymeIndependent;
    }

    public List<Enzyme> getEnzymeList() {
        return enzymeList;
    }

    public void setEnzymeList(List<Enzyme> enzymeList) {
        this.enzymeList = enzymeList;
    }

    public List<MassTable> getMassTableList() {
        return massTableList;
    }

    public void setMassTableList(List<MassTable> massTableList) {
        this.massTableList = massTableList;
    }

    public List<CvParam> getFragmentTolerance() {
        return fragmentTolerance;
    }

    public void setFragmentTolerance(List<CvParam> fragmentTolerance) {
        this.fragmentTolerance = fragmentTolerance;
    }

    public List<CvParam> getParentTolerance() {
        return parentTolerance;
    }

    public void setParentTolerance(List<CvParam> parentTolerance) {
        this.parentTolerance = parentTolerance;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public DataBaseTranslation getDataBaseTranslation() {
        return dataBaseTranslation;
    }

    public void setDataBaseTranslation(DataBaseTranslation dataBaseTranslation) {
        this.dataBaseTranslation = dataBaseTranslation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SpectrumIdentificationProtocol that = (SpectrumIdentificationProtocol) o;

        return enzymeIndependent == that.enzymeIndependent && !(dataBaseTranslation != null ? !dataBaseTranslation.equals(that.dataBaseTranslation) : that.dataBaseTranslation != null) && !(enzymeList != null ? !enzymeList.equals(that.enzymeList) : that.enzymeList != null) && !(filterList != null ? !filterList.equals(that.filterList) : that.filterList != null) && !(fragmentTolerance != null ? !fragmentTolerance.equals(that.fragmentTolerance) : that.fragmentTolerance != null) && !(massTableList != null ? !massTableList.equals(that.massTableList) : that.massTableList != null) && !(parentTolerance != null ? !parentTolerance.equals(that.parentTolerance) : that.parentTolerance != null) && !(searchModificationList != null ? !searchModificationList.equals(that.searchModificationList) : that.searchModificationList != null) && !(searchType != null ? !searchType.equals(that.searchType) : that.searchType != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataBaseTranslation != null ? dataBaseTranslation.hashCode() : 0);
        result = 31 * result + (enzymeList != null ? enzymeList.hashCode() : 0);
        result = 31 * result + (filterList != null ? filterList.hashCode() : 0);
        result = 31 * result + (fragmentTolerance != null ? fragmentTolerance.hashCode() : 0);
        result = 31 * result + (massTableList != null ? massTableList.hashCode() : 0);
        result = 31 * result + (parentTolerance != null ? parentTolerance.hashCode() : 0);
        result = 31 * result + (searchModificationList != null ? searchModificationList.hashCode() : 0);
        result = 31 * result + (searchType != null ? searchType.hashCode() : 0);
        result = 31 * result + (enzymeIndependent ? 1 : 0);
        return result;
    }
}



