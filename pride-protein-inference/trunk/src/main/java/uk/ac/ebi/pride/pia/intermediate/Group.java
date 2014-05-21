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

    /** ID of the group's tree */
	private long treeID;

    /** Children groups of this group, i.e. groups where this group points to. */
	private Map<Comparable, Group> children;

	/** Parents of this group, i.e. groups pointing to this group. */
	private Map<Comparable, Group> parents;

    /** List of all peptide Ids in the group. */
    private List<Comparable> peptideIds;

    /** List of the direct proteinIds of this group. */
	private List<Comparable> proteinIds;

	/** List of all parents' and own accession. */
	private List<Comparable> allAccessions;
    /** List of Proteins in this Group
	
	
	/**
	 * Basic Constructor, sets all the maps to null and score to NaN.
	 * 
	 * @param id
	 */
	public Group(Comparable id) {
		this.ID = id;
		this.treeID = -1;
		this.peptideIds = null;
		this.children = new HashMap<Comparable, Group>();
		this.parents = new HashMap<Comparable, Group>();
		this.proteinIds = new ArrayList<Comparable>();
		this.allAccessions = new ArrayList<Comparable>();
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

        if (treeID != group.treeID) return false;
        if (!ID.equals(group.ID)) return false;
        if (!allAccessions.equals(group.allAccessions)) return false;
        if (!children.equals(group.children)) return false;
        if (!parents.equals(group.parents)) return false;
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
	 * Setter for the treeID.
	 * 
	 * @param id
	 */
	public void setTreeID(long id) {
		this.treeID = id;
	}
	
	
	/**
	 * Getter for the group's treeID.
	 * 
	 * @return
	 */
	public long getTreeID() {
		return treeID;
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
        /*
	    List<Comparable> allPeptideIds = new ArrayList<Comparable>();
		
		if (peptideIds!= null) {
			allPeptideIds.addAll(peptideIds);
		}
		
		for ( child : getAllPeptideChildren().entrySet()) {
			Map<String, Peptide> childPepMap = child.getValue().getPeptides();
			if (childPepMap != null) {
				for (Map.Entry<String, Peptide> childPeps : childPepMap.entrySet()) {
					ret.put(childPeps.getKey(), childPeps.getValue());
				}
			}
		}
		
		return ret;
		*/
        return null;
	}
	
	/**
	 * Adds a child to the children map.
	 * If the map is not yet initialized, initialize it.
	 *
	 */
	public void addChild(Group child) {
        /*
		children.put(child.getID(), child);
		
		if (allAccessions != null) {
			for (List<Comparable> acc : allAccessions.) {
				child.addToAllAccessions(acc.getValue());
			}
		}*/
	}
	
	
	/**
	 * Getter for the children.
	 * 
	 * @return
	 */
	public Map<Comparable, Group> getChildren() {
		return children;
	}
	
	
	/**
	 * Getter for all children groups of this group, including children's
	 * children and so on.
	 */
	public Map<Comparable, Group> getAllChildren(){
		Map<Comparable, Group> allChildren = new HashMap<Comparable, Group>();
		
		for (Map.Entry<Comparable, Group> cIt : children.entrySet()) {
			allChildren.put(cIt.getKey(), cIt.getValue());
			
			Map<Comparable, Group> childChildren = cIt.getValue().getAllChildren();
			for (Map.Entry<Comparable, Group> ccIt : childChildren.entrySet()) {
				allChildren.put(ccIt.getKey(), ccIt.getValue());
			}
		}
		
		return allChildren;
	}
	
	
	/**
	 * Getter for all children groups of this group that have at least one
	 * peptide, recursive, i.e. get the reporting peptide groups.
	 */
	public Map<Comparable, Group> getAllPeptideChildren(){
	/*	Map<Comparable, Group> allChildren = new HashMap<Comparable, Group>();
		Map<Comparable, Group> childChildren;
		
		for (Map.Entry<Comparable, Group> cIt : children.entrySet()) {
			childChildren = cIt.getValue().getAllPeptideChildren();
			
			for (Map.Entry<Comparable, Group> ccIt : childChildren.entrySet()) {
				allChildren.put(ccIt.getKey(), ccIt.getValue());
			}
			
			if ((cIt.getValue(). != null) &&
					(cIt.getValue().getPeptides().size() > 0)) {
				allChildren.put(cIt.getKey(), cIt.getValue());
			}
		}
		
		return allChildren;*/
        return null;
	}


	/**
	 * Adds a new group to the map of parents.
	 * If the map is not yet initialized, initialize it.
	 *
	 * @param parent
	 */
	public void addParent(Group parent) {
	/*	parents.put(parent.getID(), parent);
		if (parent.getAllAccessions() != null) {
			for (Map.Entry<String, Accession> acc : parent.getAllAccessions().entrySet()) {
				addToAllAccessions(acc.getValue());
			}
		}*/
	}
	
	
	/**
	 * Getter for the parents.
	 * 
	 * @return
	 */
	public  Map<Comparable, Group> getParents() {
		return parents;
	}
	
	
	/**
	 * Getter for the proteinIds.
	 * 
	 * @return
	 */
	public List<Comparable> getProteinIds() {
		return proteinIds;
	}
	

	/**
	 * String getter for the peptides.
	 * 
	 * @return
	 */
	public String getPeptidesStr() {
		StringBuffer sb = new StringBuffer();
		
		if (peptideIds != null) {
			for (Comparable pep : peptideIds) {
				sb.append(pep.toString() + " ");
			}
		}
		
		for (Map.Entry<Comparable, Group> pepChild : getAllPeptideChildren().entrySet()) {
			sb.append(pepChild.getValue().getPeptidesStr());
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Adds the given offset to the own id and and the keys in the children and
	 * parent maps.<br/>
	 * This function should only be called, if all the IDs in a cluster are
	 * updated.
	 * 
	 * @param offset
	 */
	public void setOffset(Comparable offset) {
	/*	Map<Long, Group> tmpMap;
		this.ID += offset;
		
		// offset the children keys
		tmpMap = new HashMap<Long, Group>(children.size());
		for (Map.Entry<Long, Group> childrenIt : children.entrySet()) {
			tmpMap.put( childrenIt.getKey()+offset, childrenIt.getValue());
		}
		children = tmpMap;
		
		// offset the parents' keys
		tmpMap = new HashMap<Long, Group>(parents.size());
		for (Map.Entry<Long, Group> parentsIt : parents.entrySet()) {
			tmpMap.put( parentsIt.getKey()+offset, parentsIt.getValue());
		}
		parents = tmpMap;*/
	}

	
	

}
