package uk.ac.ebi.pride.pia.intermediate.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.pia.intermediate.Accession;
import uk.ac.ebi.pride.pia.intermediate.Group;
import uk.ac.ebi.pride.pia.intermediate.Peptide;


/**
 * This thread builds up the intermediate structure given the peptide accession
 * map, more precisely the peptide accession map cluster. 
 * 
 * @author yperez
 *
 */
public class CompilerWorkerThread extends Thread {
	
	/** the ID of this worker thread */
	private int ID;
	
	/** the caller of this thread */
	private PIACompiler parent;
	
	/** the groups, which are processed by this thread */
	private Map<Long, Group> threadGroups;
	
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(CompilerWorkerThread.class);
	
	
	public CompilerWorkerThread(int ID, PIACompiler parent) {
		this.ID = ID;
		this.parent = parent;
		this.threadGroups = new HashMap<Long, Group>();
		
		this.setName("PIA-Worker-"+ID);
	}
	
	
	@Override
	public void run() {
		int workedClusters = 0;
		long nrThreadGroups = 0;
		long threadGroupOffset = 0;
		Map<String, Map<String, Accession>> cluster;
		
		// get the next available cluster from the parent
		cluster = parent.getNextCluster();
		while (cluster != null) {
			Map<Long, Group> subGroups = new HashMap<Long, Group>();
			
			for (Map.Entry<String, Map<String, Accession>> pepIt : cluster.entrySet()) {
				Peptide peptide = parent.getPeptide(pepIt.getKey());
				
				InsertIntoMap(peptide, pepIt.getValue(), subGroups);
			}
			
			// merge the groups into the thread groups
			workedClusters++;
			for (Group group : subGroups.values()) {
				nrThreadGroups++;
				group.setOffset(threadGroupOffset);
				group.setTreeID(workedClusters);
				threadGroups.put(nrThreadGroups, group);
			}
			threadGroupOffset = nrThreadGroups;
			
			parent.increaseBuildProgress();
			cluster = parent.getNextCluster();
		}
		
		logger.info(" <thread " + ID + " has no more work after " +
				workedClusters + " clusters> ");
		
		parent.mergeClustersIntoMap(threadGroups, workedClusters);
	}
	
	
	/**
	 * Inserts the given peptide with its accessions into the intermediate
	 * format, which is until then build up by the subGroups.
	 * 
	 * @param peptide
	 * @param accessions
	 * @param subGroups
	 */
	public void InsertIntoMap(Peptide peptide,
			Map<String, Accession> accessions, Map<Long, Group> subGroups) {
		
		Map<Long, Map<String, Accession>> groupAccMap;	// the accessions, grouped by their groups
		
		// group the accessions by the groups they are in
		groupAccMap = new HashMap<Long, Map<String,Accession>>();
		for (Accession accession : accessions.values()) {
			Long id;
			
			if (accession.getGroup() != null) {
				id = accession.getGroup().getID();
			} else {
				id = -1L;
			}
			
			Map<String, Accession> groupsAcc = groupAccMap.get(id);
			if (groupsAcc == null) {
				groupsAcc = new HashMap<String, Accession>();
				groupAccMap.put(id, groupsAcc);
			}
			
			groupsAcc.put(accession.getAccession(), accession);
		}
		
		
		if ((groupAccMap.size() == 1) && (groupAccMap.containsKey(-1L))) {
			// all accessions are not yet assigned to any group
		    //  => assign all to a new group
			
			// create the new group
			Group group = new Group(subGroups.size()+1);
			subGroups.put(group.getID(), group);
			
			// connect peptide and group
			connectPeptideToGroup(peptide, group);
			
			// add all accessions to this new group
			for (Accession accession : accessions.values()) {
				connectAccessionToGroup(accession, group);
			}
		} else {
			if (groupAccMap.size() == 1) {
				// all accessions have the same group (but are already assigned)
				// get group of the accessions
				
				Group group = null;
				for (Long id : groupAccMap.keySet()) {
					// there is only one id, so get this group
					group = subGroups.get(id);
				}
				
				// look, if the group of the accessions has any other accessions
				if (groupHasNoOtherAccessions(group, accessions)) {
					// the group of the accessions has only the accessions to be
					// assigned to the peptide
			        //   => add the peptide to the group
					connectPeptideToGroup(peptide, group);
				} else {
			        // the group of the accessions has NOT only the accessions
					// to be assigned to the peptide
			        //   => create a group with the peptide and the accessions
					//      group and move the accessions there
					
			        // create the new group
					Group betweenGroup = new Group(subGroups.size()+1);
					subGroups.put(betweenGroup.getID(), betweenGroup);
					
			        // add group to peptide and vice versa
					connectPeptideToGroup(peptide, betweenGroup);

			        // add the old group to new group as child
					connectGroups(betweenGroup, group);
					
					for (Accession accession : groupAccMap.get(group.getID()).values()) {
						connectAccessionToGroup(accession, betweenGroup);
					}
				}
			} else {
				// (accGrouped.size() != 1)
				// the accessions are in different groups / not yet assigned
				
				Set<Long> remainingGroups = new HashSet<Long>();
				Set<Long> subTreeSet = getSubtreeGroups(accessions,
						remainingGroups, subGroups);
				
				if ((remainingGroups.size() == 0) &&
						(((subTreeSet.size() == 1) && !subTreeSet.contains(-1L)) ||			// either there is only one group (and it's not -1, the unassigned)
								((subTreeSet.size() == 2) && subTreeSet.contains(-1L)))) {	// or there are 2 groups and one of it are the unassigned accessions
					// the already assigned accessions build up a whole subtree
					
			        // get the group building up the subtree (the one with the assigned)
					Group group = null;
					for (Long id : subTreeSet) {
						if (id > 0) {
							group = subGroups.get(id);
						}
					}
					
					if (group != null) {
						if (groupAccMap.containsKey(-1L)) {
							// we have some unassigned accessions as well
							
							// create a between group
							Group betweenGroup = new Group(subGroups.size()+1);
							subGroups.put(betweenGroup.getID(), betweenGroup);
							
							// add the unassigned accessions to the between group
							for (Accession acc : groupAccMap.get(-1L).values()) {
								connectAccessionToGroup(acc, betweenGroup);
							}
							
							// add the new group as child to the group
							connectGroups(group, betweenGroup);
							
							group = betweenGroup;
						}
						
						// add group to peptide and vice versa
						connectPeptideToGroup(peptide, group);
					} else {
						logger.error("There should have been a group for the accessions!");
					}
				} else {
					// can't say much about the constellation of groups
					
					// create new group for peptide
					Group pepGroup = new Group(subGroups.size()+1);
					subGroups.put(pepGroup.getID(), pepGroup);
					
					// add group to peptide and vice versa
					connectPeptideToGroup(peptide, pepGroup);
					
					// add the new group to all subTreeSet-groups as new child
					
					for (Long subTreeId : subTreeSet) {
						if (subTreeId == -1L) {
							// if we have unassigned accessions, add them
							// directly to the pepGroup
							for (Accession accession: groupAccMap.get(-1L).values()) {
								connectAccessionToGroup(accession, pepGroup);
							}
						} else {
							// add the pepGroup to the other (not unassigned
							// accessions) groups
							connectGroups(subGroups.get(subTreeId), pepGroup);
						}
					}
					
					// move the accessions of the remainingGroups into a new
					// group, pointing to the pepGroup and the old group
					for (Long remGroupId : remainingGroups) {
						// the "remaining" group
						Group group = subGroups.get(remGroupId);
						
						// create an between group
						Group betweenGroup = new Group(subGroups.size()+1);
						subGroups.put(betweenGroup.getID(), betweenGroup);
						
						// connect the between group to the remaining and the pepGroup
						connectGroups(betweenGroup, group);
						connectGroups(betweenGroup, pepGroup);
						
						// move the accessions to the between group
						for (Accession accession : groupAccMap.get(remGroupId).values()) {
							connectAccessionToGroup(accession, betweenGroup);
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Connects the given peptide with the given group. A warning is given, if
	 * the peptide was connected to another group.
	 *  
	 * @param peptide
	 * @param group
	 */
	private void connectPeptideToGroup(Peptide peptide, Group group) {
		if (peptide.getGroup() != null) {
			logger.warn("peptide " + peptide.getSequence() +
					" was already connected to a group!");
		}
		peptide.setGroup(group);
		group.addPeptide(peptide);
	}
	
	
	/**
	 * Connects the given accession with the given group.<br/>
	 * If the accession was connected to another group before, it will be
	 * removed there.
	 * 
	 * @param accession
	 * @param group
	 */
	private void connectAccessionToGroup(Accession accession, Group group) {
		if ((accession.getGroup() != null) &&
				!accession.getGroup().equals(group)) {
			accession.getGroup().removeAccession(accession);
		}
		
		accession.setGroup(group);
		group.addAccession(accession);
	}
	
	
	/**
	 * Connects the two given groups.
	 * 
	 * @param parent
	 * @param child
	 */
	private void connectGroups(Group parent, Group child) {
		parent.addChild(child);
		child.addParent(parent);
	}
	
	
	/**
	 * Checks if the group has any accessions which are not the given map.
	 * 
	 * @param group
	 * @param accessions
	 * @return
	 */
	private boolean groupHasNoOtherAccessions(Group group,
			Map<String, Accession> accessions) {
		
		// if the group has parent-groups, it has (by default) other accessions
		if (group.getParents().size() > 0) {
			return false;
		}
		
		// iterate through the map of the group's accessions and look for
		// accessions not in accessions
		for (String accStr : group.getProteinIds().keySet()) {
			if (!accessions.containsKey(accStr)) {
				// this accession is not in the map!
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Checks if the group has any accessions in allAccessions which are not in
	 * the accessions map.
	 * 
	 * @param group
	 * @param accessions
	 * @return
	 */
	private boolean groupHasNoOtherInAllAccessions(Group group,
			Map<String, Accession> accessions) {
		// iterate through the map of the group's allAccessions and look for
		// accessions not in accessions
		for (String accStr : group.getAllAccessions().keySet()) {
			if (!accessions.containsKey(accStr)) {
				// this accession is not in the map!
				return false;
			}
		}
		
		return true;
	}
	
	
	/**
	 * Builds a set with the group IDs which are the lowest in the tree to build
	 * up the subtrees with only the accessions in the given set.<br/>
	 * The variable remainingSet will be filled with the group IDs of
	 * accessions, which are not satisfied with the build tree, so their groups
	 * have other accessions which are not in the set of accessions.<br/>
	 * 
	 * @param accessions
	 * @param remainingSet
	 * @param subGroups
	 * @return
	 */
	private Set<Long> getSubtreeGroups(Map<String, Accession> accessions,
			Set<Long> remainingSet, Map<Long, Group> subGroups) {
		Set<Long> subTreeSet = new HashSet<Long>();
		long mostId;
		long mostAccessions;
		Group mostGroup = null;
		
		// look for unassigned accessions
		for (Accession acc : accessions.values()) {
			if (acc.getGroup() == null) {
				// if we have (at least) one unassigned accession, insert the -1 group
				subTreeSet.add(-1L);
				break;
			}
		}
		
		do {
			mostId = -1;
			mostAccessions = 0;
			
			// get the group (in subGroups) with the most accessions in its
			// allAccessions, which are also in the accessions map
			for (Group group : subGroups.values()) {
				if (groupHasNoOtherInAllAccessions(group, accessions)) {
					if ((group.getAllAccessions().size() > mostAccessions) ||
							((mostId == -1) && (group.getAllAccessions().size() > 0))) {
						mostAccessions = group.getAllAccessions().size();
						mostId = group.getID();
						mostGroup = group;
					}
				}
			}
			
			// remove the accessions of the found group from the accessions set
			if ((mostId > 0) && (mostGroup != null)) {
				for (String accStr : mostGroup.getAllAccessions().keySet()) {
					accessions.remove(accStr);
				}
				
				// and add the found ID to the subTreeSet
				subTreeSet.add(mostId);
			}
		} while (mostId > 0);
		
		
		remainingSet.clear();
		for (Accession accession : accessions.values()) {
			if (accession.getGroup() != null) {
				// if there is still an accession in the map, which has an
				// assigned group, so there were accessions in the group, which
				// were not in the accessions map -> put it into the remainingSet
				remainingSet.add(accession.getGroup().getID());
			}
		}
		
		return subTreeSet;
	}
}