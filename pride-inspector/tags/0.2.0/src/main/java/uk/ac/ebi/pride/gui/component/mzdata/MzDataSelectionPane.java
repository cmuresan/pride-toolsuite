package uk.ac.ebi.pride.gui.component.mzdata;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.utils.LoggerUtils;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.AlterRowColorTable;
import uk.ac.ebi.pride.gui.component.table.listener.EntryUpdateSelectionListener;
import uk.ac.ebi.pride.gui.component.table.model.ChromatogramTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProgressiveUpdateTableModel;
import uk.ac.ebi.pride.gui.component.table.model.SpectrumTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.RowNumberRenderer;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

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
    private AlterRowColorTable spectrumTable;
    private AlterRowColorTable chromaTable;
    private SpectrumTableModel spectrumTableModel;
    private ChromatogramTableModel chromaTableModel;
    private PrideViewerContext context;
    private static final int OFFSET = 200; //constant to retrieve only those elements from the database
    private int start;

    public MzDataSelectionPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.setLayout(new BorderLayout());
        start = 0;
    }

    @Override
    protected void addComponents() {
        // create selection pane
        tabPane = new JTabbedPane();
        tabPane.setBorder(BorderFactory.createEmptyBorder());
        // add tab change listener
        tabPane.addChangeListener(new MzDataTabChangeListener());

        // init spectra selection pane
        spectrumTable = addTable(tabPane,
                DataAccessController.SPECTRUM_TYPE,
                Spectrum.class);
        spectrumTableModel = new SpectrumTableModel();
        spectrumTable.setModel(spectrumTableModel);
        spectrumTable.setRowSorter(new NumberTableRowSorter(spectrumTableModel));
        // prevent dragging of column
        spectrumTable.getTableHeader().setReorderingAllowed(false);
        spectrumTableModel.addTableModelListener(new EntryUpdateSelectionListener(spectrumTable));
        // set row number renderer
        TableColumn rowNumColumn = spectrumTable.getColumn(SpectrumTableModel.TableHeader.ROW_NUMBER_COLUMN.getHeader());
        int rowColumnNum = rowNumColumn.getModelIndex();
        rowNumColumn.setCellRenderer(new RowNumberRenderer());
        spectrumTable.getColumnModel().getColumn(rowColumnNum).setMaxWidth(40);
        spectrumTable.setOmitColumn(rowColumnNum);
        // init chromatogram selection pane
        chromaTable = addTable(tabPane,
                DataAccessController.CHROMATOGRAM_TYPE,
                Chromatogram.class);
        chromaTable.setAutoCreateRowSorter(true);
        chromaTableModel = new ChromatogramTableModel();
        chromaTable.setModel(chromaTableModel);
        chromaTable.setRowSorter(new NumberTableRowSorter(chromaTableModel));
        // prevent dragging of column
        chromaTable.getTableHeader().setReorderingAllowed(false);
        chromaTableModel.addTableModelListener(new EntryUpdateSelectionListener(chromaTable));
        // set row number renderer
        TableColumn cRowNumColumn = chromaTable.getColumn(ChromatogramTableModel.TableHeader.ROW_NUMBER_COLUMN.getHeader());
        int cRowColumnNum = cRowNumColumn.getModelIndex();
        cRowNumColumn.setCellRenderer(new RowNumberRenderer());
        chromaTable.getColumnModel().getColumn(cRowColumnNum).setMaxWidth(40);
        chromaTable.setOmitColumn(cRowColumnNum);

        updatePropertyChange();
        this.add(tabPane, BorderLayout.CENTER);
    }

    /**
     * Update all the tabs
     */
    @Override
    protected void updatePropertyChange() {
        // spectrum tab
        start = 0;
        // this is needed to clear the peak list currently on display
        firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", null);

        boolean spectrumTabVisibility = controller.hasSpectrum();
        tabPane.setEnabledAt(SPECTRUM_TAB_INDEX, spectrumTabVisibility);
        cancelOngoingTableUpdates(spectrumTableModel);
        spectrumTableModel.removeAllRows();
        if (spectrumTabVisibility) {
            updateTable(spectrumTableModel, Spectrum.class);
            tabPane.setSelectedIndex(SPECTRUM_TAB_INDEX);
        }

        // chromatogram tab
        boolean chromaTabVisibility = controller.hasChromatogram();
        tabPane.setEnabledAt(CHROMATOGRAM_TAB_INDEX, chromaTabVisibility);
        cancelOngoingTableUpdates(chromaTableModel);
        chromaTableModel.removeAllRows();
        if (chromaTabVisibility) {
            updateTable(chromaTableModel, Chromatogram.class);
            if (!spectrumTabVisibility) {
                tabPane.setSelectedIndex(CHROMATOGRAM_TAB_INDEX);
            }
        }
        this.revalidate();
        this.repaint();
    }

    private void cancelOngoingTableUpdates(ProgressiveUpdateTableModel tableModel) {
        // TaskListener tableModel = (TaskListener) table.getModel();
        TaskManager taskMgr = context.getTaskManager();
        // stop any running retrieving task
        java.util.List<Task> existingTask = taskMgr.getTask(tableModel);
        for (Task task : existingTask) {
            taskMgr.cancelTask(task, true);
        }
    }

    private <T extends MzGraph> void updateTable(ProgressiveUpdateTableModel tableModel, Class<T> classType) {

        TaskManager taskMgr = context.getTaskManager();
        //RetrieveMzGraphTask retrieveTask = new RetrieveMzGraphTask<T>(controller, classType);
        RetrieveMzGraphTask retrieveTask = new RetrieveMzGraphTask<T>(controller, classType, start, OFFSET);
        // table model
        retrieveTask.addTaskListener(tableModel);
        // start running the task
        retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
        taskMgr.addTask(retrieveTask);
        start += OFFSET;

    }

    private AlterRowColorTable addTable(JTabbedPane tabPane, String title, Class classType) {
        AlterRowColorTable table = new AlterRowColorTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(table, classType));
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        AdjustmentListener listener = new FetchDataListener();
        scrollPane.getVerticalScrollBar().addAdjustmentListener(listener);

        tabPane.addTab(title, scrollPane);

        return table;
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

                    int index = tabPane.getSelectedIndex();
                    try {
                        if (index == SPECTRUM_TAB_INDEX) {
                            if (controller.getSpectrumIds().size() > spectrumTableModel.getRowCount()) {
                                updateTable(spectrumTableModel, Spectrum.class);
                            }

                        } else {

                            if (controller.getChromatogramIds().size() > chromaTableModel.getRowCount()) {
                                updateTable(chromaTableModel, Chromatogram.class);
                            }
                        }
                    } catch (DataAccessException e) {
                        LoggerUtils.error(logger, this, e); //To change body of catch statement use File | Settings | File Templates.

                    }
                }
            }
        }
    }

    @Override
    public void succeed(TaskEvent<MzGraph> mzGraphTaskEvent) {
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", mzGraphTaskEvent.getValue());
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
                int rowCnt = table.getRowCount();
                if (rowCnt <= 0) {
                    // todo: clear peak list
                } else if (rowNum >= 0) {
                    TableModel tableModel = table.getModel();
                    int columnNum = 0;
                    if (tableModel instanceof SpectrumTableModel) {
                        columnNum = ((SpectrumTableModel) tableModel).getColumnIndex(SpectrumTableModel.TableHeader.SPECTRUM_ID_COLUMN.getHeader());

                    } else if (tableModel instanceof ChromatogramTableModel) {
                        columnNum = ((ChromatogramTableModel) tableModel).getColumnIndex(ChromatogramTableModel.TableHeader.CHROMATOGRAM_ID_COLUMN.getHeader());
                    }
                    Comparable id = (Comparable) table.getValueAt(rowNum, columnNum);
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