package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 05/08/11
 * Time: 16:04
 *
 */
public class SearchModification {

    private boolean fixedMod = false;

    private double massDelta = 0.0;

    private List<Character> specificityList = null;

    private List<CvParam> specificityRuleList = null;

    private List<CvParam> cvParamList = null;

    public SearchModification(boolean fixedMod, double massDelta, List<Character> specificityList, List<CvParam> specificityRuleList, List<CvParam> cvParamList) {
        this.fixedMod = fixedMod;
        this.massDelta = massDelta;
        this.specificityList = specificityList;
        this.specificityRuleList = specificityRuleList;
        this.cvParamList = cvParamList;
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

    public List<Character> getSpecificityList() {
        return specificityList;
    }

    public void setSpecificityList(List<Character> specificityList) {
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
}
