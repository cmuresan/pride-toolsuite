package uk.ac.ebi.pride.gui.action;

import uk.ac.ebi.pride.gui.desktop.Desktop;

import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 16-Feb-2010
 * Time: 13:59:15
 */
public class DummyAction extends PrideAction {

    public DummyAction() {
        super("Dummy Action");
        this.setMenuLocation("file");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DummyTask task = new DummyTask("Harry");
        Desktop.getInstance().getDesktopContext().getTaskManager().addTask(task);
    }
}
