package uk.ac.ebi.pride.chart.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.chart.utils.PridePlotConstants;

/**
 * User: Qingwei
 * Date: 12/06/13
 */
public abstract class PrideDataReader {
    protected double[][] peptidesProtein;
    protected double[][] deltaMZ;
    protected double[][] missedCleavages;
    protected double[][] averageMSMS;
    protected double[][] precursorIonCharge;
    protected double[][] precursorIonMasses;
    protected double[][] peaksPerMSMS;
    protected double[][] peakIntensity;

    private long startTime;

    private Logger logger = LoggerFactory.getLogger(PrideDataReader.class);

    public void readData() {
        start();
        reading();
        end();
    }

    protected abstract void start();

    protected void start(String source) {
        logger.debug("Start load data by calling " + source + "." + PridePlotConstants.NEW_LINE);
        startTime = System.currentTimeMillis();
    }

    protected abstract void reading();

    protected abstract void end();

    protected void end(String source) {
        logger.debug("End load data from " + source + "." + PridePlotConstants.NEW_LINE);
        long endTime = System.currentTimeMillis();
        double costTime = (endTime - startTime) / 1000d / 60;
        logger.debug("Cost time: " + String.format("%1$.2f", costTime) + "(min)" + PridePlotConstants.NEW_LINE);
    }

    public double[][] readPeptidesProtein() {
        return peptidesProtein;
    }

    public double[][] readDeltaMZ() {
        return deltaMZ;
    }

    public double[][] readMissedCleavages() {
        return missedCleavages;
    }

    public double[][] readAverageMSMS() {
        return averageMSMS;
    }

    public double[][] readPrecursorIonCharge() {
        return precursorIonCharge;
    }

    public double[][] readPrecursorIonMasses() {
        return precursorIonMasses;
    }

    public double[][] readPeaksPerMSMS() {
        return peaksPerMSMS;
    }

    public double[][] readPeakIntensity() {
        return peakIntensity;
    }
}
