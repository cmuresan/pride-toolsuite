package uk.ac.ebi.pride.pia.modeller.psm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.pia.intermediate.Accession;
import uk.ac.ebi.pride.pia.intermediate.Modification;
import uk.ac.ebi.pride.pia.intermediate.Peptide;
import uk.ac.ebi.pride.pia.intermediate.PeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.modeller.IdentificationKeySettings;
import uk.ac.ebi.pride.pia.modeller.score.FDRData;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModel;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModelEnum;


public class ReportPSMSet implements PSMReportItem {

    /** the {@link uk.ac.ebi.pride.pia.modeller.IdentificationKeySettings} used for creating this set */
	private Map<String, Boolean> psmSetSettings;
	
	/** the PSMs **/
	private List<ReportPSM> PSMs;
	
	/** the Average FDR Score of the set */
	private ScoreModel averageFDRScore;
	
	/** the FDR Score of the set, in the set case the Combined FDR Score */
	private ScoreModel fdrScore;
	
	/** the value of the local FDR for this set */
	private Double fdrValue;
	
	/** the rank of this set, regarding the FDR */
	private Long rank;
	
	/** whether this set contains only decoy PSMs or not*/
	private boolean isDecoy;
	
	/** represents whether the set globally FDR good */
	private boolean isFDRGood;
	
	/** the q-value, only available when FDR is calculated */
	private Double qValue;
	
	/** The maximal set of  {@link uk.ac.ebi.pride.pia.modeller.IdentificationKeySettings} which are available on this PSM */
	private Map<String, Boolean> maximalSpectraIdentificationSettings;
	
	/** the maximal set of spectra, which are not redundant (i.e. spectrumTitle and m/z are redundant, if sourceID is given) */
	private Map<String, Boolean> maximalNotRedundantSpectraIdentificationSettings;
	
	/** the nice spectrum name, set and updated, when a new PSM is added */
	private String niceSpectrumName;
	
	/** stores the seqeunce of the PSMs, set on first call for it */
	private String sequence;
	
	/** whether the modificationsString should be rebuild on the next call */
	private boolean rebuildModificationsString;
	
	/** stores the modificationsString */
	private String modificationsString;
	
	/** the peptide identification String, set on the first call for it. this should not change afterwards, as the modifications in a PSM set should be consistent */
	private String peptideStringID;
	
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(ReportPSMSet.class);
	
	
	/**
	 * Basic constructor
	 */
	public ReportPSMSet(Map<String, Boolean> psmSetSettings) {
		this.psmSetSettings = new HashMap<String, Boolean>();
		for (Map.Entry<String, Boolean> setting : psmSetSettings.entrySet()) {
			if (setting.getValue()) {
				this.psmSetSettings.put(setting.getKey(), true);
			}
		}
				
		this.averageFDRScore = null;
		this.fdrScore = null;
		this.fdrValue = 0.0;
		this.rank = 0L;
		this.isDecoy = false;
		this.isFDRGood = true;
		this.qValue = null;
		this.PSMs = new ArrayList<ReportPSM>();
		this.sequence = null;
		this.rebuildModificationsString = true;
		this.modificationsString = null;
		this.peptideStringID = null;
		
		// initialise the map with maximal possible values
		maximalSpectraIdentificationSettings = new HashMap<String, Boolean>(5);
		maximalSpectraIdentificationSettings.put(
				IdentificationKeySettings.MASSTOCHARGE.name(), true);
		maximalSpectraIdentificationSettings.put(
				IdentificationKeySettings.RETENTION_TIME.name(), true);
		maximalSpectraIdentificationSettings.put(
				IdentificationKeySettings.SOURCE_ID.name(), true);
		maximalSpectraIdentificationSettings.put(
				IdentificationKeySettings.SPECTRUM_TITLE.name(), true);
		maximalSpectraIdentificationSettings.put(
				IdentificationKeySettings.CHARGE.name(), true);
		
		maximalNotRedundantSpectraIdentificationSettings =
				IdentificationKeySettings.noRedundantSettings(maximalSpectraIdentificationSettings);
		
		this.niceSpectrumName = null;
	}
	
	
	/**
	 * Basic constructor, initializes the given PSMs
	 * 
	 * @param psms
	 */
	public ReportPSMSet(List<ReportPSM> psms,
			Map<String, Boolean> psmSetSettings) {
		this(psmSetSettings);
		for (ReportPSM psm : psms) {
			addReportPSM(psm);
		}
	}
	
	
	/** 
	 * Returns the String which represents this PSM Set. Actually, it calls
	 * {@link ReportPSM#getIdentificationKey(Map)} for a PSM of this set.
	 */
	@Override
	public String getIdentificationKey(Map<String, Boolean> psmSetSettings) {
		if (PSMs.size() > 0) {
			return PSMs.get(0).getIdentificationKey(psmSetSettings);
		} else {
			return null;
		}
	}
	
	
	@Override
	public String getPeptideStringID(boolean considerModifications) {
		if ((peptideStringID == null) && (PSMs.size() > 0)) {
			// build the string on the first call
			peptideStringID = PSMs.get(0).getPeptideStringID(true);
		}
		
		if (considerModifications) {
			return peptideStringID;
		} else {
			return getSequence();
		}
	}
	
	
	/**
	 * Adds the given PSM to the List of PSMs, if no PSM with the same ID is in
	 * the list yet.
	 * 
	 * @param psm
	 */
	public void addReportPSM(ReportPSM psm) {
		for (ReportPSM psmIt : PSMs) {
			if (psm.getId().equals(psmIt.getId())) {
				// TODO maybe return or throw some warning, if the ID is already here
				logger.error("psm with " + psm.getId() + 
						" already in the PSM of this set");
				return;
			}
		}
		
		
		if (niceSpectrumName == null) {
			niceSpectrumName = psm.getNiceSpectrumName();
		} else if (psm.getNiceSpectrumName().length() > niceSpectrumName.length()) {
			niceSpectrumName = psm.getNiceSpectrumName();
		}
		
		String priorKey = null;
		if (PSMs.size() > 0) {
			priorKey = PSMs.get(0).getIdentificationKey(psmSetSettings);
		}
		
		PSMs.add(psm);
		if ((priorKey != null) && 
				!psm.getIdentificationKey(psmSetSettings).equals(priorKey)) {
			logger.error("PSM for PSM Set has not the Set's idKey!");
		}
		
		// adjust the maximalSpectraIdentificationSettings
		Set<String> setAvailables =
				new HashSet<String>(maximalSpectraIdentificationSettings.keySet());
		Map<String, Boolean> psmAvailables =
				psm.getAvailableIdentificationKeySettings();
		for (String setting : setAvailables) {
			if (!psmAvailables.containsKey(setting) ||
					!psmAvailables.get(setting) ) {
				maximalSpectraIdentificationSettings.remove(setting);
			}
		}
		
		maximalNotRedundantSpectraIdentificationSettings =
				IdentificationKeySettings.noRedundantSettings(maximalSpectraIdentificationSettings);
		
		rebuildModificationsString = true;
	}
	
	
	/**
	 * Calculate the AFS, given the FDR scores of the PSMs.
	 */
	public void calculateAverageFDRScore() {
		double afsValue = 1.0;
		int nrFDRScores = 0;
		
		for (ReportPSM psm : PSMs) {
			if ((psm.getFDRScore() != null) && !psm.getFDRScore().getValue().equals(Double.NaN)) {
				afsValue *= psm.getFDRScore().getValue();
				nrFDRScores++;
			}
		}
		
		if (nrFDRScores > 0) {
			afsValue = Math.pow(afsValue, 1.0 / nrFDRScores);
		} else {
			// this set has no FDR Score (probably because it has no used top ranking identification)
			afsValue = Double.NaN;
		}
		
		averageFDRScore = new ScoreModel(afsValue,
				ScoreModelEnum.AVERAGE_FDR_SCORE);
	}
	
	
	/**
	 * getter for the AverageFDRScore
	 * @return
	 */
	public ScoreModel getAverageFDRScore() {
		return averageFDRScore;
	}
	
	
	/**
	 * Getter for the PSMs
	 * @return
	 */
	public List<ReportPSM> getPSMs() {
		return PSMs;
	}
	
	
	/**
	 * Getter for the modifications.
	 * 
	 * @return
	 */
	public Map<Integer, Modification> getModifications() {
		TreeMap<Integer, Modification> modifications =
				new TreeMap<Integer, Modification>();
		
		for (ReportPSM psm : PSMs) {
			for (Map.Entry<Integer, Modification> modIt : psm.getModifications().entrySet()) {
				if (modifications.get(modIt.getKey()) != null) {
					Modification mod = modifications.get(modIt.getKey());
					
					if (((mod.getDescription() == null) ||
							mod.getDescription().trim().equals("")) && 
							((modIt.getValue().getDescription() != null) &&
									!modIt.getValue().getDescription().trim().equals(""))) {
						// either no description or empty in map so far and we
						// have one here -> overwrite the object in the map
						modifications.put(modIt.getKey(),
								new Modification(
										mod.getResidue(),
										mod.getMass(),
										modIt.getValue().getDescription(),
										mod.getAccession()
										));
					}
				} else {
					modifications.put(modIt.getKey(), modIt.getValue());
				}
			}
		}
		
		return modifications;
	}
	
	
	@Override
	public String getModificationsString() {
		if (rebuildModificationsString || (modificationsString == null)) {
			modificationsString =
					PeptideSpectrumMatch.getModificationString(getModifications());
		}
		
		return modificationsString;
	}
	
	
	/**
	 * Getter for the sequence.<br />
	 * For now, simply take the sequence of the first PSM, as they should all be
	 * the same.
	 * 
	 * @return
	 */
	@Override
	public String getSequence() {
		if ((sequence == null) && (PSMs.size() > 0)) {
			sequence = PSMs.get(0).getSequence();
		}
		
		return sequence;
	}
	
	
	/**
	 * Getter for the missed cleavages.<br />
	 * For now, simply take the first PSM, which has a valid missed cleavages
	 * value, i.e. > -1.
	 * 
	 * @return the number of missed cleavages or -1, if non is given
	 */
	@Override
	public int getMissedCleavages() {
		if (PSMs.size() > 0) {
			for (ReportPSM psm : PSMs) {
				if (psm.getMissedCleavages() > -1) {
					return psm.getMissedCleavages();
				}
			}
		}
		return -1;
	}
	
	
	/**
	 * Getter for the charge.<br />
	 * For now, simply take the charge of the first PSM, as they should all be
	 * the same.
	 * 
	 * @return
	 */
	@Override
	public int getCharge() {
		if (PSMs.size() > 0) {
			return PSMs.get(0).getCharge();
		} else {
			return 0;
		}
	}
	
	
	/**
	 * Getter for the mass to charge.<br />
	 * For now, simply take the average mass to charge of the PSMs, as they
	 * should all be almost the same.
	 * 
	 * @return
	 */
	@Override
	public double getMassToCharge() {
		double mz = 0;
		for (ReportPSM psm : PSMs) {
			mz += psm.getMassToCharge();
		}
		
		return mz / PSMs.size();
	}
	
	
	/**
	 * Getter for the delta mass.<br />
	 * For now, simply take the average deltamass of the PSMs, as they should
	 * all be almost the same.
	 * 
	 * @return
	 */
	@Override
	public double getDeltaMass() {
		double deltamass = 0;
		int dmCount = 0;
		for (ReportPSM psm : PSMs) {
			if (!Double.isNaN(psm.getDeltaMass())) {
				deltamass += psm.getDeltaMass();
				dmCount++;
			}
		}
		
		if (dmCount > 0) {
			return deltamass / dmCount;
		} else {
			return Double.NaN;
		}
	}
	
	
	/**
	 * Getter for the retention time.<br />
	 * For now, simply take the average available retention times of the PSMs,
	 * as they should all be almost the same.
	 * 
	 * @return
	 */
	@Override
	public Double getRetentionTime() {
		int rtCount = 0;
		double rt = 0;
		
		for (ReportPSM psm : PSMs) {
			Double psmRT = psm.getRetentionTime();
			if ((psmRT != null) && !psmRT.equals(Double.NaN)) {
				rt += psmRT;
				rtCount++;
			}
		}
		
		if (rtCount > 0) {
			return rt / rtCount;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Getter for the delta mass given in PPM.<br />
	 * For now, simply take the average PPM delta mass of the PSMs, as they
	 * should all be the almost same.
	 * 
	 * @return
	 */
	@Override
	public double getDeltaPPM() {
		double ppm = 0;
		int ppmCount = 0;
		for (ReportPSM psm : PSMs) {
			if (!Double.isNaN(psm.getDeltaPPM())) {
				ppm += psm.getDeltaPPM();
				ppmCount++;
			}
		}
		
		if (ppmCount > 0) {
			return ppm / PSMs.size();
		} else {
			return Double.NaN;
		}
	}
	
	
	@Override
	public String getSourceID() {
		// For now, simply take the ID of the first PSM providing it
		for (ReportPSM psm : PSMs) {
			if (psm.getSourceID() != null) {
				return psm.getSourceID();
			}
		}
		
		return null;
	}
	
	
	@Override
	public String getSpectrumTitle() {
		// For now, simply take the title of the first PSM providing it
		for (ReportPSM psm : PSMs) {
			if (psm.getSpectrumTitle() != null) {
				return psm.getSpectrumTitle();
			}
		}
		
		return null;
	}
	
	
	@Override
	public ScoreModel getCompareScore(String scoreShortName) {
		return getCompareScore(scoreShortName, null, null);
	}
	
	/**
	 * Returns the score, with which the comparison will be performed, and using
	 * only ReportPSMs whose IDs are not in the nonScoringPSMs and whise spectra
	 * are not in the nonScoringSpectra.
	 */
	public ScoreModel getCompareScore(String scoreShortName,
			Set<Long> nonScoringPSMs, Set<String> nonScoringSpectra) {
		if (nonScoringPSMs == null) {
			nonScoringPSMs = new HashSet<Long>(0);
		}
		if (nonScoringSpectra == null) {
			nonScoringSpectra = new HashSet<String>(0);
		}
		
		if ((averageFDRScore != null) &&
				averageFDRScore.getType().isValidDescriptor(scoreShortName)) {
			if (!anyPSMinSet(nonScoringPSMs) &&
					!anySpectrumInSet(nonScoringSpectra)) {
				return averageFDRScore;
			} else {
				// the averageFDRScore is invalid, if any PSM is not scoring
				return null;
			}
		} else if ((fdrScore != null) &&
				(fdrScore.getType().isValidDescriptor(scoreShortName) ||
						ScoreModelEnum.PSM_LEVEL_COMBINED_FDR_SCORE.isValidDescriptor(scoreShortName))) {
			if (!anyPSMinSet(nonScoringPSMs) &&
					!anySpectrumInSet(nonScoringSpectra)) {
				return fdrScore;
			} else {
				// the Combined FDR Score is invalid, if any PSM is not scoring
				return null;
			}
		} else {
			return getBestScoreModel(scoreShortName, nonScoringPSMs,
					nonScoringSpectra);
		}
	}
	
	
	/**
	 * Returns true, if any of the set's PSMs' IDs is in the given set of IDs
	 */
	private boolean anyPSMinSet(Set<Long> nonScoringPSMs) {
		for (ReportPSM psm : PSMs) {
			if (nonScoringPSMs.contains(psm.getId())) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Returns true, if any of the set's spectra ID keys is in the given set of
	 * ID keys.
	 */
	private boolean anySpectrumInSet(Set<String> nonScoringPSMs) {
		for (ReportPSM psm : PSMs) {
			if (nonScoringPSMs.contains(
					psm.getSpectrum().getSpectrumIdentificationKey(
							getAvailableIdentificationKeySettings()))) {
				return true;
			}
		}
		
		return false;
	}
	
	
	@Override
	public Double getScore(String scoreShortName) {
		if (isPSMSetScore(scoreShortName)) {
			if ((averageFDRScore != null) &&
					ScoreModelEnum.AVERAGE_FDR_SCORE.isValidDescriptor(scoreShortName)) {
				return averageFDRScore.getValue();
			} else if ((fdrScore != null) &&
					ScoreModelEnum.PSM_LEVEL_COMBINED_FDR_SCORE.isValidDescriptor(scoreShortName)) {
				return fdrScore.getValue();
			}
		}
		
		return Double.NaN;
	}
	
	
	/**
	 * Returns, whether the score given by the score name is a PSM set score
	 * and can be directly processed by this class, or another score and may be
	 * passed down to the PSMs.
	 * @return
	 */
	public static boolean isPSMSetScore(String scoreShortName) {
		if (ScoreModelEnum.AVERAGE_FDR_SCORE.isValidDescriptor(scoreShortName) ||
					ScoreModelEnum.PSM_LEVEL_COMBINED_FDR_SCORE.isValidDescriptor(scoreShortName)) {
			return true;
		}
		return false;
	}
	
	
	@Override
	public String getScoresString() {
		StringBuilder scoresSB = new StringBuilder();
		
		if (averageFDRScore != null) {
			scoresSB.append(averageFDRScore.getName());
			scoresSB.append(":");
			scoresSB.append(averageFDRScore.getValue());
		}
		
		if (fdrScore != null) {
			if (scoresSB.length() > 0) {
				scoresSB.append(",");
			}
			scoresSB.append(ScoreModelEnum.PSM_LEVEL_COMBINED_FDR_SCORE.getName());
			scoresSB.append(":");
			scoresSB.append(fdrScore.getValue());
		}
		
		return scoresSB.toString();
	}
	
	
	/**
	 * Gets the best score of the PSMs in this ReportPSMSet for the given
	 * scoreShortName.<br/>
	 * This method is only for PSMReport ScoreModels, not for e.g. the
	 * "Combined FDR Score".
	 * 
	 * @param scoreShortName
	 * @return
	 */
	public double getBestScore(String scoreShortName) {
		ScoreModel bestScoreModel = getBestScoreModel(scoreShortName);
		
		return (bestScoreModel == null) ?
				Double.NaN :
				bestScoreModel.getValue();
	}
	
	
	/**
	 * Gets the ScoreModel with the best score value of the PSMs in this
	 * ReportPSMSet for the given scoreShortName.<br/>
	 * This method is only for PSMReport ScoreModels, not for e.g. the
	 * "Combined FDR Score".
	 * 
	 * @param scoreShortName
	 * @return
	 */
	public ScoreModel getBestScoreModel(String scoreShortName) {
		return getBestScoreModel(scoreShortName, null, null);
	}
	
	
	/**
	 * Gets the ScoreModel with the best score value of the PSMs in this
	 * ReportPSMSet for the given scoreShortName and using
	 * only ReportPSMs whose IDs are not in the nonScoringPSMs and whose spectra
	 * are not in the nonScoringSpectra.<br/>
	 * This method is only for PSMReport ScoreModels, not for e.g. the
	 * "Combined FDR Score".
	 * 
	 * @param scoreShortName
	 * @param nonScoringPSMs
	 * @return
	 */
	public ScoreModel getBestScoreModel(String scoreShortName,
			Set<Long> nonScoringPSMs, Set<String> nonScoringSpectra) {
		if (nonScoringPSMs == null) {
			nonScoringPSMs = new HashSet<Long>(0);
		}
		if (nonScoringSpectra == null) {
			nonScoringSpectra = new HashSet<String>(0);
		}
		
		ScoreModel bestScoreModel = null;
		for (ReportPSM psm : PSMs) {
			if (!nonScoringPSMs.contains(psm.getId()) &&
					!nonScoringSpectra.contains(
							psm.getSpectrum().getSpectrumIdentificationKey(
									getAvailableIdentificationKeySettings()))) {
				ScoreModel newScoreModel = psm.getCompareScore(scoreShortName);
				
				if ((newScoreModel != null) && 
						((bestScoreModel == null) ||
							(newScoreModel.compareTo(bestScoreModel) < 0))) {
						bestScoreModel = newScoreModel;
				}
			}
		}
		
		return bestScoreModel;
	}
	
	@Override
	public double getFDR() {
		if (fdrValue == null) {
			return Double.NaN;
		} else {
			return fdrValue;
		}
	}


	@Override
	public void setFDR(double fdr) {
		this.fdrValue = fdr;
	}


	/**
	 * Getter for the rank.
	 * @return
	 */
	@Override
	public Long getRank() {
		return rank;
	}
	
	
	@Override
	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	
	@Override
	public void dumpFDRCalculation() {
		isFDRGood = false;
		qValue = null;
		fdrScore = null;
		fdrValue = Double.POSITIVE_INFINITY;
	}
	
	
	@Override
	public void updateDecoyStatus(FDRData.DecoyStrategy strategy, Pattern p) {
		isDecoy = true;
		
		for (ReportPSM psm : PSMs) {
			isDecoy &= psm.getIsDecoy();
		}
	}
	
	
	public void setIsDecoy(boolean decoy) {
		isDecoy = decoy;
	}
	
	
	@Override
	public boolean getIsDecoy() {
		return isDecoy;
	}
	
	
	/**
	 * Getter for isFDRGood
	 * @return
	 */
	public boolean getIsFDRGood() {
		return isFDRGood;
	}
	
	
	@Override
	public void setIsFDRGood(boolean isGood) {
		isFDRGood = isGood;
	}


	@Override
	public double getQValue() {
		if (qValue == null) {
			return Double.NaN;
		} else {
			return qValue;
		}
	}


	@Override
	public void setQValue(double value) {
		qValue = value;
	}
	
	
	@Override
	public ScoreModel getFDRScore() {
		return fdrScore;
	}
	
	
	@Override
	public void setFDRScore(Double score) {
		if (fdrScore != null) {
			fdrScore.setValue(score);
		} else {
			fdrScore = new ScoreModel(score, ScoreModelEnum.PSM_LEVEL_COMBINED_FDR_SCORE);
		}
	}
	
	
	@Override
	public List<Accession> getAccessions() {
		Set<Accession> accList = new HashSet<Accession>();
		
		for (ReportPSM psm : PSMs) {
			accList.addAll(psm.getAccessions());
		}
		
		return new ArrayList<Accession>(accList);
	}


	@Override
	public Map<String, Boolean> getAvailableIdentificationKeySettings() {
		return maximalSpectraIdentificationSettings;	
	}


	@Override
	public Map<String, Boolean> getNotRedundantIdentificationKeySettings() {
		return maximalNotRedundantSpectraIdentificationSettings;	
	}


	@Override
	public String getNiceSpectrumName() {
		return niceSpectrumName;
	}
	
	
	@Override
	public Peptide getPeptide() {
		if (PSMs.size() > 0) {
			return PSMs.get(0).getPeptide();
		} else {
			return null;
		}
	}
	
	
	/**
	 * Copies the information over from the otherSet. Almost all information is
	 * taken, except the actual PSM list.
	 * 
	 * @param otherSet
	 */
	public void copyInfo(ReportPSMSet otherSet) {
		psmSetSettings = new HashMap<String, Boolean>(otherSet.psmSetSettings);
		
		if (otherSet.averageFDRScore != null) {
			averageFDRScore = new ScoreModel(otherSet.getAverageFDRScore().getValue(),
					otherSet.getAverageFDRScore().getType());
		} else {
			averageFDRScore = null;
		}
		
		if (otherSet.getFDRScore() != null) {
			fdrScore = new ScoreModel(otherSet.getFDRScore().getValue(),
					otherSet.getFDRScore().getType());
		} else {
			fdrScore = null;
		}
		
		fdrValue = otherSet.getFDR();
		rank = otherSet.getRank();
		isDecoy = otherSet.getIsDecoy();
		isFDRGood = otherSet.getIsFDRGood();
		qValue = otherSet.getQValue();
		maximalSpectraIdentificationSettings =
				new HashMap<String, Boolean>(otherSet.maximalSpectraIdentificationSettings);
		maximalNotRedundantSpectraIdentificationSettings =
				new HashMap<String, Boolean>(otherSet.maximalNotRedundantSpectraIdentificationSettings);
		niceSpectrumName = otherSet.getNiceSpectrumName();
		sequence = otherSet.getSequence();
		rebuildModificationsString = true;
		modificationsString = null;	// this gets rebuild on next call
		peptideStringID = null;		// this gets rebuild on next call
	}
}