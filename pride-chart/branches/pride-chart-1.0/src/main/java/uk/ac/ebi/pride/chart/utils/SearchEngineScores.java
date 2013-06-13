package uk.ac.ebi.pride.chart.utils;

import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.*;

/**
 * Stores a number of scores for a list of search engines.
 * Please Note that even if scores are null if they are not provided.
 *
 * User: rwang, Qingwei
 * Date: 10/06/13
 */
public class SearchEngineScores {
    private Map<SearchEngineType, Map<CvTermReference, Number>> scores;

    /**
     * Constructor
     */
    public SearchEngineScores() {
        scores = new LinkedHashMap<SearchEngineType, Map<CvTermReference, Number>>();
    }

    /**
     * Get peptide scores for search engine
     *
     * @param se    search engine
     * @return  Map<CvTermReference, Number>    peptide score map
     */
    public Map<CvTermReference, Number> getScores(SearchEngineType se) {
        return se == null ? null : scores.get(se);
    }

    /**
     * Get peptide score for a specified search engine and cv term reference
     *
     * @param se    search engine
     * @param ref   cv term reference
     * @return  Number  peptide score
     */
    public Number getScore(SearchEngineType se, CvTermReference ref) {
        Map<CvTermReference, Number> scoreMap = getScores(se);
        return scoreMap == null ? null : scoreMap.get(ref);
    }


    /**
     * Get all peptide scores, this will produce a sequential order list
     *
     * @return  List<Number>    a list of scores
     */
    public List<Number> getAllScores() {
        List<Number> scoreList = new ArrayList<Number>();
        for (Map<CvTermReference, Number> numberMap : scores.values()) {
            scoreList.addAll(numberMap.values());
        }
        return scoreList;
    }

    /**
     * Get all the search engine types within this peptide
     *
     * @return  List<SearchEngineType>  a list of search engine types
     */
    public List<SearchEngineType> getSearchEngineTypes() {
        return new ArrayList<SearchEngineType>(scores.keySet());
    }

    /**
     * Add a new peptide score
     *
     * @param se    search engine
     * @param ref   cv term reference for the score type
     * @param num   peptide score
     */
    public void addScore(SearchEngineType se, CvTermReference ref, Number num) {
        if (se == null || ref == null || num == null) {
            throw new IllegalArgumentException("Input arguments for addScore cannot be null");
        }

        // create a new if the search engine doesn't exist
        Map<CvTermReference, Number> scoreMap = getScores(se);
        if (scoreMap == null) {
            scoreMap = new LinkedHashMap<CvTermReference, Number>();
            scores.put(se, scoreMap);

            // for each cv term
            List<CvTermReference> cvTerms = se.getSearchEngineScores();
            for (CvTermReference cvTerm : cvTerms) {
                scoreMap.put(cvTerm, null);
            }
        } else {
            // add the score
            scoreMap.put(ref, num);
        }
    }

    /**
     * Remove all the scores assigned to the input search engine.
     *
     * @param se    search engine
     */
    public void removeScore(SearchEngineType se) {
        scores.remove(se);
    }

    /**
     * Remove peptide score with specified search engine and cv term reference
     * @param se    search engine
     * @param ref   cv term reference
     */
    public void removeScore(SearchEngineType se, CvTermReference ref) {
        Map<CvTermReference, Number> scoreMap = getScores(se);
        if (scoreMap != null) {
            scoreMap.remove(ref);
        }
    }
}
