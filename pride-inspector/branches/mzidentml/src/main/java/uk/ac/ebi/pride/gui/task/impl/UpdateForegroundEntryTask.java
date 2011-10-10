package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.data.core.Identification;
import uk.ac.ebi.pride.data.core.Spectrum;

/**
 * Retrieve foreground entry using data access controller
 * 
 * User: rwang
 * Date: 01-Mar-2010
 * Time: 16:15:08
 */
public class UpdateForegroundEntryTask<T> extends AbstractDataAccessTask<T, Void> {

    private Comparable id = null;
    private Class<T> classType = null;

    public UpdateForegroundEntryTask(DataAccessController controller, Class<T> classType, Comparable id) {
        super(controller);
        this.id = id;
        this.classType = classType;
        this.setName("Loading " + classType.getSimpleName());
        this.setDescription("Loading " + classType.getSimpleName() + "[ID: " + id + "]");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T retrieve() throws Exception {
        Object result = null;
        if (ExperimentMetaData.class.equals(classType)) {
            controller.setForegroundExperimentAcc(id);
            result = controller.getForegroundExperimentAcc();
        } else if (Spectrum.class.equals(classType)) {
            controller.setForegroundSpectrumById(id);
            result = controller.getForegroundSpectrum();
        } else if (Chromatogram.class.equals(classType)) {
            controller.setForegroundChromatogramById(id);
            result = controller.getForegroundChromatogram();
        } else if (Identification.class.equals(classType)) {
            controller.setForegroundIdentificationById(id);
            result = controller.getForegroundIdentification();
        }
        
        return (T)result;
    }
}
