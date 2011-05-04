package uk.ac.ebi.pride.gui.component.ident;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.GelFreeIdentification;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;
import uk.ac.ebi.pride.data.utils.LoggerUtils;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.AlterRowColorTable;
import uk.ac.ebi.pride.gui.component.table.listener.EntryUpdateSelectionListener;
import uk.ac.ebi.pride.gui.component.table.model.IdentificationTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProgressiveUpdateTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.RowNumberRenderer;
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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * IdentificationSelectionPane displays
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 10:20:51
 */
public class IdentificationSelectionPane extends DataAccessControllerPane<Identification, Void> {

    private static final Logger logger = Logger.getLogger(IdentTabPane.class.getName());

    private AlterRowColorTable identTable;
    private PrideViewerContext context;
    private IdentificationTableModel tableModel;
    private static final int OFFSET = 100; //constant to retrieve only those elements from the database
    private int start;

    public IdentificationSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        // set layout
        this.setLayout(new BorderLayout());
        start = 0;
    }

    @Override
    protected void addComponents() {
        // create identification table
        identTable = new AlterRowColorTable();
        tableModel = new IdentificationTableModel();
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
        // set row number
        TableColumn rowNumColumn = identTable.getColumn(IdentificationTableModel.TableHeader.ROW_NUMBER_COLUMN.getHeader());
        int rowColumnNum = rowNumColumn.getModelIndex();
        rowNumColumn.setCellRenderer(new RowNumberRenderer());
        identTable.getColumnModel().getColumn(rowColumnNum).setMaxWidth(40);
        identTable.setOmitColumn(rowColumnNum);
        // prevent dragging of column
        identTable.getTableHeader().setReorderingAllowed(false);
        // remvoe border
        identTable.setBorder(BorderFactory.createEmptyBorder());
        updateTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        AdjustmentListener listener = new FetchDataListener();
        scrollPane.getVerticalScrollBar().addAdjustmentListener(listener);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private class FetchDataListener implements AdjustmentListener {

        public void adjustmentValueChanged(AdjustmentEvent evt) {
            JScrollBar vbar = (JScrollBar) evt.getSource();

            Adjustable source = evt.getAdjustable();

            if (evt.getValueIsAdjusting()) {
                return;
            }
            int orient = source.getOrientation();
            if (orient == Adjustable.VERTICAL) {
                if (vbar.getValue() != 0 && (vbar.getValue() + vbar.getVisibleAmount() == vbar.getMaximum())) {
                    try {
                        if (controller.getTwoDimIdentIds().size() + controller.getGelFreeIdentIds().size() > tableModel.getRowCount()) {
                            updateTable(tableModel);
                        }
                    }
                    catch (DataAccessException e) {
                        LoggerUtils.error(logger, this, e); //To change body of catch statement use File | Settings | File Templates.

                    }
                }
            }
        }
    }

    @Override
    protected void updatePropertyChange() {
        tableModel.removeAllRows();
        start = 0;
        this.firePropertyChange(DataAccessController.IDENTIFICATION_TYPE, "", null);
        updateTable(tableModel);
    }

    private void updateTable(ProgressiveUpdateTableModel tableModel) {
        //  DynamicDataTableModel tableModel = (DynamicDataTableModel) identTable.getModel();
        TaskManager taskMgr = context.getTaskManager();
        // stop any running retrieving task
        java.util.List<Task> existingTask = taskMgr.getTask(tableModel);
        for (Task task : existingTask) {
            taskMgr.cancelTask(task, true);
        }
        // clear all the data in table model
        // tableModel.removeAllRows();
        //RetrieveIdentificationTask retrieveTask = new RetrieveIdentificationTask(this.getController());
        RetrieveIdentificationTask retrieveTask = new RetrieveIdentificationTask(this.getController(), start, OFFSET);
        retrieveTask.addTaskListener(tableModel);
        retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
        // execute task
        taskMgr.addTask(retrieveTask);
        start += OFFSET;
    }

    @Override
    public void succeed(TaskEvent<Identification> identTaskEvent) {
        this.firePropertyChange(DataAccessController.IDENTIFICATION_TYPE, null, identTaskEvent.getValue());
    }

    private class IdentificationSelectionListener implements ListSelectionListener {
        private final JTable table;

        private IdentificationSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                int rowCnt = table.getRowCount();
                if (rowCnt <= 0) {
                    //todo: update peptide table                    
                } else if (rowNum >= 0) {
                    // get table model
                    //tableModel = (IdentificationTableModel) table.getModel();
                    // get index of the identifcation type column
                    int columnNum = tableModel.getColumnIndex(IdentificationTableModel.TableHeader.IDENTIFICATION_TYPE_COLUMN.getHeader());
                    // get identification type
                    String type = (String) table.getValueAt(rowNum, columnNum);
                    Class classType = DataAccessController.GEL_FREE_IDENTIFICATION_TYPE.equals(type) ? GelFreeIdentification.class : TwoDimIdentification.class;
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
