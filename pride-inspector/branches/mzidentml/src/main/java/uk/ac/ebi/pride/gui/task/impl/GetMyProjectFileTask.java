package uk.ac.ebi.pride.gui.task.impl;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.MzMLControllerImpl;
import uk.ac.ebi.pride.data.controller.impl.ControllerImpl.PrideXmlControllerImpl;
import uk.ac.ebi.pride.data.util.MassSpecFileFormat;
import uk.ac.ebi.pride.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.task.TaskUtil;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Download a list of submitted project files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetMyProjectFileTask extends TaskAdapter<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(GetMyProjectFileTask.class);

    private static final int BUFFER_SIZE = 1024;

    private List<SubmissionFileDetail> submissionEntries;
    private File folder;
    private String user;
    private String password;
    private String projectAccession;
    private boolean toOpenFile;

    public GetMyProjectFileTask(List<SubmissionFileDetail> submissionEntries,
                                File path,
                                String usr,
                                String pwd,
                                String projectAccession,
                                boolean toOpenFile) {
        this.submissionEntries = submissionEntries;
        this.folder = path;
        this.user = usr;
        this.password = pwd;
        this.projectAccession = projectAccession;
        this.toOpenFile = toOpenFile;

        String msg = "Downloading PRIDE submission";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected Void doInBackground() throws Exception {
        if (folder != null) {
            try {

                for (SubmissionFileDetail submissionEntry : submissionEntries) {
                    // create a http connection
                    DesktopContext context = Desktop.getInstance().getDesktopContext();

                    // initialize the download
                    String url = buildFileDownloadUrl(context.getProperty("prider.file.download.url"), projectAccession, submissionEntry.getId());

                    // get output file path
                    File output = new File(folder.getAbsolutePath() + File.separator + submissionEntry.getFileName());

                    // download submission file
                    downloadFile(url, output, user, password, submissionEntry.getFileSize());

                    // open file
                    if (toOpenFile) {
                        MassSpecFileFormat fileFormat = MassSpecFileFormat.checkFormat(output);
                        if (fileFormat != null) {
                            if (fileFormat.equals(MassSpecFileFormat.PRIDE)) {
                                publish("Opening file: " + output.getAbsolutePath());
                                openFile(output, PrideXmlControllerImpl.class);
                            } else if (fileFormat.equals(MassSpecFileFormat.MZML)) {
                                publish("Opening file: " + output.getAbsolutePath());
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

    private String buildFileDownloadUrl(String url, String projectAccession, Long fileId) {
        url = url.replace("{projectAccession}", projectAccession);
        url = url.replace("{fileId}", fileId+"");
        return url;
    }

    private void downloadFile(String url, File output, String userName, String password, long fileSize) throws IOException {
        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        client.getState().setCredentials(AuthScope.ANY, credentials);

        // Create a method instance.
        GetMethod method = new GetMethod(url);

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        BufferedOutputStream boutStream = null;
        InputStream in = null;
        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + method.getStatusLine());
            }

            // Read the response body.
            in = method.getResponseBodyAsStream();

            Header contentTypeHeader = method.getResponseHeader("Content-Type");
            if (contentTypeHeader != null && "application/x-gzip".equalsIgnoreCase(contentTypeHeader.getValue())) {
                in = new GZIPInputStream(in);
            }   output = new File(output.getAbsolutePath().replace(".gz", ""));

            boutStream = new BufferedOutputStream(new FileOutputStream(output), BUFFER_SIZE);

            copyStream(in, boutStream, fileSize);

            publish("Download has finished");
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish("Warning:Wrong login credentials");
            } else if (msg.contains("400")) {
                logger.warn("Fail to connect to the remote server: {}", msg);
                publish("Warning:Fail to connect to the remote server");
            } else {
                logger.warn("Unexpected error: " + ex.getMessage());
                publish("Unexpected error: " + ex.getMessage());
            }
        } finally {
            if (boutStream != null) {
                boutStream.close();
            }

            if (in != null) {
                in.close();
            }
        }
    }

    private void copyStream(InputStream inputStream, BufferedOutputStream outputStream, long fileSize) throws IOException {
        long readCount = 0;
        byte data[] = new byte[BUFFER_SIZE];
        int count;

        setProgress(1);
        while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
            readCount += count;
            outputStream.write(data, 0, count);
            int progress = Math.round((readCount / fileSize) * 100);
            setProgress(progress >= 100 ? 99 : progress);
        }
        outputStream.flush();
        setProgress(100);
    }


    /**
     * Open downloaded file
     *
     * @param output downloaded file
     */
    @SuppressWarnings("unchecked")
    private void openFile(File output, Class<? extends DataAccessController> controllerClass) {
        OpenFileTask openFileTask = new OpenFileTask(output, controllerClass, "Opening download file", output.getAbsolutePath());
        TaskUtil.startBackgroundTask(openFileTask);
    }
}
