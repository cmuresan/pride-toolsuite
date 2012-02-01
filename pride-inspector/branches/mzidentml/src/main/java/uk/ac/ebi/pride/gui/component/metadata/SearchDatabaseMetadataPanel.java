package uk.ac.ebi.pride.gui.component.metadata;

import org.jdesktop.layout.*;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.component.table.TableFactory;

import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: yperez
 * Date: 2/1/12
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchDatabaseMetadataPanel extends JPanel{

    public SearchDatabaseMetadataPanel(SearchDataBase dataBase) {
        populateComponents(dataBase);
        initComponents();
    }

    private void populateComponents(SearchDataBase dataBase) {
        // software name
        softwareNameField = new JTextField();
        softwareNameField.setEditable(false);

        // software version
        softwareVersionField = new JTextField();
        softwareVersionField.setEditable(false);

        // software
        String name = dataBase.getName();
        if (name != null) {
            softwareNameField.setText(name);
        }

        String version = dataBase.getVersion();
        if (version != null) {
            softwareVersionField.setText(version);
        }

        // database Parameters
        ParamGroup nameParams = dataBase.getNameDatabase();
        List<CvParam> otherParams = dataBase.getDescription();
        List<CvParam> allCvParams = new ArrayList<CvParam>();

        if(otherParams!=null) allCvParams.addAll(otherParams);
        if(nameParams.getCvParams()!=null) allCvParams.addAll(nameParams.getCvParams());

        ParamGroup allFeatures = new ParamGroup(allCvParams,nameParams.getUserParams());
        dataProcTable = TableFactory.createParamTable(allFeatures);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        softwareLabel = new JLabel();
        softwareVersionLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        scrollPane1.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

        //======== this ========

        //---- softwareLabel ----
        softwareLabel.setText("Database:");

        //---- softwareVersionLabel ----
        softwareVersionLabel.setText("Version:");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(dataProcTable);
        }

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                    .add(12, 12, 12)
                    .add(softwareLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(softwareNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(39, 39, 39)
                    .add(softwareVersionLabel)
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(softwareVersionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 145, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(55, Short.MAX_VALUE))
                .add(scrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .add(layout.createSequentialGroup()
                    .add(10, 10, 10)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(softwareLabel)
                        .add(softwareNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(softwareVersionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(softwareVersionLabel))
                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                    .add(scrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel softwareLabel;
    private JTextField softwareNameField;
    private JLabel softwareVersionLabel;
    private JTextField softwareVersionField;
    private JScrollPane scrollPane1;
    private JTable dataProcTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
