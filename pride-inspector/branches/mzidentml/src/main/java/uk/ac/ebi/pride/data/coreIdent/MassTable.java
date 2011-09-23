package uk.ac.ebi.pride.data.coreIdent;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 05/08/11
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
public class MassTable extends ParamGroup{

    private List<Integer> msLevel = null;

    private Map<String,Double> residues = null;

    private Map<String,ParamGroup> ambiguousResidues = null;


    public MassTable(List<Integer> msLevel, Map<String, Double> residueList, Map<String, ParamGroup> ambiguousResidueList) {
        this.msLevel = msLevel;
        this.residues = residueList;
        this.ambiguousResidues = ambiguousResidueList;
    }

    public List<Integer> getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(List<Integer> msLevel) {
        this.msLevel = msLevel;
    }

    public Map<String, Double> getResidues() {
        return residues;
    }

    public void setResidues(Map<String, Double> residues) {
        this.residues = residues;
    }

    public Map<String, ParamGroup> getAmbiguousResidues() {
        return ambiguousResidues;
    }

    public void setAmbiguousResidues(Map<String, ParamGroup> ambiguousResidues) {
        this.ambiguousResidues = ambiguousResidues;
    }
}
