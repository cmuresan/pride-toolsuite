package uk.ac.ebi.pride.pia;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import uk.ac.ebi.pride.pia.modeller.protein.inference.AbstractProteinInference;
import uk.ac.ebi.pride.pia.modeller.protein.inference.InferenceProteinGroup;
import uk.ac.ebi.pride.pia.modeller.protein.inference.OccamsRazorInference;
import uk.ac.ebi.pride.pia.modeller.protein.inference.ReportAllInference;

public class ProteinInferenceTest {
	
	private DataAccessController dataAccessController = null;
	
	@Before
	public void setUp() throws Exception {
		//URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("small.mzid");
		//URL url = new URL("file:/mnt/data/uniNOBACKUP/PSI/protein_grouping/results/PIA/mascot/Rosetta_peak_list_2a_-_neat.mzid");
		//URL url = new URL("file:/mnt/data/uniNOBACKUP/PIA/testfiles/report-proteins-report_all-55merge_mascot_full.mzid");
        //URL url = ProteinInferenceTest.class.getClassLoader().getResource("PRIDE_Exp_Complete_Ac_10885.xml");
        
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
		
		AbstractProteinInference proteinInference =
				new OccamsRazorInference(dataAccessController, 4);
		
		List<InferenceProteinGroup> interferenceGroups = 
				proteinInference.calculateInference(true);
		
		List<ProteinGroup> proteinGroups =
				proteinInference.createProteinGroups(interferenceGroups);
		
		System.out.println("Protein groups: " + proteinGroups.size());
		
		for (ProteinGroup group : proteinGroups) {
			
			StringBuilder accessions = new StringBuilder();
			Set<SpectrumIdentification> psms = new HashSet<SpectrumIdentification>();
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
					psms.add(pep.getSpectrumIdentification());
				}
			}
			
			StringBuilder psmSB = new StringBuilder();
			for (SpectrumIdentification psm : psms) {
				psmSB.append('\n');
				psmSB.append('\t');
				psmSB.append(psm.getId());
			}
			
			
			System.out.println(accessions.toString() + psmSB.toString());
		}
		
	}
}
