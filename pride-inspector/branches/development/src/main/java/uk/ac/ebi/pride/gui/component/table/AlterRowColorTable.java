package uk.ac.ebi.pride.gui.component.table;

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 22-Aug-2010
 * Time: 10:02:32
 */
public class AlterRowColorTable extends JXTable {
    private Color alterRowColor = new Color(214, 241, 249);
    /**
     * Selection background
     */
    private static final Color SELECTION_BACKGROUND = new Color(193, 210, 238);
    /**
     * Selection foreground
     */
    private static final Color SELECTION_FOREGROUND = Color.black;


    private int omitColumn = -1;

    public AlterRowColorTable() {
    }

    public AlterRowColorTable(TableModel dm) {
        super(dm);
    }

    public AlterRowColorTable(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
    }

    public AlterRowColorTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    public AlterRowColorTable(int numRows, int numColumns) {
        super(numRows, numColumns);
    }

    public AlterRowColorTable(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
    }

    public AlterRowColorTable(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isCellSelected(row, column)) {
            c.setBackground(colorForRow(row));
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
            if (omitColumn < 0 || column != omitColumn) {
                c.setBackground(SELECTION_BACKGROUND);
                c.setForeground(SELECTION_FOREGROUND);
            }
        }
        return c;
    }

    private Color colorForRow(int row) {
        return (row % 2 == 0) ? alterRowColor : getBackground();
    }

    public Color getAlterRowColor() {
        return alterRowColor;
    }

    public void setAlterRowColor(Color alterRowColor) {
        this.alterRowColor = alterRowColor;
    }

    public int getOmitColumn() {
        return omitColumn;
    }

    public void setOmitColumn(int omitColumn) {
        this.omitColumn = omitColumn;
    }

    @Override
    public Color getSelectionBackground() {
        return SELECTION_BACKGROUND;
    }
}
