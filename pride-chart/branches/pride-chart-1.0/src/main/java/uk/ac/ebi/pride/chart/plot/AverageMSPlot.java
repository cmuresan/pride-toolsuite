package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.PrideDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Qingwei
 * Date: 14/06/13
 */
public class AverageMSPlot extends PrideXYPlot {
    // store all series displayed on the plot.
    private XYSeriesCollection seriesCollection = new XYSeriesCollection();

    // pre-store all spectra series based on input dataset.
    private List<XYSeries> spectraSeriesList = new ArrayList<XYSeries>();

    // used to store the special spectral series.
    private XYSeries spectraSeries;

    public AverageMSPlot(XYSeriesCollection dataset, PrideDataType spectraType) {
        super(PrideChartType.AVERAGE_MS, dataset, new XYBarRenderer());

        setDomainUnitSize(250);
        setRangeUnitSize(50000);

        this.spectraSeriesList.addAll(dataset.getSeries());

        spectraSeries = getSpectraSeries(spectraType);
        if (spectraSeries != null) {
            seriesCollection.addSeries(spectraSeries);
        }

        refresh();
    }

    public Collection<PrideDataType> getSpectraDataTypeList() {
        Collection<PrideDataType> typeList = new ArrayList<PrideDataType>();

        PrideDataType type;
        String seriesKey;
        for (XYSeries series : spectraSeriesList) {
            seriesKey = (String) series.getKey();
            type = PrideDataType.findBy(seriesKey);
            typeList.add(type);
        }

        return typeList;
    }

    private XYSeries getSpectraSeries(PrideDataType spectraType) {
        String seriesKey = spectraType.getTitle();

        for (XYSeries series : spectraSeriesList) {
            if (series.getKey().equals(seriesKey)) {
                return series;
            }
        }
        return null;
    }

    public void updateSpectraSeries(PrideDataType spectraType) {
        if (spectraSeries.getKey().equals(spectraType.getTitle())) {
            return;
        }

        XYSeries series = getSpectraSeries(spectraType);
        if (series == null) {
            // can not find series in internal spectra series list.
            return;
        }

        seriesCollection.removeSeries(spectraSeries);
        spectraSeries = series;
        seriesCollection.addSeries(spectraSeries);
        refresh();
    }

    private void refresh() {
        setDataset(seriesCollection);
        XYBarRenderer renderer = (XYBarRenderer) getRenderer();
        for (int i = 0; i < getSeriesCount(); i++) {
            renderer.setShadowVisible(false);
            renderer.setShadowXOffset(0);
            renderer.setShadowXOffset(0);
        }
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
