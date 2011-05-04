package uk.ac.ebi.pride.gui.action.impl;

import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.OpenFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * OpenFileAction opens files supported by PRIDE Viewer
 * so far: mzML, PRIDE XML
 * <p/>
 * User: rwang
 * Date: 18-Aug-2010
 * Time: 11:40:33
 */
public class OpenFileAction extends PrideAction {
    public OpenFileAction(String name, Icon icon, String menuLocation) {
        super(name, icon, menuLocation);
        setAccelerator(java.awt.event.KeyEvent.VK_O, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileDialog ofd = new OpenFileDialog(System.getProperty("user.dir"), "Select mzML/PRIDE xml Files", ".mzml", ".xml");
        int result = ofd.showOpenDialog(Desktop.getInstance().getMainComponent());
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = ofd.getSelectedFiles();
            for (File selectedFile : selectedFiles) {
                String fileName = selectedFile.getName();
                Class classType = null;
                try {
                    classType = getFileType(selectedFile);
                } catch (IOException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                if (classType != null) {
                    OpenFileTask newTask = new OpenFileTask(selectedFile, classType, fileName, "In the process of opening " + fileName);
                    // set task's gui blocker
                    newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                    // add task listeners
                    // ToDo: this why we need a singleton DesktopContext
                    Desktop.getInstance().getDesktopContext().getTaskManager().addTask(newTask);
                }
            }
        }
    }

    private Class getFileType(File file) throws IOException {
        Class classType = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            // read the first three lines
            String content = reader.readLine();
            content += reader.readLine();
            content += reader.readLine();
            content = content.toLowerCase();
            // check file type
            if (content.contains("mzml")) {
                classType = MzMLControllerImpl.class;
            } else if (content.contains("experimentcollection")) {
                classType = PrideXmlControllerImpl.class;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return classType;
    }
}
