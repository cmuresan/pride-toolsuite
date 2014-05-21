package uk.ac.ebi.pride.pia.modeller.protein.scoring;

import uk.ac.ebi.pride.pia.modeller.protein.ReportProtein;
import uk.ac.ebi.pride.pia.modeller.protein.scoring.settings.PSMForScoring;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModel;

import java.util.List;
import java.util.Map;


public class MultiplicativeScoring extends AbstractScoring {
	
	/** the human readable score */
	protected static String name = "Multiplicative Scoring";
	
	/** the machine readable score */
	protected static String shortName = "scoring_multiplicative";
	
	
	public MultiplicativeScoring(Map<String, String> scoreNameMap) {
		super(scoreNameMap);
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public String getShortName() {
		return shortName;
	}
	
	
	@Override
	public Boolean higherScoreBetter() {
		// TODO: make this changeable?
		return false;
	}
	
	
	@Override
	public Double calculateProteinScore(ReportProtein protein) {
		List<ScoreModel> scores = PSMForScoring.getProteinsScores(
                getPSMForScoringSetting().getValue(), protein,
                getScoreSetting().getValue());
		
		if (scores.size() < 1) {
			// no scores found -> no scoring possible
			return Double.NaN;
		}
		Double proteinScore = Double.NaN;
		
		for (ScoreModel score : scores) {
			if ((score != null) && !score.getValue().equals(Double.NaN)) {
				if (!proteinScore.equals(Double.NaN)) {
					proteinScore += Math.log10(score.getValue());
				} else {
					proteinScore =  Math.log10(score.getValue());
				}
			}
		}
		
		return proteinScore;
	}
}
