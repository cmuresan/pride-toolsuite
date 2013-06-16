package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.gui.utils.ProteinAccession;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class ProteinAccessionHyperLinkCellRenderer extends JLabel implements TableCellRenderer {

    public ProteinAccessionHyperLinkCellRenderer() {
        this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        if (value != null && value instanceof ProteinAccession) {
            String accession = ((ProteinAccession) value).getAccession();
            StringBuilder builder = new StringBuilder();
            builder.append("<html><a href='").append(accession).append("'>");
            builder.append(accession);
            builder.append("</a>");
            builder.append("</html>");
            this.setText(builder.toString());
        } else {
            this.setText(null);
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
