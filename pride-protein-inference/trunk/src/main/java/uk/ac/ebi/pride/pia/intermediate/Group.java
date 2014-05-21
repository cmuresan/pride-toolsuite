package uk.ac.ebi.pride.pia.intermediate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Group implements Serializable {

    private static final long serialVersionUID = 1L;
	
	/** ID of the group */
	private Comparable ID;

    /** List of all peptide Ids in the group. */
    private List<Comparable> peptideIds;

    /** List of the direct proteinIds of this group. */
	private List<Comparable> proteinIds;
	
	
	/**
	 * Basic Constructor, sets all the maps to null and score to NaN.
	 * 
	 * @param id
	 */
	public Group(Comparable id) {
		this.ID = id;
		this.peptideIds = null;
		this.proteinIds = new ArrayList<Comparable>();
	}

    /**
     * When a Protein Group Information is present in the file the
     * @param idGroup
     * @param idProteins
     */
    public Group(Comparable idGroup, List<Comparable> idProteins) {
        this(idGroup);
        this.proteinIds = idProteins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (!ID.equals(group.ID)) return false;
        if (!peptideIds.equals(group.peptideIds)) return false;
        if (!proteinIds.equals(group.proteinIds)) return false;

        return true;
    }

   	/**
	 * Getter for the ID.
	 * 
	 * @return
	 */
	public Comparable getID() {
		return this.ID;
	}

    /**
     * Setter for the peptides.
     * @param peptideIds
     */
	public void setPeptides(List<Comparable> peptideIds) {
		this.peptideIds = peptideIds;
	}
	
	/**
	 * getter for all peptides, including children's peptides.
	 * @return
	 */
	public List<Comparable> getAllPeptides() {
        return getAllPeptides();
	}

	/**
	 * Getter for the proteinIds.
	 * 
	 * @return
	 */
	public List<Comparable> getProteinIds() {
		return proteinIds;
	}



	
	

}
