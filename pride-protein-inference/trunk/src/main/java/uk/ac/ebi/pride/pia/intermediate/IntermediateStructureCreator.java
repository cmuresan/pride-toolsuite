package uk.ac.ebi.pride.pia.intermediate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;

/**
 * This class creates the intermediate structure needed for fast data access
 * during protein inference.
 * 
 * @author julian
 *
 */
public class IntermediateStructureCreator {
	
	/** the logger for this class */
	private static final Logger logger= Logger.getLogger(IntermediateStructureCreator.class);
	
	/** mapping from sequence to intermediatePeptide
	 * TODO: we could decide in this class, whether a peptide is defined by the sequence only or also by the mods
	 **/
	private Map<String, IntermediatePeptide> peptides;
	
	/** mapping from db entry ID to entry */
	private Map<Comparable, DBSequence> dbSequences;
	
	/** mapping from the DB entry IDs to peptides **/
	private Map<Comparable, Set<IntermediatePeptide>> proteinsToPeptidesMapping;
	
	/** mapping from the peptide sequences to db entries */
	private Map<String, Set<DBSequence>> peptidesToProteinsMapping;
	
	/** iterates over the clustered list of peptide -> dbSequence mapping */
	private ListIterator<Map<IntermediatePeptide, Set<DBSequence>>> clusterIterator;
	
	private IntermediateStructure intermediateStructure;
	
	/** the maximal number of used threads */
	private int numberThreads;
	
	
	public IntermediateStructureCreator(int threads) {
		this.peptides = new HashMap<String, IntermediatePeptide>();
		this.dbSequences = new HashMap<Comparable, DBSequence>();
		this.proteinsToPeptidesMapping =
				new HashMap<Comparable, Set<IntermediatePeptide>>();
		this.peptidesToProteinsMapping = new HashMap<String, Set<DBSequence>>();

		this.clusterIterator = null;
		this.intermediateStructure = null;
		
		this.numberThreads = threads;
	}
	
	
	public void addSpectrumIdentification(SpectrumIdentification spectrumIdentification) {
		String pepSequence = spectrumIdentification.getSequence();
		
		IntermediatePeptide peptide = peptides.get(pepSequence);
		Set<DBSequence> dbSequencesSet = null;
		
		if (peptide == null) {
			peptide = new IntermediatePeptide(pepSequence);
			peptides.put(pepSequence, peptide);
			
			dbSequencesSet = new HashSet<DBSequence>();
			peptidesToProteinsMapping.put(pepSequence, dbSequencesSet);
		} else {
			dbSequencesSet = peptidesToProteinsMapping.get(pepSequence);
		}
		peptide.addSpectrum(spectrumIdentification);
		
		for (PeptideEvidence pepEvidence : spectrumIdentification.getPeptideEvidenceList()) {
			Comparable sequenceID = pepEvidence.getDbSequence().getId();
			Set<IntermediatePeptide> peptidesSet = null;
			if (!dbSequences.containsKey(sequenceID)) {
				dbSequences.put(sequenceID, pepEvidence.getDbSequence());
				
				peptidesSet = new HashSet<IntermediatePeptide>();
				proteinsToPeptidesMapping.put(sequenceID, peptidesSet);
			} else {
				peptidesSet = proteinsToPeptidesMapping.get(sequenceID);
			}
			
			dbSequencesSet.add(pepEvidence.getDbSequence());
			
			peptidesSet.add(peptide);
		}
	}
	
	
	public Map<String, IntermediatePeptide> getPeptides() {
		return peptides;
	}
	
	
	public Map<Comparable, DBSequence> getDBSequences() {
		return dbSequences;
	}
	
	
	/**
	 * After the peptide and dbSeqeunce data is loaded, this method creates
	 * the intermediate structure.
	 * 
	 * @return
	 */
	public IntermediateStructure buildIntermediateStructure() {
		if ((peptides.size() < 1) || (dbSequences.size() < 1)) {
			logger.error("no data to build the intermediate structure!");
			return null;
		}
		
		if (intermediateStructure != null) {
			logger.error("The intermediate structure was already created!");
			return null;
		}
		
		// first cluster the data
		List<Map<IntermediatePeptide, Set<DBSequence>>> clusterList = buildClusterList();
		
		// initialize the iterator
		clusterIterator = clusterList.listIterator();
		
		// initialize the intermediate structure
		intermediateStructure = new IntermediateStructure();
		
		// start the threads
		List<IntermediateStructorCreatorWorkerThread> threads;
		threads = new ArrayList<IntermediateStructorCreatorWorkerThread>(numberThreads);
		for (int i = 0; i < numberThreads; i++) {
			IntermediateStructorCreatorWorkerThread thread =
					new IntermediateStructorCreatorWorkerThread(i, this);
			threads.add(thread);
			
			thread.start();
		}
		
		// wait for the threads to finish
		for (IntermediateStructorCreatorWorkerThread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				logger.error("thread got interrupted!", e);
				return null;
			}
		}
		
		logger.debug("intermediate structure contains "
				+ intermediateStructure.getNrClusters() + " clusters and "
				+ intermediateStructure.getNrGroups() + " groups");
		
		return intermediateStructure;
	}
	
	
	/**
	 * Creates mappings from peptide sequences to DBSequences, which are
	 * disjoint.
	 */
	private List<Map<IntermediatePeptide, Set<DBSequence>>> buildClusterList() {
		logger.info("start sorting clusters");
		
		// list of mappings from peptide to DB entries
		List<Map<IntermediatePeptide, Set<DBSequence>>> clusteredPepEntriesMap =
				new ArrayList<Map<IntermediatePeptide,Set<DBSequence>>>();
		
		for (Map.Entry<Comparable, Set<IntermediatePeptide>> entryToPepsIt
				: proteinsToPeptidesMapping.entrySet()) {
			
			if (dbSequences.containsKey(entryToPepsIt.getKey())) {
				// this accession is not yet clustered, so start a new cluster
				// and insert all the "connected" peptides and accessions
				Map<IntermediatePeptide, Set<DBSequence>> pepEntriesMapCluster =
						createCluster(entryToPepsIt.getKey());
				
				if (pepEntriesMapCluster != null) {
					clusteredPepEntriesMap.add(pepEntriesMapCluster);
				} else {
					logger.error("cluster could not be created!");
				}
				
				dbSequences.remove(entryToPepsIt.getKey());
			}
		}
		
		// the maps are no longer needed
		proteinsToPeptidesMapping = null;
		peptidesToProteinsMapping = null;
		
		logger.info(clusteredPepEntriesMap.size() + " sorted clusters");
		return clusteredPepEntriesMap;
	}
	
	
	/**
	 * Inserts the cluster of the given accession into the peptide accession
	 * map cluster.
	 * <p>
	 * This method should only be called by
	 * {@link IntermediateStructureCreator#buildClusterList()}
	 */
	private Map<IntermediatePeptide, Set<DBSequence>> createCluster(Comparable dbSequenceID) {
		
		Map<Comparable, DBSequence> clusterEntries = new HashMap<Comparable, DBSequence>();
		Map<String, IntermediatePeptide> clusterPeptides = new HashMap<String, IntermediatePeptide>();
		
		int nrNewDBSequences = 1; // the given dbSequence is new 
		int nrNewPeptides = 0;
		
		// initialize the cluster's peptides with the peptides of the given dbSequence
		for (IntermediatePeptide pep : proteinsToPeptidesMapping.get(dbSequenceID)) {
			clusterPeptides.put(pep.getSequence(), pep);
			nrNewPeptides++;
		}
		
		// put the entries and peptides into the cluster and remove them from the original maps
		while ((nrNewDBSequences > 0) || (nrNewPeptides > 0)) {
			// repeat as long, as we get more accessions or peptides
			nrNewDBSequences = 0;
			nrNewPeptides = 0;
			
			// get accessions for peptides, which are new since the last loop
			for (String pepKey : clusterPeptides.keySet()) {
				if (peptides.containsKey(pepKey)) {
					for (DBSequence dbSequence : peptidesToProteinsMapping.get(pepKey)) {
						if (!clusterEntries.containsKey(dbSequence.getId())) {
							clusterEntries.put(dbSequence.getId(), dbSequence);
							nrNewDBSequences++;
						}
					}
					peptides.remove(pepKey);
				}
			}
			
			// get peptides for accessions, which are new since the last loop
			for (Comparable seqID : clusterEntries.keySet()) {
				if (dbSequences.containsKey(seqID)) {
					for (IntermediatePeptide pep : proteinsToPeptidesMapping.get(seqID)) {
						if (!clusterPeptides.containsKey(pep.getSequence())) {
							clusterPeptides.put(pep.getSequence(), pep);
							nrNewPeptides++;
						}
					}
					
					dbSequences.remove(seqID);
				}
			}
		}
		clusterEntries = null;
		
		// now we have the whole cluster, so put it into the pepAccMapCluster
		Map<IntermediatePeptide, Set<DBSequence>> pepEntrieMapCluster =
				new HashMap<IntermediatePeptide, Set<DBSequence>>(clusterPeptides.size());
		
		for (IntermediatePeptide pep : clusterPeptides.values()) {
			pepEntrieMapCluster.put(pep, peptidesToProteinsMapping.get(pep.getSequence()));
		}
		
		return pepEntrieMapCluster;
	}
	
	
	/**
	 * Returns the next cluster in the clustered mapping of peptides to
	 * DBSequences.
	 * 
	 * @return
	 */
	protected synchronized Map<IntermediatePeptide, Set<DBSequence>> getNextCluster() {
		synchronized (clusterIterator) {
			if (clusterIterator != null) {
				if (clusterIterator.hasNext()) {
					return clusterIterator.next();
				} else {
					return null;
				}
			} else {
				logger.error("The cluster iterator is not yet initialized!");
				return null;
			}
		}
	}
	
	
	/**
	 * Adds the given cluster information into the intermediate structure.
	 * 
	 * @param cluster
	 */
	protected synchronized void addCluster(Collection<IntermediateGroup> cluster) {
		synchronized (intermediateStructure) {
			intermediateStructure.addCluster(cluster);
		}
	}
}
