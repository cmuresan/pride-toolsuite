package uk.ac.ebi.pride.gui.component.db;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.DatabaseSearchTableModel;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.gui.search.Criteria;
import uk.ac.ebi.pride.gui.search.SearchEntry;
import uk.ac.ebi.pride.gui.task.impl.OpenPrideDatabaseTask;
import uk.ac.ebi.pride.gui.task.impl.SearchDatabaseTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * DatabaseSearchPane is the main panel contains a search box and a search result table
 *
 * All the search actions for the PRIDE public instance is done through this panel
 *
 */
public class DatabaseSearchPane extends DataAccessControllerPane<Void, Void> {
    private static final String PANE_TITLE = "Search Database";
    private static final Color BACKGROUND_COLOUR = Color.white;

    private JLabel searchLabel;
    private JComboBox categoryComboBox;
    private JComboBox criteriaComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JCheckBox searchResultCheckBox;
    private JTable searchResultTable;
    private JPanel resultSummaryPanel;
    private JButton closeButton;
    private JButton openSelectedButton;
    private JLabel searchResultLabel;

    private int resultCount = 0;

    public DatabaseSearchPane(JComponent parentComp) {
        super(null, parentComp);
        // enable annotation
        AnnotationProcessor.process(this);
    }

    protected void setupMainPane() {
        this.setBackground(BACKGROUND_COLOUR);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
        this.setTitle(PANE_TITLE);

        // set the final icon
        this.setIcon(GUIUtilities.loadIcon(appContext.getProperty("database.search.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(appContext.getProperty("database.search.loading.icon.small")));
    }


    protected void addComponents() {
        JPanel container = new JPanel();
        JPanel helpButtonPanel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        searchLabel = new JLabel();
        categoryComboBox = new JComboBox(new FieldComboBoxModel());
        criteriaComboBox = new JComboBox(Criteria.toArray());
        searchTextField = new JTextField();
        searchButton = new JButton();
        searchResultCheckBox = new JCheckBox();
        JPanel panel4 = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        searchResultTable = TableFactory.createDatabaseSearchTable();
        resultSummaryPanel = new JPanel();
        searchResultLabel = new JLabel();
        closeButton = new JButton();
        openSelectedButton = new JButton();

        // help button
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);

        //======== container panel ==========
        container.setLayout(new BorderLayout());
        container.setOpaque(false);

        //======== help button panel ========
        helpButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        helpButtonPanel.setOpaque(false);

        //-------- help button -----------
        helpButton.setToolTipText("Help on " + PANE_TITLE);
        CSH.setHelpIDString(helpButton, "help.browse.mzgraph");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        helpButtonPanel.add(helpButton);
        helpButtonPanel.setPreferredSize(new Dimension(200, 30));
        container.add(helpButtonPanel, BorderLayout.NORTH);
        add(helpButtonPanel, BorderLayout.NORTH);

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new FlowLayout());

            //======== panel2 ========
            {
                panel2.setOpaque(false);

                //======== panel3 ========
                {
                    panel3.setBorder(new LineBorder(Color.black));
                    panel3.setBackground(new Color(214, 241, 249));

                    //---- searchLabel ----
                    searchLabel.setText("Search for:");

                    //---- categoryComboBox ----
                    categoryComboBox.setOpaque(false);

                    //---- criteriaComboBox ----
                    criteriaComboBox.setSelectedIndex(0);
                    criteriaComboBox.setEditable(false);
                    criteriaComboBox.setOpaque(false);

                    //---- searchTextField ----
                    searchTextField.addKeyListener(new SearchKeyListener());

                    //---- searchButton ----
                    searchButton.setText("Search");
                    searchButton.setOpaque(false);
                    searchButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            search();
                        }
                    });

                    //---- searchResultCheckBox ----
                    searchResultCheckBox.setText("Search within results");
                    searchResultCheckBox.setOpaque(false);
                    searchResultCheckBox.setEnabled(false);

                    GroupLayout panel3Layout = new GroupLayout(panel3);
                    panel3.setLayout(panel3Layout);
                    panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                    .add(panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .add(searchLabel)
                                            .addPreferredGap(LayoutStyle.RELATED)
                                            .add(categoryComboBox, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                                            .add(12, 12, 12)
                                            .add(criteriaComboBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.RELATED)
                                            .add(searchTextField, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.RELATED)
                                            .add(searchButton)
                                            .addContainerGap())
                                    .add(GroupLayout.TRAILING, panel3Layout.createSequentialGroup()
                                            .addContainerGap(451, Short.MAX_VALUE)
                                            .add(searchResultCheckBox, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                                            .addContainerGap())
                    );
                    panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                    .add(GroupLayout.TRAILING, panel3Layout.createSequentialGroup()
                                            .addContainerGap()
                                            .add(panel3Layout.createParallelGroup(GroupLayout.BASELINE)
                                                    .add(searchButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .add(searchTextField)
                                                    .add(categoryComboBox)
                                                    .add(searchLabel)
                                                    .add(criteriaComboBox))
                                            .add(9, 9, 9)
                                            .add(searchResultCheckBox)
                                            .addContainerGap())
                    );
                }

                GroupLayout panel2Layout = new GroupLayout(panel2);
                panel2.setLayout(panel2Layout);
                panel2Layout.setHorizontalGroup(
                        panel2Layout.createParallelGroup()
                                .add(panel2Layout.createSequentialGroup()
                                        .addContainerGap()
                                        .add(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
                panel2Layout.setVerticalGroup(
                        panel2Layout.createParallelGroup()
                                .add(panel2Layout.createSequentialGroup()
                                        .addContainerGap(12, Short.MAX_VALUE)
                                        .add(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                );
            }
            panel1.add(panel2);
        }
        container.add(panel1, BorderLayout.NORTH);

        //======== panel4 ========
        {
            panel4.setBackground(BACKGROUND_COLOUR);

            //-------- searchResultLabel -----------
            searchResultLabel.setFont(searchResultLabel.getFont().deriveFont(Font.BOLD));

            //======== scrollPane1 ========
            {
                scrollPane1.setOpaque(false);
                scrollPane1.setBorder(BorderFactory.createLineBorder(Color.black));

                //---- searchResultTable ----
                searchResultTable.setBorder(null);
                searchResultTable.setFillsViewportHeight(true);
                scrollPane1.setViewportView(searchResultTable);
            }

            //======== resultSummaryPanel ========
            {
                resultSummaryPanel.setBackground(BACKGROUND_COLOUR);
                resultSummaryPanel.setOpaque(false);
                resultSummaryPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                resultSummaryPanel.add(searchResultLabel);
            }

            //---- closeButton ----
            closeButton.setText("Close");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventBus.publish(new DatabaseSearchEvent<Void>(null, DatabaseSearchEvent.Status.HIDE));
                }
            });

            //---- openSelectedButton ----
            openSelectedButton.setText("Open selected");
            openSelectedButton.addActionListener(new OpenSelectionListener(searchResultTable));

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                    panel4Layout.createParallelGroup()
                            .add(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .add(panel4Layout.createParallelGroup()
                                            .add(GroupLayout.TRAILING, panel4Layout.createSequentialGroup()
                                                    .add(openSelectedButton, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
                                                    .add(18, 18, 18)
                                                    .add(closeButton, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                    .add(20, 20, 20))
                                            .add(GroupLayout.TRAILING, panel4Layout.createSequentialGroup()
                                                    .add(panel4Layout.createParallelGroup(GroupLayout.TRAILING)
                                                            .add(resultSummaryPanel, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                                                            .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                                                    .addContainerGap())))
            );
            panel4Layout.setVerticalGroup(
                    panel4Layout.createParallelGroup()
                            .add(panel4Layout.createSequentialGroup()
                                    .add(resultSummaryPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(panel4Layout.createParallelGroup(GroupLayout.BASELINE)
                                            .add(closeButton)
                                            .add(openSelectedButton))
                                    .add(13, 13, 13))
            );
        }
        container.add(panel4, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }


    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchResultEvent(DatabaseSearchEvent evt) {
        switch (evt.getStatus()) {
            case START:
                Icon icon = GUIUtilities.loadIcon(appContext.getProperty("loading.small.icon"));
                searchResultLabel.setIcon(icon);
                break;
            case COMPLETE:
                searchResultLabel.setIcon(null);
                break;
            case RESULT:
                // enable search result check box
                if (!searchResultCheckBox.isEnabled()) {
                    searchResultCheckBox.setEnabled(true);
                }

                // update search result label
                java.util.List<java.util.List<String>> results = (java.util.List<java.util.List<String>>) evt.getResult();
                resultCount += results.size();
                searchResultLabel.setText(resultCount + " results found");
                break;
        }
    }

    private void search() {
        // reset the search result count
        resultCount = 0;

        // check the status of the search result check box
        boolean searchWithinResults = searchResultCheckBox.isSelected();

        // clear the content in search result table
        DatabaseSearchTableModel tableModel = (DatabaseSearchTableModel) searchResultTable.getModel();
        // get the existing content of the table if to search within the resutls
        java.util.List<java.util.List<String>> contents = null;
        java.util.List<String> headers = null;
        if (searchWithinResults) {
            contents = tableModel.getAllContent();
            headers = tableModel.getAllHeaders();
        }
        tableModel.removeAllRows();

        // get search entry
        String field = categoryComboBox.getSelectedItem().toString();
        String criteria = criteriaComboBox.getSelectedItem().toString();
        Criteria c = Criteria.getCriteria(criteria);
        String term = searchTextField.getText().trim();
        SearchEntry searchEntry = new SearchEntry(field, c, term);

        SearchDatabaseTask task;
        if (searchWithinResults) {
            // search within the existing results
            task = new SearchDatabaseTask(searchEntry, headers, contents);
        } else {
            task = new SearchDatabaseTask(searchEntry);
        }
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
        appContext.addTask(task);
    }


    /**
     * Key listener to trigger a search action
     */
    private class SearchKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                search();
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    /**
     * Action listener for open selection button
     */
    private class OpenSelectionListener implements ActionListener {

        private JTable table;

        private OpenSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            TableModel tableModel = table.getModel();
            int rowCnt = tableModel.getRowCount();
            int colCnt = tableModel.getColumnCount();
            int selectColIndex = -1;
            int viewColIndex = -1;
            for (int i = 0; i < colCnt; i++) {
                if (DatabaseSearchTableModel.TableHeader.SELECTED.getHeader().equals(tableModel.getColumnName(i))) {
                    selectColIndex = i;
                } else if (DatabaseSearchTableModel.TableHeader.VIEW.getHeader().equals(tableModel.getColumnName(i))) {
                    viewColIndex = i;
                }
            }

            // open selected experiments
            if (selectColIndex >= 0) {
                for (int i = 0; i < rowCnt; i++) {
                    if ((Boolean) tableModel.getValueAt(i, selectColIndex)) {
                        Comparable accession = (Comparable) tableModel.getValueAt(i, viewColIndex);
                        OpenPrideDatabaseTask task = new OpenPrideDatabaseTask(accession);
                        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
                        appContext.addTask(task);
                    }
                }
            }
        }
    }
}

