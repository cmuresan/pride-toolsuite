package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.url.HttpUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Connect to PRIDE server for reviewer download.
 * <p/>
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 13:51:20
 */
public class OpenReviewerConnectionTask extends TaskAdapter<List<Map<String, String>>, String> {
    private static final Logger logger = LoggerFactory.getLogger(OpenReviewerConnectionTask.class);
    public static final String WRONG_LOGGING_CREDENTIAL = "Warning: Wrong login credentials";
    private static final String COMMA_SEPARATOR = ",";

    private String user;
    private char[] password;
    private Set<Comparable> accessions;

    public OpenReviewerConnectionTask(String user, char[] password) {
        this(user, password, null);
    }

    public OpenReviewerConnectionTask(String user, char[] password, Collection<Comparable> accessions) {
        this.user = user;
        this.password = Arrays.copyOf(password, password.length);
        if (accessions != null) {
            this.accessions = new HashSet<Comparable>();
            this.accessions.addAll(accessions);
        }

        String msg = "Private PRIDE Experiment Download";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {

        // create a http connection
        HttpURLConnection connection = createConnection();

        List<Map<String, String>> metadata = null;

        try {
            if (connection != null) {
                // login for meta data
                loginForMetaData(connection);

                // download experiment meta data
                metadata = downloadExperimentMetaData(connection);

                // this is important for cancelling
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException ex) {
            logger.warn("Private download session has been cancelled");
        }

        return metadata;
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
     * log in for meta data information.
     *
     * @param connection http connection
     */
    private void loginForMetaData(HttpURLConnection connection) {
        OutputStreamWriter out = null;
        try {
            publish("Logging into PRIDE ....");
            out = new OutputStreamWriter(connection.getOutputStream());
            StringBuilder cmd = new StringBuilder();
            cmd.append("username=");
            cmd.append(URLEncoder.encode(String.valueOf(user), "UTF-8"));
            cmd.append("&password=");
            cmd.append(URLEncoder.encode(String.valueOf(password), "UTF-8"));
            cmd.append("&action=metadata");
            if (accessions != null && !accessions.isEmpty()) {
                cmd.append("&accession=");
                String accStr = "";
                for (Comparable accession : accessions) {
                    accStr += accession.toString() + COMMA_SEPARATOR;
                }
                accStr.substring(0, accStr.length());
                cmd.append(accStr);
            }
            out.write(cmd.toString());
            out.close();
        } catch (IOException ex) {
            logger.warn("Fail to send request to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to send request to PRIDE server");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }
    }

    private List<Map<String, String>> downloadExperimentMetaData(HttpURLConnection connection) {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        BufferedReader in = null;
        try {
            publish("Loading Experiment details ...");
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String str;
            Map<String, String> entry = new HashMap<String, String>();
            while ((str = in.readLine()) != null) {
                str = str.trim();
                if ("//".equals(str) && !entry.isEmpty()) {
                    result.add(entry);
                    entry = new HashMap<String, String>();
                } else if (!"".equals(str)) {
                    String[] parts = str.split("\t");
                    entry.put(parts[0], parts[1]);
                }
            }
            in.close();
            publish("Login has finished");
        } catch (IOException ex) {
            String msg = ex.getMessage();
            if (msg.contains("403")) {
                logger.warn("Wrong login credentials: {}", msg);
                publish(WRONG_LOGGING_CREDENTIAL);
            } else {
                logger.warn("Fail to connect to remote PRIDE server: {}", msg);
                publish("Warning: Fail to connect to remote PRIDE server");
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.warn("Failed to close output stream", e);
                }
            }
        }

        return result;
    }
}
