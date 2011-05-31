package uk.ac.ebi.pride.gui.action.impl;

import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.table.TableColumnModelExt;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveProteinNameTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 09-Oct-2010
 * Time: 18:02:21
 */
public class RetrieveProteinNameAction extends PrideAction {
    /**
     * JTable where protein name will be displayed
     */
    private JTable table;

    /**
     * the column name for protein name
     */
    private String protNameColHeader;

    /**
     * the column name for protein accession
     */
    private String protAccColHeader;
    /**
     * data access controller
     */
    private DataAccessController controller;

    public RetrieveProteinNameAction(JTable table,
                                     String protNameColHeader,
                                     String protAccColHeader,
                                     DataAccessController controller,
                                     Icon icon, String title) {
        super(title, icon);
        this.table = table;
        this.protNameColHeader = protNameColHeader;
        this.protAccColHeader = protAccColHeader;
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // set protein names column enabled
        TableColumnModel columnModel = table.getColumnModel();
        if (columnModel instanceof TableColumnModelExt) {
            // show protein name column
            TableColumnModelExt showHideColModel = (TableColumnModelExt) columnModel;
            List<TableColumn> columns = showHideColModel.getColumns(true);
            for (TableColumn column : columns) {
                if (protNameColHeader.equals(column.getHeaderValue())) {
                    ((TableColumnExt) column).setVisible(true);
                    break;
                }
            }

            // get protein accessions
            Set<String> accs = getMappedProteinAccs();

            // start a new task to retrieve protein names
            runRetrieveProteinNameTask(accs);
        }
    }

    /**
     * Get a set of mapped protein accession from table
     *
     * @return Set<String>  a set of protein accessions.
     */
    private Set<String> getMappedProteinAccs() {
        Set<String> accs = new LinkedHashSet<String>();

        int rowCount = table.getRowCount();
        int column = table.getColumnModel().getColumnIndex(protAccColHeader);
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
        RetrieveProteinNameTask task = new RetrieveProteinNameTask(mappedProteinAcces);

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
        uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().addTask(task);
    }
}
