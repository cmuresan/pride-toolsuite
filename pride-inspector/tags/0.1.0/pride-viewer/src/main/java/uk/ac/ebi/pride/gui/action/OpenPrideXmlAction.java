package uk.ac.ebi.pride.gui.action;

import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.component.OpenFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:49:21
 */
public class OpenPrideXmlAction extends PrideAction {

    public OpenPrideXmlAction() {
        super("Open Pride XML File");
        setMenuLocation("File");
        setAccelerator(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileDialog ofd = new OpenFileDialog("/", "Select Pride xml Files", ".xml");
        int result = ofd.showOpenDialog(Desktop.getInstance().getMainComponent());
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = ofd.getSelectedFiles();
            for (File selectedFile : selectedFiles) {
                String fileName = selectedFile.getName();
                OpenFileTask newTask = new OpenFileTask(selectedFile, PrideXmlControllerImpl.class, fileName, "In the process of opening " + fileName);
                // set task's gui blocker
                newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                // ToDo: this why we need a singleton DesktopContext
                Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
            }
        }
    }
}
