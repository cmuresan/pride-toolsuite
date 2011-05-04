package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Exit PRIDE Viewer.
 * 
 * User: rwang
 * Date: 09-Feb-2010
 * Time: 16:03:52
 */
public class ExitAction extends PrideAction {

    public ExitAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop.getInstance().shutdown(e);
    }
}
