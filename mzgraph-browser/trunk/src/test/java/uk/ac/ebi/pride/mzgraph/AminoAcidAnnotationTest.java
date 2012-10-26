package uk.ac.ebi.pride.mzgraph;

import uk.ac.ebi.pride.gui.io.SaveComponentUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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
        SpectrumBrowser browser = new SpectrumBrowser(ExampleUtil.mzArr, ExampleUtil.intentArr);
        browser.setPeptide(ExampleUtil.generatePeptide());

        JFrame frame = new JFrame("Amino acid annotation test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(browser, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);


        try {
            SaveComponentUtils.writeAsPDF(new File("/test.pdf"), browser.getSpectrumPanel());
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
