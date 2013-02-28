package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

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

    private List<CvParam> cvParamList         = null;

    private boolean       fixedMod            = false;

    private double        massDelta           = -1;

    private List<String>  specificityList     = null;

    private List<CvParam> specificityRuleList = null;

    public SearchModification(boolean fixedMod, double massDelta, List<String> specificityList,
                              List<CvParam> specificityRuleList, List<CvParam> cvParamList) {
        this.fixedMod            = fixedMod;
        this.massDelta           = massDelta;
        this.specificityList     = specificityList;
        this.specificityRuleList = specificityRuleList;
        this.cvParamList         = cvParamList;
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
        this.specificityList = specificityList;
    }

    public List<CvParam> getSpecificityRuleList() {
        return specificityRuleList;
    }

    public void setSpecificityRuleList(List<CvParam> specificityRuleList) {
        this.specificityRuleList = specificityRuleList;
    }

    public List<CvParam> getCvParamList() {
        return cvParamList;
    }

    public void setCvParamList(List<CvParam> cvParamList) {
        this.cvParamList = cvParamList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchModification that = (SearchModification) o;

        return fixedMod == that.fixedMod && Double.compare(that.massDelta, massDelta) == 0 && !(cvParamList != null ? !cvParamList.equals(that.cvParamList) : that.cvParamList != null) && !(specificityList != null ? !specificityList.equals(that.specificityList) : that.specificityList != null) && !(specificityRuleList != null ? !specificityRuleList.equals(that.specificityRuleList) : that.specificityRuleList != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = cvParamList != null ? cvParamList.hashCode() : 0;
        result = 31 * result + (fixedMod ? 1 : 0);
        temp = massDelta != +0.0d ? Double.doubleToLongBits(massDelta) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (specificityList != null ? specificityList.hashCode() : 0);
        result = 31 * result + (specificityRuleList != null ? specificityRuleList.hashCode() : 0);
        return result;
    }
}



