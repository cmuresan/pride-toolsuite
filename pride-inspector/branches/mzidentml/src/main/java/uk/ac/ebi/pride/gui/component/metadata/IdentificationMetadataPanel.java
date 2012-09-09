/*
 * Created by JFormDesigner on Wed Feb 01 08:45:57 GMT 2012
 */

package uk.ac.ebi.pride.gui.component.metadata;

import uk.ac.ebi.pride.data.core.Parameter;
import uk.ac.ebi.pride.data.core.Protocol;
import uk.ac.ebi.pride.data.core.SearchDataBase;
import uk.ac.ebi.pride.data.core.SpectrumIdentificationProtocol;
import uk.ac.ebi.pride.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

        //Protein Protocol Parameters
        proteinProtocolLabel = new JLabel();

        Protocol proteinProtocol = metaData.getProteinDetectionProtocol();

        if ((proteinProtocol != null) && (proteinProtocol.getAnalysisParam() != null)) {

            String nameProtocol = proteinProtocol.getName();
            if(nameProtocol == null) nameProtocol ="Dafault";
            String softwareProtocol = proteinProtocol.getAnalysisSoftware().getName();
            proteinProtocolLabel.setText("Protein Identification Protocol: " + nameProtocol + ", Software " + softwareProtocol);
            proteinProtocolLabel.setToolTipText(nameProtocol);
            // protocol table
            proteinProtocolTable = TableFactory.createParamTable(proteinProtocol.getAnalysisParam());
        } else {
            proteinProtocolTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }



        // peptide Protocol
        peptideProtocolTabbedPane = new JTabbedPane();
        peptideProtocolTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

        List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = metaData.getSpectrumIdentificationProtocol();
        if (spectrumIdentificationProtocolList != null) {
            for (SpectrumIdentificationProtocol spectrumIdentificationProtocol : spectrumIdentificationProtocolList) {
                String name = spectrumIdentificationProtocol.getName();
                if(name == null) name ="Default";
                String software = spectrumIdentificationProtocol.getAnalysisSoftware().getName();
                PeptideIdentificationMetadataPanel peptideComp = new PeptideIdentificationMetadataPanel(spectrumIdentificationProtocol);
                peptideProtocolTabbedPane.addTab("Protocol " + name + ", Software" + software, peptideComp);
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        databaseLabel = new JLabel();
        peptideProtocol = new JLabel();
        scrollPane2 = new JScrollPane();

        //======== this ========

        //---- databaseLabel ----
        databaseLabel.setText("DataBase Properties");
        databaseLabel.setFont(databaseLabel.getFont().deriveFont(databaseLabel.getFont().getStyle() | Font.BOLD));

        //---- peptideProtocol ----
        peptideProtocol.setText("Peptide Identification Protocol");
        peptideProtocol.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        proteinProtocolLabel.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(proteinProtocolTable);
        }

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
                        .add(org.jdesktop.layout.GroupLayout.LEADING, proteinProtocolLabel)
                        .add(org.jdesktop.layout.GroupLayout.LEADING, scrollPane2))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(databaseLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataBaseTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(peptideProtocol)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(peptideProtocolTabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(proteinProtocolLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(scrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
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
    private JLabel proteinProtocolLabel;
    private JTable proteinProtocolTable;
    private JScrollPane scrollPane2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
