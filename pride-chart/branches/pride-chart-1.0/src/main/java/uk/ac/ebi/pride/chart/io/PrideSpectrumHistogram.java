package uk.ac.ebi.pride.chart.io;

import uk.ac.ebi.pride.chart.dataset.*;
import uk.ac.ebi.pride.data.core.Spectrum;

import java.util.*;

/**
 * User: qingwei
 * Date: 02/07/13
 */
public class PrideSpectrumHistogram extends PrideEqualWidthHistogramDataSource {
    private class PridePeak {
        double mz;
        double intensity;
        PrideDataType dataType;

        private PridePeak(double mz, double intensity, PrideDataType dataType) {
            this.mz = mz;
            this.intensity = intensity;
            this.dataType = dataType;
        }
    }

    private List<PridePeak> peakList = new ArrayList<PridePeak>();

    public PrideSpectrumHistogram(boolean calcAllSpectra) {
        super(new PrideData[0], calcAllSpectra);
    }

    public void addSpectrum(Spectrum spectrum, PrideDataType dataType) {
        double[] mzArray = spectrum.getMzBinaryDataArray().getDoubleArray();
        double[] intensityArray = spectrum.getIntensityBinaryDataArray().getDoubleArray();

        PridePeak peak;
        for (int i = 0; i < mzArray.length; i++) {
            peak = new PridePeak(
                    mzArray[i],
                    i < intensityArray.length ? intensityArray[i] : 0d,
                    dataType
            );
            peakList.add(peak);
        }
    }

    @Override
    public Collection<PrideHistogramBin> generateBins(double start, double binWidth) {
        if (binWidth <= 0) {
            throw new IllegalArgumentException("Bin width should be great than 0");
        }

        double end = Double.MIN_VALUE;
        for (PridePeak peak : peakList) {
            double v = peak.mz;
            if (v > end) {
                end = v;
            }
        }

        int binCount = (int)Math.ceil((end - start) / binWidth);
        return generateBins(start, binWidth, binCount);
    }

    private SortedMap<PrideHistogramBin, Double> createEmptyHistogram() {
        SortedMap<PrideHistogramBin, Double> histogram = new TreeMap<PrideHistogramBin, Double>();
        for (PrideHistogramBin bin : bins) {
            histogram.put(bin, 0.0);
        }
        return histogram;
    }

    public SortedMap<PrideDataType, SortedMap<PrideHistogramBin, Double>> getIntensityMap() {
        SortedMap<PrideDataType, SortedMap<PrideHistogramBin, Double>> histMap = new TreeMap<PrideDataType, SortedMap<PrideHistogramBin, Double>>();

        SortedMap<PrideHistogramBin, Double> idHistogram;
        SortedMap<PrideHistogramBin, Double> unHistogram;
        SortedMap<PrideHistogramBin, Double> allHistogram = createEmptyHistogram();
        histMap.put(PrideDataType.ALL_SPECTRA, allHistogram);

        for (PridePeak peak : peakList) {
            for (PrideHistogramBin bin : bins) {
                if (peak.mz >= bin.getStartBoundary() && peak.mz < bin.getEndBoundary()) {
                    if (peak.dataType == PrideDataType.IDENTIFIED_SPECTRA) {
                        idHistogram = histMap.get(PrideDataType.IDENTIFIED_SPECTRA);
                        if (idHistogram == null) {
                            idHistogram = createEmptyHistogram();
                            histMap.put(PrideDataType.IDENTIFIED_SPECTRA, idHistogram);
                        }
                        idHistogram.put(bin, idHistogram.get(bin) + peak.intensity);
                    } else if (peak.dataType == PrideDataType.UNIDENTIFIED_SPECTRA) {
                        unHistogram = histMap.get(PrideDataType.UNIDENTIFIED_SPECTRA);
                        if (unHistogram == null) {
                            unHistogram = createEmptyHistogram();
                            histMap.put(PrideDataType.UNIDENTIFIED_SPECTRA, unHistogram);
                        }
                        unHistogram.put(bin, unHistogram.get(bin) + peak.intensity);
                    } else if (peak.dataType == PrideDataType.ALL_SPECTRA) {
                        allHistogram.put(bin, allHistogram.get(bin) + peak.intensity);
                    }

                    if (isCalcAllSpectra()) {
                        allHistogram.put(bin, allHistogram.get(bin) + peak.intensity);
                    }
                    break;
                }
            }
        }

        return histMap;
    }
}
