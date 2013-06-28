package uk.ac.ebi.pride.chart.io;

import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.*;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessUtilities;
import uk.ac.ebi.pride.data.core.Modification;
import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.data.core.Protein;
import uk.ac.ebi.pride.data.core.Spectrum;
import uk.ac.ebi.pride.mol.MoleculeUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;


/**
 * User: Qingwei
 * Date: 12/06/13
 */
public class DataAccessReader extends PrideDataReader {
    private static final int DELTA_BIN_COUNT = 200;
    private static final double DELTA_MIN_BIN_WIDTH = 0.0005;

    private static final double PRE_MIN_BIN_WIDTH = 100;

    private String source = "DataAccessController";

    private DataAccessController controller;

    private List<Double> deltaDomain = new ArrayList<Double>();
    private List<PrideData> deltaRange = new ArrayList<PrideData>();

    private Double[] peptidesDomain = new Double[6];
    private PrideData[] peptidesRange = new PrideData[6];

    private Double[] missedDomain = new Double[5];
    private PrideData[] missedRange = new PrideData[5];

    private List<Double> avgDomain = new ArrayList<Double>();
    private List<PrideData> avgRange = new ArrayList<PrideData>();

    private Double[] preChargeDomain = new Double[8];
    private PrideData[] preChargeRange = new PrideData[8];

    private List<Double> preMassesDomain = new ArrayList<Double>();
    private List<PrideData> preMassesRange = new ArrayList<PrideData>();

    public DataAccessReader(DataAccessController controller) throws PrideDataException {
        if (controller == null) {
            throw new NullPointerException(source + " is null!");
        }
        this.controller = controller;

        readData();
    }

    @Override
    protected void start() {
        super.start(source);
    }

    private Double calcDeltaMZ(Peptide peptide) {
        List<Double> modMassList = new ArrayList<Double>();
        for (Modification mod  : peptide.getModifications()) {
            modMassList.add(mod.getMonoisotopicMassDelta().get(0));
        }

        return MoleculeUtilities.calculateDeltaMz(
                peptide.getSequence(),
                peptide.getPrecursorMz(),
                peptide.getPrecursorCharge(),
                modMassList
        );
    }

    private int calcMissedCleavages(Peptide peptide) {
        String sequence = peptide.getSequence();
        //Always remove the last K or R from sequence
        sequence = sequence.replaceAll("[K|R]$", "");

        //We assume the hypothesis KR|P
        sequence = sequence.replaceAll("[K|R]P","");
        int initialLength = sequence.length();

        sequence = sequence.replaceAll("[K|R]","");
        return initialLength - sequence.length();
    }

    private double calcAverageMZ(Spectrum spectrum) {
        double[] dataList = spectrum.getMzBinaryDataArray().getDoubleArray();
        if (dataList == null || dataList.length == 0) {
            return 0;
        }

        double sum = 0;
        for (double v : dataList) {
            sum += v;
        }

        return sum / dataList.length;
    }

    private void readDelta(List<PrideData> deltaMZList) {
        PrideDataType dataType = PrideDataType.ALL_SPECTRA;

        PrideEqualWidthHistogramDataSource dataSource = new PrideEqualWidthHistogramDataSource(
                deltaMZList.toArray(new PrideData[deltaMZList.size()]),
                dataType
        );
        double start = Double.MAX_VALUE;
        double end = Double.MIN_VALUE;
        double v;
        for (PrideData data : deltaMZList) {
            v = data.getData();
            if (v < start) {
                start = v;
            }
            if (v > end) {
                end = v;
            }
        }
        double binWidth = (end - start) / DELTA_BIN_COUNT;
        binWidth = binWidth < DELTA_MIN_BIN_WIDTH ? DELTA_MIN_BIN_WIDTH : binWidth;
        dataSource.appendBins(dataSource.generateBins(-DELTA_BIN_COUNT * binWidth, binWidth, DELTA_BIN_COUNT * 2));

        SortedMap<PrideHistogramBin, Collection<PrideData>> histogram = dataSource.getHistogram();
        int maxFreq = 0;
        for (Collection<PrideData> cell : histogram.values()) {
            if (cell.size() > maxFreq) {
                maxFreq = cell.size();
            }
        }

        for (PrideHistogramBin bin : histogram.keySet()) {
            deltaDomain.add(bin.getStartBoundary());
            deltaRange.add(new PrideData(histogram.get(bin).size() * 1.0d / maxFreq, dataType));
        }

        for (int i = 0; i < deltaRange.size(); i++) {
            if (deltaRange.get(i).getData() == null) {
                System.out.println(i);
            }
        }

        xyDataSourceMap.put(PrideChartType.DELTA_MASS, new PrideXYDataSource(
                deltaDomain.toArray(new Double[deltaDomain.size()]),
                deltaRange.toArray(new PrideData[deltaRange.size()]),
                PrideDataType.ALL_SPECTRA
        ));
    }

    private void readPeptide(int[] peptideBars) {
        for (int i = 0; i < peptideBars.length; i++) {
            peptidesRange[i] = new PrideData(peptideBars[i] + 0.0, PrideDataType.ALL);
        }

        xyDataSourceMap.put(PrideChartType.PEPTIDES_PROTEIN, new PrideXYDataSource(
                peptidesDomain,
                peptidesRange,
                PrideDataType.ALL
        ));
    }

    private void readMissed(int[] missedBars) {
        for (int i = 0; i < missedBars.length; i++) {
            missedRange[i] = new PrideData(missedBars[i] + 0.0, PrideDataType.ALL_SPECTRA);
        }

        xyDataSourceMap.put(PrideChartType.MISSED_CLEAVAGES, new PrideXYDataSource(
                missedDomain,
                missedRange,
                PrideDataType.ALL_SPECTRA
        ));
    }

    private void readAvg(List<PrideData> averageMSList) {
        xyDataSourceMap.put(PrideChartType.AVERAGE_MS, new PrideXYDataSource(
                avgDomain.toArray(new Double[avgDomain.size()]),
                avgRange.toArray(new PrideData[avgRange.size()]),
                PrideDataType.ALL_SPECTRA
        ));
    }

    private void readPreCharge(int[] preChargeBars) {
        for (int i = 0; i < preChargeBars.length; i++) {
            preChargeRange[i] = new PrideData(preChargeBars[i] + 0.0, PrideDataType.IDENTIFIED_SPECTRA);
        }

        xyDataSourceMap.put(PrideChartType.PRECURSOR_CHARGE, new PrideXYDataSource(
                preChargeDomain,
                preChargeRange,
                PrideDataType.IDENTIFIED_SPECTRA
        ));
    }

    private void readPreMasses(List<PrideData> preMassedList) {
        PrideEqualWidthHistogramDataSource dataSource = new PrideEqualWidthHistogramDataSource(
                preMassedList.toArray(new PrideData[preMassedList.size()]),
                PrideDataType.ALL_SPECTRA
        );
        dataSource.appendBins(dataSource.generateBins(0d, PRE_MIN_BIN_WIDTH));
        SortedMap<PrideHistogramBin, Collection<PrideData>> histogram;

        // calculate identified spectra
        int identifiedCount = 0;
        histogram = dataSource.filter(PrideDataType.IDENTIFIED_SPECTRA).getHistogram();
        for (Collection<PrideData> cell : histogram.values()) {
            identifiedCount += cell.size();
        }
        if (identifiedCount != 0) {
            for (PrideHistogramBin bin : histogram.keySet()) {

                preMassesDomain.add(bin.getStartBoundary());
                preMassesRange.add(new PrideData(
                        histogram.get(bin).size() * 1.0d / identifiedCount,
                        PrideDataType.IDENTIFIED_SPECTRA
                ));
            }
        }

        // calculate unidentified spectra
        int unidentifiedCount = 0;
        histogram = dataSource.filter(PrideDataType.UNIDENTIFIED_SPECTRA).getHistogram();
        for (Collection<PrideData> cell : histogram.values()) {
            unidentifiedCount += cell.size();
        }
        if (unidentifiedCount != 0) {
            for (PrideHistogramBin bin : histogram.keySet()) {
                preMassesDomain.add(bin.getStartBoundary());
                preMassesRange.add(new PrideData(
                        histogram.get(bin).size() * 1.0d / unidentifiedCount,
                        PrideDataType.UNIDENTIFIED_SPECTRA
                ));
            }
        }

        // calculate all spectra
        int allCount = identifiedCount + unidentifiedCount;
        if (allCount != 0) {
            histogram = dataSource.getHistogram();
            for (PrideHistogramBin bin : histogram.keySet()) {
                preMassesDomain.add(bin.getStartBoundary());
            }
            for (PrideHistogramBin bin : histogram.keySet()) {
                if (allCount == 0) {

                }
                preMassesRange.add(new PrideData(
                        histogram.get(bin).size() * 1.0d / allCount,
                        PrideDataType.ALL_SPECTRA
                ));
            }
        }

        xyDataSourceMap.put(PrideChartType.PRECURSOR_MASSES, new PrideXYDataSource(
                preMassesDomain.toArray(new Double[preMassesDomain.size()]),
                preMassesRange.toArray(new PrideData[preMassesRange.size()]),
                PrideDataType.ALL_SPECTRA
        ));
    }

    private void readPeakMS(List<PrideData> peaksMSList) {
        PrideEqualWidthHistogramDataSource dataSource = new PrideEqualWidthHistogramDataSource(
                peaksMSList.toArray(new PrideData[peaksMSList.size()]),
                PrideDataType.ALL_SPECTRA
        );
        dataSource.appendBins(dataSource.generateGranularityBins(0d, 10, 50));

        histogramDataSourceMap.put(PrideChartType.PEAKS_MS, dataSource);
    }

    private void readPeakIntensity(List<PrideData> peaksIntensityList) {
        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(
                peaksIntensityList.toArray(new PrideData[peaksIntensityList.size()]),
                PrideDataType.ALL_SPECTRA
        );
        dataSource.appendBin(new PrideHistogramBin(1, 10));
        dataSource.appendBin(new PrideHistogramBin(10, 100));
        dataSource.appendBin(new PrideHistogramBin(100, 1000));
        dataSource.appendBin(new PrideHistogramBin(1000, 10000));
        dataSource.appendBin(new PrideHistogramBin(10000, Integer.MAX_VALUE));

        histogramDataSourceMap.put(PrideChartType.PEAK_INTENSITY, dataSource);
    }

    @Override
    protected void reading() throws PrideDataException {
        for (int i = 0; i < 6; i++) {
            peptidesDomain[i] = i + 1.0;
        }

        for (int i = 0; i < 5; i++) {
            missedDomain[i] = i + 0.0;
        }

        for (int i = 0; i < 8; i++) {
            preChargeDomain[i] = i + 1.0;
        }

        int[] peptideBars = new int[6];
        int[] missedBars = new int[5];
        int[] preChargeBars = new int[8];

        List<PrideData> deltaMZList = new ArrayList<PrideData>();
        List<PrideData> preMassedList = new ArrayList<PrideData>();

        Protein protein;
        List<Peptide> peptideList;
        Spectrum spectrum;
        for (Comparable proteinId : controller.getProteinIds()) {
            protein = controller.getProteinById(proteinId);
            peptideList = protein.getPeptides();
            // fill peptides per protein
            int size = peptideList.size();
            if (size < 6) {
                peptideBars[size - 1]++;
            } else {
                peptideBars[5]++;
            }

            int missedCleavages;
            Double deltaMZ;
            for (Peptide peptide : peptideList) {
                // fill delta m/z histogram.
                deltaMZ = calcDeltaMZ(peptide);
                if (deltaMZ != null) {
                    deltaMZList.add(new PrideData(deltaMZ, PrideDataType.ALL_SPECTRA));
                }

                // fill missed cleavages
                missedCleavages = calcMissedCleavages(peptide);
                if (missedCleavages < 4) {
                    missedBars[missedCleavages]++;
                } else {
                    missedBars[4]++;
                }
            }

        }
        readPeptide(peptideBars);
        readDelta(deltaMZList);
        readMissed(missedBars);

        // spectrum level statistics.
        Integer preCharge;
        Double preMZ;
        Peptide peptide;
        List<PrideData> peaksMSList = new ArrayList<PrideData>();
        List<PrideData> peaksIntensityList = new ArrayList<PrideData>();
        List<PrideData> averageMSList = new ArrayList<PrideData>();
        PrideDataType dataType;
        for (Comparable spectrumId : controller.getSpectrumIds()) {
            spectrum = controller.getSpectrumById(spectrumId);
            peptide = spectrum.getPeptide();

            // precursor charge and mass.
            if (peptide == null) {
                preCharge = DataAccessUtilities.getPrecursorCharge(spectrum.getPrecursors());
                preMZ = DataAccessUtilities.getPrecursorMz(spectrum);
            } else {
                preCharge = peptide.getPrecursorCharge();
                preMZ = peptide.getPrecursorMz();
            }

            if (preCharge != null && controller.isIdentifiedSpectrum(spectrumId)) {
                // Identified spectrum.
                preChargeBars[preCharge - 1]++;
            }

            if (preMZ != null && preCharge != null) {
                preMassedList.add(new PrideData(
                        preMZ * preCharge,
                        controller.isIdentifiedSpectrum(spectrumId) ? PrideDataType.IDENTIFIED_SPECTRA : PrideDataType.UNIDENTIFIED_SPECTRA
                ));
            }

            dataType = controller.isIdentifiedSpectrum(spectrumId) ? PrideDataType.IDENTIFIED_SPECTRA : PrideDataType.UNIDENTIFIED_SPECTRA;
            if (controller.getSpectrumMsLevel(spectrumId) == 2) {
                peaksMSList.add(new PrideData(spectrum.getMzBinaryDataArray().getDoubleArray().length + 0.0d, PrideDataType.ALL_SPECTRA)) ;

                averageMSList.add(new PrideData(calcAverageMZ(spectrum), dataType));
            }

            for (double v : spectrum.getIntensityBinaryDataArray().getDoubleArray()) {
                peaksIntensityList.add(new PrideData(v, dataType));
            }

        }

        readPreCharge(preChargeBars);
        readPreMasses(preMassedList);

        readAvg(averageMSList);
        readPeakMS(peaksMSList);
        readPeakIntensity(peaksIntensityList);
    }

    @Override
    protected void end() {
        super.end(source);
    }
}
