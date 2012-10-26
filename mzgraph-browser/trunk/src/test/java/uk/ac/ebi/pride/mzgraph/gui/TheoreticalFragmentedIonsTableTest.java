package uk.ac.ebi.pride.mzgraph.gui;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.iongen.model.impl.DefaultPrecursorIon;
import uk.ac.ebi.pride.mzgraph.ExampleUtil;

import javax.swing.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class TheoreticalFragmentedIonsTableTest {
    public static void main(String[] args) {
        // table value matrix reference: theoretical_fragmented_ions.proteomecluster file
        // which stored in the test resources directory.
        PrecursorIon precursorIon = new DefaultPrecursorIon(ExampleUtil.generatePeptide(), 2);
        TheoreticalFragmentedIonsTable table = new TheoreticalFragmentedIonsTable(precursorIon);
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.getContentPane().add(scrollPane);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }
}
