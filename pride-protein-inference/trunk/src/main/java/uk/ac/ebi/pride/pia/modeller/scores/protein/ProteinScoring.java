package uk.ac.ebi.pride.pia.modeller.scores.protein;

import uk.ac.ebi.pride.pia.modeller.protein.inference.InferenceProteinGroup;
import uk.ac.ebi.pride.pia.modeller.scores.CvScore;
import uk.ac.ebi.pride.pia.modeller.scores.ScoreUtilities;

public abstract class ProteinScoring {
	
	/** accession of the base score */
	protected String scoreAccession;
	
	/** whether higher value of the base score is better than a lower value */
	protected boolean higherScoreBetter;
	
	
	/**
	 * Creates a new scoring object and determines (if appropriate), if a higher
	 * value is better for the given base-score. 
	 * 
	 * @param scoreAccession
	 * @param oboLookup
	 */
	public ProteinScoring(String scoreAccession, boolean oboLookup) {
		if (!CvScore.hasAccession(scoreAccession)) {
			if (!oboLookup ||
					(ScoreUtilities.findAccessionInObo(scoreAccession) == null)) {
				scoreAccession = null;
			}
		}
		
		this.scoreAccession = scoreAccession;
		higherScoreBetter = ScoreUtilities.isHigherScoreBetter(scoreAccession, oboLookup);
	}
	
	
	/**
	 * Calculates the protein score for the inference group.
	 * 
	 * @param intermediatePeptide
	 */
	public abstract Double calculateProteinScore(InferenceProteinGroup proteinGroup);
}
