package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartException;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChartFactory;
import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;

import javax.help.CSH;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * <p>Class to show a thumbnail of the chart with a toolbar for 'playing' with the chart.</p>
 *
 * @author Antonio Fabregat
 *         Date: 20-ago-2010
 *         Time: 14:03:15
 */
public class ChartThumbnailPane extends ChartPane {
    /**
     * Contains an specialized toolBar customized for the big size chart
     */
    private JPanel toolBar = new JPanel();

    /**
     * Contains a JFreeChart ChartPanel for containing the chart to be shown
     */
    private ChartPanel cp = null;

    /**
     * <p> Creates an instance of this ChartThumbnailPane object, setting all fields as per description below.</p>
     *
     * @param container  The tab containing the chart pane
     * @param managedPrideChart The chart to be contained in this chart pane
     */
    public ChartThumbnailPane(ChartTabPane container, PrideChartManager managedPrideChart) {
        super(container, managedPrideChart);

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.gray));

        addTitle();
        addMain();
        addButtons();
    }

    /**
     * Setting the new title to the thumbnail
     */
    private void addTitle() {
        JLabel title = new JLabel(managedPrideChart.getPrideChart().getChartShortTitle());
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 17);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        add(titleBar, BorderLayout.NORTH);
    }

    private void addMain(){

        try {
            JFreeChart chart = managedPrideChart.getPrideChart().getChart();
            setChartProperties(chart);

            cp = new ChartPanel(chart);
            cp.addChartMouseListener(new PrideChartMouseEvent(container, managedPrideChart));
            add(cp, BorderLayout.CENTER);
        } catch (PrideChartException e) {
            List<String> errors = e.getErrorMessages();
            ChartErrorPanel errorPanel = new ChartErrorPanel(errors, ChartErrorPanel.Type.SMALL);
            add(errorPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Customize the toolbar to contain all the needed functions
     */
    private void addButtons() {
        // get property manager
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.small"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        ChartButton btnInfo = new ChartButton(infoIcon, infoTooltip);
        try {
            int chartID = PrideChartFactory.getPrideChartIdentifier(managedPrideChart.getPrideChart());
            CSH.setHelpIDString(btnInfo, "help.chart." + chartID);
            btnInfo.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        } catch (PrideChartException e) {/*Nothing here*/}
        toolBar.add(btnInfo);

        // Zoom in
        Icon zoomIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_zoom_in.icon.small"));
        String zoomTooltip = propMgr.getProperty("chart_zoom_in.tooltip");
        ChartButton btnZoomIn = new ChartButton(zoomIcon, zoomTooltip);

        btnZoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showChart(managedPrideChart);
            }
        });
        toolBar.add(btnZoomIn);

        // Auto adjust
        Icon adjustIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_auto_adjust.icon.small"));
        String adjustTooltip = propMgr.getProperty("chart_auto_adjust.tooltip");
        ChartButton btnAdjust = new ChartButton(adjustIcon, adjustTooltip);
        btnAdjust.setEnabled(cp != null);

        btnAdjust.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (cp != null) cp.restoreAutoBounds();
            }
        });
        toolBar.add(btnAdjust);

        toolBar.setBackground(Color.WHITE);
        add(toolBar, BorderLayout.SOUTH);
    }
}
