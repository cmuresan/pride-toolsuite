package uk.ac.ebi.pridemod.controller.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pridemod.model.PTM;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * DataAccessController Test
 * yperez
 */
public class PSIModDataAccessControllerTest {

    private PSIModDataAccessController psiModDataAccessController = null;

    @Before
    public void setUp() throws Exception {
        URL url = PSIModDataAccessControllerTest.class.getClassLoader().getResource("PSI-MOD.obo");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        psiModDataAccessController = new PSIModDataAccessController(inputFile);
    }


    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void TestGetPTms() {
        List<PTM> ptms = psiModDataAccessController.getPTMListByPatternDescription("Phospho");
        assertTrue("Number of PTMs with Term 'Phospho' in name:", ptms.size() == 72);
    }

    @Test
    public void TestGetMod(){
        PTM ptm = psiModDataAccessController.getPTMbyAccession("MOD:00036");
        assertTrue("Difference mass for Average mass is:", ptm.getAveDeltaMass() == 16.0);
    }
}
