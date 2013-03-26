package uk.ac.ebi.pride.data.core;

/**
 * ToDo: document this class
 * ToDo: finish implementing this class or remove it
 * User: yperez
 * Date: 08/08/11
 * Time: 14:24
 */
public class ProteinDetectionList extends ParamGroup {
    String id;
    String name;
    //List<ProteinAmbiguityGroup> proteinAmbiguityGroupList = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ProteinDetectionList that = (ProteinDetectionList) o;

        return !(id != null ? !id.equals(that.id) : that.id != null) && !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}



