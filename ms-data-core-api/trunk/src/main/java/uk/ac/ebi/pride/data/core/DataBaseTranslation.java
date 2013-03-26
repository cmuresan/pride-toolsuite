package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import uk.ac.ebi.pride.data.utils.CollectionUtils;

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
    private List<Integer> allowedFrames;
    private List<IdentifiableParamGroup> translationTableList;

    public DataBaseTranslation(List<Integer> allowedFrames, List<IdentifiableParamGroup> translationTableList) {
        this.allowedFrames = CollectionUtils.createListFromList(allowedFrames);
        this.translationTableList = CollectionUtils.createListFromList(translationTableList);
    }

    public List<Integer> getAllowedFrames() {
        return allowedFrames;
    }

    public void setAllowedFrames(List<Integer> allowedFrames) {
        CollectionUtils.replaceValuesInCollection(allowedFrames, this.allowedFrames);
    }

    public List<IdentifiableParamGroup> getTranslationTableList() {
        return translationTableList;
    }

    public void setTranslationTableList(List<IdentifiableParamGroup> translationTableList) {
        CollectionUtils.replaceValuesInCollection(translationTableList, this.translationTableList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataBaseTranslation)) return false;

        DataBaseTranslation that = (DataBaseTranslation) o;

        if (!allowedFrames.equals(that.allowedFrames)) return false;
        if (!translationTableList.equals(that.translationTableList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = allowedFrames.hashCode();
        result = 31 * result + translationTableList.hashCode();
        return result;
    }
}



