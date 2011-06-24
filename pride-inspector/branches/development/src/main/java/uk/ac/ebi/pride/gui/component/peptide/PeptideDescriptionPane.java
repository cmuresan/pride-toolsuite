package uk.ac.ebi.pride.gui.component.peptide;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.action.impl.RetrieveExtraPeptideDetailAction;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.renderer.PeptideFitCellRenderer;
import uk.ac.ebi.pride.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PeptideDescriptionPane displays all peptides details.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 11:53:51
 */
public class PeptideDescriptionPane extends DataAccessControllerPane {
    private static final Logger logger = LoggerFactory.getLogger(PeptideDescriptionPane.class);

    /**
     * peptide details table
     */
    private JTable pepTable;

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
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        try {
            pepTable = TableFactory.createPeptideTable(controller.getSearchEngine(), controller, false);
            TableColumnExt peptideFitColumn = (TableColumnExt) pepTable.getColumn(PeptideTableModel.TableHeader.PEPTIDE_FIT.getHeader());
            peptideFitColumn.setCellRenderer(new PeptideFitCellRenderer());
            peptideFitColumn.setVisible(false);
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
        JLabel label = new JLabel("<html><b>Peptide Details</b></html>");
        metaDataPanel.add(label);

        return metaDataPanel;
    }

    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // load protein names
        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
        loadAllProteinNameButton.setForeground(Color.blue);

        loadAllProteinNameButton.setAction(new RetrieveExtraPeptideDetailAction(pepTable, controller));

        toolBar.add(loadAllProteinNameButton);

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

    /**
     * Return peptide table
     *
     * @return JTable  peptide table
     */
    public JTable getPeptideTable() {
        return pepTable;
    }

    /**
     * Trigger when a peptide is selected
     */
    @SuppressWarnings("unchecked")
    private class PeptideSelectionListener implements ListSelectionListener {
        private final JTable table;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    logger.debug("Peptide table has been clicked, row number: {}", rowNum);
                    // get table model
                    PeptideTableModel pepTableModel = (PeptideTableModel) pepTable.getModel();

                    // get spectrum reference column
                    int identColNum = pepTableModel.getColumnIndex(PeptideTableModel.TableHeader.IDENTIFICATION_ID.getHeader());
                    int peptideColNum = pepTableModel.getColumnIndex(PeptideTableModel.TableHeader.PEPTIDE_ID.getHeader());

                    // get spectrum id
                    int modelRowIndex = pepTable.convertRowIndexToModel(rowNum);
                    Comparable identId = (Comparable) pepTableModel.getValueAt(modelRowIndex, identColNum);
                    Comparable peptideId = (Comparable) pepTableModel.getValueAt(modelRowIndex, peptideColNum);

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
