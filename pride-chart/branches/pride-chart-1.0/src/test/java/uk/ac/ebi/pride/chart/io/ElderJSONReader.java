package uk.ac.ebi.pride.chart.io;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.*;
import uk.ac.ebi.pride.chart.utils.PridePlotConstants;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: qingwei
 * Date: 21/06/13
 */
public class ElderJSONReader extends PrideDataReader {
    Logger logger = Logger.getLogger(ElderJSONReader.class);
    private PrideChartType type;

    private String source = "JSON";

    private String ERROR = "ErrorMessages";
    private String SERIES = "Series";
    private String ID = "id";
    private String X_AXIS = "XAxis";
    private String Y_AXIS = "YAxis";

    private JSONObject json = null;

    private void init(JSONObject json, PrideChartType type) throws PrideDataException {
        if (json == null) {
            throw new IllegalArgumentException("JSON Object can not set null!");
        }

        readErrorMsg(json);

        this.json = json;
        this.type = type;
    }

    public ElderJSONReader(String jsonString, PrideChartType type) throws PrideDataException {
        try {
            init(new JSONObject(jsonString), type);
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    public ElderJSONReader(File jsonFile, PrideChartType type) throws PrideDataException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(PridePlotConstants.NEW_LINE);
            }

            init(new JSONObject(sb.toString()), type);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected void start() {
        super.start(source);
    }

    private void readErrorMsg(JSONObject object) throws PrideDataException {
        try {
            StringBuilder sb = new StringBuilder();
            JSONArray msgList = object.getJSONArray(ERROR);
            sb.append(msgList.getString(0));
            for (int i = 1; i < msgList.length(); i++) {
                sb.append("\n").append(msgList.getString(i));
            }

            throw new PrideDataException(sb.toString());
        } catch (JSONException e) {
            // do nothing.
        }
    }

    private Double[] getDomainData(JSONArray array) throws JSONException {
        List<Double> dataArray = new ArrayList<Double>();
        for (int i = 0; i < array.length(); i++) {
            dataArray.add(array.getDouble(i));
        }

        return dataArray.toArray(new Double[dataArray.size()]);
    }

    private PrideData[] getRangeData(JSONArray array, PrideDataType dataType) throws JSONException {
        List<PrideData> dataArray = new ArrayList<PrideData>();
        PrideData data;
        for (int i = 0; i < array.length(); i++) {
            data = new PrideData(array.getDouble(i), dataType);
            dataArray.add(data);
        }

        return dataArray.toArray(new PrideData[dataArray.size()]);
    }

    private void fillData(Double[] srcDomainData, PrideData[] srcRangeData,
                          Double[] targetDomainData, PrideData[] targetRangeData) {
        for (int i = 0; i < targetDomainData.length - 1; i++) {
            for (int j = 0; j < srcDomainData.length; j++) {
                if (srcDomainData[j].equals(targetDomainData[i])) {
                    targetRangeData[i] = srcRangeData[j];
                    break;
                }
            }
        }

        Double lastDomainData = targetDomainData[targetDomainData.length - 1];
        PrideData lastRangeData = targetRangeData[targetRangeData.length - 1];
        for (int i = 0; i < srcDomainData.length; i++) {
            if (srcDomainData[i] >= lastDomainData) {
                lastRangeData.setData(lastRangeData.getData() + srcRangeData[i].getData());
            }
        }
    }

    private void readDelta() throws JSONException {
        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        Double[] domainData = getDomainData(series.getJSONArray(X_AXIS));
        PrideData[] rangeData = getRangeData(series.getJSONArray(Y_AXIS), PrideDataType.ALL_SPECTRA);
        PrideXYDataSource dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
        xyDataSourceMap.put(PrideChartType.DELTA_MASS, dataSource);
    }

    private void readPeptide() throws JSONException {
        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        Double[] domainData = getDomainData(series.getJSONArray(X_AXIS));
        PrideData[] rangeData = getRangeData(series.getJSONArray(Y_AXIS), PrideDataType.ALL);

        // put data into six bars.
        Double[] sixDomainData = new Double[6];
        PrideData[] sixRangeData = new PrideData[6];
        for (int i = 0; i < 6; i++) {
            sixDomainData[i] = i + 1.0;
            sixRangeData[i] = new PrideData(0.0, PrideDataType.ALL);
        }
        fillData(domainData, rangeData, sixDomainData, sixRangeData);

        PrideXYDataSource dataSource = new PrideXYDataSource(sixDomainData, sixRangeData, PrideDataType.ALL);
        xyDataSourceMap.put(PrideChartType.PEPTIDES_PROTEIN, dataSource);
    }

    private void readMissed() throws JSONException {
        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        Double[] domainData = getDomainData(series.getJSONArray(X_AXIS));
        PrideData[] rangeData = getRangeData(series.getJSONArray(Y_AXIS), PrideDataType.ALL_SPECTRA);

        // put data into five bars.
        Double[] fiveDomainData = new Double[5];
        PrideData[] fiveRangeData = new PrideData[5];
        for (int i = 0; i < 5; i++) {
            fiveDomainData[i] = i + 0.0;
            fiveRangeData[i] = new PrideData(0.0, PrideDataType.ALL_SPECTRA);
        }
        fillData(domainData, rangeData, fiveDomainData, fiveRangeData);

        PrideXYDataSource dataSource = new PrideXYDataSource(fiveDomainData, fiveRangeData, PrideDataType.ALL_SPECTRA);
        xyDataSourceMap.put(PrideChartType.MISSED_CLEAVAGES, dataSource);
    }

    private void readAvg() throws JSONException {
        List<Double> domainDataList = new ArrayList<Double>();
        List<PrideData> rangeDataList = new ArrayList<PrideData>();

        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        PrideDataType type = PrideDataType.findBy(series.getString(ID));
        if (series.has(X_AXIS)) {
            domainDataList.addAll(Arrays.asList(getDomainData(series.getJSONArray(X_AXIS))));
            rangeDataList.addAll(Arrays.asList(getRangeData(series.getJSONArray(Y_AXIS), type)));
        }

        series = json.getJSONArray(SERIES).getJSONObject(1);
        type = PrideDataType.findBy(series.getString(ID));
        if (series.has(X_AXIS)) {
            domainDataList.addAll(Arrays.asList(getDomainData(series.getJSONArray(X_AXIS))));
            rangeDataList.addAll(Arrays.asList(getRangeData(series.getJSONArray(Y_AXIS), type)));
        }

        Double[] domainData = domainDataList.toArray(new Double[domainDataList.size()]);
        PrideData[] rangeData = rangeDataList.toArray(new PrideData[rangeDataList.size()]);
        PrideXYDataSource dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
        xyDataSourceMap.put(PrideChartType.AVERAGE_MS, dataSource);
    }

    private void readPreCharge() throws JSONException {
        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        Double[] domainData = getDomainData(series.getJSONArray(X_AXIS));
        PrideData[] rangeData = getRangeData(series.getJSONArray(Y_AXIS), PrideDataType.IDENTIFIED_SPECTRA);

        // put data into 8 bars
        Double[] eightDomainData = new Double[8];
        PrideData[] eightRangeData = new PrideData[8];
        for (int i = 0; i < 8; i++) {
            eightDomainData[i] = i + 1.0;
            eightRangeData[i] = new PrideData(0.0, PrideDataType.IDENTIFIED_SPECTRA);
        }
        fillData(domainData, rangeData, eightDomainData, eightRangeData);

        PrideXYDataSource dataSource = new PrideXYDataSource(eightDomainData, eightRangeData, PrideDataType.IDENTIFIED_SPECTRA);
        xyDataSourceMap.put(PrideChartType.PRECURSOR_CHARGE, dataSource);
    }

    private void readPreMasses() throws JSONException {
        List<Double> domainDataList = new ArrayList<Double>();
        List<PrideData> rangeDataList = new ArrayList<PrideData>();

        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        PrideDataType type = PrideDataType.findBy(series.getString(ID));
        if (series.has(X_AXIS)) {
            domainDataList.addAll(Arrays.asList(getDomainData(series.getJSONArray(X_AXIS))));
            rangeDataList.addAll(Arrays.asList(getRangeData(series.getJSONArray(Y_AXIS), type)));
        }

        series = json.getJSONArray(SERIES).getJSONObject(1);
        type = PrideDataType.findBy(series.getString(ID));
        if (series.has(X_AXIS)) {
            domainDataList.addAll(Arrays.asList(getDomainData(series.getJSONArray(X_AXIS))));
            rangeDataList.addAll(Arrays.asList(getRangeData(series.getJSONArray(Y_AXIS), type)));
        }

        Double[] domainData = domainDataList.toArray(new Double[domainDataList.size()]);
        PrideData[] rangeData = rangeDataList.toArray(new PrideData[rangeDataList.size()]);
        PrideXYDataSource dataSource = new PrideXYDataSource(domainData, rangeData, PrideDataType.ALL_SPECTRA);
        xyDataSourceMap.put(PrideChartType.PRECURSOR_MASSES, dataSource);
    }

    private PrideHistogramBin parseBin(String label) {
        label = label.replaceAll(",", "");
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(label);

        if (matcher.find()) {
            int lowerBound = Integer.parseInt(matcher.group(1));
            int upperBound = Integer.parseInt(matcher.group(2));
            return new PrideHistogramBin(lowerBound, upperBound);
        }

        pattern = Pattern.compile(">(\\d+)");
        matcher = pattern.matcher(label);
        if (matcher.find()) {
            int lowerBound = Integer.parseInt(matcher.group(1));
            int upperBound = Integer.MAX_VALUE;
            return new PrideHistogramBin(lowerBound, upperBound);
        }

        return null;
    }

    private List<PrideHistogramBin> parseBins(JSONArray array) throws JSONException {
        List<PrideHistogramBin> bins = new ArrayList<PrideHistogramBin>();

        String binLabel;
        PrideHistogramBin bin;
        for (int i = 0; i < array.length(); i++) {
            binLabel = array.getString(i);
            bin = parseBin(binLabel);
            if (bin != null) {
                bins.add(bin);
            }
        }

        return bins;
    }

    private List<Integer> parseCounts(JSONArray array) throws JSONException {
        List<Integer> countList = new ArrayList<Integer>();

        for (int i = 0; i < array.length(); i++) {
            countList.add(array.getInt(i));
        }

        return countList;
    }

    private void readPeakMS() throws JSONException {
        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        List<PrideHistogramBin> bins = parseBins(series.getJSONArray(X_AXIS));
        List<Integer> countList = parseCounts(series.getJSONArray(Y_AXIS));

        List<PrideData> values = new ArrayList<PrideData>();
        int count;
        for (int i = 0; i < countList.size(); i++) {
            count = countList.get(i);
            for (int j = 0; j < count; j++) {
                values.add(new PrideData(bins.get(i).getStartBoundary() + 0.0d, PrideDataType.ALL_SPECTRA));
            }
        }

        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(values.toArray(new PrideData[values.size()]), PrideDataType.ALL_SPECTRA);
        for (PrideHistogramBin bin : bins) {
            dataSource.appendBin(bin);
        }
        histogramDataSourceMap.put(PrideChartType.PEAKS_MS, dataSource);
    }

    private void addSeries(List<PrideData> values, List<PrideHistogramBin> bins, List<Integer> countList, PrideDataType type) throws JSONException {
        int count;
        for (int i = 0; i < countList.size(); i++) {
            count = countList.get(i);
            for (int j = 0; j < count; j++) {
                values.add(new PrideData(bins.get(i).getStartBoundary() + 0.0d, type));
            }
        }
    }

    private void readPeakIntensity() throws JSONException {
        List<PrideData> values = new ArrayList<PrideData>();

        JSONObject series = json.getJSONArray(SERIES).getJSONObject(0);
        PrideDataType type = PrideDataType.findBy(series.getString(ID));
        List<PrideHistogramBin> bins = parseBins(series.getJSONArray(X_AXIS));
        List<Integer> countList = parseCounts(series.getJSONArray(Y_AXIS));
        addSeries(values, bins, countList, type);

        series = json.getJSONArray(SERIES).getJSONObject(1);
        type = PrideDataType.findBy(series.getString(ID));
        bins = parseBins(series.getJSONArray(X_AXIS));
        countList = parseCounts(series.getJSONArray(Y_AXIS));
        addSeries(values, bins, countList, type);

        PrideHistogramDataSource dataSource = new PrideHistogramDataSource(values.toArray(new PrideData[values.size()]), PrideDataType.ALL_SPECTRA);
        for (PrideHistogramBin bin : bins) {
            dataSource.appendBin(bin);
        }

        histogramDataSourceMap.put(PrideChartType.PEAK_INTENSITY, dataSource);
    }

    @Override
    protected void reading() throws PrideDataException {
        try {
            switch (type) {
                case DELTA_MASS:
                    readDelta();
                    break;
                case PEPTIDES_PROTEIN:
                    readPeptide();
                    break;
                case MISSED_CLEAVAGES:
                    readMissed();
                    break;
                case AVERAGE_MS:
                    readAvg();
                    break;
                case PRECURSOR_CHARGE:
                    readPreCharge();
                    break;
                case PRECURSOR_MASSES:
                    readPreMasses();
                    break;
                case PEAKS_MS:
                    readPeakMS();
                    break;
                case PEAK_INTENSITY:
                    readPeakIntensity();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void end() {
        super.end(source);
    }
}
