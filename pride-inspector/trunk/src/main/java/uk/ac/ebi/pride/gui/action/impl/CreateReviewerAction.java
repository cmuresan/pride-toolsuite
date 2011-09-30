package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.curation.CreateReviewerDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action for creating a reviewer
 *
 * User: rwang
 * Date: 25-Nov-2010
 * Time: 16:36:41
 */
public class CreateReviewerAction extends PrideAction{

    public CreateReviewerAction(String name, Icon icon) {
        super(name, icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CreateReviewerDialog dialog = new CreateReviewerDialog(Desktop.getInstance().getMainComponent());
        dialog.setVisible(true);
    }
}
