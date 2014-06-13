package uk.ac.ebi.pride.pia.modeller.protein.scores;

/**
 * Enumeration of known CV terms, for faster access
 * 
 * @author julian
 *
 */
public enum CvScore{
	
	PRIDE_OMSSA_E_VALUE("PRIDE", "PRIDE:0000185", "OMSSA E-value", false),
	PRIDE_OMSSA_P_VALUE("PRIDE", "PRIDE:0000186", "OMSSA P-value", false),
	PSI_OMSSA_E_VALUE("MS", "MS:1001328", "OMSSA:evalue", false),
	PSI_OMSSA_P_VALUE("MS", "MS:1001329", "OMSSA:pvalue", false),
	
	PRIDE_MASCOT_SCORE("PRIDE", "PRIDE:0000069", "Mascot Score", true),
	PRIDE_MASCOT_EXPECT_VALUE("PRIDE", "PRIDE:0000212", "Mascot expect value", false),
	PSI_MASCOT_SCORE("MS", "MS:1001171", "Mascot:score", true),
	PSI_MASCOT_EXPECT_VALUE("MS", "MS:1001172", "Mascot:expectation value", false),
	
	PRIDE_XTANDEM_HYPER_SCORE("PRIDE", "PRIDE:0000176", "X!Tandem Hyperscore", true),
	PRIDE_XTANDEM_EXPECTANCY_SCORE("PRIDE", "PRIDE:0000183", "X|Tandem expectancy score", false),
	
	PSI_XTANDEM_HYPERSCORE("MS", "MS:1001331", "X!Tandem:hyperscore", true),
	PSI_XTANDEM_EXPECTANCY_SCORE("MS", "MS:1001330", "X!Tandem:expect", false),
	
	PRIDE_SEQUEST_CN("PRIDE", "PRIDE:0000052", "Cn", true),
	PRIDE_SEQUEST_SCORE("PRIDE", "PRIDE:0000053", "SEQUEST SCORE", true),
	PRIDE_SEQUEST_DELTA_CN("PRIDE", "PRIDE:0000012", "Delta Cn", true),
	PSI_SEQUEST_CONSENSUS_SCORE("MS", "MS:1001163", "Sequest:consensus score", true),
	PSI_SEQUEST_DELTA_CN("MS", "MS:1001156", "Sequest:deltacn", true),
	PSI_SEQUEST_XCORR("MS", "MS:1001155", "Sequest:xcorr", true),
	
	PRIDE_PEPTIDE_PROPHET_DISCRIMINANT_SCORE("PRIDE", "PRIDE:0000138", "Discriminant score", true),
	PRIDE_PEPTIDE_PROPHET_PROBABILITY("PRIDE", "PRIDE:0000099", "PeptideProphet probability score", false),
	
	PSI_MYRIMATCH_MVH("MS", "MS:1001589", "MyriMatch:MVH", true),
	PSI_MYRIMATCH_NMATCHS("MS", "MS:1001121", "number of matched peaks", true),
	PSI_MYRIMATCH_NOMATCHS("MS", "MS:1001362", "number of unmatched peaks", false),
	PSI_MYRIMATCH_MZFIDELITY("MS", "MS:1001590", "MyriMatch:mzFidelity", true),
	
	//MS-GF
	PSI_MSGF_RAWSCORE("MS", "MS:1002049", "MS-GF raw score", true),
	PSI_MSGF_DENOVOSCORE("MS", "MS:1002050", "MS-GF de novo score", true),
	PSI_MSGF_SPECEVALUE("MS", "MS:1002052", "MS-GF spectral E-value", false),
	PSI_MSGF_EVALUE("MS", "MS:1002053", "MS-GF E-value", false),
	PSI_MSGF_QVALUE("MS", "MS:1002054", "MS-GF Q-value", false),
	PSI_MSGF_PEPQVALUE("MS", "MS:1002055", "MS-GF peptide-level Q-value", false),
	
	// Paragon
	//PSI_PARAGON_SCORE("MS", "MS:1001166", "Paragon:score", "MS:1001153"),
	
	// Phenyx
	PSI_PHENYX_SCORE("MS", "MS:1001390", "Phenyx:Score", true),
	
	// ProteinScape
	PSI_PROTEIN_EXTRACTOR_SCORE("MS", "MS:1001507", "ProteinExtractor:Score", true),
	PSI_PROTEINSCAPE_SEQUEST_METASCORE("MS", "MS:1001506", "ProteinScape:SequestMetaScore", true),
	
	// ProteinLynx
	//PSI_PROTEIN_LYNC_SCORE("MS", "MS:1001571", "ProteinLynx:Ladder Score", "MS:1001143"),
	
	// Sonar
	//PSI_SONAR_SCORE("MS", "MS:1001502", "Sonar:Score", "MS:1001143"),
	
	// percolator:score
	//PSI_PERCULATOR_SCORE("MS", "MS:1001492", "percolator:score", "MS:1001143"),
	;
	
	
	private final String cvLabel;
	private final String accession;
	private final String name;
	private final boolean higherScoreBetter;
	
	private CvScore(String cvLabel, String accession, String name, boolean higherScoreBetter) {
		this.cvLabel = cvLabel;
		this.accession = accession;
		this.name = name;
		this.higherScoreBetter = higherScoreBetter;
	}
	
	
	public String getCvLabel() {
	    return cvLabel;
	}
	
	
	public String getAccession() {
	    return accession;
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public boolean getHigherScoreBetter() {
		return higherScoreBetter;
	}
	
	
	/**
	 * Get Cv score by accession.
	 * @return the Cv score with the accession or null
	 */
	public static CvScore getCvRefByAccession(String accession) {
		for (CvScore cv : CvScore.values()) {
			if (cv.getAccession().equals(accession)) {
				return cv;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Check whether the accession exists in the enum.
	 *
	 * @return boolean  true if exists
	 */
	public static boolean hasAccession(String accession) {
		for (CvScore cv : CvScore.values()) {
			if (cv.getAccession().equals(accession)) {
				return true;
			}
		}
		
		return false;
	}
}
