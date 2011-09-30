package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.QuartilesType;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartQuartiles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * <p></p>
 *
 * User: Antonio Fabregat
 * Date: 29-nov-2010
 * Time: 16:18:55
 */
class ChartQuartilesListener implements ActionListener {
    private ChartBigPane cbp;
    private PrideChart prideChart;
    private QuartilesType quartilesType;
    private Set<JToggleButton> selectedButtons;

    ChartQuartilesListener(ChartBigPane cbp,
                         PrideChart prideChart,
                         Set<JToggleButton> selectedButtons,
                         QuartilesType quartilesType) {
        this.cbp = cbp;
        this.prideChart = prideChart;
        this.quartilesType = quartilesType;
        this.selectedButtons = selectedButtons;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PrideChartQuartiles pco = (PrideChartQuartiles) prideChart;

        JToggleButton jtb;
        if(pco.isMultipleQuartile()){
            jtb = (JCheckBox) actionEvent.getSource();
            /************ Next keep always a checkbox selected *******/
            if(jtb.isSelected())
                selectedButtons.add(jtb);
            else
                selectedButtons.remove(jtb);
            //To be checked here because only make sense in MultipleChoice
            if(selectedButtons.isEmpty()){
                jtb.setSelected(true);
                selectedButtons.add(jtb);
                return;
            }
            /*********************************************************/
        } else {
            jtb = (JRadioButton) actionEvent.getSource();
            for (QuartilesType quartilesType : prideChart.getSupportedQuartiles()) {
                pco.setQuartileVisibility(quartilesType, false);
            }
        }
        pco.setQuartileVisibility(quartilesType, jtb.isSelected());

        if(pco.isMultipleQuartile())
            cbp.refreshChart();
        else if(jtb.isSelected())
            cbp.refreshChart();
    }
}