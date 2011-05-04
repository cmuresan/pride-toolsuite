package uk.ac.ebi.pride.gui.test;

import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;

import java.io.File;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 20-Feb-2010
 * Time: 21:12:15
 */
public class MzMLTest {
    public static void main(String[] args) throws Exception{
        String file = args[0];
        MzMLUnmarshaller reader = new MzMLUnmarshaller(new File(file));
        Collection<String> ids = reader.getSpectrumIDs();
        for (String id : ids) {
            //System.out.println(id);
            Spectrum spectrum = reader.getSpectrumById(id);
            System.out.println(spectrum.getDataProcessing().getId());
            System.out.println(spectrum.getSourceFile().getId());
            System.out.println(spectrum.getSourceFile().getName());
        }
    }
}
