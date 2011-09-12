package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.coreIdent.ExperimentMetaData;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;

/**
 * Retrieving MetaData information from the given data access controller.
 *
 * User: rwang
 * Date: 22-Oct-2010
 * Time: 12:18:34
 */
public class RetrieveMetaDataTask extends AbstractDataAccessTask<ExperimentMetaData, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEntryTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Metadata";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Experiment Metadata";

    public RetrieveMetaDataTask(DataAccessController controller) {
        super(controller);
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected ExperimentMetaData retrieve() throws Exception {
        ExperimentMetaData metaData = null;
        try {
            metaData = controller.getExperimentMetaData();
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve meta data from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }
        return metaData;
    }
}
