package uk.ac.ebi.pride.gui.action;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.DBAccessControllerImpl;
import uk.ac.ebi.pride.data.utils.LoggerUtils;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:49:36
 */
public class OpenDatabaseAction extends PrideAction {
    private static final Logger logger = Logger.getLogger(DBAccessControllerImpl.class.getName());

    public OpenDatabaseAction() {
        super("Open Pride Database");
        setMenuLocation("File");
        setAccelerator(java.awt.event.KeyEvent.VK_B, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // ToDo: implement
        DBAccessControllerImpl dbAccessController = null;
        // ToDo: implement
        try{
            //connect to database
            dbAccessController = new DBAccessControllerImpl();
            dbAccessController.setName("Database connection");

        }
        catch(DataAccessException err){
            LoggerUtils.error(logger, this, err);
        }
        if (dbAccessController != null){
            //if connection successful, add Monitor
            PrideViewerContext context = ((PrideViewerContext) Desktop.getInstance().getDesktopContext());
            context.getDataAccessMonitor().addDataAccessController(dbAccessController);
        }

    }
}
