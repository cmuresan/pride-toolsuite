package uk.ac.ebi.pride.mzgraph.gui.data;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import uk.ac.ebi.pride.iongen.model.ProductIon;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mol.ion.FragmentIonType;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class ExperimentalFragmentedIonsDataset extends XYSeriesCollection implements ExperimentalTableModelObserver {
    private void generateSeriesList(ProductIonPair ionPair) {
        removeAllSeries();

        switch (ionPair) {
            case A_X:
                addSeries(new XYSeries(FragmentIonType.X_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.A_ION.getName()));
                break;
            case B_Y:
                addSeries(new XYSeries(FragmentIonType.Y_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.B_ION.getName()));
                break;
            case C_Z:
                addSeries(new XYSeries(FragmentIonType.Z_ION.getName()));
                addSeries(new XYSeries(FragmentIonType.C_ION.getName()));
                break;
        }
    }

    public ExperimentalFragmentedIonsDataset(ExperimentalFragmentedIonsTableModel tableModel) {
        // the first table model observer, which update called first too.
        tableModel.addObserver(1, this);
        update(tableModel);
    }

    @Override
    public void update(ExperimentalFragmentedIonsTableModel tableModel) {
        Object[][] matchedData = tableModel.getMatchedData();

        ProductIonPair ionPair = tableModel.getIonPair();
        generateSeriesList(ionPair);

        Object o;
        ProductIon theoreticalIon;
        Double matchedMass;

        XYSeries series;
        int seriesIndex;
        double x;
        double y;
        for (int col = 1; col < tableModel.getColumnCount(); col++) {
            if (! tableModel.isMassColumn(col)) {
                continue;
            }

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                o = tableModel.getValueAt(row, col);
                theoreticalIon = (ProductIon) o;
                if (matchedData[row][col] == null) {
                    continue;
                }

                matchedMass = (Double) matchedData[row][col];
                seriesIndex = indexOf(theoreticalIon.getType().getGroup().getName());
                series = getSeries(seriesIndex);
                x = theoreticalIon.getMassOverCharge();
                y = matchedMass - x;
                series.add(x, y);
            }
        }
    }
}