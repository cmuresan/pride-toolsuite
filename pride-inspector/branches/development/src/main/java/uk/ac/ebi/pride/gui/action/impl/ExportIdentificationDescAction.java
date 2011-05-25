package uk.ac.ebi.pride.gui.action.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.DataAccessException;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.gui.action.PrideAction;
import uk.ac.ebi.pride.gui.component.SharedLabels;
import uk.ac.ebi.pride.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.impl.ExportIdentificationDescTask;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import static uk.ac.ebi.pride.gui.component.SharedLabels.DOI;
import static uk.ac.ebi.pride.gui.component.SharedLabels.DOT;
import static uk.ac.ebi.pride.gui.component.SharedLabels.TAB_SEP_FILE;

/**
 * Created by IntelliJ IDEA.
 * User: rwang
 * Date: 01-Sep-2010
 * Time: 17:18:40
 */
public class ExportIdentificationDescAction extends PrideAction implements PropertyChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ExportIdentificationDescAction.class);
    private static final String FILE_NAME = "protein_desc";

    private final PrideInspectorContext context;

    public ExportIdentificationDescAction(String name, Icon icon) {
        super(name, icon);
        // register this action as property listener to database access monitor
        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        context.addPropertyChangeListenerToDataAccessMonitor(this);
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataAccessController controller = context.getForegroundDataAccessController();
        String defaultFileName = controller.getName().split("\\" + DOT)[0] + "_" + FILE_NAME;
        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Export Identification Descriptions", defaultFileName, false, TAB_SEP_FILE);
        ofd.setMultiSelectionEnabled(false);
        int result = ofd.showDialog(Desktop.getInstance().getMainComponent(), null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = ofd.getSelectedFile();
            // store file path for reuse
            String filePath = selectedFile.getPath();
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
            ExportIdentificationDescTask newTask = new ExportIdentificationDescTask(controller, filePath + (filePath.endsWith(TAB_SEP_FILE) ? "" : TAB_SEP_FILE));
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
                this.setEnabled(controller != null && controller.hasIdentification());
            }
        } catch (DataAccessException e) {
            logger.error("Failed to check data access controller", e);
        }
    }
}
