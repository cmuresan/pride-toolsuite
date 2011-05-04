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
import uk.ac.ebi.pride.gui.task.impl.ExportSpectrumDescTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 17:46:34
 */
public class ExportSpectrumDescAction extends PrideAction implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ExportSpectrumDescAction.class);
    private static final String FILE_EXTENSION = ".txt";
    
    private final PrideInspectorContext context;

    public ExportSpectrumDescAction(String name, Icon icon) {
        super(name, icon);
        // register this action as property listener to database access monitor
        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListenerToDataAccessMonitor(this);
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OpenFileDialog ofd = new OpenFileDialog(context.getOpenFilePath(), "Export Spectrum Descriptions", FILE_EXTENSION);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showSaveDialog(Desktop.getInstance().getMainComponent());
        if (result == JFileChooser.APPROVE_OPTION) {
            DataAccessController controller = context.getForegroundDataAccessController();
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
            ExportSpectrumDescTask newTask = new ExportSpectrumDescTask(controller, filePath + (filePath.endsWith(FILE_EXTENSION) ? "" : FILE_EXTENSION));
            // set task's gui blocker
            newTask.setGUIBlocker(new DefaultGUIBlocker(newTask, GUIBlocker.Scope.NONE, null));
            // add task listeners
            Desktop.getInstance().getDesktopContext().addTask(newTask);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evtName = evt.getPropertyName();
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

