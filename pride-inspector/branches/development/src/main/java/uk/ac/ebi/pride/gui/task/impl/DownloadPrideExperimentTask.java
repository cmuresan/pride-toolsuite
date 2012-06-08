package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * Task for download private experiments form PRIDE server.
 * <p/>
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 15:41:24
 */
public class DownloadPrideExperimentTask extends AbstractConnectPrideTask {
    private static final Logger logger = LoggerFactory.getLogger(DownloadPrideExperimentTask.class);

    private Map<Comparable, Double> accessions;
    private File folder;
    private String user;
    private String password;
    private boolean toOpenFile;

    public DownloadPrideExperimentTask(Map<Comparable, Double> accessions, File path,
                                       String usr, String pwd, boolean toOpenFile) {
        this.accessions = accessions;
        this.folder = path;
        this.user = usr;
        this.password = pwd;
        this.toOpenFile = toOpenFile;

        String msg = "Downloading PRIDE experiment";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        if (folder != null) {
            try {

                for (Map.Entry<Comparable, Double> acc : accessions.entrySet()) {
                    Comparable accession = acc.getKey();
                    Double size = acc.getValue();

                    // create a http connection
                    DesktopContext context = Desktop.getInstance().getDesktopContext();
                    HttpURLConnection connection = connect(context.getProperty("pride.experiment.download.url"));

                    if (connection != null) {
                        // login for download
                        initExperimentDownload(connection, accession, user, password);

                        // get output file path
                        File output = getFilePath(accession);

                        // download experiment
                        downloadFile(connection, output, size);

                        // open
                        if (toOpenFile) {
                            openFile(output);
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
     * Get the output file path
     *
     * @param accession pride experiment accession
     * @return File    output file
     */
    private File getFilePath(Comparable accession) {
        String filePathSymbol = System.getProperty("file.separator");
        String currPath = folder.getPath();
        currPath = currPath.endsWith(filePathSymbol) ? currPath : currPath + filePathSymbol;
        return new File(currPath + accession + ".xml");
    }


    /**
     * Open downloaded file
     *
     * @param output downloaded file
     */
    @SuppressWarnings("unchecked")
    private void openFile(File output) {
        publish("Opening file: " + output.getAbsolutePath());
        OpenFileTask openFileTask = new OpenFileTask(output, PrideXmlControllerImpl.class, "Opening download file", output.getAbsolutePath());
        // set task's gui blocker
        openFileTask.setGUIBlocker(new DefaultGUIBlocker(openFileTask, GUIBlocker.Scope.NONE, null));
        // add task listeners
        Desktop.getInstance().getDesktopContext().addTask(openFileTask);
    }
}
