package uk.ac.ebi.pride.gui;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.core.*;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 25-Mar-2010
 * Time: 15:40:05
 */
public class PrideXmlControllerTest {
    private PrideXmlControllerImpl controller;

    @Before
    public void prepareTest() throws DataAccessException {
        File file = new File("src\\test\\resources\\pride-test.xml");
        // create access controller
        controller = new PrideXmlControllerImpl(file);
    }

    @Test
    public void testExperimentIds() throws DataAccessException {
        List<String> expIds = controller.getExperimentIds();
        assertTrue("Experiment 1", expIds.contains("1"));
        assertTrue("Experiment 2", expIds.contains("2"));

        assertTrue("Number of experiments", expIds.size() == 2);
    }

    @Test
    public void testExperiment() throws DataAccessException {
        Experiment exp = controller.getExperimentById("1");
        // title
        assertTrue("Experiment title", "COFRADIC cysteine proteome of unstimulated human blood platelets".equals(exp.getTitle()));
        // short label
        assertTrue("Experiment short label", "Platelets Cys".equals(exp.getShortLabel()));
        // protocol
        Protocol protocol = exp.getProtocol();
        List<ParamGroup> steps = protocol.getProtocolSteps();
        CvParam cvParam = steps.get(0).getCvParams().get(0);
        assertTrue("Protocol step name", "Chromatographic Shift".equals(cvParam.getName()));
        assertTrue("Protocol step accession", "0001".equals(cvParam.getAccession()));
        // reference
        List<Reference> references = exp.getReferences();
        Reference reference = references.get(0);
        assertTrue("Reference line", ("Gevaert, K., Ghesquière, B., Staes, A., Martens, L., Van Damme, J., Thomas, G.R., Vandekerckhove, J., Proteomics 2004, 4 , 897-908.\n" +
                "Martens, L., Van Damme, P., Van Damme, J., Staes, A., Timmerman, E., Ghesquière, B., Thomas, G.R., Vandekerckhove, J., Gevaert, K., Proteomics, in press").equals(reference.getFullReference()));
        //additional params
        CvParam additional = exp.getCvParams().get(0);
        assertTrue("Additional Cv param", "000123".equals(additional.getAccession()));            
    }

    @Test
    public void testSpectrumIds() throws DataAccessException {
        List<String> specIds = controller.getSpectrumIds();
        assertTrue("Spectrum Id", specIds.contains("0"));
        assertTrue("Spectrum Id", specIds.contains("1"));
        assertTrue("Num of spectra", specIds.size() == 2);
    }

    @Test
    public void testSpectrum() throws DataAccessException {
        Spectrum spec = controller.getSpectrumById("0");
        // scan list
        ScanList scanList = spec.getScanList();
        List<CvParam> cvParams = scanList.getCvParams();
        CvParam cvParam = cvParams.get(0);
        assertTrue("Cv Param: scan mode", "ScanMode".equals(cvParam.getName()));
        assertTrue("Cv Param: scan mode value", "SelectedIonDetection".equals(cvParam.getValue()));
        assertTrue("Cv param size", cvParams.size()==1);
        List<UserParam> userParams = scanList.getUserParams();
        UserParam userParam = userParams.get(0);
        assertTrue("User Param: spectrum type", "spectrum type".equals(userParam.getName()));
        assertTrue("User Param: spectrum type value", "discrete".equals(userParam.getValue()));
        assertTrue("User param size", userParams.size()==3);
        // scan
        List<Scan> scans = scanList.getScans();
        assertTrue("Number of scans", scans.size()==1);
        Scan scan = scans.get(0);
        ParamGroup scanWin = scan.getScanWindows().get(0);
        CvParam stop = scanWin.getCvParams().get(1);
        assertTrue("Scan Window: stop range", "123.45".equals(stop.getValue()));
        // binary arr
    }

    @Test
    public void testTwoDimIds() throws DataAccessException {
        List<String> twoDimIds = controller.getTwoDimIdentIds();
        assertTrue("Number of two dimensional identification", twoDimIds.size()==1);
        assertTrue("Two dimensional idenfication", twoDimIds.contains("IPI00295313"));
    }

    @Test
    public void testTwoDim() throws DataAccessException {
        TwoDimIdentification twoDim = controller.getTwoDimIdentById("IPI00295313");
        // accession version
        assertTrue("Two Dim accession version", "1".equals(twoDim.getAccessionVersion()));
        // database
        assertTrue("Two Dim database", "IPI human".equals(twoDim.getSearchDatabase()));
        // database version
        assertTrue("Two Dim database version", "2.31".equals(twoDim.getSearchDatabaseVerison()));
        // peptide
        List<Peptide> peptides = twoDim.getPeptides();
        Peptide peptide = peptides.get(0);
        assertTrue("Peptide sequence", "GPAGEPMGPEAGSK".equals(peptide.getSequence()));
        assertTrue("Peptide start", "344".equals(peptide.getStart().toString()));
        assertTrue("Peptide end", "357".equals(peptide.getEnd().toString()));
        assertTrue("Peptide spectrum reference", "1".equals(peptide.getSpectrum().getId()));
        // modification
        Modification modification = peptide.getModifications().get(0);
        assertTrue("Modification location", "2".equals(modification.getLocation().toString()));
        assertTrue("Modification avg delta", "44.2".equals(modification.getAvgMassDeltas().get(1)+""));
        // gel
        Gel gel = twoDim.getGel();
        assertTrue("Gel x coordinate", Double.parseDouble("3.141593E0") == gel.getxCoordinate());
        assertTrue("Gel y coordinate", Double.parseDouble("3.141593E0") == gel.getyCoordinate());
        // mol weight
        assertTrue("Mol Weight", Double.parseDouble("3.141593E0") == gel.getMolecularWeight());
        // pI
        assertTrue("pI", Double.parseDouble("3.141593E0") == gel.getpI());
        // sequence coverage
        assertTrue("Sequence coverage", Double.parseDouble("1.0E0") == twoDim.getSequenceConverage());
    }

    @Test
    public void testGelFreeIds() throws DataAccessException {

    }

    @Test
    public void testGelFree() throws DataAccessException {

    }
}
