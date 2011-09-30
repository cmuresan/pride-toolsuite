package uk.ac.ebi.pride.chart.utils;

import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import uk.ac.ebi.pride.chart.graphics.implementation.data.DataSeries;
import uk.ac.ebi.pride.chart.graphics.implementation.data.SeriesPair;

import java.util.List;

/**
 * A single series score dataset.
 */
public class ScoreIntervalXYDataset extends AbstractIntervalXYDataset
                                     implements IntervalXYDataset {
    /** Defines the percentage of space between bars in function 
        of the width defined in the constructor (between 0 and 1) */
    private static final double SPACE_BETWEEN_BARS = 0.25;

    /** The start values. */
    private Double[] xStart;

    /** The end values. */
    private Double[] xEnd;

    /** The y values. */
    private Integer[] yValues;

    /**
     * Creates a new dataset.
     */
    public ScoreIntervalXYDataset(int bins, double width, List<DataSeries<Double,Integer>> seriesList) {
        xStart = new Double[bins+1];
        xEnd = new Double[bins+1];
        yValues = new Integer[bins+1];

        for (DataSeries<Double,Integer> series : seriesList) {
            int i = 0;
            for (SeriesPair<Double, Integer> values : series.getSeriesValues(Double.class, Integer.class)) {
                xStart[i] = values.getX() - width * SPACE_BETWEEN_BARS;
                xEnd[i] = values.getX() + width * SPACE_BETWEEN_BARS;
                yValues[i] = values.getY();
                i++;
            }
        }
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the key for a series.
     *
     * @param series the series (zero-based index).
     *
     * @return The series key.
     */
    public Comparable getSeriesKey(int series) {
        return "Series 1";
    }

    /**
     * Returns the number of items in a series.
     *
     * @param series the series (zero-based index).
     *
     * @return the number of items within a series.
     */
    public int getItemCount(int series) {
        return yValues.length;
    }

    /**
     * Returns the x-value for an item within a series.
     * <P>
     * The implementation is responsible for ensuring that the x-values are presented in ascending
     * order.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return  the x-value for an item within a series.
     */
    public Number getX(int series, int item) {
        return this.xStart[item];
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the y-value for an item within a series.
     */
    public Number getY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getStartX(int series, int item) {
        return this.xStart[item];
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return the end x value.
     */
    public Number getEndX(int series, int item) {
        return this.xEnd[item];
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getStartY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item within a series (zero-based index).
     *
     * @return The value.
     */
    public Number getEndY(int series, int item) {
        return this.yValues[item];
    }

    /**
     * Registers an object for notification of changes to the dataset.
     *
     * @param listener  the object to register.
     */
    public void addChangeListener(DatasetChangeListener listener) {
        // ignored
    }

    /**
     * Deregisters an object for notification of changes to the dataset.
     *
     * @param listener  the object to deregister.
     */
    public void removeChangeListener(DatasetChangeListener listener) {
        // ignored
    }

}