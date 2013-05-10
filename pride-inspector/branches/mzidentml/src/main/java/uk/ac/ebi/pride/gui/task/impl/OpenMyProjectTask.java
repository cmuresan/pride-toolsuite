package uk.ac.ebi.pride.gui.task.impl;

import uk.ac.ebi.pride.gui.PrideInspector;
import uk.ac.ebi.pride.gui.component.reviewer.MyProjectDownloadDialog;
import uk.ac.ebi.pride.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.*;
import uk.ac.ebi.pride.prider.webservice.file.model.FileDetail;
import uk.ac.ebi.pride.prider.webservice.file.model.FileDetailList;

import java.util.ArrayList;
import java.util.List;

/**
 * Task for opening my project
 *
 * @author Rui Wang
 * @version $Id$
 */
public class OpenMyProjectTask extends TaskAdapter<Void, Void> implements TaskListener<FileDetailList, String> {

    /**
     * list of proteomexchange accession
     */
    private String projectAccession;
    /**
     * PRIDE account user name
     */
    private String username;
    /**
     * PRIDE account password
     */
    private char[] password;


    public OpenMyProjectTask(String accession, String username, char[] password) {
        this.projectAccession = accession;
        this.username = username;
        this.password = password;

    }

    @Override
    protected Void doInBackground() throws Exception {
        // assign default user name and password if necessary
        if (username == null) {
            DesktopContext context = Desktop.getInstance().getDesktopContext();
            this.username = context.getProperty("default.pride.username");
            this.password = context.getProperty("default.pride.password").toCharArray();
        }

        // retrieve submission details
        Task task = new GetMyProjectFilesMetadataTask(username, password, projectAccession);
        task.addTaskListener(this);
        TaskUtil.startBackgroundTask(task);

        return null;
    }

    @Override
    public void succeed(TaskEvent<FileDetailList> listTaskEvent) {
        FileDetailList fileDetailList = listTaskEvent.getValue();

        if (fileDetailList != null) {
            List<SubmissionFileDetail> submissionFileDetails = new ArrayList<SubmissionFileDetail>();
            for (FileDetail fileDetail : fileDetailList.getFileDetails()) {
                submissionFileDetails.add(new SubmissionFileDetail(fileDetail));
            }

            // open project summary dialog
            MyProjectDownloadDialog myProjectDownloadDialog = new MyProjectDownloadDialog(PrideInspector.getInstance().getMainComponent(), username, password, projectAccession, submissionFileDetails);
            myProjectDownloadDialog.setVisible(true);
        }
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
