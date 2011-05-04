package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.task.Task;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:15:08
 */
public class UpdateForegroundEntryTask<T> extends Task<T, String> {

    private DataAccessController controller = null;
    private String id = null;
    private String type = null;

    public UpdateForegroundEntryTask(DataAccessController controller, String id, String type) {
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
            controller.setForegroundSpectrumById(id);
            result = controller.getForegroundSpectrum();
        } else if (DataAccessController.CHROMATOGRAM_TYPE.equals(type)) {
            controller.setForegroundChromatogramById(id);
            result = controller.getForegroundChromatogram();
        } else if (DataAccessController.TWO_DIM_IDENTIFICATION_TYPE.equals(type)) {
            controller.setForegroundTwoDimIdentById(id);
            result = controller.getForegroundTwoDimIdent();
        } else if (DataAccessController.GEL_FREE_IDENTIFICATION_TYPE.equals(type)) {
            controller.setForegroundGelFreeIdentById(id);
            result = controller.getForegroundGelFreeIdent();
        } else if (DataAccessController.EXPERIMENT_TYPE.equals(type)) {
            controller.setForegroundExperimentById(id);
            result = controller.getForegroundExperiment();
        }
        
        return (T)result;
    }
}
