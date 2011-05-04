package uk.ac.ebi.pride.gui.test;

import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.Precursor;
import uk.ac.ebi.pride.data.core.Spectrum;

import java.io.File;
import java.util.Collection;


/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 24-Feb-2010
 * Time: 09:54:36
 */
public class MzMLControllerTest {
    public static void main(String[] args) throws Exception{
        MzMLControllerImpl controller = new MzMLControllerImpl(new File(args[0]));

        Collection<String> ids = controller.getSpectrumIds();

        for(String id : ids) {
            System.out.println(id);
            Spectrum spec = controller.getSpectrumById(id);
            System.out.println(spec.getSourceFile().getName());
        }

        ids = controller.getChromatogramIds();

        for(String id : ids) {
            System.out.println(id);
            Chromatogram chroma = controller.getChromatogramById(id);
            Collection<Precursor> precursors = chroma.getPrecursors();
            for (Precursor pre : precursors) {
                Collection<CvParam> cvParams = pre.getIsolationWindow().getCvParams();
                for(CvParam cvParam : cvParams)
                    System.out.println(cvParam.getName());
            }
        }
    }
}
