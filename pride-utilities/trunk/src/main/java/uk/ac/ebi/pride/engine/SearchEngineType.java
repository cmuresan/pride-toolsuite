package uk.ac.ebi.pride.engine;

import uk.ac.ebi.pride.term.CvTermReference;

import java.util.Arrays;
import java.util.List;

/**
 * SearchEngineType defines a list of supported search engines.
 *
 * User: rwang
 * Date: 01-Dec-2010
 * Time: 16:41:12
 */
public enum SearchEngineType {
    MASCOT(Arrays.asList(CvTermReference.MASCOT_SCORE, CvTermReference.MASCOT_EXPECT_VALUE,CvTermReference.MS_MASCOT_SCORE,CvTermReference.MS_MASCOT_EXPECT_VALUE)),
    XTANDEM(Arrays.asList(CvTermReference.XTANDEM_HYPER_SCORE, CvTermReference.XTANDEM_EXPECTANCY_SCORE,CvTermReference.MS_XTANDEM_EXPECTANCY_SCORE,CvTermReference.MS_XTANDEM_HYPERSCORE)),
    SEQUEST(Arrays.asList(CvTermReference.SEQUEST_SCORE, CvTermReference.X_CORRELATION, CvTermReference.SEQUEST_DELTA_CN,CvTermReference.MS_SEQUEST_CONSENSUS_SCORE,CvTermReference.MS_SEQUEST_DELTA_CN,CvTermReference.MS_SEQUEST_XCORR)),
    SPECTRUM_MILL(Arrays.asList(CvTermReference.SPECTRUM_MILL_PEPTIDE_SCORE,CvTermReference.MS_SPECTRUMMILL_SCORE)),
    OMSSA(Arrays.asList(CvTermReference.OMSSA_E_VALUE, CvTermReference.OMSSA_P_VALUE,CvTermReference.MS_OMSSA_E,CvTermReference.MS_OMSSA_P));

    private List<CvTermReference> searchEngineScores;

    private SearchEngineType(List<CvTermReference> searchEngineScores) {
        this.searchEngineScores = searchEngineScores;
    }

    public List<CvTermReference> getSearchEngineScores() {
        return searchEngineScores;
    }

    public static SearchEngineType getByCvTermReference(CvTermReference termReference){
        for (SearchEngineType searchEngineType : SearchEngineType.values()) {
            for (CvTermReference termReferenceAux : searchEngineType.getSearchEngineScores()) {
                if(termReferenceAux.equals(termReference)) return searchEngineType;
            }
        }
        return null;
    }
}
