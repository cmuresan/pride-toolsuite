package uk.ac.ebi.pride.gui.component.metadata;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.term.CvTermReference;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/*
 * Created by JFormDesigner on Sat Jul 23 08:30:00 BST 2011
 */


/**
 * @author User #2
 */
public class GeneralMetadataPanel extends JPanel {

    public GeneralMetadataPanel(MetaData metaData) {
        populateComponents(metaData);
        initComponents();
    }

    /**
     * Create key components and populate them with values
     *
     * @param metaData meta data
     */
    private void populateComponents(MetaData metaData) {
        // get accession
        String accession = metaData.getAccession();
        accessionField = new JTextField();
        if (accession != null) {
            accessionField.setText(accession);
        }

        expTitleField = new JTextField();
        shortLabelField = new JTextField();
        if (metaData instanceof Experiment) {
            // get experiment title
            String expTitle = ((Experiment) metaData).getTitle();
            if (expTitle != null) {
                expTitleField.setText(expTitle);
            }
            // get short label
            String sl = ((Experiment) metaData).getShortLabel();
            if (sl != null) {
                shortLabelField.setText(sl);
            }
        }

        projectField = new JTextField();
        expDescArea = new JTextPane();
        List<CvParam> cvs = metaData.getCvParams();
        if (cvs != null) {
            for (CvParam cv : cvs) {
                // get project name
                if (CvTermReference.PROJECT_NAME.getAccession().equals(cv.getAccession())) {
                    projectField.setText(cv.getValue());
                } else if (CvTermReference.EXPERIMENT_DESCRIPTION.getAccession().equals(cv.getAccession())) {
                    // get experiment description
                    expDescArea.setText(cv.getValue());
                }
            }
        }

        // reference
        if (metaData instanceof Experiment && ((Experiment) metaData).getReferences() != null) {
            List<Reference> references = ((Experiment) metaData).getReferences();
            referenceTable = TableFactory.createReferenceTable(references);
        } else {
            referenceTable = TableFactory.createReferenceTable(new ArrayList<Reference>());
        }

        // contact

        List<ParamGroup> contacts = metaData.getFileDescription().getContacts();
        contactTable = TableFactory.createContactTable(contacts == null ? new ArrayList<ParamGroup>() : contacts);

        // additional params
        additionalTable = TableFactory.createParamTable(metaData);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        accessionLabel = new JLabel();
        expTitleLabel = new JLabel();
        shortLabel = new JLabel();
        projectLabel = new JLabel();
        expDescLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        referenceLabel = new JLabel();
        scrollPane2 = new JScrollPane();
        contactLabel = new JLabel();
        scrollPane3 = new JScrollPane();
        additionalLabel = new JLabel();
        scrollPane4 = new JScrollPane();

        //======== this ========
        setFocusable(false);

        //---- accessionLabel ----
        accessionLabel.setText("Experiment Accession");
        accessionLabel.setFont(accessionLabel.getFont().deriveFont(accessionLabel.getFont().getStyle() | Font.BOLD));

        //---- expTitleLabel ----
        expTitleLabel.setText("Experiment Title");
        expTitleLabel.setFont(expTitleLabel.getFont().deriveFont(expTitleLabel.getFont().getStyle() | Font.BOLD));

        //---- shortLabel ----
        shortLabel.setText("Experiment Label");
        shortLabel.setFont(shortLabel.getFont().deriveFont(shortLabel.getFont().getStyle() | Font.BOLD));

        //---- projectLabel ----
        projectLabel.setText("Project Name");
        projectLabel.setFont(projectLabel.getFont().deriveFont(projectLabel.getFont().getStyle() | Font.BOLD));

        //---- expDescLabel ----
        expDescLabel.setText("Experiment Description");
        expDescLabel.setFont(expDescLabel.getFont().deriveFont(expDescLabel.getFont().getStyle() | Font.BOLD));

        //---- accessionField ----
        accessionField.setEditable(false);

        //---- expTitleField ----
        expTitleField.setEditable(false);

        //---- shortLabelField ----
        shortLabelField.setEditable(false);

        //---- projectField ----
        projectField.setEditable(false);

        //======== scrollPane1 ========
        {

            //---- expDescArea ----
            expDescArea.setEditable(false);
            scrollPane1.setViewportView(expDescArea);
        }

        //---- referenceLabel ----
        referenceLabel.setText("Reference");
        referenceLabel.setFont(referenceLabel.getFont().deriveFont(referenceLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setPreferredSize(new Dimension(300, 220));

            //---- referenceTable ----
            referenceTable.setPreferredScrollableViewportSize(new Dimension(400, 200));
            scrollPane2.setViewportView(referenceTable);
        }

        //---- contactLabel ----
        contactLabel.setText("Contact");
        contactLabel.setFont(contactLabel.getFont().deriveFont(contactLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane3 ========
        {

            //---- contactTable ----
            contactTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane3.setViewportView(contactTable);
        }

        //---- additionalLabel ----
        additionalLabel.setText("Additional");
        additionalLabel.setFont(additionalLabel.getFont().deriveFont(additionalLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane4 ========
        {

            //---- additionalTable ----
            additionalTable.setPreferredScrollableViewportSize(new Dimension(450, 200));
            scrollPane4.setViewportView(additionalTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup()
                                        .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(layout.createParallelGroup()
                                                        .add(accessionLabel)
                                                        .add(expTitleLabel)
                                                        .add(shortLabel)
                                                        .add(expDescLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(LayoutStyle.RELATED)
                                                .add(layout.createParallelGroup()
                                                        .add(expTitleField, GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                                                        .add(accessionField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
                                                        .add(projectField, GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                                                        .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 594, Short.MAX_VALUE)
                                                        .add(shortLabelField, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE))
                                                .add(11, 11, 11))
                                        .add(layout.createSequentialGroup()
                                                .add(projectLabel)
                                                .addContainerGap(691, Short.MAX_VALUE))
                                        .add(layout.createSequentialGroup()
                                                .add(referenceLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(615, Short.MAX_VALUE))
                                        .add(layout.createSequentialGroup()
                                                .add(contactLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(615, Short.MAX_VALUE))
                                        .add(layout.createSequentialGroup()
                                                .add(additionalLabel, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(615, Short.MAX_VALUE))
                                        .add(GroupLayout.TRAILING, layout.createSequentialGroup()
                                                .add(layout.createParallelGroup(GroupLayout.TRAILING)
                                                        .add(GroupLayout.LEADING, scrollPane4, GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                                                        .add(GroupLayout.LEADING, scrollPane3, GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
                                                        .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE))
                                                .add(11, 11, 11))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(accessionLabel)
                                        .add(accessionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(expTitleLabel)
                                        .add(expTitleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(shortLabel)
                                        .add(shortLabelField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(GroupLayout.BASELINE)
                                        .add(projectLabel)
                                        .add(projectField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .add(layout.createParallelGroup()
                                        .add(layout.createSequentialGroup()
                                                .add(18, 18, 18)
                                                .add(scrollPane1, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
                                        .add(layout.createSequentialGroup()
                                                .add(27, 27, 27)
                                                .add(expDescLabel)))
                                .add(20, 20, 20)
                                .add(referenceLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(contactLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane3, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(additionalLabel)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(scrollPane4, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                                .addContainerGap(20, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel accessionLabel;
    private JLabel expTitleLabel;
    private JLabel shortLabel;
    private JLabel projectLabel;
    private JLabel expDescLabel;
    private JTextField accessionField;
    private JTextField expTitleField;
    private JTextField shortLabelField;
    private JTextField projectField;
    private JScrollPane scrollPane1;
    private JTextPane expDescArea;
    private JLabel referenceLabel;
    private JScrollPane scrollPane2;
    private JTable referenceTable;
    private JLabel contactLabel;
    private JScrollPane scrollPane3;
    private JTable contactTable;
    private JLabel additionalLabel;
    private JScrollPane scrollPane4;
    private JTable additionalTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
