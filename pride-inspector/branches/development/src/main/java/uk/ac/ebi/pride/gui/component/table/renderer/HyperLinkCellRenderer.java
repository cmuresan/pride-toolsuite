package uk.ac.ebi.pride.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Aug-2010
 * Time: 14:34:20
 */
public class HyperLinkCellRenderer extends  JLabel implements TableCellRenderer {

    public HyperLinkCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        if (value != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("<html><a href=''>");
            builder.append(value.toString());
            builder.append("</a>");
            builder.append("</html>");
            this.setText(builder.toString());
        }
        // set background
        if (isSelected) {
            this.setBackground(table.getSelectionBackground());
        } else {
            this.setBackground(table.getBackground());
        }
        // repaint the component
        this.revalidate();
        this.repaint();
        return this;
    }
}
