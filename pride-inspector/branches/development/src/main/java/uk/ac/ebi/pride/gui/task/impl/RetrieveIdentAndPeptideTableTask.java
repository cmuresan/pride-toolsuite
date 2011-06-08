package uk.ac.ebi.pride.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.utils.CollectionUtils;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;
import uk.ac.ebi.pride.gui.component.report.ReportMessage;
import uk.ac.ebi.pride.gui.component.table.TableRowDataRetriever;
import uk.ac.ebi.pride.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.gui.event.SummaryReportEvent;

import java.util.Collection;
import java.util.List;

/**
 * Retrieve details for identification and peptide table.
 * <p/>
 * User: rwang
 * Date: 14-Sep-2010
 * Time: 11:34:33
 */
public class RetrieveIdentAndPeptideTableTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, List<Object>>> {
    private final static Logger logger = LoggerFactory.getLogger(RetrieveIdentAndPeptideTableTask.class);

    /**
     * the size of each read iteration, for example: return every 100 spectra
     */
    private static final int DEFAULT_ITERATION_SIZE = 100;
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Proteins And Peptides";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Proteins And Peptides Details";
    /**
     * the start index
     */
    private int start;
    /**
     * the number of entries to retrieve
     */
    private int size;

    /**
     * Retrieve all the identifications.
     *
     * @param controller DataAccessController
     * @throws uk.ac.ebi.pride.data.controller.DataAccessException
     *          thrown when there is error while reading the data.
     */
    public RetrieveIdentAndPeptideTableTask(DataAccessController controller) throws DataAccessException {
        this(controller, 0, controller.getNumberOfIdentifications());
    }

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     */
    public RetrieveIdentAndPeptideTableTask(DataAccessController controller, int start) {
        this(controller, start, DEFAULT_ITERATION_SIZE);
    }

    /**
     * Retrieve a subset of identifications.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     * @param size       the total size of the identifications to retrieve.
     */
    public RetrieveIdentAndPeptideTableTask(DataAccessController controller,
                                            int start,
                                            int size) {
        super(controller);
        this.start = start;
        this.size = size;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void runDataAccess() throws Exception {
        try {
            Collection<Comparable> identIds = controller.getIdentificationIds();
            // count stores the number of peptides without any spectrum
            int missingSpectrumLinks = 0;

            int identSize = identIds.size();
            if (start >= 0 && start < identSize && size > 0) {
                int stop = start + size;
                stop = stop > identSize ? identSize : stop;

                // check internet availability
                for (int i = start; i < stop; i++) {
                    // get identification id
                    Comparable identId = CollectionUtils.getElement(identIds, i);

                    // get and publish protein related details
                    List<Object> identContent = TableRowDataRetriever.getIdentificationTableRow(controller, identId);
                    publish(new Tuple<TableContentType, List<Object>>(TableContentType.IDENTIFICATION, identContent));

                    // get and publish peptide related details
                    Collection<Comparable> ids = controller.getPeptideIds(identId);
                    if (ids != null) {
                        for (Comparable peptideId : ids) {
                            List<Object> peptideContent = TableRowDataRetriever.getPeptideTableRow(controller, identId, peptideId);
                            publish(new Tuple<TableContentType, List<Object>>(TableContentType.PEPTIDE, peptideContent));
                            if (controller.getPeptideSpectrumId(identId, peptideId) == null) {
                                missingSpectrumLinks++;
                            }
                        }
                    }

                    checkInterruption();
                }
            }

            if (missingSpectrumLinks > 0) {
                EventBus.publish(new SummaryReportEvent(this, controller, new ReportMessage(ReportMessage.Type.ERROR, "Missing spectra [" + missingSpectrumLinks + "]", "The number of peptides without spectrum links")));
            }
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve protein and peptide related data";
            logger.error(msg, dex);
            PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        } catch (InterruptedException ex) {
            logger.warn("Protein table and peptide table update has been cancelled");
        }
        return null;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}
