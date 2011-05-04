package uk.ac.ebi.pride.gui.task.impl;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.DBAccessControllerImpl;
import uk.ac.ebi.pride.data.utils.LoggerUtils;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: dani
 * Date: 04-Aug-2010
 * Time: 14:45:00
 * To change this template use File | Settings | File Templates.
 */

public class OpenDatabaseTask<D extends DataAccessController> extends TaskAdapter<D, String> {
    private static final Logger logger = Logger.getLogger(DBAccessControllerImpl.class.getName());
    private DBAccessControllerImpl dbAccessController = null;

    public OpenDatabaseTask(){
        this.setName("Loading data from database");
        this.setDescription("Loading data from database");    
    }

    @Override
    protected void succeed(DataAccessController result) {
        // register the adapter to DesktopContext
        if (result != null) {
            PrideViewerContext context = ((PrideViewerContext) Desktop.getInstance().getDesktopContext());
            context.getDataAccessMonitor().addDataAccessController(result);
        }
    }

    @Override
    protected void cancelled() {
        releaseResources();
    }

    @Override
    protected void interrupted(InterruptedException iex) {
        releaseResources();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected D doInBackground() throws Exception {
        try {
            //connect to database
            dbAccessController = new DBAccessControllerImpl();
            dbAccessController.setName("Pride Public Instance");

        }
        catch (DataAccessException err) {
            LoggerUtils.error(logger, this, err);
        }

        return (D) dbAccessController;
    }

    private void releaseResources() {
        if (dbAccessController != null)
            dbAccessController.close();
    }
}
