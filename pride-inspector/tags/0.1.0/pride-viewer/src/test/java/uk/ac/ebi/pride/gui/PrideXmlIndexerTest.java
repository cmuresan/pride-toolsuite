package uk.ac.ebi.pride.gui;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.xxindex.PrideXmlIndexer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 23-Mar-2010
 * Time: 16:07:20
 */
public class PrideXmlIndexerTest {

    private PrideXmlIndexer indexer = null;

    @Before
    public void prepareIndexer() {
        // load test pride xml file
        File pridexml = new File("src\\test\\resources\\pride-test.xml");
        // create indexer
        indexer = new PrideXmlIndexer(pridexml);
    }

    @Test
    public void testExperimentIds() {
        List<String> ids = indexer.getExperimentIds();
        assertTrue("Experiment 1 accession not found", ids.contains("1"));
        assertTrue("Experiment 2 accession not found", ids.contains("2"));
    }

    @Test
    public void testExperimentIdsOrder() {
        List<String> ids = indexer.getExperimentIds();
        assertTrue("Experiment 1 is the first one", "1".equals(ids.get(0)));
        assertTrue("Experiment 2 is the second one", "2".equals(ids.get(1)));
    }

    @Test
    public void testTitleXmlString() throws IOException {
        String xml = indexer.getTitleXmlString("1");
        assertTrue("Experiment 1's title: " + xml, "<Title>COFRADIC cysteine proteome of unstimulated human blood platelets</Title>".equals(xml));
        xml = indexer.getTitleXmlString("2");
        assertTrue("Experiment 2's title: " + xml, "<Title>human blood platelets</Title>".equals(xml));
    }

    @Test
    public void testShortLabelString() throws IOException {
        String xml = indexer.getShortLabelXmlString("1");
        assertTrue("Experiment 1's short label: " + xml, "<ShortLabel>Platelets Cys</ShortLabel>".equals(xml));
        xml = indexer.getShortLabelXmlString("2");
        assertTrue("Experiment 2's short label: ", "<ShortLabel>Cysv</ShortLabel>".equals(xml));
    }

    @Test
    public void testSpectrumString() throws IOException {
        String spectrumXml = indexer.getSpectrumXmlString("1", "0");
        assertTrue("Experiment 1's spectrum 0", spectrumXml.contains("spectrumInstrument mzRangeStop=\"123.45\" mzRangeStart=\"342.678\" msLevel=\"0\""));
        assertTrue("Experiment 1's specturm 0", spectrumXml.contains("<data precision=\"32\" endian=\"big\" length=\"0\">UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi</data>"));
        spectrumXml = indexer.getSpectrumXmlString("2", "0");
        assertTrue("Experiment 2's spectrum 0",spectrumXml.contains("spectrumInstrument mzRangeStop=\"123.456\" mzRangeStart=\"342.678\" msLevel=\"0\""));
        assertTrue("Experiment 2's specturm 0", spectrumXml.contains("<data precision=\"32\" endian=\"big\" length=\"0\">UjBsR09EbGhjZ0dTQUxNQUFBUUNBRU1tQ1p0dU1GUXhEUzhi</data>"));
    }

    @Test
    public void testTwoDimString() throws IOException {
        String twoDimString = indexer.getTwoDimIdentXmlString("1", "IPI00295313");
        assertTrue("Experiment 1's twoDim", twoDimString.contains("<Database>IPI human</Database>"));
        twoDimString = indexer.getTwoDimIdentXmlString("2", "IPI00295313");
        assertTrue("Experiment 2's twoDim", twoDimString.contains("<Database>IPI mouse</Database>"));
    }
}
