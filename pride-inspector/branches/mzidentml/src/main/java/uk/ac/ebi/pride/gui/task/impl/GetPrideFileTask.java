package uk.ac.ebi.pride.gui.task.impl;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.data.controller.DataAccessController;
import uk.ac.ebi.pride.gui.action.impl.OpenFileAction;
import uk.ac.ebi.pride.gui.component.reviewer.SubmissionFileDetail;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.task.TaskUtil;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Download a list of submitted project files
 *
 * @author Rui Wang
 * @version $Id$
 */
public class GetPrideFileTask extends TaskAdapter<Void, String> {
    private static final Logger logger = LoggerFactory.getLogger(GetPrideFileTask.class);

    private static final int BUFFER_SIZE = 1024;

    private List<SubmissionFileDetail> submissionEntries;
    private File folder;
    private String user;
    private String password;
    private boolean toOpenFile;

    public GetPrideFileTask(List<SubmissionFileDetail> submissionEntries,
                            File path,
                            String usr,
                            String pwd,
                            boolean toOpenFile) {
        this.submissionEntries = submissionEntries;
        this.folder = path;
        this.user = usr;
        this.password = pwd;
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
                    String url = buildFileDownloadUrl(context.getProperty("prider.file.download.url"), submissionEntry.getId());

                    // get output file path
                    File output = new File(folder.getAbsolutePath() + File.separator + submissionEntry.getFileName());

                    // download submission file
                    output = downloadFile(url, output, user, password, submissionEntry.getFileSize());

                    // open file
                    if (toOpenFile && output != null) {
                        OpenFileAction openFileAction = new OpenFileAction(null, null, Arrays.asList(output));
                        openFileAction.actionPerformed(null);
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

    private String buildFileDownloadUrl(String url, Long fileId) {
        url = url.replace("{fileId}", fileId + "");
        return url;
    }

    private File downloadFile(String url, File output, String userName, String password, long fileSize) throws IOException {
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
            }

            output = new File(output.getAbsolutePath().replace(".gz", ""));

            boutStream = new BufferedOutputStream(new FileOutputStream(output), BUFFER_SIZE);

            copyStream(in, boutStream, fileSize);

            publish("Download has finished");

            return output;
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

        return output;
    }

    private void copyStream(InputStream inputStream, BufferedOutputStream outputStream, long fileSize) throws IOException {
        int readCount = 0;
        byte data[] = new byte[BUFFER_SIZE];
        int count;

        while ((count = inputStream.read(data, 0, BUFFER_SIZE)) != -1) {
            readCount += count;
            outputStream.write(data, 0, count);

            float ratio = ((float) readCount) / fileSize;
            int progress = Math.abs(Math.round(ratio * 100));
            if (progress >= 100) {
                progress = 99;
            }

            if (progress > getProgress()) {
                setProgress(progress);
            }
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
