package uk.ac.ebi.pride.mzgraph;

import uk.ac.ebi.pride.mol.Peptide;

import javax.swing.*;
import java.awt.*;

/**
 * Creator: Qingwei-XU
 * Date: 29/10/12
 * Version: 0.1-SNAPSHOT
 */

public class PrideInspectorTest {
    public static void main(String[] args) {
        SpectrumBrowser browser = new SpectrumBrowser();
        browser.setPeaks(ExampleUtil.mzArr, ExampleUtil.intentArr);
        browser.displayMzTable(true);
        browser.setId("11111");

        Peptide peptide = ExampleUtil.generatePeptide();
        browser.setPeptide(peptide);
        browser.setAminoAcidAnnotationParameters(peptide.getLength(), null);
        browser.addAllAnnotations(ExampleUtil.generateAnnotationList());

        JFrame frame = new JFrame("aa");
        frame.getContentPane().add(browser, BorderLayout.CENTER);
        frame.setSize(new Dimension(300, 400));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
