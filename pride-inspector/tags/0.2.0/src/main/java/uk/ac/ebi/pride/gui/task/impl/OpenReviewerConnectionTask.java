package uk.ac.ebi.pride.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.gui.desktop.Desktop;
import uk.ac.ebi.pride.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.gui.prop.PropertyManager;
import uk.ac.ebi.pride.gui.task.TaskAdapter;
import uk.ac.ebi.pride.gui.utils.HttpUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Connect to PRIDE server for reviewer download.
 *
 * User: rwang
 * Date: 04-Aug-2010
 * Time: 13:51:20
 */
public class OpenReviewerConnectionTask extends TaskAdapter<List<Map<String, String>>, String> {
    private static final Logger logger = LoggerFactory.getLogger(OpenReviewerConnectionTask.class);
    private String user;
    private char[] password;

    public OpenReviewerConnectionTask(String usr, char[] pwd) {
        this.user = usr;
        this.password = pwd;
    }

    @Override
    protected List<Map<String, String>> doInBackground() throws Exception {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        HttpURLConnection connection = null;
        try {
            connection = createHttpConnection();
            publish("Connection to PRIDE server established");
        } catch (Exception ex) {
            logger.warn("Fail to connect to PRIDE server: {}", ex.getMessage());
            publish("Warning: Fail to connect to PRIDE server");
        }

        if (connection != null) {
            OutputStreamWriter out = null;
            try {
                publish("Logging into PRIDE ....");
                out = new OutputStreamWriter(connection.getOutputStream());
                String cmd = "username=" + user + "&password=" + String.valueOf(password) + "&action=metadata";
                out.write(cmd);
                out.close();
                publish("Login successfully");
            } catch (IOException ex) {
                logger.warn("Fail to send request to PRIDE server: {}", ex.getMessage());
                publish("Warning: Fail to send request to PRIDE server");
            } finally {
                if (out != null) {
                    out.close();
                }
            }

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
                    publish("Warning: Wrong login credentials");
                } else if (msg.contains("400")) {
                    logger.warn("Fail to connect to remote PRIDE server: {}", msg);
                    publish("Warning: Fail to connect to remote PRIDE server");
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }

        return result;
    }

    private HttpURLConnection createHttpConnection() throws Exception {
        DesktopContext context = Desktop.getInstance().getDesktopContext();
        PropertyManager propMgr = context.getPropertyManager();
        return HttpUtilities.createHttpConnection(propMgr.getProperty("reviewer.download.url"), "POST");
    }
}
