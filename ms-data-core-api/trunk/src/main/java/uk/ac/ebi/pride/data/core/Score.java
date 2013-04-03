package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.data.utils.MapUtils;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;

import java.util.*;

/**
 * Score stores a number of peptide scores for a list of search engines.
 * Please Note that peptide scores are null if they are not provided.
 * <p/>
 * User: rwang, yperez
 * Date: Dec 2, 2010
 * Time: 9:02:05 AM
 */
public class Score {

    private Map<SearchEngineType, Map<CvTermReference, Number>> scores;

    public Score() {
        scores = new HashMap<SearchEngineType, Map<CvTermReference, Number>>();
    }

    public Score(Map<SearchEngineType, Map<CvTermReference, Number>> scores) {
        this.scores = MapUtils.createMapFromMap(scores);
    }

    /**
     * Get peptide scores for search engine
     *
     * @param se search engine
     * @return   peptide score map
     */
    public Map<CvTermReference, Number> getPeptideScores(SearchEngineType se) {
        return scores.get(se);
    }

    /**
     * Get peptide score for a specified search engine and cv term reference
     *
     * @param se  search engine
     * @param ref cv term reference
     * @return Number  peptide score
     */
    public Number getPeptideScore(SearchEngineType se, CvTermReference ref) {
        Map<CvTermReference, Number> scoreMap = scores.get(se);

        return (scoreMap == null) ? null  : scoreMap.get(ref);
    }

    /**
     * Get all peptide scores, this will produce a sequential order list
     *
     * @return List<Number>    a list of scores
     */
    public List<Number> getAllScoreValues() {
        List<Number> scoreList = new ArrayList<Number>();

        for (Map<CvTermReference, Number> numberMap : scores.values()) {
            scoreList.addAll(numberMap.values());
        }

        return scoreList;
    }

    /**
     * Get all the search engine types within this peptide
     *
     * @return List<SearchEngineType>  a list of search engine types
     */
    public List<SearchEngineType> getSearchEngineTypes() {
        return new ArrayList<SearchEngineType>(scores.keySet());
    }

    /**
     * Add a new peptide score
     *
     * @param se  search engine
     * @param ref cv term reference for the score type
     * @param num peptide score
     */
    public void addScore(SearchEngineType se, CvTermReference ref, Number num) {

        // create a new if the search engine doesn't exist
        Map<CvTermReference, Number> scoreMap = scores.get(se);

        if (scoreMap == null) {
            scoreMap = new LinkedHashMap<CvTermReference, Number>();
            scores.put(se, scoreMap);

            // for each cv term
            List<CvTermReference> cvTerms = se.getSearchEngineScores();

            for (CvTermReference cvTerm : cvTerms) {
                scoreMap.put(cvTerm, null);
            }
        }

        // add the score
        scoreMap.put(ref, num);
    }

    /**
     * Remove all the scores assigned to the input search engine.
     *
     * @param se search engine
     */
    public void removeScore(SearchEngineType se) {
        scores.remove(se);
    }

    /**
     * Remove peptide score with specified search engine and cv term reference
     *
     * @param se  search engine
     * @param ref cv term reference
     */
    public void removeScore(SearchEngineType se, CvTermReference ref) {
        Map<CvTermReference, Number> scoreMap = scores.get(se);

        if (scoreMap != null) {
            scoreMap.remove(ref);
        }
    }

    /**
     * Get the Default score for Search Engine
     *
     * * @return score
     */
    public double getDefaultScore(){
        Object[] scoresArray = scores.values().toArray();
        Object[] scoresArrayValue = ((Map<CvTermReference, Number>) scoresArray[0]).values().toArray();
        double scoreValue = -1;
        for (Object aScoresArrayValue : scoresArrayValue) {
            if (aScoresArrayValue != null) {
                scoreValue = ((Double) aScoresArrayValue);
                break;
            }
        }
        return scoreValue;
    }

    /**
     * Get Default Search Engine for Scores.
     *
     * @return Default Search Engine for the Scores
     */
    public SearchEngineType getDefaultSearchEngine(){
        Object[] searchengines = scores.keySet().toArray();
        return (SearchEngineType) searchengines[0];
    }

    public List<CvTermReference> getCvTermReferenceWithValues(){
        List<CvTermReference> listReference = new ArrayList<CvTermReference>();
        for (Map<CvTermReference, Number> numberMap : scores.values()) {
            for (CvTermReference cvTermReference: numberMap.keySet()){
                if(numberMap.get(cvTermReference) !=null){
                    listReference.add(cvTermReference);
                }
            }
        }
        return listReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Score)) return false;

        Score score = (Score) o;

        if (!scores.equals(score.scores)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return scores.hashCode();
    }
}



