package uk.ac.ebi.pride.pia.intermediate;

import java.util.ArrayList;
import java.util.Collection;
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
	
	
	/**
	 * Basic constructor, only initializes the sequence
	 *  
	 * @param sequence
	 */
	public IntermediatePeptide(String sequence) {
		this.sequence = sequence;
		this.group = null;
		this.peptideSpectrumMatches = new HashMap<Comparable, IntermediatePeptideSpectrumMatch>();
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
	 * adds the given spectrumIdentification to the peptide's PSMs
	 * 
	 * @param spectrum
	 * @return true if this peptide did not already contain the spectrum identification
	 */
	public boolean addPeptideSpectrumMatch(IntermediatePeptideSpectrumMatch spectrumIdentification) {
		if (!peptideSpectrumMatches.containsKey(spectrumIdentification.getID())) {
			peptideSpectrumMatches.put(spectrumIdentification.getID(), spectrumIdentification);
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * Getter for the spectra
	 * 
	 * @return
	 */
	public List<IntermediatePeptideSpectrumMatch> getPeptideSpectrumMatches() {
		return new ArrayList<IntermediatePeptideSpectrumMatch>(peptideSpectrumMatches.values());
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || !(obj instanceof IntermediatePeptide)) return false;
		
		IntermediatePeptide peptide = (IntermediatePeptide)obj;
		
		if (!sequence.equals(peptide.getSequence())) return false;
		
		return !((group != null) ? !group.getID().equals(peptide.getGroup().getID()) : (peptide.getGroup() != null)) &&
				!((peptideSpectrumMatches != null) ? !peptideSpectrumMatches.equals(peptide.getPeptideSpectrumMatches()) : (peptide.getPeptideSpectrumMatches() != null));
	}
	
	
	@Override
	public int hashCode() {
        int result = (sequence != null) ? sequence.hashCode() : 0;
        result = 31 * result + ((group != null) ? group.getID().hashCode() : 0);
        result = 31 * result + ((peptideSpectrumMatches != null) ? peptideSpectrumMatches.hashCode() : 0);
        return result;
	}
}
