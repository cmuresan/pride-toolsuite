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
 * Date: 01-Mar-2010
 * Time: 16:15:08
 */
public class UpdateForegroundEntryTask<T> extends Task<T, Void> {

    private DataAccessController controller = null;
    private String id = null;
    private Class<T> classType = null;

    public UpdateForegroundEntryTask(DataAccessController controller, Class<T> classType, String id) {
        this.controller = controller;
        this.id = id;
        this.classType = classType;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T doInBackground() throws Exception {
        Object result = null;
        if (Spectrum.class.equals(classType)) {
            controller.setForegroundSpectrumById(id);
            result = controller.getForegroundSpectrum();
        } else if (Chromatogram.class.equals(classType)) {
            controller.setForegroundChromatogramById(id);
            result = controller.getForegroundChromatogram();
        } else if (TwoDimIdentification.class.equals(classType)) {
            controller.setForegroundTwoDimIdentById(id);
            result = controller.getForegroundTwoDimIdent();
        } else if (GelFreeIdentification.class.equals(classType)) {
            controller.setForegroundGelFreeIdentById(id);
            result = controller.getForegroundGelFreeIdent();
        }

        return (T)result;
    }

    @Override
    protected void finished() {}

    @Override
    protected void succeed(T results) {}

    @Override
    protected void cancelled() {}

    @Override
    protected void interrupted(InterruptedException iex) {}
}
