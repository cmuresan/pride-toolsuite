package uk.ac.ebi.pride.chart.io;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;


/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class DataAccessReader extends PrideDataReader {
    private Logger logger = Logger.getLogger(DataAccessReader.class);
    private String source = "DataAccessController";

    private DataAccessController controller;

    public DataAccessReader(DataAccessController controller) throws PrideDataException {
        if (controller == null) {
            throw new NullPointerException(source + " is null!");
        }
        this.controller = controller;

        readData();
    }

    @Override
    protected void start() {
        super.start(source);
    }

    @Override
    protected void reading() throws PrideDataException {

    }

    @Override
    protected void end() {
        super.end(source);
    }
}
