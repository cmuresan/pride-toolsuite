package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinDetailTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.util.InternetChecker;

import javax.swing.*;
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
public abstract class ExtraProteinDetailAction extends PrideAction {
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
     *
     * @param table      protein table
     * @param controller data access controller
     */
    public ExtraProteinDetailAction(JTable table,
                                    DataAccessController controller) {
        super(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.title"),
                GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("load.protein.detail.small.icon")));
        this.table = table;
        this.controller = controller;
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
     * Get the table where the details will be added
     * @return  JTable  data table
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Get data access controller who owns this task and action
     * @return  DataAccessController    data access controller
     */
    public DataAccessController getController() {
        return controller;
    }

    /**
     * Set hidden columns visible
     * such as: protein name and protein sequence coverage
     */
    protected abstract void setColumnVisible();

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
