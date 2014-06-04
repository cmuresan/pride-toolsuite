package uk.ac.ebi.pride.pia.intermediate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;

/**
 * A peptide class, which defines a peptide by its sequence only.
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
	private Set<SpectrumIdentification> peptideSpectrumMatches;
	
	/** the peptide evidences */
	private Set<PeptideEvidence> pepEvidences;
	
	
	/**
	 * Basic constructor, only initializes the sequence
	 *  
	 * @param sequence
	 */
	public IntermediatePeptide(String sequence) {
		this.sequence = sequence;
		this.group = null;
		this.peptideSpectrumMatches = new HashSet<SpectrumIdentification>();
		this.pepEvidences = new HashSet<PeptideEvidence>();
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
	 * Setter for the peptideSpectrumMatches
	 * 
	 * @param spectra
	 */
	public void setSpectra(Collection<SpectrumIdentification> spectrumIDs) {
		this.peptideSpectrumMatches =
				new HashSet<SpectrumIdentification>(spectrumIDs);
	}
	
	
	/**
	 * adds the given peptideSpectrumMatches to the list of
	 * peptideSpectrumMatches
	 * 
	 * @param spectrum
	 * @return true if this peptide did not already contain the spectrum identification
	 */
	public boolean addSpectrum(SpectrumIdentification spectrumIdentification) {
		
		boolean changed = false;
		
		if (peptideSpectrumMatches.add(spectrumIdentification)) {
			changed = true;
			
			for (PeptideEvidence pepEvidence : spectrumIdentification.getPeptideEvidenceList()) {
				pepEvidences.add(pepEvidence);
			}
		} else {
			
			System.out.println("already in set (but are the pepEvidences?) " + spectrumIdentification.getId());
			
		}
		
		return changed;
	}
	
	
	/**
	 * Getter for the spectra
	 * 
	 * @return
	 */
	public Set<SpectrumIdentification> getPeptideSpectrumMatches() {
		return peptideSpectrumMatches;
	}
	
	
	/**
	 * getter for the AccessionOccurrences
	 * @return
	 */
	public Set<PeptideEvidence> getPeptideEvidences() {
		return pepEvidences;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
		
		IntermediatePeptide peptide = (IntermediatePeptide)obj;
		
		return !((sequence != null) ? !sequence.equals(peptide.sequence) : (peptide.sequence != null)) && 
				!((group != null) ? !group.getID().equals(peptide.group.getID()) : (peptide.group != null)) &&
				!((peptideSpectrumMatches != null) ? !peptideSpectrumMatches.equals(peptide.peptideSpectrumMatches) : (peptide.peptideSpectrumMatches != null)) &&
				!((pepEvidences != null) ? !pepEvidences.equals(peptide.pepEvidences) : (peptide.pepEvidences != null));
	}
	
	
	@Override
	public int hashCode() {
        int result = (sequence != null) ? sequence.hashCode() : 0;
        result = 31 * result + ((group != null) ? group.getID().hashCode() : 0);
        result = 31 * result + ((peptideSpectrumMatches != null) ? peptideSpectrumMatches.hashCode() : 0);
        result = 31 * result + ((pepEvidences != null) ? pepEvidences.hashCode() : 0);
        return result;
	}
}
