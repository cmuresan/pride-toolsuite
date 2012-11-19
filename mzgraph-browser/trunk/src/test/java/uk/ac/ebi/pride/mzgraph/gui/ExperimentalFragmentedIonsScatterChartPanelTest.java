package uk.ac.ebi.pride.mzgraph.gui;

import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mol.Peptide;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;

/**
 * Creator: Qingwei-XU
 * Date: 12/10/12
 */

public class ExperimentalFragmentedIonsScatterChartPanelTest {
    public static void main(String[] args) {
        Peptide peptide = ExampleUtil.generatePeptide();
        PrecursorIon precursorIon = new DefaultPrecursorIon(peptide, 2);

        ExperimentalFragmentedIonsScatterChartPanel chartPanel = new ExperimentalFragmentedIonsScatterChartPanel(precursorIon, ProductIonPair.B_Y, ExampleUtil.mzArr, ExampleUtil.intentArr);

        java.util.List<IonAnnotation> annotationList = ExampleUtil.generateAnnotationList();
        chartPanel.addManualAnnotation(annotationList.get(0));
        chartPanel.addManualAnnotation(annotationList.get(1));
        chartPanel.addManualAnnotation(annotationList.get(2));

//        chartPanel.addAllManualAnnotations(ExampleUtil.specialAnnotationList());

        chartPanel.setShowAutoAnnotations(true);
//
//        chartPanel.setShowAutoAnnotations(false);
//        chartPanel.setShowManualAnnotations(false);

        ApplicationFrame mainFrame = new ApplicationFrame("test");


        mainFrame.getContentPane().add(chartPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
        RefineryUtilities.centerFrameOnScreen(mainFrame);
    }
}
