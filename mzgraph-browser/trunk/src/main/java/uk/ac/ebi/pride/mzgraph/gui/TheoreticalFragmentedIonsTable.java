package uk.ac.ebi.pride.mzgraph.gui;

import uk.ac.ebi.pride.iongen.model.PrecursorIon;
import uk.ac.ebi.pride.mol.AminoAcid;
import uk.ac.ebi.pride.mol.PTModification;
import uk.ac.ebi.pride.mol.ProductIonPair;
import uk.ac.ebi.pride.mzgraph.chart.renderer.AminoAcidRenderer;
import uk.ac.ebi.pride.mzgraph.chart.renderer.PeptideIonRenderer;
import uk.ac.ebi.pride.mzgraph.gui.data.TheoreticalFragmentedIonsTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Map;

/**
 * Creator: Qingwei-XU
 * Date: 10/10/12
 */

public class TheoreticalFragmentedIonsTable extends JTable {
    // define default fraction position of ion mass is 3.
    private int fraction;

    private String fontName = "sansserif";
    private int columnFontSize = 16;
    private int cellFontSize = 12;
    private TheoreticalFragmentedIonsTableModel tableModel;

    private Map<Integer, PTModification> ptm;

    public TheoreticalFragmentedIonsTable(PrecursorIon precursorIon) {
        this(precursorIon, ProductIonPair.B_Y);
    }

    public TableCellRenderer getCellRenderer(int row, int column) {
        if (tableModel.isMassColumn(column)) {
            return new PeptideIonRenderer(fraction, row, column);
        } else if (tableModel.isIDColumn(column)) {
            return new DefaultTableCellRenderer() {
                public void setHorizontalAlignment(int alignment) {
                    super.setHorizontalAlignment(SwingConstants.RIGHT);
                }

                public void setForeground(Color fg) {
                    super.setForeground(Color.blue);
                }

                public void setBackground(Color fg) {
                    super.setBackground(Color.lightGray);
                }

                public void setFont(Font font) {
                    super.setFont(new Font(fontName, Font.BOLD, cellFontSize));
                }
            };
        } else if (tableModel.isSeqColumn(column)) {
            return new AminoAcidRenderer(ptm.get(row));
        } else {
            return super.getCellRenderer(row, column);
        }
    }

    public TheoreticalFragmentedIonsTable(PrecursorIon precursorIon,
                                          ProductIonPair ionPair) {
        this(precursorIon, ionPair, 3);
    }

    public TheoreticalFragmentedIonsTable(PrecursorIon precursorIon,
                                          ProductIonPair ionPair,
                                          int fraction) {
        tableModel = new TheoreticalFragmentedIonsTableModel(precursorIon, ionPair);
        setModel(tableModel);
        this.fraction = fraction;
        this.ptm = precursorIon.getPeptide().getPTM();

        setPreferredScrollableViewportSize(new Dimension(1400, 300));
        getTableHeader().setFont(new Font(fontName, Font.BOLD, columnFontSize));
        setFont(new Font(fontName, Font.PLAIN, cellFontSize));

        setAutoCreateColumnsFromModel(false);
        setRowHeight(cellFontSize + 8);

        //set ID column width
        getColumnModel().getColumn(0).setMaxWidth(24);
        getColumnModel().getColumn(0).setMinWidth(24);
        getColumnModel().getColumn(getColumnCount() - 1).setMaxWidth(24);
        getColumnModel().getColumn(getColumnCount() - 1).setMinWidth(24);
        getColumnModel().getColumn(getColumnCount() / 2).setMaxWidth(48);
        getColumnModel().getColumn(getColumnCount() / 2).setMinWidth(48);

        getTableHeader().setReorderingAllowed(false);
    }

    public int getFraction() {
        return fraction;
    }

    public Component prepareRenderer(TableCellRenderer renderer,
                                     int rowIndex, int vColIndex) {
        Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);
        Object o = getValueAt(rowIndex, vColIndex);

        PTModification modification;
        if (o instanceof AminoAcid) {
            modification = ptm.get(rowIndex);
            if (modification != null) {
                JComponent jc = (JComponent)c;
                jc.setToolTipText((modification.getName() == null ? "" : modification.getName()) + "[" + modification.getMonoMassDeltas().get(0) + "]");
            }
        }

        return c;
    }
}
