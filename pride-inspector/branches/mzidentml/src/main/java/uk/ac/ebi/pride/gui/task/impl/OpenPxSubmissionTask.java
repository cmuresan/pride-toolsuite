package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.gui.GUIUtilities;
import uk.ac.ebi.pride.gui.PrideInspectorContext;
import uk.ac.ebi.pride.gui.component.reviewer.PxDownloadSelectionPane;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.task.TaskEvent;
import uk.ac.ebi.pride.gui.task.TaskListener;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Task for downloading a ProteomeXchange submission
 *
 * @author Rui Wang
 * @version $Id$
 */
public class OpenPxSubmissionTask extends TaskAdapter<Void, Void> implements TaskListener<List<Map<String, String>>, String> {

    /**
     * list of proteomexchange accession
     */
    private List<Comparable> pxAccessions;
    /**
     * PRIDE account user name
     */
    private String username;
    /**
     * PRIDE account password
     */
    private String password;
    /**
     * Reference to PRIDE context
     */
    PrideInspectorContext context;


    public OpenPxSubmissionTask(List<Comparable> pxAccessions, String username, String password) {
        this.pxAccessions = pxAccessions;
        this.username = username;
        this.password = password;
        this.context = ((PrideInspectorContext) Desktop.getInstance().getDesktopContext());
    }

    @Override
    protected Void doInBackground() throws Exception {
        // assign default user name and password if necessary
        if (username == null) {
            this.username = context.getProperty("default.pride.username");
            this.password = context.getProperty("default.pride.password");
        }

        // retrieve submission details
        GetPrideExperimentDetailTask task = new GetPrideExperimentDetailTask(username, password, pxAccessions);
        task.addTaskListener(this);
        task.setGUIBlocker(new DefaultGUIBlocker(task, GUIBlocker.Scope.NONE, null));
        context.addTask(task);

        return null;
    }

    @Override
    public void succeed(TaskEvent<List<Map<String, String>>> listTaskEvent) {
        List<Map<String, String>> metaData = listTaskEvent.getValue();

        // open a download window
        JDialog dialog = new JDialog();
        dialog.setAlwaysOnTop(true);
        ImageIcon icon = GUIUtilities.loadImageIcon(context.getProperty("pride.inspector.logo.small.icon"));
        dialog.setIconImage(icon.getImage());
        dialog.setTitle(context.getProperty("px.download.dialog.title"));
        dialog.setSize(new Dimension(650, 600));
        dialog.setLayout(new BorderLayout());
        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((d.width - dialog.getWidth()) / 2, (d.height - dialog.getHeight()) / 2);

        // add a experiment selection pane
        PxDownloadSelectionPane pxDownloadSelectionPane = new PxDownloadSelectionPane(dialog, true, username, password);
        pxDownloadSelectionPane.addExperimentMetaData(metaData);
        dialog.add(pxDownloadSelectionPane, BorderLayout.CENTER);
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<String>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
