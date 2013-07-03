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

    private boolean existIdentifiedSpectra = false;
    private boolean existUnidentifiedSpectra = false;

    public PrideSpectrumHistogram(PrideDataType type) {
        super(new PrideData[0], type);
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
    public PrideHistogramDataSource filter(PrideDataType type) {
        throw new UnsupportedOperationException("Not support filter operation in this histogram.");
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

    @Override
    public SortedMap<PrideHistogramBin, Collection<PrideData>> getHistogram() {
        SortedMap<PrideHistogramBin, Collection<PrideData>> histogram = new TreeMap<PrideHistogramBin, Collection<PrideData>>();

        // every cell of histogram, cell[0] is identified spectra intensity, cell[1] is unidentified and cell[2] is all.
        List<PrideData> cell;
        for (PrideHistogramBin bin : bins) {
            cell = new ArrayList<PrideData>();
            cell.add(new PrideData(0.0, PrideDataType.IDENTIFIED_SPECTRA));
            cell.add(new PrideData(0.0, PrideDataType.UNIDENTIFIED_SPECTRA));
            cell.add(new PrideData(0.0, PrideDataType.ALL_SPECTRA));
            histogram.put(bin, cell);
        }

        PrideData data;
        for (PridePeak peak : peakList) {
            for (PrideHistogramBin bin : histogram.keySet()) {
                if (peak.mz >= bin.getStartBoundary() && peak.mz < bin.getEndBoundary()) {
                    cell = (List) histogram.get(bin);
                    switch (peak.dataType) {
                        case IDENTIFIED_SPECTRA:
                            existIdentifiedSpectra = true;
                            data = cell.get(0);
                            data.setData(data.getData() + peak.intensity);
                            data = cell.get(2);
                            data.setData(data.getData() + peak.intensity);
                            break;
                        case UNIDENTIFIED_SPECTRA:
                            existUnidentifiedSpectra = true;
                            data = cell.get(1);
                            data.setData(data.getData() + peak.intensity);
                            data = cell.get(2);
                            data.setData(data.getData() + peak.intensity);
                            break;
                    }

                }
            }
        }

        return histogram;
    }

    public boolean isExistIdentifiedSpectra() {
        return existIdentifiedSpectra;
    }

    public boolean isExistUnidentifiedSpectra() {
        return existUnidentifiedSpectra;
    }
}
