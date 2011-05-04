package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.GelFreeIdentification;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;
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
    private Class<T> classType = null;

    public RetrieveEntryTask(DataAccessController controller, Class<T> classType, String id) {
        this.controller = controller;
        this.id = id;
        this.classType = classType;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doInBackground() throws Exception {
        Object result = null;

        if (Spectrum.class.equals(classType)) {
            result = controller.getSpectrumById(id);
        } else if (Chromatogram.class.equals(classType)) {
            result = controller.getChromatogramById(id);
        } else if (TwoDimIdentification.class.equals(classType)) {
            result = controller.getTwoDimIdentById(id);
        } else if (GelFreeIdentification.class.equals(classType)) {
            result = controller.getGelFreeIdentById(id);
        }

        return (T)result;
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
}
