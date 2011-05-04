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
public class HyperLinkCellRenderer implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        if (value != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("<html><a href=''>");
            builder.append(value.toString());
            builder.append("</a>");
            builder.append("</html>");
            label.setText(builder.toString());
        }
        // set background
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
        } else {
            label.setBackground(table.getBackground());
        }
        // repaint the component
        label.revalidate();
        label.repaint();
        return label;
    }
}
