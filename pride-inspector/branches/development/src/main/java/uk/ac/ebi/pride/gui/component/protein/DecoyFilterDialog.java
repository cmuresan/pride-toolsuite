package uk.ac.ebi.pride.gui.component.protein;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.table.filter.DecoyAccessionFilter;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * Created by JFormDesigner on Wed Aug 31 11:14:10 BST 2011
 */


/**
 * @author User #2
 */
public class DecoyFilterDialog extends JDialog {
    /**
     * Property change event when a new filter is created
     */
    public static final String NEW_FILTER = "New Filter";

    private static final String FILTER_STRING_LABEL= "Filter String";
    private static final String PREFIX_MESSAGE= "Decoy accessions start with";
    private static final String POST_MESSAGE= "Decoy accessions end with";
    private static final String CONTAIN_MESSAGE= "Decoy accessions contain";

    /**
     * Table which contains all the protein identifications
     */
    private JTable proteinTable;

    /**
     * Pride Inspector desktop context
     */
    private PrideInspectorContext appContext;

    public DecoyFilterDialog(Frame owner, JTable proteinTable) {
        super(owner, uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title"));
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        this.proteinTable = proteinTable;
        initComponents();
        populateComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        prefixRadioButton = new JRadioButton();
        postRadioButton = new JRadioButton();
        containRadioButton = new JRadioButton();
        descriptionLabel = new JLabel();
        descriptionContentLabel = new JLabel();
        criteriaLabel = new JLabel();
        criteriaTextField = new JTextField();
        buttonBar = new JPanel();
        helpButton = new JButton();
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder(null, "Filter Action", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));

                    //---- prefixRadioButton ----
                    prefixRadioButton.setText("Prefix");
                    prefixRadioButton.setSelected(true);

                    //---- postRadioButton ----
                    postRadioButton.setText("Postfix");

                    //---- containRadioButton ----
                    containRadioButton.setText("Contain");

                    //---- descriptionLabel ----
                    descriptionLabel.setText("Description: ");

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                            panel1Layout.createParallelGroup()
                                    .add(panel1Layout.createSequentialGroup()
                                            .add(panel1Layout.createParallelGroup()
                                                    .add(panel1Layout.createSequentialGroup()
                                                            .add(prefixRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .add(70, 70, 70)
                                                            .add(postRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .add(55, 55, 55)
                                                            .add(containRadioButton))
                                                    .add(panel1Layout.createSequentialGroup()
                                                            .add(9, 9, 9)
                                                            .add(descriptionLabel)
                                                            .addPreferredGap(LayoutStyle.RELATED)
                                                            .add(descriptionContentLabel, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)))
                                            .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                            panel1Layout.createParallelGroup()
                                    .add(panel1Layout.createSequentialGroup()
                                            .add(panel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                                    .add(prefixRadioButton)
                                                    .add(postRadioButton)
                                                    .add(containRadioButton))
                                            .addPreferredGap(LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                                            .add(panel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                                    .add(descriptionLabel)
                                                    .add(descriptionContentLabel)))
                    );
                }

                //---- criteriaLabel ----
                criteriaLabel.setText("Filter String");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(panel1, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                                .add(contentPanelLayout.createSequentialGroup()
                                        .add(8, 8, 8)
                                        .add(criteriaLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(262, Short.MAX_VALUE))
                                .add(criteriaTextField, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .add(GroupLayout.TRAILING, contentPanelLayout.createSequentialGroup()
                                        .add(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(criteriaLabel)
                                        .addPreferredGap(LayoutStyle.RELATED)
                                        .add(criteriaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText("OK");
                buttonBar.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(prefixRadioButton);
        buttonGroup1.add(postRadioButton);
        buttonGroup1.add(containRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    /**
     * Populate the components with actions
     */
    private void populateComponents() {
        // cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DecoyFilterDialog.this.setVisible(false);
            }
        });

        // ok button
        okButton.addActionListener(new FilterActionListener());

        // set the default text of description
        descriptionContentLabel.setText(PREFIX_MESSAGE);

        // action listener to radio buttons
        prefixRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(PREFIX_MESSAGE);
            }
        });

        postRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(POST_MESSAGE);
            }
        });

        containRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(CONTAIN_MESSAGE);
            }
        });
    }

    /**
     * Set the row filter
     *
     * @param rowFilter a given row filter
     */
    private void setRowFilter(RowFilter rowFilter) {
        // get table model
        TableModel tableModel = proteinTable.getModel();
        RowSorter rowSorter = proteinTable.getRowSorter();
        if (rowSorter == null || !(rowSorter instanceof TableRowSorter)) {
            rowSorter = new NumberTableRowSorter(tableModel);
            proteinTable.setRowSorter(rowSorter);
        }
        ((TableRowSorter) rowSorter).setRowFilter(rowFilter);
    }

    /**
     * Action triggered when the ok button is clicked
     */
    private class FilterActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // get criteria
            String criteria = criteriaTextField.getText();
            if (criteria != null && !"".equals(criteria.trim())) {
                // get action
                DecoyAccessionFilter.Type type;
                if (prefixRadioButton.isSelected()) {
                    type = DecoyAccessionFilter.Type.PREFIX;
                } else if (postRadioButton.isSelected()) {
                    type = DecoyAccessionFilter.Type.POSTFIX;
                } else {
                    type = DecoyAccessionFilter.Type.CONTAIN;
                }

                // get accession index
                int index = -1;
                TableModel tableModel = proteinTable.getModel();
                String protAccColName = ProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
                int colCnt = tableModel.getColumnCount();
                for (int i = 0; i < colCnt; i++) {
                    if (tableModel.getColumnName(i).equals(protAccColName)) {
                        index = i;
                    }
                }

                // get criteria
                setRowFilter(new DecoyAccessionFilter(type, criteria, index, false));
                DecoyFilterDialog.this.setVisible(false);
                DecoyFilterDialog.this.firePropertyChange(NEW_FILTER, false, true);

                // reset label
                criteriaLabel.setText(FILTER_STRING_LABEL);
            } else {
                // set error message
                criteriaLabel.setText("<html><div> " + FILTER_STRING_LABEL + " <b style=\"color:#FF0000\"> (Empty String)</b></div></html>");
            }
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JRadioButton prefixRadioButton;
    private JRadioButton postRadioButton;
    private JRadioButton containRadioButton;
    private JLabel criteriaLabel;
    private JTextField criteriaTextField;
    private JPanel buttonBar;
    private JButton helpButton;
    private JButton cancelButton;
    private JButton okButton;
    private JLabel descriptionLabel;
    private JLabel descriptionContentLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
