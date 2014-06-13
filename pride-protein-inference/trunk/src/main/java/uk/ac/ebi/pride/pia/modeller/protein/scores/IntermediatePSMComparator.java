package uk.ac.ebi.pride.pia.modeller.protein.scores;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;

/**
 * Compares the scores of {@link IntermediatePeptideSpectrumMatch}es
 * 
 * @author julian
 *
 */
public class IntermediatePSMComparator implements Comparator<IntermediatePeptideSpectrumMatch>{
	
	/** the accession of the compared score */
	private String scoreAccession;
	
	private boolean higherScoreBetter;
	
	/** a score lookup map */
	private Map<Comparable, Double> scoreLookup;
	
	
	public IntermediatePSMComparator(String scoreAccession, boolean oboLookup) {
		this.scoreAccession = scoreAccession;
		this.higherScoreBetter = ScoreUtilities.isHigherScoreBetter(scoreAccession, oboLookup);
		this.scoreLookup = new HashMap<Comparable, Double>();
	}
	
	
	@Override
	public int compare(IntermediatePeptideSpectrumMatch o1,
			IntermediatePeptideSpectrumMatch o2) {
		Double score1;
		Double score2;
		
		if (o1 != null) {
			if (scoreLookup.containsKey(o1.getID())) {
				score1 = scoreLookup.get(o1.getID());
			} else {
				score1 = o1.getScore(scoreAccession);
				scoreLookup.put(o1.getID(), score1);
			}
		} else {
			score1 = null;
		}
		
		if (o2 != null) {
			if (scoreLookup.containsKey(o2.getID())) {
				score2 = scoreLookup.get(o2.getID());
			} else {
				score2 = o2.getScore(scoreAccession);
				scoreLookup.put(o2.getID(), score2);
			}
		} else {
			score2 = null;
		}
		
		if ((score1 == null) && 
				(score2 == null)) {
			// both PSMs don't have this score
			return 0;
		} else if ((score1 == null) && 
				(score2 != null)) {
			// psm1 does not have the score
			return 1;
		} else if ((score1 != null) && 
				(score2 == null)) {
			// psm2 does not have the score
			return -1;
		} else {
			// both scores are != null
			if (score1.equals(Double.NaN)) {
				// NaN is always considered worse than anything (like null)
				return 1;
			} else if (score2.equals(Double.NaN)) {
				return -1;
			} else {
				if (higherScoreBetter) {
					return -score1.compareTo(score2);
				} else {
					return score1.compareTo(score2);
				}
			}
		}
	}
}
