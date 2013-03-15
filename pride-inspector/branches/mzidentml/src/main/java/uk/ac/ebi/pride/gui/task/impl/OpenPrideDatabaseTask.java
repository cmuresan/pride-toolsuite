package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.gui.EDTUtils;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.EmptyDataAccessController;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.lang.reflect.InvocationTargetException;

/**
 * Open a connection to pride database
 * <p/>
 * User: dani, rwang
 * Date: 04-Aug-2010
 * Time: 14:45:00
 */

public class OpenPrideDatabaseTask extends TaskAdapter<DataAccessController, Void> {
    private static final Logger logger = LoggerFactory.getLogger(PrideDBAccessControllerImpl.class);

    /**
     * data access controller to pride public instance
     */
    private PrideDBAccessControllerImpl dbAccessController = null;

    /**
     * This defines the default experiment accession to open
     */
    private Comparable experimentAccession;

    /**
     * Reference to PRIDE context
     */
    PrideInspectorContext context;

    /**
     * This will not register the data access controller to data access monitor
     */
    public OpenPrideDatabaseTask() {
        this("Connecting to PRIDE", "Connecting to PRIDE", null);
    }

    /**
     * This will register the data access controller to data access monitor
     *
     * @param experimentAcc foreground experiment accession
     */
    public OpenPrideDatabaseTask(Comparable experimentAcc) {
        this("Opening Experiment " + experimentAcc, "Opening Experiment " + experimentAcc, experimentAcc);
    }

    /**
     * Open a connection to pride database
     *
     * @param name   name of the task
     * @param desc   description of the task
     * @param expAcc experiment accession
     */
    public OpenPrideDatabaseTask(String name, String desc, Comparable expAcc) {
        this.setName(name);
        this.setDescription(desc);
        this.experimentAccession = expAcc;
        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DataAccessController doInBackground() throws Exception {
        boolean opened = alreadyOpened(experimentAccession);
        try {
            if (opened) {
                // if already opened
                openExistingDataAccessController(experimentAccession);
            } else {
                // open a new controller
                createNewDataAccessController(experimentAccession);
            }

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        } catch (InterruptedException e) {
            logger.warn("Open PRIDE experiment action has been interrupted");
        }

        return dbAccessController;
    }

    /**
     * Check whether the experiment is already opened.
     *
     * @param acc experiment accession
     * @return boolean  true if it is already opened.
     */
    private boolean alreadyOpened(Comparable acc) {
        // check whether the node has been selected before
        boolean exist = false;

        java.util.List<DataAccessController> controllers = context.getControllers();
        for (DataAccessController controller : controllers) {
            if (DataAccessController.Type.DATABASE.equals(controller.getType())&&
                    ((PrideDBAccessControllerImpl)controller).getExperimentAcc().equals(acc)) {
                exist = true;
            }
        }

        return exist;
    }

    /**
     * This method is called if the experiment is already open, then the experiment will be
     * bring to the foreground.
     *
     */
    private void openExistingDataAccessController(final Comparable acc) throws InvocationTargetException, InterruptedException {
        Runnable code = new Runnable() {

            @Override
            public void run() {
                // show warning message
                GUIUtilities.warn(Desktop.getInstance().getMainComponent(), "Experiment " + acc.toString() +
                        " is already opened", context.getProperty("open.existing.experiment.title"));
            }
        };
        EDTUtils.invokeAndWait(code);
    }

    /**
     * Create new DB data access controller
     *
     * @param acc experiment accession
     */
    private void createNewDataAccessController(Comparable acc) {
        try {
            EmptyDataAccessController dummy = null;
            if (acc != null) {
                dummy = new EmptyDataAccessController();
                //dummy.setForegroundExperimentAcc(acc);
                dummy.setName("PRIDE Experiment " + acc);
                dummy.setType(DataAccessController.Type.DATABASE);
                // add a closure hook
                this.addOwner(dummy);
                context.addDataAccessController(dummy, false);
            }

            //connect to database
            dbAccessController = new PrideDBAccessControllerImpl(acc);
            if (acc != null) {
                dbAccessController.setName("PRIDE Experiment " + acc);
                // this is important for cancelling
                if (Thread.interrupted()) {
                    context.removeDataAccessController(dummy, false);
                    throw new InterruptedException();
                } else {
                    context.replaceDataAccessController(dummy, dbAccessController, false);
                }
            }
        }catch (DataAccessException err) {
            String msg = "<html><b>Failed to connect to PRIDE public database.</b> <br/>As an alternative, you can download experiments from PRIDE website</html>";
            logger.error(msg, err);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Connection Error");
        } catch (OutOfMemoryError mex) {
            PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), context.getProperty("out.of.memory.message"), context.getProperty("out.of.memory.title"));
        } catch (InterruptedException e) {
            logger.warn("Connection to PRIDE public database has been cancelled", e);
        }
    }
}
