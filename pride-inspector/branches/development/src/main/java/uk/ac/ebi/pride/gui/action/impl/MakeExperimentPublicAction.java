package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.curation.MakeExperimentPublicDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for making a experiment public
 * 
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 13:26:32
 */
public class MakeExperimentPublicAction extends PrideAction {

    public MakeExperimentPublicAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MakeExperimentPublicDialog dialog = new MakeExperimentPublicDialog(Desktop.getInstance().getMainComponent());
        dialog.setVisible(true);
    }
}
