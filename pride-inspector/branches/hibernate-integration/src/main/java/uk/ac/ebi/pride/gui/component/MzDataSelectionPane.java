package uk.ac.ebi.pride.gui.component;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.table.listener.EntryUpdateSelectionListener;
import uk.ac.ebi.pride.gui.component.table.model.ChromatogramTableModel;
import uk.ac.ebi.pride.gui.component.table.model.DynamicDataTableModel;
import uk.ac.ebi.pride.gui.component.table.model.SpectrumTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskManager;
import uk.ac.ebi.pride.gui.task.impl.RetrieveMzGraphTask;
import uk.ac.ebi.pride.gui.task.impl.UpdateForegroundEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Collection;

/**
 * MzDataSelectionPane contains two tabs: one for spectra and one for chromatogram.
 * <p/>
 * 1. It listens the PropertyChangeEvent from the background DataAccessController.
 * 2. It notifies all parties (components) listening on the changes with appropriate data.
 * <p/>
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:55:11
 */
public class MzDataSelectionPane extends DataAccessControllerPane<MzGraph, Void> {
    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    private static final int SPECTRUM_TAB_INDEX = 0;
    private static final int CHROMATOGRAM_TAB_INDEX = 1;
    private JTabbedPane tabPane;
    private JTable spectrumTable;
    private JTable chromaTable;
    private PrideViewerContext context;

    public MzDataSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void addComponents() {
        try {
            // create selection pane
            tabPane = new JTabbedPane();
            // add tab change listener
            tabPane.addChangeListener(new MzDataTabChangeListener());

            // init spectra selection pane
            Collection<Comparable> specIds = controller.getSpectrumIds();
            spectrumTable = addTable(tabPane,
                    DataAccessController.SPECTRUM_TYPE + "(" + (specIds == null ? 0 : specIds.size()) + ")",
                    Spectrum.class);
            SpectrumTableModel spectrumTableModel = new SpectrumTableModel();
            spectrumTable.setModel(spectrumTableModel);
            spectrumTable.setRowSorter(new NumberTableRowSorter(spectrumTableModel));
            // prevent dragging of column
            spectrumTable.getTableHeader().setReorderingAllowed(false);
            spectrumTableModel.addTableModelListener(new EntryUpdateSelectionListener(spectrumTable));

            // init chromatogram selection pane
            Collection<Comparable> chromaIds = controller.getChromatogramIds();
            chromaTable = addTable(tabPane,
                    DataAccessController.CHROMATOGRAM_TYPE + "(" + (chromaIds == null ? 0 : chromaIds.size()) + ")",
                    Chromatogram.class);
            chromaTable.setAutoCreateRowSorter(true);
            ChromatogramTableModel chromaTableModel = new ChromatogramTableModel();
            chromaTable.setModel(chromaTableModel);
            chromaTable.setRowSorter(new NumberTableRowSorter(chromaTableModel));
            // prevent dragging of column
            chromaTable.getTableHeader().setReorderingAllowed(false);
            chromaTableModel.addTableModelListener(new EntryUpdateSelectionListener(chromaTable));

            updatePropertyChange();
            this.add(tabPane, BorderLayout.CENTER);

        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            // ToDo: is this right?    
        }
    }

    /**
     * Update all the tabs
     */
    @Override
    protected void updatePropertyChange() {
        // spectrum tab
        boolean spectrumTabVisibility = controller.hasSpectrum();
        tabPane.setEnabledAt(SPECTRUM_TAB_INDEX, spectrumTabVisibility);
        if (spectrumTabVisibility) {
            updateTable(spectrumTable, Spectrum.class);
            tabPane.setSelectedIndex(SPECTRUM_TAB_INDEX);
        }

        // chromatogram tab
        boolean chromaTabVisibility = controller.hasChromatogram();
        tabPane.setEnabledAt(CHROMATOGRAM_TAB_INDEX, chromaTabVisibility);
        if (chromaTabVisibility) {
            updateTable(chromaTable, Chromatogram.class);
            if (!spectrumTabVisibility) {
                tabPane.setSelectedIndex(CHROMATOGRAM_TAB_INDEX);
            }
        }
    }

    private <T extends MzGraph> void updateTable(JTable table, Class<T> classType) {

        DynamicDataTableModel tableModel = (DynamicDataTableModel) table.getModel();
        TaskManager taskMgr = context.getTaskManager();
        // stop any running retrieving task
        taskMgr.removeTaskListener(tableModel);
        // clear all the data in table model
        tableModel.removeAllRows();
        // create spectra retrieval task, no GUI blocker
        RetrieveMzGraphTask retrieveTask = new RetrieveMzGraphTask<T>(controller, classType);
        // create table model
        retrieveTask.addTaskListener(tableModel);
        // start running the task
        retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
        taskMgr.addTask(retrieveTask);
    }

    private JTable addTable(JTabbedPane tabPane, String title, Class classType) {
        JTable table = new JTable();

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(table, classType));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tabPane.addTab(title, scrollPane);

        return table;
    }

    @Override
    public void succeed(TaskEvent<MzGraph> mzGraphTaskEvent) {
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, mzGraphTaskEvent.getValue());
    }

    /**
     * Listens to row selection.
     */
    private class MzDataListSelectionListener implements ListSelectionListener {

        private Class classType = null;
        private JTable table = null;

        public MzDataListSelectionListener(JTable table, Class classType) {
            this.classType = classType;
            this.table = table;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {

                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    String id = (String) table.getValueAt(rowNum, 0);
                    Task newTask = new UpdateForegroundEntryTask(MzDataSelectionPane.this.getController(), classType, id);
                    newTask.addTaskListener(MzDataSelectionPane.this);
                    newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                    // add task listeners
                    uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
                }
            }
        }
    }

    /**
     * Listens to tab selection among spectrum tab and chromatogram tab.
     */
    private class MzDataTabChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            DataAccessController controller = MzDataSelectionPane.this.getController();
            JTabbedPane pane = (JTabbedPane) e.getSource();
            switch (pane.getSelectedIndex()) {
                case 0:
                    MzDataSelectionPane.this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, controller.getForegroundSpectrum());
                    break;
                case 1:
                    MzDataSelectionPane.this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, controller.getForegroundChromatogram());
                    break;
            }
        }
    }
}