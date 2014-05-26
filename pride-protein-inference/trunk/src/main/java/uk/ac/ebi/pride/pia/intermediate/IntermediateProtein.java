package uk.ac.ebi.pride.pia.intermediate;

import uk.ac.ebi.pride.data.core.DBSequence;

/**
 * A n intermediate class, which represents a protein (in teh PRIDE case, a
 * {@link DBSequence}).
 * 
 * @author julian
 *
 */
public class IntermediateProtein {
	
	/** the representative */
	private DBSequence dbSequence;
	
	/** the connected group of this peptides */
	private IntermediateGroup group;
	
	
	
	/**
	 * Basic constructor, only initializes the representative
	 *  
	 * @param sequence
	 */
	public IntermediateProtein(DBSequence dbSequence) {
		this.dbSequence = dbSequence;
		this.group = null;
	}
	
	
	/**
	 * Getter for the representative.
	 * 
	 * @return
	 */
	public DBSequence getRepresentative() {
		return dbSequence;
	}
	
	
	/**
	 * sets the group of this protein
	 * @param group
	 */
	public void setGroup(IntermediateGroup group) {
		this.group = group;
	}
	
	
	/**
	 * returns the group of this protein
	 */
	public IntermediateGroup getGroup() {
		return group;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
		
		IntermediateProtein protein = (IntermediateProtein)obj;
		
		return !((dbSequence != null) ? !dbSequence.equals(protein.dbSequence) : (protein.dbSequence != null)) && 
				!((group != null) ? !group.getID().equals(protein.group.getID()) : (protein.group != null)); // cannot compare group (as it is dependend), instead compare group's ID
	}
	
	
	@Override
	public int hashCode() {
        int result = (dbSequence != null) ? dbSequence.hashCode() : 0;
        result = 31 * result + ((group != null) ? group.getID().hashCode() : 0);
        return result;
	}
}
