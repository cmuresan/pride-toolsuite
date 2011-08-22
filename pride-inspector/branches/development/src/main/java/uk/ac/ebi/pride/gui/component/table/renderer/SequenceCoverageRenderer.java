package uk.ac.ebi.pride.gui.component.table.renderer;

import uk.ac.ebi.pride.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.gui.component.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * Rendering the protein sequence coverage percentage
 * <p/>
 * User: rwang
 * Date: 24/06/11
 * Time: 15:58
 */
public class SequenceCoverageRenderer extends JLabel implements TableCellRenderer {
    private Object value;

    public SequenceCoverageRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.value = value;

        return this;
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

        // formatter
        DecimalFormat format = new DecimalFormat("##.#%");
        // text to display
        String percentage = (value == null || Double.parseDouble(value.toString()) == -1) ? "" : format.format(Double.parseDouble(value.toString()));


        // paint text
        g2.setColor(Color.black);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
        FontMetrics fontMetrics = g2.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(percentage);
        int xPos = (width - textWidth) / 2;
        int yPos = height / 2 + fontMetrics.getDescent() + 2;
        g2.drawString(percentage, xPos, yPos);

        g2.dispose();
    }
}
