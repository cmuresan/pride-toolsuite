package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataset;
import uk.ac.ebi.pride.chart.PrideChartType;

import java.text.DecimalFormat;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class AverageMSPlot extends PrideXYPlot {
    public AverageMSPlot(XYBarDataset dataset) {
        super(PrideChartType.AVERAGE_MS, dataset, new XYBarRenderer());

        setDomainUnitSize(250);
        setRangeUnitSize(50000);
    }

    public void setDomainUnitSize(double domainUnitSize) {
        NumberAxis domainAxis = (NumberAxis) getDomainAxis();
        domainAxis.setTickUnit(new NumberTickUnit(domainUnitSize, new DecimalFormat("###,###")));
    }

    public void setRangeUnitSize(double rangeUnitSize) {
        NumberAxis rangeAxis = (NumberAxis) getRangeAxis();
        rangeAxis.setTickUnit(new NumberTickUnit(rangeUnitSize, new DecimalFormat("#,###,###")));
    }
}
