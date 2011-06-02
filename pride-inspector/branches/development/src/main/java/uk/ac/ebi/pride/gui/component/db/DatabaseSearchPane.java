package uk.ac.ebi.pride.gui.component.db;

import org.bushe.swing.event.EventBus;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import org.jdesktop.swingx.JXTable;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.event.DatabaseSearchEvent;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class DatabaseSearchPane extends DataAccessControllerPane<Void, Void> {
    private static final String PANE_TITLE = "Search Database";
    private static final Color BACKGROUND_COLOUR = Color.white;

    private JLabel searchLabel;
    private JComboBox categoryComboBox;
    private JComboBox criteriaComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JCheckBox searchResultCheckBox;
    private JXTable searchResultTable;
    private JPanel resultSummaryPanel;
    private JButton closeButton;

    private PrideInspectorContext context;

    public DatabaseSearchPane(JComponent parentComp) {
        super(null, parentComp);
    }

    protected void setupMainPane() {
        this.setBackground(BACKGROUND_COLOUR);
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
        this.setTitle(PANE_TITLE);

        // set the final icon
        context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        this.setIcon(GUIUtilities.loadIcon(context.getProperty("database.search.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(context.getProperty("database.search.loading.icon.small")));
    }


    protected void addComponents() {
        JPanel container = new JPanel();
        JPanel helpButtonPanel = new JPanel();
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        searchLabel = new JLabel();
        categoryComboBox = new JComboBox();
        criteriaComboBox = new JComboBox();
        searchTextField = new JTextField();
        searchButton = new JButton();
        searchResultCheckBox = new JCheckBox();
        JPanel panel4 = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        searchResultTable = new JXTable();
        resultSummaryPanel = new JPanel();
        JLabel searchResultLabel = new JLabel();
        closeButton = new JButton();
        // help button
        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
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
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        helpButtonPanel.add(helpButton);
        helpButtonPanel.setPreferredSize(new Dimension(200, 25));
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
                    criteriaComboBox.setOpaque(false);

                    //---- searchButton ----
                    searchButton.setText("Search");
                    searchButton.setOpaque(false);

                    //---- searchResultCheckBox ----
                    searchResultCheckBox.setText("Search within results");
                    searchResultCheckBox.setOpaque(false);

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
                                            .add(criteriaComboBox, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.RELATED)
                                            .add(searchTextField, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.RELATED)
                                            .add(searchButton)
                                            .addContainerGap())
                                    .add(GroupLayout.TRAILING, panel3Layout.createSequentialGroup()
                                            .addContainerGap(451, Short.MAX_VALUE)
                                            .add(searchResultCheckBox, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
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

            //======== scrollPane1 ========
            {
                scrollPane1.setOpaque(false);

                //---- searchResultTable ----
                searchResultTable.setColumnControlVisible(true);
                searchResultTable.setBorder(null);
                searchResultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

            GroupLayout panel4Layout = new GroupLayout(panel4);
            panel4.setLayout(panel4Layout);
            panel4Layout.setHorizontalGroup(
                    panel4Layout.createParallelGroup()
                            .add(panel4Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .add(panel4Layout.createParallelGroup()
                                            .add(panel4Layout.createSequentialGroup()
                                                    .add(panel4Layout.createParallelGroup(GroupLayout.TRAILING)
                                                            .add(GroupLayout.LEADING, scrollPane1, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                                                            .add(resultSummaryPanel, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                                                    .addContainerGap())
                                            .add(GroupLayout.TRAILING, panel4Layout.createSequentialGroup()
                                                    .add(closeButton, GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                                                    .add(20, 20, 20))))
            );
            panel4Layout.setVerticalGroup(
                    panel4Layout.createParallelGroup()
                            .add(panel4Layout.createSequentialGroup()
                                    .add(resultSummaryPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(scrollPane1, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.RELATED)
                                    .add(closeButton)
                                    .add(13, 13, 13))
            );
        }
        container.add(panel4, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }
}

