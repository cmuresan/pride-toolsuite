package uk.ac.ebi.pride.pia.modeller.protein.inference;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.pia.intermediate.Group;
import uk.ac.ebi.pride.pia.modeller.peptide.ReportPeptide;
import uk.ac.ebi.pride.pia.modeller.protein.ReportProtein;
import uk.ac.ebi.pride.pia.modeller.protein.scoring.AbstractScoring;
import uk.ac.ebi.pride.pia.modeller.psm.ReportPSMSet;
import uk.ac.ebi.pride.pia.modeller.report.filter.AbstractFilter;
import uk.ac.ebi.pride.pia.tools.LabelValueContainer;

import java.util.*;


/**
 * This abstract class define the main methods for each ProteinInference methods.
 * It works with a controller that do not contain protein groups information. If the
 * data access controller contains ProteinGroup Information the class return the original
 * information from the file. The Protein Inference algorithm also use a set of Filters at
 * at Protein and Peptide level.
 *
 * An {@link AbstractProteinInference} calculates for the PIA groups,
 * which are to be reported and in which way.
 * 
 * @author julian, yperez
 *
 */
public abstract class AbstractProteinInference {

    DataAccessController controller = null;
	
	/** list of the settings. */
	protected List<AbstractFilter> filters;
	
	/** the available scores for the inference, maps from the scoreShort to the shown name */
	private Map<String, String> availableScoreShorts;
	
	/** the currently set scoring */
	private AbstractScoring currentScoring;

	/** the logger for this class */
	private static final Logger logger= Logger.getLogger(AbstractProteinInference.class);

    /** list of protein groups in the protein inference, if the file contains protein group information then
     * this list is initialized with the controller information
     * */
    private List<Group> proteinDetectionList = null;

    /***
     * The constructor works using a DataAccessController. Any of the current implementations of this class
     * should use this controller to retrieve the peptide information and protein information.
     * @param controller
     */
	public AbstractProteinInference(DataAccessController controller) {
        this.controller = controller;
		this.filters = Collections.emptyList();
		this.currentScoring = null;
        this.proteinDetectionList = initProteinGroup(controller);
	}

    /**
     * This function initialize a Protein Group List using the DataAccessController.
     * If the DataAccessController does not contain Protein Group Information it returns an empty List.
     *
     * @param controller
     * @return
     */
    private List<Group> initProteinGroup(DataAccessController controller) {
        List<Group> groups = Collections.emptyList();
        if(containsProteinGroupInformation()){
            for(Comparable idGroup: controller.getProteinAmbiguityGroupIds()){
                List<Protein> proteins = controller.getProteinAmbiguityGroupById(idGroup).getProteinDetectionHypothesis();
                List<Comparable> idProteins = new ArrayList<Comparable>(proteins.size());
                for(Protein protein: proteins) idProteins.add(protein.getId());
                Group group = new Group(idGroup,idProteins);
                groups.add(group);
            }
        }
        return groups;
    }

    /**
     * Return TRUE if the ProteinInference Information is present for the DataAccessController.
     * @return
     */
    public boolean containsProteinGroupInformation(){
        return !proteinDetectionList.isEmpty();
    }

    /**
     * calculateInference is the method of the abstract class which allows the class to compute the protein groups.
     * and create the List of different groups.
     * @param considerModifications
     * @param psmSetSettings
     * @return
     */
    public abstract List<Group> calculateInference(
            boolean considerModifications,
            Map<String, Boolean> psmSetSettings);


	/**
	 * This method creates a Map from the groups' IDs to the associated
	 * {}s, which can be build and satisfy the currently set
	 * filters.
	 *
	 * @param considerModifications
	 * @return
	 */
	public List<String> createFilteredReportPeptides(boolean considerModifications, Map<String, Boolean> psmSetSettings) {

        List<Comparable> peptideIds = new ArrayList<Comparable>();

        for(Comparable proteinId: controller.getProteinIds()){
            peptideIds.addAll(controller.getPeptideIds(proteinId));

        }

        for(Comparable peptideId: peptideIds){


        }

	/*
		for (Map.Entry<Long, Group> gIt : groupMap.entrySet()) {
			Map<String, ReportPeptide> gPepsMap = new HashMap<String, ReportPeptide>();
			
			if (gIt.getValue().getPeptides() == null) {
				// no peptides in the group -> go on
				continue;
			}
			
			for (Peptide pep :  gIt.getValue().getPeptides().values()) {
				
				for (PeptideSpectrumMatch psm : pep.getSpectra()) {
					// get the ReportPSM for each PeptideSpectrumMatch
					
					ReportPSMSet repSet =
							reportPSMSetMap.get(
									psm.getIdentificationKey(psmSetSettings));
					if (repSet == null) {
						// TODO: better error
						logger.warn("no PSMSet found for " +
								psm.getIdentificationKey(psmSetSettings) +
								"! createFilteredReportPeptides");
						continue;
					}
					
					ReportPSM reportPSM = null;
					for (ReportPSM repPSM : repSet.getPSMs()) {
						if (repPSM.getSpectrum().equals(psm)) {
							reportPSM = repPSM;
						}
					}
					if (reportPSM == null) {
						// TODO: better error
						logger.warn("no PSM found for " +
								psm.getIdentificationKey(psmSetSettings) +
								"! createFilteredReportPeptides");
						continue;
					}
					
					
					if (FilterFactory.
							satisfiesFilterList(reportPSM, 0L, filters)) {
						// all filters on PSM level are satisfied -> use this PSM
						String pepStringID = ReportPeptide.createStringID(reportPSM, considerModifications);
						
						// get the peptide of this PSM
						ReportPeptide peptide = gPepsMap.get(pepStringID);
						if (peptide == null) {
							// no peptide for the pepStringID in the map yet
							peptide = new ReportPeptide(reportPSM.getSequence(),
									pepStringID, pep);
							gPepsMap.put(pepStringID, peptide);
						}
						
						// get ReportPSMSet from the peptide
						ReportPSMSet reportPSMSet = null;
						List<PSMReportItem> setList =
								peptide.getPSMsByIdentificationKey(
										reportPSM.getIdentificationKey(
												psmSetSettings),
										psmSetSettings);
						
						if (setList != null) {
							if (setList.size() > 1) {
								// TODO: better error
								logger.warn("more than one ReportPSMSet in setList for "+
										reportPSM.getSourceID()+"! createFilteredReportPeptides");
							}
							
							for (PSMReportItem psmItem : setList) {
								if (psmItem instanceof ReportPSMSet) {
									reportPSMSet = (ReportPSMSet)psmItem;
									break;
								} else {
									// TODO: better error
									logger.warn("psmItem is not a ReportPSMSet! " +
											"createFilteredReportPeptides");
								}
							}
						}
						
						if (reportPSMSet == null) {
							reportPSMSet = new ReportPSMSet(psmSetSettings);
							peptide.addPSM(reportPSMSet);
						}
						
						reportPSMSet.addReportPSM(reportPSM);
					}
					
				}
				
			}
			
			// in the following, peptides can become PSM-less or don't satisfy the filters, so remove them later
			Set<String> removePeps = new HashSet<String>();
			
			// if a psmSet has the same PSMs as the associated one in
			// reportPSMSetMap, set all the FDR variables
			for (ReportPeptide pepIt : gPepsMap.values()) {
				for (String psmKey : pepIt.getPSMsIdentificationKeys(psmSetSettings)) {
					
					for (PSMReportItem psm
							: pepIt.getPSMsByIdentificationKey(psmKey, psmSetSettings)) {
						if (psm instanceof ReportPSMSet) {
							ReportPSMSet checkSet = reportPSMSetMap.get(psmKey);
							
							if (((ReportPSMSet) psm).getPSMs().size() == checkSet.getPSMs().size()) {
								// same size of PSMs
								boolean samePSMs = true;
								
								for (ReportPSM p : ((ReportPSMSet) psm).getPSMs()) {
									boolean found = false;
									
									for (ReportPSM q : checkSet.getPSMs()) {
										if (p.getId() == q.getId()) {
											found = true;
											break;
										}
									}
									
									if (!found) {
										samePSMs = false;
										break;
									}
								}
								
								if (samePSMs) {
									// same PSMs in both sets -> set FDR scores and so one
									if (checkSet.getFDRScore() != null) {
										psm.setFDR(checkSet.getFDR());
										psm.setFDRScore(checkSet.getFDRScore().getValue());
										psm.setIsFDRGood(checkSet.getIsFDRGood());
										psm.setQValue(checkSet.getQValue());
										psm.setRank(checkSet.getRank());
									}
								}
							}
							
							if (!FilterFactory.
									satisfiesFilterList(psm, 0L, filters)) {
								// if the ReportPSMSet does not satisfy the filters, remove it
								pepIt.removeReportPSMSet((ReportPSMSet) psm,
										psmSetSettings);
								
								if (pepIt.getNrPSMs() == 0) {
									removePeps.add(pepIt.getStringID());
								}
							}
						} else {
							// TODO: better error
							logger.warn("psm is not a ReportPSMSet! " +
									"createFilteredReportPeptides");
						}
					}
				}
				
				if ((pepIt.getNrPSMs() > 0) && 
						!FilterFactory.satisfiesFilterList(pepIt, 0L, filters)) {
					// the peptide has still PSMs but does not satisfy the filters
					// -> put it on the remove list
					removePeps.add(pepIt.getStringID());
				}
			}
			
			// remove empty or not filter satisfying peptides
			for (String pepKey : removePeps) {
				gPepsMap.remove(pepKey);
			}
			
			if (gPepsMap.size() > 0) {
				peptidesMap.put(gIt.getKey(),
						new ArrayList<ReportPeptide>(gPepsMap.values()));
			}
		}
		
		return peptidesMap;*/
        return null;
	}

    /** ----- Start of functions related with Filters handling. ----- */

    /**
     * Returns a List of SelectItems representing the available filters for this
     * inference.
     *
     * @return
     */
    public abstract List<LabelValueContainer<String>> getFilterTypes();

    /**
     * Get the human readable name of the filter.
     * @return
     */
    public abstract String getName();

    /**
     * Get the unique machine readable short name of the filter.
     * @return
     */
    public abstract String getShortName();

    /**
     * adds a new filter to the inference filters.
     * @param newFilter
     * @return
     */
    public boolean addFilter(AbstractFilter newFilter) {
        return filters.add(newFilter);
    }

    /**
     * Returns a {@link List} of all filter settings for this inference filter.
     * @return
     */
    public List<AbstractFilter> getFilters() {
        return filters;
    }

    /**
     * Removes the filter given by the index
     */
    public AbstractFilter removeFilter(int index) {
        if ((index >= 0) &&
                (index < filters.size())) {
            return filters.remove(index);
        }

        return null;
    }

    /** ----- End of functions related with Filters handling. ----- */


    /** ----- Start of functions related with Scoring handling. ----- */

    /**
     * Sets the available scores to the given Set of {@link uk.ac.ebi.pride.pia.modeller.score.ScoreModel}s.
     *
     * @param scores
     */
    public void setAvailableScoreShorts(Map<String, String> scores) {
        availableScoreShorts = scores;
    }

    /**
     * Getter for the available {@link uk.ac.ebi.pride.pia.modeller.score.ScoreModel}s.
     * @return
     */
    public Map<String, String> getAvailableScoreShorts() {
        return availableScoreShorts;
    }

    /**
     * Setter for the currently set scoring.
     * @param scoring
     */
    public void setScoring(AbstractScoring scoring) {
        this.currentScoring = scoring;
    }

    /**
     * Getter for the currently set scoring.
     * @return
     */
    public AbstractScoring getScoring() {
        return currentScoring;
    }

    /** ----- End of functions related with Scoring handling. ----- */

}
