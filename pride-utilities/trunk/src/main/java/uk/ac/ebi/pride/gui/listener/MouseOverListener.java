package uk.ac.ebi.pride.gui.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseOverListener change the mouse cursor
 *
 * User: rwang
 * Date: 04-Oct-2010
 * Time: 14:57:41
 */
public class MouseOverListener extends MouseAdapter {
    private JComponent component;

    public MouseOverListener(JComponent component) {
        this.component = component;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
}
