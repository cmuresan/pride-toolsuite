package uk.ac.ebi.pride.data.core;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
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
}



