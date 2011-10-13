package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.component.table.model.QuantProteinTableModel;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.InternetChecker;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Abstract class to to retrieve extra protein details, such as: name, status and coverage
 * <p/>
 * User: rwang
 * Date: 25/08/2011
 * Time: 10:29
 */
public class ExtraProteinDetailAction extends PrideAction {
    /**
     * data access controller
     */
    private DataAccessController controller;
    /**
     * Pride Inspector context
     */
    private PrideInspectorContext appContext;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public ExtraProteinDetailAction(DataAccessController controller) {
        super(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.small.icon")));
        this.controller = controller;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (InternetChecker.check()) {
            // set hidden protein details columns visible
            setColumnVisible();
            // start retrieval task
            startRetrieval();
        } else {
            String msg = Desktop.getInstance().getDesktopContext().getProperty("internet.connection.warning.message");
            String shortMsg = Desktop.getInstance().getDesktopContext().getProperty("internet.connection.warning.short.message");
            JOptionPane.showMessageDialog(Desktop.getInstance().getMainComponent(), msg, shortMsg, JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Set hidden columns visible
     * such as: protein name and protein sequence coverage
     */
    private void setColumnVisible() {
        // get the main GUI component for this controller
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);

        // set protein tab table columns to visible
        JTable proteinTable = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();
        TableColumnModelExt showHideColModel = (TableColumnModelExt) proteinTable.getColumnModel();
        List<TableColumn> columns = showHideColModel.getColumns(true);
        for (TableColumn column : columns) {
            Object header = column.getHeaderValue();
            if (ProteinTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(header) ||
                    ProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(header) ||
                    ProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(header)) {
                ((TableColumnExt) column).setVisible(true);
            }
        }

        // set protein tab's peptide table columns to visible
        JTable proteinPeptideTable = contentPane.getProteinTabPane().getPeptidePane().getPeptideTable();
        showHideColModel = (TableColumnModelExt) proteinPeptideTable.getColumnModel();
        columns = showHideColModel.getColumns(true);
        for (TableColumn column : columns) {
            Object header = column.getHeaderValue();
            if (PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(header)) {
                ((TableColumnExt) column).setVisible(true);
            }
        }

        // set peptide tab table columns to visible
        JTable peptideTable = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
        showHideColModel = (TableColumnModelExt) peptideTable.getColumnModel();
        columns = showHideColModel.getColumns(true);
        for (TableColumn column : columns) {
            Object header = column.getHeaderValue();
            if (PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(header) ||
                    PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(header)) {
                ((TableColumnExt) column).setVisible(true);
            }
        }

        // set quantitative tab table columns to visible
        if (contentPane.isQuantTabEnabled()) {
            JTable quantTable = contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable();
            showHideColModel = (TableColumnModelExt) quantTable.getColumnModel();
            columns = showHideColModel.getColumns(true);
            for (TableColumn column : columns) {
                Object header = column.getHeaderValue();
                if (QuantProteinTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(header) ||
                        QuantProteinTableModel.TableHeader.PROTEIN_STATUS.getHeader().equals(header) ||
                        QuantProteinTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(header)) {
                    ((TableColumnExt) column).setVisible(true);
                }
            }
        }

    }

    /**
     * Start the task to retrieve protein details
     */
    private void startRetrieval() {
        // get protein accessions
        List<String> accs = getMappedProteinAccs();

        // start a new task to retrieve protein names
        runRetrieveProteinNameTask(accs);
    }

    /**
     * Get a set of mapped protein accession from table
     *
     * @return List<String>  a list of protein accessions.
     */
    private List<String> getMappedProteinAccs() {

        // get the main GUI component for this controller
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
        JTable table = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();

        List<String> accs = new ArrayList<String>();

        int rowCount = table.getRowCount();
        int column = table.getColumnModel().getColumnIndex(PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        int selectedRow = table.getSelectedRow();
        // add selected row first
        if (selectedRow >= 0) {
            Object selectedVal = table.getValueAt(selectedRow, column);
            if (selectedVal != null) {
                accs.add((String) selectedVal);
            }
        }
        // add the rest
        for (int row = 0; row < rowCount; row++) {
            Object val = table.getValueAt(row, column);
            if (val != null && row != selectedRow && !accs.contains(val)) {
                accs.add((String) val);
            }
        }

        return accs;
    }

    /**
     * Start and run a new protein name retrieve task
     *
     * @param mappedProteinAcces a collection of mapped protein accessions.
     */
    private void runRetrieveProteinNameTask(Collection<String> mappedProteinAcces) {

        // create a task to retrieve protein name
        RetrieveProteinDetailTask task = new RetrieveProteinDetailTask(controller);

        // set task name, indicates which data access controller it is from
        task.setName(task.getName() + " (" + controller.getName() + ")");

        // assign this task to a controller
        task.addOwner(controller);

        // get the main GUI component for this controller
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);

        // add table model as a task listener
        // protein tab
        JTable table = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();
        TableModel tableModel = table.getModel();
        task.addTaskListener((TaskListener) tableModel);
        // peptide tab
        table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
        tableModel = table.getModel();
        task.addTaskListener((TaskListener) tableModel);
        // quant tab
        if (contentPane.isQuantTabEnabled()) {
            table = contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable();
            tableModel = table.getModel();
            task.addTaskListener((TaskListener) tableModel);
        }


        // gui blocker
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));

        // add task to task manager without notify
        Desktop.getInstance().getDesktopContext().addTask(task);
    }
}
