package uk.ac.ebi.pride.pia;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.MzIdentMlControllerImplTest;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.core.DBSequence;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.PeptideEvidence;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.ProteinGroup;
import uk.ac.ebi.pride.data.core.SpectrumIdentification;
import uk.ac.ebi.pride.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructure;
import uk.ac.ebi.pride.pia.intermediate.IntermediateStructureCreator;
import uk.ac.ebi.pride.pia.modeller.protein.inference.AbstractProteinInference;
import uk.ac.ebi.pride.pia.modeller.protein.inference.ReportAllInference;

public class ReportAllInferenceTest {
	
	private DataAccessController dataAccessController = null;
	
	@Before
	public void setUp() throws Exception {
		//URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("small.mzid");
		//URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("55merge_mascot_full.mzid");
		URL url = new URL("file:/mnt/data/uniNOBACKUP/PSI/protein_grouping/results/PIA/mascot/Rosetta_peak_list_2a_-_neat.mzid");
		//URL url = new URL("file:/mnt/data/uniNOBACKUP/PIA/testfiles/report-proteins-report_all-55merge.mzid");
		
		if (url == null) {
		    throw new IllegalStateException("no file for input found!");
		}
		File inputFile = new File(url.toURI());
		
		dataAccessController = new MzIdentMLControllerImpl(inputFile, true);
	}
	
	@After
	public void tearDown() throws Exception {
		dataAccessController.close();
	}
	
	@Test
	public void testProteinGroup() throws Exception {
		
		AbstractProteinInference proteinInference =
				new ReportAllInference(dataAccessController, 4);
		
		List<ProteinGroup> proteinGroups = 
				proteinInference.calculateInference(false, null);
		
		System.out.println("Proteins: " + proteinGroups.size());
	}
}
