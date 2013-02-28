package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 05/08/11
 * Time: 16:40
 */
public class MassTable extends ParamGroup {
    private Map<String, ParamGroup> ambiguousResidues = null;
    private List<Integer>           msLevel           = null;
    private Map<String, Float>      residues          = null;

    public MassTable(List<Integer> msLevel, Map<String, Float> residueList,
                     Map<String, ParamGroup> ambiguousResidueList) {
        this.msLevel           = msLevel;
        this.residues          = residueList;
        this.ambiguousResidues = ambiguousResidueList;
    }

    public List<Integer> getMsLevel() {
        return msLevel;
    }

    public void setMsLevel(List<Integer> msLevel) {
        this.msLevel = msLevel;
    }

    public Map<String, Float> getResidues() {
        return residues;
    }

    public void setResidues(Map<String, Float> residues) {
        this.residues = residues;
    }

    public Map<String, ParamGroup> getAmbiguousResidues() {
        return ambiguousResidues;
    }

    public void setAmbiguousResidues(Map<String, ParamGroup> ambiguousResidues) {
        this.ambiguousResidues = ambiguousResidues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MassTable massTable = (MassTable) o;

        return !(ambiguousResidues != null ? !ambiguousResidues.equals(massTable.ambiguousResidues) : massTable.ambiguousResidues != null) && !(msLevel != null ? !msLevel.equals(massTable.msLevel) : massTable.msLevel != null) && !(residues != null ? !residues.equals(massTable.residues) : massTable.residues != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ambiguousResidues != null ? ambiguousResidues.hashCode() : 0);
        result = 31 * result + (msLevel != null ? msLevel.hashCode() : 0);
        result = 31 * result + (residues != null ? residues.hashCode() : 0);
        return result;
    }
}



