package uk.ac.ebi.pride.gui.component;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.component.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 11:26:45
 */
public class PeptideSelectionPane extends JPanel implements TaskListener<Spectrum, Void>, PropertyChangeListener {

    private DataAccessController controller = null;
    private PeptideTableModel pepTableModel = null;
    private PropertyChangeHelper propHelper = null;

    public PeptideSelectionPane(DataAccessController controller) {
        this.controller = controller;
        propHelper = new PropertyChangeHelper(this);
        initialize();
    }

    private void initialize() {
        // set layout
        this.setLayout(new BorderLayout());
        // create identification table
        JTable peptideTable = new JTable();
        pepTableModel = new PeptideTableModel();
        peptideTable.setModel(pepTableModel);
        peptideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peptideTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(peptideTable));
        peptideTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(peptideTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public void addPeptideChangeListener(PropertyChangeListener listener) {
        propHelper.addPropertyChangeListener(listener);
    }

    public void removePeptideChangeListener(PropertyChangeListener listener) {
        propHelper.removePropertyChangeListener(listener);
    }

    public void notifyPeptideChangeEvent(Spectrum spectrum) {
        propHelper.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, spectrum);
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
    public void succeed(TaskEvent<Spectrum> spectrumTaskEvent) {
        Spectrum spectrum = spectrumTaskEvent.getValue();
        if (spectrum != null) {
            this.notifyPeptideChangeEvent(spectrum);
        }
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();

        if (DataAccessController.IDENTIFICATION_TYPE.equals(evtName)) {
            Identification newIdent = (Identification)evt.getNewValue();
            if (pepTableModel.getRowCount() > 0) {
                pepTableModel.removeAllRows();
            }
            pepTableModel.addData(newIdent);
            pepTableModel.fireTableDataChanged();
        }
    }


    private class PeptideSelectionListener implements ListSelectionListener {
        private JTable table = null;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int rowNum = table.getSelectedRow();
            // get table model
            TableModel tableModel = table.getModel();
            if (tableModel instanceof PeptideTableModel) {
                // get spectrum reference column
                int columnNum = ((PeptideTableModel) tableModel).getColumnIndex(PeptideTableModel.TableHeader.SPECTRUM_REFERENCE_COLUMN.getHeader());
                // get spectrum id
                String specId = (String)table.getValueAt(rowNum, columnNum);
                if (specId != null && !specId.equals("-1")) {
                    Task newTask = new RetrieveEntryTask(controller, specId, DataAccessController.SPECTRUM_TYPE);
                    newTask.addTaskListener(PeptideSelectionPane.this);
                    newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                    // add task listeners
                    uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
                }
            }
        }
    }
}
