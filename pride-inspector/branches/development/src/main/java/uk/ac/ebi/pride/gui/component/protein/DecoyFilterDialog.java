package uk.ac.ebi.pride.gui.component.protein;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.table.model.AbstractProteinTableModel;
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
    private enum Type {PREFIX, POSTFIX, CONTAIN}

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
        containButton = new JRadioButton();
        criteriaLabel = new JLabel();
        criteriaTextField = new JTextField();
        decoyOnlyCheckBox = new JCheckBox();
        buttonBar = new JPanel();
        helpButton = new JButton();
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        cancelButton = new JButton();
        resetButton = new JButton();
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
                    panel1.setBorder(new TitledBorder(null, "Action", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));

                    //---- prefixRadioButton ----
                    prefixRadioButton.setText("Prefix");
                    prefixRadioButton.setSelected(true);

                    //---- postRadioButton ----
                    postRadioButton.setText("Postfix");

                    //---- containButton ----
                    containButton.setText("Contain");

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .add(panel1Layout.createSequentialGroup()
                                .add(prefixRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(70, 70, 70)
                                .add(postRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.RELATED, 59, Short.MAX_VALUE)
                                .add(containButton)
                                .add(8, 8, 8))
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .add(panel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                .add(prefixRadioButton)
                                .add(postRadioButton)
                                .add(containButton))
                    );
                }

                //---- criteriaLabel ----
                criteriaLabel.setText("Criteria");
                criteriaLabel.setHorizontalAlignment(SwingConstants.RIGHT);

                //---- decoyOnlyCheckBox ----
                decoyOnlyCheckBox.setText("Show decoy only");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .add(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(contentPanelLayout.createSequentialGroup()
                            .add(criteriaLabel, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(criteriaTextField, GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                        .add(contentPanelLayout.createSequentialGroup()
                            .add(decoyOnlyCheckBox, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .add(contentPanelLayout.createSequentialGroup()
                            .add(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(LayoutStyle.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(contentPanelLayout.createParallelGroup(GroupLayout.BASELINE)
                                .add(criteriaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .add(criteriaLabel))
                            .addPreferredGap(LayoutStyle.RELATED)
                            .add(decoyOnlyCheckBox))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpButton.setText("Help");
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- resetButton ----
                resetButton.setText("Reset");
                buttonBar.add(resetButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
        buttonGroup1.add(containButton);
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

        // reset button
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRowFilter(null);
                DecoyFilterDialog.this.setVisible(false);
            }
        });

        // ok button
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get criteria
                String criteria = criteriaTextField.getText();
                if (criteria != null && !"".equals(criteria.trim())) {
                    // get action
                    Type type;
                    if (prefixRadioButton.isSelected()) {
                        type = Type.PREFIX;
                    } else if (postRadioButton.isSelected()) {
                        type = Type.POSTFIX;
                    } else {
                        type = Type.CONTAIN;
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
                    setRowFilter(new DecoyRowFilter(type, criteria, index, decoyOnlyCheckBox.isSelected()));
                } else {
                    setRowFilter(null);
                }

                DecoyFilterDialog.this.setVisible(false);
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
     * Decoy row filter
     */
    private class DecoyRowFilter extends RowFilter {
        /**
         * Type of the matching mechanism
         */
        private Type type;

        /**
         * Matching criteria
         */
        private String criteria;
        /**
         * Index of the protein accession column
         */
        private int accIndex;
        /**
         * whether to show decoy records only
         */
        private boolean decoyOnly;

        /**
         * Constructor
         *
         * @param type     type of the matching mechanism
         * @param criteria matching criteria
         * @param accIndex index of the protein accession
         * @param decoyOnly whether to show decoy records only
         */
        private DecoyRowFilter(Type type, String criteria, int accIndex, boolean decoyOnly) {
            this.type = type;
            this.criteria = criteria.toLowerCase();
            this.accIndex = accIndex;
            this.decoyOnly = decoyOnly;
        }

        @Override
        public boolean include(Entry entry) {
            String accession = entry.getStringValue(accIndex);

            if (accession != null) {
                accession = accession.toLowerCase();
                switch (type) {
                    case PREFIX:
                        return decoyOnly ? accession.startsWith(criteria) : ! accession.startsWith(criteria);
                    case POSTFIX:
                        return decoyOnly ? accession.endsWith(criteria) : ! accession.startsWith(criteria);
                    case CONTAIN:
                        return decoyOnly ? accession.contains(criteria) : ! accession.contains(criteria);
                }
            }
            return false;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JRadioButton prefixRadioButton;
    private JRadioButton postRadioButton;
    private JRadioButton containButton;
    private JLabel criteriaLabel;
    private JTextField criteriaTextField;
    private JPanel buttonBar;
    private JButton helpButton;
    private JButton cancelButton;
    private JButton resetButton;
    private JButton okButton;
    private JCheckBox decoyOnlyCheckBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
