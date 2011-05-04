package uk.ac.ebi.pride.data.controller.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 24-May-2010
 * Time: 13:48:35
 */
public class MzMLControllerImplTest {

    private MzMLControllerImpl mzMLController = null;

    @Before
    public void setUp() throws Exception {
        URL url = PrideXmlControllerImplTest.class.getClassLoader().getResource("tiny.pwiz.1.1.xml");
        if (url == null) {
            throw new IllegalStateException("no file for input found!");
        }
        File inputFile = new File(url.toURI());
        mzMLController = new MzMLControllerImpl(inputFile);
    }

    @After
    public void tearDown() throws Exception {
        mzMLController.close();
    }

    @Test
    public void testGetMetaData() throws Exception {
    }

    @Test
    public void testGetCvLookups() throws Exception {
    }

    @Test
    public void testGetFileDescription() throws Exception {
    }

    @Test
    public void testGetReferenceableParamGroup() throws Exception {
    }

    @Test
    public void testGetSamples() throws Exception {
    }

    @Test
    public void testGetSoftware() throws Exception {
    }

    @Test
    public void testGetScanSettings() throws Exception {
    }

    @Test
    public void testGetInstrumentConfigurations() throws Exception {
    }

    @Test
    public void testGetDataProcessings() throws Exception {
    }

    @Test
    public void testGetSpectrumIds() throws Exception {
    }

    @Test
    public void testGetSpectrumById() throws Exception {
    }

    @Test
    public void testGetChromatogramIds() throws Exception {
    }

    @Test
    public void testGetChromatogramById() throws Exception {
    }
}
