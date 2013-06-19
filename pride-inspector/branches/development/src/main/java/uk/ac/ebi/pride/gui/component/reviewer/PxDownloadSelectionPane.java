package uk.ac.ebi.pride.gui.component.reviewer;

import org.jdesktop.swingx.JXTreeTable;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.component.dialog.TaskDialog;
import uk.ac.ebi.pride.gui.component.table.model.PxSubmissionTableModel;
import uk.ac.ebi.pride.gui.px.PxSubmissionEntry;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.task.impl.DownloadPxSubmissionTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Displays a list of files to be downloaded for ProteomeXchange submissions, also allows user to select which files to be downloaded.
 *
 * @author Rui Wang
 * @version $Id$
 */
public class PxDownloadSelectionPane extends JPanel
        implements ActionListener, TreeModelListener, TaskListener<java.util.List<Map<String, String>>, String> {

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
    protected JXTreeTable downloadTable;
    protected PxSubmissionTableModel downloadTableModel;
    protected JButton selectAllButton;
    protected JButton deselectAllButton;
    protected JButton downloadButton;
    protected String currentUserName;
    protected String currentPassWord;
    protected JCheckBox openAfterDownloadCheckbox;
    protected PrideInspectorContext context;

    public PxDownloadSelectionPane(Component parent, boolean toDispose) {
        this(parent, toDispose, null, null);
    }

    public PxDownloadSelectionPane(Component parent, boolean toDispose, String username, String password) {
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
        if (metadata != null && metadata.size() > 0) {
            downloadTableModel.addData(metadata);
        }
    }

    /**
     * Set up all the main GUI components
     */
    private void setupMainPane() {
        this.setLayout(new BorderLayout());

        // create download table
        createTable();
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

    private void createTable() {
        downloadTable = new JXTreeTable();
        downloadTableModel = new PxSubmissionTableModel();
        downloadTableModel.addTreeModelListener(this);
        downloadTable.setTreeTableModel(downloadTableModel);
        downloadTable.setColumnControlVisible(true);
        downloadTable.setFillsViewportHeight(true);
        downloadTable.setCellEditor(new DefaultCellEditor(new JCheckBox()));

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
        }
    }

    private void selectionButtonPressed(boolean downloadable) {
        int downloadColumnIndex = downloadTableModel.getColumnIndex(PxSubmissionTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
        Object root = downloadTableModel.getRoot();
        int cnt = downloadTableModel.getChildCount(root);
        if (cnt > 0) {
            for (int i = 0; i < cnt; i++) {
                Object parent = downloadTableModel.getChild(root, i);
                downloadTableModel.setValueAt(downloadable, parent, downloadColumnIndex);
                int childCnt = downloadTableModel.getChildCount(parent);
                for (int j = 0; j < childCnt; j++) {
                    Object child = downloadTableModel.getChild(parent, j);
                    downloadTableModel.setValueAt(downloadable, child, downloadColumnIndex);
                    int nestedChildCnt = downloadTableModel.getChildCount(child);
                    for (int k = 0; k < nestedChildCnt; k++) {
                        downloadTableModel.setValueAt(downloadable, downloadTableModel.getChild(child, k), downloadColumnIndex);
                    }
                }
            }
        }
    }

    private void downloadButtonPressed() {
        // check whether download file is valid path
        File path = new File(pathField.getText());

        if (path.exists() && path.isDirectory()) {
            java.util.List<PxSubmissionEntry> pxSubmissionEntries = new ArrayList<PxSubmissionEntry>();

            // find all the selected px submission files
            java.util.Set<Object> leaves = downloadTableModel.getNoneParentNodes();
            boolean toDownload = true;
            for (Object leaf : leaves) {
                if (((PxSubmissionEntry) leaf).isToDownload()) {
                    PxSubmissionEntry entry = (PxSubmissionEntry) leaf;
                    pxSubmissionEntries.add(entry);
                    if (entry.getFileType().toLowerCase().equals("result")) {
                        toDownload = false;
                    }
                }
            }

            if (!toDownload) {
                int confirmed = JOptionPane.showConfirmDialog(this, "Please be aware that only the file format supported by PRIDE Inspector can be opened, such as: PRIDE XML, mzML",
                        "Download ProteomeXchange submission", JOptionPane.OK_CANCEL_OPTION);
                if (confirmed != 0) {
                    return;
                }
            }

            // create a dialog to show progress
            TaskDialog dialog = new TaskDialog(uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getMainComponent(), "Download PRIDE Experiment", "Downloading...Please be aware that this may take a few minutes");
            dialog.setVisible(true);
            DownloadPxSubmissionTask downloadTask = new DownloadPxSubmissionTask(pxSubmissionEntries, path, currentUserName, currentPassWord, openAfterDownloadCheckbox.isSelected());
            downloadTask.addTaskListener(dialog);
            downloadTask.setGUIBlocker(new DefaultGUIBlocker(downloadTask, GUIBlocker.Scope.NONE, null));
            context.addTask(downloadTask);
            if (toDispose) {
                parent.setVisible(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please specify a valid output path", "Error", JOptionPane.ERROR_MESSAGE);
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

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        boolean newEntries = downloadTableModel.getChildCount(downloadTableModel.getRoot()) > 0;

        selectAllButton.setEnabled(newEntries);
        deselectAllButton.setEnabled(newEntries);
        downloadButton.setEnabled(false);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        boolean downloadable = false;

        if (e.getChildren().length > 0) {
            PxSubmissionEntry selectedNode = (PxSubmissionEntry) e.getChildren()[0];
            int downloadColumnIndex = downloadTableModel.getColumnIndex(PxSubmissionTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
            if (selectedNode.hasChildren() && selectedNode.getFileName() == null) {
                // if node represents a high-level px submission
                for (PxSubmissionEntry childNode : selectedNode.getChildren()) {
                    downloadTableModel.setValueAt(selectedNode.isToDownload(), childNode, downloadColumnIndex);
                    for (PxSubmissionEntry nestedChildNode : childNode.getChildren()) {
                        downloadTableModel.setValueAt(selectedNode.isToDownload(), nestedChildNode, downloadColumnIndex);
                    }
                }
            } else {
                Set<PxSubmissionEntry> parentNodes = selectedNode.getParents();
                for (PxSubmissionEntry parentNode : parentNodes) {
                    if (parentNode.getFileName() == null && parentNode.isToDownload() != selectedNode.isToDownload()) {
                        boolean neighborInSameState = true;
                        for (PxSubmissionEntry childNode : parentNode.getChildren()) {
                            if (childNode.isToDownload() != selectedNode.isToDownload()) {
                                neighborInSameState = false;
                                break;
                            }

                            if (!neighborInSameState) {
                                break;
                            }

                            for (PxSubmissionEntry nestedChild : childNode.getChildren()) {
                                if (nestedChild.isToDownload() != selectedNode.isToDownload()) {
                                    neighborInSameState = false;
                                    break;
                                }
                            }
                        }

                        if (neighborInSameState) {
                            downloadTableModel.setValueAt(selectedNode.isToDownload(), parentNode, downloadColumnIndex);
                        }
                    }
                }
            }
        }

        // check whether any file has been selected, enable/disable the download button
        Set<Object> nodes = downloadTableModel.getNoneParentNodes();
        for (Object node : nodes) {
            if (((PxSubmissionEntry)node).isToDownload()) {
                downloadable = true;
                break;
            }
        }

        downloadButton.setEnabled(downloadable);

        downloadTable.revalidate();
        downloadTable.repaint();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PxDownloadSelectionPane pxPane = new PxDownloadSelectionPane(frame, true, "test", "test");
        frame.setContentPane(pxPane);
        frame.setVisible(true);
        frame.pack();

        java.util.List<Map<String, String>> pxDetails = new ArrayList<Map<String, String>>();
        Map<String, String> px1 = new HashMap<String, String>();
        px1.put(PxSubmissionEntry.ACCESSION, "PXD000001");
        px1.put(PxSubmissionEntry.DOI, "DOI/PXD000001");
        px1.put(PxSubmissionEntry.FILE_ID, "1");
        px1.put(PxSubmissionEntry.FILE_NAME, "pride_1001.xml");
        px1.put(PxSubmissionEntry.FILE_TYPE, "RESULT");
        px1.put(PxSubmissionEntry.FILE_MAPPING, "2");
        px1.put(PxSubmissionEntry.PRIDE_ACC, "1001");
        px1.put(PxSubmissionEntry.FILE_SIZE, "2");
        pxDetails.add(px1);

        Map<String, String> px2 = new HashMap<String, String>();
        px2.put(PxSubmissionEntry.ACCESSION, "PXD000001");
        px2.put(PxSubmissionEntry.DOI, "DOI/PXD000001");
        px2.put(PxSubmissionEntry.FILE_ID, "2");
        px2.put(PxSubmissionEntry.FILE_NAME, "raw.xml");
        px2.put(PxSubmissionEntry.FILE_TYPE, "RAW");
        px2.put(PxSubmissionEntry.FILE_SIZE, "2000");
        pxDetails.add(px2);


        Map<String, String> px3 = new HashMap<String, String>();
        px3.put(PxSubmissionEntry.ACCESSION, "PXD000002");
        px3.put(PxSubmissionEntry.DOI, "DOI/PXD000002");
        px3.put(PxSubmissionEntry.FILE_ID, "1");
        px3.put(PxSubmissionEntry.FILE_NAME, "pride_1001.xml");
        px3.put(PxSubmissionEntry.FILE_TYPE, "RESULT");
        px3.put(PxSubmissionEntry.FILE_MAPPING, "2");
        px3.put(PxSubmissionEntry.PRIDE_ACC, "1001");
        px3.put(PxSubmissionEntry.FILE_SIZE, "2");
        pxDetails.add(px3);

        Map<String, String> px4 = new HashMap<String, String>();
        px4.put(PxSubmissionEntry.ACCESSION, "PXD000002");
        px4.put(PxSubmissionEntry.DOI, "DOI/PXD000002");
        px4.put(PxSubmissionEntry.FILE_ID, "2");
        px4.put(PxSubmissionEntry.FILE_NAME, "raw.xml");
        px4.put(PxSubmissionEntry.FILE_TYPE, "RAW");
        px4.put(PxSubmissionEntry.FILE_SIZE, "2000");
        pxDetails.add(px4);

        pxPane.addExperimentMetaData(pxDetails);

    }
}
