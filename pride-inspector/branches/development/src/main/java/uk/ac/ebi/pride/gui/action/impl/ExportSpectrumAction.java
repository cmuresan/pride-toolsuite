package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.dialog.OpenFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.ExportSpectrumMGFTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Export spectra to mgf format.
 * <p/>
 * User: dani
 * Date: 23-Aug-2010
 * Time: 11:38:26
 * To change this template use File | Settings | File Templates.
 */
public class ExportSpectrumAction extends PrideAction implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumAction.class);
    private PrideInspectorContext context;
    private static final String FILE_EXTENSION = ".mgf";

    public ExportSpectrumAction(String name, Icon icon) {
        super(name, icon);
        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListenerToDataAccessMonitor(this);
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileDialog ofd = new OpenFileDialog(context.getOpenFilePath(), "Select File To Export Spectrum Data To", ".mgf");
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showSaveDialog(Desktop.getInstance().getMainComponent());
        if (result == JFileChooser.APPROVE_OPTION) {
            DataAccessController controller = context.getForegroundDataAccessController();
            File selectedFile = ofd.getSelectedFile();
            // store file path for reuse
            String filePath = selectedFile.getPath();
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));

            ExportSpectrumMGFTask newTask = new ExportSpectrumMGFTask(controller, filePath + (filePath.endsWith(FILE_EXTENSION) ? "" : FILE_EXTENSION));
            // set task's gui blocker
            newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
            // add task listeners
            Desktop.getInstance().getDesktopContext().addTask(newTask);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        try {
            if (DataAccessMonitor.NEW_FOREGROUND_DATA_SOURCE_PROP.equals(evtName)) {
                DataAccessController controller = context.getForegroundDataAccessController();
                this.setEnabled(controller != null && controller.hasSpectrum());
            }
        } catch (DataAccessException e) {
            logger.error("Failed to check the data access controller", e);
        }
    }
}


