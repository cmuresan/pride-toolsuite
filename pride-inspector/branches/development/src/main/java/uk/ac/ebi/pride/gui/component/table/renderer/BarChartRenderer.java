package uk.ac.ebi.pride.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer to draw bar chart
 * <p/>
 * User: rwang
 * Date: 21/08/2011
 * Time: 10:03
 */
public class BarChartRenderer implements TableCellRenderer {
    public static enum ORIENTATION {HORIZONTAL, VERTICAL}

    private static final double DEFAULT_REFERENCE_VALUE = 0;
    private static final double DEFAULT_MAXIMUM_VALUE = 10;
    private static final double DEFAULT_MINIMUM_VALUE = 0;
    private static final Color DEFAULT_POSITIVE_VALUE_COLOUR = Color.green;
    private static final Color DEFAULT_NEGATIVE_VALUE_COLOUR = Color.RED;
    private static final ORIENTATION DEFAULT_ORIENTATION = ORIENTATION.HORIZONTAL;
    private static final boolean DEFAULT_NUMBER_AND_CHART = true;

    private double maximumValue;
    private double referenceValue;
    private double minimumValue;
    private Paint positiveValuePaint;
    private Paint negativeValuePaint;
    private ORIENTATION orientation;
    private boolean numberAndChart;

    public BarChartRenderer() {
        this(DEFAULT_MAXIMUM_VALUE, DEFAULT_MINIMUM_VALUE, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(double maxValue) {
        this(maxValue, DEFAULT_MINIMUM_VALUE, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(double maxValue, double minValue) {
        this(maxValue, minValue, DEFAULT_REFERENCE_VALUE);
    }

    public BarChartRenderer(double maxValue, double minValue, double refValue) {
        this.maximumValue = maxValue;
        this.minimumValue = minValue;
        this.referenceValue = refValue;
        this.orientation = DEFAULT_ORIENTATION;
        this.positiveValuePaint = DEFAULT_POSITIVE_VALUE_COLOUR;
        this.negativeValuePaint = DEFAULT_NEGATIVE_VALUE_COLOUR;
        this.numberAndChart = DEFAULT_NUMBER_AND_CHART;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
//        JLabel label;
//
//        // only none null number value is painted
//        if (value != null && value instanceof Number) {
//            label = new BarChartLabel();
//        } else if (value == null) {
//            label = new JLabel();
//        } else {
//            label = new JLabel(value.toString());
//        }
//
//        return label;
        return null;
    }

    public double getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public double getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(double referenceValue) {
        this.referenceValue = referenceValue;
    }

    public double getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(double minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Paint getPositiveValuePaint() {
        return positiveValuePaint;
    }

    public void setPositiveValuePaint(Paint positiveValuePaint) {
        this.positiveValuePaint = positiveValuePaint;
    }

    public Paint getNegativeValuePaint() {
        return negativeValuePaint;
    }

    public void setNegativeValuePaint(Paint negativeValuePaint) {
        this.negativeValuePaint = negativeValuePaint;
    }

    public ORIENTATION getOrientation() {
        return orientation;
    }

    public void setOrientation(ORIENTATION orientation) {
        this.orientation = orientation;
    }

    public boolean isNumberAndChart() {
        return numberAndChart;
    }

    public void setNumberAndChart(boolean numberAndChart) {
        this.numberAndChart = numberAndChart;
    }

    private class BarChartLabel extends JLabel {
        private JTable table;
        private Number value;
        private int row;
        private int column;

        private BarChartLabel(JTable table, Number value,
                              int row, int column) {
            this.table = table;
            this.value = value;
            this.row = row;
            this.column = column;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            // get size
            int width = this.getWidth();
            int height = this.getHeight();
            int xPos = 0;
            int yPos = 10;

            //


            g2.dispose();

        }
    }
}
