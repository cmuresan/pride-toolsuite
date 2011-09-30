package uk.ac.ebi.pride.chart.graphics.implementation.labelers;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

import java.text.DecimalFormat;

/**
 * <p>A label generator for the bar chats.</p>
 *
 * @author Antonio Fabregat
 * Date: 12-oct-2010
 * Time: 11:16:27
 */
public class NumberLegendGenerator extends StandardCategoryItemLabelGenerator {

    private DecimalFormat numberFormat = null;

    public NumberLegendGenerator() {}

    public NumberLegendGenerator(DecimalFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String generateLabel(CategoryDataset dataset, int row, int column) {
        Number value = dataset.getValue(row, column);

        if (value.doubleValue() == 0.0) {
            return "";
        } else if (numberFormat == null) {
            return "" + value.intValue();
        } else {
            return numberFormat.format(value);
        }
    }
}
