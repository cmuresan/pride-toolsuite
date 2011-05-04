package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideViewerContext;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.Task;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import uk.ac.ebi.pride.data.controller.impl.MzMLHibernateControllerImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Andreas Schoenegger <aschoen@ebi.ac.uk>
 * Date: 22.07.2010
 * Time: 14:10:47
 */
public class OpenHibernateSessionTask<D extends DataAccessController> extends Task<D, String> {

    private Class<D> adaptorClass = null;
    private D adaptor = null;

    public OpenHibernateSessionTask(Class<D> adaptorClass, String description) {
        this.adaptorClass = adaptorClass;
        //this.setName(name);
        this.setDescription(description);
    }

    @Override
    protected void finished() {
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
    protected D doInBackground() throws Exception {
        // 1. build a DataAccessController instance
        this.publish("Opening Pride NextGen Hibernate Session");
        List<DataAccessController> controllers = ((PrideViewerContext) Desktop.getInstance().getDesktopContext()).getDataAccessMonitor().getControllers();

        boolean controllerExist = false;
        for (DataAccessController controller : controllers) {
            if (controller instanceof MzMLHibernateControllerImpl) {
                controllerExist = true;
                //TODO: adapt this so you can actually have multiple Hibernate Controllers
            }
        }

        //
        if (!controllerExist) {
            Constructor<D> cstruct = adaptorClass.getDeclaredConstructor(String.class);
            adaptor = cstruct.newInstance("Pride NextGen Session");
        }
        return adaptor;
    }

    private void releaseResources() {
        if (adaptor != null) {
            adaptor.close();
        }
    }
}
