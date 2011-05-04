package uk.ac.ebi.pride.gui.action;

import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 09-Feb-2010
 * Time: 16:03:52
 */
public class ExitAction extends PrideAction {

    public ExitAction() {
        super("Exit");
        setMenuLocation("File");
        setAccelerator(java.awt.event.KeyEvent.VK_Q, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop.getInstance().shutdown(e);
    }
}
