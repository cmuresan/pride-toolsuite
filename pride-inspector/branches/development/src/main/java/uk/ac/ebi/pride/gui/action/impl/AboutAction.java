package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.startup.AboutDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Show About panel.
 *
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 14:28:10
 */
public class AboutAction extends PrideAction {

    public AboutAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AboutDialog(PrideInspector.getInstance().getMainComponent());
    }
}
