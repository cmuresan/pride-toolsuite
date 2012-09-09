package uk.ac.ebi.pride.gui.component.reviewer;

import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.component.dialog.TaskDialog;
import uk.ac.ebi.pride.gui.component.table.model.ListTableModel;
import uk.ac.ebi.pride.gui.component.table.model.PrideExperimentTableModel;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.DownloadPrideExperimentTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PrideDownloadSelectionPane display both a list of experiments to be downloaded
 * and where it is going to be stored.
 * <p/>
 * User: rwang
 * Date: 24/01/11
 * Time: 11:08
 */
public class PrideDownloadSelectionPane extends JPanel
        implements ActionListener, TableModelListener, TaskListener<java.util.List<Map<String, String>>, String> {

    protected static final String SAVE_TO_TITLE = "Save to";
    protected static final String BROWSE_BUTTON = "Browse";
    protected static final String SELECTION_ALL_BUTTON = "Select All";
    protected static final String DESELECTION_ALL_BUTTON = "Deselect All";
    protected static final String DOWNLOAD_BUTTON = "Download";
    protected static final Dimension PATH_FIELD_SIZE = new Dimension(440, 20);

    /**
     * Parent component which contains this component
     */
    protected Component parent;

    /**
     * This indicates whether to dispose parent component when the download button has been clicked
     */
    protected boolean toDispose;
    protected JTextField pathField;
    protected JXTable downloadTable;
    protected ListTableModel<java.util.List<Map<String, String>>> downloadTableModel;
    protected JButton selectAllButton;
    protected JButton deselectAllButton;
    protected JButton downloadButton;
    protected String currentUserName;
    protected String currentPassWord;
    protected JCheckBox openAfterDownloadCheckbox;
    protected PrideInspectorContext context;

    public PrideDownloadSelectionPane(Component parent, boolean toDispose) {
        this(parent, toDispose, null, null);
    }

    public PrideDownloadSelectionPane(Component parent, boolean toDispose, String username, String password) {
        this.parent = parent;
        this.toDispose = toDispose;
        this.currentUserName = username;
        this.currentPassWord = password;
        this.context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        setupMainPane();
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentPassWord() {
        return currentPassWord;
    }

    public void setCurrentPassWord(String currentPassWord) {
        this.currentPassWord = currentPassWord;
    }

    public void addExperimentMetaData(java.util.List<Map<String, String>> metadata) {
        downloadTableModel.addData(metadata);
        downloadTableModel.fireTableDataChanged();
    }

    /**
     * Set up all the main GUI components
     */
    private void setupMainPane() {
        this.setLayout(new BorderLayout());

        // create download table
        downloadTable = new JXTable();
        downloadTableModel = new PrideExperimentTableModel();
        downloadTableModel.addTableModelListener(this);
        downloadTable.setModel(downloadTableModel);
        downloadTable.setColumnControlVisible(true);
        downloadTable.setFillsViewportHeight(true);
        downloadTable.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(downloadTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        // create file browser pane
        JPanel dirPane = new JPanel();
        dirPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        // save to label
        JLabel saveToLabel = new JLabel(SAVE_TO_TITLE);
        // path text field
        pathField = new JTextField(System.getProperty("user.home"));
        pathField.setPreferredSize(PATH_FIELD_SIZE);
        // browse button
        JButton browseButton = new JButton(BROWSE_BUTTON);
        browseButton.setActionCommand(BROWSE_BUTTON);
        browseButton.addActionListener(this);
        // add components
        dirPane.add(saveToLabel);
        dirPane.add(pathField);
        dirPane.add(browseButton);

        // create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        openAfterDownloadCheckbox = new JCheckBox("Open after download", true);
        buttonPanel.add(openAfterDownloadCheckbox);
        selectAllButton = new JButton(SELECTION_ALL_BUTTON);
        selectAllButton.setActionCommand(SELECTION_ALL_BUTTON);
        selectAllButton.setEnabled(false);
        selectAllButton.addActionListener(this);
        deselectAllButton = new JButton(DESELECTION_ALL_BUTTON);
        deselectAllButton.setActionCommand(DESELECTION_ALL_BUTTON);
        deselectAllButton.setEnabled(false);
        deselectAllButton.addActionListener(this);
        downloadButton = new JButton(DOWNLOAD_BUTTON);
        downloadButton.setActionCommand(DOWNLOAD_BUTTON);
        downloadButton.setEnabled(false);
        downloadButton.addActionListener(this);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(downloadButton);

        JPanel container = new JPanel(new BorderLayout());
        container.add(dirPane, BorderLayout.NORTH);
        container.add(buttonPanel, BorderLayout.CENTER);
        this.add(container, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();

        if (BROWSE_BUTTON.equals(evtCmd)) {
            browseButtonPressed();
        } else if (SELECTION_ALL_BUTTON.equals(evtCmd)) {
            selectionButtonPressed(true);
        } else if (DESELECTION_ALL_BUTTON.equals(evtCmd)) {
            selectionButtonPressed(false);
        } else if (DOWNLOAD_BUTTON.equals(evtCmd)) {
            downloadButtonPressed();
            if (toDispose) {
                parent.setVisible(false);
            }
        }
    }

    private void browseButtonPressed() {
        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select Path Save To", null, false);
        ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            pathField.setText(filePath);
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }
    }

    private void selectionButtonPressed(boolean downloadable) {
        int rowCnt = downloadTable.getRowCount();
        if (rowCnt > 0) {
            int downloadColumnIndex = downloadTableModel.getColumnIndex(PrideExperimentTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
            for (int i = 0; i < rowCnt; i++) {
                downloadTableModel.setValueAt(downloadable, i, downloadColumnIndex);
            }
        }
    }

    private void downloadButtonPressed() {
        // check whether download file is valid path
        File path = new File(pathField.getText());

        if (path.exists() && path.isDirectory()) {
            int rowCnt = downloadTable.getRowCount();
            if (rowCnt > 0) {
                Map<Comparable, Double> accs = new LinkedHashMap<Comparable, Double>();
                int downloadColumnIndex = downloadTableModel.getColumnIndex(PrideExperimentTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
                int accColumnIndex = downloadTableModel.getColumnIndex(PrideExperimentTableModel.TableHeader.EXP_ACC_COLUMN.getHeader());
                int sizeColumnIndex = downloadTableModel.getColumnIndex(PrideExperimentTableModel.TableHeader.SIZE_COLUMN.getHeader());
                for (int i = 0; i < rowCnt; i++) {
                    Boolean value = (Boolean) downloadTableModel.getValueAt(i, downloadColumnIndex);
                    if (value) {
                        String acc = downloadTableModel.getValueAt(i, accColumnIndex).toString();
                        double size = Double.parseDouble(downloadTableModel.getValueAt(i, sizeColumnIndex).toString()) * 1024 * 1024;
                        accs.put(acc, size);
                    }
                }

                // create a dialog to show progress
                TaskDialog dialog = new TaskDialog(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getMainComponent(), "Download PRIDE Experiment", "Downloading...Please be aware that this may take a few minutes");
                dialog.setVisible(true);
                DownloadPrideExperimentTask downloadTask = new DownloadPrideExperimentTask(accs, path, currentUserName, currentPassWord, openAfterDownloadCheckbox.isSelected());
                downloadTask.addTaskListener(dialog);
                downloadTask.setGUIBlocker(new DefaultGUIBlocker(downloadTask, GUIBlocker.Scope.NONE, null));
                PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
                context.addTask(downloadTask);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please specify a valid output path", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        ListTableModel tableModel = (ListTableModel) e.getSource();
        if (tableModel.getRowCount() > 0) {
            selectAllButton.setEnabled(true);
            deselectAllButton.setEnabled(true);
            boolean downloadable = false;
            int rowCnt = tableModel.getRowCount();
            int column = tableModel.getColumnIndex(PrideExperimentTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
            for (int i = 0; i < rowCnt; i++) {
                Boolean val = (Boolean) tableModel.getValueAt(i, column);
                if (val) {
                    downloadable = true;
                }
            }
            downloadButton.setEnabled(downloadable);
        } else {
            selectAllButton.setEnabled(false);
            deselectAllButton.setEnabled(false);
            downloadButton.setEnabled(false);
        }
    }

    @Override
    public void succeed(TaskEvent<java.util.List<Map<String, String>>> listTaskEvent) {
        addExperimentMetaData(listTaskEvent.getValue());
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<java.util.List<String>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
