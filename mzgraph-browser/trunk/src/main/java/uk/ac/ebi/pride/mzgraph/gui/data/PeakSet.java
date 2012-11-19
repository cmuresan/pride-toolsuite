package uk.ac.ebi.pride.mzgraph.gui.data;

import java.util.*;

/**
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public class PeakSet extends TreeSet<Peak> {
    public PeakSet() {
    }

    public PeakSet(Collection<Peak> peakList) {
        super(peakList);
    }

    /**
     * Translate the m/z Array and intensity Array into a set of peaks.
     * <P>Notice: the length of two array should be equal.</P>
     * @param mzArray if null, return a empty peak set.
     * @param intensityArray if null, return a empty peak set.
     * @return
     */
    public static PeakSet getInstance(double[] mzArray, double[] intensityArray) {
        if (mzArray == null || intensityArray == null) {
            return new PeakSet();
        }

        if (mzArray.length != intensityArray.length) {
            throw new IllegalArgumentException("the mz array not equal to intensity array!");
        }

        PeakSet peakSet = new PeakSet();

        Peak peak;
        for (int i = 0; i < mzArray.length; i++) {
            peak = new Peak(mzArray[i], intensityArray[i]);
            peakSet.add(peak);
        }

        return peakSet;
    }

    public List<PeakSet> splitPeaksWindow(int splitSize) {
        if (splitSize <= 1) {
            throw new IllegalArgumentException("Can not split peaks into windows which size less than 2");
        }

        List<PeakSet> windowList = new ArrayList<PeakSet>();

        int step = 0;
        Peak peak;
        PeakSet window = new PeakSet();
        Iterator<Peak> it = iterator();
        while (it.hasNext()) {
            peak = it.next();
            if (step < splitSize) {
                window.add(peak);
            } else {
                windowList.add(window);
                step = 0;
                window = new PeakSet();
            }
        }

        return windowList;
    }

    /**
     * Create a subset of peak set. The range is [m/z - interval, m/z + interval].
     *
     * @param mz should be in the range of peak set. that is, [first_peak.m/z, last_peak.m/z]. Otherwise,
     *           return empty peak set.
     * @param interval should great than 0. Otherwise, return empty peak set.
     * @return
     */
    public PeakSet subSet(double mz, double interval) {
        if (size() == 0) {
            return new PeakSet();
        }

        if (mz < first().getMz()) {
            return new PeakSet();
        }

        if (mz > last().getMz()) {
            return new PeakSet();
        }

        if (interval <= 0d) {
            return new PeakSet();
        }

        double start = mz - interval;
        double end = mz + interval;

        //adjust start and end location.
        start = start < first().getMz() ? first().getMz() : start;
        end = end > last().getMz() ? last().getMz() : end;

        //create a mock start and end peak.
        Peak startPeak = new Peak(start, 0d);
        Peak endPeak = new Peak(end, 0d);

        // location concrete start and end peak within peaks window.
        startPeak = floor(startPeak);
        endPeak = ceiling(endPeak);

        NavigableSet<Peak> set = subSet(startPeak, true, endPeak, true);
        // create a new peak set, therefore, we can remove a peak from peak set safely.
        PeakSet result = new PeakSet(set);

        if (mz - startPeak.getMz() > interval) {
            result.remove(startPeak);
        }

        if (endPeak.getMz() - mz > interval) {
            result.remove(endPeak);
        }

        return result;
    }

    /**
     *
     * @return m/z array of peak set.
     */
    public double[] getMzArray() {
        double[] mzArray = new double[size()];

        Iterator<Peak> it = iterator();
        Peak peak;
        int i = 0;
        while (it.hasNext()) {
            peak = it.next();
            mzArray[i] = peak.getMz();
            i++;
        }

        return mzArray;
    }

    /**
     *
     * @return intensity array of peak set.
     */
    public double[] getIntensityArray() {
        double[] intensityArray = new double[size()];

        Iterator<Peak> it = iterator();
        Peak peak;
        int i = 0;
        while (it.hasNext()) {
            peak = it.next();
            intensityArray[i] = peak.getIntensity();
            i++;
        }

        return intensityArray;
    }

    /**
     *
     * @return the maximum intensity peak within the peaks window. If more than one peaks,
     * return the first matching peak.
     */
    public Peak getMaxIntensityPeak() {
        Peak represent = new Peak(0d, Double.MIN_VALUE);
        Iterator<Peak> it = iterator();

        Peak peak;
        while (it.hasNext()) {
            peak = it.next();
            if (peak.getIntensity() > represent.getIntensity()) {
                represent = peak;
            }
        }

        return represent;
    }

    /**
     *
     * @return the minimum intensity peak within the peaks window. If more than one peaks,
     * return the first matching peak.
     */
    public Peak getMinIntensityPeak() {
        Peak represent = new Peak(0d, Double.MAX_VALUE);
        Iterator<Peak> it = iterator();

        Peak peak;
        while (it.hasNext()) {
            peak = it.next();
            if (peak.getIntensity() < represent.getIntensity()) {
                represent = peak;
            }
        }

        return represent;
    }


    public PeakSet getIntensityPeakSet(double lowerIntensity, boolean lowerInclusive,
                                       double upperIntensity, boolean upperInclusive) {
        PeakSet result = new PeakSet();

        if (lowerIntensity > upperIntensity) {
            return result;
        }

        Iterator<Peak> it = iterator();

        boolean lower;
        boolean upper;

        double intensity;
        Peak peak;
        while (it.hasNext()) {
            lower = false;
            upper = false;

            peak = it.next();
            intensity = peak.getIntensity();
            if (lowerInclusive) {
                if (intensity >= lowerIntensity) {
                    lower = true;
                }
            } else {
                if (intensity > lowerIntensity) {
                    lower = true;
                }
            }

            if (upperInclusive) {
                if (upperIntensity >= intensity) {
                    upper = true;
                }
            } else {
                if (upperIntensity > intensity) {
                    upper = true;
                }
            }

            if (lower && upper) {
                result.add(peak);
            }
        }

        return result;
    }

}
