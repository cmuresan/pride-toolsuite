package uk.ac.ebi.pride.pia.intermediate;

/**
 * This class describes the occurrence of an Accession's dbSequence of a
 * Peptide.
 * 
 * @author julian
 *
 */
public class AccessionOccurrence {
	
	/** the Accession */
	String accession;
	
	/** start in the DBSequence for this occurrence */
	Integer start;
	
	/** end in the DBSequence for this occurrence */
	Integer end;
	
	
	/**
	 * Simple constructor 
	 * @param accession
	 * @param start
	 * @param end
	 */
	public AccessionOccurrence(String accession, Integer start, Integer end) {
		this.accession = accession;
		this.start = start;
		this.end = end;
	}
	
	
	public String getAccession() {
		return accession;
	}
	
	
	public Integer getStart() {
		return start;
	}
	
	
	public Integer getEnd() {
		return end;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if ( !(obj instanceof AccessionOccurrence) ) {
			return false;
		}
		
		AccessionOccurrence objAO= (AccessionOccurrence)obj;
		
	    if ((objAO.accession == this.accession) &&
	    		(((start == null) && (objAO.start == null)) ||
	    				start.equals(objAO.start)) &&
	    		(((end == null) && (objAO.end == null)) ||
	    				end.equals(objAO.end)) ) {
	    	return true;
	    } else {
	    	return false;
	    }
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		
		hash += (new Long(accession)).hashCode();
		hash += (start != null) ? start.hashCode() : 0;
		hash += (end != null) ? end.hashCode() : 0;
		
		return hash;
	}
}
