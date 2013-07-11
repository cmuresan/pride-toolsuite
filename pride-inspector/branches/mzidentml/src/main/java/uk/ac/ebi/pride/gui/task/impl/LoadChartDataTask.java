package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.io.DataAccessReader;
import uk.ac.ebi.pride.chart.io.ElderJSONReader;
import uk.ac.ebi.pride.chart.io.PrideDataException;
import uk.ac.ebi.pride.chart.io.PrideDataReader;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.gui.component.message.MessageType;

/**
 * <p>Class to load the charts data in background.</p>
 *
 * @author Antonio Fabregat
 * Date: 26-ago-2010
 * Time: 11:59:12
 */
public class LoadChartDataTask extends AbstractDataAccessTask<PrideDataReader, Void> {
    private static final Logger logger = LoggerFactory.getLogger(LoadChartDataTask.class);

    public LoadChartDataTask(DataAccessController controller) {
        super(controller);
        this.setName("Loading chart data");
        this.setDescription("Loading chart data");
    }

    @Override
    protected PrideDataReader retrieve() throws Exception {
        DataAccessReader reader = null;

        try {
            reader = new DataAccessReader(controller);
        } catch (PrideDataException ex) {
            String msg = "Failed to get summary charts";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }

        return reader;
    }
}
