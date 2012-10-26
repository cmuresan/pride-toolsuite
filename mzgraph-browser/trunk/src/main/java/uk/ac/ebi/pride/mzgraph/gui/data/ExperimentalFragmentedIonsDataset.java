package uk.ac.ebi.pride.mzgraph.gui.data;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;

import java.util.ArrayList;
import java.util.List;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsDataset extends AbstractXYDataset implements XYDataset, DomainInfo, RangeInfo, ExperimentalTableModelObserver {
    // theoretical data
    private ProductIon[][] xValues;

    // diff data
    private Double[][] yValues;

    /** The minimum x value. */
    private double domainMin = 0;

    /** The maximum x value. */
    private double domainMax = 0;

    /** The minimum y value. */
    private double rangeMin = -0.5;

    /** The maximum y value. */
    private double rangeMax = 0.5;

    private List<FragmentIonType> seriesNameList = new ArrayList<FragmentIonType>();

    private void generateSeriesList(ProductIonPair ionPair) {
        seriesNameList.clear();

        switch (ionPair) {
            case A_X:
                seriesNameList.add(FragmentIonType.X_ION);
                seriesNameList.add(FragmentIonType.A_ION);
                break;
            case B_Y:
                seriesNameList.add(FragmentIonType.Y_ION);
                seriesNameList.add(FragmentIonType.B_ION);
                break;
            case C_Z:
                seriesNameList.add(FragmentIonType.Z_ION);
                seriesNameList.add(FragmentIonType.C_ION);
                break;
        }
    }

    private class Point {
        int series;
        int item;

        Point(int series, int item) {
            this.series = series;
            this.item = item;
        }

        public int getSeries() {
            return series;
        }

        public int getItem() {
            return item;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (item != point.item) return false;
            if (series != point.series) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = series;
            result = 31 * result + item;
            return result;
        }
    }

    /**
     * this matrix use to store the series and item value of each matched data.
     */
    private Point[][] pointMatrix;

    // get series index based on product ion type.
    private int getSeriesIndex(ProductIon ion) {
        FragmentIonType ionGroup = ion.getType().getGroup();
        return seriesNameList.indexOf(ionGroup);
    }

    /**
     * based series index, search xValue array, get max item index which can be add new item.
     */
    private int getMaxItem(int series) {
        int itemIndex = -1;

        ProductIon[] row = xValues[series];
        while (++itemIndex < row.length) {
            if (row[itemIndex] == null) {
                return itemIndex;
            }
        }

        return itemIndex;
    }

    public ExperimentalFragmentedIonsDataset(ExperimentalFragmentedIonsTableModel tableModel) {
        tableModel.addObserver(this);
        update(tableModel);
    }

    @Override
    public int getSeriesCount() {
        return seriesNameList.size();
    }

    @Override
    public Comparable getSeriesKey(int i) {
        return seriesNameList.get(i).getName();
    }

    @Override
    public double getDomainLowerBound(boolean b) {
        return domainMin;
    }

    @Override
    public double getDomainUpperBound(boolean b) {
        return domainMax;
    }

    @Override
    public Range getDomainBounds(boolean b) {
        return new Range(domainMin, domainMax);
    }

    @Override
    public double getRangeLowerBound(boolean b) {
        return rangeMin;
    }

    @Override
    public double getRangeUpperBound(boolean b) {
        return rangeMax;
    }

    @Override
    public Range getRangeBounds(boolean b) {
        return new Range(rangeMin, rangeMax);
    }

    @Override
    public int getItemCount(int series) {
        return xValues[series].length;
    }

    @Override
    public Number getX(int series, int item) {
        ProductIon ion = this.xValues[series][item];

        if (ion == null) {
            return null;
        } else {
            return ion.getMassOverCharge();
        }
    }

    @Override
    public Number getY(int series, int item) {
        return this.yValues[series][item];
    }

    public ProductIon[][] getxValues() {
        return xValues;
    }

    public int getItemIndex(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getItem();
        }
    }

    public int getSeries(int row, int col) {
        Point point = pointMatrix[row][col];
        if (point == null) {
            return -1;
        } else {
            return point.getSeries();
        }
    }

    public int getRowNumber(int series, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == series && point.getItem() == itemIndex) {
                    return row;
                }
            }
        }

        return -1;
    }

    public int getColNumber(int series, int itemIndex) {
        Point point;
        for (int row = 0; row < pointMatrix.length; row++) {
            for (int col = 0; col < pointMatrix[row].length; col++) {
                point = pointMatrix[row][col];
                if (point != null && point.getSeries() == series && point.getItem() == itemIndex) {
                    return col;
                }
            }
        }

        return -1;
    }

    /**
     * update {@link #pointMatrix}, {@link #xValues}, {@link #yValues}, {@link #domainMax}
     */
    @Override
    public void update(ExperimentalFragmentedIonsTableModel tableModel) {
        Object[][] matchedData = tableModel.getMatchedData();
        this.pointMatrix = new Point[tableModel.getRowCount()][tableModel.getColumnCount()];

        ProductIonPair ionPair = tableModel.getIonPair();
        generateSeriesList(ionPair);

        this.xValues = new ProductIon[seriesNameList.size()][tableModel.getRowCount() * tableModel.getColumnCount()];
        this.yValues = new Double[seriesNameList.size()][tableModel.getRowCount() * tableModel.getColumnCount()];

        Object o;
        ProductIon theoreticalIon;
        Double matchedMass;
        Point point;
        int series;
        int item;

        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            if (! tableModel.isMassColumn(col)) {
                continue;
            }

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                o = tableModel.getValueAt(row, col);
                theoreticalIon = (ProductIon) o;
                if (matchedData[row][col] == null) {
                    continue;
                }

                matchedMass = (Double) matchedData[row][col];
                if (theoreticalIon.getMassOverCharge() > domainMax) {
                    domainMax = theoreticalIon.getMassOverCharge();
                }

                series = getSeriesIndex(theoreticalIon);
                item = getMaxItem(series);
                xValues[series][item] = theoreticalIon;
                yValues[series][item] = matchedMass - theoreticalIon.getMassOverCharge();
                point = new Point(series, item);
                pointMatrix[row][col] = point;
            }
        }
    }
}