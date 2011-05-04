package uk.ac.ebi.pride.gui.component.mzdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.impl.ExportSpectrumAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.ChromatogramTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProgressiveUpdateTableModel;
import uk.ac.ebi.pride.gui.component.table.model.SpectrumTableModel;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.RetrieveChromatogramTableTask;
import uk.ac.ebi.pride.gui.task.impl.RetrieveSpectrumTableTask;
import uk.ac.ebi.pride.gui.task.impl.UpdateForegroundEntryTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MzDataSelectionPane contains two tabs: one for spectra and one for chromatogram.
 * <p/>
 * <p/>
 * 1. It listens the PropertyChangeEvent from the background DataAccessController.
 * <p/>
 * 2. It notifies all parties (components) listening on the changes with appropriate data.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:55:11
 */
public class MzDataSelectionPane extends DataAccessControllerPane<MzGraph, Void> implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(MzDataTabPane.class.getName());
    /**
     * Action commands
     */
    private static final String LOAD_NEXT = "Load Next";
    private static final String LOAD_ALL = "Load All";
    private static final String EXPORT = "EXPORT";
    /**
     * Tab index for spectrum table
     */
    private static final int SPECTRUM_TAB_INDEX = 0;
    /**
     * Tab index for chromatogram table
     */
    private static final int CHROMATOGRAM_TAB_INDEX = 1;
    /**
     * Label to display the number loaded spectra and chromatogram
     */
    private MzDataCountLabel countLabel;
    /**
     * Tab pane contains spectrum table and chromatogram table
     */
    private JTabbedPane tabPane;
    /**
     * Spectrum table
     */
    private JTable spectrumTable;
    /**
     * Chromatogram table
     */
    private JTable chromaTable;
    /**
     * Reference to pride inspector context
     */
    private PrideInspectorContext context;
    /**
     * the number of entries to read for each iteration of the paging
     */
    private int defaultOffset;
    /**
     * start index for spectrum, this is for paging
     */
    private int startForSpec;

    /**
     * start index for chromatogram
     */
    private int startForChroma;

    /**
     * Load next batch button
     */
    private JButton loadNextButton;
    /**
     * Load all button
     */
    private JButton loadAllButton;
    /**
     * Export button
     */
    private JButton exportButton;

    /**
     * Constructor
     *
     * @param controller      data access controller
     * @param parentComponent parent component
     */
    public MzDataSelectionPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    /**
     * initialize the main display pane
     */
    @Override
    protected void setupMainPane() {
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        defaultOffset = Integer.parseInt(context.getProperty("mzdata.batch.load.size"));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * add the rest components
     */
    @Override
    protected void addComponents() {
        // add the top panel which display the spectra and chromatogram count
        JPanel topPane = initializeTopPane();
        this.add(topPane, BorderLayout.NORTH);

        // create selection pane
        tabPane = new JTabbedPane();
        tabPane.setBorder(BorderFactory.createEmptyBorder());

        // add tab change listener
        tabPane.addChangeListener(new MzDataTabChangeListener());

        // init spectra selection pane
        spectrumTable = TableFactory.createSpectrumTable();

        // add selection listener
        spectrumTable.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(spectrumTable, Spectrum.class));

        // add to scroll pane
        JScrollPane spectrumScrollPane = new JScrollPane(spectrumTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spectrumScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tabPane.addTab(DataAccessController.SPECTRUM_TYPE, spectrumScrollPane);

        // init chromatogram selection pane
        chromaTable = TableFactory.createChromatogramTable();

        // add selection listener
        chromaTable.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(chromaTable, Chromatogram.class));

        // add to scroll pane
        JScrollPane chromaScrollPane = new JScrollPane(chromaTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chromaScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tabPane.addTab(DataAccessController.CHROMATOGRAM_TYPE, chromaScrollPane);

        // initialize the visibility and the content of the tabs
        initializeTabPane();

        // add tabPane to the main display pane
        this.add(tabPane, BorderLayout.CENTER);
    }

    /**
     * Create the top panel
     *
     * @return JPanel top panel
     */
    private JPanel initializeTopPane() {
        // top panel
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout());
        labelPanel.setOpaque(false);

        // count label
        try {
            countLabel = new MzDataCountLabel(controller.getNumberOfSpectra(), controller.getNumberOfChromatograms());
        } catch (DataAccessException e) {
            String msg = "Error while creating mzData count label";
            logger.error(msg);
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        labelPanel.add(countLabel, BorderLayout.WEST);

        // button panel
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // load next button
        // load icon
        Icon loadNextIcon = GUIUtilities.loadIcon(context.getProperty("load.next.mzdata.small.icon"));
        loadNextButton = GUIUtilities.createLabelLikeButton(loadNextIcon, context.getProperty("load.next.mzdata.title"));
        loadNextButton.setToolTipText(context.getProperty("load.next.mzdata.tooltip"));
        loadNextButton.setForeground(Color.blue);
        // set action command
        loadNextButton.setActionCommand(LOAD_NEXT);
        loadNextButton.addActionListener(this);

        toolBar.add(loadNextButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // load all button
        // load icon
        Icon loadAllIcon = GUIUtilities.loadIcon(context.getProperty("load.all.mzdata.small.icon"));
        loadAllButton = GUIUtilities.createLabelLikeButton(loadAllIcon, context.getProperty("load.all.mzdata.title"));
        loadAllButton.setToolTipText(context.getProperty("load.all.mzdata.tooltip"));
        loadAllButton.setForeground(Color.blue);

        // set action command
        loadAllButton.setActionCommand(LOAD_ALL);
        loadAllButton.addActionListener(this);

        toolBar.add(loadAllButton);
        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // export button
        Icon exportIcon = GUIUtilities.loadIcon(context.getProperty("export.enabled.small.icon"));
        Icon disabledExportIcon = GUIUtilities.loadIcon(context.getProperty("export.disabled.small.icon"));

        exportButton = GUIUtilities.createLabelLikeButton(exportIcon, "Export");
        exportButton.setDisabledIcon(disabledExportIcon);
        exportButton.setToolTipText(context.getProperty("export.tooltip"));
        exportButton.setForeground(Color.blue);

        // set action command
        exportButton.setActionCommand(EXPORT);
        exportButton.addActionListener(this);

        toolBar.add(exportButton);
        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.mzgraph");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        toolBar.add(helpButton);

        labelPanel.add(toolBar, BorderLayout.EAST);

        return labelPanel;
    }

    /**
     * Update all the tabs
     */
    private void initializeTabPane() {
        // set the start index for paging
        startForSpec = 0;
        startForChroma = 0;

        // check whether there is spectra
        boolean spectrumTabVisibility = false;
        // check whether there is chromatogram
        boolean chromaTabVisibility = false;
        try {
            spectrumTabVisibility = controller.hasSpectrum();
            chromaTabVisibility = controller.hasChromatogram();
        } catch (DataAccessException e) {
            logger.error("Failed to check the number of mzgraph", e);
        }

        // set the visibility of the tab
        tabPane.setEnabledAt(SPECTRUM_TAB_INDEX, spectrumTabVisibility);
        tabPane.setEnabledAt(CHROMATOGRAM_TAB_INDEX, chromaTabVisibility);

        // start retrieving data for spectrum table
        if (spectrumTabVisibility) {
            updateTable((SpectrumTableModel) spectrumTable.getModel(), Spectrum.class, defaultOffset);
        }

        // start retrieving data for chromatogram table
        if (chromaTabVisibility) {
            updateTable((ChromatogramTableModel) chromaTable.getModel(), Chromatogram.class, defaultOffset);
        }

        // set the tab selection, if spectrum tab is enabled, this should be used as default
        if (spectrumTabVisibility) {
            tabPane.setSelectedIndex(SPECTRUM_TAB_INDEX);
        } else if (chromaTabVisibility) {
            tabPane.setSelectedIndex(CHROMATOGRAM_TAB_INDEX);
        }
    }

    /**
     * This method is responsible for fire up a new background task to retrieve mzgraph data.
     *
     * @param tableModel table model to insert the result to
     * @param classType  indicates the type of mzgraph
     * @param offset
     */
    @SuppressWarnings("unchecked")
    private <T extends MzGraph> void updateTable(ProgressiveUpdateTableModel tableModel, Class<T> classType, int offset) {
        // create a new task
        Task retrieveTask = null;
        if (Spectrum.class.equals(classType)) {
            retrieveTask = new RetrieveSpectrumTableTask(controller, startForSpec, offset);
            startForSpec += defaultOffset;
        } else if (Chromatogram.class.equals(classType)) {
            retrieveTask = new RetrieveChromatogramTableTask(controller, startForChroma, offset);
            startForChroma += defaultOffset;
        }

        if (retrieveTask != null) {
            // add parent component as a task listener
            if (parentComponent instanceof TaskListener) {
                retrieveTask.addTaskListener((TaskListener) parentComponent);
            }

            // add table model as a task listener
            retrieveTask.addTaskListener(tableModel);

            // add count label as a task listener
            retrieveTask.addTaskListener(countLabel);

            // start running the task
            retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
            context.addTask(retrieveTask);
        }
    }

    /**
     * Triggered when load all button has been clicked.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCmd = e.getActionCommand();
        int offset = defaultOffset;

        //
        int index = tabPane.getSelectedIndex();
        try {
            if (index == SPECTRUM_TAB_INDEX) {
                if (LOAD_ALL.equals(actionCmd) || LOAD_NEXT.equals(actionCmd)) {
                    int numOfSpectra = controller.getNumberOfSpectra();
                    // set offset
                    if (LOAD_ALL.equals(actionCmd)) {
                        offset = numOfSpectra - startForSpec;
                    }
                    // get table model
                    if (startForSpec < numOfSpectra && numOfSpectra > spectrumTable.getRowCount()) {
                        updateTable((SpectrumTableModel) spectrumTable.getModel(), Spectrum.class, offset);
                    }
                } else if (EXPORT.equals(actionCmd)) {
                    ExportSpectrumAction exportAction = new ExportSpectrumAction(null, null);
                    exportAction.actionPerformed(e);
                }

            } else {
                if (LOAD_ALL.equals(actionCmd) || LOAD_NEXT.equals(actionCmd)) {
                    int numOfChromas = controller.getNumberOfChromatograms();

                    // set offset
                    if (LOAD_ALL.equals(actionCmd)) {
                        offset = numOfChromas - startForChroma;
                    }

                    if (startForChroma < numOfChromas && numOfChromas > chromaTable.getRowCount()) {
                        updateTable((ChromatogramTableModel) chromaTable.getModel(), Chromatogram.class, offset);
                    }
                }
            }
        } catch (DataAccessException ex) {
            String msg = "Failed to get the number of spectra";
            logger.error(msg, ex);
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }
    }

    /**
     * forward the retrieved mzgraph to mzgraph pane
     *
     * @param mzGraphTaskEvent mzGraph task event
     */
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

                if (rowCnt > 0 && rowNum >= 0) {
                    // get table model
                    TableModel tableModel = table.getModel();

                    // get the column number for mzgraph id
                    int columnNum = 0;
                    if (tableModel instanceof SpectrumTableModel) {
                        columnNum = ((SpectrumTableModel) tableModel).getColumnIndex(SpectrumTableModel.TableHeader.SPECTRUM_ID_COLUMN.getHeader());
                    } else if (tableModel instanceof ChromatogramTableModel) {
                        columnNum = ((ChromatogramTableModel) tableModel).getColumnIndex(ChromatogramTableModel.TableHeader.CHROMATOGRAM_ID_COLUMN.getHeader());
                    }

                    // get the mzgraph id
                    Comparable id = (Comparable) table.getValueAt(rowNum, columnNum);

                    // start a new task to retrieve the mzgraph
                    Task newTask = new UpdateForegroundEntryTask(MzDataSelectionPane.this.getController(), classType, id);
                    newTask.addTaskListener(MzDataSelectionPane.this);
                    newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));

                    // add task to task manager
                    uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext().addTask(newTask);
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
                    exportButton.setEnabled(true);
                    exportButton.setForeground(Color.blue);
                    break;
                case 1:
                    MzDataSelectionPane.this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, null, controller.getForegroundChromatogram());
                    exportButton.setEnabled(false);
                    exportButton.setForeground(Color.gray);
                    break;
            }
        }
    }
}