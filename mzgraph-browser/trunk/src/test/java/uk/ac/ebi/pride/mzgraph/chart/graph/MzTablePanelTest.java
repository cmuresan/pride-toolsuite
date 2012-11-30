package uk.ac.ebi.pride.mzgraph.chart.graph;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;

import java.awt.*;

public class MzTablePanelTest {
    public static void main(String[] args) {
        Peptide peptide = ExampleUtil.generatePeptide();
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide, 2);

        MzTablePanel panel = new MzTablePanel(precursorIon);

        // case 1: when delta m/z overflow.
//        panel.calculateAuto(false);

        // case 2: show auto annotations:
//        panel.calculateAuto(true);
//        panel.setPeaks(ExampleUtil.mzArr, ExampleUtil.intentArr);

        // case 3: show manual annotations:
        panel.calculateAuto(false);
        java.util.List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        panel.addAllManualAnnotations(annotationList);

        // create main frame.
        ApplicationFrame mainFrame = new ApplicationFrame("test");
        mainFrame.getContentPane().add(panel);
        mainFrame.setPreferredSize(new Dimension(1200, 350));
        panel.getTablePanel().setPreferredSize(new Dimension(1000, 250));
        panel.getChartPanel().setPreferredSize(new Dimension(200, 250));
        mainFrame.pack();
        mainFrame.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(mainFrame);
    }
}
