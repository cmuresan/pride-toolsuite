package uk.ac.ebi.pride.gui.action.impl;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.gui.task.impl.DownloadSingleExperimentTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Export pride database experiment to pride xml
 * <p/>
 * User: rwang
 * Date: 14/09/2011
 * Time: 11:40
 */
public class ExportPrideXmlAction extends PrideAction {

    public ExportPrideXmlAction(String name, Icon icon) {
        super(name, icon);

        // enable annotation
        AnnotationProcessor.process(this);

        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        DataAccessController controller = appContext.getForegroundDataAccessController();
        // experiment accession
        Comparable accession = controller.getForegroundExperimentAcc();

        String defaultFilePath = controller.getName() + ".xml";
        SimpleFileDialog ofd = new SimpleFileDialog(appContext.getOpenFilePath(), "Export PRIDE XML", defaultFilePath, false, ".xml");
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ofd.getSelectedFile();
            appContext.setOpenFilePath(selectedFile.getAbsolutePath().replace(selectedFile.getName(), ""));
            String defaultUserName = appContext.getProperty("default.pride.username");
            String defaultPwd = appContext.getProperty("default.pride.password");

            DownloadSingleExperimentTask task = new DownloadSingleExperimentTask(accession, selectedFile, defaultUserName, defaultPwd);
            // set task's gui blocker
            task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
            // add task listeners
            appContext.addTask(task);
        }
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();
        this.setEnabled(controller != null && controller.getType().equals(DataAccessController.Type.DATABASE));
    }
}
