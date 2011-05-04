package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.task.Task;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 14:07:16
 */
public class RetrieveEntryTask<T> extends Task<T, String> {

    private DataAccessController controller = null;
    private String id = null;
    private String type = null;

    public RetrieveEntryTask(DataAccessController controller, String id, String type) {
        this.controller = controller;
        this.id = id;
        this.type = type;
    }

    @Override
    protected void finished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void succeed(T results) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void cancelled() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void interrupted(InterruptedException iex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doInBackground() throws Exception {

        Object result = null;

        if (DataAccessController.SPECTRUM_TYPE.equals(type)) {
            result = controller.getSpectrumById(id);
        } else if (DataAccessController.CHROMATOGRAM_TYPE.equals(type)) {
            result = controller.getChromatogramById(id);
        } else if (DataAccessController.TWO_DIM_IDENTIFICATION_TYPE.equals(type)) {
            result = controller.getTwoDimIdentById(id);
        } else if (DataAccessController.GEL_FREE_IDENTIFICATION_TYPE.equals(type)) {
            result = controller.getGelFreeIdentById(id);
        } else if (DataAccessController.EXPERIMENT_TYPE.equals(type)) {
            result = controller.getExperimentById(id);
        }

        return (T)result;
    }
}
