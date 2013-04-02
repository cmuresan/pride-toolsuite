package uk.ac.ebi.pride.data.core;


import uk.ac.ebi.pride.data.utils.CollectionUtils;

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
    private DataBaseTranslation dataBaseTranslation;

    /**
     * The list of enzymes used in experiment
     */
    private List<Enzyme> enzymeList;

    /**
     * The specification of filters applied to the database searched.
     */
    private List<Filter> filterList;

    /**
     * The tolerance of the search given as a plus and minus value with units.
     */
    private List<CvParam> fragmentTolerance;

    /**
     * The masses of residues used in the search.
     */
    private List<MassTable> massTableList;

    /**
     * The tolerance of the search given as a plus and minus value with units.
     */
    private List<CvParam> parentTolerance;

    /**
     * The specification of static/variable modifications
     * (e.g. Oxidation of Methionine) that are to be considered in the spectra search.
     */
    private List<SearchModification> searchModificationList;

    /**
     * The type of search performed e.g. PMF, Tag searches, MS-MS
     */
    private ParamGroup searchType;

    /**
     * If there are multiple enzymes specified, this attribute is set to true if
     * cleavage with different enzymes is performed independently.
     */
    private boolean enzymeIndependent;

    /**
     * SpectrumIdentificationProtocol Constructor
     *
     * @param id                     Generic Id for Spectrum Identification Protocol
     * @param name                   Generic Name for Spectrum Identification Protocol
     * @param analysisSoftware       Analysis Software
     * @param analysisParam          Analysis CvPram Group
     * @param threshold              Threshold
     * @param searchType             Search Type
     * @param searchModificationList Modification List
     * @param enzymeIndependent      Enzyme Independent
     * @param enzymeList             List of Enzymes used in the Experiment
     * @param massTableList          Mass Table used in the Experiment
     * @param fragmentTolerance      Fragment Tolerance
     * @param parentTolerance        Precursor or Parent Tolerance
     * @param filterList             List of Filters used during the identification
     * @param dataBaseTranslation    Translation Database
     */
    public SpectrumIdentificationProtocol(Comparable id, String name, Software analysisSoftware,
                                          ParamGroup analysisParam, ParamGroup threshold, ParamGroup searchType,
                                          List<SearchModification> searchModificationList, boolean enzymeIndependent, List<Enzyme> enzymeList,
                                          List<MassTable> massTableList, List<CvParam> fragmentTolerance, List<CvParam> parentTolerance,
                                          List<Filter> filterList, DataBaseTranslation dataBaseTranslation) {
        this(analysisParam, id, name, analysisSoftware, threshold, searchType, searchModificationList, enzymeIndependent,
                enzymeList, massTableList, fragmentTolerance, parentTolerance, filterList, dataBaseTranslation);
    }


    public SpectrumIdentificationProtocol(ParamGroup analysisParam, Comparable id, String name,
                                          Software analysisSoftware, ParamGroup threshold, ParamGroup searchType,
                                          List<SearchModification> searchModificationList, boolean enzymeIndependent, List<Enzyme> enzymeList,
                                          List<MassTable> massTableList, List<CvParam> fragmentTolerance, List<CvParam> parentTolerance,
                                          List<Filter> filterList, DataBaseTranslation dataBaseTranslation) {
        super(analysisParam, id, name, analysisSoftware, threshold);
        this.searchType = searchType;
        this.searchModificationList = CollectionUtils.createListFromList(searchModificationList);
        this.enzymeIndependent = enzymeIndependent;
        this.enzymeList = CollectionUtils.createListFromList(enzymeList);
        this.massTableList = CollectionUtils.createListFromList(massTableList);
        this.fragmentTolerance = CollectionUtils.createListFromList(fragmentTolerance);
        this.parentTolerance = CollectionUtils.createListFromList(parentTolerance);
        this.filterList = CollectionUtils.createListFromList(filterList);
        this.dataBaseTranslation = dataBaseTranslation;
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
        CollectionUtils.replaceValuesInCollection(searchModificationList, this.searchModificationList);
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
        CollectionUtils.replaceValuesInCollection(enzymeList, this.enzymeList);
    }

    public List<MassTable> getMassTableList() {
        return massTableList;
    }

    public void setMassTableList(List<MassTable> massTableList) {
        CollectionUtils.replaceValuesInCollection(massTableList, this.massTableList);
    }

    public List<CvParam> getFragmentTolerance() {
        return fragmentTolerance;
    }

    public void setFragmentTolerance(List<CvParam> fragmentTolerance) {
        CollectionUtils.replaceValuesInCollection(fragmentTolerance, this.fragmentTolerance);
    }

    public List<CvParam> getParentTolerance() {
        return parentTolerance;
    }

    public void setParentTolerance(List<CvParam> parentTolerance) {
        CollectionUtils.replaceValuesInCollection(parentTolerance, this.parentTolerance);
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        CollectionUtils.replaceValuesInCollection(filterList, this.filterList);
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
        if (!(o instanceof SpectrumIdentificationProtocol)) return false;
        if (!super.equals(o)) return false;

        SpectrumIdentificationProtocol that = (SpectrumIdentificationProtocol) o;

        if (enzymeIndependent != that.enzymeIndependent) return false;
        if (dataBaseTranslation != null ? !dataBaseTranslation.equals(that.dataBaseTranslation) : that.dataBaseTranslation != null)
            return false;
        if (!enzymeList.equals(that.enzymeList)) return false;
        if (!filterList.equals(that.filterList)) return false;
        if (!fragmentTolerance.equals(that.fragmentTolerance)) return false;
        if (!massTableList.equals(that.massTableList)) return false;
        if (!parentTolerance.equals(that.parentTolerance)) return false;
        if (!searchModificationList.equals(that.searchModificationList)) return false;
        return !(searchType != null ? !searchType.equals(that.searchType) : that.searchType != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (dataBaseTranslation != null ? dataBaseTranslation.hashCode() : 0);
        result = 31 * result + enzymeList.hashCode();
        result = 31 * result + filterList.hashCode();
        result = 31 * result + fragmentTolerance.hashCode();
        result = 31 * result + massTableList.hashCode();
        result = 31 * result + parentTolerance.hashCode();
        result = 31 * result + searchModificationList.hashCode();
        result = 31 * result + (searchType != null ? searchType.hashCode() : 0);
        result = 31 * result + (enzymeIndependent ? 1 : 0);
        return result;
    }
}



