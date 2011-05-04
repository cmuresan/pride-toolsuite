package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Class to show a big size chart with a toolbar for browsing and manage the charts contained in PRIDE Inspector.</p>
 *
 * @author Antonio Fabregat
 * Date: 23-ago-2010
 * Time: 14:36:02
 */
public class ChartBigPane extends ChartPane {
    /**
     * Contains an specialized toolBar customized for the big size chart
     */
    private JPanel toolBar = new JPanel();

    /**
     * Contains a JFreeChart ChartPanel for containing the chart to be shown
     */
    private ChartPanel cp;

    /**
     * <p> Creates an instance of this ChartBigPane object, setting all fields as per description below.</p>
     *
     * @param container The tab containing the chart pane
     * @param prideChart The chart to be contained in this chart pane
     */
    public ChartBigPane(ChartTabPane container, PrideChart prideChart) {
        super(container, prideChart);

        setBackground(Color.WHITE);

        //Reset the chart Title (removed in the thumbnail)
        JFreeChart chart = prideChart.getChart();
        chart.setTitle(prideChart.getChartTitle());

        cp = new ChartPanel(chart);
        add(cp, BorderLayout.CENTER);

        toolBar.setBackground(Color.WHITE);
        add(toolBar, BorderLayout.SOUTH);

        addButtons();
    }

    /**
     * Customize the toolbar to contain all the needed functions
     */
    private void addButtons() {
        // get property manager
        DesktopContext context = uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        // Previous
        Icon previousIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_previous.icon.large"));
        String previousTooltip = propMgr.getProperty("chart_previous.tooltip");
        ChartButton btnPrevious = new ChartButton(previousIcon, previousTooltip);

        btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showPreviousChart(prideChart);
            }
        });
        toolBar.add(btnPrevious);

        // Show all charts
        Icon allChartIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_all_charts.icon.large"));
        String allChartTooltip = propMgr.getProperty("chart_all_charts.tooltip");
        ChartButton btnAllChart = new ChartButton(allChartIcon, allChartTooltip);

        btnAllChart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.setThumbnailView();
            }
        });
        toolBar.add(btnAllChart);

        // Next
        Icon nextIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_next.icon.large"));
        String nextTooltip = propMgr.getProperty("chart_next.tooltip");
        ChartButton btnNext = new ChartButton(nextIcon, nextTooltip);

        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showNextChart(prideChart);
            }
        });
        toolBar.add(btnNext);

        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.large"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        ChartButton btnInfo = new ChartButton(infoIcon, infoTooltip);

        btnInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showChartInformation();
            }
        });
        toolBar.add(btnInfo);

        // Auto adjust
        Icon resizeIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_auto_adjust.icon.large"));
        String resizeTooltip = propMgr.getProperty("chart_auto_adjust.tooltip");
        ChartButton btnResize = new ChartButton(resizeIcon, resizeTooltip);

        btnResize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cp.restoreAutoBounds();
            }
        });
        toolBar.add(btnResize);
    }
}
