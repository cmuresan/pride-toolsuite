package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.px.PxSubmissionEntry;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Download a list of ProteomeXchange submission files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class DownloadPxSubmissionTask extends AbstractConnectPrideTask {
    private static final Logger logger = LoggerFactory.getLogger(DownloadPrideExperimentTask.class);

    private List<PxSubmissionEntry> submissionEntries;
    private File folder;
    private String user;
    private String password;
    private boolean toOpenFile;

    public DownloadPxSubmissionTask(List<PxSubmissionEntry> submissionEntries,
                                    File path,
                                    String usr,
                                    String pwd,
                                    boolean toOpenFile) {
        this.submissionEntries = submissionEntries;
        this.folder = path;
        this.user = usr;
        this.password = pwd;
        this.toOpenFile = toOpenFile;

        String msg = "Downloading ProteomeXchange submission";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        if (folder != null) {
            try {

                for (PxSubmissionEntry submissionEntry : submissionEntries) {
                    // create a http connection
                    DesktopContext context = Desktop.getInstance().getDesktopContext();

                    // initialize the download
                    String url = buildPxFileDownloadURL(context.getProperty("px.submission.file.download.url"), submissionEntry.getAccession(), submissionEntry.getFileID(), user, password);

                    // get output file path
                    File output = new File(folder.getAbsolutePath() + File.separator + submissionEntry.getFileName());

                    // download submission file
                    downloadFile(url, output, submissionEntry.getSize());

                    // open file
                    if (toOpenFile) {
                        MassSpecFileFormat fileFormat = MassSpecFileFormat.checkFormat(output);
                        if (fileFormat != null) {
                            if (fileFormat.equals(MassSpecFileFormat.PRIDE)) {
                                openFile(output, PrideXmlControllerImpl.class);
                            } else if (fileFormat.equals(MassSpecFileFormat.MZML)) {
                                openFile(output, MzMLControllerImpl.class);
                            }
                        }
                    }

                    // this is important for cancelling
                    if (Thread.interrupted()) {
                        throw new InterruptedException();
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Experiment downloading has been interrupted");
            }
        }
        return null;
    }


    /**
     * Open downloaded file
     *
     * @param output downloaded file
     */
    @SuppressWarnings("unchecked")
    private void openFile(File output, Class<? extends DataAccessController> controllerClass) {
        publish("Opening file: " + output.getAbsolutePath());
        OpenFileTask openFileTask = new OpenFileTask(output, controllerClass, "Opening download file", output.getAbsolutePath());
        // set task's gui blocker
        openFileTask.setGUIBlocker(new DefaultGUIBlocker(openFileTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        Desktop.getInstance().getDesktopContext().addTask(openFileTask);
    }
}
