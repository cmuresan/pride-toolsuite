package uk.ac.ebi.pride.gui.component.chart;

import uk.ac.ebi.pride.chart.graphics.implementation.PrideChart;
import uk.ac.ebi.pride.chart.graphics.implementation.data.DataSeriesType;
import uk.ac.ebi.pride.chart.graphics.interfaces.PrideChartSpectraOptions;

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
class ChartOptionsListener implements ActionListener {
    private ChartBigPane cbp;
    private PrideChart prideChart;
    private DataSeriesType dataSeriesType;
    private Set<JToggleButton> selectedButtons;

    ChartOptionsListener(ChartBigPane cbp,
                         PrideChart prideChart,
                         Set<JToggleButton> selectedButtons,
                         DataSeriesType dataSeriesType) {
        this.cbp = cbp;
        this.prideChart = prideChart;
        this.dataSeriesType = dataSeriesType;
        this.selectedButtons = selectedButtons;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        PrideChartSpectraOptions pco = (PrideChartSpectraOptions) prideChart;

        JToggleButton jtb;
        if(pco.isSpectraMultipleChoice()){
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
            for (DataSeriesType dataSeriesType : prideChart.getSupportedTypes()) {
                pco.setSpectraTypeVisibility(dataSeriesType, false);
            }
        }
        pco.setSpectraTypeVisibility(dataSeriesType, jtb.isSelected());

        if(pco.isSpectraMultipleChoice())
            cbp.refreshChart();
        else if(jtb.isSelected())
            cbp.refreshChart();
    }
}