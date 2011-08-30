package uk.ac.ebi.pride.data.coreIdent;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 05/08/11
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class MassTable extends ParamGroup{

    private String msLevel = null;

    private HashMap<Character,Double> residueList = null;

    private HashMap<Character,ParamGroup> ambiguousResidueList = null;

    public String getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(String msLevel) {
        this.msLevel = msLevel;
    }

    public HashMap<Character, Double> getResidueList() {
        return residueList;
    }

    public void setResidueList(HashMap<Character, Double> residueList) {
        this.residueList = residueList;
    }

    public HashMap<Character, ParamGroup> getAmbiguousResidueList() {
        return ambiguousResidueList;
    }

    public void setAmbiguousResidueList(HashMap<Character, ParamGroup> ambiguousResidueList) {
        this.ambiguousResidueList = ambiguousResidueList;
    }
}
