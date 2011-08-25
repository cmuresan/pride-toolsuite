package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;

import javax.help.CSH;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Open Help manuel.
 *
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 11:41:13
 */
public class OpenHelpAction extends PrideAction {
    public OpenHelpAction(String name, Icon icon) {
        super(name, icon);
        setAccelerator(java.awt.event.KeyEvent.VK_F1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext)PrideInspector.getInstance().getDesktopContext();
        CSH.setHelpIDString(PrideInspector.getInstance().getMainComponent(), "help.index");
        ActionListener listener = new CSH.DisplayHelpFromSource(context.getMainHelpBroker());
        listener.actionPerformed(e);
    }
}
