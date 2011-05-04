package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.utils.GUIUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <p>Class to show a thumbnail of the chart with a toolbar for 'playing' with the chart.</p>
 *
 * @author Antonio Fabregat
 * Date: 20-ago-2010
 * Time: 14:03:15
 */
public class ChartThumbnailPane extends ChartPane {
    /**
     * Contains an specialized toolBar customized for the big size chart
     */
    private JPanel toolBar = new JPanel();

    /**
     * Contains a JFreeChart ChartPanel for containing the chart to be shown
     */
    private ChartPanel cp;

    /**
     * <p> Creates an instance of this ChartThumbnailPane object, setting all fields as per description below.</p>
     *
     * @param container The tab containing the chart pane
     * @param prideChart The chart to be contained in this chart pane
     */
    public ChartThumbnailPane(ChartTabPane container, PrideChart prideChart) {
        super(container, prideChart);

        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.gray));

        //Removing title and subtitle from the JFreeCharts
        prideChart.getChart().setTitle("");
        for(Object subtitle : prideChart.getChart().getSubtitles()){
            //The legend is returned as a subtitle, but it has not to be deleted
            if(!(subtitle instanceof LegendTitle)){
                prideChart.getChart().removeSubtitle((Title) subtitle);
            }
        }

        //Setting the new title to the thumbnail
        String fontName = prideChart.getChart().getTitle().getFont().getFontName();
        Font titleFont = new Font(fontName,Font.BOLD,15);
        JLabel title = new JLabel(prideChart.getChartTitle());
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        add(titleBar, BorderLayout.NORTH);

        cp = new ChartPanel(prideChart.getChart());
        cp.addChartMouseListener(new PrideChartMouseEvent(container, prideChart));
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

        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.small"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        ChartButton btnInfo = new ChartButton(infoIcon, infoTooltip);

        btnInfo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                showChartInformation();
            }
        });
        toolBar.add(btnInfo);

        // Zoom in
        Icon zoomIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_zoom_in.icon.small"));
        String zoomTooltip = propMgr.getProperty("chart_zoom_in.tooltip");
        ChartButton btnZoomIn = new ChartButton(zoomIcon, zoomTooltip);

        btnZoomIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showChart(prideChart);
            }
        });
        toolBar.add(btnZoomIn);

        // Auto adjust
        Icon adjustIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_auto_adjust.icon.small"));
        String adjustTooltip = propMgr.getProperty("chart_auto_adjust.tooltip");
        ChartButton btnAdjust = new ChartButton(adjustIcon, adjustTooltip);

        btnAdjust.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                cp.restoreAutoBounds();
            }
        });
        toolBar.add(btnAdjust);
    }
}
