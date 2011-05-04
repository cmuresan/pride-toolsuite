package uk.ac.ebi.pride.gui;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.pride.data.io.PrideXmlUnmarshaller;
import uk.ac.ebi.pride.data.jaxb.pridexml.*;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 23-Mar-2010
 * Time: 15:51:20
 */
public class PrideXmlUnmarshallerTest {
    private PrideXmlUnmarshaller um = null;

    @Before
    public void prepareTest() {
        // load the file
        File file = new File("src\\test\\resources\\pride-test.xml");
        // construct PrideXmlUnmarshaller
        um = new PrideXmlUnmarshaller(file);
    }

    @Test
    public void testExperimentTitle() {
        String title = um.getExperimentTitle("1");
        assertTrue("Experiment 1's title", "COFRADIC cysteine proteome of unstimulated human blood platelets".equals(title));
        title = um.getExperimentTitle("2");
        assertTrue("Experiment 2's title", "human blood platelets".equals(title));
    }

    @Test
    public void testReferences() {
        List<ReferenceType> refs = um.getReferences("1");
        ReferenceType ref = refs.get(0);
        assertTrue("Experiment 1's reference line", ref.getRefLine().equals("Gevaert, K., Ghesquière, B., Staes, A., Martens, L., Van Damme, J., Thomas, G.R., Vandekerckhove, J., Proteomics 2004, 4 , 897-908.\n" +
                "Martens, L., Van Damme, P., Van Damme, J., Staes, A., Timmerman, E., Ghesquière, B., Thomas, G.R., Vandekerckhove, J., Gevaert, K., Proteomics, in press"));
    }

    @Test
    public void testShortLabel() {
        String sl = um.getShortLabel("1");
        assertTrue("Experiment 1's short label", "Platelets Cys".equals(sl));
        sl = um.getShortLabel("2");
        assertTrue("Experiment 2's short label", "Cysv".equals(sl));
    }

    @Test
    public void testProtocol() {
        ExperimentType.Protocol prot = um.getProtocol("1");

        // check protocol name
        assertTrue("Protocol name", "methionine oxidation induces a chromatographic shift on a diagonal RP-HPLC system".equals(prot.getProtocolName().trim()));

        // check protocol step
        ExperimentType.Protocol.ProtocolSteps steps = prot.getProtocolSteps();
        List<ParamType> descs = steps.getStepDescription();
        CvParamType cvParam = (CvParamType)descs.get(0).getCvParamOrUserParam().get(0);
        assertTrue("Protocol step", "Chromatographic Shift".equals(cvParam.getName()));
    }

    @Test
    public void testAdmin() {
        AdminType admin = um.getAdmin("1");
        // sample name
        assertTrue("Sample name", "unstimulated human platelets".equals(admin.getSampleName()));
        // sample desc
        DescriptionType descType = admin.getSampleDescription();
        assertTrue("Sample description", "unstimulated human platelets".equals(descType.getComment()));
        // source file
        SourceFileType sf = admin.getSourceFile();
        assertTrue("Source file name", "MadeUpFile".equals(sf.getNameOfFile()));
        // Contact
        List<PersonType> contacts = admin.getContact();
        PersonType person = contacts.get(0);
        assertTrue("Person name", "Lennart Martens".equals(person.getName()));
    }

    @Test
    public void testAdditionalParam() {
        ParamType additional = um.getAdditionalParam("1");

        List<Object> params = additional.getCvParamOrUserParam();
        CvParamType cvParam = (CvParamType)params.get(0);
        assertTrue("Additional cv value", "3 A Value".equals(cvParam.getValue()));
        assertTrue("Additional cv cvLabel", "3MadeUpCV".equals(cvParam.getCvLabel()));
    }

    @Test
    public void testSpectrum() {
        // ToDo: implement test here
    }
        
}
