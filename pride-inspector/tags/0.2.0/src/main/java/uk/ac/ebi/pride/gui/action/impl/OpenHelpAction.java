package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.action.PrideAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open Help manuel.
 *
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 11:41:13
 */
public class OpenHelpAction extends PrideAction {
    public OpenHelpAction(String name, Icon icon, String menuLocation) {
        super(name, icon, menuLocation);
        setAccelerator(java.awt.event.KeyEvent.VK_H, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
