package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.gui.component.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Cell renderer for the peptide present column
 * <p/>
 * User: rwang
 * Date: 22/06/11
 * Time: 10:33
 */
public class PeptidePresentCellRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new HighlightLabel(value.toString(), table, isSelected);
    }

    private class HighlightLabel extends JPanel {
        private String text;
        private JTable table;
        private boolean isSelected;

        private HighlightLabel(String text, JTable table, boolean isSelected) {
            this.text = text;
            this.table = table;
            this.isSelected = isSelected;
            this.setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            int width = getWidth();
            int height = getHeight();

            // rendering hints
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // paint a background
            Color background = null;

            if (isSelected) {
                background = Constants.PEPTIDE_HIGHLIGHT_COLOUR;
            } else {
                if (Constants.NOT_FIT.equals(text)) {
                    background = new Color(215, 39, 41, 100);
                } else if (Constants.FIT.equals(text)) {
                    background = Constants.FIT_PEPTIDE_BACKGROUND_COLOUR;
                } else if (Constants.STRICT_FIT.equals(text)) {
                    background = Constants.STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR;
                }
            }

            if (background != null) {
                g2.setColor(background);
                g2.fillRect(0, 0, width, height);
            }

            // paint text
            g2.setColor(Color.black);
            g2.setFont(g2.getFont().deriveFont(Font.BOLD));
            FontMetrics fontMetrics = g2.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(text);
            int xPos = (width - textWidth) / 2;
            int yPos = height / 2 + fontMetrics.getDescent() + 2;
            g2.drawString(text, xPos, yPos);

            g2.dispose();
        }
    }
}
