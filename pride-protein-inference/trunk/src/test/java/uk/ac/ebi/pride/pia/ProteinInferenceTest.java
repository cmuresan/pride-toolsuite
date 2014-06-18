package uk.ac.ebi.pride.pia;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.MzIdentMlControllerImplTest;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.DataImportController;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructure;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructureCreator;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructureCreatorWorkerThread;
import uk.ac.ebi.pride.pia.intermediate.impl.PrideImportController;
import uk.ac.ebi.pride.pia.modeller.fdr.FDRUtilities;
import uk.ac.ebi.pride.pia.modeller.filter.AbstractFilter;
import uk.ac.ebi.pride.pia.modeller.filter.FilterComparator;
import uk.ac.ebi.pride.pia.modeller.filter.psm.PSMAccessionsFilter;
import uk.ac.ebi.pride.pia.modeller.filter.psm.PSMDecoyFilter;
import uk.ac.ebi.pride.pia.modeller.filter.psm.PSMQValueFilter;
import uk.ac.ebi.pride.pia.modeller.filter.psm.PSMScoreFilter;
import uk.ac.ebi.pride.pia.modeller.protein.inference.AbstractProteinInference;
import uk.ac.ebi.pride.pia.modeller.protein.inference.InferenceProteinGroup;
import uk.ac.ebi.pride.pia.modeller.protein.inference.OccamsRazorInference;
import uk.ac.ebi.pride.pia.modeller.protein.inference.ReportAllInference;
import uk.ac.ebi.pride.pia.modeller.scores.CvScore;
import uk.ac.ebi.pride.pia.modeller.scores.IntermediatePSMComparator;
import uk.ac.ebi.pride.pia.modeller.scores.peptide.PeptideScoring;
import uk.ac.ebi.pride.pia.modeller.scores.peptide.PeptideScoringUseBestPSM;
import uk.ac.ebi.pride.pia.modeller.scores.protein.ProteinScoring;
import uk.ac.ebi.pride.term.CvTermReference;

public class ProteinInferenceTest {
	
	/** logger for this class */
	private static final Logger logger = Logger.getLogger(ProteinInferenceTest.class);
	
	private DataAccessController dataAccessController = null;
	
	@Before
	public void setUp() throws Exception {
		//URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("small.mzid");
		//URL url = new URL("file:/mnt/data/uniNOBACKUP/PSI/protein_grouping/results/PIA/mascot/Rosetta_peak_list_2a_-_neat.mzid");
		//URL url = ProteinInferenceTest.class.getClassLoader().getResource("report-proteins-report_all-55merge_mascot_full.mzid");
		//URL url = ProteinInferenceTest.class.getClassLoader().getResource("PRIDE_Exp_Complete_Ac_10885.xml");
		//URL url = new URL("file:/mnt/data/uniNOBACKUP/PIA/testfiles/report-proteins-Test.mzid");
		URL url = new URL("file:/mnt/data/uniNOBACKUP/PIA/testfiles/report-proteins-Joana_10153.mzid");
		
		if (url == null) {
		    throw new IllegalStateException("no file for input found!");
		}
		File inputFile = new File(url.toURI());
		
		dataAccessController = new MzIdentMLControllerImpl(inputFile, true);
		//dataAccessController = new PrideXmlControllerImpl(inputFile);
	}
	
	@After
	public void tearDown() throws Exception {
		dataAccessController.close();
	}
	
	@Test
	public void testProteinGroup() throws Exception {
		
		// ---------------------------------------------------------------------
		// some testing variables
		//
		int allowedThreads = 4;
		boolean considerModifications = false;
		boolean filterPSMsOnImport = false;
		String fdrScoreAccession = CvTermReference.MS_MASCOT_SCORE.getAccession();
		boolean oboLookup = false;
		
		
		// ---------------------------------------------------------------------
		// set up some filters
		//
		List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
		
		AbstractFilter filter =
				new PSMScoreFilter(FilterComparator.less_equal, 0.01, false, CvScore.PSI_PSM_LEVEL_FDRSCORE.getAccession(), oboLookup);
		filters.add(filter);
		
		filter = new PSMDecoyFilter(FilterComparator.equal, false, false);
		filters.add(filter);
		
		//filter = new PSMQValueFilter(FilterComparator.less_equal, 0.01, false);
		//filters.add(filter);
		
		
		// ---------------------------------------------------------------------
		// first create the intermediate structure from the data given by the controller
		//
        IntermediateStructureCreator structCreator =
        		new IntermediateStructureCreator(allowedThreads);
		
        DataImportController importController;
        if (filterPSMsOnImport) {
        	importController = new PrideImportController(dataAccessController, filters);
        } else {
        	importController = new PrideImportController(dataAccessController);
        }
        
		logger.info("start importing data from the controller");
        importController.addAllSpectrumIdentificationsToStructCreator(structCreator);
        
        
        logger.info("creating intermediate structure with\n\t"
				+ structCreator.getNrSpectrumIdentifications() + " spectrum identifications\n\t"
				+ structCreator.getNrPeptides() + " peptides\n\t"
				+ structCreator.getNrProteins() + " protein accessions");
		
		IntermediateStructure intermediateStructure = structCreator.buildIntermediateStructure();
		// the creator is no longer needed
		structCreator = null;
		
		
		// ---------------------------------------------------------------------
		// calculate FDR
		
		// sort the PSMs by score
		logger.info("sorting PSMs by score");
		List<IntermediatePeptideSpectrumMatch> psms =
				new ArrayList<IntermediatePeptideSpectrumMatch>(intermediateStructure.getAllIntermediatePSMs());
		logger.info("   obtained PSMs for sorting");
		
		Collections.sort(psms, new IntermediatePSMComparator(fdrScoreAccession, oboLookup));
		logger.info("   sorting done");
		
		// then calculate the FDR and FDRScore
		PSMAccessionsFilter decoyFilter = new PSMAccessionsFilter(FilterComparator.regex_only, "s.*", false);
		int nrDecoys = FDRUtilities.markDecoys(psms, decoyFilter);
		logger.info("   decoys marked (" + nrDecoys + "/" + psms.size() + ")");
		
		FDRUtilities.calculateFDR(psms, fdrScoreAccession);
		logger.info("   fdr calculation done");
		
		FDRUtilities.calculateFDRScore(psms,  fdrScoreAccession, false);
		logger.info("   FDRScore calculation done");
		
		psms = null;	// this list is no longer needed
		
		
		// ---------------------------------------------------------------------
		// perform the protein inference
		//
		PeptideScoring pepScoring = null;
		//		new PeptideScoringUseBestPSM(CvTermReference.MS_MASCOT_SCORE.getAccession(), oboLookup);
		ProteinScoring protScoring = null;
		
		AbstractProteinInference proteinInference =
				new OccamsRazorInference(intermediateStructure, pepScoring, protScoring, filters, allowedThreads);
		
		List<InferenceProteinGroup> inferenceGroups = 
				proteinInference.calculateInference(considerModifications);
		
		logger.info("inferred  groups: " + inferenceGroups.size());
        
        
		// ---------------------------------------------------------------------
		// create the ProteinGroups from the inference groups
		//
		List<ProteinGroup> proteinGroups =
				proteinInference.createProteinGroups(inferenceGroups);
		
		logger.info("Protein groups: " + proteinGroups.size());
		
		
        
		// ---------------------------------------------------------------------
		// print out some information
		//
		/*
        for (ProteinGroup group : proteinGroups) {
			
			StringBuilder accessions = new StringBuilder();
			Set<SpectrumIdentification> psmSet = new HashSet<SpectrumIdentification>();
			for (Protein protein : group.getProteinDetectionHypothesis()) {
				if (accessions.length() > 0) {
					accessions.append(',');
				}
				
				if (!protein.isPassThreshold()) {
					accessions.append('[');
					accessions.append(protein.getDbSequence().getAccession());
					accessions.append(']');
				} else {
					accessions.append(protein.getDbSequence().getAccession());
				}
				
				
				
				for (Peptide pep : protein.getPeptides()) {
					psmSet.add(pep.getSpectrumIdentification());
				}
			}
			
			StringBuilder psmSB = new StringBuilder();
			for (SpectrumIdentification psm : psmSet) {
				psmSB.append('\n');
				psmSB.append('\t');
				psmSB.append(psm.getId());
			}
			
			
			logger.info(accessions.toString() + psmSB.toString());
		}
		*/
	}
}
