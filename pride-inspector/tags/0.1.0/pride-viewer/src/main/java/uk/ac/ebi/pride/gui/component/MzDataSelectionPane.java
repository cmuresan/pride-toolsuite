package uk.ac.ebi.pride.gui.component;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.component.model.ChromatogramTableModel;
import uk.ac.ebi.pride.gui.component.model.DynamicDataTableModel;
import uk.ac.ebi.pride.gui.component.model.SpectrumTableModel;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveMzGraphTask;
import uk.ac.ebi.pride.gui.task.impl.UpdateForegroundEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHelper;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;

/**
 * MzDataSelectionPane contains two tabs: one for spectra and one for chromatogram.
 * 
 * 1. It listens the PropertyChangeEvent from the background DataAccessController.
 * 2. It notifies all parties (components) listening on the changes with appropriate data.
 * 
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:55:11
 */
public class MzDataSelectionPane extends JPanel implements TaskListener<MzGraph, Void>, PropertyChangeListener {
    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());
    
    private DataAccessController controller = null;
    private JTabbedPane tabPane =  null;
    private PropertyChangeHelper propHelper = null;

    public MzDataSelectionPane(DataAccessController controller) {
        this.controller = controller;
        this.propHelper = new PropertyChangeHelper(this);
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(120, 120));
        initialize();
    }


    private void initialize() {
        try {
            // create selection pane
            tabPane = new JTabbedPane();
            // init spectra and chromatogram select pane
            initializeSpectrumTable();
            initializeChromatogramTable();
            // add tab change listener
            tabPane.addChangeListener(new MzDataTabChangeListener());

            this.add(tabPane, BorderLayout.CENTER);

        } catch(DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            // ToDo: is this right?    
        }
    }

    private void initializeSpectrumTable() throws DataAccessException {
        Collection<String> spectrumIds = controller.getSpectrumIds();
        if (spectrumIds != null && !spectrumIds.isEmpty()) {
            // create spectra retrieval task, no GUI blocker
            RetrieveMzGraphTask retrieveTask = new RetrieveMzGraphTask<Spectrum>(controller, Spectrum.class);
            // create table model
            SpectrumTableModel spectrumTableModel = new SpectrumTableModel();
            retrieveTask.addTaskListener(spectrumTableModel);
            // create table
            buildTable(spectrumTableModel, DataAccessController.SPECTRUM_TYPE + "(" + spectrumIds.size() + ")", DataAccessController.SPECTRUM_TYPE);
            // start running the task
            retrieveTask.execute();
        }
    }

    private void initializeChromatogramTable() throws DataAccessException {
        Collection<String> chromaIds = controller.getChromatogramIds();
        if (chromaIds != null && !chromaIds.isEmpty()) {
            // create chromatogram retrieval task, no GUI blocker
            RetrieveMzGraphTask retrieveTask = new RetrieveMzGraphTask<Chromatogram>(controller, Chromatogram.class);
            // create table model
            ChromatogramTableModel chromaTableModel = new ChromatogramTableModel();
            retrieveTask.addTaskListener(chromaTableModel);
            // create table
            buildTable(chromaTableModel, DataAccessController.CHROMATOGRAM_TYPE + "(" + chromaIds.size() + ")", DataAccessController.CHROMATOGRAM_TYPE);
            // start running the task
            retrieveTask.execute();
        }
    }

    private JTable buildTable(TableModel tableModel, String title, String type) {
        JTable table = new JTable();

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //table.getSelectionModel().setSelectionInterval(0, 0);
        table.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(table, type));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tabPane.addTab(title, scrollPane);

        return table;
    }

    public void addMzGraphChangeListener(PropertyChangeListener listener) {
        propHelper.addPropertyChangeListener(listener);
    }

    public void removeMzGraphChangeListener(PropertyChangeListener listener) {
        propHelper.removePropertyChangeListener(listener);
    }

    public void notifyMzGraphChangeEvent(MzGraph newMzGraph) {
        propHelper.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, newMzGraph);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        if (DataAccessController.FOREGROUND_EXPERIMENT_CHANGED.equals(evtName)) {
            try {
                tabPane.removeAll();
                initializeSpectrumTable();
                initializeChromatogramTable();
            } catch(DataAccessException dex) {
                String msg = String.format("%s failed on : %s", this, dex);
                logger.log(Level.ERROR, msg, dex);
                // ToDo: is this right?    
            }
        }
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
    public void succeed(TaskEvent<MzGraph> mzGraphTaskEvent) {
        MzGraph mzGraph = mzGraphTaskEvent.getValue();
        notifyMzGraphChangeEvent(mzGraph);
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
    private class MzDataTable extends JTable {
        private String type = null;
        private boolean hasDefaultSelection = false;

        public MzDataTable(String type) {
            this.type = type;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            super.tableChanged(e);

            if (!hasDefaultSelection) {
                Object source = e.getSource();
                if (source instanceof DynamicDataTableModel) {
                    int rowCount = ((DynamicDataTableModel)source).getRowCount();
                    if (rowCount > 0) {
                        try {
                            int defaultSelectionIndex = 1;
                            if (DataAccessController.SPECTRUM_TYPE.equals(type)) {
                                Collection<String> specIds = controller.getSpectrumIds();
                                defaultSelectionIndex = CollectionUtils.getIndex(specIds, controller.getForegroundSpectrum().getId());
                            } else if (DataAccessController.CHROMATOGRAM_TYPE.equals(type)) {
                                Collection<String> chromaIds = controller.getChromatogramIds();
                                defaultSelectionIndex = CollectionUtils.getIndex(chromaIds, controller.getForegroundChromatogram().getId());
                            }
                            this.getSelectionModel().setSelectionInterval(defaultSelectionIndex, defaultSelectionIndex);
                            hasDefaultSelection = true;
                        } catch(DataAccessException ex) {
                            logger.error("Error while trying to set table default selection", ex);
                        }
                    }
                }
            }
        }
    }
    */

    private class MzDataListSelectionListener implements ListSelectionListener {

        private String type = null;
        private JTable table = null;

        public MzDataListSelectionListener(JTable table, String type){
            this.type = type;
            this.table = table;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void valueChanged(ListSelectionEvent e) {
            int rowNum = table.getSelectedRow();
            String id = (String) table.getValueAt(rowNum, 0);
            Task newTask = new UpdateForegroundEntryTask(controller, id, type);
            newTask.addTaskListener(MzDataSelectionPane.this);
            newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
            // add task listeners
            uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
        }
    }

    private class MzDataTabChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JTabbedPane pane = (JTabbedPane)e.getSource();
            switch(pane.getSelectedIndex()) {
                case 0 :
                    MzDataSelectionPane.this.firePropertyChange(DataAccessController.SPECTRUM_TYPE, null, controller.getForegroundSpectrum());
                    break;
                case 1 :
                    MzDataSelectionPane.this.firePropertyChange(DataAccessController.CHROMATOGRAM_TYPE, null, controller.getForegroundChromatogram());
                    break;
            }
        }
    }
}