package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;

import javax.help.CSH;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Open FAQ section of the help document.
 *
 * User: rwang
 * Date: 02-Nov-2010
 * Time: 23:34:04
 */
public class OpenFAQAction extends PrideAction{
    public OpenFAQAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        CSH.setHelpIDString(PrideInspector.getInstance().getMainComponent(), "help.faq");
        ActionListener listener = new CSH.DisplayHelpFromSource(context.getMainHelpBroker());
        listener.actionPerformed(e);
    }
}
