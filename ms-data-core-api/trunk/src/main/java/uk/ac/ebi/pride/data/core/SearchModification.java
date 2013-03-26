package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import uk.ac.ebi.pride.data.utils.CollectionUtils;

import java.util.List;

/**
 * Search Modification is the variable or fixed modification defined in the Identification Search
 * by the user. This modification is close related with the parameters of the Spectrum protocol
 * Identification.
 *
 * <p/>
 * User: yperez
 * Date: 05/08/11
 * Time: 16:04
 */
public class SearchModification {

    private List<CvParam> cvParamList;

    private boolean fixedMod;

    private double massDelta;

    private List<String> specificityList;

    private List<CvParam> specificityRuleList;

    public SearchModification(boolean fixedMod, double massDelta, List<String> specificityList,
                              List<CvParam> specificityRuleList, List<CvParam> cvParamList) {
        this.fixedMod            = fixedMod;
        this.massDelta           = massDelta;
        this.specificityList     = CollectionUtils.createListFromList(specificityList);
        this.specificityRuleList = CollectionUtils.createListFromList(specificityRuleList);
        this.cvParamList         = CollectionUtils.createListFromList(cvParamList);
    }

    public boolean isFixedMod() {
        return fixedMod;
    }

    public void setFixedMod(boolean fixedMod) {
        this.fixedMod = fixedMod;
    }

    public double getMassDelta() {
        return massDelta;
    }

    public void setMassDelta(double massDelta) {
        this.massDelta = massDelta;
    }

    public List<String> getSpecificityList() {
        return specificityList;
    }

    public void setSpecificityList(List<String> specificityList) {
        CollectionUtils.replaceValuesInCollection(specificityList, this.specificityList);
    }

    public List<CvParam> getSpecificityRuleList() {
        return specificityRuleList;
    }

    public void setSpecificityRuleList(List<CvParam> specificityRuleList) {
        CollectionUtils.replaceValuesInCollection(specificityRuleList, this.specificityRuleList);
    }

    public List<CvParam> getCvParamList() {
        return cvParamList;
    }

    public void setCvParamList(List<CvParam> cvParamList) {
        CollectionUtils.replaceValuesInCollection(cvParamList, this.cvParamList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchModification)) return false;

        SearchModification that = (SearchModification) o;

        if (fixedMod != that.fixedMod) return false;
        if (Double.compare(that.massDelta, massDelta) != 0) return false;
        if (!cvParamList.equals(that.cvParamList)) return false;
        if (!specificityList.equals(that.specificityList)) return false;
        if (!specificityRuleList.equals(that.specificityRuleList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = cvParamList.hashCode();
        result = 31 * result + (fixedMod ? 1 : 0);
        temp = massDelta != +0.0d ? Double.doubleToLongBits(massDelta) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + specificityList.hashCode();
        result = 31 * result + specificityRuleList.hashCode();
        return result;
    }
}



