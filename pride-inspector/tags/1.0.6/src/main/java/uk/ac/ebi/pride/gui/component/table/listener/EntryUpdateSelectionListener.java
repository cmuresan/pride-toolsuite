package uk.ac.ebi.pride.gui.component.table.listener;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

/**
 * This listener set the default selection on a table.
 * <p/>
 * User: rwang
 * Date: 28-Jul-2010
 * Time: 14:55:57
 */
public class EntryUpdateSelectionListener implements TableModelListener {
    private final JTable table;

    public EntryUpdateSelectionListener(JTable table) {
        this.table = table;
    }

    public void tableChanged(TableModelEvent e) {
        int firstRow = e.getFirstRow();
        if (firstRow == 0) {
            if (SwingUtilities.isEventDispatchThread()) {
                setDefaultRowSelection();
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        setDefaultRowSelection();
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }

    private void setDefaultRowSelection() {
        table.getSelectionModel().setSelectionInterval(0, 0);
    }
}
