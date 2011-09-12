package uk.ac.ebi.pride.gui.component.table.listener;

import org.jdesktop.swingx.table.TableColumnExt;
import uk.ac.ebi.pride.data.coreIdent.Peptide;
import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.component.protein.PTMDialog;
import uk.ac.ebi.pride.gui.component.table.model.PeptideTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Mouse listener to show PTM dialog
 *
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 10:56:50
 */
public class PeptideCellMouseClickListener extends MouseAdapter {
    private JTable table;
    private String columnHeader;

    public PeptideCellMouseClickListener(JTable table, String columnHeader) {
        this.table = table;
        this.columnHeader = columnHeader;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        String header = table.getColumnName(col);
        if (header.equals(columnHeader)) {
            TableModel tableModel = table.getModel();
            TableColumnExt column = (TableColumnExt)table.getColumn(PeptideTableModel.TableHeader.PEPTIDE_PTM_COLUMN.getHeader());
            Object val = tableModel.getValueAt(table.convertRowIndexToModel(row), column.getModelIndex());
            if (val != null && val instanceof Peptide) {
                Peptide peptide = (Peptide) val;
                if (peptide.hasModification()) {
                    PrideInspector inspector = (PrideInspector) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance();
                    PTMDialog dialog = new PTMDialog(inspector.getMainComponent(), (Peptide) val);
                    dialog.setVisible(true);
                }
            }
        }
    }
}
