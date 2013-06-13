package uk.ac.ebi.pride.chart.plot.axis;

import org.jfree.chart.axis.NumberTickUnit;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
* User: Qingwei
* Date: 13/06/13
*/
public class PrideIntegerTickUnit extends NumberTickUnit {
    private int maxValue = Integer.MAX_VALUE;
    private int minValue = Integer.MIN_VALUE;

    public PrideIntegerTickUnit(NumberTickUnit unit) {
        super(unit.getSize(), NumberFormat.getInstance(), unit.getMinorTickCount());
    }

    @Override
    public String valueToString(double value) {
        String str = super.valueToString(value);

        if (value == 0) {
            return str;
        }

        if (value > maxValue) {
            str = ">" + maxValue;
        } else if (value < minValue) {
            str = "<" + minValue;
        }

        return str;
    }

    public void setMaxValue(int maxValue) {
        if (maxValue <= this.minValue) {
            throw new IllegalArgumentException("new maxValue " + maxValue + " less than or equals minValue " + minValue);
        }

        this.maxValue = maxValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMinValue(int minValue) {
        if (minValue >= this.maxValue) {
            throw new IllegalArgumentException("new minValue " + minValue + " great than or equals maxValue " + maxValue);
        }

        this.minValue = minValue;
    }

    public int getMinValue() {
        return minValue;
    }
}
