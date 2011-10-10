package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;

/**
 * Get protein identification by id
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 16:08
 */
public class RetrieveIdentificationTask extends AbstractDataAccessTask<Identification, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveIdentificationTask.class);

    private Comparable identId;

    public RetrieveIdentificationTask(DataAccessController controller, Comparable identId) {
        super(controller);
        this.identId = identId;
    }

    @Override
    protected Identification retrieve() throws Exception {
        Identification result = null;

        try {
                result = controller.getIdentificationById(identId);
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve data entry from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return result;
    }
}
