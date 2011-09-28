package uk.ac.ebi.pride.data.controller.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.coreIdent.*;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: local_admin
 * Date: 28/09/11
 * Time: 09:55
 */
public class MzIdentMlControllerImplTest {

    private MzIdentMLControllerImpl mzIdentMlController = null;

    @Before
    public void setUp() throws Exception {
        URL url = MzIdentMlControllerImplTest.class.getClassLoader().getResource("55merge_mascot_full.mzid");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        mzIdentMlController = new MzIdentMLControllerImpl(inputFile);
    }

    @After
    public void tearDown() throws Exception {
        mzIdentMlController.close();
    }


    @Test
    public void testGetSamples() throws Exception {
        List<Sample> samples = mzIdentMlController.getSamples();
        assertTrue("There should be only one sample", samples.size() == 1);
        assertEquals("Sample ID should always be sample1", samples.get(0).getId(), "sample1");
        assertEquals("Sample cv param should be lung", samples.get(0).getCvParams().get(0).getName(), "lung");
    }

    @Test
    public void testGetSoftware() throws Exception {
        List<Software> software = mzIdentMlController.getSoftwareList();
        assertTrue("There should be only one software", software.size() == 1);
        assertEquals("Software ID should be Xcalibur", software.get(0).getName(), "Xcalibur");
        assertEquals("Software version should be 1.2 SP1", software.get(0).getVersion(), "1.2 SP1");
    }

    @Test
    public void testGetMetaData() throws Exception {
        ExperimentMetaData experiment = (ExperimentMetaData) mzIdentMlController.getExperimentMetaData();

        // test additional param
        List<CvParam> additional = experiment.getCvParams();
        assertTrue("There should be only two additional cv parameters", additional.size()==2);
        assertEquals("XML generation software accession should be PRIDE:0000175", additional.get(0).getAccession(), "PRIDE:0000175");

        // test references
        List<Reference> references = experiment.getReferences();
        assertTrue("There should be only one reference", references.size()==2);
        assertEquals("PubMed number should be 16038019", references.get(0).getCvParams().get(0).getAccession(), "16038019");

        // test protocol
        ExperimentProtocol protocol = experiment.getProtocol();
        assertEquals("Protocol name is In Gel Protein Digestion", protocol.getName(), "In Gel Protein Digestion");
        assertEquals("First protocol step is reduction", protocol.getProtocolSteps().get(0).getCvParams().get(0).getName(), "Reduction");

        // test version
        assertEquals("Version should be 2.1", experiment.getVersion(), "2.1");
    }

}
