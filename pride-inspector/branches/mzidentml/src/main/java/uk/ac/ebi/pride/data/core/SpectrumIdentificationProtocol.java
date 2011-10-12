package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

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
     * @param id
     * @param name
     * @param analysisSoftware
     * @param analysisParam
     * @param threshold
     * @param searchType
     * @param searchModificationList
     * @param enzymeIndependent
     * @param enzymeList
     * @param massTableList
     * @param fragmentTolerance
     * @param parentTolerance
     * @param filterList
     * @param dataBaseTranslation
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
}



