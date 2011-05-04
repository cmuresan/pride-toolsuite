package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.GelFreeIdentification;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.table.listener.EntryUpdateSelectionListener;
import uk.ac.ebi.pride.gui.component.table.model.DynamicDataTableModel;
import uk.ac.ebi.pride.gui.component.table.model.IdentificationTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskManager;
import uk.ac.ebi.pride.gui.task.impl.RetrieveIdentificationTask;
import uk.ac.ebi.pride.gui.task.impl.UpdateForegroundEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;

/**
 * IdentificationSelectionPane displays
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 10:20:51
 */
public class IdentificationSelectionPane extends DataAccessControllerPane<Identification, Void> {

    private JTable identTable;
    private PrideViewerContext context;

    public IdentificationSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        // set layout
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void addComponents() {
        // create identification table
        identTable = new JTable();
        IdentificationTableModel tableModel = new IdentificationTableModel();
        identTable.setModel(tableModel);
        identTable.setRowSorter(new NumberTableRowSorter(tableModel));
        tableModel.addTableModelListener(new EntryUpdateSelectionListener(identTable));
        identTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel selectionModel = identTable.getSelectionModel();
        selectionModel.addListSelectionListener(new IdentificationSelectionListener(identTable));
        identTable.setFillsViewportHeight(true);
        // hide the protein id column
        TableColumnModel columnModel = identTable.getColumnModel();
        TableColumn proteinIdColumn = identTable.getColumn(IdentificationTableModel.TableHeader.PROTEIN_UNIQUE_ID.getHeader());
        columnModel.removeColumn(proteinIdColumn);
        // prevent dragging of column
        identTable.getTableHeader().setReorderingAllowed(false);
        updateTable();
        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void updateTable() {
        DynamicDataTableModel tableModel = (DynamicDataTableModel) identTable.getModel();
        TaskManager taskMgr = context.getTaskManager();
        // stop any running retrieving task
        taskMgr.removeTaskListener(tableModel);
        // clear all the data in table model
        tableModel.removeAllRows();
        RetrieveIdentificationTask retrieveTask = new RetrieveIdentificationTask(this.getController());
        retrieveTask.addTaskListener(tableModel);
        retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
        // execute task
        taskMgr.addTask(retrieveTask);
    }

    @Override
    protected void updatePropertyChange() {
        updateTable();
    }

    @Override
    public void succeed(TaskEvent<Identification> identTaskEvent) {
        this.firePropertyChange(DataAccessController.IDENTIFICATION_TYPE, null, identTaskEvent.getValue());
    }

    private class IdentificationSelectionListener implements ListSelectionListener {
        private JTable table;

        private IdentificationSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    // get table model
                    IdentificationTableModel tableModel = (IdentificationTableModel) table.getModel();
                    // get index of the identifcation type column
                    int columnNum = tableModel.getColumnIndex(IdentificationTableModel.TableHeader.IDENTIFICATION_TYPE_COLUMN.getHeader());
                    // get identification type
                    String type = (String) table.getValueAt(rowNum, columnNum);
                    Class classType = DataAccessController.GEL_FREE_IDENTIFICATION_TYPE.equals(type) ? GelFreeIdentification.class : TwoDimIdentification.class;
                    //TODO: check with Rui, is that right ??
                    columnNum = tableModel.getColumnIndex(IdentificationTableModel.TableHeader.PROTEIN_UNIQUE_ID.getHeader());
                    Task newTask = new UpdateForegroundEntryTask(IdentificationSelectionPane.this.getController(), classType, (String) tableModel.getValueAt(table.convertRowIndexToModel(rowNum), columnNum));
                    newTask.addTaskListener(IdentificationSelectionPane.this);
                    newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                    // add task listeners
                    uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
                }
            }
        }
    }
}
