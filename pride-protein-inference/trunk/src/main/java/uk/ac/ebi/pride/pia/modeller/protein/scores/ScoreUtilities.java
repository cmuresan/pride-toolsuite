package uk.ac.ebi.pride.pia.modeller.protein.scores;

public class ScoreUtilities {
	/**
	 * Checks the PSI-MS obo and returns the name of the accession.
	 * 
	 * @param scoreAccession
	 * @return name of the accession or null, if not found
	 */
	public static String findAccessionInObo(String scoreAccession) {
		// TODO: implement!
		return null;
	}
	
	
	/**
	 * Returns for a given score accession, whether a higher score is better.
	 * <p>
	 * If the score is not known and oboLookup is true, the score is searched
	 * in the PSI-MS obo.
	 * <p>
	 * If nothing about the score is found, true will be returned.
	 * 
	 * @param scoreAccession
	 */
	public static boolean isHigherScoreBetter(String scoreAccession, boolean oboLookup) {
		
		CvScore cvScore = CvScore.getCvRefByAccession(scoreAccession);
		if (cvScore != null) {
			return cvScore.getHigherScoreBetter();
		}
		
		if (oboLookup) {
			// TODO: implement
		}
		
		return false;
	}
	
}
