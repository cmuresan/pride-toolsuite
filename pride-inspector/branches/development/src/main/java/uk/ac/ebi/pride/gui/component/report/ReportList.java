package uk.ac.ebi.pride.gui.component.report;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * List to display summary report messages
 *
 * User: rwang
 * Date: 07/06/11
 * Time: 14:53
 */
public class ReportList extends JList {

    @Override
    public String getToolTipText(MouseEvent event) {
        // get index
        int index = locationToIndex(event.getPoint());

        // get item
        ReportMessage item = (ReportMessage)getModel().getElementAt(index);

        return item.getDescription();
    }
}
