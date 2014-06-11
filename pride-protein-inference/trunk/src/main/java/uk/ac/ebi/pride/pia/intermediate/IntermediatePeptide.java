package uk.ac.ebi.pride.pia.intermediate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A peptide class, which is used in the intermediate structure.
 * 
 * @author julian
 *
 */
public class IntermediatePeptide {
	
	/** Sequence of the peptide */
	private String sequence;
	
	/** the connected group of this peptides */
	private IntermediateGroup group;
	
	/** List of identifications for this peptide */
	private Map<Comparable, IntermediatePeptideSpectrumMatch> peptideSpectrumMatches;
	
	/** The PSMs passing a given filter */
	private Set<Comparable> psmsPassingFilter;
	
	
	/**
	 * Basic constructor, only initializes the sequence
	 *  
	 * @param sequence
	 */
	public IntermediatePeptide(String sequence) {
		this.sequence = sequence;
		this.group = null;
		this.peptideSpectrumMatches = new HashMap<Comparable, IntermediatePeptideSpectrumMatch>();
		this.psmsPassingFilter = null;
	}
	
	
	/**
	 * Returns an identifier for the peptide
	 * 
	 * @return
	 */
	public Comparable getID() {
		return computeID(sequence);
	}
	
	
	/**
	 * Returns the ID comoputed by the given arguments.
	 * <p>
	 * This is for probable future integration of modifications
	 * 
	 * @param sequence
	 * @return
	 */
	public static String computeID(String sequence) {
		return sequence;
	}
	
	
	/**
	 * Getter for the sequence.
	 * 
	 * @return
	 */
	public String getSequence() {
		return sequence;
	}
	
	
	/**
	 * sets the group of this peptide
	 * @param group
	 */
	public void setGroup(IntermediateGroup group) {
		this.group = group;
	}
	
	
	/**
	 * returns the group of this peptide
	 */
	public IntermediateGroup getGroup() {
		return group;
	}
	
	
	/**
	 * Adds the given spectrumIdentification to the peptide's PSMs
	 * <p>
	 * Is the PSM is added, it is also added to the filter passing PSMs.
	 * 
	 * @param spectrum
	 * @return true if this peptide did not already contain the spectrum identification
	 */
	public boolean addPeptideSpectrumMatch(IntermediatePeptideSpectrumMatch spectrumIdentification) {
		if (!peptideSpectrumMatches.containsKey(spectrumIdentification.getID())) {
			peptideSpectrumMatches.put(spectrumIdentification.getID(), spectrumIdentification);
			
			if (psmsPassingFilter != null) {
				psmsPassingFilter.add(spectrumIdentification.getID());
			}
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Getter for the psms.
	 * <p>
	 * If a passing set is created, only the passing PSMs are returned. 
	 * 
	 * @return
	 */
	public List<IntermediatePeptideSpectrumMatch> getPeptideSpectrumMatches() {
		if (psmsPassingFilter != null) {
			List<IntermediatePeptideSpectrumMatch> psms =
					new ArrayList<IntermediatePeptideSpectrumMatch>(psmsPassingFilter.size());
			for (Map.Entry<Comparable, IntermediatePeptideSpectrumMatch> psmIt : peptideSpectrumMatches.entrySet()) {
				if (psmsPassingFilter.contains(psmIt.getKey())) {
					psms.add(psmIt.getValue());
				}
			}
			return psms;
		} else {
			return new ArrayList<IntermediatePeptideSpectrumMatch>(peptideSpectrumMatches.values());
		}
	}
	
	
	/**
	 * Getter for the PSMs, also the ones, which do not pass the filters.
	 * @return
	 */
	public List<IntermediatePeptideSpectrumMatch> getAllPeptideSpectrumMatches() {
		return new ArrayList<IntermediatePeptideSpectrumMatch>(peptideSpectrumMatches.values());
	}
	
	
	/**
	 * Makes all filtering obsolete, i.e. all PSMs are passing the filters.
	 */
	public void removeFilteringEffects() {
		psmsPassingFilter = null;
	}
	
	
	/**
	 * Makes all PSMs failing the filters (or initializes the filtering)
	 */
	public void filterOutAllPSMs() {
		psmsPassingFilter = new HashSet<Comparable>();
	}
	
	
	/**
	 * Makes the given PSM pass the filters
	 */
	public void psmPassesFilter(Comparable psmID) {
		psmsPassingFilter.add(psmID);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || !(obj instanceof IntermediatePeptide)) return false;
		
		IntermediatePeptide peptide = (IntermediatePeptide)obj;
		
		if (!sequence.equals(peptide.getSequence())) return false;
		if (psmsPassingFilter != null) {
			if (!psmsPassingFilter.equals(peptide.psmsPassingFilter)) {
				return false;
			}
		} else {
			if (peptide.psmsPassingFilter != null) {
				return false;
			}
		}
		
		return !((group != null) ? !group.getID().equals(peptide.getGroup().getID()) : (peptide.getGroup() != null)) &&
				!((peptideSpectrumMatches != null) ? !peptideSpectrumMatches.equals(peptide.peptideSpectrumMatches) : (peptide.peptideSpectrumMatches != null));
	}
	
	
	@Override
	public int hashCode() {
        int result = (sequence != null) ? sequence.hashCode() : 0;
        result = 31 * result + ((group != null) ? group.getID().hashCode() : 0);
        result = 31 * result + ((peptideSpectrumMatches != null) ? peptideSpectrumMatches.hashCode() : 0);
        return result;
	}
}
