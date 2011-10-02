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
        assertTrue("There should be only one sample", samples.size() == 2);
        assertEquals("Sample ID should always be sample1", samples.get(0).getId(), "sample1");
        assertEquals("Sample cv param should be ", samples.get(0).getCvParams().get(0).getName(), "name31");
    }

    @Test
    public void testGetSoftware() throws Exception {
        List<Software> software = mzIdentMlController.getSoftwareList();
        assertTrue("There should be only one software", software.size() == 2);
        assertEquals("Software ID should be Mascot Server", software.get(0).getName(), "Mascot Server");
        assertEquals("Software version should be 2.3.3.0 for the second software", software.get(1).getVersion(), "2.3.3.0");
    }

    @Test
    public void testGetMetaData() throws Exception {
        ExperimentMetaData experiment = (ExperimentMetaData) mzIdentMlController.getExperimentMetaData();

        // test references
        List<Reference> references = experiment.getReferences();
        assertTrue("There should be only one reference", references.size()==1);
        assertEquals("PubMed number should be 16038019", references.get(0).getDoi(), "10.1002/(SICI)1522-2683(19991201)20:18<3551::AID-ELPS3551>3.0.CO;2-2");

        // test version
        assertEquals("Version should be 1.1.0", experiment.getVersion(), "1.1.0");
        // test name
        assertEquals("The name of the file should be PSI Example File", experiment.getName(), "PSI Example File");

        // test the Provider of the File
        assertEquals("The id of the Provider should be person2",((Person)(experiment.getProvider().getContact())).getId(),"person2");
        assertEquals("The role of the Provider should be researcher",experiment.getProvider().getRole().getName(),"researcher");

    }

    @Test
    public void testGetPersonContacts() throws Exception{
        List<Person> persons = mzIdentMlController.getPersonContacts();
        assertTrue("There should be only two persons", persons.size() == 2);
        assertEquals("Person one ID should be person1", persons.get(0).getId(), "person1");
        assertEquals("Person two last Name should be Perez-Riverol", persons.get(1).getLastname(), "Perez-Riverol");
        assertEquals("Affiliation for Person two should be Matrix Science Limited", persons.get(1).getAffiliation().get(0).getName(),"Matrix Science Limited");
    }

    @Test
    public void testGetOrganizationContacts() throws Exception{
        List<Organization> organizations = mzIdentMlController.getOrganizationContacts();
        assertTrue("There should be only two organizations", organizations.size() == 2);
        assertEquals("Organization one ID should be ORG_MSL", organizations.get(0).getId(), "ORG_MSL");
        assertEquals("Organization two Parent Organization Name should be Matrix Science Limited", organizations.get(1).getParentOrganization().getName(), "Matrix Science Limited");
    }

    @Test
    public void testGetIdentificationIDs(){

    }


}
