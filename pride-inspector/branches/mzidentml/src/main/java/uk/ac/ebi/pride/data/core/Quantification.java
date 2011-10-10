package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.QuantCvTermReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Quantification object is a pseudo object which contains all the cv params related to quantitative data
 *
 * It also provides a set of methods for accessing these information
 *
 *
 * User: rwang
 * Date: 28/07/2011
 * Time: 09:21
 */
public class Quantification {

    /**
     * The type of the identification;
     */
    public enum Type {PROTEIN, PEPTIDE}

    /**
     * A list of quantitative cv params
     */
    private List<CvParam> cvParamList;

    /**
     * The type of the identification
     */
    private Type type;


    public Quantification(Type type, List<CvParam> cvParamList) {
        this.type = type;
        this.cvParamList = new ArrayList<CvParam>(cvParamList);
    }

    public List<CvParam> getCvParamList() {
        return cvParamList;
    }

    public void setCvParamList(List<CvParam> cvParamList) {
        this.cvParamList = cvParamList;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean hasLabelFreeQuantMethod() {
        return false;
    }

    public List<CvParam> getLabelFreeQuantData() {
        return null;
    }

    public boolean hasIsotopeLabellingQuantData() {
        return false;
    }

    public List<CvParam> getIsotopeLabellingQuantData() {
        return null;
    }

    public QuantCvTermReference getUnit() {
        return null;
    }

}
