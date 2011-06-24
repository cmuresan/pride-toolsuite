package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Retrieve additional peptide details
 *
 * User: rwang
 * Date: 23/06/11
 * Time: 17:02
 */
public class RetrieveExtraPeptideDetailAction extends PrideAction{
    /**
     * JTable where protein name will be displayed
     */
    private JTable table;

    /**
     * data access controller
     */
    private DataAccessController controller;

    /**
     * Constructor
     * @param table protein table
     * @param controller    data access controller
     */
    public RetrieveExtraPeptideDetailAction(JTable table,
                                            DataAccessController controller) {
        super(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.title"),
              GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.small.icon")));
        this.table = table;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // set hidden protein details columns visible
        setColumnVisible();
        // start retrieval task
        startRetrieval();
    }

    /**
     * Set hidden columns visible
     * such as: protein name and protein sequence coverage
     */
    private void setColumnVisible() {
        TableColumnModelExt showHideColModel = (TableColumnModelExt) table.getColumnModel();
        List<TableColumn> columns = showHideColModel.getColumns(true);
        for (TableColumn column : columns) {
            if (PeptideTableModel.TableHeader.PROTEIN_NAME.getHeader().equals(column.getHeaderValue()) ||
                    PeptideTableModel.TableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(column.getHeaderValue()) ||
                    PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader().equals(column.getHeaderValue())) {
                ((TableColumnExt) column).setVisible(true);
            }
        }
    }

    /**
     * Start the task to retrieve protein details
     */
    private void startRetrieval() {
        // get protein accessions
        Set<String> accs = getMappedProteinAccs();

        // start a new task to retrieve protein names
        runRetrieveProteinNameTask(accs);
    }

    /**
     * Get a set of mapped protein accession from table
     *
     * @return Set<String>  a set of protein accessions.
     */
    private Set<String> getMappedProteinAccs() {
        Set<String> accs = new LinkedHashSet<String>();

        int rowCount = table.getRowCount();
        int column = table.getColumnModel().getColumnIndex(PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader());
        for (int row = 0; row < rowCount; row++) {
            Object val = table.getValueAt(row, column);
            if (val != null) {
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
        RetrieveProteinDetailTask task = new RetrieveProteinDetailTask(mappedProteinAcces);

        // set task name, indicates which data access controller it is from
        task.setName(task.getName() + " (" + controller.getName() + ")");

        // assign this task to a controller
        task.addOwner(controller);

        // add table model as a task listener
        TableModel tableModel = table.getModel();
        if (tableModel instanceof TaskListener) {
            task.addTaskListener((TaskListener) tableModel);
        }

        // gui blocker
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));

        // add task to task manager without notify
        Desktop.getInstance().getDesktopContext().addTask(task);
    }
}
