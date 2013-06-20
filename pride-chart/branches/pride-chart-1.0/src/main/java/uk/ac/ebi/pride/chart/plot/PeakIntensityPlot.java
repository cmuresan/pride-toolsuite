package uk.ac.ebi.pride.chart.plot;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import uk.ac.ebi.pride.chart.PrideChartType;
import uk.ac.ebi.pride.chart.dataset.PrideDataType;
import uk.ac.ebi.pride.chart.plot.label.CategoryPercentageLabel;

import java.util.ArrayList;
import java.util.List;

/**
* User: Qingwei
* Date: 14/06/13
*/
public class PeakIntensityPlot extends PrideCategoryPlot {
    private CategoryDataset dataset;
    private PrideDataType dataType;

    public PeakIntensityPlot(CategoryDataset dataset, PrideDataType dataType) {
        super(PrideChartType.PEAK_INTENSITY, dataset);

        BarRenderer renderer = (BarRenderer) getRenderer();
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            renderer.setSeriesItemLabelGenerator(i, new CategoryPercentageLabel());
            renderer.setSeriesItemLabelsVisible(i, true);
        }

        this.dataType = dataType;
        this.dataset = dataset;

        // only display current data type series.
        for (PrideDataType type : dataType.getChildren()) {
            setVisible(false, type);
        }
        setVisible(true, dataType);
    }

    public List<PrideDataType> getSpectraDataTypeList() {
        List<PrideDataType> typeList = new ArrayList<PrideDataType>();

        typeList.addAll(dataType.getChildren());
        typeList.add(dataType);

        return typeList;
    }

    public void setVisible(boolean visible, PrideDataType dataType) {
        BarRenderer renderer = (BarRenderer) getRenderer();
        for (int i = 0; i < dataset.getRowCount(); i++) {
            if (dataset.getRowKey(i).equals(dataType.getTitle())) {
                renderer.setSeriesVisible(i, visible);
            }
        }
    }
}
