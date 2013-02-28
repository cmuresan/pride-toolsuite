package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 * todo: add documentation
 *
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 05/08/11
 * Time: 17:36
 */
public class DataBaseTranslation {
    List<Integer>                alowedFrames         = null;
    List<IdentifiableParamGroup> translationTableList = null;

    public DataBaseTranslation(List<Integer> alowedFrames, List<IdentifiableParamGroup> translationTableList) {
        this.alowedFrames         = alowedFrames;
        this.translationTableList = translationTableList;
    }

    public List<Integer> getAlowedFrames() {
        return alowedFrames;
    }

    public void setAlowedFrames(List<Integer> alowedFrames) {
        this.alowedFrames = alowedFrames;
    }

    public List<IdentifiableParamGroup> getTranslationTableList() {
        return translationTableList;
    }

    public void setTranslationTableList(List<IdentifiableParamGroup> translationTableList) {
        this.translationTableList = translationTableList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataBaseTranslation that = (DataBaseTranslation) o;

        return !(alowedFrames != null ? !alowedFrames.equals(that.alowedFrames) : that.alowedFrames != null) && !(translationTableList != null ? !translationTableList.equals(that.translationTableList) : that.translationTableList != null);

    }

    @Override
    public int hashCode() {
        int result = alowedFrames != null ? alowedFrames.hashCode() : 0;
        result = 31 * result + (translationTableList != null ? translationTableList.hashCode() : 0);
        return result;
    }
}



