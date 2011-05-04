package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.GelFreeIdentification;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.data.core.TwoDimIdentification;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 14:07:16
 */
public class RetrieveEntryTask<T> extends TaskAdapter<T, String> {

    private DataAccessController controller = null;
    private Comparable id = null;
    private Class<T> classType = null;

    public RetrieveEntryTask(DataAccessController controller, Class<T> classType, Comparable id) {
        this.controller = controller;
        this.id = id;
        this.classType = classType;
        this.setName("Loading " + classType.getSimpleName());
        this.setDescription("Loading " + classType.getSimpleName()+ "[ID: " + id + "]");
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
}
