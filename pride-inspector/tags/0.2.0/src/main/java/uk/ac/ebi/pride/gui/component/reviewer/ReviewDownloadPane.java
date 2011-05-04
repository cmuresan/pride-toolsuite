package uk.ac.ebi.pride.gui.component.reviewer;

import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.component.OpenFileDialog;
import uk.ac.ebi.pride.gui.component.table.model.ListTableModel;
import uk.ac.ebi.pride.gui.component.table.model.SuccessiveUpdateTableModel;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.task.impl.DownloadExperimentTask;
import uk.ac.ebi.pride.gui.task.impl.OpenReviewerConnectionTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 09:43:33
 */
public class ReviewDownloadPane extends JDialog implements ActionListener, TableModelListener {

    private static final String TITLE = "Review Download";
    private static final String LOGIN_TITLE = "Review Login";
    private static final String MSG_TITLE = "Message";
    private static final String DOWNLOAD_TITLE = "Experiment Download";
    private static final String USER_NAME_TITLE = "User Name";
    private static final String PASSWORD_TITLE = "Password";
    private static final String LOG_IN_BUTTON = "Login";
    private static final String SAVE_TO_TITLE = "Save to";
    private static final String BROWSE_BUTTON = "Browse";
    private static final String SELECTION_ALL_BUTTON = "Select All";
    private static final String DESELECTION_ALL_BUTTON = "Deselect All";
    private static final String DOWNLOAD_BUTTON = "Download";
    private static final String CLOSE_BUTTON = "Close";

    private static final Dimension MAIN_PANE_SIZE = new Dimension(650, 600);
    private static final Dimension LOGIN_PANE_SIZE = new Dimension(650, 40);
    private static final Dimension TXT_FIELD_SIZE = new Dimension(80, 20);
    private static final Dimension MSG_PANE_SIZE = new Dimension(650, 100);
    private static final Dimension PATH_FIELD_SIZE = new Dimension(440, 20);

    private JTextField userField;
    private JPasswordField pwdField;
    private MessageLabel msgLabel;
    private JTextField pathField;
    private JTable downloadTable;
    private ReviewDownloadTableModel downloadTableModel;
    private JButton selectAllButton;
    private JButton deselectAllButton;
    private JButton downloadButton;
    private String currentUserName;
    private char[] currentPassWord;
    private JCheckBox openAfterDownloadCheckbox;

    public ReviewDownloadPane(Frame owner) {
        super(owner, TITLE);
        setupMainPane();
    }

    /**
     * Set up the main pane and add all components.
     */
    public void setupMainPane() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(createLoginPane());
        mainPanel.add(createMsgPane());
        mainPanel.add(createDownloadPane());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(mainPanel);
        this.setMinimumSize(MAIN_PANE_SIZE);
        // set icon
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        ImageIcon dialogIcon = (ImageIcon) GUIUtilities.loadIcon(propMgr.getProperty("reviewer.download.icon.small"));
        this.setIconImage(dialogIcon.getImage());
    }

    /**
     * Create a log in panel
     *
     * @return
     */
    private JPanel createLoginPane() {
        JPanel loginPane = new JPanel();
        loginPane.setBorder(BorderFactory.createTitledBorder(LOGIN_TITLE));
        loginPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        loginPane.setMaximumSize(LOGIN_PANE_SIZE);
        // username
        JLabel usrLabel = new JLabel(USER_NAME_TITLE);
        userField = new JTextField();
        userField.setPreferredSize(TXT_FIELD_SIZE);
        loginPane.add(usrLabel);
        loginPane.add(userField);
        // password
        JLabel pwdLabel = new JLabel(PASSWORD_TITLE);
        pwdField = new JPasswordField();
        pwdField.setPreferredSize(TXT_FIELD_SIZE);
        loginPane.add(pwdLabel);
        loginPane.add(pwdField);
        // login button
        JButton loginButton = new JButton(LOG_IN_BUTTON);
        loginButton.setActionCommand(LOG_IN_BUTTON);
        loginButton.addActionListener(this);
        loginPane.add(loginButton);
        return loginPane;
    }

    private JPanel createMsgPane() {
        JPanel msgPane = new JPanel();
        msgPane.setLayout(new BorderLayout());
        msgPane.setBorder(BorderFactory.createTitledBorder(MSG_TITLE));
        msgPane.setMaximumSize(MSG_PANE_SIZE);
        msgPane.setVisible(false);
        msgLabel = new MessageLabel(msgPane);
        msgLabel.setOpaque(false);
        msgPane.add(msgLabel);
        return msgPane;
    }

    private JPanel createDownloadPane() {
        JPanel downloadPane = new JPanel();
        downloadPane.setBorder(BorderFactory.createTitledBorder(DOWNLOAD_TITLE));
        downloadPane.setLayout(new BorderLayout());
        // create file browser pane
        JPanel dirPane = new JPanel();
        dirPane.setLayout(new FlowLayout(FlowLayout.LEFT));
        // save to label
        JLabel saveToLabel = new JLabel(SAVE_TO_TITLE);
        // path text field
        pathField = new JTextField(System.getProperty("java.io.tmpdir"));
        pathField.setPreferredSize(PATH_FIELD_SIZE);
        // browse button
        JButton browseButton = new JButton(BROWSE_BUTTON);
        browseButton.setActionCommand(BROWSE_BUTTON);
        browseButton.addActionListener(this);
        // add components
        dirPane.add(saveToLabel);
        dirPane.add(pathField);
        dirPane.add(browseButton);
        downloadPane.add(dirPane, BorderLayout.NORTH);

        // create download table
        downloadTable = new JTable();
        downloadTableModel = new ReviewDownloadTableModel();
        downloadTableModel.addTableModelListener(this);
        downloadTable.setModel(downloadTableModel);
        downloadTable.setFillsViewportHeight(true);
        downloadTable.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(downloadTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        downloadPane.add(scrollPane, BorderLayout.CENTER);

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
        JButton closeButton = new JButton(CLOSE_BUTTON);
        closeButton.setActionCommand(CLOSE_BUTTON);
        closeButton.addActionListener(this);
        buttonPanel.add(selectAllButton);
        buttonPanel.add(deselectAllButton);
        buttonPanel.add(downloadButton);
        buttonPanel.add(closeButton);
        downloadPane.add(buttonPanel, BorderLayout.SOUTH);
        return downloadPane;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();

        if (LOG_IN_BUTTON.equals(evtCmd)) {
            loginButtonPressed();
        } else if (BROWSE_BUTTON.equals(evtCmd)) {
            browseButtonPressed();
        } else if (SELECTION_ALL_BUTTON.equals(evtCmd)) {
            selectionButtonPressed(true);
        } else if (DESELECTION_ALL_BUTTON.equals(evtCmd)) {
            selectionButtonPressed(false);
        } else if (DOWNLOAD_BUTTON.equals(evtCmd)) {
            downloadButtonPressed();
        } else if (CLOSE_BUTTON.equals(evtCmd)) {
            this.dispose();
        }
    }

    private void loginButtonPressed() {
        currentUserName = userField.getText();
        currentPassWord = pwdField.getPassword();
        OpenReviewerConnectionTask reviewerTask = new OpenReviewerConnectionTask(currentUserName, currentPassWord);
        reviewerTask.setGUIBlocker(new DefaultGUIBlocker(reviewerTask, GUIBlocker.Scope.NONE, null));
        reviewerTask.addTaskListener(downloadTableModel);
        reviewerTask.addTaskListener(msgLabel);
        reviewerTask.execute();
    }

    private void browseButtonPressed() {
        OpenFileDialog ofd = new OpenFileDialog(System.getProperty("user.dir"), "Select Path Save To");
        ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File path = ofd.getSelectedFile();
            pathField.setText(path.getPath());
        }
    }

    private void selectionButtonPressed(boolean downloadable) {
        int rowCnt = downloadTable.getRowCount();
        if (rowCnt > 0) {
            int downloadColumnIndex = downloadTableModel.getColumnIndex(TableHeader.DOWNLOAD_COLUMN.getHeader());
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
                List<String> accs = new ArrayList<String>();
                int downloadColumnIndex = downloadTableModel.getColumnIndex(TableHeader.DOWNLOAD_COLUMN.getHeader());
                int accColumnIndex = downloadTableModel.getColumnIndex(TableHeader.EXP_ACC_COLUMN.getHeader());
                for (int i = 0; i < rowCnt; i++) {
                    Boolean value = (Boolean) downloadTableModel.getValueAt(i, downloadColumnIndex);
                    if (value) {
                        String acc = (String) downloadTableModel.getValueAt(i, accColumnIndex);
                        accs.add(acc);
                    }
                }
                DownloadExperimentTask downloadTask = new DownloadExperimentTask(accs, path, currentUserName, currentPassWord, openAfterDownloadCheckbox.isSelected());
                downloadTask.addTaskListener(msgLabel);
                downloadTask.setGUIBlocker(new DefaultGUIBlocker(downloadTask, GUIBlocker.Scope.NONE, null));
                PrideViewerContext context = (PrideViewerContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
                context.getTaskManager().addTask(downloadTask);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please specify a valid output path", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Set all buttons state
     *
     * @param e table's data base changed
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        ListTableModel tableModel = (ListTableModel) e.getSource();
        if (tableModel.getRowCount() > 0) {
            selectAllButton.setEnabled(true);
            deselectAllButton.setEnabled(true);
            boolean downloadable = false;
            int rowCnt = tableModel.getRowCount();
            int column = tableModel.getColumnIndex(TableHeader.DOWNLOAD_COLUMN.getHeader());
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

    private class ReviewDownloadTableModel extends SuccessiveUpdateTableModel<List<Map<String, String>>, String> {

        @Override
        public void initializeTableModel() {
            TableHeader[] headers = TableHeader.values();
            for (TableHeader header : headers) {
                columnNames.put(header.getHeader(), header.getHeaderClass());
            }
        }

        @Override
        public void addData(List<Map<String, String>> newData) {
            contents.clear();
            if (newData != null && !newData.isEmpty()) {
                NumberFormatter formatter = new NumberFormatter();
                for (Map<String, String> exp : newData) {
                    List<Object> content = new ArrayList<Object>();
                    String accession = exp.get("Accession");
                    if (!isAlreadyExist(accession)) {
                        content.add(exp.get("Accession"));
                        double size = Double.valueOf(exp.get("Size"));
                        String mSize = "0";
                        try {
                            mSize = formatter.valueToString(new Double(size/(1024*1024)));
                        } catch (ParseException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        content.add(Double.valueOf(mSize));
                        content.add(exp.get("Title"));
                        content.add(exp.get("Species"));
                        content.add(Integer.valueOf(exp.get("Spectrum Count")));
                        content.add(Integer.valueOf(exp.get("Identification Count")));
                        content.add(Integer.valueOf(exp.get("Peptide Count")));
                        content.add(true);
                        contents.add(content);
                    }
                }
            }
        }

        private boolean isAlreadyExist(String accession) {
            boolean exist = false;
            for (List<Object> content : contents) {
                if (content.get(0).equals(accession)) {
                    exist = true;
                }
            }
            return exist;
        }

        /**
         * Enable cell editor
         *
         * @param columnIndex column number
         * @return Class<?> column class type
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return CollectionUtils.getElement(columnNames.values(), columnIndex);
        }

        /**
         * Is Cell content is Boolean then it is editable
         *
         * @param rowIndex    row number
         * @param columnIndex column number
         * @return boolean  true if cell is editable
         */
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            boolean editable = false;
            Object val = getValueAt(rowIndex, columnIndex);
            if (val instanceof Boolean) {
                editable = true;
            }
            return editable;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            List<Object> content = contents.get(rowIndex);
            content.set(columnIndex, aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private enum TableHeader {

        EXP_ACC_COLUMN("Accession", String.class),
        SIZE_COLUMN("Size (M)", Double.class),
        EXP_TITLE_COLUMN("Title", String.class),
        SPECIES_COLUMN("Species", String.class),
        SPECTRA_CNT_COLUMN("Spectra", Integer.class),
        PROTEIN_CNT_COLUMN("Protein", Integer.class),
        PEPTIDE_CNT_COLUMN("Peptide", Integer.class),
        DOWNLOAD_COLUMN("Download", Boolean.class);

        private final String header;
        private final Class headerClass;

        private TableHeader(String header, Class classType) {
            this.header = header;
            this.headerClass = classType;
        }

        public String getHeader() {
            return header;
        }

        public Class getHeaderClass() {
            return headerClass;
        }
    }
}
