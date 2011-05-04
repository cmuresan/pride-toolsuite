package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.gui.component.model.IdentificationTableModel;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveIdentificationTask;
import uk.ac.ebi.pride.gui.task.impl.UpdateForegroundEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * IdentificationSelectionPane displays 
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 10:20:51
 */
public class IdentificationSelectionPane extends JPanel implements TaskListener<Identification, Void> {
    private DataAccessController controller = null;
    private PropertyChangeHelper propHelper = null;

    public IdentificationSelectionPane(DataAccessController controller) {
        this.controller = controller;
        this.propHelper = new PropertyChangeHelper(this);
        initialize();
    }

    private void initialize() {
        // set layout
        this.setLayout(new BorderLayout());

        // create identification table
        JTable identTable = new JTable();
        IdentificationTableModel tableModel = new IdentificationTableModel();
        identTable.setModel(tableModel);
        identTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        identTable.getSelectionModel().addListSelectionListener(new IdentificationSelectionListener(identTable));
        identTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane, BorderLayout.CENTER);

        // create identifcation retrieval task
        RetrieveIdentificationTask retrieveTask = new RetrieveIdentificationTask(controller);
        retrieveTask.addTaskListener(tableModel);

        // execute task
        retrieveTask.execute();
    }


    public void addIdentificationListener(PropertyChangeListener listener) {
        propHelper.addPropertyChangeListener(listener);
    }

    public void removeIdentificationListener(PropertyChangeListener listener) {
        propHelper.removePropertyChangeListener(listener);
    }

    public void notifyIdentificationChangeEvent(Identification ident) {
        propHelper.firePropertyChange(DataAccessController.IDENTIFICATION_TYPE, null, ident);
    }

    @Override
    public void process(TaskEvent<List<Void>> listTaskEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void finished(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void succeed(TaskEvent<Identification> identTaskEvent) {
        Identification ident = identTaskEvent.getValue();
        notifyIdentificationChangeEvent(ident);
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private class IdentificationSelectionListener implements ListSelectionListener {
        private JTable table =  null;

        private IdentificationSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int rowNum = table.getSelectedRow();
            // get table model
            TableModel tableModel = table.getModel();
            if (tableModel instanceof IdentificationTableModel) {
                // get index of the identification accession column
                int columnNum = ((IdentificationTableModel) tableModel).getColumnIndex(IdentificationTableModel.TableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
                // get identification accession
                String acc = (String) table.getValueAt(rowNum, columnNum);
                // get index of the identifcation type column
                columnNum = ((IdentificationTableModel) tableModel).getColumnIndex(IdentificationTableModel.TableHeader.IDENTIFICATION_TYPE_COLUMN.getHeader());
                // get identification type
                String type = (String) table.getValueAt(rowNum, columnNum);
                Task newTask = new UpdateForegroundEntryTask(controller, acc, type);
                newTask.addTaskListener(IdentificationSelectionPane.this);
                newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
            }
        }
    }
}
