package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzIdentMLControllerImpl;
import uk.ac.ebi.pride.data.core.SpectraData;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.EmptyDataAccessController;
import uk.ac.ebi.pride.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.gui.component.peptide.PeptideTabPane;
import uk.ac.ebi.pride.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Task to open mzML/MzIdentML or PRIDE xml files.
 * Note: this task doesn't check whether file has been loaded before
 * This is handled by OpenFileAction.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 01-Feb-2010
 * Time: 10:37:49
 */
public class OpenFileTask<D extends DataAccessController> extends TaskAdapter<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(OpenFileTask.class);
    /**
     * file to open
     */
    private File inputFile;

    /**
     * reference pride inspector context
     */
    private PrideInspectorContext context;

    /**
     * the class type of the data access controller to open the file
     */
    private Class<D> dataAccessControllerClass;

    private List<File> msFiles = null;

    public OpenFileTask(File inputFile, Class<D> dataAccessControllerClass, String name, String description) {
        this.inputFile = inputFile;
        this.dataAccessControllerClass = dataAccessControllerClass;
        this.setName(name);
        this.setDescription(description);

        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
    }

    public OpenFileTask(File inputFile, List<File> msFiles, Class<D> dataAccessControllerClass, String name, String description) {
        this.inputFile = inputFile;
        this.dataAccessControllerClass = dataAccessControllerClass;
        this.setName(name);
        this.setDescription(description);
        context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
        this.msFiles = msFiles;
    }



    @Override
    protected Void doInBackground() throws Exception {
        boolean opened = alreadyOpened(inputFile);
        if (opened) {
            openExistingDataAccessController(inputFile);
        } else {
            // publish a notice for starting the file loading
            publish("Loading " + inputFile.getName());
            createNewDataAccessController(inputFile);
        }

        return null;
    }

    /**
     * Check whether the file has been opened before
     *
     * @param file the input file to check
     * @return boolean true if it has been opened before
     */
    private boolean alreadyOpened(File file) {
        boolean isOpened = false;

        List<DataAccessController> controllers = context.getControllers();
        for (DataAccessController controller : controllers) {
            if (file.equals(controller.getSource())) {
                isOpened = true;
            }
        }

        return isOpened;
    }

    /**
     * This method is called if the experiment is already open, then the experiment will be
     * bring to the foreground.
     *
     * @param file file to open.
     */
    private void openExistingDataAccessController(File file) {
        java.util.List<DataAccessController> controllers = context.getControllers();
        for (DataAccessController controller : controllers) {
            if (DataAccessController.Type.XML_FILE.equals(controller.getType()) &&
                    controller.getSource().equals(file)) {
                context.setForegroundDataAccessController(controller);
            }
        }
    }

    /**
     * Create new DB data access controller
     *
     * @param file file to open
     */
    private void createNewDataAccessController(File file) {
        try {
            // create dummy
            EmptyDataAccessController dummy = new EmptyDataAccessController();
            dummy.setName(inputFile.getName());
            if(dataAccessControllerClass == MzIdentMLControllerImpl.class){
                dummy.setType(DataAccessController.Type.MZIDENTML);
            }else{
                dummy.setType(DataAccessController.Type.XML_FILE);
            }
            // add a closure hook
            this.addOwner(dummy);
            context.addDataAccessController(dummy);

            Constructor<D> cstruct = dataAccessControllerClass.getDeclaredConstructor(File.class);
            DataAccessController controller = cstruct.newInstance(inputFile);

            if(dataAccessControllerClass == MzIdentMLControllerImpl.class && msFiles != null){
                Map<SpectraData, File> msFileMap = null;
                try {
                    msFileMap = ((MzIdentMLControllerImpl) controller).checkMScontrollers(msFiles);
                    ((MzIdentMLControllerImpl)controller).addMSController(msFileMap);
                } catch (DataAccessException e1) {
                    logger.error("Failed to check the files as controllers", e1);
                }
            }

            // this is important for cancelling
            if (Thread.interrupted()) {
                // remove dummy
                context.removeDataAccessController(dummy, false);
                throw new InterruptedException();
            } else {
                // add the real thing
                context.replaceDataAccessController(dummy, controller, false);
            }
        } catch (InterruptedException ex) {
            logger.warn("File loading has been interrupted: {}", file.getName());
        } catch (Exception err) {
            String msg = "Failed to loading from the file: " + file.getName();
            logger.error(msg, err);
            GUIUtilities.error(Desktop.getInstance().getMainComponent(), msg, "Open File Error");
        }
    }
}
