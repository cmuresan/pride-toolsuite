package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.reviewer.PrideDownloadPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open reviewer download panel.
 *
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 09:44:36
 */
public class OpenReviewAction extends PrideAction {

    public OpenReviewAction(String name, Icon icon) {
        super(name, icon);
        this.setAccelerator(java.awt.event.KeyEvent.VK_R, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideDownloadPane downloadPane = new PrideDownloadPane(Desktop.getInstance().getMainComponent());
        downloadPane.setVisible(true);
    }
}
