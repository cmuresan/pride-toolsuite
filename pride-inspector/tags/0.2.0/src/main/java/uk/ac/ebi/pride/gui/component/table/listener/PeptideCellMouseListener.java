package uk.ac.ebi.pride.gui.component.table.listener;

import uk.ac.ebi.pride.data.core.Peptide;
import uk.ac.ebi.pride.gui.PrideViewer;
import uk.ac.ebi.pride.gui.component.ident.PTMDialog;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 10:56:50
 */
public class PeptideCellMouseListener extends MouseAdapter {
    private JTable table;
    private int column;

    public PeptideCellMouseListener(JTable table, int column) {
        this.table = table;
        this.column = column;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        if (col == column) {
            TableModel tableModel = table.getModel();
            Object val = tableModel.getValueAt(row, col);
            if (val != null && val instanceof Peptide) {
                Peptide peptide = (Peptide) val;
                if (peptide.hasModification()) {
                    PrideViewer viewer = (PrideViewer) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance();
                    PTMDialog dialog = new PTMDialog(viewer.getMainComponent(), (Peptide) val);
                    dialog.setVisible(true);
                }
            }
        }
    }
}
