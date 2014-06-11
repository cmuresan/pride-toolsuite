package uk.ac.ebi.pride.pia.intermediate.impl;

import java.util.Map;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.PeptideSequence;
import uk.ac.ebi.pride.data.core.Score;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.term.CvTermReference;


/**
 * Representation of a peptide spectrum match in the intermediate structure.
 * 
 * @author julian
 *
 */
public class PrideIntermediatePeptideSpectrumMatch implements IntermediatePeptideSpectrumMatch {
	
	private Comparable id;
	
	private DataAccessController controller;
	
	private Comparable proteinID;
	
	private Comparable peptideID;
	
	
	public PrideIntermediatePeptideSpectrumMatch(DataAccessController controller,
			Comparable proteinID, Comparable peptideID) {
		this.controller = controller;
		this.proteinID = proteinID;
		this.peptideID = peptideID;
	}
	
	
	@Override
	public Comparable getID() {
		if (id == null) {
			id = controller.getUid() + ":" + getSpectrumIdentification().getId();
		}
		return id;
	}
	
	
	@Override
	public SpectrumIdentification getSpectrumIdentification() {
		return controller.getPeptideByIndex(proteinID, peptideID).getSpectrumIdentification();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || !(obj instanceof IntermediatePeptideSpectrumMatch)) return false;
		
        IntermediatePeptideSpectrumMatch psm = (IntermediatePeptideSpectrumMatch)obj;
		
        if (!getID().equals(psm.getID())) return false;
		return getSpectrumIdentification().equals(psm.getSpectrumIdentification());
	}
	
	
	@Override
	public int hashCode() {
        int result = getID().hashCode();
        result = 31 * result + peptideID.hashCode();
        result = 31 * result + proteinID.hashCode();
        return result;
	}
}
