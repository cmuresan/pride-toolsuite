package uk.ac.ebi.pride.chart.io;

import org.junit.Test;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.*;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

/**
 * User: qingwei
 * Date: 25/06/13
 */
public class DataAccessReaderTest {
    private File prideXMLFile = new File("testset/PRIDE_Exp_Complete_Ac_2.xml");
    private File jsonFile = new File("testset/old_2.json");

//    private File prideXMLFile = new File("testset/PRIDE_Exp_Complete_Ac_1643.xml");
//    private File jsonFile = new File("testset/old_1643.json");

    private DataAccessReader dataReader;
    private ElderJSONReader jsonReader;

    public DataAccessReaderTest() throws Exception {
        DataAccessController controller = new PrideXmlControllerImpl(prideXMLFile);
        dataReader = new DataAccessReader(controller);
        jsonReader = new ElderJSONReader(jsonFile);
    }

    @Test
    public void testPeptides() throws Exception {
        PrideChartType type = PrideChartType.PEPTIDES_PROTEIN;

        PrideXYDataSource prideDataSource = dataReader.getXYDataSourceMap().get(type);
        PrideXYDataSource jsonDataSource = jsonReader.getXYDataSourceMap().get(type);

        assertArrayEquals(prideDataSource.getDomainData(), jsonDataSource.getDomainData());
        assertArrayEquals(prideDataSource.getRangeData(), jsonDataSource.getRangeData());
    }

    @Test
    public void testDelta() throws Exception {
        PrideChartType type = PrideChartType.DELTA_MASS;

        PrideXYDataSource prideDataSource = dataReader.getXYDataSourceMap().get(type);
        PrideXYDataSource jsonDataSource = jsonReader.getXYDataSourceMap().get(type);

        double threshold = 0.1;
        for (int i = 0; i < prideDataSource.getDomainData().length; i++) {
            assertTrue(Math.abs(prideDataSource.getDomainData()[i] - jsonDataSource.getDomainData()[i]) < threshold);
        }
    }

    @Test
    public void testMissedCleavages() throws Exception {
        PrideChartType type = PrideChartType.MISSED_CLEAVAGES;

        PrideXYDataSource prideDataSource = dataReader.getXYDataSourceMap().get(type);
        PrideXYDataSource jsonDataSource = jsonReader.getXYDataSourceMap().get(type);

        assertArrayEquals(prideDataSource.getDomainData(), jsonDataSource.getDomainData());
        double sum = 0;
        for (PrideData data : jsonDataSource.getRangeData()) {
            sum += data.getData();
        }

        // tolerance is <1%
        for (int i = 0; i < prideDataSource.getRangeData().length; i++) {
            assertTrue(Math.abs(prideDataSource.getRangeData()[i].getData() - jsonDataSource.getRangeData()[i].getData()) < sum * 0.01);
        }
    }

    @Test
    public void testPrecursorCharge() throws Exception {
        PrideChartType type = PrideChartType.PRECURSOR_CHARGE;

        PrideXYDataSource prideDataSource = dataReader.getXYDataSourceMap().get(type);
        PrideXYDataSource jsonDataSource = jsonReader.getXYDataSourceMap().get(type);

        assertArrayEquals(prideDataSource.getDomainData(), jsonDataSource.getDomainData());
        assertArrayEquals(prideDataSource.getRangeData(), jsonDataSource.getRangeData());
    }

    @Test
    public void testPrecursorMasses() throws Exception {
        PrideChartType type = PrideChartType.PRECURSOR_MASSES;

        PrideXYDataSource prideDataSource = dataReader.getXYDataSourceMap().get(type);
        PrideXYDataSource jsonDataSource = jsonReader.getXYDataSourceMap().get(type);

        PrideXYDataSource idPrideDataSource = prideDataSource.filter(PrideDataType.IDENTIFIED_SPECTRA);
        PrideXYDataSource unPrideDataSource = prideDataSource.filter(PrideDataType.UNIDENTIFIED_SPECTRA);
        PrideXYDataSource allPrideDataSource = prideDataSource.filter(PrideDataType.ALL_SPECTRA);

        PrideXYDataSource idJSONDatasource = jsonDataSource.filter(PrideDataType.IDENTIFIED_SPECTRA);
        PrideXYDataSource unJSONDatasource = jsonDataSource.filter(PrideDataType.UNIDENTIFIED_SPECTRA);
        PrideXYDataSource allJSONDatasource = jsonDataSource.filter(PrideDataType.ALL_SPECTRA);

        int min = Math.min(idPrideDataSource.getDomainData().length, idJSONDatasource.getDomainData().length);
        for (int i = 0; i < min; i++) {
            assertEquals(idPrideDataSource.getDomainData()[i], idJSONDatasource.getDomainData()[i]);
            assertTrue(Math.abs(idPrideDataSource.getRangeData()[i].getData() - idJSONDatasource.getRangeData()[i].getData()) < 0.01);
        }

        min = Math.min(unPrideDataSource.getDomainData().length, unJSONDatasource.getDomainData().length);
        for (int i = 0; i < min; i++) {
            assertEquals(unPrideDataSource.getDomainData()[i], unJSONDatasource.getDomainData()[i]);
            assertTrue(Math.abs(unPrideDataSource.getRangeData()[i].getData() - unJSONDatasource.getRangeData()[i].getData()) < 0.01);
        }

        min = Math.min(allPrideDataSource.getDomainData().length, allJSONDatasource.getDomainData().length);
        for (int i = 0; i < min; i++) {
            assertEquals(allPrideDataSource.getDomainData()[i], allJSONDatasource.getDomainData()[i]);
            assertTrue(Math.abs(allPrideDataSource.getRangeData()[i].getData() - allJSONDatasource.getRangeData()[i].getData()) < 0.01);
        }
    }

    private void compareHistogram(SortedMap<PrideHistogramBin, Collection<PrideData>> prideHistogram,
                                  SortedMap<PrideHistogramBin, Collection<PrideData>> jsonHistogram) {
        assertArrayEquals(prideHistogram.keySet().toArray(), jsonHistogram.keySet().toArray());

        List<Integer> prideFrequencyList = new ArrayList<Integer>();
        for (Collection<PrideData> cell : prideHistogram.values()) {
            prideFrequencyList.add(cell.size());
        }
        List<Integer> jsonFrequencyList = new ArrayList<Integer>();
        for (Collection<PrideData> cell : jsonHistogram.values()) {
            jsonFrequencyList.add(cell.size());
        }

        assertArrayEquals(prideFrequencyList.toArray(), jsonFrequencyList.toArray());
    }

    @Test
    public void testPeaksMS() throws Exception {
        PrideChartType type = PrideChartType.PEAKS_MS;

        PrideHistogramDataSource prideDataSource = dataReader.getHistogramDataSourceMap().get(type);
        PrideHistogramDataSource jsonDataSource = jsonReader.getHistogramDataSourceMap().get(type);

        SortedMap<PrideHistogramBin, Collection<PrideData>> prideHistogram = prideDataSource.getHistogram();
        SortedMap<PrideHistogramBin, Collection<PrideData>> jsonHistogram = jsonDataSource.getHistogram();

        compareHistogram(prideHistogram, jsonHistogram);
    }

    @Test
    public void testPeaksIntensity() throws Exception {
        PrideChartType type = PrideChartType.PEAK_INTENSITY;

        PrideHistogramDataSource prideDataSource = dataReader.getHistogramDataSourceMap().get(type);
        PrideHistogramDataSource jsonDataSource = jsonReader.getHistogramDataSourceMap().get(type);

        SortedMap<PrideHistogramBin, Collection<PrideData>> idPrideHistogram = prideDataSource.filter(PrideDataType.IDENTIFIED_SPECTRA).getHistogram();
        SortedMap<PrideHistogramBin, Collection<PrideData>> unPrideHistogram = prideDataSource.filter(PrideDataType.UNIDENTIFIED_SPECTRA).getHistogram();
        SortedMap<PrideHistogramBin, Collection<PrideData>> allPrideHistogram = prideDataSource.filter(PrideDataType.ALL_SPECTRA).getHistogram();

        SortedMap<PrideHistogramBin, Collection<PrideData>> idJSONHistogram = jsonDataSource.filter(PrideDataType.IDENTIFIED_SPECTRA).getHistogram();
        SortedMap<PrideHistogramBin, Collection<PrideData>> unJSONHistogram = jsonDataSource.filter(PrideDataType.UNIDENTIFIED_SPECTRA).getHistogram();
        SortedMap<PrideHistogramBin, Collection<PrideData>> allJSONHistogram = jsonDataSource.filter(PrideDataType.ALL_SPECTRA).getHistogram();

        compareHistogram(idPrideHistogram, idJSONHistogram);
        compareHistogram(unPrideHistogram, unJSONHistogram);
        compareHistogram(allPrideHistogram, allJSONHistogram);
    }
}
