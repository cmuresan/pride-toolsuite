package uk.ac.ebi.pride.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Render button which will open an PRIDE public experiment
 * <p/>
 * User: rwang
 * Date: 03/06/11
 * Time: 09:28
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    private String text;

    public ButtonRenderer() {
        this(null);
    }

    public ButtonRenderer(String text) {
        this.text = text;
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (isSelected) {
            this.setForeground(Color.red);
            this.setBackground(Color.red);
        } else {
            this.setForeground(Color.red);
            this.setBackground(Color.red);
        }

        this.setText(text == null ? value.toString() : text);

        return this;
    }
}
