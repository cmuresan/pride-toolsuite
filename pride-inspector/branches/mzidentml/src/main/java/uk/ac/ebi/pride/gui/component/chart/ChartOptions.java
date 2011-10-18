package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.ui.tabbedui.VerticalLayout;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.DataSeriesType;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartSpectraOptions;
import uk.ac.ebi.pride.data.controller.impl.PrideChartManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * <p></p>
 *
 * User: Antonio Fabregat
 * Date: 23-nov-2010
 * Time: 13:46:39
 */
public class ChartOptions extends JPanel {
    public  ChartOptions(ChartBigPane chartBigPane, PrideChartManager managedPrideChart) {
        super(new BorderLayout());

        //Options title
        JLabel title = new JLabel("Chart Options");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        add(titleBar, BorderLayout.NORTH);

        //Options
        PrideChart prideChart = managedPrideChart.getPrideChart();
        JPanel optionPanel = new JPanel(new VerticalLayout());
        PrideChartSpectraOptions pco = (PrideChartSpectraOptions) prideChart;
        boolean isMultipleChoice = pco.isSpectraMultipleChoice();
        ButtonGroup group = new ButtonGroup();
        Set<JToggleButton> selectedButtons = new HashSet<JToggleButton>();
        for (DataSeriesType dataSeriesType : prideChart.getSupportedTypes()) {
            String type = dataSeriesType.getType();
            JToggleButton jtb;
            if(isMultipleChoice){
                jtb = new JCheckBox(type);
            } else {
                jtb = new JRadioButton(type);
                group.add(jtb);
            }
            jtb.setSelected(prideChart.getVisibleTypes().contains(dataSeriesType));
            if(jtb.isSelected()) selectedButtons.add(jtb);
            jtb.setEnabled(!pco.isSpectraSeriesEmpty(dataSeriesType));

            ChartOptionsListener cbl = new ChartOptionsListener(chartBigPane, prideChart, selectedButtons, dataSeriesType);
            jtb.addActionListener(cbl);

            jtb.setOpaque(false);

            optionPanel.add(jtb);
        }
        optionPanel.setBackground(Color.WHITE);
        add(optionPanel, BorderLayout.CENTER);

        int border = 3;
        setBorder(BorderFactory.createEmptyBorder(border, 0, border, border));

        Dimension dimension = new Dimension(50,50);
        this.setPreferredSize(dimension);
    }
}