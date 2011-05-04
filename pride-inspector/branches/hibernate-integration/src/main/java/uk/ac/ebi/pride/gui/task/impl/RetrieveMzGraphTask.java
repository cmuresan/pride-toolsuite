package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.task.Task;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

/**
 * Retrieve all the MzGraph from DataAccessController, either Spectrum or Chromatogram
 * User: rwang
 * Date: 12-Apr-2010
 * Time: 16:55:59
 */
public class RetrieveMzGraphTask<T extends MzGraph> extends Task<Void, T> {

    /** the size of each read iteration, for example: return every 100 spectra */
    private static final int CACHE_SIZE = 100;
    /** Data access controller */
    private DataAccessController controller = null;
    /** MzGraph class, either spectrum or chromatogram */
    private Class<T> mzClass;

    public RetrieveMzGraphTask(DataAccessController controller, Class<T> mzGraphClass) {
        this.controller = controller;
        this.mzClass = mzGraphClass;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // get a list of ids
        boolean isSpectrum = mzClass.equals(Spectrum.class);
        Collection<Comparable> ids = isSpectrum ? controller.getSpectrumIds() : controller.getChromatogramIds();
        // iterate over all the ids
        T[] cachedMzGraph = (T[])Array.newInstance(mzClass, CACHE_SIZE);
        int count = 0;
        if (ids != null) {
            for(Comparable id : ids) {
                T mzObj = (T)(isSpectrum ? controller.getSpectrumById(id) : controller.getChromatogramById(id));
                cachedMzGraph[count] = mzObj;
                count++;
                if ((count%CACHE_SIZE) == 0) {
                    this.publish(cachedMzGraph);
                    cachedMzGraph = (T[])Array.newInstance(mzClass, CACHE_SIZE);
                    count = 0;
                }
            }
            // finish unfinished business here
            if (count > 0) {
                T[] subArr = Arrays.copyOfRange(cachedMzGraph, 0, count );
                this.publish(subArr);
            }
        }
        return null;
    }

    @Override
    protected void finished() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void succeed(Void results) {
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
