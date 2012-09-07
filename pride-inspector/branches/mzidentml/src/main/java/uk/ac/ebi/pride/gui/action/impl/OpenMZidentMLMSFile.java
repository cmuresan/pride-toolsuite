package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.component.dialog.SimpleMsDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.OpenFileTask;
import uk.ac.ebi.pride.gui.utils.Constants;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yperez
 * Date: 8/15/12
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenMZidentMLMSFile extends PrideAction {

    private static final Logger logger = LoggerFactory.getLogger(OpenMZidentMLMSFile.class);

    private PrideInspectorContext context;

    @Override
    public void actionPerformed(ActionEvent e) {
        context = (PrideInspectorContext) uk.ac.ebi.pride.gui.desktop.Desktop.getInstance().getDesktopContext();
        // create a open file dialog
        createFileOpenMSDialog();


    }

    /**
     * create a file open dialog if not input files has been specified
     *
     * @return List<File>  a list of input files
     */
    private List<File> createFileOpenMSDialog() {

        SimpleMsDialog ofd = new SimpleMsDialog((Dialog) null);

       // int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);

        List<File> filesToOpen = new ArrayList<File>();

        /*if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }*/

        return filesToOpen;
    }

    /**
         * <code> openFiles </code> opens a list of files.
         *
         * @param files files to open
         */
    @SuppressWarnings("unchecked")
    private void openFiles(List<File> files) {
        for (File selectedFile : files) {
            // check the file type
            Class classType = null;
            classType = null; //getFileType(selectedFile);
            if (classType != null) {
                String msg = "Opening " + selectedFile.getName();
                OpenFileTask newTask = new OpenFileTask(selectedFile, classType, msg, msg);
                // set task's gui blocker
                newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                // ToDo: this why we need a singleton DesktopContext
                Desktop.getInstance().getDesktopContext().addTask(newTask);
            }
        }
    }
}
