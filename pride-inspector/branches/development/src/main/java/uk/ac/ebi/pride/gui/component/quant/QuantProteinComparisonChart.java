package uk.ac.ebi.pride.gui.component.quant;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.Tuple;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.data.core.CvParam;
import uk.ac.ebi.pride.data.core.Quantitation;
import uk.ac.ebi.pride.data.core.QuantitativeSample;
import uk.ac.ebi.pride.data.utils.QuantCvTermReference;
import uk.ac.ebi.pride.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.gui.event.QuantSelectionEvent;
import uk.ac.ebi.pride.gui.event.ReferenceSampleChangeEvent;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Histogram to compare quantitative results between proteins
 * <p/>
 * User: rwang
 * Date: 15/08/2011
 * Time: 11:36
 */
public class QuantProteinComparisonChart extends DataAccessControllerPane implements EventBusSubscribable {
    private static final Logger logger = LoggerFactory.getLogger(QuantProteinComparisonChart.class);

    private QuantCategoryDataset dataset;
    private BarRenderer renderer;
    private boolean colorSet;
    private int referenceSampleIndex = -1;
    private Map<Comparable, java.util.List<Comparable>> idMapping;
    private QuantSelectionSubscriber proteinSelectionSubscriber;
    private ReferenceSampleSubscriber referenceSampleSubscriber;


    public QuantProteinComparisonChart(DataAccessController controller) {
        super(controller);
        this.idMapping = new HashMap<Comparable, java.util.List<Comparable>>();
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    @Override
    protected void addComponents() {
        dataset = new QuantCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart("Protein Comparison",
                "Reagent",
                "Ratio",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        // plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangePannable(true);
        plot.setRangeZeroBaselineVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairValue(1);
        // renderer
        renderer = new BarRenderer();
        plot.setRenderer(renderer);
        renderer.setItemMargin(0);
        renderer.setMaximumBarWidth(20);
        renderer.setShadowVisible(false);
        // label
        renderer.setBaseItemLabelPaint(Color.black);
        QuantCategoryItemLabelGenerator labelGenerator = new QuantCategoryItemLabelGenerator();
        renderer.setBaseItemLabelGenerator(labelGenerator);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("SansSerif", Font.PLAIN, 11));
        ItemLabelPosition p = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER,
                TextAnchor.CENTER, -Math.PI / 2.0);
        renderer.setBasePositiveItemLabelPosition(p);

        ItemLabelPosition p2 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT,
                TextAnchor.CENTER_LEFT, -Math.PI / 2.0);
        renderer.setPositiveItemLabelPositionFallback(p2);
        // tooltips
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{2}", NumberFormat.getInstance()));

        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setBorder(null);
        this.add(chartPanel, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        proteinSelectionSubscriber = new QuantSelectionSubscriber();
        referenceSampleSubscriber = new ReferenceSampleSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(QuantSelectionEvent.class, proteinSelectionSubscriber);
        eventBus.subscribe(ReferenceSampleChangeEvent.class, referenceSampleSubscriber);
    }

    /**
     * Set the colour for different render series, this is specific to jfreechart
     */
    private void setChartBarColour() {
        renderer.setSeriesPaint(0, new Color(166, 206, 227));
        renderer.setSeriesPaint(1, new Color(31, 120, 180));
        renderer.setSeriesPaint(2, new Color(51, 160, 44));
        renderer.setSeriesPaint(3, new Color(255, 127, 0));
        renderer.setSeriesPaint(4, new Color(127, 201, 127));
        renderer.setSeriesPaint(5, new Color(190, 174, 212));
        renderer.setSeriesPaint(6, new Color(253, 192, 134));
        renderer.setSeriesPaint(7, new Color(56, 108, 176));
        renderer.setSeriesPaint(8, new Color(240, 2, 127));
        renderer.setSeriesPaint(9, new Color(191, 91, 23));
    }

    /**
     * Listen to QuantSelectionEvent
     */
    private class QuantSelectionSubscriber implements EventSubscriber<QuantSelectionEvent> {

        @Override
        public void onEvent(QuantSelectionEvent selectionEvent) {
            if (QuantSelectionEvent.Type.PROTIEN.equals(selectionEvent.getType())) {
                Comparable id = selectionEvent.getId();
                if (selectionEvent.isSelected()) {
                    addData(id);
                    if (!colorSet) {
                        setChartBarColour();
                        colorSet = true;
                    }
                } else {
                    removeData(id);
                }
            }
        }
    }

    /**
     * Listen to ReferenceSampleChangeEvent
     */
    private class ReferenceSampleSubscriber implements EventSubscriber<ReferenceSampleChangeEvent> {
        @Override
        public void onEvent(ReferenceSampleChangeEvent referenceSampleChangeEvent) {
            int newReferenceSampleIndex = referenceSampleChangeEvent.getReferenceSampleIndex();
            if (newReferenceSampleIndex != referenceSampleIndex) {
                java.util.List<Comparable> ids = new ArrayList<Comparable>(idMapping.keySet());
                // clear dataset
                dataset.clear();
                // set new reference sample index
                referenceSampleIndex = newReferenceSampleIndex;
                // regenerate new dataset
                for (Comparable id : ids) {
                    addData(id);
                }
            }
        }
    }

    /**
     * Add a new data row into the bar chart
     *
     * @param id protein id
     */
    private void addData(Comparable id) {
        try {
            // get protein accession
            String proteinAcc = controller.getProteinAccession(id);
            // get quantitation data
            Quantitation quantitation = controller.getProteinQuantData(id);
            QuantitativeSample sample = controller.getQuantSample();
            if (referenceSampleIndex < 1) {
                referenceSampleIndex = controller.getReferenceSubSampleIndex();
            }
            // checks whether it contains isotope labelling methods
            if (quantitation.hasIsotopeLabellingMethod()) {
                // get reference reagent
                Double referenceReagentResult = quantitation.getIsotopeLabellingResult(referenceSampleIndex);
                CvParam referenceReagent = sample.getReagent(referenceSampleIndex);
                // get short label for the reagent
                for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
                    if (referenceSampleIndex != i) {
                        CvParam reagent = sample.getReagent(i);
                        if (reagent != null) {
                            Double reagentResult = quantitation.getIsotopeLabellingResult(i);
                            if (referenceReagentResult != null && reagentResult != null) {
                                double value = reagentResult / referenceReagentResult;
                                Comparable column = QuantCvTermReference.getReagentShortLabel(reagent)
                                        + "/" + QuantCvTermReference.getReagentShortLabel(referenceReagent);
                                dataset.addValue(value, proteinAcc, id, column);
                                java.util.List<Comparable> columns = idMapping.get(id);
                                if (columns == null) {
                                    columns = new ArrayList<Comparable>();
                                    idMapping.put(id, columns);
                                }
                                columns.add(column);
                            }
                        }
                    }
                }
            }
        } catch (DataAccessException e) {
            logger.error("Failed to retrieve quantitation data", e);
        }
    }

    /**
     * Remove a data row from the bar chart
     *
     * @param id protein id
     */
    private void removeData(Comparable id) {
        // remove from bar chart
        java.util.List<Comparable> columns = idMapping.get(id);
        for (Comparable column : columns) {
            dataset.removeValue(id, column);
        }
        // remove mapping
        idMapping.remove(id);
    }

    private class QuantCategoryDataset extends DefaultCategoryDataset {

        private Map<Comparable, Comparable> labelMap;

        private QuantCategoryDataset() {
            labelMap = new HashMap<Comparable, Comparable>();
        }

        public void addValue(Number value, Comparable label, Comparable rowKey, Comparable columnKey) {
            labelMap.put(rowKey, label);
            super.addValue(value, rowKey, columnKey);
        }

        @Override
        public void removeValue(Comparable rowKey, Comparable columnKey) {
            super.removeValue(rowKey, columnKey);
            labelMap.remove(rowKey);
        }

        public Comparable getLabel(Comparable rowKey) {
            return labelMap.get(rowKey);
        }
    }

    private class QuantCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator {

        protected QuantCategoryItemLabelGenerator() {
            super("{1}", NumberFormat.getInstance());
        }

        @Override
        protected String generateLabelString(CategoryDataset dataset, int row, int column) {
            if (dataset instanceof QuantCategoryDataset) {
                Comparable rowKey = dataset.getRowKey(row);
                Comparable label = ((QuantCategoryDataset) dataset).getLabel(rowKey);
                return label == null ? null : label.toString();
            } else {
                return super.generateLabelString(dataset, row, column);
            }
        }
    }
}
