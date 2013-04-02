package uk.ac.ebi.pride.gui.component.metadata;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.data.core.InstrumentComponent;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.Parameter;
import uk.ac.ebi.pride.gui.component.table.TableFactory;

import javax.swing.*;
import java.util.ArrayList;
/*
 * Created by JFormDesigner on Sun Jul 24 22:04:54 BST 2011
 */


/**
 * @author User #2
 */
public class InstrumentCompMetadataPanel extends JPanel {
    public InstrumentCompMetadataPanel(InstrumentConfiguration instrument) {
        populateComponents(instrument);
        initComponents();
    }

    private void populateComponents(InstrumentConfiguration instrument) {
        // ion source
        if (!instrument.getSource().isEmpty() && instrument.getSource().get(0) != null) {
            ionSourceTable = TableFactory.createParamTable(instrument.getSource().get(0));
        } else {
            ionSourceTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }

        // analyzers
        if (!instrument.getAnalyzer().isEmpty() && instrument.getAnalyzer().get(0) != null) {
            analyzerTable = TableFactory.createParamTable(instrument.getAnalyzer().get(0));
        } else {
            analyzerTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }

        // detector

        if (!instrument.getDetector().isEmpty() && instrument.getDetector().get(0) != null) {
            detectorTable = TableFactory.createParamTable(instrument.getDetector().get(0));
        } else {
            detectorTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        ionSourceLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        analyzerLabel = new JLabel();
        detectorLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        scrollPane3 = new JScrollPane();

        //======== this ========

        //---- ionSourceLabel ----
        ionSourceLabel.setText("Ion Source");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(ionSourceTable);
        }

        //---- analyzerLabel ----
        analyzerLabel.setText("Analyzer");

        //---- detectorLabel ----
        detectorLabel.setText("Detector");

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(detectorTable);
        }

        //======== scrollPane3 ========
        {
            scrollPane3.setViewportView(analyzerTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                                        .add(GroupLayout.LEADING, scrollPane2, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .add(GroupLayout.LEADING, scrollPane3, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .add(GroupLayout.LEADING, scrollPane1, GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
                                        .add(GroupLayout.LEADING, ionSourceLabel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                        .add(GroupLayout.LEADING, analyzerLabel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                        .add(GroupLayout.LEADING, detectorLabel, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .add(layout.createSequentialGroup()
                                .add(8, 8, 8)
                                .add(ionSourceLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(analyzerLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane3, GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(detectorLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                                .addContainerGap())
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel ionSourceLabel;
    private JScrollPane scrollPane1;
    private JTable ionSourceTable;
    private JLabel analyzerLabel;
    private JLabel detectorLabel;
    private JScrollPane scrollPane2;
    private JTable detectorTable;
    private JScrollPane scrollPane3;
    private JTable analyzerTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
