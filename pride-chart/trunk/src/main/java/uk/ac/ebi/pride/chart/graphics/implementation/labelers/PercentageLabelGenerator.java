package uk.ac.ebi.pride.chart.graphics.implementation.labelers;

import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;

import java.text.DecimalFormat;

/**
 * <p>A label generator for the bar chats.</p>
 *
 * @author Antonio Fabregat
 * Date: 21-sep-2010
 * Time: 14:09:22
 */
public class PercentageLabelGenerator extends StandardCategoryItemLabelGenerator {

    private DecimalFormat numberFormat = null;

    public PercentageLabelGenerator() {
        super();
    }

    public PercentageLabelGenerator(DecimalFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String generateLabel(CategoryDataset dataset, int row, int column) {        
        long sum = 0;
        for(int col=0; col<dataset.getColumnCount(); col++){
            sum += dataset.getValue(row, col).longValue();
        }

        int value = dataset.getValue(row, column).intValue();
        double p = (value * 100.0) / (double) sum;

        if (p == 0) {
            return "";
        }else if(p<1){
            return "<1 %";
        } else if (numberFormat == null) {
            int out = (int) Math.round(p);
            return out + " %";
        } else {
            return numberFormat.format(p) + " %";
        }
    }
}