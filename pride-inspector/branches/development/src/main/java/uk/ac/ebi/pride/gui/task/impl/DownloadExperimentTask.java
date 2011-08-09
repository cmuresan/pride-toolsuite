package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.url.HttpUtilities;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Task for download private experiments form PRIDE server.
 * <p/>
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 15:41:24
 */
public class DownloadExperimentTask extends TaskAdapter<List<Map<String, String>>, String> {
    private static final Logger logger = LoggerFactory.getLogger(DownloadExperimentTask.class);
    private static final int BUFFER_SIZE = 2048;

    private Map<Comparable, Double> accessions;
    private File path;
    private String user;
    private char[] password;
    private boolean toOpenFile;

    public DownloadExperimentTask(Map<Comparable, Double> accessions, File path, String usr, char[] pwd, boolean toOpenFile) {
        this.accessions = accessions;
        this.path = path;
        this.user = usr;
        this.password = (pwd == null ? pwd : Arrays.copyOf(pwd, pwd.length));
        this.toOpenFile = toOpenFile;

        String msg = "Downloading PRIDE private experiment";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        if (path != null) {
            try {
                for (Map.Entry<Comparable, Double> acc : accessions.entrySet()) {
                    Comparable accession = acc.getKey();
                    Double size = acc.getValue();

                    // create a http connection
                    HttpURLConnection connection = createConnection();

                    if (connection != null) {
                        // login for download
                        loginForDownload(connection, accession);

                        // download experiment
                        downloadExperiment(connection, accession, size);
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
     * Create http connection to pride download
     *
     * @return HttpURLConnection    http connection
     */
    private HttpURLConnection createConnection() {
        HttpURLConnection connection = null;

        try {
            DesktopContext context = Desktop.getInstance().getDesktopContext();
            connection = HttpUtilities.createHttpConnection(context.getProperty("pride.experiment.download.url"), "POST");
        } catch (Exception ex) {
            logger.warn("Fail to create connection to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to create connection to PRIDE server");
        }

        return connection;
    }

    /**
     * login to pride to download
     *
     * @param connection http connection.
     * @param accession  pride experiment accession
     */
    private void loginForDownload(HttpURLConnection connection, Comparable accession) {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=downloadFile");
            cmd.append("&accession=");
            cmd.append(accession);
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send download request to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to send download request to PRIDE server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Fail to close output stream", e);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    private void downloadExperiment(HttpURLConnection connection, Comparable accession, Double size) {
        BufferedOutputStream boutStream = null;
        GZIPInputStream in = null;
        try {
            publish("Downloading " + accession);
            in = new GZIPInputStream(connection.getInputStream());

            // check whether it is a legal path
            String filePathSymbol = System.getProperty("file.separator");
            String currPath = path.getPath();
            currPath = currPath.endsWith(filePathSymbol) ? currPath : currPath + filePathSymbol;
            String filePath = currPath + accession + ".xml";
            FileOutputStream outStream = new FileOutputStream(filePath);
            boutStream = new BufferedOutputStream(outStream, BUFFER_SIZE);
            byte data[] = new byte[BUFFER_SIZE];
            int count;
            int readCount = 0;
            setProgress(0);
            while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                readCount += count;
                boutStream.write(data, 0, count);
                int progress = (int) Math.round((readCount / size)*100);
                setProgress(progress>= 100 ? 99 : progress);
            }
            String logMsg = "Downloaded " + readCount + " (" + size + ") bytes for experiment " + accession;
            if (readCount < 100) {
                logger.warn(logMsg);
            } else {
                logger.debug(logMsg);
            }
            setProgress(100);
            boutStream.flush();
            boutStream.close();
            publish("Download has finished");

            // open file if asked
            if (toOpenFile) {
                publish("Opening file: " + filePath);
                OpenFileTask openFileTask = new OpenFileTask(new File(filePath), PrideXmlControllerImpl.class, "Opening download file", filePath);
                // set task's gui blocker
                openFileTask.setGUIBlocker(new DefaultGUIBlocker(openFileTask, GUIBlocker.Scope.NONE, null));
                // add task listeners
                Desktop.getInstance().getDesktopContext().addTask(openFileTask);
            }
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning: Wrong login credentials");
            } else if (msg.contains("400")) {
                logger.warn("Fail to connect to remote PRIDE server: {}", msg);
                publish("Warning: Fail to connect to remote PRIDE server");
            } else {
                logger.warn("Unexpected error: " + ex.getMessage());
                publish("Unexpected error: " + ex.getMessage());
            }
        } finally {
            if (boutStream != null) {
                try {
                    boutStream.close();
                } catch (IOException e) {
                    logger.error("Failed to close output stream", e);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("Failed to close Gzip input stream", e);
                }
            }
        }
    }
}
