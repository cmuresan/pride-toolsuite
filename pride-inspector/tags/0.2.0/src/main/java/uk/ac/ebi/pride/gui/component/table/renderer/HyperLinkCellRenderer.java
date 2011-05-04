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
    private String hyperLinkPrefix;
    private String hyperLinkEndfix;

    public HyperLinkCellRenderer(String hyperLinkPrefix, String hyperLinkEndfix) {
        this.hyperLinkPrefix = hyperLinkPrefix;
        this.hyperLinkEndfix = hyperLinkEndfix;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        JLabel label = new JLabel();
        if (value != null) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("<html><a href='");
            buffer.append(hyperLinkPrefix);
            buffer.append(value.toString());
            buffer.append(hyperLinkEndfix);
            buffer.append("'>");
            buffer.append(value.toString());
            buffer.append("</a>");
            buffer.append("</html>");
            label.setText(buffer.toString());
        }
        
        // set background
        if (isSelected) {
            label.setBackground(table.getSelectionBackground());
            label.setForeground(table.getSelectionForeground());
        } else {
            label.setBackground(table.getBackground());
        }

        // repaint the component
        label.revalidate();
        label.repaint();
        return label;
    }
}
