package uk.ac.ebi.pride.mzgraph;

import uk.ac.ebi.pride.mol.NeutralLoss;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotation;
import uk.ac.ebi.pride.mzgraph.chart.data.annotation.IonAnnotationInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 12-Aug-2010
 * Time: 13:45:03
 */
public class AminoAcidAnnotationTest {
    public static void main(String[] args) {
        Runnable runner = new Runnable() {
            public void run() {
                createGUI();
            }
        };

        EventQueue.invokeLater(runner);
    }

    private static void createGUI() {
        double[] mzArr = new double[]{1.0, 2.012312313, 3.0, 4.234, 6.0, 7.34342};
        double[] intentArr = new double[]{0.05, 4.345345345, 6.0, 1.4545, 5.0, 8.23423};

        SpectrumBrowser browser = new SpectrumBrowser();
        browser.setPeaks(mzArr, intentArr);

        JFrame frame = new JFrame("Amino acid annotation test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(browser, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);


        java.util.List<IonAnnotation> ions = new ArrayList<IonAnnotation>();
        IonAnnotationInfo ionInfo1 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 1, null);
        ionInfo1.addItem(annotationItem);
        IonAnnotation ion1 = new IonAnnotation(189.855, 35300, ionInfo1);
        ions.add(ion1);

        IonAnnotationInfo ionInfo2 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem2 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 2, null);
        ionInfo2.addItem(annotationItem2);
        IonAnnotation ion2 = new IonAnnotation(319.005, 24600, ionInfo2);
        ions.add(ion2);

        IonAnnotationInfo ionInfo3 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem3 = new IonAnnotationInfo.Item(1, FragmentIonType.Y_ION, 2, null);
        ionInfo3.addItem(annotationItem3);
        IonAnnotation ion3 = new IonAnnotation(274.127, 18630, ionInfo3);
        ions.add(ion3);

        IonAnnotationInfo ionInfo4 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem4 = new IonAnnotationInfo.Item(1, FragmentIonType.Y_ION, 3, null);
        ionInfo4.addItem(annotationItem4);
        IonAnnotation ion4 = new IonAnnotation(421.237, 16260, ionInfo4);
        ions.add(ion4);

        IonAnnotationInfo ionInfo5 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem5 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 3, null);
        ionInfo5.addItem(annotationItem5);
        IonAnnotation ion5 = new IonAnnotation(390.071, 62080, ionInfo5);
        ions.add(ion5);

        IonAnnotationInfo ionInfo6 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem6 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 7, null);
        ionInfo6.addItem(annotationItem6);
        IonAnnotation ion6 = new IonAnnotation(760.278, 447200, ionInfo6);
        ions.add(ion6);

        IonAnnotationInfo ionInfo7 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem7 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 3, NeutralLoss.WATER_LOSS);
        ionInfo7.addItem(annotationItem7);
        IonAnnotation ion7 = new IonAnnotation(390.071 - NeutralLoss.WATER_LOSS.getMonoMass(), 547200, ionInfo7);
        ions.add(ion7);

        IonAnnotationInfo ionInfo8 = new IonAnnotationInfo();
        IonAnnotationInfo.Item annotationItem8 = new IonAnnotationInfo.Item(1, FragmentIonType.B_ION, 2, NeutralLoss.WATER_LOSS);
        ionInfo8.addItem(annotationItem8);
        IonAnnotation ion8 = new IonAnnotation(319.005 - NeutralLoss.WATER_LOSS.getMonoMass(), 44600, ionInfo8);
        ions.add(ion8);
        
        browser.addFragmentIons(ions);

        browser.setSource("Hahah");
        browser.setId("1232332");

//        try {
//            SaveComponentUtils.writeAsPDF(new File("/test.pdf"), browser.getSpectrumPanel());
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
    }
}
