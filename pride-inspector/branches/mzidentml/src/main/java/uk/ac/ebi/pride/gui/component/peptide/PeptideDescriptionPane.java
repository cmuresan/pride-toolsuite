package uk.ac.ebi.pride.gui.component.peptide;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.jdesktop.swingx.JXTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.action.impl.DecoyFilterAction;
import uk.ac.ebi.pride.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTreeTableModel;
import uk.ac.ebi.pride.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;
import uk.ac.ebi.pride.gui.task.TaskUtil;
import uk.ac.ebi.pride.gui.task.impl.FilterPeptideRankingTask;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * PeptideDescriptionPane displays all peptides details.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 11:53:51
 */
public class PeptideDescriptionPane extends DataAccessControllerPane {
    private static final Logger logger = LoggerFactory.getLogger(PeptideDescriptionPane.class);

    private enum PeptideRankingFilter {
        LESS_EQUAL_THAN_ONE("<= 1", 1),
        LESS_EQUAL_THAN_TWO("<= 2", 2),
        LESS_EQUAL_THAN_THREE("<= 3", 3),
        ALL("All", 1000);


        private String rankingFilter;
        private int rankingThreshold;

        private PeptideRankingFilter(String rankingFilter, int rankingThreshold) {
            this.rankingFilter = rankingFilter;
            this.rankingThreshold = rankingThreshold;
        }

        private String getRankingFilter() {
            return rankingFilter;
        }

        private int getRankingThreshold() {
            return rankingThreshold;
        }

        public static java.util.List<String> getRankingFilters() {
            java.util.List<String> filters = new ArrayList<String>();

            for (PeptideRankingFilter peptideRankingFilter : values()) {
                filters.add(peptideRankingFilter.getRankingFilter());
            }

            return filters;
        }

        public static int getRankingThreshold(String filter) {
            for (PeptideRankingFilter peptideRankingFilter : values()) {
                if (peptideRankingFilter.getRankingFilter().equalsIgnoreCase(filter)) {
                    return peptideRankingFilter.getRankingThreshold();
                }
            }

            return -1;
        }
    }

    /**
     * peptide details table
     */
    private JXTreeTable pepTable;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public PeptideDescriptionPane(DataAccessController controller) {
        super(controller);
    }

    /**
     * Setup the main display pane
     */
    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        try {
            pepTable = TableFactory.createPeptideTreeTable(controller.getAvailablePeptideLevelScores(), PeptideRankingFilter.LESS_EQUAL_THAN_ONE.rankingThreshold);
        } catch (DataAccessException e) {
            String msg = "Failed to retrieve search engine details";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // meta data panel
        JPanel titlePanel = buildHeaderPane();
        this.add(titlePanel, BorderLayout.NORTH);

        // add selection listener
        pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

        JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Build the header panel
     *
     * @return JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        JPanel metaDataPanel = buildMetaPane();

        // create button panel
        JToolBar toolBar = buildButtonPane();

        // add both meta data and button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(toolBar, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel buildMetaPane() {
        JPanel metaDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metaDataPanel.setOpaque(false);

        // table label
        JLabel label = new JLabel("Peptide spectrum match");
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        metaDataPanel.add(label);

        return metaDataPanel;
    }

    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(BorderFactory.createEmptyBorder());
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // filter peptide by ranking
        JLabel rankingFilterLabel = new JLabel("Filter by ranking: ");
        toolBar.add(rankingFilterLabel);

        JComboBox rankingFilterList = getRankingFilterComboBox();
        toolBar.add(rankingFilterList);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // load protein names
        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
        loadAllProteinNameButton.setForeground(Color.blue);
        loadAllProteinNameButton.setAction(new ExtraProteinDetailAction(controller));
        toolBar.add(loadAllProteinNameButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // decoy filter
        JButton decoyFilterButton = GUIUtilities.createLabelLikeButton(null, null);
        decoyFilterButton.setForeground(Color.blue);
        PrideAction action = appContext.getPrideAction(controller, DecoyFilterAction.class);
        if (action == null) {
            action = new DecoyFilterAction(controller);
            appContext.addPrideAction(controller, action);
        }
        decoyFilterButton.setAction(action);
        toolBar.add(decoyFilterButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // expand button
        Icon expandIcon = GUIUtilities.loadIcon(appContext.getProperty("expand.table.icon.small"));
        JButton expandButton = GUIUtilities.createLabelLikeButton(expandIcon, null);
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideDescriptionPane.this);
                eventBus.publish(new ExpandPanelEvent(PeptideDescriptionPane.this, PeptideDescriptionPane.this));
            }
        });
        toolBar.add(expandButton);

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        CSH.setHelpIDString(helpButton, "help.browse.peptide");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }

    private JComboBox getRankingFilterComboBox() {
        JComboBox rankingFilterList = new JComboBox(PeptideRankingFilter.getRankingFilters().toArray());
        rankingFilterList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox filterComboBox = (JComboBox)e.getSource();
                String filter = (String) filterComboBox.getSelectedItem();
                int rankingThreshold = PeptideRankingFilter.getRankingThreshold(filter);
                PeptideTreeTableModel treeTableModel = (PeptideTreeTableModel)pepTable.getTreeTableModel();
                FilterPeptideRankingTask filterPeptideRankingTask = new FilterPeptideRankingTask(treeTableModel, rankingThreshold);
                TaskUtil.startBackgroundTask(filterPeptideRankingTask, controller);
            }
        });
        return rankingFilterList;
    }

    /**
     * Return peptide table
     *
     * @return JTable  peptide table
     */
    public JXTreeTable getPeptideTable() {
        return pepTable;
    }

    /**
     * Trigger when a peptide is selected
     */
    @SuppressWarnings("unchecked")
    private class PeptideSelectionListener implements ListSelectionListener {
        private final JTable table;
        private int previousSelectedRow;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
            this.previousSelectedRow = -1;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0 && rowNum != previousSelectedRow) {
                    previousSelectedRow = rowNum;
                    logger.debug("Peptide table has been clicked, row number: {}", rowNum);
                    // get table model
                    PeptideTreeTableModel treeTableModel = (PeptideTreeTableModel)pepTable.getTreeTableModel();


                    // get spectrum reference column
                    int identColNum = treeTableModel.getColumnIndex(PeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
                    int peptideColNum = treeTableModel.getColumnIndex(PeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());

                    TreePath treePath = pepTable.getPathForRow(rowNum);
                    // get spectrum id
                    Object rowNode = treePath.getLastPathComponent();
                    Comparable identId = (Comparable) treeTableModel.getValueAt(rowNode, identColNum);
                    Comparable peptideId = (Comparable) treeTableModel.getValueAt(rowNode, peptideColNum);

                    logger.debug("Peptide table selection:  Protein id: " + identId + " Peptide Id: " + peptideId);

                    // fire a background task to retrieve peptide
                    if (peptideId != null && identId != null) {
                        // publish the event to local event bus
                        EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideDescriptionPane.this);
                        eventBus.publish(new PeptideEvent(PeptideDescriptionPane.this, controller, identId, peptideId));
                    }
                }
            }
        }
    }
}
