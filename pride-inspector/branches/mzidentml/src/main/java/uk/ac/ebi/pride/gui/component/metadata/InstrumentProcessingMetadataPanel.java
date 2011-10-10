package uk.ac.ebi.pride.gui.component.metadata;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.data.core.DataProcessing;
import uk.ac.ebi.pride.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.data.core.ProcessingMethod;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/*
 * Created by JFormDesigner on Sun Jul 24 22:11:06 BST 2011
 */


/**
 * @author User #2
 */
public class InstrumentProcessingMetadataPanel extends JPanel {
    public InstrumentProcessingMetadataPanel(ExperimentMetaData metaData) {
        populateComponents(metaData);
        initComponents();
    }

    private void populateComponents(ExperimentMetaData metaData) {
        // instrument configurations
        instrumentTabbedPane = new JTabbedPane();
        instrumentTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        //List<InstrumentConfiguration> instrumentConfigurationList = metaData.getInstrumentConfigurations();
        List<InstrumentConfiguration> instrumentConfigurationList = null;
        //Todo: Changes to Compile Instrument Configuration
        if (instrumentConfigurationList != null) {
            for (InstrumentConfiguration instrumentConfiguration : instrumentConfigurationList) {
                String name = instrumentConfiguration.getId();
                InstrumentCompMetadataPanel comps = new InstrumentCompMetadataPanel(instrumentConfiguration);
                instrumentTabbedPane.addTab(name, comps);
            }
        }

        // data processings
        dataProcTabbedPane = new JTabbedPane();
        dataProcTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        //List<DataProcessing> dataProcessingList = metaData.getDataProcessings();
        //Todo: changes to Compile dataProcessing
        List<DataProcessing> dataProcessingList = null;
        if (dataProcessingList != null) {
            int cnt = 1;
            for (DataProcessing dataProcessing : dataProcessingList) {
                List<ProcessingMethod> methods = dataProcessing.getProcessingMethods();
                if (methods != null) {
                    for (ProcessingMethod method : methods) {
                        DataProcessingMetadataPanel dataProc = new DataProcessingMetadataPanel(method);
                        dataProcTabbedPane.addTab("Method " + cnt, dataProc);
                        cnt++;
                    }
                }
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        instrumentLabel = new JLabel();
        dataProcLabel = new JLabel();

        //======== this ========

        //---- instrumentLabel ----
        instrumentLabel.setText("Instrument Configurations");
        instrumentLabel.setFont(instrumentLabel.getFont().deriveFont(instrumentLabel.getFont().getStyle() | Font.BOLD));

        //---- dataProcLabel ----
        dataProcLabel.setText("Data Processings");
        dataProcLabel.setFont(dataProcLabel.getFont().deriveFont(dataProcLabel.getFont().getStyle() | Font.BOLD));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(GroupLayout.TRAILING)
                        .add(GroupLayout.LEADING, dataProcTabbedPane, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                        .add(GroupLayout.LEADING, instrumentTabbedPane, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                        .add(GroupLayout.LEADING, instrumentLabel)
                        .add(GroupLayout.LEADING, dataProcLabel))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                    .addContainerGap()
                    .add(instrumentLabel)
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(instrumentTabbedPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                    .add(18, 18, 18)
                    .add(dataProcLabel)
                    .addPreferredGap(LayoutStyle.RELATED)
                    .add(dataProcTabbedPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                    .add(28, 28, 28))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel instrumentLabel;
    private JTabbedPane instrumentTabbedPane;
    private JLabel dataProcLabel;
    private JTabbedPane dataProcTabbedPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
