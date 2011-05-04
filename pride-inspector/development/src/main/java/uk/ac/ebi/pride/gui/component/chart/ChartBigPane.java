package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
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
import java.util.ArrayList;
import java.util.List;

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
     * Buttons which could be in the pressed state
     */
    private ArrayList<ChartButton> possiblePressedButtons = new ArrayList<ChartButton>();

    /**
     * Buttons which could be toggled
     */
    public static enum show {
        LEGEND, OPTIONS
    }

    /**
     * Contains a JFreeChart ChartPanel for containing the chart to be shown
     */
    private ChartPanel chartPanel;

    /**
     * Contains the chart extra legend when needed
     */
    private ChartLegend legend = null;

    /**
     * Contains the chart user information
     */
    private JComponent options = null;

    /**
     * Contains the chart spectra information
     */
    private JPanel  spectra = null;

    /**
     * Contins the chart quartiles information
     */
    private JPanel quartiles = null;

    /**
     * Contains the chart and the legend (it the legend exists)
     */
    private JSplitPane splitPane = null;

    /**
     * Defines the dimension for the extra-panels
     */
    private Dimension dimension = new Dimension(50,50);
    /**
     * <p> Creates an instance of this ChartBigPane object, setting all fields as per description below.</p>
     *
     * @param container  The tab containing the chart pane
     * @param managedPrideChart The chart to be contained in this chart pane
     */
    public ChartBigPane(ChartTabPane container, PrideChartManager managedPrideChart) {
        super(container, managedPrideChart);

        setBackground(Color.WHITE);

        setTitle();
        addMain();
        addButtons();
    }

    /**
     * Setting the new title to the big size chart
     */
    private void setTitle() {
        title.setText(managedPrideChart.getPrideChart().getChartTitle());
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 20);
        title.setFont(titleFont);
    }

    private void addMain(){
        JFreeChart chart;
        try {
            chart = managedPrideChart.getPrideChart().getChart();

            chartPanel = new ChartPanel(chart);

            splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartPanel, null);
            splitPane.setResizeWeight(0.8);

            if (managedPrideChart.hasLegend()) {
                legend = new ChartLegend(managedPrideChart);
                if(managedPrideChart.isLegendVisible()) setPanelVisible(legend);
            }

            if(managedPrideChart.hasSpectraOptions()){
                spectra = new ChartOptions(this, managedPrideChart);
            }

            if(managedPrideChart.hasQuartiles()){
                quartiles = new ChartQuartiles(this, managedPrideChart);
            }

            if(spectra!=null && quartiles!=null){
                options = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spectra, quartiles);
                ((JSplitPane) options).setResizeWeight(0.5);
            }else if(spectra!=null){
                options = spectra;
            }else if(quartiles!=null){
                options = quartiles;
            }

            if(options!=null && managedPrideChart.isOptionsVisible())
                setPanelVisible(options);

            setChartProperties(chart);
            add(splitPane, BorderLayout.CENTER);
        } catch (PrideChartException e) {
            List<String> errors = e.getErrorMessages();
            ChartErrorPanel errorPanel = new ChartErrorPanel(errors, ChartErrorPanel.Type.LARGE);
            add(errorPanel, BorderLayout.CENTER);
        }
    }

    public void refreshChart(){
        try {
            PrideChart prideChart = managedPrideChart.getPrideChart();
            prideChart.refreshChart();
            JFreeChart chart = prideChart .getChart();
            setChartProperties(chart);
            chartPanel.setChart(chart);
        } catch (PrideChartException e) {/*Nothing here*/}
        setTitle();
    }

    /**
     * Customize the toolbar to contain all the needed functions
     */
    private void addButtons() {
        // get property manager
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();

        // Previous
        Icon previousIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_previous.icon.large"));
        String previousTooltip = propMgr.getProperty("chart_previous.tooltip");
        ChartButton btnPrevious = new ChartButton(previousIcon, previousTooltip);

        btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                container.showPreviousChart(managedPrideChart);
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
                container.showNextChart(managedPrideChart);
            }
        });
        toolBar.add(btnNext);


        // Auto adjust
        Icon resizeIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_auto_adjust.icon.large"));
        String resizeTooltip = propMgr.getProperty("chart_auto_adjust.tooltip");
        ChartButton btnResize = new ChartButton(resizeIcon, resizeTooltip);
        btnResize.setEnabled(chartPanel!=null);

        btnResize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if( chartPanel!=null ) chartPanel.restoreAutoBounds();
            }
        });
        toolBar.add(btnResize);


        // Info
        Icon infoIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_info.icon.large"));
        String infoTooltip = propMgr.getProperty("chart_info.tooltip");
        ChartButton btnInfo = new ChartButton(infoIcon, infoTooltip);
        btnInfo.setEnabled(chartPanel!=null);
        try {
            int chartID = PrideChartFactory.getPrideChartIdentifier(managedPrideChart.getPrideChart());
            CSH.setHelpIDString(btnInfo, "help.chart." + chartID);
            btnInfo.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        } catch (PrideChartException e) {/*Nothing here*/}
        toolBar.add(btnInfo);


        // Options
        Icon optionIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_options.icon.large"));
        String optionTooltip = propMgr.getProperty("chart_options.tooltip");
        ChartButton btnOptions = new ChartButton(optionIcon, optionTooltip);

        boolean chartHasOptions = (options != null);
        btnOptions.setEnabled(chartHasOptions);
        // If the chart has options, the behaviour of the options button has to be like a standard JToggleButton
        if (chartHasOptions) {
            btnOptions.setSelected(managedPrideChart.isOptionsVisible());
            btnOptions.setKeepSelected(managedPrideChart.isOptionsVisible());
            //Only needed an action listener if the chart has legend
            btnOptions.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setExtraPanel(show.OPTIONS, (ChartButton) ae.getSource());
                }
            });
        }
        toolBar.add(btnOptions);
        possiblePressedButtons.add(btnOptions);
        
        // Extra legend
        Icon legendIcon = GUIUtilities.loadIcon(propMgr.getProperty("chart_legend.icon.large"));
        String legendTooltip = propMgr.getProperty("chart_legend.tooltip");
        ChartButton btnLegend = new ChartButton(legendIcon, legendTooltip);

        boolean chartHasLegend = managedPrideChart.hasLegend();
        btnLegend.setEnabled(chartHasLegend);
        // If the chart has legend, the behaviour of the extra legend button has to be like a standard JToggleButton
        if (chartHasLegend) {
            btnLegend.setSelected(managedPrideChart.isLegendVisible());
            btnLegend.setKeepSelected(managedPrideChart.isLegendVisible());
            //Only needed an action listener if the chart has legend
            btnLegend.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    setExtraPanel(show.LEGEND, (ChartButton) ae.getSource());
                }
            });
        }
        toolBar.add(btnLegend);
        possiblePressedButtons.add(btnLegend);

        toolBar.setBackground(Color.WHITE);
        add(toolBar, BorderLayout.SOUTH);
    }

    public void setExtraPanel(show element, ChartButton button) {
        for (ChartButton btn : possiblePressedButtons) {
            btn.setContentAreaFilled(false);
            btn.setKeepSelected(false);
        }

        switch (element) {
            case OPTIONS:
                managedPrideChart.toggleOptionsVisibility();
                setPanelProperties(options, managedPrideChart.isOptionsVisible(), button);
                break;

            case LEGEND:
                splitPane.setRightComponent(legend);
                setPanelProperties(legend, managedPrideChart.isLegendVisible(), button);
                break;
        }
    }

    private void setPanelProperties(JComponent panel, boolean visible, ChartButton button){
        splitPane.setRightComponent(panel);
        panel.setVisible(visible);
        button.setKeepSelected(visible);
    }

    private void setPanelVisible(JComponent panel){
        panel.setVisible(true);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chartPanel, panel);
        splitPane.setResizeWeight(0.8);
    }
}
