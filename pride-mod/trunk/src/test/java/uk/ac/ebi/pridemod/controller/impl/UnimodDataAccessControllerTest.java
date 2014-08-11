package uk.ac.ebi.pridemod.controller.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pridemod.model.PTM;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class UnimodDataAccessControllerTest {

    public UnimodDataAccessController unimodDataAccessController = null;

    @Before
    public void setUp() throws Exception {
        URL url = UnimodDataAccessControllerTest.class.getClassLoader().getResource("unimod.xml");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        unimodDataAccessController = new UnimodDataAccessController(inputFile);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void TestGetPTms() {
        List<PTM> ptms = unimodDataAccessController.getPTMListByPatternDescription("Phospho");
        assertTrue("Number of PTMs with Term 'Phospho' in name:", ptms.size() == 30);
    }

    @Test
    public void TestGetMod(){
        PTM ptm = unimodDataAccessController.getPTMbyAccession("UNIMOD:1");
        assertTrue("Difference mass for Average mass is:", ptm.getAveDeltaMass() == 42.0367);
    }
}