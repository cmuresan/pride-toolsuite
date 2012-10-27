package uk.ac.ebi.pride.mzgraph.chart.graph;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;

public class MzTablePanelTest {
    public static void main(String[] args) {
        Peptide peptide = ExampleUtil.generatePeptide();
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide, 2);

        MzTablePanel tablePanel = new MzTablePanel(peptide, ExampleUtil.mzArr, ExampleUtil.intentArr);

        java.util.List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        tablePanel.addManualAnnotation(annotationList.get(0));
        tablePanel.addManualAnnotation(annotationList.get(1));
        tablePanel.addManualAnnotation(annotationList.get(2));

        tablePanel.setShowAutoAnnotations(true);
        tablePanel.setShowManualAnnotations(true);
//
//        tablePanel.setShowAutoAnnotations(false);
//        tablePanel.setShowManualAnnotations(false);

        ApplicationFrame mainFrame = new ApplicationFrame("test");


        mainFrame.getContentPane().add(tablePanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(mainFrame);
    }
}
