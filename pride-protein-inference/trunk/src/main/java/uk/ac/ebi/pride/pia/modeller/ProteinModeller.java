package uk.ac.ebi.pride.pia.modeller;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.core.SearchDataBase;
import uk.ac.ebi.pride.data.core.Software;
import uk.ac.ebi.pride.pia.intermediate.Accession;
import uk.ac.ebi.pride.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.pia.intermediate.Modification;
import uk.ac.ebi.pride.pia.modeller.peptide.ReportPeptide;
import uk.ac.ebi.pride.pia.modeller.protein.ProteinExecuteCommands;
import uk.ac.ebi.pride.pia.modeller.protein.ReportProtein;
import uk.ac.ebi.pride.pia.modeller.protein.ReportProteinComparatorFactory;
import uk.ac.ebi.pride.pia.modeller.protein.inference.AbstractProteinInference;
import uk.ac.ebi.pride.pia.modeller.protein.scoring.AbstractScoring;
import uk.ac.ebi.pride.pia.modeller.psm.PSMReportItem;
import uk.ac.ebi.pride.pia.modeller.psm.ReportPSM;
import uk.ac.ebi.pride.pia.modeller.psm.ReportPSMSet;
import uk.ac.ebi.pride.pia.modeller.report.SortOrder;
import uk.ac.ebi.pride.pia.modeller.report.filter.AbstractFilter;
import uk.ac.ebi.pride.pia.modeller.report.filter.FilterFactory;
import uk.ac.ebi.pride.pia.modeller.score.FDRData;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModel;
import uk.ac.ebi.pride.pia.modeller.score.ScoreModelEnum;
import uk.ac.ebi.pride.pia.modeller.score.comparator.RankCalculator;


/**
 * Modeller for protein related stuff.
 * 
 * @author julian
 *
 */
public class ProteinModeller {
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(ProteinModeller.class);
	
	
	/** maps from the string ID to the {@link SearchDataBase}s, they are straight from the intermediateHandler */
	private Map<String, SearchDataBase> searchDatabases;
	
	/** maps from the string ID to the {@link Software}s, they are straight from the intermediateHandler */
	private Map<String, Software> analysisSoftware;
	
	
	/** List of the report proteins */
	private List<ReportProtein> reportProteins;
	
	/** Map of the report proteins, for easier accession */
	private Map<Long, ReportProtein> reportProteinsMap;
	
	/** the last applied inference filter */
	private AbstractProteinInference appliedProteinInference;
	
	/** the last applied scoring method */
	private AbstractScoring appliedScoringMethod;
	
	
	/** the corresponding {@link PSMModeller} */
	private PSMModeller psmModeller;
	
	/** the corresponding {@link PSMModeller} */
	private PeptideModeller peptideModeller;
	
	/** map of the {@link uk.ac.ebi.pride.pia.intermediate.IntermediateGroup}s in the intermediate structure */
	private Map<Long, IntermediateGroup> intermediateGroups;
	
	
	/** the FDR settings for the protein FDR */
	private FDRData fdrData;
	
	
	/** filters which may be used for a protein inference */
	private List<AbstractFilter> inferenceFilters;
	
	/** the list of filters applied to the protein report */
	private List<AbstractFilter> reportFilters;
	
	
	// TODO: set these defaults in a file
	private static FDRData.DecoyStrategy defaultDecoyStrategy =
			FDRData.DecoyStrategy.ACCESSIONPATTERN;
	private static String defaultDecoyPattern = "s.*";
	private static Double defaultFDRThreshold = 0.05;
	
	
	/**
	 * Basic constructor for the ProteinModeller.<br/>
	 * There will be no inference, but only initialization.
	 * 
	 * @param groups
	 * @param
	 */
	public ProteinModeller(PSMModeller psmModeller,
			PeptideModeller peptideModeller, Map<Long, IntermediateGroup> groups,
			Map<String, SearchDataBase> searchDatabases,
			Map<String, Software> analysisSoftware) {
		if (psmModeller == null) {
			throw new IllegalArgumentException("The given PSMModeller is null!");
		} else {
			this.psmModeller = psmModeller;
			
		}
		
		if (peptideModeller == null) {
			throw new IllegalArgumentException("The given PeptideModeller is null!");
		} else {
			this.peptideModeller = peptideModeller;
		}
		
		if (groups == null) {
			throw new IllegalArgumentException("The given intermediate Groups is null!");
		} else {
			this.intermediateGroups = groups;
		}
		
		this.appliedProteinInference = null;
		this.appliedScoringMethod = null;
		
		this.fdrData = new FDRData(defaultDecoyStrategy, defaultDecoyPattern,
				defaultFDRThreshold);
		
		this.reportFilters = new ArrayList<AbstractFilter>();
		
		this.searchDatabases = searchDatabases;
		this.analysisSoftware = analysisSoftware;
	}
	
	
	/**
	 * Applies the general settings
	 */
	public void applyGeneralSettings() {
		
	}
	
	
	/**
	 * Returns whether modifications were considered while building the
	 * peptides.
	 * 
	 * @return
	 */
	public Boolean getConsiderModifications() {
		return peptideModeller.getConsiderModifications();
	}
	
	
	/**
	 * Returns a List of all the currently available scoreShortNames
	 * @return
	 */
	public List<String> getAllScoreShortNames() {
		List<String> scoreShortNames = new ArrayList<String>();
		
		// get the scores from the files
		for (Long fileID : psmModeller.getFiles().keySet()) {
			for (String scoreShort : psmModeller.getScoreShortNames(fileID)) {
				if (!scoreShortNames.contains(scoreShort)) {
					scoreShortNames.add(scoreShort);
				}
			}
		}
		
		return scoreShortNames;
	}
	
	
	/**
	 * Returns the Score name, given the scoreShortName.
	 * @param shortName
	 * @return
	 */
	public String getScoreName(String shortName) {
		return psmModeller.getScoreName(shortName);
	}
	
	
	/**
	 * Returns the mapping from the shortNames to the nicely readable names.
	 * 
	 * @return
	 */
	public Map<String, String> getScoreShortsToScoreNames() {
		return psmModeller.getScoreShortsToScoreNames();
	}
	
	
	/**
	 * Returns the filtered List of {@link ReportProtein}s or null, if the
	 * proteins are not inferred yet.
	 *  
	 * @param filters
	 * @return
	 */
	public List<ReportProtein> getFilteredReportProteins(
			List<AbstractFilter> filters) {
		if (reportProteins != null) {
			return FilterFactory.applyFilters(reportProteins, filters);
		} else {
			return null;
		}
	}
	
	
	/**
	 * Returns the protein with the given ID.
	 * @param proteinID
	 * @return
	 */
	public ReportProtein getProtein(Long proteinID) {
		return reportProteinsMap.get(proteinID);
	}
	
	
	/**
	 * Calculates the reported proteins with the given settings for the
	 * inference.
	 */
	public void infereProteins(AbstractProteinInference proteinInference) {
		reportProteins = new ArrayList<ReportProtein>();
		
		if (proteinInference != null) {
			appliedProteinInference = proteinInference;
			reportProteins = proteinInference.calculateInference(
					intermediateGroups,
					psmModeller.getReportPSMSets(),
					peptideModeller.getConsiderModifications(),
					psmModeller.getPSMSetSettings());
		} else {
			logger.error("No inference method set!");
			appliedProteinInference = null;
			reportProteins = null;
		}
		
		this.fdrData = new FDRData(defaultDecoyStrategy, defaultDecoyPattern,
				defaultFDRThreshold);
		
		
		// create the protein map
		reportProteinsMap = new HashMap<Long, ReportProtein>();
		for (ReportProtein protein : reportProteins) {
			reportProteinsMap.put(protein.getID(), protein);
			
			if (protein.getSubSets().size() > 0) {
				for (ReportProtein subProtein : protein.getSubSets()) {
					reportProteinsMap.put(subProtein.getID(), subProtein);
				}
			}
		}
	}
	
	
	/**
	 * Returns the last applied inference filter.<br/>
	 * If there was no filter or an error occurred during the inference, null
	 * will be returned.
	 * 
	 * @return
	 */
	public AbstractProteinInference getAppliedProteinInference() {
		return appliedProteinInference;
	}
	
	
	/**
	 * Returns the last applied scoring method.<br/>
	 * If there was no scoring yet or an error occurred during the scoring, null
	 * will be returned.
	 * 
	 * @return
	 */
	public AbstractScoring getAppliedScoringMethod() {
		return appliedScoringMethod;
	}
	
	
	/**
	 * Returns whether the proteins are ranked.
	 * @return
	 */
	public Boolean getAreProteinsRanked() {
		for (ReportProtein protein : reportProteins) {
			if ((protein.getRank() != null)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Calculates the ranking. If the filter List is not null or empty, the
	 * Report is filtered before ranking.
	 *
	 * @param filters
	 */
	public void calculateRanking(List<AbstractFilter> filters) {
		if ((appliedProteinInference == null) ||
				(appliedProteinInference.getScoring() == null)) {
			logger.error("No protein inference set." +
					" Please calculate inference before ranking.");
			return;
		}
		
		// first, dump all prior ranking
		for (ReportProtein protein : reportProteins) {
			protein.setRank(-1L);
		}
		
		Comparator<ReportProtein> comparator;
		if (appliedProteinInference.getScoring().higherScoreBetter()) {
			comparator = ReportProteinComparatorFactory.CompareType.
					SCORE_SORT_HIGHERSCOREBETTER.getNewInstance();
		} else {
			comparator = ReportProteinComparatorFactory.CompareType.
					SCORE_SORT.getNewInstance();
		}
		
		RankCalculator.calculateRanking(
                ScoreModelEnum.PROTEIN_SCORE.getShortName(),
                FilterFactory.applyFilters(reportProteins, filters),
                comparator);
	}
	
	
	/**
	 * Resorts the report with the given sorting parameters
	 */
	public void sortReport(List<String> sortOrders,
			Map<String, SortOrder> sortables) {
		
		List<Comparator<ReportProtein>> compares =
				new ArrayList<Comparator<ReportProtein>>();
		
		for (String sortKey : sortOrders) {
			SortOrder order = sortables.get(sortKey);
			
			if (sortKey.equals(
					ReportProteinComparatorFactory.CompareType.
						SCORE_SORT.toString()) &&
					getAppliedScoringMethod().higherScoreBetter()) {
				// if it is the score sorting with a higherScoreBetter flag
				sortKey = ReportProteinComparatorFactory.CompareType.
							SCORE_SORT_HIGHERSCOREBETTER.toString();
			}
			
			compares.add( ReportProteinComparatorFactory.getComparatorByName(
					sortKey, order)
					);
		}
		
		Collections.sort(reportProteins,
				ReportProteinComparatorFactory.getComparator(compares));
	}
	
	
	/**
	 * Apply the given scoring to the List of {@link ReportProtein}s.
	 * 
	 * @param scoring
	 */
	public void applyScoring(AbstractScoring scoring) {
		if (scoring == null) {
			logger.error("No scoring method given.");
			appliedScoringMethod = null;
			return;
		}
		
		logger.info("applying scoring method: " + scoring.getName());
		scoring.calculateProteinScores(reportProteins);
		logger.info("scoring done");
		appliedScoringMethod = scoring;
		
		this.fdrData = new FDRData(defaultDecoyStrategy, defaultDecoyPattern,
				defaultFDRThreshold);
	}
	
	
	/**
	 * Getter for the protein FDR data
	 * @return
	 */
	public FDRData getFDRData() {
		return fdrData;
	}
	
	
	/**
	 * Returns, whether there are any PSMs in the PIA XML file, which are
	 * flagged as decoys.
	 * @return
	 */
	public Boolean getInternalDecoysExist() {
		for (Long fileID : psmModeller.getFiles().keySet()) {
			if ((fileID > 0) && psmModeller.getFileHasInternalDecoy(fileID)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Updates the {@link FDRData} for the protein FDR
	 * @return
	 */
	public void updateFDRData(FDRData.DecoyStrategy decoyStrategy,
			String decoyPattern, Double fdrThreshold) {
		fdrData.setDecoyStrategy(decoyStrategy);
		fdrData.setDecoyPattern(decoyPattern);
		fdrData.setFDRThreshold(fdrThreshold);
		fdrData.setScoreShortName(ScoreModelEnum.PROTEIN_SCORE.getShortName());
		
		logger.info("Protein FDRData set to: " +
				fdrData.getDecoyStrategy() + ", " +
				fdrData.getDecoyPattern() + ", " +
				fdrData.getFDRThreshold() + ", " +
				fdrData.getScoreShortName());
	}
	
	
	/**
	 * Updates the decoy states of the Proteins with the current settings from
	 * the FDRData.
	 */
	public void updateDecoyStates() {
		logger.info("updateDecoyStates ");
		Pattern p = Pattern.compile(fdrData.getDecoyPattern());
		
		for (ReportProtein protein : reportProteins) {
			// dump all FDR data
			protein.dumpFDRCalculation();
			protein.updateDecoyStatus(fdrData.getDecoyStrategy(), p);
		}
	}
	
	
	/**
	 * Calculate the protein FDR
	 */
	public void calculateFDR() {
		// calculate the FDR values
		fdrData.calculateFDR(reportProteins,
				getAppliedScoringMethod().higherScoreBetter());
	}
	
	
	/**
	 * Returns the report filters.
	 * @return
	 */
	public List<AbstractFilter> getReportFilters() {
		if (reportFilters == null) {
			reportFilters = new ArrayList<AbstractFilter>();
		}
		
		return reportFilters;
	}
	
	
	/**
	 * Add a new filter to the report filters.
	 */
	public boolean addReportFilter(AbstractFilter newFilter) {
		if (newFilter != null) {
			return getReportFilters().add(newFilter);
		} else {
			return false;
		}
	}
	
	
	/**
	 * Removes the report filter at the given index.
	 * @param removingIndex
	 * @return
	 */
	public AbstractFilter removeReportFilter(int removingIndex) {
		if ((removingIndex >= 0) &&
				(reportFilters != null) &&
				(removingIndex < reportFilters.size())) {
			return reportFilters.remove(removingIndex);
		}
		
		return null;
	}
	
	
	/**
	 * Returns the list of inference filters. These are not the currently set
	 * filters, but a list of filters which may be used for inference.
	 * @return
	 */
	public List<AbstractFilter> getInferenceFilters() {
		if (inferenceFilters == null) {
			inferenceFilters = new ArrayList<AbstractFilter>();
		}
		
		return inferenceFilters;
	}
	
	
	/**
	 * Add a new filter to the inference filters. These are not the currently
	 * set filters, but a list of filters which may be used for inference. 
	 */
	public boolean addInferenceFilter(AbstractFilter newFilter) {
		if (newFilter != null) {
			return getInferenceFilters().add(newFilter);
		} else {
			return false;
		}
	}
	
	
	/**
     * Writes the Protein report with the given filters in a loose CSV format.
     * <br/>
     * As the export may or may not also contain peptide-, PSM-set- and
     * PSM-data, each exported line has a specifying tag in the beginning, if
     * more than only the protein data is exported. The tags are PROTEIN,
     * PEPTIDE, PSMSET and PSM for the respective data. Additionally there is a
     * line starting with COLS_[tag], specifying the columns of the respective
     * data.
     * 
     * @throws IOException
     */
	public void exportCSV(Writer writer, Boolean filterExport,
			Boolean includePeptides, Boolean includePSMSets,
			Boolean includePSMs, Boolean exportForSC) throws IOException {
		List<ReportProtein> report;
		Boolean includes = includePeptides || includePSMSets || includePSMs;
		List<String> scoreShorts = peptideModeller.getScoreShortNames(0L);
		
		boolean considermodifications =
				peptideModeller.getConsiderModifications();
		
		if (includes && !exportForSC) {
			writer.append(
					"\"COLS_PROTEIN\";"+
					"\"accessions\";" +
					"\"score\";" +
					"\"#peptides\";" +
					"\"#PSMs\";" +
					"\"#spectra\";" +
					"\n"
					);
			
			if (includePeptides) {
				writer.append(
						"\"COLS_PEPTIDE\";"+
						"\"sequence\";");
				
				if (considermodifications) {
					writer.append("\"modifications\";");
				}
				
				writer.append(	"\"accessions\";" +
						"\"#spectra\";" +
						"\"#PSMSets\";" +
						"\"bestScores\";" +
						"\n"
						);
			}
			
			if (includePSMSets) {
				writer.append(
						"\"COLS_PSMSET\";"+
						"\"sequence\";");
				
				if (considermodifications) {
					writer.append("\"modifications\";");
				}
				
				writer.append("\"#identifications\";" +
						"\"charge\";" +
						"\"m/z\";" +
						"\"dMass\";" +
						"\"ppm\";" +
						"\"RT\";" +
						"\"missed\";" +
						"\"sourceID\";" +
						"\"spectrumTitle\";" +
						"\n"
						);
			}
			
			if (includePSMs) {
				writer.append(
						"\"COLS_PSM\";"+
						"\"filename\";" +
						"\"sequence\";");
				
				if (considermodifications) {
					writer.append("\"modifications\";");
				}
				
				writer.append("\"charge\";" +
						"\"m/z\";" +
						"\"dMass\";" +
						"\"ppm\";" +
						"\"RT\";" +
						"\"missed\";" +
						"\"sourceID\";" +
						"\"spectrumTitle\";" +
						"\"scores\";" +
						"\n"
						);
			}
			
		} else if (!exportForSC) {
			// no special includes, no SpectralCounting
			writer.append(
					"\"accessions\";" +
					"\"score\";" +
					"\"#peptides\";" +
					"\"#PSMs\";" +
					"\"#spectra\";" +
					"\n"
					);
		} else {
			// exportForSC is set, overrride everything else
			writer.append(
					"\"accession\";" +
					"\"filename\";" +
					"\"sequence\";");
			
			if (considermodifications) {
				writer.append("\"modifications\";");
			}
			
			writer.append("\"charge\";" +
					"\"m/z\";" +
					"\"dMass\";" +
					"\"ppm\";" +
					"\"RT\";" +
					"\"missed\";" +
					"\"sourceID\";" +
					"\"spectrumTitle\";" +
					"\"scores\";" +
					"\"isUnique\";" +
					"\n"
					);
		}
		
		report = filterExport ? getFilteredReportProteins(getReportFilters()) :
			reportProteins;
		
		if (report == null) {
			// no inference run?
			logger.warn("The report is empty, probably no inference run?");
			writer.flush();
			return;
		}
		
		for (ReportProtein protein : report) {
			// Accessions	Score	Coverage	#Peptides	#PSMs	#Spectra
			
			StringBuffer accSB = new StringBuffer();
			for (Accession accession : protein.getAccessions()) {
				if (accSB.length() > 0) {
					accSB.append(",");
				}
				accSB.append(accession.getAccession());
			}
			
			if (!exportForSC) {
				if (includes) {
					writer.append("\"PROTEIN\";");
				}
				
				writer.append("\"" + accSB.toString() + "\";" +
						"\"" + protein.getScore() + "\";" +
						"\"" + protein.getNrPeptides() + "\";" +
						"\"" + protein.getNrPSMs() + "\";" +
						"\"" + protein.getNrSpectra() + "\";" +
						"\n"
						);
			}
			
			
			if (includes || exportForSC) {
				for (ReportPeptide peptide : protein.getPeptides()) {
					
					StringBuffer modStringBuffer = new StringBuffer();
					if (considermodifications) {
						for (Map.Entry<Integer, Modification> modIt
								: peptide.getModifications().entrySet()) {
							modStringBuffer.append("[" + modIt.getKey() + ";" +
									modIt.getValue().getMass() + ";");
							if (modIt.getValue().getDescription() != null) {
								modStringBuffer.append(
										modIt.getValue().getDescription());
							}
							modStringBuffer.append("]");
						}
					}
					
					if (includePeptides && !exportForSC) {
						
						accSB = new StringBuffer();
						for (Accession accession : peptide.getAccessions()) {
							if (accSB.length() > 0) {
								accSB.append(",");
							}
							accSB.append(accession.getAccession());
						}
						
						StringBuffer scoresSB = new StringBuffer();
						for (String scoreShort : scoreShorts) {
							ScoreModel model =
									peptide.getBestScoreModel(scoreShort);
							
							if (model != null) {
								if (scoresSB.length() > 0) {
									scoresSB.append(",");
								}
								
								scoresSB.append(model.getName() + ":" +
										model.getValue());
							}
							
						}
						
						writer.append(
								"\"PEPTIDE\";"+
								"\"" + peptide.getSequence() + "\";");
						
						if (considermodifications) {
							writer.append(
									"\"" + modStringBuffer.toString() + "\";");
						}
						
						writer.append("\"" + accSB.toString() + "\";" +
								"\"" + peptide.getNrSpectra() + "\";" +
								"\"" + peptide.getNrPSMs() + "\";" +
								"\"" + scoresSB.toString() + "\";" +
								"\n"
								);
					}
					
					
					if (includePSMSets || includePSMs || exportForSC) {
						for (PSMReportItem psmSet : peptide.getPSMs()) {
							if (psmSet instanceof ReportPSMSet) {
								
								if (includePSMSets && !exportForSC) {
									String rt;
									if (psmSet.getRetentionTime() != null) {
										rt = psmSet.getRetentionTime().toString();
									} else {
										rt = "";
									}
									
									String sourceID = psmSet.getSourceID();
									if (sourceID == null) {
										sourceID = "";
									}
									
									String spectrumTitle = psmSet.getSpectrumTitle();
									if (spectrumTitle == null) {
										spectrumTitle = "";
									}
									
									writer.append(
											"\"PSMSET\";"+
											"\"" + psmSet.getSequence() +"\";");
									
									if (considermodifications) {
										writer.append(
												"\"" + modStringBuffer.toString() + "\";");
									}
									
									writer.append("\"" + ((ReportPSMSet)psmSet).getPSMs().size() + "\";" +
											"\"" + psmSet.getCharge() + "\";" +
											"\"" + psmSet.getMassToCharge() + "\";" +
											"\"" + psmSet.getDeltaMass() + "\";" +
											"\"" + psmSet.getDeltaPPM() + "\";" +
											"\"" + rt + "\";" +
											"\"" + psmSet.getMissedCleavages() + "\";" +
											"\"" + sourceID + "\";" +
											"\"" + spectrumTitle + "\";" +
											"\n"
											);
								}
								
								if (includePSMs || exportForSC) {
									for (ReportPSM psm
											: ((ReportPSMSet)psmSet).getPSMs()) {
										
										String rt;
										if (psm.getRetentionTime() != null) {
											rt = psmSet.getRetentionTime().toString();
										} else {
											rt = "";
										}
										
										String sourceID = psm.getSourceID();
										if (sourceID == null) {
											sourceID = "";
										}
										
										String spectrumTitle = psm.getSpectrumTitle();
										if (spectrumTitle == null) {
											spectrumTitle = "";
										}
										
										StringBuffer scoresSB = new StringBuffer();
										for (ScoreModel model : psm.getScores()) {
											if (scoresSB.length() > 0) {
												scoresSB.append(",");
											}
											scoresSB.append(model.getName() +
													":" + model.getValue());
										}
										
										Boolean uniqueness =
												(psm.getSpectrum().getIsUnique() != null) ?
														psm.getSpectrum().getIsUnique() : false;
										
										if (!exportForSC) {
											writer.append(
													"\"PSM\";" +
													"\"" + psm.getInputFileName() + "\";" +
													"\"" + psm.getSequence() + "\";");
											
											if (considermodifications) {
												writer.append(
														"\"" + modStringBuffer.toString() + "\";");
											}
											
											writer.append("\"" + psm.getCharge() + "\";" +
													"\"" + psm.getMassToCharge() + "\";" +
													"\"" + psm.getDeltaMass() + "\";" +
													"\"" + psm.getDeltaPPM() + "\";" +
													"\"" + rt + "\";" +
													"\"" + psm.getMissedCleavages() + "\";" +
													"\"" + sourceID + "\";" +
													"\"" + spectrumTitle + "\";" +
													"\"" + scoresSB.toString() + "\";" +
													"\n"
													);
										} else {
											// export for Spectral Counting
											for (Accession acc
													: protein.getAccessions()) {
												
												writer.append(
														"\"" + acc.getAccession() + "\";" +
														"\"" + psm.getInputFileName() + "\";" +
														"\"" + psm.getSequence() + "\";"
														);
												
												if (considermodifications) {
													writer.append(
															"\"" + modStringBuffer.toString() + "\";");
												}
												
												writer.append("\"" + psm.getCharge() + "\";" +
														"\"" + psm.getMassToCharge() + "\";" +
														"\"" + psm.getDeltaMass() + "\";" +
														"\"" + psm.getDeltaPPM() + "\";" +
														"\"" + rt + "\";" +
														"\"" + psm.getMissedCleavages() + "\";" +
														"\"" + sourceID + "\";" +
														"\"" + spectrumTitle + "\";" +
														"\"" + scoresSB.toString() + "\";" +
														"\"" + uniqueness  + "\";" +
														"\n"
														);
											}
										}
										
									}
								}
								
							} else {
								// TODO: better error/exception
								logger.error("PSM in peptide must be PSMSet!");
							}
						}
					}
					
					
				}
			}
		}
		
		writer.flush();
	}



	
	/**
	 * Processes the command line on the protein level.
	 * 
	 * @param model
	 * @param commands
	 * @return
	 */
	public static boolean processCLI(ProteinModeller model, String[] commands) {
		if (model == null) {
			logger.error("No protein modeller given while processing CLI " +
					"commands");
			return false;
		}
		
		Pattern pattern = Pattern.compile("^([^=]+)=(.*)");
		Matcher commandParamMatcher;
		
		for (String command : commands) {
			String[] params = null;
			commandParamMatcher = pattern.matcher(command);
			
			if (commandParamMatcher.matches()) {
				command = commandParamMatcher.group(1);
				params = commandParamMatcher.group(2).split(",");
			}
			
			try {
				//ProteinExecuteCommands.valueOf(command).execute(model, params);
			} catch (IllegalArgumentException e) {
				logger.error("Could not process unknown call to " +
						command);
			}
		}
		
		return true;
	}
}