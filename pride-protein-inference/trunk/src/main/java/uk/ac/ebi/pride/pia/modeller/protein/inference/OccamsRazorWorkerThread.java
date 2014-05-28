package uk.ac.ebi.pride.pia.modeller.protein.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.pia.modeller.report.filter.AbstractFilter;


public class OccamsRazorWorkerThread extends Thread {
	
	/** the ID of this worker thread */
	private int ID;
	
	/** the caller of this thread */
	private OccamsRazorInference parent;
	
	/** the applied inference filters */
	private List<AbstractFilter> filters;
	
	/** whether modifications are considered while inferring the peptides */
	private boolean considerModifications;
	
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(OccamsRazorWorkerThread.class);
	
	
	public OccamsRazorWorkerThread(int ID,
			OccamsRazorInference parent,
			List<AbstractFilter> filters,
			boolean considerModifications) {
		this.ID = ID;
		this.parent = parent;
		this.filters = filters;
		this.considerModifications = considerModifications;
		
		this.setName("OccamsRazorWorkerThread-" + this.ID);
	}
	
	
	@Override
	public void run() {
		int treeCount = 0;
		Set<IntermediateGroup> cluster = parent.getNextCluster();
		while (cluster != null) {
			processTree(cluster);
			treeCount++;
			cluster = parent.getNextCluster();
		}
		
		logger.info("inference worker " + ID + " finished after " + treeCount);
	}
	
	/**
	 * Do the protein inference on the cluster
	 * 
	 * @param cluster
	 */
	private void processTree(Set<IntermediateGroup> cluster) {
		// get the filtered report peptides mapping from the groups' IDs
		Map<Integer, Set<IntermediatePeptide>> groupIdToReportPeptides =
				parent.createFilteredPeptidesMap(considerModifications);
		
		// the map of actually reported proteins
		Map<Integer, ProteinGroup> proteins =
				new HashMap<Integer, ProteinGroup>(groupIdToReportPeptides.size());
		
		/*
		// maps from the protein/group IDs to the peptide keys 
		Map<Long, Set<String>> peptideKeysMap =
				new HashMap<Long, Set<String>>();
		
		// maps from the groups ID to the IDs, which have the same peptides
		Map<Long, Set<Long>> sameSetMap =
				new HashMap<Long, Set<Long>>(groupIdToReportPeptides.size());
		*/
		
		// create for each group, which has at least one peptide and accession, a protein group (i.e. in this case a list of Proteins with same peptides)
		for (IntermediateGroup group : cluster) {
			if ((group.getProteins().size() == 0) ||
					!parent.groupHasReportPeptides(group, groupIdToReportPeptides)) {
				// this group has no parents or no peptides, skip it
				continue;
			}
			
			String proteinGroupID = parent.createProteinGroupID(group);
			ProteinGroup proteinGroup = new ProteinGroup(
					proteinGroupID,
					proteinGroupID,
					new ArrayList<Protein>());
			
			
			
			Protein protein = new Protein(id, name, dbSequence, passThreshold, peptides, score, threshold, sequenceCoverage, gel);
			
			
			
			ReportProtein protein = new ReportProtein(group.getKey());
			
			// add the accessions
			for (Accession acc : group.getValue().getAccessions().values()) {
				protein.addAccession(acc);
			}
			
			// collect the peptides
			List<Peptide> peptideList = new ArrayList<Peptide>();
			Set<IntermediatePeptide> interPeptides = new HashSet<IntermediatePeptide>();
			
			interPeptides.addAll(groupIdToReportPeptides.get(group.getID()));
			for (IntermediateGroup pepGroup : group.getAllPeptideChildren()) {
				interPeptides.addAll(
						groupIdToReportPeptides.get(pepGroup.getID()));
			}
			
			for (IntermediatePeptide pep : interPeptides) {
				add the peptides' psms to the psms fo the proteins...
			}
			
			Set<String> peptideKeys = new HashSet<String>();
			pepGroupIDs.add(group.getKey());
			pepGroupIDs.addAll(
					group.getValue().getAllPeptideChildren().keySet());
			for (Long pepGroupID : pepGroupIDs) {
				if (groupIdToReportPeptides.containsKey(pepGroupID)) {
					for (ReportPeptide peptide
							: groupIdToReportPeptides.get(pepGroupID)) {
						if (!peptideKeys.add(peptide.getStringID())) {
							logger.warn(
									"Peptide already in list of peptides '" +
									peptide.getStringID() + "'");
						} else {
							protein.addPeptide(peptide);
						}
					}
				}
			}
			
			
			
			
			// get the proteins with same peptides and subgroups
			Set<Long> sameSet = new HashSet<Long>();
			for (Map.Entry<Long, Set<String>> peptideKeyIt
					: peptideKeysMap.entrySet()) {
				if (peptideKeyIt.getValue().equals(peptideKeys)) {
					sameSet.add(peptideKeyIt.getKey());
					sameSetMap.get(peptideKeyIt.getKey()).add(group.getKey());
				}
			}
			sameSetMap.put(group.getKey(), sameSet);
			
			peptideKeysMap.put(group.getKey(), peptideKeys);
			
			proteins.put(group.getID(), proteinGroup);
		}
		
		if (proteins.size() < 1) {
			// no proteins could be created (e.g. due to filters?) 
			return;
		}
		/*
		// merge proteins with same peptides
		for (Map.Entry<Long, Set<Long>> sameSetIt : sameSetMap.entrySet()) {
			Long protID = sameSetIt.getKey();
			ReportProtein protein = proteins.get(protID);
			if (protein != null) {
				// the protein is not yet deleted due to samesets
				for (Long sameID : sameSetIt.getValue()) {
					if (sameID != protID) {
						ReportProtein sameProtein = proteins.get(sameID);
						if (sameProtein != null) {
							// add the accessions of sameProtein to protein
							for (Accession acc : sameProtein.getAccessions()) {
								protein.addAccession(acc);
							}
							
							// and remove the same-protein
							proteins.remove(sameID);
							peptideKeysMap.remove(sameID);
							
							// this makes sure, the protein does not get removed, when it is iterated over sameProtein
							sameSetMap.get(sameID).remove(protID);
						}
					}
				}
			}
		}
		// the sameSetMap is no longer needed
		sameSetMap = null;
		
		// check the proteins whether they satisfy the filters
		Set<Long> removeProteins = new HashSet<Long>(proteins.size());
		for (ReportProtein protein : proteins.values()) {
			// score the proteins before filtering
			Double protScore =
					parent.getScoring().calculateProteinScore(protein); 
			protein.setScore(protScore);
			
			if (!FilterFactory.satisfiesFilterList(protein, 0L, filters)) {
				removeProteins.add(protein.getID());
			}
		}
		for (Long rID : removeProteins) {
			proteins.remove(rID);
			peptideKeysMap.remove(rID);
		}
		
		// this will be the list of reported proteins
		List<ReportProtein> reportProteins = new ArrayList<ReportProtein>();
		
		// the still unreported proteins
		HashMap<Long, ReportProtein> unreportedProteins =
				new HashMap<Long, ReportProtein>(proteins);
		
		// check proteins for sub-proteins and intersections. this cannot be
		// done before, because all proteins have to be built beforehand
		Map<Long, Set<Long>> subProteinMap =
				new HashMap<Long, Set<Long>>(groupIdToReportPeptides.size());
		Map<Long, Set<Long>> intersectingProteinMap =
				new HashMap<Long, Set<Long>>(groupIdToReportPeptides.size());
		Set<Long> isSubProtein = new HashSet<Long>();
		Set<String> reportedPeptides = new HashSet<String>();
		for (Long proteinID : proteins.keySet()) {
			Set<String> peptideKeys = peptideKeysMap.get(proteinID);
			
			Set<Long> subProteins = new HashSet<Long>();
			subProteinMap.put(proteinID, subProteins);
			
			Set<Long> intersectingProteins = new HashSet<Long>();
			intersectingProteinMap.put(proteinID, intersectingProteins);
			
			for (Long subProtID : proteins.keySet()) {
				if (proteinID == subProtID) {
					continue;
				}
				
				Set<String> intersection = new HashSet<String>(
						peptideKeysMap.get(subProtID));
				intersection.retainAll(peptideKeys);
				
				if (intersection.size() > 0) {
					if (intersection.size() ==
							peptideKeysMap.get(subProtID).size()) {
						// the complete subProtID is in proteinID
						subProteins.add(subProtID);
					} else if (intersection.size() == peptideKeys.size()) {
						// the complete proteinID is in subProtID
						isSubProtein.add(proteinID);
					} else if (intersection.size() != peptideKeys.size()) {
						// subProtID intersects proteinID somehow
						intersectingProteins.add(subProtID);
					}
				}
			}
			
			if ((intersectingProteins.size() == 0) &&
					!isSubProtein.contains(proteinID)) {
				// this protein is no subProtein and has no intersections (but
				// maybe subProteins) -> report this protein
				ReportProtein protein = proteins.get(proteinID);
				
				reportProteins.add(protein);
				reportedPeptides.addAll(peptideKeysMap.get(proteinID));
				unreportedProteins.remove(proteinID);
				
				// add the subproteins
				for (Long subID : subProteins) {
					protein.addToSubsets(proteins.get(subID));
					unreportedProteins.remove(subID);
				}
			}
		}
		
		// report all the proteins ordered by which explains the most new peptides
		while (unreportedProteins.size() > 0) {
			Set<Long> mostPepsIDs = null;
			Set<String> mostCanReport = null;
			int nrMostPeps = -1;
			
			for (ReportProtein protein : unreportedProteins.values()) {
				if (isSubProtein.contains(protein.getID())) {
					// subproteins are reported indirectly
					continue;
				}
				Set<String> canReport = peptideKeysMap.get(protein.getID());
				canReport.removeAll(reportedPeptides);
				
				if (canReport.size() > nrMostPeps) {
					mostPepsIDs = new HashSet<Long>();
					mostPepsIDs.add(protein.getID());
					nrMostPeps = canReport.size();
					mostCanReport = canReport;
				} else if ((canReport.size() == nrMostPeps) &&
						canReport.equals(mostCanReport)) {
					mostPepsIDs.add(protein.getID());
				}
			}
			
			for (Long protID : mostPepsIDs) {
				ReportProtein protein = proteins.get(protID);
				if (nrMostPeps > 0) {
					// TODO: for now, the proteins which "explain" no more peptides are not reported (this happens sometimes)
					reportProteins.add(protein);
					reportedPeptides.addAll(peptideKeysMap.get(protID));
				}
				unreportedProteins.remove(protID);
				
				// add the subproteins
				for (Long subID : subProteinMap.get(protID)) {
					protein.addToSubsets(proteins.get(subID));
					unreportedProteins.remove(subID);
				}
			}
		}
		
		if (reportProteins.size() > 0) {
			parent.addToReports(reportProteins);
		}
		*/
	}
}
