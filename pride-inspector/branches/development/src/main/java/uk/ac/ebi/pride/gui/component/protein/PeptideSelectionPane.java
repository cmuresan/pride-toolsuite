package uk.ac.ebi.pride.gui.component.protein;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.table.TableFactory;
import uk.ac.ebi.pride.gui.component.table.listener.PeptideCellMouseClickListener;
import uk.ac.ebi.pride.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.gui.component.table.model.ProgressiveUpdateTableModel;
import uk.ac.ebi.pride.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.gui.event.container.PeptideEvent;
import uk.ac.ebi.pride.gui.event.container.ProteinIdentificationEvent;
import uk.ac.ebi.pride.gui.task.Task;
import uk.ac.ebi.pride.gui.task.impl.RetrievePeptideTableTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * PeptideSelectionPane displays a list of peptide for a selected identification.
 * <p/>
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 11:26:45
 */
public class PeptideSelectionPane extends DataAccessControllerPane<Peptide, Void> implements EventBusSubscribable{
    private static final Logger logger = LoggerFactory.getLogger(PeptideSelectionPane.class);

    /**
     * the title for ptm label
     */
    private final static String PTM_LABEL = "<html><b>PTM</b>: ";
    private final static String PEPTIDE_TABLE_DESC = "<html><b>Peptide Details</b></html>";

    /**
     * peptide table for peptide related details
     */
    private JTable pepTable;

    /**
     * PTM label display an overview of the PTMs
     */
    private JLabel ptmLabel;

    /**
     * Subscriber for event bus on protein selection event
     */
    private SelectProteinIdentSubscriber proteinIdentSubscriber;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public PeptideSelectionPane(DataAccessController controller) {
        super(controller);
    }

    /**
     * Setup main pane
     */
    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBackground(Color.white);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    /**
     * Add the rest of the components
     */
    @Override
    protected void addComponents() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // table label
        JLabel tableLabel = new JLabel(PEPTIDE_TABLE_DESC);
        metaDataPanel.add(tableLabel);

        // rigid area
        metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        // PTM
        ptmLabel = new JLabel(PTM_LABEL + "NONE</html>");
        metaDataPanel.add(ptmLabel);
        this.add(metaDataPanel, BorderLayout.NORTH);

        // create identification table
        try {
            pepTable = TableFactory.createPeptideTable(controller.getSearchEngine(), true);
        } catch (DataAccessException e) {
            String msg = "Failed to retrieve search engine details";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // add row selection listener
        pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

        // add mouse listener for ptm selection
        String protAccColumnHeader = PeptideTableModel.TableHeader.MAPPED_PROTEIN_ACCESSION_COLUMN.getHeader();
        String peptideColumnHeader = PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader();
        pepTable.addMouseMotionListener(new TableCellMouseMotionListener(pepTable, protAccColumnHeader, peptideColumnHeader));
        PeptideCellMouseClickListener peptideMouseListener = new PeptideCellMouseClickListener(pepTable, peptideColumnHeader);
        pepTable.addMouseListener(peptideMouseListener);

        JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus() {
        // get local event bus
        EventService eventBus = ContainerEventServiceFinder.getEventService(this);

        // subscriber
        proteinIdentSubscriber = new SelectProteinIdentSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(ProteinIdentificationEvent.class, proteinIdentSubscriber);
    }

    /**
     * Listen to the event when a new protein identification has been selected
     */
    private class SelectProteinIdentSubscriber implements EventSubscriber<ProteinIdentificationEvent> {

        @Override
        public void onEvent(ProteinIdentificationEvent event) {
            Comparable identId = event.getIdentificationId();
            logger.debug("Identification has been selected: {}", identId);

            // update ptm label
            updatePTMLabel(identId);

            // clear peptide table,
            PeptideTableModel tableModel = (PeptideTableModel) pepTable.getModel();

            // reset sorting behavior
            pepTable.setRowSorter(new NumberTableRowSorter(tableModel));
            tableModel.removeAllRows();

            // cancel ongoing table update tasks
            cancelOngoingTableUpdates(tableModel);

            // update peptide table
            updateTable(tableModel, identId);
        }

        /**
         * Update PTM summary label
         *
         * @param identId identification id
         */
        private void updatePTMLabel(Comparable identId) {
            try {
                // generate the ptm label string
                String ptmValues = generateModString(identId);

                // set the string to label
                if ("".equals(ptmValues)) {
                    ptmLabel.setText(PTM_LABEL + "NONE</html>");
                } else {
                    ptmLabel.setText(PTM_LABEL + ptmValues + "</html>");
                }
            } catch (DataAccessException e) {
                String msg = "Failed to generated PTM summary label";
                logger.error(msg, e);
                appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
            }
        }

        /**
         * Cancel ongoing table update task
         *
         * @param tableModel peptide table model
         */
        private void cancelOngoingTableUpdates(ProgressiveUpdateTableModel tableModel) {
            // stop any running retrieving task
            java.util.List<Task> existingTask = appContext.getTask(tableModel);
            for (Task task : existingTask) {
                appContext.cancelTask(task, true);
            }
        }

        /**
         * Fire up a peptide table update table in the background.
         *
         * @param tableModel peptide table model
         * @param identId    identification id
         */
        @SuppressWarnings("unchecked")
        private void updateTable(ProgressiveUpdateTableModel tableModel, Comparable identId) {
            try {
                RetrievePeptideTableTask retrieveTask = new RetrievePeptideTableTask(PeptideSelectionPane.this.getController(), identId);
                retrieveTask.addTaskListener(tableModel);
                retrieveTask.setGUIBlocker(new DefaultGUIBlocker(retrieveTask, GUIBlocker.Scope.NONE, null));
                appContext.addTask(retrieveTask);
            } catch (DataAccessException e) {
                String msg = "Failed to retrieve information for peptide table";
                logger.error(msg, e);
                appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
            }
        }

        /**
         * Generate PTM summary string, it gathers all the unique amino acids with the same modification.
         *
         * @param identId identificaiton id
         * @return String   ptm summary string in the format of [amin acids - monoisotopic weight]
         * @throws DataAccessException data access exception
         */
        private String generateModString(Comparable identId) throws DataAccessException {
            String modStr = "";
            Map<String, Map<String, Double>> modMap = new HashMap<String, Map<String, Double>>();
            Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
            if (peptideIds != null) {
                for (Comparable peptideId : peptideIds) {
                    String seq = controller.getPeptideSequence(identId, peptideId);
                    Collection<Modification> mods = controller.getPTMs(identId, peptideId);
                    for (Modification mod : mods) {
                        // get accession
                        String accession = mod.getAccession();
                        Map<String, Double> aminoAcidMap = modMap.get(accession);
                        if (aminoAcidMap == null) {
                            aminoAcidMap = new HashMap<String, Double>();
                            modMap.put(accession, aminoAcidMap);
                        }
                        // get the amino acid according to the location
                        int location = mod.getLocation();
                        location = location == 0 ? 1 : (location == accession.length() + 1 ? location - 1 : location);
                        if (location > 0 && location <= seq.length()) {
                            String aminoAcid = String.valueOf(seq.charAt(location - 1));
                            // get delta mass (monoisotopic)
                            double massDelta = -1;
                            java.util.List<Double> massDeltas = mod.getMonoMassDeltas();
                            if (massDeltas != null && !massDeltas.isEmpty()) {
                                massDelta = mod.getMonoMassDeltas().get(0);
                            }
                            aminoAcidMap.put(aminoAcid, massDelta);
                        }
                    }
                }
            }

            DecimalFormat formatter = new DecimalFormat("#.####");
            for (Map<String, Double> aminoAcidMap : modMap.values()) {
                StringBuilder aminoAcids = new StringBuilder();
                double massDelta = -1;
                for (Map.Entry<String, Double> aminoAcidEntry : aminoAcidMap.entrySet()) {
                    aminoAcids.append(aminoAcidEntry.getKey());
                    massDelta = aminoAcidEntry.getValue();
                }
                modStr += "[" + aminoAcids.toString() + " - " + (massDelta == -1 ? " Unknown" : formatter.format(massDelta)) + "] ";
            }
            return modStr;
        }
    }

    /**
     * Trigger when a peptide is selected from the table,
     * a new background task will be started to retrieve the peptide.
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
                    // get table model
                    PeptideTableModel tableModel = (PeptideTableModel) table.getModel();
                    // get identification and peptide column
                    int identColNum = tableModel.getColumnIndex(PeptideTableModel.TableHeader.IDENTIFICATION_ID_COLUMN.getHeader());
                    int peptideColNum = tableModel.getColumnIndex(PeptideTableModel.TableHeader.PEPTIDE_ID_COLUMN.getHeader());

                    // get identification and peptide id
                    if (table.getRowCount() > 0) {
                        int modelRowIndex = table.convertRowIndexToModel(rowNum);
                        Comparable identId = (Comparable) tableModel.getValueAt(modelRowIndex, identColNum);
                        Comparable peptideId = (Comparable) tableModel.getValueAt(modelRowIndex, peptideColNum);

                        if (peptideId != null && identId != null) {
                            // publish the event to local event bus
                            EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideSelectionPane.this);
                            eventBus.publish(new PeptideEvent(PeptideSelectionPane.this, controller, identId, peptideId));
                        }
                    }
                }
            }
        }
    }
}
