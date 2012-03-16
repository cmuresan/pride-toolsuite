package uk.ac.ebi.pride.gui.component.chart;

import org.jfree.ui.tabbedui.VerticalLayout;
import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.QuartilesType;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartQuartiles;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideChartManager;

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
public class ChartQuartiles extends JPanel {
    public ChartQuartiles(ChartBigPane chartBigPane, PrideChartManager managedPrideChart) {
        super(new BorderLayout());

        //Options title
        JLabel title = new JLabel("Chart Quartiles");
        Font titleFont = new Font(title.getFont().getFontName(), Font.BOLD, 15);
        title.setFont(titleFont);
        JPanel titleBar = new JPanel();
        titleBar.add(title);
        titleBar.setBackground(Color.WHITE);
        add(titleBar, BorderLayout.NORTH);

        //Quartiles
        PrideChart prideChart = managedPrideChart.getPrideChart();
        JPanel optionPanel = new JPanel(new VerticalLayout());
        PrideChartQuartiles pco = (PrideChartQuartiles) prideChart;
        boolean isMultipleChoice = pco.isMultipleQuartile();
        ButtonGroup group = new ButtonGroup();
        Set<JToggleButton> selectedButtons = new HashSet<JToggleButton>();
        for (QuartilesType quartilesType : prideChart.getSupportedQuartiles()) {
            String type = quartilesType.getType();
            JToggleButton jtb;
            if(isMultipleChoice){
                jtb = new JCheckBox(type);
            } else {
                jtb = new JRadioButton(type);
                group.add(jtb);
            }
            jtb.setSelected(prideChart.getVisibleQuartile() == quartilesType);
            if(jtb.isSelected()) selectedButtons.add(jtb);
            jtb.setEnabled(!pco.isQuartileEmpty(quartilesType));

            ChartQuartilesListener cbl = new ChartQuartilesListener(chartBigPane, prideChart, selectedButtons, quartilesType);
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