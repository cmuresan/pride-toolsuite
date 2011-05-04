package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.impl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.DefaultGUIBlocker;
import uk.ac.ebi.pride.gui.utils.GUIBlocker;
import uk.ac.ebi.pride.gui.utils.HttpUtilities;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;

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
    private List<String> accessions;
    private File path;
    private String user;
    private char[] password;
    private boolean toOpenFile;

    public DownloadExperimentTask(List<String> accessions, File path, String usr, char[] pwd, boolean toOpenFile) {
        this.accessions = accessions;
        this.path = path;
        this.user = usr;
        this.password = pwd;
        this.toOpenFile = toOpenFile;
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        if (path != null) {
            for (String accession : accessions) {
                HttpURLConnection connection = null;
                try {
                    connection = createHttpConnection();
                } catch (Exception ex) {
                    logger.warn("Fail to create connection to PRIDE server: {}", ex.getMessage());
                    publish("Warning: Fail to create connection to PRIDE server");
                }

                OutputStreamWriter out = null;
                try {
                    out = new OutputStreamWriter(connection.getOutputStream());
                    String cmd = "username=" + user + "&password=" + String.valueOf(password) + "&accession=" + accession + "&action=downloadFile";
                    out.write(cmd);
                    out.close();
                } catch (IOException ex) {
                    logger.warn("Fail to send download request to PRIDE server: {}", ex.getMessage());
                    publish("Warning: Fail to send download request to PRIDE server");
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                BufferedOutputStream boutStream = null;
                try {
                    publish("Downloading: " + accession + "...");
                    GZIPInputStream in = new GZIPInputStream(connection.getInputStream());
                    ZipEntry entry;
                    // check whether it is a legal path
                    String filePathSymbol = System.getProperty("file.separator");
                    String currPath = path.getPath();
                    currPath = currPath.endsWith(filePathSymbol) ? currPath : currPath + filePathSymbol;
                    String filePath = currPath + accession + ".xml";
                    FileOutputStream outStream = new FileOutputStream(filePath);
                    boutStream = new BufferedOutputStream(outStream, BUFFER_SIZE);
                    byte data[] = new byte[BUFFER_SIZE];
                    int count;
                    while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
                        boutStream.write(data, 0, count);
                    }
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
                        Desktop.getInstance().getDesktopContext().getTaskManager().addTask(openFileTask);
                    }
                } catch (IOException ex) {
                    String msg = ex.getMessage();
                    if (msg.contains("403")) {
                        logger.warn("Wrong login credentials: {}", msg);
                        publish("Warning: Wrong login credentials");
                    } else if (msg.contains("400")) {
                        logger.warn("Fail to connect to remote PRIDE server: {}", msg);
                        publish("Warning: Fail to connect to remote PRIDE server");
                    }
                } finally {
                    if (boutStream != null) {
                        boutStream.close();
                    }
                }
            }
        }
        return null;
    }


    private HttpURLConnection createHttpConnection() throws Exception {
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        return HttpUtilities.createHttpConnection(propMgr.getProperty("reviewer.download.url"), "POST");
    }
}
