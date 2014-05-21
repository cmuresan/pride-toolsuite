package uk.ac.ebi.pride.pia.modeller.score.comparator;

import uk.ac.ebi.pride.pia.modeller.score.ScoreModel;

public interface ScoreComparable {
	
	/**
	 * returns the score, with which the comparison will be performed.
	 * @param scoreShortname
	 * @return
	 */
	public ScoreModel getCompareScore(String scoreShortname);
}
