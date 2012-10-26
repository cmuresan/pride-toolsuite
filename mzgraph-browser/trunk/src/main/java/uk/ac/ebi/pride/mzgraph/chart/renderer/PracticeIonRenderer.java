package uk.ac.ebi.pride.mzgraph.chart.renderer;

import javax.swing.*;
import java.awt.*;

/**
 * Creator: Qingwei-XU
 * Date: 11/10/12
 */

public class PracticeIonRenderer extends PeptideIonRenderer {
    private Object[][] practiceData;

    public PracticeIonRenderer(int fraction, Object[][] practiceData, int row, int col) {
        super(fraction, row, col);
        this.practiceData = practiceData;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        //Cells are by default rendered as a JLabel.
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

        if (practiceData[row][col] != null) {
            Font oldFont = getFont();
            label.setFont(new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize()));

            // the predMatrix display red, and the postMatrix display blue.
            if (col < table.getModel().getColumnCount() / 2) {
                label.setForeground(Color.red);
            } else {
                label.setForeground(Color.blue);
            }
        }

        return label;
    }
}
