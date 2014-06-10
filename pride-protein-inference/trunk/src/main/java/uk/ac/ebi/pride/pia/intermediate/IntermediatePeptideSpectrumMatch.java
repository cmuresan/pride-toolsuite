package uk.ac.ebi.pride.pia.intermediate;

import uk.ac.ebi.pride.data.core.SpectrumIdentification;


/**
 * Representation of a peptide spectrum match in the intermediate structure.
 * 
 * @author julian
 *
 */
public interface IntermediatePeptideSpectrumMatch {
	
	/**
	 * Returns an ID of the peptideSpectrumMatch
	 * 
	 * @return
	 */
	public Comparable getID();
	
	
	/**
	 * Returns the actual spectrum identification.
	 * 
	 * @return
	 */
	public SpectrumIdentification getSpectrumIdentification();
}
