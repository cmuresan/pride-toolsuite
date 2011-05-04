package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.core.Chromatogram;
import uk.ac.ebi.pride.data.core.MzGraph;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.gui.task.TaskAdapter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

/**
 * Retrieve all the MzGraph from DataAccessController, either Spectrum or Chromatogram
 * User: rwang
 * Date: 12-Apr-2010
 * Time: 16:55:59
 */
public class RetrieveMzGraphTask<T extends MzGraph> extends TaskAdapter<Void, T> {

    /** the size of each read iteration, for example: return every 100 spectra */
    private static final int DEFAULT_CACHE_SIZE = 100;
    /** Data access controller */
    private DataAccessController controller = null;
    /** MzGraph class, either spectrum or chromatogram */
    private final Class<T> mzClass;
    private int offset;
    private int start;
    private boolean isOneIter = false;

    public RetrieveMzGraphTask(DataAccessController controller, Class<T> mzGraphClass) {
        this.controller = controller;
        this.mzClass = mzGraphClass;
        String title = "";
        if (mzGraphClass.equals(Spectrum.class)) {
            title = "Loading Spectra";
        } else if (mzGraphClass.equals(Chromatogram.class)) {
            title = "Loading Chromatograms";
        }
        this.setName(title);
        this.setDescription(title);
        this.offset = DEFAULT_CACHE_SIZE;
    }

    public RetrieveMzGraphTask(DataAccessController controller, Class<T> mzGraphClass, int start, int max_size) {
        this.controller = controller;
        this.mzClass = mzGraphClass;

        String title = "";
        if (mzGraphClass.equals(Spectrum.class)) {
            title = "Loading Spectra";
        } else if (mzGraphClass.equals(Chromatogram.class)) {
            title = "Loading Chromatograms";
        }
        this.setName(title);
        this.setDescription(title);
        this.offset = max_size;
        this.start = start;
        this.isOneIter = true;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // get a list of ids
        boolean isSpectrum = mzClass.equals(Spectrum.class);
        Collection<Comparable> ids = isSpectrum ? controller.getSpectrumIds() : controller.getChromatogramIds();
        // iterate over all the ids
        T[] cachedMzGraph = (T[])Array.newInstance(mzClass, offset);
        int count = 0;
        int max_size_array;
        if (ids != null) {
            if (start + offset < ids.size()){
               max_size_array = start + offset;
            }
            else{
                max_size_array = ids.size();
            }
            for(int i = start; i < max_size_array; i++){
                Comparable[] array = (Comparable[])ids.toArray(new Comparable[ids.size()]);
                Comparable id = array[i];
                T mzObj = (T)(isSpectrum ? controller.getSpectrumById(id) : controller.getChromatogramById(id));
                cachedMzGraph[count] = mzObj;
                count++;
                if ((count%offset) == 0) {
                    this.publish(cachedMzGraph);
                    cachedMzGraph = (T[])Array.newInstance(mzClass, offset);
                    count = 0;
                    if (isOneIter) {
                        break;
                    }
                }
            }
            // finish unfinished business here
            if (count > 0) {
                T[] subArr = Arrays.copyOfRange(cachedMzGraph, 0, count);
                this.publish(subArr);
            }
        }
        return null;
    }
}
