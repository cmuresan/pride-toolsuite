package uk.ac.ebi.pride.pia.modeller.protein.inference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.data.core.Score;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.intermediate.IntermediateProtein;

/**
 * This group bundles the information about an inferred protein group.
 * 
 * @author julian
 *
 */
public class InferenceProteinGroup {
	
	/** a unique identifier */
	private String ID;
	
	/** the proteins in this group */
	private Set<IntermediateProtein> proteins;
	
	/** the identifications leading to this group */
	private Set<IntermediatePeptideSpectrumMatch> spectrumIdentifications;
	
	/** any subgroups */
	private Set<InferenceProteinGroup> subGroups;
	
	
	/**
	 * Basic constructor
	 *  
	 * @param sequence
	 */
	public InferenceProteinGroup(String id) {
		this.ID = id;
		this.proteins = new HashSet<IntermediateProtein>();
		this.spectrumIdentifications = new HashSet<IntermediatePeptideSpectrumMatch>();
		this.subGroups = new HashSet<InferenceProteinGroup>();
	}
	
	
	public String getID() {
		return ID;
	}
	
	
	/**
	 * Adds one protein to the set of proteins
	 * @param protein
	 * @return
	 */
	public boolean addProtein(IntermediateProtein protein) {
		return proteins.add(protein);
	}
	
	
	/**
	 * Returns the set of proteins
	 * @return
	 */
	public Set<IntermediateProtein> getProteins() {
		return proteins;
	}
	
	
	/**
	 * Adds one IntermediatePeptideSpectrumMatch to the set of spectrum identifications
	 * @param specID
	 * @return
	 */
	public boolean addPeptideSpectrumMatch(IntermediatePeptideSpectrumMatch specID) {
		return spectrumIdentifications.add(specID);
	}
	
	
	/**
	 * Adds a collection of IntermediatePeptideSpectrumMatches to the set of spectrum identifications
	 * @param specID
	 * @return
	 */
	public boolean addSpectrumIdentifications(Collection<IntermediatePeptideSpectrumMatch> specIDs) {
		return spectrumIdentifications.addAll(specIDs);
	}
	
	
	/**
	 * Returns the set of IntermediatePeptideSpectrumMatches
	 * @return
	 */
	public Set<IntermediatePeptideSpectrumMatch> getSpectrumIdentifications() {
		return spectrumIdentifications;
	}
	
	
	/**
	 * Adds a protein group to the subGroups of this group
	 * @param proteinGroup
	 * @return
	 */
	public boolean addSubgroup(InferenceProteinGroup proteinGroup) {
		return subGroups.add(proteinGroup);
	}
	
	
	/**
	 * Returns the set of sub-groups
	 * @return
	 */
	public Set<InferenceProteinGroup> getSubGroups() {
		return subGroups;
	}
	
	
	/**
	 * Creates a {@link ProteinGroup} (a protein ambiguity group in mzIdentML)
	 * from this inferred protein group.
	 * @return
	 */
	public ProteinGroup createProteinGroup() {
		Set<DBSequence> dbSequences = new HashSet<DBSequence>();
		for (IntermediateProtein interProt : proteins) {
			dbSequences.add(interProt.getDBSequence());
		}
		
		Set<Peptide> peptides = new HashSet<Peptide>();
		for (IntermediatePeptideSpectrumMatch psm : spectrumIdentifications) {
			SpectrumIdentification specID = psm.getSpectrumIdentification();
			for (PeptideEvidence pepEvidence : specID.getPeptideEvidenceList()) {
				Peptide pep = new Peptide(pepEvidence, specID);
				peptides.add(pep);
			}
		}
		
		List<Protein> proteins = new ArrayList<Protein>();
		for (DBSequence dbSeq : dbSequences) {
			Score score = null;
			double sequenceCoverage = -1; // TODO: include calculation of this coverage
			
			Protein protein = new Protein(
					ID + "_" + dbSeq.getAccession(),
					dbSeq.getAccession(),
					dbSeq,
					true /*passThreshold*/,
					new ArrayList<Peptide>(peptides),
					score,
					-1 /*threshold*/,
					sequenceCoverage,
					null /*gel*/);
			
			
			proteins.add(protein);
		}
		
		// the sub-groups proteins will become proteins which do not pass the threshold
		for (InferenceProteinGroup subGroup : subGroups) {
			ProteinGroup subProteinGroup = subGroup.createProteinGroup();
			
			for (Protein subProtein : subProteinGroup.getProteinDetectionHypothesis()) {
				subProtein.setId(ID + "_" + subProtein.getDbSequence().getAccession());
				subProtein.setPassThreshold(false);
				proteins.add(subProtein);
			}
		}
		
		return new ProteinGroup(ID, ID, proteins);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
		
		InferenceProteinGroup group = (InferenceProteinGroup)obj;
		
		if (ID != group.ID) return false;
		return !((proteins != null) ? !proteins.equals(group.proteins) : (group.proteins != null)) &&
				!((spectrumIdentifications != null) ? !spectrumIdentifications.equals(group.spectrumIdentifications) : (group.spectrumIdentifications != null)) &&
				!((subGroups != null) ? !subGroups.equals(group.subGroups) : (group.subGroups != null));
	}
	
	
	@Override
	public int hashCode() {
        int result = ID.hashCode();
        result = 31 * result + ((proteins != null) ? proteins.hashCode() : 0);
        result = 31 * result + ((spectrumIdentifications != null) ? spectrumIdentifications.hashCode() : 0);
        result = 31 * result + ((subGroups != null) ? subGroups.hashCode() : 0);
        return result;
	}
}
