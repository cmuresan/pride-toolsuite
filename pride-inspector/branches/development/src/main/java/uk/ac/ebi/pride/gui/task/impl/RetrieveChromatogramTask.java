package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;

/**
 * Retrieve chromatogram
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 15:54
 */
public class RetrieveChromatogramTask extends AbstractDataAccessTask<Chromatogram, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEntryTask.class);

    private Comparable chromaId;

    public RetrieveChromatogramTask(DataAccessController controller, Comparable chromaId) {
        super(controller);
        this.chromaId = chromaId;
    }

    @Override
    protected Chromatogram runDataAccess() throws Exception {
        Chromatogram result = null;

        try {
            result = controller.getChromatogramById(chromaId);
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve data entry from data source";
            logger.error(msg, dex);
            PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
            context.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return result;
    }
}
