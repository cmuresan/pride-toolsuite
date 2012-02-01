/*
 * Created by JFormDesigner on Wed Feb 01 08:45:57 GMT 2012
 */

package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.access.GeneralMetaDataGroup;

import java.awt.*;
import java.util.List;
import javax.swing.*;

/**
 * @author User #2
 */
public class IdentificationMetadataPanel extends JPanel {

    public IdentificationMetadataPanel(GeneralMetaDataGroup metaData) {
        populateComponents(metaData);
        initComponents();
    }

    private void populateComponents(GeneralMetaDataGroup metaData) {

        // database parameters
        dataBaseTabbedPane = new JTabbedPane();
        dataBaseTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        List<SearchDataBase> searchDataBaseList = metaData.getSearchDatabases();
        if(searchDataBaseList != null){
            int cnt = 1;
            for(SearchDataBase searchDataBase: searchDataBaseList){
                String name = searchDataBase.getName();
                SearchDatabaseMetadataPanel databaseMetadataPanel = new SearchDatabaseMetadataPanel(searchDataBase);
                dataBaseTabbedPane.add(name, databaseMetadataPanel);
            }
        }
        /*List<InstrumentConfiguration> instrumentConfigurationList = metaData.getInstrumentConfigurations();
        if (instrumentConfigurationList != null) {
            for (InstrumentConfiguration instrumentConfiguration : instrumentConfigurationList) {
                String name = instrumentConfiguration.getId();
                InstrumentCompMetadataPanel comps = new InstrumentCompMetadataPanel(instrumentConfiguration);
                dataBaseTabbedPane.addTab("Database " + name, comps);
            }
        } */

        proteinProtocolTabbedPane = new JTabbedPane();
        proteinProtocolTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

        // peptide Protocol
        peptideProtocolTabbedPane = new JTabbedPane();
        peptideProtocolTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        List<DataProcessing> dataProcessingList = metaData.getDataProcessings();
        if (dataProcessingList != null) {
            int cnt = 1;
            for (DataProcessing dataProcessing : dataProcessingList) {
                List<ProcessingMethod> methods = dataProcessing.getProcessingMethods();
                if (methods != null) {
                    for (ProcessingMethod method : methods) {
                        DataProcessingMetadataPanel dataProc = new DataProcessingMetadataPanel(method);
                        peptideProtocolTabbedPane.addTab("Method " + cnt, dataProc);
                        cnt++;
                    }
                }
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        databaseLabel = new JLabel();
        peptideProtocol = new JLabel();
        proteinProtocol = new JLabel();

        //======== this ========

        //---- databaseLabel ----
        databaseLabel.setText("DataBase Properties");
        databaseLabel.setFont(databaseLabel.getFont().deriveFont(databaseLabel.getFont().getStyle() | Font.BOLD));

        //---- peptideProtocol ----
        peptideProtocol.setText("Peptide Identification Protocol");
        peptideProtocol.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        proteinProtocol.setText("Peptide Identification Protocol");
        proteinProtocol.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                    .addContainerGap()
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, peptideProtocolTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, dataBaseTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, databaseLabel)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, peptideProtocol)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, proteinProtocol)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, proteinProtocolTabbedPane))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(databaseLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataBaseTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(peptideProtocol)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peptideProtocolTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(proteinProtocol)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(proteinProtocolTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .add(28, 28, 28))

        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel databaseLabel;
    private JTabbedPane dataBaseTabbedPane;
    private JLabel peptideProtocol;
    private JTabbedPane peptideProtocolTabbedPane;
    private JLabel proteinProtocol;
    private JTabbedPane proteinProtocolTabbedPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
