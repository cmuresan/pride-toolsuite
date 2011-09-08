package uk.ac.ebi.pride.gui.component.quant;

import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.MetaData;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.QuantProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.task.impl.ExportTableDataTask;
import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.EnsemblSpeciesMapper;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.NumberUtilities;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static uk.ac.ebi.pride.gui.utils.Constants.DOT;
import static uk.ac.ebi.pride.gui.utils.Constants.TAB_SEP_FILE;
/*
 * Created by JFormDesigner on Mon Aug 22 14:56:28 BST 2011
 */


/**
 * @author User #2
 */
public class QuantExportDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(QuantExportDialog.class);

    private final static String UP_REGULATED = "Up Regulated";
    private final static String DOWN_REGULATED = "Down Regulated";

    private PrideInspectorContext appContext;
    private JTable table;
    private DataAccessController controller;

    public QuantExportDialog(Frame owner, JTable table, DataAccessController controller) {
        super(owner);
        this.table = table;
        this.controller = controller;
        setupMainPane();
        initComponents();
        populateComponents();
    }

    private void setupMainPane() {
        this.appContext = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setTitle(appContext.getProperty("export.quantitative.data.long.title"));
        ImageIcon dialogIcon = (ImageIcon) GUIUtilities.loadIcon(appContext.getProperty("export.quantitative.data.small.icon"));
        this.setIconImage(dialogIcon.getImage());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        panel1 = new JPanel();
        scrollPane2 = new JScrollPane();
        proteinTable = createProteinTable();
        ensemblButton = new JButton();
        exportButton = new JButton();
        label5 = new JLabel();
        panel2 = new JPanel();
        reagentLabel = new JLabel();
        reagentComboBox = new JComboBox();
        regulationLabel = new JLabel();
        regulationComboBox = new JComboBox();
        percentageLabel = new JLabel();
        percentageTextField = new JTextField();
        filterButton = new JButton();
        closeButton = new JButton();
        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));

        //======== this ========
        Container contentPane = getContentPane();

        //======== panel1 ========
        {
            panel1.setBorder(null);

            //======== scrollPane2 ========
            {
                scrollPane2.setViewportView(proteinTable);
            }

            //---- ensemblButton ----
            ensemblButton.setText("Ensembl View");

            //---- exportButton ----
            exportButton.setText("Export");

            //---- label5 ----
            label5.setText("Protein");

            GroupLayout panel1Layout = new GroupLayout(panel1);
            panel1.setLayout(panel1Layout);
            panel1Layout.setHorizontalGroup(
                    panel1Layout.createParallelGroup()
                            .add(panel1Layout.createSequentialGroup()
                                    .add(8, 8, 8)
                                    .add(panel1Layout.createParallelGroup()
                                            .add(label5, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
                                            .add(panel1Layout.createSequentialGroup()
                                                    .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
                                                    .addPreferredGap(LayoutStyle.RELATED)
                                                    .add(panel1Layout.createParallelGroup(GroupLayout.TRAILING)
                                                            .add(ensemblButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                                                            .add(exportButton, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE))))
                                    .addContainerGap())
            );
            panel1Layout.setVerticalGroup(
                    panel1Layout.createParallelGroup()
                            .add(panel1Layout.createSequentialGroup()
                                    .add(label5)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(panel1Layout.createParallelGroup()
                                            .add(panel1Layout.createSequentialGroup()
                                                    .add(68, 68, 68)
                                                    .add(exportButton)
                                                    .add(18, 18, 18)
                                                    .add(ensemblButton))
                                            .add(GroupLayout.TRAILING, panel1Layout.createSequentialGroup()
                                                    .add(6, 6, 6)
                                                    .add(scrollPane2, GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)))
                                    .addContainerGap())
            );
        }

        //======== panel2 ========
        {
            panel2.setBorder(new TitledBorder("Filter"));

            //---- reagentLabel ----
            reagentLabel.setText("Reagent Ratio");

            //---- regulationLabel ----
            regulationLabel.setText("Regulation");

            //---- percentageLabel ----
            percentageLabel.setText("Percentage (%)");

            //---- filterButton ----
            filterButton.setText("Filter");
            filterButton.setEnabled(true);

            GroupLayout panel2Layout = new GroupLayout(panel2);
            panel2.setLayout(panel2Layout);
            panel2Layout.setHorizontalGroup(
                    panel2Layout.createParallelGroup()
                            .add(panel2Layout.createSequentialGroup()
                                    .add(12, 12, 12)
                                    .add(panel2Layout.createParallelGroup()
                                            .add(reagentLabel)
                                            .add(reagentComboBox, 0, 202, Short.MAX_VALUE))
                                    .add(25, 25, 25)
                                    .add(panel2Layout.createParallelGroup()
                                            .add(regulationComboBox, 0, 160, Short.MAX_VALUE)
                                            .add(regulationLabel))
                                    .add(24, 24, 24)
                                    .add(panel2Layout.createParallelGroup()
                                            .add(panel2Layout.createSequentialGroup()
                                                    .add(percentageTextField, GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                                                    .add(18, 18, 18)
                                                    .add(filterButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                                            .add(percentageLabel))
                                    .addContainerGap())
            );
            panel2Layout.setVerticalGroup(
                    panel2Layout.createParallelGroup()
                            .add(panel2Layout.createSequentialGroup()
                                    .add(panel2Layout.createParallelGroup(GroupLayout.BASELINE)
                                            .add(reagentLabel)
                                            .add(regulationLabel)
                                            .add(percentageLabel))
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(panel2Layout.createParallelGroup()
                                            .add(panel2Layout.createParallelGroup(GroupLayout.BASELINE)
                                                    .add(filterButton)
                                                    .add(regulationComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                    .add(percentageTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                            .add(reagentComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(8, Short.MAX_VALUE))
            );
        }

        //---- closeButton ----
        closeButton.setText("Close");

        //---- helpLabel ----


        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .add(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .add(contentPaneLayout.createParallelGroup()
                                        .add(contentPaneLayout.createSequentialGroup()
                                                .add(contentPaneLayout.createParallelGroup()
                                                        .add(GroupLayout.TRAILING, panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(panel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .add(GroupLayout.TRAILING, closeButton))
                                                .addContainerGap())
                                        .add(GroupLayout.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .add(helpButton)
                                                .add(8, 8, 8))))
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .add(GroupLayout.TRAILING, contentPaneLayout.createSequentialGroup()
                                .add(helpButton)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(panel2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(closeButton)
                                .addContainerGap())
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private void populateComponents() {
        // filter options

        // configure protein table
        proteinTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        proteinTable.setRowSelectionAllowed(false);
        proteinTable.setColumnSelectionAllowed(true);
        proteinTable.setSelectionForeground(Color.red);
        proteinTable.setSelectionBackground(Color.red);

        // get all the reagents
        populateReagentComboBox();

        // get all the regulations
        regulationComboBox.addItem(UP_REGULATED);
        regulationComboBox.addItem(DOWN_REGULATED);

        // ensembl button action
        ensemblButton.addActionListener(new EnsemblActionListener());

        // export button action
        exportButton.addActionListener(new ExportActionListener());

        // filter button action
        filterButton.addActionListener(new FilterActionListener());

        // close button action
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QuantExportDialog.this.dispose();
            }
        });

    }

    /**
     * Populate the protein table with initial values
     *
     * @return JTable  a new jtable
     */
    private JTable createProteinTable() {
        QuantProteinTableModel tableModel = new QuantProteinTableModel(controller);
        tableModel.removeAllColumns();

        // get the existing quant protein table model
        QuantProteinTableModel existingTableModel = (QuantProteinTableModel) table.getModel();

        // get compre column index
        int compareColumnIndex = existingTableModel.getColumnIndex(QuantProteinTableModel.TableHeader.COMPARE.getHeader());

        // copy all the columns
        int colCnt = existingTableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            if (i != compareColumnIndex) {
                tableModel.addColumn(existingTableModel.getColumnName(i), existingTableModel.getColumnTooltip(i));
            }
        }

        // copy all the data
        int rowCnt = existingTableModel.getRowCount();
        for (int i = 0; i < rowCnt; i++) {
            java.util.List<Object> row = existingTableModel.getRow(i);
            row.remove(compareColumnIndex);
            tableModel.addRow(row);
        }

        return TableFactory.createQuantProteinTable(tableModel);
    }

    /**
     * Set the initial values of the reagent combo box
     */
    private void populateReagentComboBox() {
        TableModel tableModel = table.getModel();
        int colCnt = tableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            String header = tableModel.getColumnName(i);
            if (header.contains("/")) {
                reagentComboBox.addItem(header);
                filterButton.setEnabled(true);
            }
        }
    }

    /**
     * Triggered when ensembl button is clicked
     */
    private class EnsemblActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int rowCnt = proteinTable.getRowCount();
            if (rowCnt > 0) {
                int protColIndex = -1;
                int mappedProtColIndex = -1;
                int colCnt = proteinTable.getColumnCount();
                for (int i = 0; i < colCnt; i++) {
                    String colName = proteinTable.getColumnName(i);
                    if (colName.equals(QuantProteinTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader())) {
                        protColIndex = i;
                    } else if (colName.equals(QuantProteinTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader())) {
                        mappedProtColIndex = i;
                    }
                }

                if (protColIndex >= 0 && mappedProtColIndex >= 0) {
                    String url = getBaseURL();
                    if (url != null) {
                        for (int i = 0; i < rowCnt; i++) {
                            String prot = (String) proteinTable.getValueAt(i, mappedProtColIndex);
                            if (prot == null) {
                                prot = (String) proteinTable.getValueAt(i, protColIndex);
                            }

                            if (prot != null) {
                                url += ";id=" + prot;
                            }
                        }
                        HttpUtilities.openURL(url);
                    } else {
                        // show warning message
                        Runnable code = new Runnable() {

                            @Override
                            public void run() {
                                GUIUtilities.warn(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getMainComponent(),
                                        "Experiment species is not supported by Ensembl Karyotype", "Unknown Species");
                            }
                        };
                        try {
                            EDTUtils.invokeAndWait(code);
                        } catch (InvocationTargetException e1) {
                            logger.error("Failed to show warning message", e1);
                        } catch (InterruptedException e1) {
                            logger.error("Failed to show warning message", e1);
                        }
                    }
                }
            }
        }

        /**
         * Get the base url to connect to Ensembl karyotype
         *
         * @return String  url string
         */
        private String getBaseURL() {
            String url = null;

            try {
                MetaData metaData = controller.getMetaData();
                java.util.List<String> speciesIds = DataAccessUtilities.getTaxonomy(metaData);
                if (speciesIds.size() > 0) {
                    url = appContext.getProperty("ensembl.genome.browser.url");
                    String ensemblSpeciesName = EnsemblSpeciesMapper.getInstance().getEnsemblName(speciesIds.get(0));
                    url = String.format(url, ensemblSpeciesName);
                }
            } catch (DataAccessException e) {
                logger.error("Failed to retrieve metadata", e);
            }

            return url;
        }
    }

    /**
     * Triggered when export button is clicked
     */
    private class ExportActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            DataAccessController controller = appContext.getForegroundDataAccessController();
            String defaultFileName = controller.getName().split("\\" + DOT)[0] + "_quantitative_data";
            SimpleFileDialog ofd = new SimpleFileDialog(appContext.getOpenFilePath(), "Export Quantitative Data", defaultFileName, false, TAB_SEP_FILE);
            ofd.setMultiSelectionEnabled(false);
            int result = ofd.showDialog(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getMainComponent(), null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = ofd.getSelectedFile();
                // store file path for reuse
                String filePath = selectedFile.getPath();
                appContext.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
                ExportTableDataTask newTask = new ExportTableDataTask(proteinTable,
                        filePath + (filePath.endsWith(TAB_SEP_FILE) ? "" : TAB_SEP_FILE),
                        "Export Protein Quantification", "Export Protein Quantification");
                // set task's gui blocker
                newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().addTask(newTask);
            }
        }
    }

    /**
     * Triggered when filter button is clicked
     */
    private class FilterActionListener implements ActionListener {
        private String label;

        private FilterActionListener() {
            label = percentageLabel.getText();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // valid the input
            String input = percentageTextField.getText();

            if (NumberUtilities.isNumber(input)) {
                float percentage = Float.parseFloat(input);
                if (percentage < 0) {
                    setWarningMessage();
                } else {
                    // set the original message
                    removeWarningMessage();
                    // add filter to table
                    addFilter(percentage);
                    // highlight selected reagent column
                    highlightColumn();

                }
            } else {
                setWarningMessage();
            }
        }

        private void addFilter(float percentage) {
            // get reagent
            String reagent = (String) reagentComboBox.getSelectedItem();


            // get regulation
            String regulation = (String) regulationComboBox.getSelectedItem();
            boolean upRegulated = regulation.equals(UP_REGULATED);

            // get table model
            TableModel tableModel = proteinTable.getModel();
            RowSorter rowSorter = proteinTable.getRowSorter();
            if (rowSorter == null || !(rowSorter instanceof TableRowSorter)) {
                rowSorter = new NumberTableRowSorter(tableModel);
                table.setRowSorter(rowSorter);
            }
            ((TableRowSorter) rowSorter).setRowFilter(new ReagentRowFilter(reagent, percentage, upRegulated));

        }

        private void highlightColumn() {
            // get reagent
            String reagent = (String) reagentComboBox.getSelectedItem();
            // get the column index
            int columnIndex = -1;
            int colCnt = proteinTable.getColumnCount();
            for (int i = 0; i < colCnt; i++) {
                if (proteinTable.getColumnName(i).equals(reagent)) {
                    columnIndex = i;
                }
            }
            proteinTable.setColumnSelectionInterval(columnIndex, columnIndex);
        }

        private void setWarningMessage() {
            percentageLabel.setText("<html>" + label + "<div style=\"color:#FF0000\">Positive Number Only</div></html>");
        }

        private void removeWarningMessage() {
            percentageLabel.setText(label);
        }
    }

    /**
     * Reagent row filter
     */
    private class ReagentRowFilter extends RowFilter<Object, Object> {

        private int columnIndex;
        private boolean upRegulated;
        private float value;

        private ReagentRowFilter(String reagent, float percentage, boolean upRegulated) {
            // get the column index
            TableModel tableModel = proteinTable.getModel();
            int colCnt = tableModel.getColumnCount();
            for (int i = 0; i < colCnt; i++) {
                if (tableModel.getColumnName(i).equals(reagent)) {
                    this.columnIndex = i;
                }
            }
            // calculate reference value
            this.upRegulated = upRegulated;
            if (upRegulated) {
                this.value = 1 + 1 * (percentage / 100);
            } else {
                this.value = 1 - 1 * (percentage / 100);
            }
        }

        @Override
        public boolean include(Entry<? extends Object, ? extends Object> entry) {
            Object entryValue = entry.getValue(columnIndex);

            if (entryValue != null) {
                float floatValue = Float.parseFloat(entryValue.toString());
                if ((upRegulated && floatValue >= value) || (!upRegulated && floatValue <= value)) {
                    return true;
                }
            }

            return false;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel panel1;
    private JScrollPane scrollPane2;
    private JTable proteinTable;
    private JButton ensemblButton;
    private JButton exportButton;
    private JLabel label5;
    private JPanel panel2;
    private JLabel reagentLabel;
    private JComboBox reagentComboBox;
    private JLabel regulationLabel;
    private JComboBox regulationComboBox;
    private JLabel percentageLabel;
    private JTextField percentageTextField;
    private JButton filterButton;
    private JButton closeButton;
    private JButton helpButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

