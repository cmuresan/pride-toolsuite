package uk.ac.ebi.pride.gui.component.table.editor;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 28/06/2011
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class ButtonEditor extends DefaultCellEditor {
    private JButton button;
    private String text;

    public ButtonEditor(JCheckBox checkBox, String text) {
        super(checkBox);
        this.button = new JButton();
        this.text = text;
        this.button.setOpaque(true);
        // add action listener
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }

        if (text ==  null) {
            button.setText((value == null) ? "" : value.toString());
        } else {
            button.setText(text);
        }

        return button;
    }
}
