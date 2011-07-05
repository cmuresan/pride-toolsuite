package uk.ac.ebi.pride.gui.component.table.renderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer used for drawing number of ptms column
 *
 * User: rwang
 * Date: 05/07/2011
 * Time: 10:11
 */
public class OpenPTMRenderer implements TableCellRenderer {
    private ImageIcon icon;

    public OpenPTMRenderer(ImageIcon icon) {
        if (icon == null) {
            throw new IllegalArgumentException("Input icon cannot be NULL");
        }
        this.icon = icon;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int count = value == null ? 0 : Integer.parseInt(value.toString());
        return new PTMLabel(count);
    }

    private class PTMLabel extends JLabel {
        private int value;

        private PTMLabel(int val) {
            this.value = val;
            this.setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // create graphics 2d
            Graphics2D g2 = (Graphics2D) g.create();

            // rendering hints
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw image
            int xPos = 2;
            int yPos = 2;

            if (value > 0) {
                // draw icon
                g2.drawImage(icon.getImage(), xPos, yPos, null);

                xPos += icon.getIconWidth() + 5;
            }

            // draw value
            yPos = 15;
            g2.drawString(value+"", xPos, yPos);

            // remove
            g2.dispose();
        }
    }
}
