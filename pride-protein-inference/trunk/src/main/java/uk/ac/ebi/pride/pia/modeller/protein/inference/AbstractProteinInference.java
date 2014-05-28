package uk.ac.ebi.pride.pia.modeller.protein.inference;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructure;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructureCreator;
import uk.ac.ebi.pride.pia.modeller.protein.scoring.AbstractScoring;
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
	
	/** the logger for this class */
	private static final Logger logger= Logger.getLogger(AbstractProteinInference.class);
	
	/** list of the settings. */
	protected List<AbstractFilter> filters;
	
	/** the currently set scoring */
	protected AbstractScoring currentScoring;
	
	/** the number of allowed threads (smaller 1 = all available)*/
	protected int allowedThreads;

	/** the PIA intermediate structure */
	protected IntermediateStructure intermediateStructure;
    
    
    /***
     * The constructor works using a DataAccessController. Any of the current
     * implementations of this class should use this controller to retrieve the
     * peptide information and protein information.
     * 
     * @param controller
     * @param nrThreads
     */
	public AbstractProteinInference(DataAccessController controller, int nrThreads) {
		this.filters = new ArrayList<AbstractFilter>();
		this.currentScoring = null;
		this.allowedThreads = nrThreads;
		
		// create the interemdiate structure from the data given by the controller
        IntermediateStructureCreator structCreator =
        		new IntermediateStructureCreator(this.allowedThreads);
		
		for (Comparable proteinId : controller.getProteinIds()) {
			for (Peptide pep : controller.getProteinById(proteinId).getPeptides()) {
				structCreator.addSpectrumIdentification(pep.getSpectrumIdentification());
			}
		}
		
		intermediateStructure = structCreator.buildIntermediateStructure();
	}
	
	

    /**
     * calculateInference is the method of the abstract class which allows the
     * class to compute the protein groups and create the List of different
     * groups.
     * 
     * @param considerModifications
     * @param psmSetSettings
     * @return
     */
    public abstract List<ProteinGroup> calculateInference(
    		boolean considerModifications);
    
    
	/**
	 * This method creates a Map from the groups' IDs to the associated
	 * {@link IntermediatePeptide}s, which satisfy the currently set filters.
	 *
	 * @param considerModifications
	 * @return
	 */
	public Map<Integer, Set<IntermediatePeptide>> createFilteredPeptidesMap(boolean considerModifications) {
		Map<Integer, Set<IntermediatePeptide>> groupIdToPeptides =
				new HashMap<Integer, Set<IntermediatePeptide>>(intermediateStructure.getNrGroups() / 2);
		
		for (Set<IntermediateGroup> cluster : intermediateStructure.getClusters().values()) {
			// this could be parallelized for each cluster...
			for (IntermediateGroup group : cluster) {
				
				if ((group.getPeptides() == null) ||
						(group.getPeptides().size() < 1)) {
					// no peptides in the group -> go on
					continue;
				}
				
				Map<Comparable, IntermediatePeptide> groupsPepsMap =
						new HashMap<Comparable, IntermediatePeptide>();
				
				for (IntermediatePeptide pep : group.getPeptides()) {
					for (SpectrumIdentification psm : pep.getPeptideSpectrumMatches()) {
						if (/* TODO: turn on filtering on PSM level! FilterFactory.satisfiesFilterList(psm, 0L, filters)*/ true) {
							// all filters on PSM level are satisfied -> use this PSM
							Comparable pepID = considerModifications ?
									psm.getPeptideSequence().getId() : psm.getSequence();
							
							// get the peptide of this PSM
							IntermediatePeptide psmsPeptide = groupsPepsMap.get(pepID);
							if (psmsPeptide == null) {
								// no peptide for the pepID in the map yet
								psmsPeptide = new IntermediatePeptide(psm.getSequence());
								groupsPepsMap.put(pepID, psmsPeptide);
							}
							
							psmsPeptide.addSpectrum(psm);
						}
					}
				}
				
				Set<IntermediatePeptide> groupsPeptides = new HashSet<IntermediatePeptide>();
				for (IntermediatePeptide pep : groupsPepsMap.values()) {
					if (/* TODO: turn on filtering on peptide level! FilterFactory.satisfiesFilterList(pep, 0L, filters)*/ true) {
						// the peptide does satisfy the filters
						groupsPeptides.add(pep);
					}
				}
				groupIdToPeptides.put(group.getID(), groupsPeptides);
			}
		}
		
		return groupIdToPeptides;
	}
	

	/**
	 * Tests for the given group, if it has any direct {@link IntermediatePeptide}s,
	 * in the given Map. This Map should by created by 
	 * {@link AbstractProteinInference#createFilteredPeptidesMap(boolean)} prior
	 * to calling this function.
	 */
	public boolean groupHasDirectReportPeptides(IntermediateGroup group,
			Map<Integer, Set<IntermediatePeptide>> groupIdToReportPeptides) {
		Set<IntermediatePeptide> pepSet = groupIdToReportPeptides.get(group.getID());
		return ((pepSet != null) && (pepSet.size() > 0));
	}
	
	
	/**
	 * Tests for the given group, if it has any {@link IntermediatePeptide}s,
	 * whether direct or in the peptideChildren, in the given Map. This Map
	 * should be created by {@link AbstractProteinInference#createFilteredPeptidesMap(boolean)}
	 * prior to calling this function.
	 */
	public boolean groupHasReportPeptides(IntermediateGroup group,
			Map<Integer, Set<IntermediatePeptide>> groupIdToReportPeptides) {
		// check for direct ReportPeptides
		if (groupHasDirectReportPeptides(group, groupIdToReportPeptides)) {
			return true;
		}
		
		// check for ReportPeptides of the children
		for (IntermediateGroup childGroup : group.getAllPeptideChildren()) {
			if (groupHasDirectReportPeptides(childGroup, groupIdToReportPeptides)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Ceates a unique ID for a ProteinAmbiguityGroup from the group ID
	 * @param group
	 * @return
	 */
	public String createProteinGroupID(IntermediateGroup group) {
		return "PAG_" + group;
	}
	
	
	/**
	 * Ceates a unique ID for a ProteinDetectionHypothesis from the group ID
	 * @param group
	 * @return
	 */
	public String createProteinID(IntermediateGroup group, DBSequence dbSeq) {
		return "PDH_" + dbSeq.getAccession() + "_"
				+ createProteinGroupID(group);
	}
	
	
    /**
	 * If polling of inference is performed, return the current state of the
	 * progress (between 0 and 100 progress in percent, &lt;0 inference aborted,
	 * &gt;100 inference is done).
	 * @return
	 */
	public abstract Long getProgressValue();
	
	
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
